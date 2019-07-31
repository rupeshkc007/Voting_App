package com.technosales.net.votingreloded.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.technosales.net.votingreloded.R;
import com.technosales.net.votingreloded.pojoModels.SummaryList;
import com.technosales.net.votingreloded.utils.BackgroundColor;
import com.technosales.net.votingreloded.utils.GeneralUtils;

import java.util.List;

public class SummaryAdaptar extends RecyclerView.Adapter<SummaryAdaptar.MyViewHolder> {
    private List<SummaryList> summaryLists;
    private Context context;
    private int numberOfColumns;
    private OnItemClick onItemClick;
    public interface OnItemClick{
        void onItemClick(SummaryList summaryList);
    }

    public SummaryAdaptar(List<SummaryList> summaryLists, Context context,int numberOfColumns,OnItemClick onItemClick) {
        this.summaryLists = summaryLists;
        this.context = context;
        this.numberOfColumns = numberOfColumns;
        this.onItemClick = onItemClick;
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.summary_view, parent, false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final SummaryList summaryList = summaryLists.get(position);
        holder.canName.setText(summaryList.getCanName() + ":" + summaryList.getPostNepali());
//            holder.canName.setText(summaryList.getCanName() + "(" + summaryList.getPostNepali() + ")");
        holder.swastikImage.setVisibility(View.VISIBLE);
        GeneralUtils.canImagePath(summaryList.canId, holder.candidateImage);
        BackgroundColor.setBackroundColor(context, summaryList.postId, holder.container);


        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        int width = displayMetrics.widthPixels / numberOfColumns;

        LinearLayout.LayoutParams lp =
                new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, (int) (width * .45));
        holder.imageContainer.setLayoutParams(lp);
        holder.canName.setTextSize((float) (28 - (numberOfColumns * 1.5)));
        Log.i("adapterList", "" + summaryLists.size());
        holder.container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                onItemClick.onItemClick(summaryList);

            }
        });


    }

    @Override
    public int getItemCount() {
        return summaryLists.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        LinearLayout container;
        LinearLayout imageContainer;
        ImageView swastikImage;
        ImageView candidateImage;
        TextView canName;


        public MyViewHolder(View itemView) {
            super(itemView);


            container = itemView.findViewById(R.id.container);
            imageContainer = itemView.findViewById(R.id.imageContainer);
            swastikImage = itemView.findViewById(R.id.swastikImage);
            candidateImage = itemView.findViewById(R.id.candidateImage);
            canName = itemView.findViewById(R.id.canName);


        }
    }


}

