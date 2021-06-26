package org.udg.pds.todoandroid.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.navigation.ActionOnlyNavDirections;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import org.udg.pds.todoandroid.R;
import org.udg.pds.todoandroid.SellApp;
import org.udg.pds.todoandroid.entity.User;
import org.udg.pds.todoandroid.rest.TodoApi;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditPassword extends Fragment {
    private static final String ARG_PARAM = "userInfo";

    private User user;
    TodoApi mTodoService;

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
        View root = inflater.inflate(R.layout.fragment_edit_password, container, false);

        Button cancelBttn = root.findViewById(R.id.cancelBttn);
        cancelBttn.setOnClickListener(v -> {
            Bundle args = new Bundle();
            if(user != null) args.putSerializable("userInfo", user);
            else args = null;

            NavDirections action = new ActionOnlyNavDirections(R.id.action_editPassword_to_editProfileFragment);;
            NavController navController = Navigation.findNavController(v);
            navController.navigate(action.getActionId(), args);
        });

        Button editUserBttn = root.findViewById(R.id.acceptBttn);
        editUserBttn.setOnClickListener(v -> {
            String password = getPassFromView();
            if(password == null)
                Toast.makeText(EditPassword.this.getContext(), "Passwords do not match", Toast.LENGTH_SHORT).show();
            else if(password.matches(""))
                Toast.makeText(EditPassword.this.getContext(), "Invalid Password", Toast.LENGTH_SHORT).show();
            else
                patchPassword(password);

        });

        if (getArguments() != null) {
            user = (User) getArguments().getSerializable(ARG_PARAM);
        }

        return root;
    }

    public void patchPassword(String pssw) {
        user.setPassword(pssw);
        Call<String> call = mTodoService.patchSelfUserData(user);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if(response.isSuccessful()) {
                    NavDirections action = new ActionOnlyNavDirections(R.id.action_editPassword_to_profileFragment);;
                    NavController navController = Navigation.findNavController(getView());
                    navController.navigate(action.getActionId(), null);
                } else
                    Toast.makeText(EditPassword.this.getContext(), "Error updating password", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onFailure(Call<String> str, Throwable t) {
                Toast.makeText(EditPassword.this.getContext(), "Error connecting to server", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public String getPassFromView() {
        TextView newPass = getView().findViewById(R.id.passEdit);
        TextView confirmPass = getView().findViewById(R.id.confirmPass);

        String ret = newPass.getText().toString();
        if(!ret.matches(confirmPass.getText().toString())) ret = null;

        return ret;
    }
}
