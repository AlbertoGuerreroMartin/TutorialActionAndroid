package com.edu.tutorialaction;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.TextView;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by albertoguerreromartin on 13/04/15.
 */
public class NewReserveActivity extends ActionBarActivity implements SelectCourseDialog.SelectCourseDialogListener{

    @InjectView(R.id.select_course) TextView selectCourseTextView;
    int selectedCourseID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_reserve);
        ButterKnife.inject(this);

        selectedCourseID = 0;
        selectCourseTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SelectCourseDialog dialogFragment = new SelectCourseDialog();
                dialogFragment.setSelectedCourseID(selectedCourseID);
                dialogFragment.show(getFragmentManager(), "courses");
            }
        });


        getSupportActionBar().setElevation(2);
        getSupportActionBar().setTitle("Nueva reserva");
    }


    @Override
    public void onDialogPositiveClick(SelectCourseDialog dialog) {
        selectedCourseID = dialog.getSelectedCourseID();
        selectCourseTextView.setText(dialog.getSelectedCourse());
    }

    @Override
    public void onDialogNegativeClick(SelectCourseDialog dialog) {
        selectedCourseID = 0;
        selectCourseTextView.setText("Seleccionar asignatura");
    }
}
