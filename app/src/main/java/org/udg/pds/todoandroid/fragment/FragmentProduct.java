package org.udg.pds.todoandroid.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.navigation.ActionOnlyNavDirections;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.udg.pds.todoandroid.R;

import org.udg.pds.todoandroid.entity.Product;

import org.udg.pds.todoandroid.SellApp;
import org.udg.pds.todoandroid.entity.Product;
import org.udg.pds.todoandroid.entity.User;

import org.udg.pds.todoandroid.rest.TodoApi;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FragmentProduct extends Fragment {

    private TextView etNameProduct;
    private TextView etPriceProduct;
    private TextView etDescriptionProduct;

    private Button btnWishlist;
    TodoApi mTodoService;

    private Bundle args;
    private Product product;

    private Button btnDeleteProduct;
    private Button btnEditProduct;
    private Button btnSellerProf;
    private Button btnRequest;

    private boolean existInWishlist;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        View view = inflater.inflate(R.layout.fragment_product, container, false);

        etNameProduct = view.findViewById(R.id.etNameProduct);
        etPriceProduct = view.findViewById(R.id.etPriceProduct);
        etDescriptionProduct = view.findViewById(R.id.etDescriptionProduct);


        btnWishlist = view.findViewById(R.id.btnWishlist);
        btnEditProduct = view.findViewById(R.id.btnEditProduct);
        btnDeleteProduct = view.findViewById(R.id.btnDeleteProduct);
        btnSellerProf = view.findViewById(R.id.btnSellerProf);
        btnRequest = view.findViewById(R.id.btnRequest);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        mTodoService = ((SellApp) this.getActivity().getApplication()).getAPI();
        args = getArguments();
        product = (Product) args.getSerializable("Product");

        etNameProduct.setText(product.getName());
        etDescriptionProduct.setText(product.getDescription());
        etPriceProduct.setText(product.getPrice().toString());

        btnWishlist.setVisibility(View.GONE);
        btnRequest.setVisibility(View.GONE);
        btnSellerProf.setVisibility(View.GONE);

        if (product.getState() == 0) {
            btnRequest.setEnabled(true);
            btnRequest.setText(R.string.send_buy_request);
        } else if (product.getState() == 1) {
            btnRequest.setEnabled(false);
            btnRequest.setText(R.string.requested);
        } else if (product.getState() == 2) {
            btnRequest.setEnabled(false);
            btnRequest.setText(R.string.sold);
        }

        chechOwner();

        btnRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (product.getState() == 0) {
                    product.setState((byte) 1);
                    updateProductStatus();
                    sendBuyRequestMessage();
                } else if (product.getState() == 1) {
                    product.setState((byte) 2);
                    updateProductStatus();
                }
            }
        });

        btnSellerProf.setOnClickListener(v -> {
            NavDirections action = new ActionOnlyNavDirections(R.id.action_fragmentProduct_to_profileFragmentSeller);
            NavController navController = Navigation.findNavController(v);
            navController.navigate(action.getActionId(), args);
        });

        btnWishlist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (existInWishlist) {
                    removeProductFromWishlist(product);
                    existInWishlist = false;
                    btnWishlist.setText(R.string.add_to_your_wishlist);
                } else {
                    addProductToWishlist(product);
                    existInWishlist = true;
                    btnWishlist.setText(R.string.delete_from_your_wishlist);
                }
            }
        });
    }

    private void addProductToWishlist(Product product) {
        Long productId = product.getIdProduct();
        Call<String> call = mTodoService.addProductToWishlist(productId);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                Toast toast = Toast.makeText(FragmentProduct.this.getActivity(), "Product added to your Wish List", Toast.LENGTH_SHORT);
                toast.show();
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Toast toast = Toast.makeText(FragmentProduct.this.getActivity(), "There was an error adding your product in your Wish List", Toast.LENGTH_SHORT);
                toast.show();
            }
        });
    }

    private void removeProductFromWishlist(Product product) {
        Long productId = product.getIdProduct();
        Call<String> call = mTodoService.deleteProductFromWishList(productId);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                Toast toast = Toast.makeText(FragmentProduct.this.getActivity(), "Product deleted from your Wish List", Toast.LENGTH_SHORT);
                toast.show();
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Toast toast = Toast.makeText(FragmentProduct.this.getActivity(), "There was an error deleting the product from your Wish List", Toast.LENGTH_SHORT);
                toast.show();
            }
        });
    }

    private void checkProduct(Product product) {
        Long productId = product.getIdProduct();
        Call<Product> call = mTodoService.checkIfProductExistsOnWishList(productId);
        call.enqueue(new Callback<Product>() {
            @Override
            public void onResponse(Call<Product> call, Response<Product> response) {
                existInWishlist = true;
                btnWishlist.setText(R.string.delete_from_your_wishlist);
            }

            @Override
            public void onFailure(Call<Product> call, Throwable t) {
                existInWishlist = false;
                btnWishlist.setText(R.string.add_to_your_wishlist);
            }
        });
    }

    public void chechOwner() {
        Call<User> call = mTodoService.getMe();
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful()) {
                    User u = response.body();
                    if (u.id == product.getOwnerId()) {
                        setInvisibleButtons();
                        if (product.getState() == 0) {
                            btnRequest.setVisibility(View.GONE);
                        } else if (product.getState() == 1) {
                            btnRequest.setEnabled(true);
                            btnRequest.setVisibility(View.VISIBLE);
                            btnRequest.setText(R.string.confirm_buy_request);
                        } else {
                            btnRequest.setVisibility(View.GONE);
                        }
                    }
                    else {
                        LinearLayout linEditProduct = getView().findViewById(R.id.linEditProduct);
                        linEditProduct.setVisibility(View.GONE);
                        btnWishlist.setVisibility(View.VISIBLE);
                        btnSellerProf.setVisibility(View.VISIBLE);
                        if (product.getState() == 0) {
                            btnRequest.setEnabled(true);
                            btnRequest.setVisibility(View.VISIBLE);
                            btnRequest.setText(R.string.send_buy_request);
                        } else if (product.getState() == 1) {
                            btnRequest.setEnabled(false);
                            btnRequest.setVisibility(View.VISIBLE);
                            btnRequest.setText(R.string.requested);
                        } else {
                            btnRequest.setEnabled(false);
                            btnRequest.setVisibility(View.VISIBLE);
                            btnRequest.setText(R.string.sold);
                        }
                        checkProduct(product);
                    }

                } else
                    Toast.makeText(FragmentProduct.this.getContext(), "Error Getting User Data", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Toast.makeText(FragmentProduct.this.getContext(), "Error in connection", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void setInvisibleButtons() {
        btnEditProduct.setOnClickListener(v -> {
            NavDirections action = new ActionOnlyNavDirections(R.id.action_fragmentProduct_to_editProductFragment);
            NavController navController = Navigation.findNavController(v);
            navController.navigate(action.getActionId(), args);
        });

        btnDeleteProduct.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setCancelable(true);
            builder.setMessage("Are you sure you want to delete this product?");
            builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    delete(v);
                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });

            AlertDialog dialog = builder.create();
            dialog.show();
        });
    }

    private void updateProductStatus() {
        Call<String> callPathProduct = mTodoService.patchProdData(product.getIdProduct(), product);
        callPathProduct.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> callPathProduct, Response<String> response) {
                if (response.isSuccessful()) {
                    if (product.getState() == 1) {
                        Toast.makeText(FragmentProduct.this.getContext(), "Request sent", Toast.LENGTH_SHORT).show();
                        btnRequest.setText(R.string.requested);
                        btnRequest.setEnabled(false);
                    } else if (product.getState() == 2) {
                        Toast.makeText(FragmentProduct.this.getContext(), "Product sold", Toast.LENGTH_SHORT).show();
                        btnRequest.setText(R.string.sold);
                        btnRequest.setEnabled(false);
                    }
                } else {
                    Toast.makeText(FragmentProduct.this.getContext(), "Error updating info", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<String> str, Throwable t) {
            }
        });
    }

    private void sendBuyRequestMessage() {
        Call<String> callSendMessage = mTodoService.sendBuyRequestMessage(product.getOwnerId());
        callSendMessage.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful()) {

                } else {

                }
            }

            @Override
            public void onFailure(Call<String> str, Throwable t) {
            }
        });
    }

    private void delete (View v){

        Call<String> call = mTodoService.deleteProduct(product.getIdProduct());
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful()) {
                    NavDirections action = new ActionOnlyNavDirections(R.id.action_fragmentProduct_to_profileFragment);
                    NavController navController = Navigation.findNavController(v);
                    navController.navigate(action.getActionId(), args);
                } else
                    Toast.makeText(FragmentProduct.this.getContext(), "Error updating info", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<String> str, Throwable t) {

            }
        });
    }
}
