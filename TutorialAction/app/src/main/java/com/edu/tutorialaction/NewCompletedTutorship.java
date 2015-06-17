package com.edu.tutorialaction;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.edu.tutorialaction.entity.Course;
import com.edu.tutorialaction.entity.TutorshipDay;
import com.edu.tutorialaction.entity.TutorshipType;
import com.edu.tutorialaction.entity.User;
import com.edu.tutorialaction.network.CompletedTutorshipModel;
import com.edu.tutorialaction.network.NetworkManager;
import com.edu.tutorialaction.network.NewReserveModel;
import com.edu.tutorialaction.network.RxLoaderActivity;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateChangedListener;
import com.welbits.izanrodrigo.emptyview.library.EmptyView;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;
import retrofit.RetrofitError;
import rx.Observer;

/**
 * Created by albertoguerreromartin on 24/05/15.
 */
public class NewCompletedTutorship extends AppCompatActivity {

    @InjectView(R.id.courses_spinner) Spinner coursesSpinner;
    @InjectView(R.id.students_spinner) Spinner studentsSpinner;
    @InjectView(R.id.reason_edit_text) EditText reasonEditText;
    @InjectView(R.id.type_spinner) Spinner typeSpinner;
    @InjectView(R.id.date_calendar_view) MaterialCalendarView dateCalendarView;
    @InjectView(R.id.hour_spinner) Spinner hourSpinner;
    @InjectView(R.id.duration_edit_text) EditText durationEditText;
    @InjectView(R.id.newCompletedTutorship_button) Button registerButton;
    @InjectView(R.id.newCompletedTutorship_emptyView) EmptyView emptyView;

    private List<Course> courses;
    private int teacherID;

    private int selectedCoursePosition;
    private int selectedStudentPosition;

    private int selectedStudentID;
    private int selectedCourseID;
    private int selectedTypeID;
    private String selectedDate;
    private String selectedHour;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_completed_tutorship);
        ButterKnife.inject(this);

        courses = Course.LIST_SERIALIZER.fromBundle(getIntent().getExtras(), "courses");
        teacherID = getIntent().getExtras().getInt("teacherID");

        selectedCoursePosition = 0;
        selectedStudentPosition = 0;
        if(courses != null) {
            selectedStudentID = courses.get(0).getUsers().get(0).getUserID();
            selectedCourseID = courses.get(0).getCourseID();
        } else {
            selectedStudentID = 0;
            selectedCourseID = 0;
        }
        selectedTypeID = 0;
        selectedDate = "";
        selectedHour = "";

        System.out.println("Current studentID: " + selectedStudentID);

        this.emptyView.retry("Reintentar", new Runnable() {
            @Override
            public void run() {
                configCourses();
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                RxLoaderActivity<Map<String, String>> loader = new RxLoaderActivity<Map<String, String>>() {
                    @Override
                    public void onNext(Map<String, String> stringStringMap) {

                    }
                };

                System.out.println("\nteacherID: " + teacherID + "\nstudentID: " + selectedStudentID + "\ncourseID: " + selectedCourseID + "\ntypeID: " + selectedTypeID + "\nreason: " + reasonEditText.getText().toString() + "\ndate: " + selectedDate + "\nhour: " + selectedHour + "\n");

                loader.addSubscription(CompletedTutorshipModel.INSTANCE.createCompletedTutorship(new Observer<Object>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        System.out.println("ERROR AL REGISTRAR TUTORIA COMPLETA");
                        System.out.println(e.getMessage());
                        e.printStackTrace();

                        // If user unauthorized, show login
                        int errorCode = ((RetrofitError) e).getResponse().getStatus();
                        if (errorCode == 401) {
                            NetworkManager.sessionExpiration(NewCompletedTutorship.this, null);
                        }
                    }

                    @Override
                    public void onNext(Object o) {
                        System.out.println("TUTORIA COMPLETADA");
                        Intent resultIntent = new Intent();
                        resultIntent.putExtra("completedTutorshipRegistered", true);
                        NewCompletedTutorship.this.setResult(RESULT_OK, resultIntent);
                        NewCompletedTutorship.this.finish();
                    }
                }, getApplicationContext(), teacherID, selectedStudentID, selectedCourseID, -1, 0, selectedDate, selectedHour, reasonEditText.getText().toString(), selectedTypeID, Integer.parseInt(durationEditText.getText().toString())));
            }
        });

        configCourses();

        getSupportActionBar().setElevation(2);
        getSupportActionBar().setTitle("Añadir tutoría completada");
    }


    private void configCourses() {

        ArrayList<String> coursesNames = new ArrayList<>(courses.size());
        for (Course course : courses) {
            coursesNames.add(course.getCourseName());
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.spinner_item, coursesNames);
        adapter.setDropDownViewResource(R.layout.spinner_item_dropdown);
        coursesSpinner.setAdapter(adapter);
        coursesSpinner.setSelection(selectedCoursePosition);
        coursesSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedCourseID = courses.get(position).getCourseID();
                selectedCoursePosition = position;
                configStudents(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void configStudents(final int coursePosition) {

        List<User> students = courses.get(coursePosition).getUsers();
        final ArrayList<String> studentsNames = new ArrayList<>(students.size());
        for (User student : students) {
            studentsNames.add(student.getFullname());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.spinner_item, studentsNames);
        adapter.setDropDownViewResource(R.layout.spinner_item_dropdown);
        studentsSpinner.setAdapter(adapter);
        studentsSpinner.setSelection(selectedStudentPosition);
        studentsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedStudentPosition = position;
                selectedStudentID = courses.get(coursePosition).getUsers().get(position).getUserID();
                System.out.println("New studentID: " + selectedStudentID);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        configType();
        configDate();
    }

    private void configType() {

        TutorshipType[] types = TutorshipType.values();
        ArrayList<String> typesNames = new ArrayList<>(types.length);
        for (TutorshipType type : types) {
            typesNames.add(type.getRawValue());
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.spinner_item, typesNames);
        adapter.setDropDownViewResource(R.layout.spinner_item_dropdown);
        typeSpinner.setAdapter(adapter);
        typeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedTypeID = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void configDate() {


        RxLoaderActivity<ArrayList<TutorshipDay>> loader = new RxLoaderActivity<ArrayList<TutorshipDay>>() {
            @Override
            public void onNext(ArrayList<TutorshipDay> tutorshipDays) {
            }
        };

        loader.addSubscription(NewReserveModel.INSTANCE.getTimetable(new Observer<Object>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
                emptyView.errorLoading();

                // If user unauthorized, show login
                int errorCode = ((RetrofitError) e).getResponse().getStatus();
                if(errorCode == 401) {
                    NetworkManager.sessionExpiration(NewCompletedTutorship.this, null);
                }
            }

            @Override
            public void onNext(Object o) {

                emptyView.successLoading();

                final ArrayList<TutorshipDay> days = (ArrayList<TutorshipDay>) o;

                Calendar nextYear = Calendar.getInstance();
                nextYear.add(Calendar.YEAR, 1);
                Date today = new Date();

                dateCalendarView.setMinimumDate(today);
                dateCalendarView.setMaximumDate(nextYear);
                dateCalendarView.setSelectedDate(today);
                dateCalendarView.enableAllDays(false);

                for (TutorshipDay day : days) {
                    DateFormat format = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
                    try {
                        Date date = format.parse(day.getDateString());
                        System.out.println(date.toString());
                        dateCalendarView.enableDay(new CalendarDay(date), true);
                    } catch (ParseException e) {
                        emptyView.error("Error al obtener las fechas").errorLoading();
                    }
                }

                dateCalendarView.setOnDateChangedListener(new OnDateChangedListener() {
                    @Override
                    public void onDateChanged(MaterialCalendarView materialCalendarView, CalendarDay calendarDay) {
                        selectedDate = calendarDay.getDay() + "/" + ((calendarDay.getMonth()+1) <= 9 ? "0" : "") + (calendarDay.getMonth()+1) + "/" + calendarDay.getYear();
                        configHour(days, calendarDay);
                    }
                });
            }
        }, getApplicationContext(), teacherID));
    }


    private void configHour(ArrayList<TutorshipDay> days, CalendarDay calendarDay) {
        if (calendarDay != null) {
            final ArrayList<String> hours = new ArrayList<>();

            for (TutorshipDay day : days) {
                if (day.getCalendarDay().equals(calendarDay)) {
                    hours.add(day.getHour());
                }
            }

            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.spinner_item, hours);
            adapter.setDropDownViewResource(R.layout.spinner_item_dropdown);
            hourSpinner.setAdapter(adapter);
            hourSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    selectedHour = hours.get(position);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        }
    }
}
