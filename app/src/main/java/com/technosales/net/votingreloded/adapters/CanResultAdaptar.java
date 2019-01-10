package com.technosales.net.votingreloded.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.technosales.net.votingreloded.R;
import com.technosales.net.votingreloded.pojoModels.CandidatesList;
import com.technosales.net.votingreloded.utils.BackgroundColor;
import com.technosales.net.votingreloded.utils.GeneralUtils;
import com.technosales.net.votingreloded.utils.UtilStrings;

import java.util.List;

public class CanResultAdaptar extends RecyclerView.Adapter<CanResultAdaptar.MyViewHolder> {
    private List<CandidatesList> candidatesPojos;
    private Context context;
    private int numberOfColumns;

    public CanResultAdaptar(List<CandidatesList> candidatesPojos, Context context) {
        this.candidatesPojos = candidatesPojos;
        this.context = context;
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.can_result_item, parent, false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        CandidatesList candidatesPojo = candidatesPojos.get(position);

        holder.resultsCount.setVisibility(View.INVISIBLE);
        holder.container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.resultsCount.setVisibility(View.VISIBLE);
            }
        });

        Typeface suryodayaFont = Typeface.createFromAsset(context.getAssets(), "fonts/Suryodaya.ttf");
        holder.resultsCount.setTypeface(suryodayaFont);

        numberOfColumns = context.getSharedPreferences(UtilStrings.SHARED_PREFERENCES, 0).getInt(UtilStrings.NO_OF_COLUMNS, 3);


        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        int width = displayMetrics.widthPixels / numberOfColumns;

        LinearLayout.LayoutParams lp =
                new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, (int) (width * .45));
        holder.imageContainer.setLayoutParams(lp);
        holder.canName.setTextSize((float) (28 - (numberOfColumns * 1.5)));

        holder.canName.setText(candidatesPojo.can_nep_name);
        holder.resultsCount.setText(String.valueOf(candidatesPojo.vote_count));
//        holder.resultsCount.setText(String.valueOf(888));
        GeneralUtils.canImagePath(candidatesPojo.can_id, holder.candidateImage);


    }

    @Override
    public int getItemCount() {
        return candidatesPojos.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {


        LinearLayout container;
        LinearLayout imageContainer;
        TextView resultsCount;
        ImageView candidateImage;
        TextView canName;


        public MyViewHolder(View itemView) {
            super(itemView);

            container = itemView.findViewById(R.id.container);
            imageContainer = itemView.findViewById(R.id.imageContainer);
            resultsCount = itemView.findViewById(R.id.resultsCount);
            candidateImage = itemView.findViewById(R.id.candidateImage);
            canName = itemView.findViewById(R.id.canName);


        }
    }


}
