package org.stepik.android.exams.api.auth


object SocialManager {
    private const val GOOGLE_SOCIAL_IDENTIFIER = "google"
    private const val FACEBOOK_SOCIAL_IDENTIFIER = "facebook"
    private const val MAILRU_SOCIAL_IDENTIFIER = "itmailru"
    private const val TWITTER_SOCIAL_IDENTIFIER = "twitter"
    private const val GITHUB_SOCIAL_IDENTIFIER = "github"
    private const val VK_SOCIAL_IDENTIFIER = "vk"

    enum class SocialType(val identifier: String) {
        vk(VK_SOCIAL_IDENTIFIER),
        google(GOOGLE_SOCIAL_IDENTIFIER),
        facebook(FACEBOOK_SOCIAL_IDENTIFIER),
        twitter(TWITTER_SOCIAL_IDENTIFIER),
        mailru(MAILRU_SOCIAL_IDENTIFIER),
        github(GITHUB_SOCIAL_IDENTIFIER);

        fun needUseAccessTokenInsteadOfCode() =
                identifier == VK_SOCIAL_IDENTIFIER || identifier == FACEBOOK_SOCIAL_IDENTIFIER
    }
}
