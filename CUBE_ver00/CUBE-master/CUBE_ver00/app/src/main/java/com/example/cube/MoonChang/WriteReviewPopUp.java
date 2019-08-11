package com.example.cube.MoonChang;

import android.os.Bundle;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;

import com.example.cube.R;


public class WriteReviewPopUp extends DialogFragment {
    OnMyDialogResult mDialogResult;

    private Fragment fragment;
    Button cancel;
    Button dismiss;
    EditText reviewMain;
    RatingBar ratingBar;

    public WriteReviewPopUp() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.popup_write_review, container, false);

        cancel = (Button)view.findViewById(R.id.review_cancel);
        dismiss = (Button)view.findViewById(R.id.review_dismiss);
        reviewMain = (EditText)view.findViewById(R.id.review_write);
        ratingBar = (RatingBar)view.findViewById(R.id.rating_write);

        Bundle args = getArguments();
        String value = args.getString("key");

        fragment = getActivity().getSupportFragmentManager().findFragmentByTag("review");
        dismiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (fragment != null) {
                    if( mDialogResult != null ) {
                        Bundle bundle = new Bundle();
                        bundle.putString("review",reviewMain.getText().toString());
                        bundle.putFloat("rating",ratingBar.getRating());
                        mDialogResult.finish(bundle);
                    }

                    DialogFragment dialogFragment = (DialogFragment) fragment;
                    dialogFragment.dismiss();
                }
            }
        });

        return view;
    }

    public void setDialogResult(OnMyDialogResult dialogResult){
        mDialogResult = dialogResult;
    }

    public interface OnMyDialogResult{
        void finish(Bundle result);
    }

}
