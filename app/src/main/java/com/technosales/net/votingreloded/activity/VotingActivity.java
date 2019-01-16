package com.technosales.net.votingreloded.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.smdt.SmdtManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.InputDevice;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.technosales.net.votingreloded.R;
import com.technosales.net.votingreloded.helper.DatabaseHelper;
import com.technosales.net.votingreloded.pojoModels.CandidatesList;
import com.technosales.net.votingreloded.pojoModels.SummaryList;
import com.technosales.net.votingreloded.utils.BackgroundColor;
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
import java.util.Map;

import static com.technosales.net.votingreloded.helper.DatabaseHelper.POST_NAME_NEPALI;
import static com.technosales.net.votingreloded.helper.DatabaseHelper.POST_TABLE;

public class VotingActivity extends AppCompatActivity implements View.OnClickListener {

    private DatabaseHelper databaseHelper;
    private LinearLayout canContainer;
    private SharedPreferences preferences;
    private int numberOfColumns;
    private int postId = 0;

    public ImageView voting_cover_image;
    private List<CandidatesList> candidatesLists = new ArrayList<>();
    private HashMap<String, String> vote = new HashMap<>();

    private Button nextBtn, previousBtn;
    private String postName;
    private int selectCount;
    private View rootView;
    private File deviceScreenShotPath;
    private String dateTime;
    private RecyclerView summaryListView;
    private RelativeLayout nextPrevContainer;
    private RelativeLayout confirmationLayout;
    private RelativeLayout votingActivity;

    private Button finished_btn;
    private Button not_finished_btn;
    public TextView voterCountTxt;
    public TextView dateHeader;
    private int voterCounter;

    private SmdtManager smdtManager;


    private Animation animationLtr;
    private Animation animationRtl;

    private LinearLayout postNameCountLayout;
    private TextView postNameTxtView;
    private TextView postKaLaagi;
    private TextView selectCountTxt;
    private TextView matadaanGarnuhos;
    private Animation blinkingAnimation;
    private Typeface suryodayaFont;
    private LinearLayout overLayout;
    private boolean summaryCorrection = false;
    private boolean pressedFinished = false;
    private boolean toastShown;
    private int clearDataCount = 0;
    private boolean stageCompleted;
    private int finishCounter = 0;
    private TextView summaryCorrectionTxt;
    private LinearLayout passwordLayout;
    private EditText enterPass;
    private TextView alertDateView;
    private Button alertPassBtn;
    private boolean keyPressed = true;
    private Animation finishBlink;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voting);


        databaseHelper = new DatabaseHelper(this);

        databaseHelper.clearSummary();

        smdtManager = SmdtManager.create(this);

        greenOn(false);
        redOn(true);

        /*initial anim*/
        animationLtr = AnimationUtils.loadAnimation(this, R.anim.slide_ltr);
        animationRtl = AnimationUtils.loadAnimation(this, R.anim.slide_rtl);

        blinkingAnimation = new AlphaAnimation(0.0f, 1.0f);
        blinkingAnimation.setDuration(300);
        blinkingAnimation.setStartOffset(50);
        blinkingAnimation.setRepeatMode(Animation.REVERSE);
        blinkingAnimation.setRepeatCount(Animation.INFINITE);

        finishBlink = new AlphaAnimation(0.0f, 1.0f);
        finishBlink.setDuration(700);
        finishBlink.setStartOffset(50);
        finishBlink.setRepeatMode(Animation.REVERSE);
        finishBlink.setRepeatCount(Animation.INFINITE);
        initializePreferences();
        initializationViews();


    }

    private void getNameListFromPost(int post_id) {
        postId = post_id;
        Log.i("getPostID", post_id + "");
        canContainer.setVisibility(View.VISIBLE);
        BackgroundColor.setBackroundColor(this, post_id, votingActivity);

        if (post_id == 1) {
            previousBtn.setVisibility(View.GONE);
        } else {
            previousBtn.setVisibility(View.VISIBLE);
        }
        voting_cover_image.setVisibility(View.GONE);
        candidatesLists = databaseHelper.voteProcess(post_id);

        generateRuntimeGridView();

        postName = databaseHelper.getPost(post_id);
        int max = databaseHelper.getMax(post_id);
        selectCount = max;
        int savedCount = preferences.getInt(postName, selectCount);

        selectCount = savedCount;

        postNameTxtView.setText(postName);
        if (selectCount == 0) {
            selectCountTxt.setText("");
            postKaLaagi.setText(getString(R.string.post_ma_max_selection_reached));
            matadaanGarnuhos.setText(getString(R.string.wanna_change));
        } else {
            postKaLaagi.setText(getString(R.string.post_ma_kunai));
            matadaanGarnuhos.setText(getString(R.string.matadaan_garnuhos));
            selectCountTxt.setText(String.valueOf(selectCount));
        }


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
        Log.i("getVIew", i + "");
        final CandidatesList canList = candidatesLists.get(i);

        final String candidatesId = canList.can_id;
        final View candidateItemView = LayoutInflater.from(this).inflate(R.layout.candidates_item_layout, null);

        final ImageView candidateImage = candidateItemView.findViewById(R.id.candidateImage);
        final ImageView swastikImage = candidateItemView.findViewById(R.id.candidateSwastik);
        final LinearLayout ballotCandidate = candidateItemView.findViewById(R.id.ballotPaper);
        TextView candidateName = candidateItemView.findViewById(R.id.candidateName);
        candidateName.setTextSize((float) (28 - (numberOfColumns * 1.5)));

        GeneralUtils.canImagePath(candidatesId, candidateImage);

        candidateName.setText(canList.can_nep_name);


        candidateItemView.setTag(canList.can_id);
        if (vote == null || vote.get(canList.can_id) == null || vote.get(canList.can_id).equals("0")) {
            swastikImage.setVisibility(View.INVISIBLE);

        } else {
            swastikImage.setVisibility(View.VISIBLE);

        }

        candidateItemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                Log.i("getVIew", candidatesId + "");
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
                    selectCountTxt.setText(String.valueOf(selectCount));
                    postNameTxtView.setText(postName);
                    postKaLaagi.setText(getString(R.string.post_ma_kunai));
                    matadaanGarnuhos.setText(getString(R.string.matadaan_garnuhos));

                    vote.put(candidatesId, "0");
                    databaseHelper.removeFromSummary(candidatesId);


                } else if (selectCount > 0) {

                    swastikImage.setVisibility(View.VISIBLE);
                    selectCount--;
                    selectCountTxt.setText(String.valueOf(selectCount));
                    postNameTxtView.setText(postName);
                    postKaLaagi.setText(getString(R.string.post_ma_kunai));
                    matadaanGarnuhos.setText(getString(R.string.matadaan_garnuhos));
                    vote.put(candidatesId, "1");
                    databaseHelper.setSummary(postId, postName, canList.can_nep_name, candidatesId);
                    if (selectCount == 0) {
                        overLayout.setVisibility(View.VISIBLE);


                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {

                                nextList();

                            }
                        }, 500);

                    }

                } else if (selectCount == 0) {
                    if (!toastShown)
                        showToast(ballotCandidate);
                }
                preferences.edit().putInt(postName, selectCount).apply();

                if (databaseHelper.summaryData() < 1) {
                    greenOn(false);
                } else {
                    greenOn(true);
                }

            }
        });
        return candidateItemView;
    }

    private void showToast(LinearLayout view) {
        toastShown = true;
        Rect rectf = new Rect();
        view.getLocalVisibleRect(rectf);
        view.getGlobalVisibleRect(rectf);

        Log.d("WIDTH        :", String.valueOf(rectf.width()));
        Log.d("HEIGHT       :", String.valueOf(rectf.height()));
        Log.d("left         :", String.valueOf(rectf.left));
        Log.d("right        :", String.valueOf(rectf.right));
        Log.d("top          :", String.valueOf(rectf.top));
        Log.d("bottom       :", String.valueOf(rectf.bottom));

        final Toast toast = new Toast(VotingActivity.this);
        toast.setGravity(Gravity.TOP, 0, rectf.top);
        TextView textview = new TextView(VotingActivity.this);
        textview.setTextColor(Color.RED);
        textview.setTextSize(25);
        textview.setPadding(10, 10, 10, 10);
        textview.setBackgroundResource(R.drawable.max_selection_bg);
        toast.setView(textview);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.show();
        textview.setText(R.string.max_selection_reached);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                toastShown = false;
            }
        }, 3000);
    }


    private void initializationViews() {
        canContainer = findViewById(R.id.canContainer);
        voting_cover_image = findViewById(R.id.voting_cover_image);

        nextBtn = findViewById(R.id.nextBtn);
        previousBtn = findViewById(R.id.previousBtn);
        nextPrevContainer = findViewById(R.id.nextPrevContainer);

        postKaLaagi = findViewById(R.id.postKaLaagi);
        selectCountTxt = findViewById(R.id.selectCountTxt);
        postNameTxtView = findViewById(R.id.postNameTxtView);
        matadaanGarnuhos = findViewById(R.id.matadaanGarnuhos);
        postNameCountLayout = findViewById(R.id.postNameCountLayout);


        summaryListView = findViewById(R.id.summaryListView);
        summaryCorrectionTxt = findViewById(R.id.summaryCorrectionTxt);

        confirmationLayout = findViewById(R.id.confirmationLayout);
        overLayout = findViewById(R.id.overLayout);
        votingActivity = findViewById(R.id.votingActivity);
        finished_btn = findViewById(R.id.yesFinished);
        not_finished_btn = findViewById(R.id.notFinished);

        passwordLayout = findViewById(R.id.passwordLayout);
        enterPass = findViewById(R.id.enterPass);
        alertDateView = findViewById(R.id.alertDateView);
        alertPassBtn = findViewById(R.id.alertPassBtn);


        voterCountTxt = findViewById(R.id.voterCount);
        dateHeader = findViewById(R.id.dateHeader);

        dateHeader.setText(GeneralUtils.getDateOnly());

        if (databaseHelper.candidatesData() > 0) {

            voterCountTxt.setVisibility(View.VISIBLE);
            voterCountTxt.setText(getString(R.string.total_voters) + String.valueOf(voterCounter));
        }
        selectCountTxt.startAnimation(blinkingAnimation);
        finished_btn.startAnimation(finishBlink);
        suryodayaFont = Typeface.createFromAsset(getAssets(), "fonts/Suryodaya.ttf");
        selectCountTxt.setTypeface(suryodayaFont);

        GridLayoutManager gridLayoutManagers = new GridLayoutManager(this, numberOfColumns);
        summaryListView.setLayoutManager(gridLayoutManagers);
        summaryListView.setHasFixedSize(true);

//      take screenshot and write
        /*dateTime = GeneralUtils.getDateTime();
        Bitmap bitmap = takeScreenshot();
        saveBitmap(bitmap, String.valueOf(dateTime) + "summary");*/


        if (databaseHelper.postsData() == 0) {
            voting_cover_image.setImageResource(R.drawable.image1);
        } else {
            voting_cover_image.setImageResource(R.drawable.image2);
        }

        setClickListener();
    }

    private void setClickListener() {
        nextBtn.setOnClickListener(this);
        previousBtn.setOnClickListener(this);
        finished_btn.setOnClickListener(this);
        not_finished_btn.setOnClickListener(this);
        overLayout.setOnClickListener(this);
        voterCountTxt.setOnClickListener(this);
        alertPassBtn.setOnClickListener(this);
    }

    private void previousList() {
        overLayout.setVisibility(View.GONE);
        canContainer.startAnimation(animationLtr);
        postNameCountLayout.startAnimation(animationLtr);
        postId--;
        getNameListFromPost(postId);
        previousBtn.postDelayed(new Runnable() {
            @Override
            public void run() {
                previousBtn.setClickable(true);
            }
        }, 1500);
        previousBtn.setClickable(false);


    }

    private void nextList() {
        Log.i("hashmap", vote + "");
        overLayout.setVisibility(View.GONE);

        if (postId < databaseHelper.postsData() && !summaryCorrection) {
            postId++;
            getNameListFromPost(postId);


            nextBtn.postDelayed(new Runnable() {
                @Override
                public void run() {
                    nextBtn.setClickable(true);
                }
            }, 1500);

            nextBtn.setClickable(false);
            canContainer.startAnimation(animationRtl);
            postNameCountLayout.startAnimation(animationRtl);
        } else if (postId == databaseHelper.postsData() || summaryCorrection) {
            summaryCorrection = false;
            nextPrevContainer.setVisibility(View.GONE);
            canContainer.setVisibility(View.GONE);
            confirmationLayout.setVisibility(View.VISIBLE);
            voting_cover_image.setVisibility(View.VISIBLE);
            voting_cover_image.setImageResource(R.drawable.image3);
            summaryCorrectionTxt.setVisibility(View.VISIBLE);
            summaryListView.setVisibility(View.VISIBLE);
            summaryListView.setAdapter(new SummaryAdaptar(databaseHelper.summaryLists(), this));

        }

    }

    private void initializePreferences() {
        preferences = getSharedPreferences(UtilStrings.SHARED_PREFERENCES, 0);
        numberOfColumns = preferences.getInt(UtilStrings.NO_OF_COLUMNS, 0);
        voterCounter = preferences.getInt(UtilStrings.VOTER_COUNT, 0);
        stageCompleted = preferences.getBoolean(UtilStrings.STAGE_COMPLETED, false);

        if (stageCompleted) {
            startActivity(new Intent(this, VoteResultsActivity.class));
            finish();
        }


    }

    /*key Events from Keyboard*/


    public boolean onKeyDown(int keyCode, KeyEvent event) {


        if (keyCode == KeyEvent.KEYCODE_NUMPAD_9 && event.getSource() == InputDevice.SOURCE_KEYBOARD) {

            goToResults();


        } else if (keyCode == KeyEvent.KEYCODE_NUMPAD_2 && event.getSource() == InputDevice.SOURCE_KEYBOARD) {
            /*main functionHere*/
            finishCounter = 0;
            enterPass.setText("");
            if (passwordLayout.getVisibility() == View.VISIBLE) {
                passwordLayout.setVisibility(View.GONE);
                try {
                    enterPass.setFocusable(false);
                    InputMethodManager inputMethodManager = (InputMethodManager) getApplicationContext().getSystemService(Activity.INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);

                } catch (Exception ex) {

                }
            }
            if (databaseHelper.candidatesData() > 0 && databaseHelper.postsData() > 0) {

                redOn(false);
                greenOn(false);
                summaryCorrection = false;
                overLayout.setVisibility(View.GONE);
                summaryCorrectionTxt.setVisibility(View.GONE);


                if (preferences.getInt(UtilStrings.VOTER_COUNT, 0) == 0) {
                    GeneralUtils.createVoteStartEndTimeFile(preferences.getString(UtilStrings.DEVICE_NUMBER, ""), UtilStrings.BALLOT_PATH_DEVICE, "Start");
                    GeneralUtils.createVoteStartEndTimeFile(preferences.getString(UtilStrings.DEVICE_NUMBER, ""), UtilStrings.BALLOT_PATH_CARD, "Start");
                }

                if (summaryListView.getVisibility() == View.GONE && voting_cover_image.getVisibility() == View.GONE) {
                    if (String.valueOf(vote).equals("{}")) {
                        summaryListView.setVisibility(View.GONE);
                        summaryCorrectionTxt.setVisibility(View.GONE);
                        postId = 1;
                        nextPrevContainer.setVisibility(View.VISIBLE);
                        getNameListFromPost(postId);
                    } else {
                        voting_cover_image.setImageResource(R.drawable.image3);
                        nextPrevContainer.setVisibility(View.GONE);
                        voting_cover_image.setVisibility(View.VISIBLE);
                        summaryListView.setVisibility(View.VISIBLE);
                        summaryCorrectionTxt.setVisibility(View.VISIBLE);
                        summaryListView.setAdapter(new SummaryAdaptar(databaseHelper.summaryLists(), this));

//                        new Handler()

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {

                                Bitmap takeScreen = takeScreenshot();
                                saveBitmap(takeScreen, dateTime);
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        finishVoting();

                                        dateTime = GeneralUtils.getDateTime();
                                        voterCountTxt.setVisibility(View.GONE);
                                        voterCountTxt.setVisibility(View.GONE);
                                        summaryListView.setVisibility(View.GONE);
                                        summaryCorrectionTxt.setVisibility(View.GONE);
                                        confirmationLayout.setVisibility(View.GONE);
                                        postId = 1;
                                        nextPrevContainer.setVisibility(View.VISIBLE);
                                        getNameListFromPost(postId);
                                    }
                                }, 2000);

                            }
                        }, 500);


                    }
                } else if (voting_cover_image.getVisibility() == View.VISIBLE && summaryListView.getVisibility() == View.GONE) {
                    overLayout.setVisibility(View.GONE);
                    clearPref();
                    dateTime = GeneralUtils.getDateTime();
                    voterCountTxt.setVisibility(View.GONE);
                    summaryListView.setVisibility(View.GONE);
                    summaryCorrectionTxt.setVisibility(View.GONE);
                    postId = 1;
                    nextPrevContainer.setVisibility(View.VISIBLE);
                    getNameListFromPost(postId);
                } else if (voting_cover_image.getVisibility() == View.VISIBLE && summaryListView.getVisibility() == View.VISIBLE) {
                    Bitmap takeScreen = takeScreenshot();
                    saveBitmap(takeScreen, dateTime);
                    finishVoting();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {

                            dateTime = GeneralUtils.getDateTime();
                            voterCountTxt.setVisibility(View.GONE);
                            voterCountTxt.setVisibility(View.GONE);
                            summaryListView.setVisibility(View.GONE);
                            summaryCorrectionTxt.setVisibility(View.GONE);
                            confirmationLayout.setVisibility(View.GONE);
                            overLayout.setVisibility(View.GONE);
                            postId = 1;
                            nextPrevContainer.setVisibility(View.VISIBLE);
                            getNameListFromPost(postId);
                        }
                    }, 2000);
                }


            }


        } else if (keyCode == KeyEvent.KEYCODE_NUMPAD_ADD && event.getSource() == InputDevice.SOURCE_KEYBOARD) {
            clearDataCount++;
            if (clearDataCount == 3) {
                try {
                    // clearing app data
                    String packageName = getApplicationContext().getPackageName();
                    Runtime runtime = Runtime.getRuntime();
                    runtime.exec("pm clear " + packageName);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        } else if (keyCode == KeyEvent.KEYCODE_NUMPAD_MULTIPLY && event.getSource() == InputDevice.SOURCE_KEYBOARD) {
            /*insert data here*/

            if (databaseHelper.candidatesData() == 0 && databaseHelper.postsData() == 0) {
                new InsertingDataFromTxt(this).insertCandidatesTxt();
            } else {
                AlertDialog.Builder builder;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    builder = new AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog_Alert);
                } else {
                    builder = new AlertDialog.Builder(this);
                }
                builder.setTitle("Delete Data")
                        .setMessage("Are you sure you want to delete this entry?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // continue with delete
                                databaseHelper.clearSummary();
                                databaseHelper.clearCandidates();
                                databaseHelper.clearPosts();
                                preferences.edit().remove(UtilStrings.VOTER_COUNT).apply();
                                voterCountTxt.setVisibility(View.GONE);
                                nextPrevContainer.setVisibility(View.GONE);
                                confirmationLayout.setVisibility(View.GONE);
                                summaryListView.setVisibility(View.GONE);
                                summaryCorrectionTxt.setVisibility(View.GONE);
                                vote.clear();
                                voting_cover_image.setVisibility(View.VISIBLE);
                                voting_cover_image.setImageResource(R.drawable.image1);

                                GeneralUtils.deleteFilesInBallot(new File(UtilStrings.BALLOT_PATH_DEVICE), new File(UtilStrings.BALLOT_PATH_CARD));


                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // do nothing
                            }
                        })
                        /*.setNeutralButton(getString(R.string.start_activity), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                preferences.edit().remove(UtilStrings.STAGE_PASSED).apply();
                                startActivity(new Intent(VotingActivity.this, StartActivityVisual.class));
                                finish();
                            }
                        })*/
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }

        }


        return false;
    }

    public void goToResults() {
        GeneralUtils.createVoteStartEndTimeFile(preferences.getString(UtilStrings.DEVICE_NUMBER, ""), UtilStrings.BALLOT_PATH_DEVICE, "End");
        GeneralUtils.createVoteStartEndTimeFile(preferences.getString(UtilStrings.DEVICE_NUMBER, ""), UtilStrings.BALLOT_PATH_CARD, "End");

        preferences.edit().putBoolean(UtilStrings.STAGE_COMPLETED, true).apply();
        startActivity(new Intent(this, VoteResultsActivity.class));
        finish();
    }


    public Bitmap takeScreenshot() {
        rootView = findViewById(android.R.id.content).getRootView();
        rootView.setDrawingCacheEnabled(true);
        Bitmap bitmap = rootView.getDrawingCache();
        return bitmap;
    }

    private void saveBitmap(Bitmap bitmap, String time) {
        deviceScreenShotPath = new File(UtilStrings.SCREENSHOT_DEVICE + time + ".jpg");

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

        saveOnCard(bitmap, time);

    }

    private void saveOnCard(Bitmap bitmap, String time) {
        deviceScreenShotPath = new File(UtilStrings.SCREENSHOT_CARD + time + ".jpg");
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

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.nextBtn:
                nextList();
                break;
            case R.id.previousBtn:
                previousList();
                break;
            case R.id.yesFinished:
                overLayout.setVisibility(View.VISIBLE);
                pressedFinished = true;
                confirmationLayout.setVisibility(View.GONE);

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        Bitmap takeScreen = takeScreenshot();
                        saveBitmap(takeScreen, dateTime);
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {

                                finishVoting();
                            }
                        }, 2000);
                    }
                }, 1000);


                break;
            case R.id.notFinished:
                summaryListView.setVisibility(View.GONE);
                summaryCorrectionTxt.setVisibility(View.GONE);
                voting_cover_image.setVisibility(View.GONE);
                confirmationLayout.setVisibility(View.GONE);
                nextPrevContainer.setVisibility(View.VISIBLE);

                getNameListFromPost(databaseHelper.postsData());

                break;
            case R.id.overLayout:
                break;
            case R.id.voterCount:
                finishCounter++;
                Log.i("finishCount", finishCounter + "");
                if (finishCounter % 5 == 0) {

                    passwordLayout.setVisibility(View.VISIBLE);
                    alertDateView.setText(GeneralUtils.getDateOnly());
                    enterPass.setFocusable(true);

                }

                break;

            case R.id.alertPassBtn:
                if (GeneralUtils.passwordMatch(enterPass.getText().toString().trim())) {
                    goToResults();
                } else {
                    passwordLayout.setVisibility(View.GONE);
                }
                break;
        }

    }

    private void finishVoting() {
        databaseHelper.clearSummary();
        confirmationLayout.setVisibility(View.GONE);
        summaryListView.setVisibility(View.GONE);
        summaryCorrectionTxt.setVisibility(View.GONE);
        postId = 0;
        clearPref();
        voting_cover_image.setImageResource(R.drawable.image4);
        voterCounter = preferences.getInt(UtilStrings.VOTER_COUNT, 0);
        voterCounter++;
        preferences.edit().putInt(UtilStrings.VOTER_COUNT, voterCounter).apply();
        voterCountTxt.setVisibility(View.VISIBLE);
        voterCountTxt.setText(getString(R.string.total_voters) + String.valueOf(voterCounter));

        for (Map.Entry<String, String> entry : vote.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            databaseHelper.updateVoteCount(Integer.parseInt(value), key);
        }
        vote.clear();
        databaseHelper.writeToFile(preferences.getString(UtilStrings.DEVICE_NUMBER, ""), UtilStrings.BALLOT_PATH_DEVICE);
        databaseHelper.writeToFile(preferences.getString(UtilStrings.DEVICE_NUMBER, ""), UtilStrings.BALLOT_PATH_CARD);
        pressedFinished = false;
        redOn(true);
        overLayout.setVisibility(View.GONE);


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
            holder.canName.setText(summaryList.getCanName() + ":" + summaryList.getPostNepali());
//            holder.canName.setText(summaryList.getCanName() + "(" + summaryList.getPostNepali() + ")");
            holder.swastikImage.setVisibility(View.VISIBLE);
            GeneralUtils.canImagePath(summaryList.canId, holder.candidateImage);
            BackgroundColor.setBackroundColor(context, summaryList.postId, holder.container);


            DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
            int width = displayMetrics.widthPixels / numberOfColumns;

            LinearLayout.LayoutParams lp =
                    new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, (int) (width * .45));
            holder.imageContainer.setLayoutParams(lp);
            holder.canName.setTextSize((float) (28 - (numberOfColumns * 1.5)));
            Log.i("adapterList", "" + summaryLists.size());
            holder.container.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!pressedFinished) {
                        summaryCorrection = true;
                        summaryListView.setVisibility(View.GONE);
                        summaryCorrectionTxt.setVisibility(View.GONE);
                        nextPrevContainer.setVisibility(View.VISIBLE);
                        voting_cover_image.setVisibility(View.GONE);
                        confirmationLayout.setVisibility(View.GONE);
                        postId = summaryList.postId;
                        getNameListFromPost(postId);
                    }
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

    private void greenOn(boolean onOff) {
        smdtManager.smdtSetExtrnalGpioValue(4, !onOff);
    }

    private void redOn(boolean onOff) {
        smdtManager.smdtSetExtrnalGpioValue(3, !onOff);
    }


}
