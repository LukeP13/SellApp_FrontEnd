package org.udg.pds.todoandroid.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.navigation.ActionOnlyNavDirections;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import org.udg.pds.todoandroid.R;
import org.udg.pds.todoandroid.SellApp;
import org.udg.pds.todoandroid.entity.Product;
import org.udg.pds.todoandroid.entity.User;
import org.udg.pds.todoandroid.rest.TodoApi;
import org.udg.pds.todoandroid.util.Global;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragmentSeller extends Fragment{

    TodoApi mTodoService;
    Bundle args;
    private ImageView avatarImageView;
    private View view;
    Product p;
    User user;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            p = (Product) getArguments().getSerializable("Product");
        }
    }


    @Override
    public void onStart() {
        super.onStart();
        mTodoService = ((SellApp) this.getActivity().getApplication()).getAPI();
    }


    @Override
    public void onResume() {
        super.onResume();
        if(user == null) this.getUserInfo();
        else updateView(user);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View root = inflater.inflate(R.layout.fragment_profile_seller, container, false);
        this.view = root;


        Button goToProfile = root.findViewById(R.id.goToProfile);
        Button rateOwner = root.findViewById(R.id.rateOwner);

        goToProfile.setOnClickListener(v -> {

            Call<User> call = mTodoService.getUser(p.getOwnerId());
            call.enqueue(new Callback<User>() {
                @Override
                public void onResponse(Call<User> call, Response<User> response) {
                    if(response.isSuccessful()){
                        User u = response.body();
                        Bundle args = new Bundle();
                        args.putSerializable("userInfo", u);

                        NavDirections action = new ActionOnlyNavDirections(R.id.action_profileFragmentSeller_to_profileFragment);
                        NavController navController = Navigation.findNavController(v);
                        navController.navigate(action.getActionId(), args);
                    } else {
                        Toast.makeText(ProfileFragmentSeller.this.getContext(), "Error Getting User Data", Toast.LENGTH_SHORT).show();
                    }
                }
                @Override
                public void onFailure(Call<User> call, Throwable t) {
                    Toast.makeText(ProfileFragmentSeller.this.getContext(), "Error in connection", Toast.LENGTH_SHORT).show();
                }
            });

        });

        rateOwner.setOnClickListener(v -> {
            Call<User> call = mTodoService.getUser(p.getOwnerId());
            call.enqueue(new Callback<User>() {
                @Override
                public void onResponse(Call<User> call, Response<User> response) {
                    if(response.isSuccessful()){
                        User u = response.body();
                        Bundle args = new Bundle();
                        args.putSerializable("userInfo", u);

                        NavDirections action = ProfileFragmentSellerDirections.actionProfileFragmentSellerToAddRate();
                        NavController navController = Navigation.findNavController(v);
                        navController.navigate(action.getActionId(), args);

                    } else {
                        Toast.makeText(ProfileFragmentSeller.this.getContext(), "Error Getting User Data", Toast.LENGTH_SHORT).show();
                    }
                }
                @Override
                public void onFailure(Call<User> call, Throwable t) {
                    Toast.makeText(ProfileFragmentSeller.this.getContext(), "Error in connection", Toast.LENGTH_SHORT).show();
                }
            });

        });

        return root;
    }


    public void getUserInfo(){
        mTodoService = ((SellApp) this.getActivity().getApplication()).getAPI();
        if (getArguments() != null) {
            p = (Product) getArguments().getSerializable("Product");
        }

        Call<User> call = mTodoService.getUser(p.getOwnerId());
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if(response.isSuccessful()){
                    user = response.body();
                    updateView(user);
                } else {
                    Toast.makeText(ProfileFragmentSeller.this.getContext(), "Error Getting User Data", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Toast.makeText(ProfileFragmentSeller.this.getContext(), "Error in connection", Toast.LENGTH_SHORT).show();
            }
        });
    }


    public void updateView(User u){
        TextView nameView = getView().findViewById(R.id.nameView);
        TextView phoneView = getView().findViewById(R.id.phoneView);
        TextView emailView = getView().findViewById(R.id.emailView);
        ImageView avatarView = getView().findViewById(R.id.avatarImage);

        if(u.name != null) {
            nameView.setText(u.name);
        }
        if(u.tel != null) {
            phoneView.setText(String.valueOf(u.tel));
        }
        if(u.email != null){
            emailView.setText(u.email);
        }

        //if(u.avatar != null) avatarView.setImageResource(u.avatar);
    }
}

