package com.example.sqlitesampleapp;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class DeleteConfirmationFragment extends DialogFragment {

    static final String TAG = "DeleteConfirmationFragment";

    private DeleteConfirmationListener listener;


    public interface DeleteConfirmationListener {
        void onDeleteConfirmed();
        void onDeleteCancelled();
    }

    public static DeleteConfirmationFragment newInstance() {
        return new DeleteConfirmationFragment();
    }



    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        try {
            listener = (DeleteConfirmationListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement OnDeleteConfirmationListener");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setMessage("Are you sure you want to delete your profile?")
                .setPositiveButton("Delete", (dialog, which) -> listener.onDeleteConfirmed())
                .setNegativeButton("Cancel", (dialog, which) -> listener.onDeleteCancelled());
        return builder.create();
    }


}
