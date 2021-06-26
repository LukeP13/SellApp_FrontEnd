package org.udg.pds.todoandroid.fragment;

import android.content.Context;
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
import org.udg.pds.todoandroid.Adapter.*;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class WishListFragment extends Fragment {

    TodoApi mTodoService;
    RecyclerView mRecyclerView;
    private PRAdapter mAdapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
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

        checkFavoritedProducts();

        mRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getContext(), mRecyclerView, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

                Product product = mAdapter.getItem(position);

                Bundle args = new Bundle();
                args.putSerializable("Product", product);

                NavDirections action = WishListFragmentDirections.actionWishListFragmentToFragmentProduct();

                NavController navController = Navigation.findNavController(view);
                navController.navigate(action.getActionId(), args);

            }

            @Override
            public void onLongItemClick(View view, int position) {
                // Podriem fer una vista previa
            }
        }));

    }

    public void checkFavoritedProducts(){
        Call<List<Product>> call = mTodoService.getUserFavoriteProducts();
        mAdapter.clear();
        call.enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {

                if (response.isSuccessful()) {
                    List<Product> productsFound = response.body();
                    showFavoritedProducts(productsFound);
                }

                else {
                    Toast toast = Toast.makeText(WishListFragment.this.getActivity(), "There are no products saved to the Wish List", Toast.LENGTH_SHORT);
                    toast.show();
                }
            }

            @Override
            public void onFailure(Call<List<Product>> str, Throwable t) {
                Toast toast = Toast.makeText(WishListFragment.this.getActivity(), "Error in searching products", Toast.LENGTH_SHORT);
                toast.show();
            }
        });


    }

    private void showFavoritedProducts(List<Product> pFound){
        mAdapter.clear();
        if (pFound.isEmpty()){
            Toast toast = Toast.makeText(WishListFragment.this.getActivity(), "There are no products found on your Wish List", Toast.LENGTH_SHORT);
            toast.show();
        }
        else for (Product p : pFound) mAdapter.add(p);
    }
}
