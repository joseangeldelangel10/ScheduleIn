package com.example.schedulein_20.models;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.schedulein_20.R;

import java.util.List;

public class GroupsAdapter extends RecyclerView.Adapter<GroupsAdapter.ViewHolder> {
    List<Group> userGroup;
    Context context;

    public GroupsAdapter(Context context, List<Group> userGroup){
        this.context = context;
        this.userGroup = userGroup;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.group_item , parent, false);
        return new GroupsAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Group group = userGroup.get(position);
        holder.bind(group);
    }

    @Override
    public int getItemCount() {
        return userGroup.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView groupName;
        TextView groupMembers;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            groupName = itemView.findViewById(R.id.groupItemName);
            groupMembers = itemView.findViewById(R.id.groupItemMembers);

        }

        public void bind(Group group) {
            groupName.setText(group.getTitle());
            Integer membersInGroup = group.getMembers().size();
            groupMembers.setText(membersInGroup.toString() + " members");
        }
    }

}
