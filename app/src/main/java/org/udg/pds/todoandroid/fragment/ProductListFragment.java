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

import org.udg.pds.todoandroid.R;
import org.udg.pds.todoandroid.SellApp;
import org.udg.pds.todoandroid.entity.Product;
import org.udg.pds.todoandroid.rest.TodoApi;
import org.udg.pds.todoandroid.util.Global;
import org.udg.pds.todoandroid.Adapter.*;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by imartin on 12/02/16.
 */
public class ProductListFragment extends Fragment {

    TodoApi mTodoService;

    RecyclerView mRecyclerView;
    private PRAdapter mAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        return inflater.inflate(R.layout.fragment_product_list, container, false);
    }

    @Override
    public void onStart() {

        super.onStart();
        mTodoService = ((SellApp) this.getActivity().getApplication()).getAPI();

        mRecyclerView = getView().findViewById(R.id.productRecyclerView);
        mAdapter = new PRAdapter(this.getActivity().getApplication());
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this.getActivity()));
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this.getActivity(), 2);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getContext(), mRecyclerView, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

                Product product = mAdapter.getItem(position);

                Bundle args = new Bundle();
                args.putSerializable("Product", product);

                NavDirections action = ProductListFragmentDirections.actionProductListFragmentToFragmentProduct();

                NavController navController = Navigation.findNavController(view);
                navController.navigate(action.getActionId(), args);
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
        this.updateProductList();
    }

    public void showProductList(List<Product> pl) {
        mAdapter.clear();
        for (Product p : pl) {
            mAdapter.add(p);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Global.RQ_ADD_TASK) {
            this.updateProductList();
        }
    }

    public void updateProductList() {

        Call<List<Product>> call = mTodoService.getAllProducts();

        call.enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                if (response.isSuccessful()) {
                    ProductListFragment.this.showProductList(response.body());
                } else {
                    Toast.makeText(ProductListFragment.this.getContext(), "Error reading products", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {

            }
        });
    }
}
