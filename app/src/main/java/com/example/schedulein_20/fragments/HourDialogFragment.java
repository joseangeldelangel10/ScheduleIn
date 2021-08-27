package com.example.schedulein_20.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.schedulein_20.CUeventActivity;
import com.example.schedulein_20.R;

import java.util.Calendar;

public class HourDialogFragment extends DialogFragment {
    private final String TAG = "HourDialogFragment";
    Calendar calendar;
    EditText etHour;
    EditText etMin;
    Button button;
    TextView alert;

    public HourDialogFragment() {
        // Empty constructor is required for DialogFragment
        // Make sure not to add arguments to the constructor
        // Use `newInstance` instead as shown below
    }

    public static HourDialogFragment newInstance(String title) {
        HourDialogFragment frag = new HourDialogFragment();
        Bundle args = new Bundle();
        args.putString("Flag", title);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.time_dialog_fragment, container);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        /* ------------------------------------------------------------------------------------------------------
         THIS FRAGMENT IS CALLED BY THE ACTIVITY USED TO CREATE, UPDATE AND DELETE EVENTS (CUeventActivity)
         whenever user wants to modify the start or end time of an event
         ------------------------------------------------------------------------------------------------------*/

        super.onViewCreated(view, savedInstanceState);

        etHour = view.findViewById(R.id.timeDialogEditHourEt);
        etMin = view.findViewById(R.id.timeDialogEditMinuteEt);
        button = view.findViewById(R.id.timeDialogButt);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if( timeIsValid() ){
                    String strHour = etHour.getText().toString();
                    String strMin = etMin.getText().toString();
                    Integer hour = Integer.valueOf(strHour);
                    Integer min = Integer.valueOf(strMin);

                    calendar = Calendar.getInstance();
                    calendar.set(Calendar.HOUR_OF_DAY,hour);
                    calendar.set(Calendar.MINUTE, min);
                    calendar.set(Calendar.SECOND, 0);

                    if( getArguments().get("Flag").equals(CUeventActivity.FLAG_EDIT_START_TIME) ) {
                        HourDialogFragment.EditTimeListener listener = (HourDialogFragment.EditTimeListener) getActivity();
                        listener.onFinishStartTimeEdit(calendar);
                    }else if( getArguments().get("Flag").equals(CUeventActivity.FLAG_EDIT_END_TIME) ){
                        HourDialogFragment.EditTimeListener listener = (HourDialogFragment.EditTimeListener) getActivity();
                        listener.onFinishEndTimeEdit(calendar);
                    }
                    dismiss();
                }
                else {
                    Toast.makeText(getContext(), "Invalid Time!", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

    private boolean timeIsValid() {
        String strHour = etHour.getText().toString();
        String strMin = etMin.getText().toString();
        Integer hour;
        Integer min;
        try {
            hour = Integer.valueOf(strHour);
            min = Integer.valueOf(strMin);
        }catch (Exception e){
            Log.e(TAG, "Invalid Time" + e);
            return false;
        }

        if (hour < 0  || hour > 23 || min < 0 || min > 60){
            Log.e(TAG, "invalid time");
            return false;
        }
        return true;
    }

    // 1. Defines the listener interface with a method passing back data result.
    public interface EditTimeListener {
        void onFinishStartTimeEdit(Calendar calendar);
        void onFinishEndTimeEdit(Calendar calendar);
    }
}
