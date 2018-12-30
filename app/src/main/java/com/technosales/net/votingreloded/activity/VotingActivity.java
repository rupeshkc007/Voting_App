package com.technosales.net.votingreloded.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.InputDevice;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.technosales.net.votingreloded.R;
import com.technosales.net.votingreloded.helper.DatabaseHelper;
import com.technosales.net.votingreloded.pojoModels.CandidatesList;
import com.technosales.net.votingreloded.pojoModels.SummaryList;
import com.technosales.net.votingreloded.utils.GeneralUtils;
import com.technosales.net.votingreloded.utils.InsertingDataFromTxt;
import com.technosales.net.votingreloded.utils.UtilStrings;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.technosales.net.votingreloded.helper.DatabaseHelper.POST_NAME_NEPALI;
import static com.technosales.net.votingreloded.helper.DatabaseHelper.POST_TABLE;

public class VotingActivity extends AppCompatActivity {

    private DatabaseHelper databaseHelper;
    private LinearLayout canContainer;
    private SharedPreferences preferences;
    private int numberOfColumns;
    private int postId = 0;

    public ImageView voting_cover_image;
    private List<CandidatesList> candidatesLists = new ArrayList<>();
    private int candidatesData;
    private int postsData;
    private HashMap<String, String> vote = new HashMap<>();

    private Button nextBtn, previousBtn;
    private String postName;
    private int selectCount;
    private TextView voteInfoSelection;
    private View rootView;
    private File deviceScreenShotPath;
    private String dateTime;
    private RecyclerView summaryListView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voting);


        databaseHelper = new DatabaseHelper(this);

        candidatesData = databaseHelper.candidatesData();
        postsData = databaseHelper.postsData();


        initializePreferences();
        initializationViews();


    }

    private void getNameListFromPost(int post_id) {
        voting_cover_image.setVisibility(View.GONE);
        candidatesLists = databaseHelper.voteProcess(post_id);

        generateRuntimeGridView();

        postName = databaseHelper.getPost(post_id);
        int max = databaseHelper.getMax(post_id);
        selectCount = max;
        int savedCount = preferences.getInt(postName, selectCount);

        selectCount = savedCount;

        voteInfoSelection.setText(postName + String.valueOf(selectCount));


    }

    private void generateRuntimeGridView() {
        int value = Math.abs(candidatesLists.size() / numberOfColumns);
        int rem = candidatesLists.size() % numberOfColumns;

        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        int width = displayMetrics.widthPixels / numberOfColumns;
        int count = 0;

        canContainer.removeAllViews();

        canContainer.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
        Log.i("CanmodelSize", "CanmodelSize:" + candidatesLists.size());
        for (int i = 0; i < value; i++) {
            LinearLayout itemsLayout = new LinearLayout(this);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT);
            params.weight = 1;
            itemsLayout.setWeightSum(numberOfColumns);
            itemsLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, (int) (width * .65)));


            for (int j = 0; j < numberOfColumns; j++) {
                View view = getView(count);
                view.setLayoutParams(params);
                itemsLayout.addView(view);
                count++;
            }
            canContainer.addView(itemsLayout);

        }
        LinearLayout row = new LinearLayout(this);

        row.setWeightSum(numberOfColumns);
        row.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, (int) (width * .65)));
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT);
        params.weight = 1;
        row.setWeightSum(numberOfColumns);

        for (int i = 0; i < rem; i++) {
            View view = getView(count);
            view.setLayoutParams(params);

            row.addView(view);
            count++;
        }
        canContainer.addView(row);


    }


    public View getView(int i) {
        final CandidatesList canList = candidatesLists.get(i);

        final String candidatesId = canList.can_id;
        final View candidateItemView = LayoutInflater.from(this).inflate(R.layout.candidates_item_layout, null);

        ImageView candidateImage = candidateItemView.findViewById(R.id.candidateImage);
        final ImageView swastikImage = candidateItemView.findViewById(R.id.candidateSwastik);
        TextView candidateName = candidateItemView.findViewById(R.id.candidateName);
        candidateName.setTextSize((float) (28 - (numberOfColumns * 1.5)));

        candidateName.setText(canList.can_nep_name);
//        alertImage(imageName, im, ".png");


        candidateItemView.setTag(canList.can_id);
        if (vote == null || vote.get(canList.can_id) == null || vote.get(canList.can_id).equals("0")) {
            swastikImage.setVisibility(View.INVISIBLE);

        } else {
            swastikImage.setVisibility(View.VISIBLE);

        }

        candidateItemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                view.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        view.setClickable(true);
                    }
                }, 1000);
                view.setClickable(false);


                if (swastikImage.getVisibility() == View.VISIBLE) {
                    swastikImage.setVisibility(View.INVISIBLE);
                    selectCount++;
                    voteInfoSelection.setText(postName + String.valueOf(selectCount));

                    vote.put(candidatesId, "0");
                    databaseHelper.removeFromSummary(candidatesId);
                } else {
                    swastikImage.setVisibility(View.VISIBLE);
                    selectCount--;
                    voteInfoSelection.setText(postName + String.valueOf(selectCount));


                    vote.put(candidatesId, "1");
                    databaseHelper.setSummary(postId, postName, canList.can_nep_name, candidatesId);

                }
                preferences.edit().putInt(postName, selectCount).apply();

            }
        });
        return candidateItemView;
    }

    private void initializationViews() {
        canContainer = findViewById(R.id.canContainer);
        voting_cover_image = findViewById(R.id.voting_cover_image);

        nextBtn = findViewById(R.id.nextBtn);
        previousBtn = findViewById(R.id.previousBtn);

        voteInfoSelection = findViewById(R.id.voteInfoSelection);
        summaryListView = findViewById(R.id.summaryListView);

        GridLayoutManager gridLayoutManagers = new GridLayoutManager(this, numberOfColumns);
        summaryListView.setLayoutManager(gridLayoutManagers);
        summaryListView.setHasFixedSize(true);


        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                nextList();

            }
        });

        previousBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                previousList();

                dateTime = GeneralUtils.getDateTime();
                Bitmap bitmap = takeScreenshot();
                saveBitmap(bitmap, String.valueOf(dateTime) + "summary");
            }
        });


        if (databaseHelper.postsData() == 0) {
            voting_cover_image.setImageResource(R.drawable.image1);
        }
        voting_cover_image.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return false;
            }
        });
        voting_cover_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (databaseHelper.candidatesData() > 0 && databaseHelper.postsData() > 0) {
                    postId = 1;
                    getNameListFromPost(postId);
                }
            }
        });


    }

    private void previousList() {

        postId--;
        getNameListFromPost(postId);

    }

    private void nextList() {

        if (postId < databaseHelper.postsData()) {
            postId++;
            getNameListFromPost(postId);
        } else if (postId == databaseHelper.postsData()) {
            voting_cover_image.setVisibility(View.VISIBLE);
            voting_cover_image.setImageResource(R.drawable.image3);
            summaryListView.setVisibility(View.VISIBLE);
            summaryListView.setAdapter(new SummaryAdaptar(databaseHelper.summaryLists(), this));

        }

    }

    private void initializePreferences() {
        preferences = getSharedPreferences(UtilStrings.SHARED_PREFERENCES, 0);
        numberOfColumns = preferences.getInt(UtilStrings.NO_OF_COLUMNS, 0);
    }

    /*key Events from Keyboard*/


    public boolean onKeyDown(int keyCode, KeyEvent event) {


        if (keyCode == KeyEvent.KEYCODE_NUMPAD_6 && event.getSource() == InputDevice.SOURCE_KEYBOARD) {


        } else if (keyCode == KeyEvent.KEYCODE_NUMPAD_2 && event.getSource() == InputDevice.SOURCE_KEYBOARD) {

            /*main functionHere*/
            if (databaseHelper.candidatesData() > 0 && databaseHelper.postsData() > 0) {
                postId = 1;
                getNameListFromPost(postId);
            }


        } else if (keyCode == KeyEvent.KEYCODE_NUMPAD_ADD && event.getSource() == InputDevice.SOURCE_KEYBOARD) {
            try {
                // clearing app data
                String packageName = getApplicationContext().getPackageName();
                Runtime runtime = Runtime.getRuntime();
                runtime.exec("pm clear " + packageName);

            } catch (Exception e) {
                e.printStackTrace();
            }

        } else if (keyCode == KeyEvent.KEYCODE_NUMPAD_MULTIPLY && event.getSource() == InputDevice.SOURCE_KEYBOARD) {
            /*insert data here*/

            if (databaseHelper.candidatesData() == 0 && databaseHelper.postsData() == 0) {
                new InsertingDataFromTxt(this).insertCandidatesTxt();
            }

        }


        return false;
    }


    public Bitmap takeScreenshot() {
        rootView = findViewById(android.R.id.content).getRootView();
        rootView.setDrawingCacheEnabled(true);
        Bitmap bitmap = rootView.getDrawingCache();
        return bitmap;
    }

    private void saveBitmap(Bitmap bitmap, String time) {
        deviceScreenShotPath = new File(Environment.getExternalStorageDirectory() + "/ballot/" + time + ".jpg");

        FileOutputStream fos;
        try {
            fos = new FileOutputStream(deviceScreenShotPath);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);

            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            Log.e("GREC", e.getMessage(), e);
        } catch (IOException e) {
            Log.e("GREC", e.getMessage(), e);

        }

        rootView.destroyDrawingCache();

    }

    private void clearPref() {

        SQLiteDatabase databases = databaseHelper.getWritableDatabase();
        Cursor c = databases.rawQuery("select * from " + POST_TABLE, null);

        while (c.moveToNext()) {

            String post = c.getString(c.getColumnIndex(POST_NAME_NEPALI));
            Log.i("post", "Post:" + post);
            preferences.edit().remove(post).apply();

        }
        c.close();
        databaseHelper.close();


    }

    /*summary Adapter*/

    public class SummaryAdaptar extends RecyclerView.Adapter<SummaryAdaptar.MyViewHolder> {
        private List<SummaryList> summaryLists;
        private Context context;

        public SummaryAdaptar(List<SummaryList> summaryLists, Context context) {
            this.summaryLists = summaryLists;
            this.context = context;
        }


        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.summary_view, parent, false);

            return new MyViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final MyViewHolder holder, final int position) {
            final SummaryList summaryList = summaryLists.get(position);
            holder.canName.setText(summaryList.getCanName() + "\n(" + summaryList.getPostNepali() + ")");
            holder.swastikImage.setVisibility(View.VISIBLE);

            Log.i("adapterList", "" + summaryLists.size());
            holder.container.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


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
            ImageView swastikImage;
            ImageView candidateImage;
            TextView canName;


            public MyViewHolder(View itemView) {
                super(itemView);


                container = itemView.findViewById(R.id.container);
                swastikImage = itemView.findViewById(R.id.swastikImage);
                candidateImage = itemView.findViewById(R.id.candidateImage);
                canName = itemView.findViewById(R.id.canName);


            }
        }


    }

}
