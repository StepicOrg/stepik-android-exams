package org.stepik.android.exams.api.auth;
import org.stepik.android.exams.api.UserRegistrationRequest;

import io.reactivex.Completable;
import io.reactivex.Single;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface OAuthService {

    @FormUrlEncoded
    @POST("oauth2/token/")
    Single<OAuthResponse> authWithLoginPassword(
            @Field("grant_type") final String grant_type,
            @Field(value = "username", encoded = true) final String username,
            @Field(value = "password", encoded = true) final String password
    );

    @FormUrlEncoded
    @POST("oauth2/token/")
    Single<OAuthResponse> getTokenByCode(
            @Field("grant_type") final String grant_type,
            @Field("code") final String code,
            @Field("redirect_uri") final String redirect_uri
    );

    @FormUrlEncoded
    @POST("oauth2/token/")
    Call<OAuthResponse> refreshAccessToken(
            @Field("grant_type") final String grant_type,
            @Field("refresh_token") final String refresh_token
    );

    @FormUrlEncoded
    @POST("/oauth2/social-token/")
    Single<OAuthResponse> getTokenByNativeCode(
            @Field("provider") String providerName,
            @Field("code") String providerCode,
            @Field("grant_type") String grant_type,
            @Field("redirect_uri") String redirect_uri,
            @Field("code_type") String accessToken
    );

    @POST("/api/users")
    Completable createAccount(
            @Body UserRegistrationRequest user
    );

}
