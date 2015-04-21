package com.edu.tutorialaction;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.edu.tutorialaction.entity.Course;
import com.edu.tutorialaction.entity.TutorshipDay;
import com.edu.tutorialaction.entity.TutorshipType;
import com.edu.tutorialaction.entity.User;
import com.edu.tutorialaction.network.NewReserveModel;
import com.edu.tutorialaction.network.RxLoaderActivity;
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
import rx.Observer;

/**
 * Created by albertoguerreromartin on 13/04/15.
 */
public class NewReserveActivity extends ActionBarActivity {

    @InjectView(R.id.courses_spinner) Spinner coursesSpinner;
    @InjectView(R.id.teachers_spinner) Spinner teachersSpinner;
    @InjectView(R.id.reason_edit_text) EditText reasonEditText;
    @InjectView(R.id.type_spinner) Spinner typeSpinner;
    @InjectView(R.id.date_calendar_view) MaterialCalendarView dateCalendarView;
    @InjectView(R.id.hour_spinner) Spinner hourSpinner;
    @InjectView(R.id.newReserve_button) Button sendButton;


    @InjectView(R.id.newReserve_emptyView) EmptyView emptyView;

    private List<Course> courses;
    private int selectedCoursePosition;
    private int selectedTeacherPosition;

    private int selectedTeacherID;
    private int selectedCourseID;
    private int selectedTypeID;
    private String selectedDate;
    private String selectedHour;


    /*
    TODO
    Para la memoria:
        NO SE PUDO USAR TIMES SQUARE POR FALLO DEL OS: https://code.google.com/p/android/issues/detail?id=75940
        Explicar en detalle el fork de la libreria del calendario
     */


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_reserve);
        ButterKnife.inject(this);

        courses = Course.LIST_SERIALIZER.fromBundle(getIntent().getExtras(), "courses");

        selectedCoursePosition = 0;
        selectedTeacherPosition = 0;
        if(courses != null) {
            selectedTeacherID = courses.get(0).getTeachers().get(0).getUserID();
            selectedCourseID = courses.get(0).getCourseID();
        } else {
            selectedTeacherID = 0;
            selectedCourseID = 0;
        }
        selectedTypeID = 0;
        selectedDate = "";
        selectedHour = "";

        this.emptyView.retry("Reintentar", new Runnable() {
            @Override
            public void run() {
                configCourses();
            }
        });

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                RxLoaderActivity<Map<String, String>> loader = new RxLoaderActivity<Map<String, String>>() {
                    @Override
                    public void onNext(Map<String, String> stringStringMap) {

                    }
                };

                System.out.println("\nteacherID: " + selectedTeacherID + "\ncourseID: " + selectedCourseID + "\ntypeID: " + selectedTypeID + "\nreason: " + reasonEditText.getText().toString() + "\ndate: " + selectedDate + "\nhour: " + selectedHour + "\n");

                loader.addSubscription(NewReserveModel.INSTANCE.createReserve(new Observer<Object>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        System.out.println("ERROR AL RESERVAR TUTORIA");
                        System.out.println(e.getLocalizedMessage());
                        e.printStackTrace();
                    }

                    @Override
                    public void onNext(Object o) {
                        System.out.println("TUTORIA RESERVADA");
                    }
                }, getApplicationContext(), selectedTeacherID, selectedCourseID, selectedTypeID, reasonEditText.getText().toString(), selectedDate, selectedHour));
            }
        });

        configCourses();

        getSupportActionBar().setElevation(2);
        getSupportActionBar().setTitle("Nueva reserva");
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
                configTeachers(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void configTeachers(final int coursePosition) {

        List<User> teachers = courses.get(coursePosition).getTeachers();
        final ArrayList<String> teachersNames = new ArrayList<>(teachers.size());
        for (User teacher : teachers) {
            teachersNames.add(teacher.getFullname());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.spinner_item, teachersNames);
        adapter.setDropDownViewResource(R.layout.spinner_item_dropdown);
        teachersSpinner.setAdapter(adapter);
        teachersSpinner.setSelection(selectedTeacherPosition);
        teachersSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                configType();
                selectedTeacherPosition = position;
                selectedTeacherID = courses.get(coursePosition).getTeachers().get(position).getUserID();
                configDate();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
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
                    // TODO:
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
        }, getApplicationContext(), selectedTeacherID));
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
