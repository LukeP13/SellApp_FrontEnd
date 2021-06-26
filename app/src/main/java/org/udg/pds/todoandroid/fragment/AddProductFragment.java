package org.udg.pds.todoandroid.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import androidx.fragment.app.Fragment;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import com.google.android.gms.common.util.IOUtils;

import org.udg.pds.todoandroid.R;
import org.udg.pds.todoandroid.SellApp;
import org.udg.pds.todoandroid.entity.IdObject;
import org.udg.pds.todoandroid.entity.Product;
import org.udg.pds.todoandroid.entity.Image;
import org.udg.pds.todoandroid.rest.TodoApi;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.Multipart;

/**
 * A simple {@link Fragment} subclass.
 */

public class AddProductFragment extends Fragment{

    ImageView ivSelected;
    ImageView ivDownloaded;
    Uri selectedImage = null;
    String imageUploaded;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        View root = inflater.inflate(R.layout.fragment_add_product, container, false);

        return root;
    }

    TodoApi mTodoService;

    @Override
    public void onStart() {
        super.onStart();

        mTodoService = ((SellApp)this.getActivity().getApplication()).getAPI();

        EditText name = getView().findViewById(R.id.inputProductName);
        EditText description = getView().findViewById(R.id.inputProductDescription);
        EditText price = getView().findViewById(R.id.inputProductPrice);
        EditText tags = getView().findViewById(R.id.inputProductTag);
        Spinner location = getView().findViewById(R.id.inputLocation);
        location.setAdapter(getPossibleLocations());

        ArrayList<EditText> fields = new ArrayList<>();
        fields.add(name); fields.add(description);
        fields.add(price);

        ivSelected = getView().findViewById(R.id.imageViewProduct);

        getView().findViewById(R.id.productImageBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("image/*");
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), 1);
            }
        });

        Button addProductBtn = getView().findViewById(R.id.editProductButton);
        // When the "Save" button is pressed, we make the call to the responder
        addProductBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(!allCampsFilled(fields)) {
                    Toast.makeText(getContext(), "Incomplete Form", Toast.LENGTH_LONG).show();
                }
                else{
                    Date dateAdded = new Date();
                    double priceNumber = Double.parseDouble(price.getText().toString());
                    try {

                        //add Product

                        Product product = new Product(getStr(name), getStr(description), priceNumber, dateAdded, (byte) 0,  new Date(0), location.getSelectedItem().toString());

                        String s = tags.getText().toString();

                        if (!s.isEmpty()) {
                            String[] strings = s.split(",");
                            List<String> stringList = new ArrayList<String>(Arrays.asList(strings));
                            product.setTags((ArrayList<String>) stringList);
                        }

                        Call<IdObject> call = mTodoService.addProduct(product);
                        call.enqueue(new Callback<IdObject>() {
                            @Override
                            public void onResponse(Call<IdObject> call, Response<IdObject> response) {
                                if (response.isSuccessful()) {
                                    Toast.makeText(getContext(), "Product added successfully", Toast.LENGTH_LONG).show();
                                    NavDirections action = AddProductFragmentDirections.actionAddProductFragmentToProductListFragment();
                                    Navigation.findNavController(v).navigate(action);
                                } else {
                                    Toast.makeText(getContext(), "Error adding product", Toast.LENGTH_LONG).show();
                                }
                            }

                            @Override
                            public void onFailure(Call<IdObject> call, Throwable t) {

                            }
                        });

                        //upload Image

                        String extension = "." + MimeTypeMap.getSingleton().getExtensionFromMimeType(getContext().getContentResolver().getType(selectedImage));
                        File tempFile = File.createTempFile("upload", extension, getContext().getCacheDir());
                        RequestBody requestFile =
                            RequestBody.create(
                                MediaType.parse(getContext().getContentResolver().getType(selectedImage)),
                                tempFile
                            );

                        MultipartBody.Part body =
                            MultipartBody.Part.createFormData("file", tempFile.getName(), requestFile);

                        Call<String> callUpload = mTodoService.uploadImage(body);
                        callUpload.enqueue(new Callback<String>() {
                            @Override
                            public void onResponse(Call<String> call, Response<String> response) {
                                if (response.isSuccessful()) {
                                    Toast.makeText(getContext(), "Image uploaded successfully", Toast.LENGTH_LONG).show();
                                    imageUploaded = response.body();
                                } else {
                                    Toast.makeText(getContext(), "Error adding product", Toast.LENGTH_LONG).show();
                                }
                            }

                            @Override
                            public void onFailure(Call<String> call, Throwable t) {

                            }
                        });

                        //add Image to Product

                    } catch (Exception ex) {
                        return;
                    }
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        if (data != null && requestCode==1){
            selectedImage = data.getData();
            ivSelected.setImageURI(selectedImage);
        }
    }

    private boolean allCampsFilled(ArrayList<EditText> fields){
        boolean res = true;
        Iterator<EditText> it = fields.iterator();
        while (res && it.hasNext()){
            if(it.next().getText().toString().matches("")) res = false;
        }

        return res;
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
}
