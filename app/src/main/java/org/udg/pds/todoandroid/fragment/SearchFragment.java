package org.udg.pds.todoandroid.fragment;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.udg.pds.todoandroid.R;
import org.udg.pds.todoandroid.SellApp;
import org.udg.pds.todoandroid.entity.Product;
import org.udg.pds.todoandroid.rest.TodoApi;
import org.udg.pds.todoandroid.Adapter.*;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class SearchFragment extends Fragment {

    TodoApi mTodoService;

    RecyclerView mRecyclerView;
    private PRAdapter mAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        return inflater.inflate(R.layout.fragment_search, container, false);
    }

    @Override
    public void onStart() {

        super.onStart();
        mTodoService = ((SellApp) this.getActivity().getApplication()).getAPI();

        mRecyclerView = getView().findViewById(R.id.searchRecyclerView);
        mAdapter = new PRAdapter(this.getActivity().getApplication());
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this.getActivity()));
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this.getActivity(), 2);
        mRecyclerView.setLayoutManager(mLayoutManager);

        loadAllProducts();

        EditText searchInfo = getView().findViewById(R.id.searchBar);

        searchInfo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 2) {
                    checkProducts(s.toString());
                }
                else {
                    loadAllProducts();
                }
            }
        });

        mRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getContext(), mRecyclerView, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

                Product product = mAdapter.getItem(position);

                Bundle args = new Bundle();
                args.putSerializable("Product", product);

                NavDirections action = SearchFragmentDirections.actionSearchFragmentToFragmentProduct();

                NavController navController = Navigation.findNavController(view);
                navController.navigate(action.getActionId(), args);
            }

            @Override
            public void onLongItemClick(View view, int position) {
                // Podriem fer una vista previa
            }
        }));
    }

    private void loadAllProducts() {
        Call<List<Product>> call = mTodoService.getAllProducts();
        mAdapter.clear();
        call.enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                if (response.isSuccessful()) {
                    showProducts(response.body());
                }
            }

            @Override
            public void onFailure(Call<List<Product>> str, Throwable t) {
                Toast toast = Toast.makeText(SearchFragment.this.getActivity(), "Error in searching products", Toast.LENGTH_SHORT);
                toast.show();
            }
        });
    }

    private void checkProducts(String info) {
        Call<List<Product>> call = mTodoService.searchProduct(info);
        mAdapter.clear();
        call.enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                if (response.isSuccessful()) {
                        showProducts(response.body());
                }
            }

            @Override
            public void onFailure(Call<List<Product>> str, Throwable t) {
                Toast toast = Toast.makeText(SearchFragment.this.getActivity(), "Error in searching products", Toast.LENGTH_SHORT);
                toast.show();
            }
        });
    }

    private void showProducts(List<Product> pFound){
        mAdapter.clear();
        for (Product p : pFound)
            mAdapter.add(p);
    }

}
