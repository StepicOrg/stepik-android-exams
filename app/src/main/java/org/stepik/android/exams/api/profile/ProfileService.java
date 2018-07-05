package org.stepik.android.exams.api.profile;


import org.stepik.android.adaptive.api.profile.model.ChangePasswordRequest;
import org.stepik.android.exams.api.profile.model.EmailAddressRequest;
import org.stepik.android.exams.api.profile.model.EmailAddressesResponse;
import org.stepik.android.exams.api.profile.model.ProfileRequest;
import org.stepik.android.exams.api.profile.model.ProfileResponse;

import io.reactivex.Completable;
import io.reactivex.Single;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ProfileService {

    @GET("api/stepics/1")
    Single<ProfileResponse> getProfile();

    @PUT("api/profiles/{userId}")
    Completable setProfile(
            @Path("userId") final long userId,
            @Body final ProfileRequest profileRequest
    );

    @POST("api/profiles/{userId}/change-password")
    Completable changePassword(
            @Path("userId") final long userId,
            @Body final ChangePasswordRequest request
    );

    @GET("api/email-addresses")
    Single<EmailAddressesResponse> getEmailAddresses(
            @Query("ids[]") long[] ids
    );

    @GET("api/email-addresses/{emailId}")
    Single<EmailAddressesResponse> getEmailAddress(
            @Path("emailId") final long emailId
    );

    @POST("api/email-addresses")
    Single<EmailAddressesResponse> createEmailAddress(
            @Body final EmailAddressRequest request
    );

    @POST("api/email-addresses/{emailId}/set-as-primary")
    Completable setEmailAsPrimary(
            @Path("emailId") final long emailId
    );

    @DELETE("api/email-addresses/{emailId}")
    Completable removeEmailAddress(
            @Path("emailId") final long emailId
    );
}
