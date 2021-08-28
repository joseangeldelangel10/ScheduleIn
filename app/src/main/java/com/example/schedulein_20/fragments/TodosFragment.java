package com.example.schedulein_20.fragments;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.schedulein_20.R;
import com.example.schedulein_20.models.GroupsAdapter;
import com.example.schedulein_20.models.TodoItems;
import com.example.schedulein_20.models.TodosAdapter;
import com.example.schedulein_20.parseDatabaseComms.TodoQueries;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

import nl.dionsegijn.konfetti.KonfettiView;
import nl.dionsegijn.konfetti.models.Shape;
import nl.dionsegijn.konfetti.models.Size;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TodosFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TodosFragment extends Fragment implements TextDialogFragment.OnTextEditsListener, TodosAdapter.OnItemCompletedListener {
    Context context;
    ArrayList<TodoItems> todoItemsArrayList;
    public RecyclerView todosRv;
    FloatingActionButton addItemFAB;
    TodosAdapter adapter;
    ParseUser currentUser;
    KonfettiView confetti;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public TodosFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment TodosFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static TodosFragment newInstance(String param1, String param2) {
        TodosFragment fragment = new TodosFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
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
        return inflater.inflate(R.layout.fragment_todos, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        context = getContext();
        todoItemsArrayList = new ArrayList<>();
        currentUser = ParseUser.getCurrentUser();

        /* ---------------- Layout referencing ----------------- */
        todosRv = view.findViewById(R.id.TodoFragment_Rv);
        addItemFAB = view.findViewById(R.id.TodoFragment_FloatingButt);
        confetti = view.findViewById(R.id.TodoFragment_confettiView);

        /* ---------------- Setting up Rv ---------------------- */

        adapter = new TodosAdapter(context, todoItemsArrayList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        todosRv.setLayoutManager(linearLayoutManager); // we bind a layout manager to RV
        todosRv.setAdapter(adapter);

        /* ---------------------------------------------------------------- */

        TodoQueries.getTodoItems(context, currentUser, new FindCallback<TodoItems>() {
            @Override
            public void done(List<TodoItems> objects, ParseException e) {
                if(e == null){
                    todoItemsArrayList.addAll(objects);
                    adapter.notifyDataSetChanged();
                }else {
                    Toast.makeText(context, "sorry we couldn't load your events :(", Toast.LENGTH_SHORT).show();
                }
            }
        });

        addItemFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showEditTextDialog();
            }
        });

    }

    private void showEditTextDialog() {
        FragmentManager fm = ((FragmentActivity) context).getSupportFragmentManager();
        TextDialogFragment textDialogFragment = new TextDialogFragment();
        textDialogFragment.show(fm, "fragment_edit_text");
    }

    @Override
    public void onFinishTextEdit(String text) {
        if(text != null && !text.equals("")) {
            TodoQueries.saveTodoItem(context, currentUser, text);
            TodoItems newItem = new TodoItems();
            newItem.setUser(currentUser);
            newItem.setDescription(text);
            todoItemsArrayList.add(newItem);
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onItemCompleted(int position) {
        confetti.build()
                .addColors(Color.YELLOW, Color.GREEN, Color.MAGENTA)
                .setDirection(0.0, 359.0)
                .setSpeed(1f, 5f)
                .setFadeOutEnabled(true)
                .setTimeToLive(500L)
                .addShapes(Shape.Square.INSTANCE, Shape.Circle.INSTANCE)
                .addSizes(new Size(12, 5f))
                .setPosition(-50f, confetti.getWidth() + 50f, -50f, -50f)
                .streamFor(300, 500L);
        TodoQueries.removeTodoItem(context, todoItemsArrayList.get(position)); // remove item from db
        todoItemsArrayList.remove(position); // remove item from list
        adapter.notifyItemRemoved(position);
    }
}