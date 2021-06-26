package org.udg.pds.todoandroid.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.udg.pds.todoandroid.R;
import org.udg.pds.todoandroid.SellApp;
import org.udg.pds.todoandroid.entity.User;
import org.udg.pds.todoandroid.entity.UserLogin;
import org.udg.pds.todoandroid.entity.UserRegister;
import org.udg.pds.todoandroid.rest.TodoApi;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Register extends AppCompatActivity {

    TodoApi mTodoService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mTodoService = ((SellApp)this.getApplication()).getAPI();

        EditText username = findViewById(R.id.usernameInput);
        EditText name = findViewById(R.id.nameInput);
        EditText email = findViewById(R.id.emailInput);
        EditText password = findViewById(R.id.passwInput);
        EditText confPassword = findViewById(R.id.confPasswInput);
        EditText tel = findViewById(R.id.telInput);
        Spinner location = findViewById(R.id.locationInput);
        location.setAdapter(getPossibleLocations());
        CheckBox check = findViewById(R.id.checkPolicy);

        ArrayList<EditText> fields = new ArrayList<>();
        fields.add(username); fields.add(name);
        fields.add(password); fields.add(confPassword);
        fields.add(tel);

        Button btnReturn = findViewById(R.id.returnButton);
        btnReturn.setOnClickListener(v -> {
            Register.this.finish(); //Finish this task and return to login menu
        });

        Button btnSignUp = findViewById(R.id.signUpButton);
        btnSignUp.setOnClickListener(v -> {
            Date birthday = getDateFromEditText(R.id.birthdayInput);

            if(!allCampsFilled(fields)){
                Toast.makeText(Register.this, "Incomplete Form", Toast.LENGTH_LONG).show();
            } else if (location.getSelectedItemPosition() == 0){
                Toast.makeText(Register.this, "Select a location", Toast.LENGTH_LONG).show();
            } else if (!pswdMatch(password, confPassword)){
                Toast.makeText(Register.this, "Passwords do not match", Toast.LENGTH_LONG).show();
            } else if (birthday == null){
                Toast.makeText(Register.this, "Wrong Date format : [dd/mm/yyyy] ", Toast.LENGTH_LONG).show();
            } else if (!check.isChecked()){
                Toast.makeText(Register.this, "Terms of Services must be accepted", Toast.LENGTH_LONG).show();
            } else {
                UserRegister newUser = new UserRegister();
                newUser.username = getStr(username);
                newUser.name = getStr(name);
                newUser.email = getStr(email);
                newUser.password = getStr(password);
                newUser.location = location.getSelectedItem().toString();
                newUser.tel = Long.valueOf(getStr(tel));
                newUser.birthday = birthday;

                Register.this.checkCredentials(newUser);
            }
        });
    }

    public void checkCredentials(UserRegister ur){
        Call<String> call = mTodoService.register(ur);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if(response.isSuccessful()) {
                    Toast toast = Toast.makeText(Register.this, "Hi " + ur.name + "!", Toast.LENGTH_SHORT);
                    toast.show();
                    Register.this.login(ur.username, ur.password);
                } else {
                    Toast toast = Toast.makeText(Register.this, "Username or Email already exist", Toast.LENGTH_SHORT);
                    toast.show();
                }
            }

            @Override
            public void onFailure(Call<String> str, Throwable t) {
                Toast toast = Toast.makeText(Register.this, "Error in Sign Up", Toast.LENGTH_SHORT);
                toast.show();
            }
        });
    }

    private boolean allCampsFilled(ArrayList<EditText> fields){
        boolean res = true;
        Iterator<EditText> it = fields.iterator();
        while (res && it.hasNext()){
            if(it.next().getText().toString().matches("")) res = false;
        }

        return res;
    }

    private Date getDateFromEditText(int id){
        EditText dateTxt = findViewById(id);
        @SuppressLint("SimpleDateFormat") DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        Date date;
        try {
            String dob_var = (dateTxt.getText().toString());
            date = formatter.parse(dob_var);

        } catch (Exception e) { date = null; }
        return date;
    }

    private boolean pswdMatch(EditText a, EditText b){
        return getStr(a).matches(getStr(b));
    }

    private String getStr(EditText txt){
        return txt.getText().toString();
    }

    public void login(String username, String password) {
        UserLogin ul = new UserLogin();
        ul.username = username;
        ul.password = password;
        Call<User> call = mTodoService.login(ul);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful()) {
                    Intent i = new Intent();
                    setResult(Activity.RESULT_OK, i);
                    Register.this.startActivity(new Intent(Register.this, NavigationActivity.class));
                    Register.this.finish();
                } else {
                    Toast toast = Toast.makeText(Register.this, "Error logging in", Toast.LENGTH_SHORT);
                    toast.show();
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Toast toast = Toast.makeText(Register.this, "Error logging in", Toast.LENGTH_SHORT);
                toast.show();
            }
        });
    }

    public ArrayAdapter<String> getPossibleLocations() {
        String [] str = new String [] {
            "Select location... ", "Girona", "Barcelona", "Lleida", "Tarragona"
        };
        return new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item, str){
            @Override
            public boolean isEnabled(int position){
                return position != 0;
            }
            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;
                if(position == 0){ tv.setTextColor(Color.GRAY); }
                else { tv.setTextColor(Color.BLACK); }
                return view;
            }
        };
    }
}
