package com.example.schedulein_20.models;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.schedulein_20.R;
import com.parse.ParseUser;

import java.util.List;

public class GroupMembersSearchAdapter extends RecyclerView.Adapter<GroupMembersSearchAdapter.ViewHolder> {
    Context context;
    List<ParseUser> searchResults;
    List<ParseUser> selectedUsers;

    public GroupMembersSearchAdapter(Context context, List<ParseUser> searchResults, List<ParseUser> selectedUsers){
        this.context = context;
        this.searchResults = searchResults;
        this.selectedUsers = selectedUsers;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.relate_request_item , parent, false);
        return new GroupMembersSearchAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ParseUser user = searchResults.get(position);
        holder.bind(user);

    }

    @Override
    public int getItemCount() {
        return searchResults.size();
    }

    public interface onUserSelectedListener{
        void userSelected(ParseUser user);
        void userUnselected(ParseUser user);
    }


    public class ViewHolder extends RecyclerView.ViewHolder{
        ImageView userProfilePicture;
        TextView userName;
        TextView userDetails;
        ImageButton butt1;
        ImageButton butt2;
        View view;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            userProfilePicture = itemView.findViewById(R.id.RequestItemPicture);
            userName = itemView.findViewById(R.id.RequestItemName);
            userDetails = itemView.findViewById(R.id.RequestItemDetails);
            butt1 = itemView.findViewById(R.id.RequestItemDecline);
            butt2 = itemView.findViewById(R.id.RequestItemAccept);
            view = itemView;
        }

        public void bind(ParseUser user) {
            Glide.with(context)
                    .load(user.getParseFile("profilePic").getUrl())
                    .placeholder(R.drawable.profile_picture_placeholder)
                    .into(userProfilePicture);

            userName.setText(user.getString("name") + " " + user.getString("surname"));
            userDetails.setText("username: " + user.getString("username"));

            butt1.setBackground(new ColorDrawable(context.getResources().getColor(R.color.emphasis1)));
            butt1.setEnabled(false);
            butt2.setEnabled(true);
            butt1.setVisibility(View.INVISIBLE);

            if (selectedUsers.contains(user)){
                view.setBackground(new ColorDrawable(context.getResources().getColor(R.color.secondary)));
                butt2.setBackground(new ColorDrawable(context.getResources().getColor(R.color.gray)));
                Glide.with(context).load(R.drawable.accept_icon).into(butt2);

                butt1.setVisibility(View.VISIBLE);
                butt1.setEnabled(true);
                butt2.setEnabled(false);
            }else {
                view.setBackground(new ColorDrawable(context.getResources().getColor(R.color.white)));
                butt2.setBackground(new ColorDrawable(context.getResources().getColor(R.color.emphasis2)));
                Glide.with(context).load(R.drawable.plus_icon).into(butt2);
            }

            butt2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    view.setBackground(new ColorDrawable(context.getResources().getColor(R.color.secondary)));
                    butt2.setBackground(new ColorDrawable(context.getResources().getColor(R.color.gray)));
                    Glide.with(context).load(R.drawable.accept_icon).into(butt2);

                    butt1.setVisibility(View.VISIBLE);
                    butt1.setEnabled(true);
                    butt2.setEnabled(false);

                    GroupMembersSearchAdapter.onUserSelectedListener listener = (GroupMembersSearchAdapter.onUserSelectedListener) context;
                    listener.userSelected(user);
                }
            });

            butt1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(context, "unselecting: " + user.getUsername(), Toast.LENGTH_SHORT).show();

                    GroupMembersSearchAdapter.onUserSelectedListener listener = (GroupMembersSearchAdapter.onUserSelectedListener) context;
                    listener.userUnselected(user);
                }
            });
        }
    }
}
