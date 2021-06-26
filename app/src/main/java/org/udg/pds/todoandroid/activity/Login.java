package org.udg.pds.todoandroid.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import org.udg.pds.todoandroid.R;
import org.udg.pds.todoandroid.SellApp;
import org.udg.pds.todoandroid.entity.User;
import org.udg.pds.todoandroid.entity.UserLogin;
import org.udg.pds.todoandroid.rest.TodoApi;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created with IntelliJ IDEA.
 * User: imartin
 * Date: 28/03/13
 * Time: 16:01
 * To change this template use File | Settings | File Templates.
 */

// This is the Login fragment where the user enters the username and password and
// then a RESTResponder_RF is called to check the authentication
public class Login extends AppCompatActivity {

    TodoApi mTodoService;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        mTodoService = ((SellApp)this.getApplication()).getAPI();

        //----- Remove this and the next lines -------//
        //Intent i = new Intent(Login.this, Register.class);
        //startActivityForResult(i, 1);
        // ----- //

        Button b = findViewById(R.id.login_button);
        // This is teh listener that will be used when the user presses the "Login" button
        b.setOnClickListener(v -> {
            EditText u = Login.this.findViewById(R.id.login_username);
            EditText p = Login.this.findViewById(R.id.login_password);
            Login.this.checkCredentials(u.getText().toString(), p.getText().toString());
        });

        Button br = findViewById(R.id.register_button);
        br.setOnClickListener(v -> {
            Intent i = new Intent(Login.this, Register.class);
            startActivityForResult(i, 1);
        });

    }
    // This method is called when the "Login" button is pressed in the Login fragment
    public void checkCredentials(String username, String password) {
        UserLogin ul = new UserLogin();
        ul.username = username;
        ul.password = password;
        Call<User> call = mTodoService.login(ul);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {

                if (response.isSuccessful()) {
                    getToken();
                    Login.this.startActivity(new Intent(Login.this, NavigationActivity.class));
                    Login.this.finish();
                } else {
                    Toast toast = Toast.makeText(Login.this, "Error logging in", Toast.LENGTH_SHORT);
                    toast.show();
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Toast toast = Toast.makeText(Login.this, "Error logging in", Toast.LENGTH_SHORT);
                toast.show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if(resultCode == Activity.RESULT_OK){
                Login.this.finish();
            }
        }
    }

    private void getToken() {
        try {
            FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(Login.this, new OnSuccessListener<InstanceIdResult>() {
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
        Call<String> call = mTodoService.refreshToken(token);
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
