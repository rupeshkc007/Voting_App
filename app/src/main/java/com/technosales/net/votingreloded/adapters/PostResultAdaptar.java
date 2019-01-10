package com.technosales.net.votingreloded.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.technosales.net.votingreloded.R;
import com.technosales.net.votingreloded.helper.DatabaseHelper;
import com.technosales.net.votingreloded.pojoModels.CandidatesList;
import com.technosales.net.votingreloded.pojoModels.PostList;
import com.technosales.net.votingreloded.utils.BackgroundColor;
import com.technosales.net.votingreloded.utils.UtilStrings;

import java.util.ArrayList;
import java.util.List;

public class PostResultAdaptar extends RecyclerView.Adapter<PostResultAdaptar.MyViewHolder> {
    private List<PostList> postLists;
    private Context context;
    private DatabaseHelper dbHelper;
    private SharedPreferences preferences;

    public PostResultAdaptar(List<PostList> postLists, Context context) {
        this.postLists = postLists;
        this.context = context;
    }


    @Override
    public PostResultAdaptar.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.post_result, parent, false);

        return new PostResultAdaptar.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final PostResultAdaptar.MyViewHolder holder, final int position) {
        PostList postList = postLists.get(position);
        dbHelper = new DatabaseHelper(context);
        preferences = context.getSharedPreferences(UtilStrings.SHARED_PREFERENCES, 0);

        int numberOfColumns = preferences.getInt(UtilStrings.NO_OF_COLUMNS, 3);

        holder.postNameResult.setText(postList.postNepali);

        List<CandidatesList> candidatesPojos = new ArrayList<>();

        GridLayoutManager gridLayoutManager = new GridLayoutManager(context, numberOfColumns);
        holder.canResultList.setLayoutManager(gridLayoutManager);
        holder.canResultList.setHasFixedSize(true);
        candidatesPojos = dbHelper.resultProcess(postList.postId);

        holder.canResultList.setAdapter(new CanResultAdaptar(candidatesPojos, context));

        BackgroundColor.setBackroundColor(context, postList.postId, holder.canResultList);


    }

    @Override
    public int getItemCount() {
        return postLists.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {


        TextView postNameResult;
        RecyclerView canResultList;


        public MyViewHolder(View itemView) {
            super(itemView);


            postNameResult = itemView.findViewById(R.id.postNameResult);
            canResultList = itemView.findViewById(R.id.canResultList);


        }
    }


}
