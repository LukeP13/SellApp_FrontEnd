package org.udg.pds.todoandroid.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.ActionOnlyNavDirections;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import org.udg.pds.todoandroid.R;
import org.udg.pds.todoandroid.SellApp;
import org.udg.pds.todoandroid.entity.Rate;
import org.udg.pds.todoandroid.entity.User;
import org.udg.pds.todoandroid.rest.TodoApi;
import org.w3c.dom.Text;

import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AddRate#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddRate extends Fragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "userInfo";

    TodoApi mTodoService;

    private User userRated;
    private View view;
    private TextView description;
    private RatingBar ratingBar;


    public AddRate() {}


    public static AddRate newInstance(User _user) {
        AddRate fragment = new AddRate();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PARAM1, _user);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            userRated = (User) getArguments().getSerializable(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.f_add_rate, container, false);

        ratingBar = view.findViewById(R.id.ratingBar);
        description = view.findViewById(R.id.rateReview);

        Button cancelBttn = view.findViewById(R.id.cancelBttn);
        cancelBttn.setOnClickListener(v -> {
            //Go to whatever fragment you should
            NavDirections action = new ActionOnlyNavDirections(R.id.action_addRate_to_productListFragment);
            NavController navController = Navigation.findNavController(v);
            navController.navigate(action.getActionId(), null);
        });

        Button sendBtt = view.findViewById(R.id.send_btt);
        sendBtt.setOnClickListener(v -> {
            if(validReview()) sendRate();
        });

        if(userRated != null){
            TextView uName = view.findViewById(R.id.userName);
            uName.setText(userRated.getName());
        }

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        mTodoService = ((SellApp) this.getActivity().getApplication()).getAPI();
    }

    private boolean validReview() {
        boolean res = false;

        String descTxt = this.description.getText().toString();
        Float rate = this.ratingBar.getRating();

        if(descTxt.length() < 5)
            Toast.makeText(AddRate.this.getContext(), (5-descTxt.length()) + " more characters. Come on!", Toast.LENGTH_SHORT).show();
        else if(descTxt.length() >= 100)
            Toast.makeText(AddRate.this.getContext(), "Maximum characters 100!!", Toast.LENGTH_SHORT).show();
        else if (rate <= 0)
            Toast.makeText(AddRate.this.getContext(), "Invalid rate: " + rate, Toast.LENGTH_SHORT).show();
        else if (userRated.getId() < 1)
            Toast.makeText(AddRate.this.getContext(), "Invalid user Rated" + rate, Toast.LENGTH_SHORT).show();
        else
            res = true;

        return res;
    }

    private void sendRate () {
        Rate r = new Rate();
        r.description = this.description.getText().toString();
        r.rate = this.ratingBar.getRating();
        r.date = new Date();

        Call<String> call = mTodoService.addRate(userRated.getId(), r);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if(response.isSuccessful()){
                    Bundle args = new Bundle();
                    if(userRated != null) args.putSerializable(ARG_PARAM1, userRated);
                    else args = null;

                    NavDirections action = new ActionOnlyNavDirections(R.id.action_addRate_to_profileFragment);
                    NavController navController = Navigation.findNavController(view);
                    navController.navigate(action.getActionId(), args);
                } else {
                    Toast.makeText(AddRate.this.getContext(), "Error sending review", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Toast.makeText(AddRate.this.getContext(), "Error connecting to server", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
