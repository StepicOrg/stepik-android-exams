package org.stepik.android.exams.adaptive.core.presenter

import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.functions.BiFunction
import io.reactivex.subjects.PublishSubject
import org.stepik.android.exams.adaptive.core.contracts.RecommendationsView
import org.stepik.android.exams.adaptive.listeners.AdaptiveReactionListener
import org.stepik.android.exams.adaptive.listeners.AnswerListener
import org.stepik.android.exams.adaptive.model.Card
import org.stepik.android.exams.adaptive.ui.adapter.QuizCardsAdapter
import org.stepik.android.exams.api.StepicRestService
import org.stepik.android.exams.core.ScreenManager
import org.stepik.android.exams.core.presenter.PresenterBase
import org.stepik.android.exams.data.db.dao.TopicDao
import org.stepik.android.exams.data.model.RecommendationReactionsRequest
import org.stepik.android.exams.data.model.RecommendationsResponse
import org.stepik.android.exams.data.model.ViewAssignment
import org.stepik.android.exams.data.preference.SharedPreferenceHelper
import org.stepik.android.exams.di.qualifiers.BackgroundScheduler
import org.stepik.android.exams.di.qualifiers.MainScheduler
import org.stepik.android.exams.graph.Graph
import org.stepik.android.model.adaptive.Reaction
import org.stepik.android.model.adaptive.RecommendationReaction
import retrofit2.HttpException
import java.util.*
import javax.inject.Inject

class RecommendationsPresenter
@Inject
constructor(
        private val stepicRestService: StepicRestService,
        @BackgroundScheduler
        private val backgroundScheduler: Scheduler,
        @MainScheduler
        private val mainScheduler: Scheduler,
        private val sharedPreferenceHelper: SharedPreferenceHelper,
        private val screenManager: ScreenManager,
        private val topicDao: TopicDao
) : PresenterBase<RecommendationsView>(), AdaptiveReactionListener, AnswerListener {

    companion object {
        private const val CARDS_IN_CACHE = 6
        private const val MIN_CARDS_IN_CACHE = 4
    }

    private val compositeDisposable = CompositeDisposable()
    private val retrySubject = PublishSubject.create<Any>()
    private val cards = ArrayDeque<Card>()
    private val adapter = QuizCardsAdapter(this, this)
    private var cardDisposable: Disposable? = null
    private var error: Throwable? = null
    private var isCourseCompleted = false
    private var course: Long = 0
    private var viewState: RecommendationsView.State = RecommendationsView.State.InitPresenter
        set(value) {
            field = value
            view?.setState(value)
        }

    fun initPresenter(topicId: String) {
        loadCourseId(topicId)
                .subscribeOn(backgroundScheduler)
                .observeOn(mainScheduler)
                .subscribe({
                    course = it
                    createReaction(0, Reaction.INTERESTING)
                }, {
                    view?.onCourseNotSupported()
                })
    }

    private fun loadCourseId(topicId: String) =
            topicDao.getAdaptiveCourseId(topicId)


    override fun attachView(view: RecommendationsView) {
        super.attachView(view)
        when {
            isCourseCompleted -> viewState = RecommendationsView.State.CourseCompleted
        }
        view.onAdapter(adapter)
        view.setState(viewState)
    }

    override fun onCorrectAnswer(submissionId: Long) {
    }

    override fun onWrongAnswer(submissionId: Long) {
    }

    override fun createReaction(lessonId: Long, reaction: Reaction) {
        if (adapter.isEmptyOrContainsOnlySwipedCard(lessonId)) {
            viewState = RecommendationsView.State.Loading
        }

        compositeDisposable.add(createReactionObservable(lessonId, reaction, cards.size + adapter.getItemCount())
                .subscribeOn(backgroundScheduler)
                .observeOn(mainScheduler)
                .doOnError(this::onError)
                .retryWhen { it.zipWith(retrySubject, BiFunction<Any, Any, Any> { a, _ -> a }) }
                .subscribe(this::onRecommendation, this::onError))
    }

    private fun onRecommendation(response: RecommendationsResponse) {
        val recommendations = response.recommendations
        if (recommendations == null || recommendations.isEmpty()) {
            isCourseCompleted = true
            viewState = RecommendationsView.State.CourseCompleted
        } else {
            val size = cards.size
            recommendations
                    .filter { !isCardExists(it.lesson) }
                    .forEach { cards.add(Card(course, it.lesson)) }

            if (size == 0) resubscribe()
        }
    }

    private fun onError(throwable: Throwable?) {
        this.error = throwable
        when (throwable) {
            is HttpException -> viewState = RecommendationsView.State.RequestError
            else -> viewState = RecommendationsView.State.NetworkError
        }
    }

    fun retry() {
        this.error = null
        retrySubject.onNext(0)
        viewState = RecommendationsView.State.Loading

        if (cards.isNotEmpty()) {
            cards.peek().initCard()
            resubscribe()
        }
    }

    private fun resubscribe() {
        if (cards.isNotEmpty()) {
            if (cardDisposable != null && cardDisposable?.isDisposed == false) {
                cardDisposable?.dispose()
            }

            cardDisposable = cards.peek()
                    .subscribe({ onCardDataLoaded(it) }, { onError(it) })
        }
    }

    private fun onCardDataLoaded(card: Card) {
        reportView(card)
        adapter.add(card)
        viewState = RecommendationsView.State.Success
        cards.poll()
        resubscribe()
    }

    private fun isCardExists(lessonId: Long) =
            cards.any { it.lessonId == lessonId } || adapter.isCardExists(lessonId)

    override fun detachView(view: RecommendationsView) {
        adapter.detach()
        cardDisposable?.dispose()
        super.detachView(view)
    }

    override fun destroy() {
        compositeDisposable.dispose()
        cards.forEach(Card::recycle)
        adapter.destroy()
    }

    private fun createReactionObservable(lesson: Long, reaction: Reaction, cacheSize: Int): Observable<RecommendationsResponse> {
        val responseObservable = stepicRestService.getNextRecommendations(course, CARDS_IN_CACHE).toObservable()

        if (lesson != 0L) {
            val reactionCompletable = stepicRestService
                    .createRecommendationReaction(RecommendationReactionsRequest(RecommendationReaction(lesson, reaction, sharedPreferenceHelper.profile?.id
                            ?: 0)))
            return if (cacheSize <= MIN_CARDS_IN_CACHE) {
                reactionCompletable.andThen(responseObservable)
            } else {
                reactionCompletable.toObservable()
            }
        }
        return responseObservable
    }

    private fun reportView(card: Card) {
        compositeDisposable.add(stepicRestService.getUnits(course, card.lessonId)
                .subscribeOn(backgroundScheduler)
                .observeOn(mainScheduler)
                .subscribe({ response ->
                    val unit = response.units?.firstOrNull()
                    val stepId = card.step?.id ?: 0
                    unit?.assignments?.firstOrNull()?.let { assignmentId ->
                        screenManager.pushToViewedQueue((ViewAssignment(assignmentId, stepId)))
                    }
                }, {}))
    }
}