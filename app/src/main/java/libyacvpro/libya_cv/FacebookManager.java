package libyacvpro.libya_cv;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import libyacvpro.libya_cv.network.ApiService;

public class FacebookManager {

    private static final String TAG = "FacebookManager";
    private static final String PROVIDER = "facebook";


    public interface FacebookLoginListener{
        void onSuccess();
        void onError(String message);
    }

    private ApiService apiService;
    private TokenManager tokenManager;
    private CallbackManager callbackManager;
    private FacebookLoginListener listener;
    private Call<libyacvpro.libya_cv.entities.AccessToken> call;

    private FacebookCallback<LoginResult> facebookCallback = new FacebookCallback<LoginResult>() {
        @Override
        public void onSuccess(LoginResult loginResult) {
            fetchUser(loginResult.getAccessToken());
        }

        @Override
        public void onCancel() {

        }

        @Override
        public void onError(FacebookException error) {
            listener.onError(error.getMessage());
        }
    };

    public FacebookManager(ApiService apiService, TokenManager tokenManager){

        this.apiService = apiService;
        this.tokenManager = tokenManager;
        this.callbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().registerCallback(callbackManager, facebookCallback);

    }

    public void login(Activity activity, FacebookLoginListener listener){
        this.listener = listener;

        if(AccessToken.getCurrentAccessToken() != null){
            //Get the user
            fetchUser(AccessToken.getCurrentAccessToken());
        }else{
            LoginManager.getInstance().logInWithReadPermissions(activity, Arrays.asList("public_profile", "email"));
        }

    }

    private void fetchUser(AccessToken accessToken){
        GraphRequest request = GraphRequest.newMeRequest(accessToken, new GraphRequest.GraphJSONObjectCallback() {
            @Override
            public void onCompleted(JSONObject object, GraphResponse response) {

                try {
                    String id = object.getString("id");
                    String name = object.getString("name");
                    String email = (object.has("email")) ? object.getString("email") : id ;
                    getTokenFromBackend(name, email, PROVIDER, id);
                } catch (JSONException e) {
                    e.printStackTrace();
                    listener.onError(e.getMessage());
                }

            }
        });

        Bundle parameters = new Bundle();
        parameters.putString("fields", "id, name, email");
        request.setParameters(parameters);
        request.executeAsync();
    }

    private void getTokenFromBackend(String name, String email, String provider, String providerUserId){

        call = apiService.socialAuth(name, email, provider, providerUserId);
        call.enqueue(new Callback<libyacvpro.libya_cv.entities.AccessToken>() {
            @Override
            public void onResponse(Call<libyacvpro.libya_cv.entities.AccessToken> call, Response<libyacvpro.libya_cv.entities.AccessToken> response) {
                if(response.isSuccessful()){
                    tokenManager.saveToken(response.body());
                    listener.onSuccess();
                }else{
                    listener.onError("An error occured");
                }
            }

            @Override
            public void onFailure(Call<libyacvpro.libya_cv.entities.AccessToken> call, Throwable t) {
                listener.onError(t.getMessage());
            }
        });

    }

    public void onActivityResult(int requestCode, int resultCode, Intent data){
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    public void onDestroy(){
        if(call != null){
            call.cancel();
        }
        call = null;
        if(callbackManager != null){
            //LoginManager.getInstance().unregisterCallback(callbackManager);
        }
    }

    public void clearSession(){
        LoginManager.getInstance().logOut();
    }
}
