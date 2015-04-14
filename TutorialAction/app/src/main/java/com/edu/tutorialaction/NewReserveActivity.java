package com.edu.tutorialaction;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.EditText;

import com.edu.tutorialaction.entity.Course;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Iterator;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by albertoguerreromartin on 13/04/15.
 */
public class NewReserveActivity extends ActionBarActivity implements SingleChoiceDialog.SingleChoiceDialogListener {

    @InjectView(R.id.select_course) EditText selectCourseEditText;
    @InjectView(R.id.select_teacher) EditText selectTeacherEditText;
    int selectedCourseID;
    int selectedTeacherID;

    private List<Course> courses;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_reserve);
        ButterKnife.inject(this);

        Gson gson = new Gson();
        Type listType = new TypeToken<List<Course>>(){}.getType();
        courses = gson.fromJson(getIntent().getStringExtra("courses"), listType);

        selectedCourseID = 0;
        selectCourseEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SingleChoiceDialog dialogFragment = new SingleChoiceDialog();
                CharSequence[] coursesNames = new CharSequence[courses.size()];
                int i=0;

                for (Course course : courses) {
                    coursesNames[i++] = course.getCourseName();
                }

                dialogFragment.setChoiceItems(coursesNames);
                dialogFragment.setSelectedItemID(selectedCourseID);
                dialogFragment.show(getFragmentManager(), "courses");
            }
        });


        getSupportActionBar().setElevation(2);
        getSupportActionBar().setTitle("Nueva reserva");
    }


    @Override
    public void onDialogPositiveClick(SingleChoiceDialog dialog) {
        selectedCourseID = dialog.getSelectedItemID();
        selectCourseEditText.setText(dialog.getSelectedItem());
    }

    @Override
    public void onDialogNegativeClick(SingleChoiceDialog dialog) {
    }
}
