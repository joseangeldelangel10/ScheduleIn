package com.example.schedulein_20.models;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.schedulein_20.R;
import com.parse.ParseUser;

import java.util.ArrayList;

public class TodosAdapter extends RecyclerView.Adapter<TodosAdapter.ViewHolder>{
    Context context;
    ArrayList<String> todos;
    ParseUser currentUser;

    public TodosAdapter(Context context, ArrayList<String> todos){
        this.context = context;
        this.todos = todos;
        currentUser = ParseUser.getCurrentUser();
    }

    @NonNull
    @Override
    public TodosAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.todos_item , parent, false);
        return new TodosAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TodosAdapter.ViewHolder holder, int position) {
        String todo = todos.get(position);
        holder.bind(todo, position);
    }

    @Override
    public int getItemCount() {
        return todos.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView todoTv;
        ImageButton checkbox;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            todoTv = itemView.findViewById(R.id.TodosItem_Text);
            checkbox = itemView.findViewById(R.id.TodosItem_checkBox);
        }

        public void bind(String todo, int position){
            todoTv.setText(todo);
            checkbox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    checkbox.setBackground(view.getResources().getDrawable(R.drawable.custom_border_filled));
                }
            });
        }

    }
}
