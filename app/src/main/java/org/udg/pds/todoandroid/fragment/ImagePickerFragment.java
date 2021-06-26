package org.udg.pds.todoandroid.fragment;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;


public abstract class ImagePickerFragment extends Fragment{

    private static final int GALLERY_REQUEST_CODE = 55;
    final int PIC_CROP = 1;
    private ImageView imageView;
    Uri imageUri;

    public void pickFromGallery(ImageView i){
        imageView = i;
        //Create an Intent with action as ACTION_PICK
        Intent intent=new Intent(Intent.ACTION_PICK);
        // Sets the type as image/*. This ensures only components of type image are selected
        intent.setType("image/*");
        //We pass an extra array with the accepted mime types. This will ensure only components with these MIME types as targeted.
        String[] mimeTypes = {"image/jpeg", "image/png"};
        intent.putExtra(Intent.EXTRA_MIME_TYPES,mimeTypes);
        // Launching the Intent
        startActivityForResult(intent,GALLERY_REQUEST_CODE);
    }

    public void onActivityResult(int requestCode,int resultCode,Intent data){
        // Result code is RESULT_OK only if the user selects an Image
        if (resultCode == Activity.RESULT_OK)
            switch (requestCode){
                case GALLERY_REQUEST_CODE:
                    //data.getData returns the content URI for the selected Image
                    imageUri = data.getData();

                    imageView.setImageURI(imageUri);

                    break;
            }
    }

    public void setImageView(ImageView _img) { imageView = _img; }
}
