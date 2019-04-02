package carterdmorgan.com.familymap.api.network;

import carterdmorgan.com.familymap.api.model.Event;
import carterdmorgan.com.familymap.api.model.Person;
import carterdmorgan.com.familymap.api.request.LoadRequest;
import carterdmorgan.com.familymap.api.request.LoginUserRequest;
import carterdmorgan.com.familymap.api.request.RegisterUserRequest;
import carterdmorgan.com.familymap.api.result.CurrentEventResult;
import carterdmorgan.com.familymap.api.result.CurrentPersonResult;
import carterdmorgan.com.familymap.api.result.SimpleResult;
import carterdmorgan.com.familymap.api.result.UserResult;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;


public interface FamilyMapService {

    @POST("clear")
    Call<SimpleResult> clear();

    @POST("fill/{username}/{generations}")
    Call<SimpleResult> fill(@Path("username") String username, @Path("generations") int generations);

    @POST("fill/{username}")
    Call<SimpleResult> fill(@Path("username") String username);

    @POST("load")
    Call<SimpleResult> load(@Body LoadRequest request);

    @GET("event/{eventID}")
    Call<Event> getSpecificEvent(@Header("Authorization") String authToken, @Path("eventID") String eventId);

    @GET("event")
    Call<CurrentEventResult> getAllEventsForPerson(@Header("Authorization") String authToken);

    @GET("person/{personID}")
    Call<Person> getSpecficPerson(@Header("Authorization") String authToken, @Path("personID") String personId);

    @GET("person")
    Call<CurrentPersonResult> getAllPersonsForCurrentUser(@Header("Authorization") String authToken);

    @POST("user/register")
    Call<UserResult> registerUser(@Body RegisterUserRequest request);

    @POST("user/login")
    Call<UserResult> loginUser(@Body LoginUserRequest request);
}
