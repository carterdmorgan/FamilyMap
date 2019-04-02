package carterdmorgan.com.familymap.data;

import android.content.Context;
import android.widget.Toast;

import carterdmorgan.com.familymap.api.network.FamilyMapService;
import carterdmorgan.com.familymap.api.result.CurrentEventResult;
import carterdmorgan.com.familymap.api.result.CurrentPersonResult;
import carterdmorgan.com.familymap.api.result.UserResult;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserDataStore {
    private static final UserDataStore instance = new UserDataStore();
    private UserResult userResult;
    private CurrentEventResult currentEventResult;
    private CurrentPersonResult currentPersonResult;
    private String serverHost;
    private String serverPort;
    private int mapType = 0;

    public static UserDataStore getInstance() {
        return instance;
    }

    private UserDataStore() { }

    public void retrieveFamilyData( final FamilyMapService fmService, LoadFamilyDataListener listener) {
        getAllPersons(fmService, listener);
    }

    private void getAllPersons(final FamilyMapService fmService, final LoadFamilyDataListener listener) {
        Call<CurrentPersonResult> personCall = fmService.getAllPersonsForCurrentUser(userResult.getAuthToken());
        personCall.enqueue(new Callback<CurrentPersonResult>() {
            @Override
            public void onResponse(Call<CurrentPersonResult> call, Response<CurrentPersonResult> response) {
                if (response.code() == 200) {
                    CurrentPersonResult currentPersonResult = response.body();
                    UserDataStore.getInstance().setCurrentPersonResult(currentPersonResult);
                    getAllEvents(fmService, listener);
                } else {
                    listener.onFailure();
                }
            }

            @Override
            public void onFailure(Call<CurrentPersonResult> call, Throwable t) {
                t.printStackTrace();
                listener.onFailure();
            }
        });
    }

    private void getAllEvents(FamilyMapService fmService, final LoadFamilyDataListener listener) {
        Call<CurrentEventResult> eventCall = fmService.getAllEventsForPerson(userResult.getAuthToken());
        eventCall.enqueue(new Callback<CurrentEventResult>() {
            @Override
            public void onResponse(Call<CurrentEventResult> call, Response<CurrentEventResult> response) {
                CurrentEventResult currentEventResult = response.body();
                UserDataStore.getInstance().setCurrentEventResult(currentEventResult);
                listener.onSuccess();
            }

            @Override
            public void onFailure(Call<CurrentEventResult> call, Throwable t) {
                t.printStackTrace();
                listener.onFailure();
            }
        });
    }

    public void clearAllData() {
        instance.setUserResult(null);
        instance.setCurrentEventResult(null);
        instance.setCurrentPersonResult(null);
    }

    public UserResult getUserResult() {
        return userResult;
    }

    public int getMapType() {
        return mapType;
    }

    public void setMapType(int mapType) {
        this.mapType = mapType;
    }

    public void setUserResult(UserResult userResult) {
        this.userResult = userResult;
    }

    public CurrentEventResult getCurrentEventResult() {
        return currentEventResult;
    }

    public void setCurrentEventResult(CurrentEventResult currentEventResult) {
        this.currentEventResult = currentEventResult;
    }

    public CurrentPersonResult getCurrentPersonResult() {
        return currentPersonResult;
    }

    public void setCurrentPersonResult(CurrentPersonResult currentPersonResult) {
        this.currentPersonResult = currentPersonResult;
    }

    public void setServerHost(String serverHost) {
        this.serverHost = serverHost;
    }

    public void setServerPort(String serverPort) {
        this.serverPort = serverPort;
    }

    public String getServerHost() {
        return serverHost;
    }

    public String getServerPort() {
        return serverPort;
    }

    public interface LoadFamilyDataListener {
        void onSuccess();
        void onFailure();
    }
}
