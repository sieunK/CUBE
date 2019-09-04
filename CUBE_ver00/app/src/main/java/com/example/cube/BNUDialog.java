package com.example.cube;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class BNUDialog extends DialogFragment {
    public static final String TAG = "BNUDialog";

    private TextView dialogTitleView;
    private String  dialogTitle;
    public BNUDialog(){}

    public static BNUDialog newInstance(String title){
            BNUDialog dialog = new BNUDialog();
            Bundle args =new Bundle();
            args.putString("title", title);
            dialog.setArguments(args);
            return dialog;
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        dialogTitle =  getArguments().getString("title");

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.bnu_dialog, null);
        dialogTitleView = view.findViewById(R.id.bnu_dialog_title);
        dialogTitleView.setText(dialogTitle);
        Log.d("DialogTITLE" , dialogTitle);
        builder.setView(view);
        return builder.create();
    }


}
