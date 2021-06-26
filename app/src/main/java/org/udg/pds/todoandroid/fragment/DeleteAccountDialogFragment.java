package org.udg.pds.todoandroid.fragment;



import android.app.Activity;
import android.app.Dialog;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;


import org.udg.pds.todoandroid.R;
import org.udg.pds.todoandroid.SellApp;
import org.udg.pds.todoandroid.activity.Login;
import org.udg.pds.todoandroid.activity.NavigationActivity;
import org.udg.pds.todoandroid.rest.TodoApi;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DeleteAccountDialogFragment extends DialogFragment {

    TodoApi mTodoService;
    Activity activity;

    public DeleteAccountDialogFragment(Activity _a){
        activity = _a;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        mTodoService = ((SellApp) this.getActivity().getApplication()).getAPI();
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.dialog_delete_account)
            .setMessage(R.string.dialog_message_delete)
            .setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    deleteUser();
                }
            })
            .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    DeleteAccountDialogFragment.this.getDialog().dismiss();
                }
            });
        // Create the AlertDialog object and return it
        return builder.create();
    }

    public void deleteUser() {
        Call<String> call = mTodoService.deleteSelfUser();
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if(response.isSuccessful()) {
                    try {
                        activity.startActivity(new Intent(activity, Login.class));
                        activity.finish();
                    } catch (Throwable throwable) {
                        throwable.printStackTrace();
                    }
                } else
                    Toast.makeText(getContext(), "Error deleting info", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onFailure(Call<String> str, Throwable t) {
                Toast.makeText(getContext(), "Error connecting to server", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
