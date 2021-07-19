package com.example.schedulein_20.models;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.schedulein_20.R;
import com.example.schedulein_20.fragments.CalendarDialogFragment;
import com.example.schedulein_20.fragments.Relations;
import com.parse.ParseUser;

import java.util.Calendar;
import java.util.List;

import javax.security.auth.callback.Callback;

public class RelationRequestsAdapter extends RecyclerView.Adapter<RelationRequestsAdapter.ViewHolder> {
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
            userDetails.setText("username: " + relatingUser.getString("username"));

            /* ----------------------------------------------------------------------------------------
            *                           BINDING BUTTONS LOGIC
            * ---------------------------------------------------------------------------------------- */
            accept.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // we accept the request (backend logic) and discard RV item
                    Relations.AcceptRequest(context, currentUser, relatingUser);
                    discardItem(position);
                    // we call the interface method implemented in Relations to add the new relation to the relations
                    // rv
                    RelationRequestsAdapter.OnItemChangeListener listener = (OnItemChangeListener) fragment;
                    listener.onRequestItemAccepted(relatingUser);
                }
            });

            decline.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // we decline the request (backend logic) and discard RV item
                    Relations.DeclineRequest(context, currentUser, relatingUser);
                    discardItem(position);
                }
            });

            /* ---------------------------------------------------------------------------------------- */
        }

    }
}
