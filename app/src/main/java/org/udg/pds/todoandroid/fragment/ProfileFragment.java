package org.udg.pds.todoandroid.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.navigation.ActionOnlyNavDirections;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import org.udg.pds.todoandroid.R;
import org.udg.pds.todoandroid.SellApp;
import org.udg.pds.todoandroid.entity.User;
import org.udg.pds.todoandroid.fragmentPagerAdapter.TabsProfilePagerAdapter;
import org.udg.pds.todoandroid.rest.TodoApi;
import org.udg.pds.todoandroid.util.Global;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends ImagePickerFragment {

    TodoApi mTodoService;
    Bundle args;
    private User lUser;
    private ImageView avatarImageView;
    private View view;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            lUser = (User) getArguments().getSerializable("userInfo");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_profile, container, false);
        this.view = root;

        avatarImageView = root.findViewById(R.id.avatarImage);

        Button btnLogout = root.findViewById(R.id.logoutBttn);
        FloatingActionButton btnEditProfile = root.findViewById(R.id.editButton);
        Button btnUpdateImage = root.findViewById(R.id.updateImageBtt);

        if(lUser == null) {
            btnLogout.setOnClickListener(v -> {
                DialogFragment d = new LogoutDialogFragment(getActivity());
                d.show(getFragmentManager(), "logoutDialog");
            });
            btnEditProfile.setOnClickListener(v -> {
                NavDirections action = new ActionOnlyNavDirections(R.id.action_profileFragment_to_fragment_edit_profile);
                ;
                NavController navController = Navigation.findNavController(v);
                navController.navigate(action.getActionId(), args);
            });
            btnUpdateImage.setOnClickListener(v -> {
                pickFromGallery(avatarImageView);
            });
        } else {
            btnUpdateImage.setVisibility(View.INVISIBLE);
            btnEditProfile.setVisibility(View.INVISIBLE);
            btnLogout.setVisibility(View.INVISIBLE);
            ImageView edAvatar = root.findViewById(R.id.editAvatar);
            edAvatar.setVisibility(View.INVISIBLE);
        }


        Button bttExpand = root.findViewById(R.id.expandButton);
        bttExpand.setOnClickListener(v -> {
            Button b = view.findViewById(R.id.expandButton);
            Float rotation = b.getRotation();
            b.setRotation(rotation + 180);

            int idTop = R.id.avatarImage;
            if(rotation / 180 % 2 == 0) idTop = R.id.dateTxt;

            ConstraintSet constraintSet = new ConstraintSet();
            ConstraintLayout constraintLayout = view.findViewById(R.id.profileSellerConstraint);
            constraintSet.clone(constraintLayout);
            constraintSet.connect(R.id.view_pager, ConstraintSet.TOP, idTop, ConstraintSet.BOTTOM,50);
            constraintSet.applyTo(constraintLayout);
        });

        return root;
    }


    @Override
    public void onStart() {
        super.onStart();
        mTodoService = ((SellApp) this.getActivity().getApplication()).getAPI();
    }

    @Override
    public void onResume() {
        super.onResume();
        if(lUser == null) this.getUserInfo();
        else this.updateView(lUser);


    }

    public void getUserInfo() {

        Call<User> call = mTodoService.getMe();
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if(response.isSuccessful()){
                    User u = response.body();
                    updateView(u);
                } else {
                    Toast.makeText(ProfileFragment.this.getContext(), "Error Getting User Data", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Toast.makeText(ProfileFragment.this.getContext(), "Error in connection", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void updateView(User u){
        String strDate = Global.DATE_ONLY_FORMAT.format(u.birthday);

        TextView nameView = getView().findViewById(R.id.nameView);
        TextView userView = getView().findViewById(R.id.usernameView);
        TextView phoneView = getView().findViewById(R.id.phoneView);
        TextView emailView = getView().findViewById(R.id.emailView);
        TextView birthdayView = getView().findViewById(R.id.birthdayView);
        ImageView avatarView = getView().findViewById(R.id.avatarImage);

        User newU = new User();
        newU.id = u.id;
        if(u.name != null) {
            nameView.setText(u.name);
            newU.setName(u.name);
        }
        if(u.username != null) {
            userView.setText(u.username);
            newU.setUsername(u.username);
        }
        if(u.tel != null) {
            phoneView.setText(String.valueOf(u.tel));
            newU.setTel(u.tel);
        }
        if(u.email != null){
            emailView.setText(u.email);
            newU.setEmail(u.email);
        }
        if(u.birthday != null) {
            birthdayView.setText(strDate);
            newU.setBirthday(u.birthday);
        }
        if(u.location != null) newU.setLocation(u.location);
//                    if(u.avatar != null) avatarView.setImageResource(u.avatar);

        // Arguments User fragment
        Fragment fragmentEdit = new EditProfile();
        args = new Bundle();
        args.putSerializable("userInfo", newU);
        fragmentEdit.setArguments(args);
        lUser = newU;

        if(u.name == null || u.tel == null || u.email == null || u.birthday == null)
            Toast.makeText(ProfileFragment.this.getContext(), "Error Getting User Data", Toast.LENGTH_SHORT).show();
        else {
            TabsProfilePagerAdapter tabsPagerAdapter = new TabsProfilePagerAdapter(ProfileFragment.this, getChildFragmentManager(), lUser);
            ViewPager viewPager = getView().findViewById(R.id.view_pager);
            viewPager.setAdapter(tabsPagerAdapter);
            TabLayout tabs = getView().findViewById(R.id.tabsLayout);
            tabs.setupWithViewPager(viewPager);
        }
    }

}
