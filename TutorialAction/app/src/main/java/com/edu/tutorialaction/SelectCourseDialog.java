package com.edu.tutorialaction;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

/**
 * Created by albertoguerreromartin on 13/04/15.
 */
public class SelectCourseDialog extends DialogFragment {

    public interface SelectCourseDialogListener {
        void onDialogPositiveClick(SelectCourseDialog dialog);
        void onDialogNegativeClick(SelectCourseDialog dialog);
    }

    private int selectedCourseID;
    private String selectedCourse;

    private NewReserveActivity newReserveActivity;

    public void setSelectedCourseID(int selectedCourseID) {
        this.selectedCourseID = selectedCourseID;
    }

    public int getSelectedCourseID() {
        return selectedCourseID;
    }

    public String getSelectedCourse() {
        return selectedCourse;
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            newReserveActivity = (NewReserveActivity) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement SelectCourseDialogListener");
        }
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setSingleChoiceItems(new CharSequence[]{
                "Administración de Bases de Datos", "Aplicación y Gestión de la Información"
        }, selectedCourseID, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                selectedCourseID = which;
                selectedCourse = which == 0 ? "Administración de Bases de Datos" : "Aplicación y Gestión de la Información";
            }
        });

        builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                newReserveActivity.onDialogPositiveClick(SelectCourseDialog.this);
            }
        });

        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                newReserveActivity.onDialogNegativeClick(SelectCourseDialog.this);
            }
        });


        return builder.create();
    }

}
