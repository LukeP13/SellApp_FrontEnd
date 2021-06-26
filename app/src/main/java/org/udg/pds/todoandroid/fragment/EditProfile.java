package org.udg.pds.todoandroid.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.navigation.ActionOnlyNavDirections;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.udg.pds.todoandroid.R;
import org.udg.pds.todoandroid.SellApp;
import org.udg.pds.todoandroid.activity.Login;
import org.udg.pds.todoandroid.activity.Register;
import org.udg.pds.todoandroid.entity.User;
import org.udg.pds.todoandroid.rest.TodoApi;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class EditProfile extends Fragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM = "userInfo";

    private User user;
    TodoApi mTodoService;

    public EditProfile() {
        // Required empty public constructor
    }

    @Override
    public void onStart() {
        super.onStart();
        mTodoService = ((SellApp) this.getActivity().getApplication()).getAPI();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_edit_profile, container, false);

        Spinner loc = root.findViewById(R.id.locationEdit);
        loc.setAdapter(getPossibleLocations());

        Button cancelBttn = root.findViewById(R.id.cancelBttn);
        cancelBttn.setOnClickListener(v -> {
            NavDirections action = new ActionOnlyNavDirections(R.id.action_editProfile_to_profileFragment);;
            NavController navController = Navigation.findNavController(v);
            navController.navigate(action.getActionId(), null);
        });

        Button editUserBttn = root.findViewById(R.id.acceptBttn);
        editUserBttn.setOnClickListener(v -> {
            patchUserData();
        });

        Button deleteAccountBtt = root.findViewById(R.id.bttDelAccount);
        deleteAccountBtt.setOnClickListener(v -> {
            DialogFragment d = new DeleteAccountDialogFragment(getActivity());
            d.show(getFragmentManager(), "deleteS");
        });

        Button changePassBtt = root.findViewById(R.id.bttChangePwd);
        changePassBtt.setOnClickListener(v -> {
            Bundle args = new Bundle();
            if(user != null) args.putSerializable("userInfo", user);
            else args = null;

            NavDirections action = new ActionOnlyNavDirections(R.id.action_editProfileFragment_to_editPassword);;
            NavController navController = Navigation.findNavController(v);
            navController.navigate(action.getActionId(), args);
        });

        if (getArguments() != null) {
            user = (User) getArguments().getSerializable(ARG_PARAM);
            setTextView(root, user);
        }

        return root;
    }

    public void patchUserData() {
        User u = getNewInfo();
        Call<String> call = mTodoService.patchSelfUserData(user);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if(response.isSuccessful()) {
                    NavDirections action = new ActionOnlyNavDirections(R.id.action_editProfile_to_profileFragment);;
                    NavController navController = Navigation.findNavController(getView());
                    navController.navigate(action.getActionId(), null);
                } else
                    Toast.makeText(EditProfile.this.getContext(), "Error updating info", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<String> str, Throwable t) {
                Toast.makeText(EditProfile.this.getContext(), "Error connecting to server", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void setTextView(View root, User u){
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        String strDate = dateFormat.format(u.birthday);
        int [] views = new int[]{R.id.usernameEdit, R.id.nameEdit, R.id.emailEdit, R.id.telEdit, R.id.birthEdit};
        String [] txt = new String[]{u.username, u.name, u.email, String.valueOf(u.tel), strDate};
        for(int i = 0; i < views.length; i++){
            TextView v = Objects.requireNonNull(root).findViewById(views[i]);
            if(txt[i] != null) v.setText(txt[i]);
        }

        Spinner spin = root.findViewById(R.id.locationEdit);
        spin.setSelection(getIndexElement(spin, u.location));
    }

    public User getNewInfo() {
        User u = user;
        EditText aux = getView().findViewById(R.id.usernameEdit);
        u.setUsername(aux.getText().toString());
        aux = getView().findViewById(R.id.nameEdit);
        u.setName(aux.getText().toString());
        aux = getView().findViewById(R.id.emailEdit);
        u.setEmail(aux.getText().toString());
        aux = getView().findViewById(R.id.telEdit);
        u.setTel(Long.valueOf(aux.getText().toString()));
        aux = getView().findViewById(R.id.birthEdit);
        @SuppressLint("SimpleDateFormat") DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        Date date;
        try {
            String dob_var = (aux.getText().toString());
            date = formatter.parse(dob_var);
            u.setBirthday(date);
        } catch (Exception e) { date = null; }
        Spinner spin = getView().findViewById(R.id.locationEdit);
        u.setLocation(spin.getSelectedItem().toString());
        return u;
    }

    public ArrayAdapter<String> getPossibleLocations() {
        String [] str = new String [] {
            "Select location... ", "Girona", "Barcelona", "Lleida", "Tarragona"
        };
        return new ArrayAdapter<String>(getContext(),R.layout.spinneritem, str){
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

    public int getIndexElement(Spinner spin, String elem){
        Adapter adapter = spin.getAdapter();
        int n = adapter.getCount(), pos = 0;
        boolean found = false;
        while (!found && pos < n) {
            String s = (String) adapter.getItem(pos);
            found = s.matches(elem);
            if (!found) pos++;
        }
        if(!found) pos = 0;
        return pos;
    }
}
