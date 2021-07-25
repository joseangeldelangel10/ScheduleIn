package com.example.schedulein_20.models;

import android.content.Context;
import android.os.Build;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.schedulein_20.R;
import com.example.schedulein_20.fragments.RelationsFragment;
import com.example.schedulein_20.parseDatabaseComms.RelationRelatedQueries;
import com.parse.LogInCallback;
import com.parse.ParseUser;

import java.util.List;

import okio.Timeout;

public class RelationRequestsAdapter extends RecyclerView.Adapter<RelationRequestsAdapter.ViewHolder> {
    private final String TAG = "RelationRequestAdapter";
    Context context;
    List<ParseUser> requests;
    ParseUser currentUser;
    Fragment fragment;

    //pass in the context and list of tweets
    public RelationRequestsAdapter(Context context, List<ParseUser> requests, Fragment fragment){
        this.context = context;
        this.requests = requests;
        this.fragment = fragment;
        currentUser = ParseUser.getCurrentUser();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.relate_request_item , parent, false);
        return new RelationRequestsAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RelationRequestsAdapter.ViewHolder holder, int position) {
        ParseUser relatingUser = requests.get(position);
        holder.bind(relatingUser, position);
    }

    @Override
    public int getItemCount() {
        return requests.size();
    }

    private void discardItem(int position) {
        requests.remove(position);
        notifyDataSetChanged();
    }

    public interface OnItemChangeListener {
        void onRequestItemAccepted(ParseUser user);
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        ImageView profilePic;
        TextView userName;
        TextView userDetails;
        ImageButton accept;
        ImageButton decline;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            profilePic = itemView.findViewById(R.id.RequestItemPicture);
            userName = itemView.findViewById(R.id.RequestItemName);
            userDetails = itemView.findViewById(R.id.RequestItemDetails);
            accept = itemView.findViewById(R.id.RequestItemAccept);
            decline = itemView.findViewById(R.id.RequestItemDecline);
        }

        private void bind(ParseUser relatingUser, int position){

            /* ----------------------------------------------------------------------------------------
             *                           BINDING USER INFO TO RV ITEM
             * ---------------------------------------------------------------------------------------- */
            Glide.with(context)
                    .load(relatingUser.getParseFile("profilePic").getUrl())
                    .into(profilePic);

            userName.setText(relatingUser.getString("name") + " " + relatingUser.getString("surname"));
            userDetails.setText(context.getString(R.string.username) + ": " + relatingUser.getString("username"));

            /* ----------------------------------------------------------------------------------------
            *                           BINDING BUTTONS LOGIC
            * ---------------------------------------------------------------------------------------- */
            accept.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // we accept the request (backend logic) and discard RV item
                    RelationRelatedQueries.AcceptRequest(context, currentUser, relatingUser);
                    // we call the interface method implemented in Relations to add the new relation to the relations
                    // rv
                    RelationRequestsAdapter.OnItemChangeListener listener = (OnItemChangeListener) fragment;
                    listener.onRequestItemAccepted(relatingUser);

                    discardItem(position);
                }
            });

            decline.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // we decline the request (backend logic) and discard RV item
                    RelationRelatedQueries.DeclineRequest(context, currentUser, relatingUser, null);
                    discardItem(position);
                }
            });

            itemView.setOnTouchListener(new OnSwipeTouchListener(context){

                @Override
                public void onSwipeRight() {
                    RelationRelatedQueries.DeclineRequest(context, currentUser, relatingUser, null);
                    discardItem(position);
                }

            });

            /* ---------------------------------------------------------------------------------------- */
        }

    }
}
