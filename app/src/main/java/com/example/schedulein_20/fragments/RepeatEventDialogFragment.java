package com.example.schedulein_20.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;

import com.example.schedulein_20.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RepeatEventDialogFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RepeatEventDialogFragment extends DialogFragment {
    Button finalButton;
    RadioButton repeatNone;
    RadioButton repeatDaily;
    RadioButton repeatWeekly;
    RadioButton repeatMonthly;
    String repeatResult;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public RepeatEventDialogFragment() {
        // Required empty public constructor
    }

    /*
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RepeatEventDialogFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RepeatEventDialogFragment newInstance(String currentRepeat) {
        RepeatEventDialogFragment fragment = new RepeatEventDialogFragment();
        Bundle args = new Bundle();
        fragment.repeatResult = currentRepeat;
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
        return inflater.inflate(R.layout.fragment_repeat_event_dialog, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        finalButton = view.findViewById(R.id.RepeatEventDialogFragmentFinalButton);
        repeatNone = view.findViewById(R.id.RepeatEventDialogFragmentNone);
        repeatDaily = view.findViewById(R.id.RepeatEventDialogFragmentDaily);
        repeatWeekly = view.findViewById(R.id.RepeatEventDialogFragmentWeekly);
        repeatMonthly = view.findViewById(R.id.RepeatEventDialogFragmentMonthly);

        if(repeatResult.equals("none")){
            repeatNone.setChecked(true);
            finalButton.setText("OK");
        }else if(repeatResult.equals("daily")){
            repeatDaily.setChecked(true);
        }else if(repeatResult.equals("weekly")){
            repeatWeekly.setChecked(true);
        }else if(repeatResult.equals("monthly")){
            repeatMonthly.setChecked(true);
        }

        repeatNone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                repeatResult = "none";
                finalButton.setText("OK");
            }
        });

        repeatDaily.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                repeatResult = "daily";
                finalButton.setText("Next");
            }
        });

        repeatWeekly.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                repeatResult = "weekly";
                finalButton.setText("Next");
            }
        });

        repeatMonthly.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                repeatResult = "monthly";
                finalButton.setText("Next");
            }
        });

        finalButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!repeatResult.equals("none")){
                    FragmentManager fm = getParentFragmentManager();

                    RepeatEventUntilDialogFragment repeatEventUntilDialogFragment = RepeatEventUntilDialogFragment.newInstance(repeatResult);
                    repeatEventUntilDialogFragment.show(fm, "fragment_edit_repeat_until");

                    // Close the dialog and return back to the parent activity
                    dismiss();
                }else{
                    RepeatEventUntilDialogFragment.EditRepeatListener listener = (RepeatEventUntilDialogFragment.EditRepeatListener) getActivity();
                    listener.onFinishRepeatEdit(repeatResult, null);
                    dismiss();
                }
            }
        });

    }


}
