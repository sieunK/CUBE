package com.example.cube.MoonChang;

import android.os.Bundle;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.cube.R;
import com.example.cube.Review.ReviewParent;


public class WriteCommentPopUp extends DialogFragment {
    OnMyDialogResult mDialogResult;

    private Fragment fragment;
    Button cancel;
    Button dismiss;
    EditText commentMain;
    ReviewParent data;

    public WriteCommentPopUp() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.popup_write_comment, container, false);

        cancel = (Button)view.findViewById(R.id.comment_cancel);
        dismiss = (Button)view.findViewById(R.id.comment_dismiss);
        commentMain = (EditText)view.findViewById(R.id.comment_write);

        Bundle args = getArguments();
        String value = args.getString("key");

        fragment = getActivity().getSupportFragmentManager().findFragmentByTag("comment");
        dismiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (fragment != null) {
                    if( mDialogResult != null ) {
                        Bundle bundle = new Bundle();
                        bundle.putString("comment",commentMain.getText().toString());
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
