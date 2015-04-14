package com.edu.tutorialaction;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

import com.edu.tutorialaction.entity.Course;

import java.util.List;

/**
 * Created by albertoguerreromartin on 13/04/15.
 */
public class SingleChoiceDialog extends DialogFragment {


    public interface SingleChoiceDialogListener {
        void onDialogPositiveClick(SingleChoiceDialog dialog);
        void onDialogNegativeClick(SingleChoiceDialog dialog);
    }

    private CharSequence[] items;
    private int selectedItemID;
    private String selectedItem;

    private SingleChoiceDialogListener parentActivity;

    public void setChoiceItems(CharSequence[] items) {
        this.items = items;
    }

    public void setSelectedItemID(int selectedItemID) {
        this.selectedItemID = selectedItemID;
    }

    public int getSelectedItemID() {
        return selectedItemID;
    }

    public String getSelectedItem() {
        return selectedItem;
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            parentActivity = (SingleChoiceDialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement SelectCourseDialogListener");
        }
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        //--- Default values ---
        selectedItemID = 0;
        selectedItem = (String) items[0];
        //----------------------

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setSingleChoiceItems(items, selectedItemID, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                selectedItemID = which;
                selectedItem = (String) items[selectedItemID];
            }
        });

        builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                parentActivity.onDialogPositiveClick(SingleChoiceDialog.this);
            }
        });

        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                parentActivity.onDialogNegativeClick(SingleChoiceDialog.this);
            }
        });


        return builder.create();
    }

}
