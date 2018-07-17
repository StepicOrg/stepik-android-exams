package org.stepik.android.exams.di.network


import dagger.Module
import dagger.Provides
import org.stepik.android.exams.di.AppSingleton
import org.stepik.android.exams.graph.Graph


@Module
abstract class GraphModule {
    @Module
    companion object {
        @Provides
        @AppSingleton
        @JvmStatic
        internal fun provideGraphInstance(): Graph<String> {
            return Graph()
        }
    }
}