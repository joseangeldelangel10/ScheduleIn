package com.example.schedulein_20.models;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.schedulein_20.R;
import com.example.schedulein_20.fragments.RelationsFragment;
import com.example.schedulein_20.parseDatabaseComms.RelationRelatedQueries;
import com.parse.ParseUser;

import java.util.List;

public class UserRelationsAdapter extends RecyclerView.Adapter<UserRelationsAdapter.ViewHolder> {
    Context context;
    List<ParseUser> relations;
    ParseUser currentUser;

    //pass in the context and list of tweets
    public UserRelationsAdapter(Context context, List<ParseUser> relations){
        this.context = context;
        this.relations = relations;
        currentUser = ParseUser.getCurrentUser();
    }

    @NonNull
    @Override
    public UserRelationsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.relation_item , parent, false);
        return new UserRelationsAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserRelationsAdapter.ViewHolder holder, int position) {
        ParseUser user = relations.get(position);
        holder.bind(user, position);
    }

    @Override
    public int getItemCount() {
        return relations.size();
    }

    private void discardItem(int position) {
        relations.remove(position);
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        ImageView ivProfilePic;
        TextView tvUserName;
        Button butt1; // unrelate button
        Button butt2; // relation type indicator

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            ivProfilePic = itemView.findViewById(R.id.RelationsProfilePic);
            tvUserName = itemView.findViewById(R.id.RelationsUserName);
            butt1 = itemView.findViewById(R.id.RelationsButt1);
            butt2 = itemView.findViewById(R.id.RelationsButt2);
        }

        public void bind(ParseUser user, int position) {
            /* ----------------------------------------------------------------------------------------
             *                           BINDING USER INFO TO RV ITEM
             * ---------------------------------------------------------------------------------------- */
            tvUserName.setText( user.getString("name") + " " + user.getString("surname") );
            Glide.with(context)
                    .load(user.getParseFile("profilePic").getUrl())
                    .placeholder(R.drawable.profile_picture_placeholder)
                    .into(ivProfilePic);


            /* ----------------------------------------------------------------------------------------
             *                           BINDING UNRELATE LOGIC
             * ---------------------------------------------------------------------------------------- */
            butt1.setText("Unrelate");
            butt1.setBackground(  new ColorDrawable(itemView.getResources().getColor(R.color.emphasis1))    );

            butt1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    RelationRelatedQueries.unrelate(context, currentUser, user, null, null);
                    discardItem(position);
                }
            });

            /* ----------------------------------------------------------------------------------------
             *                       DEFINING RELATION TYPE INDICATOR BEHAVIOUR
             * ---------------------------------------------------------------------------------------- */
            butt2.setClickable(false);
            if ( RelationRelatedQueries.getUsersRelation(currentUser, user) == 3){
                butt2.setText("Related");
                butt2.setBackground(  new ColorDrawable(itemView.getResources().getColor(R.color.emphasis2))    );
            }else if ( RelationRelatedQueries.getUsersRelation(currentUser, user) == 2){
                butt2.setText("Request sent");
                butt2.setBackground(  new ColorDrawable(itemView.getResources().getColor(R.color.gray))    );
            }
            /* ---------------------------------------------------------------------------------------- */

        }

    }
}
