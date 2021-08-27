package com.example.schedulein_20.fragments;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;

import com.example.schedulein_20.CUeventActivity;
import com.example.schedulein_20.R;

import java.util.Calendar;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RepeatEventUntilDialogFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RepeatEventUntilDialogFragment extends DialogFragment {
    CalendarView calendarView;
    Button button;
    Calendar calendar;
    String repeatResult;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public RepeatEventUntilDialogFragment() {
        // Required empty public constructor
    }

    /*
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RepeatEventUntilDialogFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RepeatEventUntilDialogFragment newInstance(String repeat) {
        RepeatEventUntilDialogFragment fragment = new RepeatEventUntilDialogFragment();
        Bundle args = new Bundle();
        fragment.repeatResult = repeat;
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_repeat_event_until_dialog, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        /* ------------------------------------------------------------------------------------------------------
         THIS FRAGMENT IS CALLED BY THE ACTIVITY USED TO CREATE, UPDATE AND DELETE EVENTS (CUeventActivity)
         whenever user wants to modify the start or end date of an event
         ------------------------------------------------------------------------------------------------------*/

        calendarView = view.findViewById(R.id.REUDialogFragmentCalView);
        button = view.findViewById(R.id.REUDialogFragmentBt);

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
                RepeatEventUntilDialogFragment.EditRepeatListener listener = (EditRepeatListener) getActivity();
                listener.onFinishRepeatEdit(repeatResult, calendar);
                // Close the dialog and return back to the parent activity
                dismiss();
            }
        });

    }

    // Defines the listener interface with a method passing back data result.
    public interface EditRepeatListener {
        void onFinishRepeatEdit(String repeatResult, Calendar calendar);
    }

}
