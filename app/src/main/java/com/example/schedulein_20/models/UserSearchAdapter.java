package com.example.schedulein_20.models;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.Adapter;

import com.bumptech.glide.Glide;
import com.example.schedulein_20.R;
import com.example.schedulein_20.fragments.ProfileFragment;
import com.example.schedulein_20.fragments.UserProfile;
import com.example.schedulein_20.fragments.UserSearchFragment;
import com.parse.ParseUser;

import java.util.List;

public class UserSearchAdapter extends Adapter<UserSearchAdapter.ViewHolder> {
    Context context;
    List<ParseUser> results;

    //pass in the context and list of tweets
    public UserSearchAdapter(Context context, List<ParseUser> results){
        this.context = context;
        this.results = results;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.user_search_item , parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ParseUser user = results.get(position);
        holder.bind(user);
    }

    @Override
    public int getItemCount() {
        return results.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        ImageView ivProfilePic;
        TextView tvUserName;
        TextView tvExtraInfo;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            ivProfilePic = itemView.findViewById(R.id.UserSearchItemProfilePic);
            tvUserName = itemView.findViewById(R.id.UserSearchItemUserName);
            tvExtraInfo = itemView.findViewById(R.id.UserSearchItemExtraInfo);
        }

        public void bind(ParseUser user) {
            tvUserName.setText( user.getString("name") + " " + user.getString("surname") );
            String extraInfo = "" +
            "username: " + user.getString("username") + "\n" +
            "email: " + user.getString("email") + "\n";
            tvExtraInfo.setText(extraInfo);
            Glide.with(context)
                    .load(user.getParseFile("profilePic").getUrl())
                    .placeholder(R.drawable.profile_picture_placeholder)
                    .into(ivProfilePic);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Fragment fragment = ProfileFragment.newInstance(user, "some str");

                    ((FragmentActivity)context).getSupportFragmentManager().beginTransaction()
                            .replace(R.id.host_frame, fragment)
                            .commit();
                }
            });

        }

    }
}