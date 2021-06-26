package org.udg.pds.todoandroid.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.udg.pds.todoandroid.Adapter.PRAdapter;
import org.udg.pds.todoandroid.Adapter.RateAdapter;
import org.udg.pds.todoandroid.Adapter.RecyclerItemClickListener;
import org.udg.pds.todoandroid.R;
import org.udg.pds.todoandroid.SellApp;
import org.udg.pds.todoandroid.entity.Rate;
import org.udg.pds.todoandroid.entity.User;
import org.udg.pds.todoandroid.rest.TodoApi;
import org.udg.pds.todoandroid.util.Global;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RateListFragment  extends Fragment {
    private static final String ARG_PARAM = "userInfo";

    TodoApi mTodoService;

    RecyclerView mRecyclerView;
    private RateAdapter mAdapter;
    private User user;

    public static RateListFragment newInstance(User u){
        RateListFragment fragment = new RateListFragment();

        Bundle args = new Bundle();
        args.putSerializable(ARG_PARAM, u);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            user = (User) getArguments().getSerializable(ARG_PARAM);
        }

        return inflater.inflate(R.layout.rating_list, container, false);
    }

    @Override
    public void onStart() {

        super.onStart();
        mTodoService = ((SellApp) this.getActivity().getApplication()).getAPI();

        mRecyclerView = getView().findViewById(R.id.rateRecyclerView);
        mAdapter = new RateAdapter(this.getActivity().getApplication());
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this.getActivity()));
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this.getActivity(), 1);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getContext(), mRecyclerView, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

//                Rate rate = mAdapter.getItem(position);
//
//                Bundle args = new Bundle();
//                args.putSerializable("Rate", rate);
//
//                NavDirections action = ;
//
//                NavController navController = Navigation.findNavController(view);
//                navController.navigate(action.getActionId(), args);
            }

            @Override
            public void onLongItemClick(View view, int position) {
                // Podriem fer una vista previa
            }
        }));
    }

    @Override
    public void onResume() {
        super.onResume();
        this.updateRateList();
    }

    public void showRateList(List<Rate> pl) {
        mAdapter.clear();
        for (Rate p : pl) {
            mAdapter.add(p);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Global.RQ_ADD_TASK) {
            this.updateRateList();
        }
    }

    public void updateRateList() {

        Call<List<Rate>> call = mTodoService.getRates(user.getId());

        call.enqueue(new Callback<List<Rate>>() {
            @Override
            public void onResponse(Call<List<Rate>> call, Response<List<Rate>> response) {
                if (response.isSuccessful()) {
                    RateListFragment.this.showRateList(response.body());
                } else {
                    Toast.makeText(RateListFragment.this.getContext(), "Error reading rates", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<List<Rate>> call, Throwable t) {
                Toast.makeText(RateListFragment.this.getContext(), "Error connecting to the server", Toast.LENGTH_LONG).show();
            }
        });
    }
}
