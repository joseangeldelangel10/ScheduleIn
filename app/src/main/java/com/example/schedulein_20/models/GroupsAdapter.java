package com.example.schedulein_20.models;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.schedulein_20.CUeventActivity;
import com.example.schedulein_20.CUgroupsActivity;
import com.example.schedulein_20.R;

import org.parceler.Parcels;

import java.util.List;

public class GroupsAdapter extends RecyclerView.Adapter<GroupsAdapter.ViewHolder> {
    List<Group> userGroup;
    Context context;
    View view;

    public GroupsAdapter(Context context, List<Group> userGroup){
        this.context = context;
        this.userGroup = userGroup;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        view = LayoutInflater.from(context).inflate(R.layout.group_item , parent, false);
        return new GroupsAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Group group = userGroup.get(position);
        holder.bind(group, view);
    }

    @Override
    public int getItemCount() {
        return userGroup.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView groupName;
        TextView groupMembers;
        Button joinedEvent;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            groupName = itemView.findViewById(R.id.groupItemName);
            groupMembers = itemView.findViewById(R.id.groupItemMembers);
            joinedEvent = itemView.findViewById(R.id.groupItemJoinedEvent);

        }

        public void bind(Group group, View view) {
            groupName.setText(group.getTitle());
            Integer membersInGroup = group.getMembers().size();
            groupMembers.setText(membersInGroup.toString() + " members");

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, CUgroupsActivity.class);
                    intent.putExtra("Flag", "UpdateDelete");
                    intent.putExtra("Group", Parcels.wrap(group));
                    context.startActivity(intent);
                }
            });

            joinedEvent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, CUeventActivity.class);
                    intent.putExtra("Flag", "CreateJoinedEvent");
                    intent.putExtra("Group", Parcels.wrap(group));
                    context.startActivity(intent);
                }
            });


        }
    }

}
