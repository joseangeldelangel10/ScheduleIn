package com.example.schedulein_20.models;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.schedulein_20.CUeventActivity;
import com.example.schedulein_20.CUgroupsActivity;
import com.example.schedulein_20.DrawerLayoutActivity;
import com.example.schedulein_20.R;
import com.example.schedulein_20.fragments.GroupsFragment;

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
        RelativeLayout rlHolder;
        TextView groupName;
        TextView groupMembers;
        Button joinedEvent;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            rlHolder = itemView.findViewById(R.id.groupItemRelativeLayout);
            groupName = itemView.findViewById(R.id.groupItemName);
            groupMembers = itemView.findViewById(R.id.groupItemMembers);
            joinedEvent = itemView.findViewById(R.id.groupItemJoinedEvent);

        }

        public void bind(Group group, View view) {

            if(group.getColor() != 0) {
                rlHolder.setBackground(new ColorDrawable(group.getColor()));
            }
            groupName.setText(group.getTitle());
            Integer membersInGroup = group.getMembers().size();
            groupMembers.setText(membersInGroup.toString() + " members");

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, CUgroupsActivity.class);
                    intent.putExtra("Flag", "UpdateDelete");
                    intent.putExtra("Group", Parcels.wrap(group));
                    Fragment currentFragment = FragmentHelpers.getVisibleFragment(((FragmentActivity)context).getSupportFragmentManager());
                    //context.startActivity(intent);
                    currentFragment.startActivityForResult(intent, GroupsFragment.UPDATE_GROUP_REQUEST_CODE);
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
