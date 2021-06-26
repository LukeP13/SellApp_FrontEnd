package org.udg.pds.todoandroid.fragment;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
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

import androidx.fragment.app.Fragment;
import androidx.navigation.ActionOnlyNavDirections;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import org.udg.pds.todoandroid.R;
import org.udg.pds.todoandroid.SellApp;
import org.udg.pds.todoandroid.entity.IdObject;
import org.udg.pds.todoandroid.entity.Product;
import org.udg.pds.todoandroid.entity.User;
import org.udg.pds.todoandroid.rest.TodoApi;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */

public class EditProductFragment extends Fragment {

    private static final String ARG_PARAM = "Product";

    private Product product;
    TodoApi mTodoService;


    @Override
    public void onStart() {
        super.onStart();
        mTodoService = ((SellApp)this.getActivity().getApplication()).getAPI();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_edit_product, container, false);

        Spinner loc = root.findViewById(R.id.inputLocation);
        loc.setAdapter(getPossibleLocations());

       if (getArguments() != null) {
            product = (Product) getArguments().getSerializable(ARG_PARAM);
            setTextView(root, product);
       }

        Button editProdBttn = root.findViewById(R.id.editProductButton);
        editProdBttn.setOnClickListener(v -> {
            patchProductData();
        });

        return root;
    }


    public void patchProductData() {
        Product p = getNewInfo();
        Call<String> call = mTodoService.patchProdData(product.getIdProduct(),p);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if(response.isSuccessful()) {
                    NavDirections action = new ActionOnlyNavDirections(R.id.action_editProductFragment_to_profileFragment);
                    NavController navController = Navigation.findNavController(getView());
                    navController.navigate(action.getActionId(), null);
                } else {
                    Toast.makeText(EditProductFragment.this.getContext(), "Error updating info", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<String> str, Throwable t) {
            }
        });
    }


    public void setTextView(View root, Product p){
        int [] views = new int[]{R.id.inputProductName, R.id.inputProductDescription, R.id.inputProductPrice};
        String [] txt = new String[]{p.getName(),p.getDescription(), String.valueOf(p.getPrice())};
        for(int i = 0; i < views.length; i++){
            TextView v = Objects.requireNonNull(root).findViewById(views[i]);
            if(txt[i] != null) v.setText(txt[i]);
        }

        Spinner spin = root.findViewById(R.id.inputLocation);
        spin.setSelection(getIndexElement(spin, p.getLocation()));
    }


    public Product getNewInfo() {
        Product p = product;
        EditText aux = getView().findViewById(R.id.inputProductName);
        p.setName(aux.getText().toString());
        aux = getView().findViewById(R.id.inputProductDescription);
        p.setDescription(aux.getText().toString());
        aux = getView().findViewById(R.id.inputProductPrice);
        p.setPrice(Double.parseDouble(aux.getText().toString()));

        Spinner spin = getView().findViewById(R.id.inputLocation);
        p.setLocation(spin.getSelectedItem().toString());
        return p;
    }

    private String getStr(EditText txt){
        return txt.getText().toString();
    }

    public ArrayAdapter<String> getPossibleLocations() {
        String [] str = new String [] {
            "Select location... ", "Girona", "Barcelona", "Lleida", "Tarragona"
        };
        return new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_dropdown_item, str){
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
