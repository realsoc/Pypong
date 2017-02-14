package com.realsoc.pipong;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by Hugo on 15/01/2017.
 */

public class NewPlayerDialogFragment extends DialogFragment {
    private TextView input;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (!(activity instanceof YesNoListener)) {
            throw new ClassCastException(activity.toString() + " must implement YesNoListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
        alertDialog.setTitle("Player");
        alertDialog.setMessage("New Players' Name");

        input = new EditText(getContext());
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        if(savedInstanceState != null){
            input.append(savedInstanceState.getString("name"));
        }
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);
        alertDialog.setView(input);
        alertDialog.setPositiveButton("Send",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        //TODO MAX LENGTH 12
                        if(!input.getText().toString().equals("")) {
                            Bundle mBundle = new Bundle();
                            mBundle.putInt("type",PlayersActivity.NEW_PLAYER_DIALOG_FRAGMENT);
                            mBundle.putString("name", input.getText().toString());
                            ((YesNoListener) getActivity()).onYes(mBundle);
                        }
                    }

                });

        alertDialog.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        ((YesNoListener) getActivity()).onNo();
                    }
                });

        return alertDialog.create();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString("name",input.getText().toString());
        super.onSaveInstanceState(outState);
    }
}
