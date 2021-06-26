package org.udg.pds.todoandroid.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import org.udg.pds.todoandroid.R;
import org.udg.pds.todoandroid.SellApp;
import org.udg.pds.todoandroid.rest.TodoApi;

import java.util.concurrent.Executor;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static androidx.constraintlayout.widget.Constraints.TAG;


public class SplashScreen extends AppCompatActivity {

    TodoApi todoApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        todoApi = ((SellApp) this.getApplication()).getAPI();

        setContentView(R.layout.activity_splash_screen);
    }

    @Override
    protected void onResume() {
        super.onResume();

        todoApi = ((SellApp) this.getApplication()).getAPI();

        Call<String> call = todoApi.check();
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful()) {
                    getToken();
                    SplashScreen.this.startActivity(new Intent(SplashScreen.this, NavigationActivity.class));
                    SplashScreen.this.finish();
                } else {
                    SplashScreen.this.startActivity(new Intent(SplashScreen.this, Login.class));
                    SplashScreen.this.finish();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Toast toast = Toast.makeText(SplashScreen.this, "Error checking login status", Toast.LENGTH_SHORT);
                toast.show();
            }
        });
    }

    private void getToken() {
        try {
            FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(SplashScreen.this, new OnSuccessListener<InstanceIdResult>() {
                @Override
                public void onSuccess(InstanceIdResult instanceIdResult) {
                    String token = instanceIdResult.getToken();
                    Log.i("FCM Token", token);
                    saveToken(token);
                }
            });
        } catch (Exception e) {
            Log.i("FCM Token", "Error " + e.getMessage());
        }
    }

    private void saveToken(String token) {
        Call<String> call = todoApi.refreshToken(token);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if(response.isSuccessful()){
                    Log.d("FCM Token", "The user token has refreshed");
                } else {

                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {

            }
        });
    }
}
