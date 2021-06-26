package org.udg.pds.todoandroid.fragment;


import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import org.udg.pds.todoandroid.R;
import org.udg.pds.todoandroid.SellApp;
import org.udg.pds.todoandroid.activity.Login;
import org.udg.pds.todoandroid.rest.TodoApi;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LogoutDialogFragment extends DialogFragment {

    TodoApi mTodoService;
    Activity activity;

    public LogoutDialogFragment (Activity _a){
        activity = _a;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        mTodoService = ((SellApp) this.getActivity().getApplication()).getAPI();
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.dialog_logout_title)
            .setMessage(R.string.dialog_logout_message)
            .setPositiveButton(R.string.logout, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    logout();
                }
            })
            .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    LogoutDialogFragment.this.getDialog().dismiss();
                }
            });
        // Create the AlertDialog object and return it
        return builder.create();
    }

    public void logout() {
        Call<String> call = mTodoService.logout();
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
                    Toast.makeText(getContext(), "Error logging out", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onFailure(Call<String> str, Throwable t) {
                Toast.makeText(getContext(), "Error connecting to server", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
