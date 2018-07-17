package org.stepik.android.exams.api.auth;

import io.reactivex.Completable;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface EmptyAuthService {

    @GET("/")
    Call<Void> getStepicForFun(
            @Header("Accept-Language") String lang
    );

    @FormUrlEncoded
    @POST("accounts/password/reset/")
    Completable remindPassword(
            @Field(value = "email", encoded = true) final String email
    );

}
