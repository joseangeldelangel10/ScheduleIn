package com.example.schedulein_20.fragments;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.schedulein_20.CUeventActivity;
import com.example.schedulein_20.R;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;


public class CalendarDialogFragment extends DialogFragment {
    private Button button;
    private CalendarView calendarView;
    private Calendar calendar;


    public CalendarDialogFragment() {
        // Empty constructor is required for DialogFragment
        // Make sure not to add arguments to the constructor
        // Use `newInstance` instead as shown below
    }

    public static CalendarDialogFragment newInstance(String title) {
        CalendarDialogFragment frag = new CalendarDialogFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.calendar_dialog_fragment, container);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        /* ------------------------------------------------------------------------------------------------------
         THIS FRAGMENT IS CALLED BY THE ACTIVITY USED TO CREATE, UPDATE AND DELETE EVENTS (CUeventActivity)
         whenever user wants to modify the start or end date of an event
         ------------------------------------------------------------------------------------------------------*/

        button = view.findViewById(R.id.CalDialogFragmentBt);
        calendarView = view.findViewById(R.id.CalDialogFragmentCalView);

        calendar = Calendar.getInstance();

        // every time user taps a new day in the calendar view we update the calendar object we will
        // be returning
        calendarView.setOnDateChangeListener( new CalendarView.OnDateChangeListener() {
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
                calendar.set(Calendar.YEAR,year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            }
        });

        //when user clicks the "select new date" button we call the corresponding method
        //implemented in CUeventActivity
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (getArguments().get("title").equals(CUeventActivity.FLAG_EDIT_START_DATE)) {
                    EditCalendarDateListener listener = (EditCalendarDateListener) getActivity();
                    listener.onFinishStartDateEdit(calendar);
                }else if(getArguments().get("title").equals(CUeventActivity.FLAG_EDIT_END_DATE)) {
                    EditCalendarDateListener listener = (EditCalendarDateListener) getActivity();
                    listener.onFinishEndDateEdit(calendar);
                }
                // Close the dialog and return back to the parent activity
                dismiss();
            }
        });

    }

    // Defines the listener interface with a method passing back data result.
    public interface EditCalendarDateListener {
        void onFinishStartDateEdit(Calendar calendar);
        void onFinishEndDateEdit(Calendar calendar);
    }
}
