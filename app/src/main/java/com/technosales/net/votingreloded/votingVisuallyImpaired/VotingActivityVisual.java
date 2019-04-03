package com.technosales.net.votingreloded.votingVisuallyImpaired;

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
import android.os.SystemClock;
import android.speech.tts.TextToSpeech;
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
import com.technosales.net.votingreloded.utils.CheckAskPermission;
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
import java.util.Locale;
import java.util.Map;

import static com.technosales.net.votingreloded.helper.DatabaseHelper.POST_NAME_NEPALI;
import static com.technosales.net.votingreloded.helper.DatabaseHelper.POST_TABLE;

public class VotingActivityVisual extends AppCompatActivity implements View.OnClickListener {

    private DatabaseHelper databaseHelper;
    private SharedPreferences preferences;
    private int numberOfColumns;
    private int postId = 0;

    public ImageView voting_cover_image;
    private List<CandidatesList> candidatesLists = new ArrayList<>();
    private HashMap<String, String> vote = new HashMap<>();

    private Button nextBtn, previousBtn;
    private String postName;
    private int selectCount;
    private TextView voteInfoSelection;
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
    private Animation finishBlink;
    private int visualPosition = -1;
    private RecyclerView candidatesListView;
    private TextToSpeech textToSpeech;
    private boolean nextNprev = false;
    private boolean keyPresssed = false;
    private String speakSummary;
    private boolean reachedMaxVisual = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visual);


        textToSpeech = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {
                    textToSpeech.setLanguage(new Locale("nep"));
                }
            }
        });
        textToSpeech.setSpeechRate(Float.parseFloat("0.89"));
       /* try {
            // clearing app data
            String packageName = getApplicationContext().getPackageName();
            Runtime runtime = Runtime.getRuntime();
            runtime.exec("pm clear " + packageName);

        } catch (Exception e) {
            e.printStackTrace();
        }*/

        databaseHelper = new DatabaseHelper(this);

        databaseHelper.clearSummary();

//        smdtManager = SmdtManager.create(this);

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
        visualPosition = -1;
        reachedMaxVisual = false;
        speakSummary = "";
        postId = post_id;
        Log.i("getPostID", post_id + "");
        candidatesListView.setVisibility(View.VISIBLE);
        BackgroundColor.setBackroundColor(this, post_id, votingActivity);

        if (post_id == 1) {
            previousBtn.setVisibility(View.GONE);
        } else {
            previousBtn.setVisibility(View.VISIBLE);
        }
        voting_cover_image.setVisibility(View.GONE);
        candidatesLists = databaseHelper.voteProcess(post_id);

//        generateRuntimeGridView();

        candidatesListView.setAdapter(new CanAdapter(candidatesLists, this));

        postName = databaseHelper.getPost(post_id);
        int max = databaseHelper.getMax(post_id);
        selectCount = max;
        int savedCount = preferences.getInt(postName, selectCount);

        selectCount = savedCount;

        postNameTxtView.setText(postName);
        if (selectCount == 0) {
            String tts=postName + " " + getString(R.string.post_ma_max_selection_reached);
            textToSpeech.speak(tts, TextToSpeech.QUEUE_FLUSH, null);
            selectCountTxt.setText("");
            postKaLaagi.setText(getString(R.string.post_ma_max_selection_reached));
            matadaanGarnuhos.setText(getString(R.string.wanna_change));
        } else {
            postKaLaagi.setText(getString(R.string.post_ma_kunai));
            matadaanGarnuhos.setText(getString(R.string.matadaan_garnuhos));
            selectCountTxt.setText(String.valueOf(selectCount));

            final String ttsChar = postName + " " + getString(R.string.post_ma_kunai) + " " + String.valueOf(selectCount) + " " + getString(R.string.matadaan_garnuhos);
            textToSpeech.speak(ttsChar, TextToSpeech.QUEUE_FLUSH, null);

//            int String ttsChars = postName.length() + getString(R.string.post_ma_kunai).length() + getString(R.string.matadaan_garnuhos).length() + 4;

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (visualPosition < 0 && selectCount > 0)
                        textToSpeech.speak(ttsChar, TextToSpeech.QUEUE_FLUSH, null);
                }
            }, GeneralUtils.getDelayTime(ttsChar.length()));
        }


    }

    Handler rHandler;
    Runnable rTicker;
    int cc = 0;

    public void intervelCheck() {

        rHandler = new Handler();
        rTicker = new Runnable() {
            public void run() {
                long now = SystemClock.uptimeMillis();
                long next = now + 15000;
                cc++;
                if (cc >= 10) {
                    cc = 0;

                }
                if (visualPosition < 0 && selectCount > 0)
                    textToSpeech.speak(postName + " " + getString(R.string.post_ma_kunai) + " " + String.valueOf(selectCount) + " " + getString(R.string.matadaan_garnuhos), TextToSpeech.QUEUE_FLUSH, null);

                rHandler.postAtTime(rTicker, next);
            }
        };
        rTicker.run();


    }

    private void generateRuntimeGridView() {
        /*int value = Math.abs(candidatesLists.size() / numberOfColumns);
        int rem = candidatesLists.size() % numberOfColumns;

        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        int width = displayMetrics.widthPixels / numberOfColumns;
        int count = 0;*/

        candidatesListView.removeAllViews();

        for (int i = 0; i < candidatesLists.size(); i++) {
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
            candidatesListView.addView(candidateItemView);
        }


    }


    public View getView(int i) {
        Log.i("getVIew", i + "");

        return null;
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

        final Toast toast = new Toast(VotingActivityVisual.this);
        toast.setGravity(Gravity.TOP, 0, rectf.top);
        TextView textview = new TextView(VotingActivityVisual.this);
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
        candidatesListView = findViewById(R.id.candidateRecycleView);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, numberOfColumns);
        candidatesListView.setLayoutManager(gridLayoutManager);
        candidatesListView.setHasFixedSize(true);


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

    public void onPause() {
       /* if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }*/
        super.onPause();
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
        candidatesListView.startAnimation(animationLtr);
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
            candidatesListView.startAnimation(animationRtl);
            postNameCountLayout.startAnimation(animationRtl);
        } else if (postId == databaseHelper.postsData() || summaryCorrection) {
            if (databaseHelper.summaryData() == 0) {
                textToSpeech.speak(getString(R.string.complete_vote), TextToSpeech.QUEUE_FLUSH, null);
            }
            summaryCorrection = false;
            nextPrevContainer.setVisibility(View.GONE);
            candidatesListView.setVisibility(View.GONE);
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
        stageCompleted = preferences.getBoolean(UtilStrings.VOTE_STAGE_PASSED, false);

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
            Toast.makeText(this, UtilStrings.CANDIDATES_TXT_PATH, Toast.LENGTH_SHORT).show();
            if (databaseHelper.candidatesData() == 0 && databaseHelper.postsData() == 0) {
                new InsertingDataFromTxt(this).insertCandidatesTxt();
                if (CheckAskPermission.isReadStorageAllowed(VotingActivityVisual.this)){
                    CheckAskPermission.askReadStorage(VotingActivityVisual.this);
                }
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

        } else if (keyCode == KeyEvent.KEYCODE_NUMPAD_8 && event.getSource() == InputDevice.SOURCE_KEYBOARD) {

            /*Log.i("ViewPosSize", "" + visualPosition + "::" + candidatesLists.size());
            if (((candidatesLists.size() - 1) - visualPosition) > -1 && (visualPosition - numberOfColumns) > -1 && !reachedMaxVisual) {
                visualPosition = visualPosition - numberOfColumns;

                scrollVisualPosition(visualPosition);
                //for up
            }*/

        } else if (keyCode == KeyEvent.KEYCODE_NUMPAD_5 && event.getSource() == InputDevice.SOURCE_KEYBOARD) {
           /* if (visualPosition < (candidatesLists.size() - numberOfColumns) && !reachedMaxVisual) {
                visualPosition = visualPosition + numberOfColumns;

                scrollVisualPosition(visualPosition);

                ///for Down

            }*/

        } else if (keyCode == KeyEvent.KEYCODE_NUMPAD_4 && event.getSource() == InputDevice.SOURCE_KEYBOARD) {
            if (summaryListView.getVisibility() == View.GONE && voting_cover_image.getVisibility() == View.GONE) {

                if (visualPosition > 0 && !nextNprev && !reachedMaxVisual) {
                    visualPosition = visualPosition - 1;

                    scrollVisualPosition(visualPosition);
                } else if (visualPosition <= 0 && !nextNprev && !reachedMaxVisual) {
                    if (postId > 1)
                        previousList();
                }
            } else if (summaryListView.getVisibility() == View.VISIBLE) {
                processNotFinished();
            }

        } else if (keyCode == KeyEvent.KEYCODE_NUMPAD_6 && event.getSource() == InputDevice.SOURCE_KEYBOARD) {
            if (summaryListView.getVisibility() == View.GONE && voting_cover_image.getVisibility() == View.GONE) {
                if (visualPosition < candidatesLists.size() - 1 && !nextNprev && !reachedMaxVisual) {
                    visualPosition = visualPosition + 1;

                    scrollVisualPosition(visualPosition);

                } else if (visualPosition == candidatesLists.size() - 1 && !nextNprev && !reachedMaxVisual) {
                    nextList();
                }
            } else {
                if (databaseHelper.summaryData() > 0) {
                    textToSpeech.speak(getString(R.string.this_is_summary) + "| " + speakSummary + "| " + getString(R.string.complete_vote), TextToSpeech.QUEUE_FLUSH, null);
                } else {
                    textToSpeech.speak(getString(R.string.non_selected), TextToSpeech.QUEUE_FLUSH, null);

                }
            }
            Log.i("getNext", visualPosition + "");

        } else if (keyCode == KeyEvent.KEYCODE_NUMPAD_7 && event.getSource() == InputDevice.SOURCE_KEYBOARD) {

            if (summaryListView.getVisibility() == View.VISIBLE) {
                processFinished();
                textToSpeech.speak(getString(R.string.matadaan_safal_vayo), TextToSpeech.QUEUE_FLUSH, null);
            } else {
                if (visualPosition > -1) {
                    if (!keyPresssed && !reachedMaxVisual)
                        voteFromKey(visualPosition);
                }

            }

        }


        return false;
    }

    private void voteFromKey(int pos) {
        keyPresssed = true;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                keyPresssed = false;
            }
        }, 1000);

        Log.i("Container", "votepos" + pos);
        for (int i = 0; i < candidatesListView.getAdapter().getItemCount(); i++) {
            LinearLayout view = (LinearLayout) candidatesListView.getChildAt(i);
            try {

                if (i == pos) {

                    if (view.findViewById(R.id.candidateSwastik).getVisibility() == View.INVISIBLE) {
                        if (selectCount > 0) {
                            view.findViewById(R.id.candidateSwastik).setVisibility(View.VISIBLE);
                            String ttsChar = postName + " पदमा " + candidatesLists.get(pos).can_nep_name + " लाई मतदान गर्नुभयो |";
                            textToSpeech.speak(ttsChar, TextToSpeech.QUEUE_FLUSH, null);
                            selectCount--;
                            selectCountTxt.setText(String.valueOf(selectCount));
                            postNameTxtView.setText(postName);
                            postKaLaagi.setText(getString(R.string.post_ma_kunai));
                            matadaanGarnuhos.setText(getString(R.string.matadaan_garnuhos));
                            vote.put(candidatesLists.get(pos).can_id, "1");
                            databaseHelper.setSummary(postId, postName, candidatesLists.get(pos).can_nep_name, candidatesLists.get(pos).can_id);

                            if (selectCount == 0) {

                                overLayout.setVisibility(View.VISIBLE);
                                reachedMaxVisual = true;

                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        nextList();

                                    }
                                }, GeneralUtils.getDelayTime(ttsChar.length()));

                            }
                        } else if (selectCount == 0) {
                            if (!reachedMaxVisual)
                                textToSpeech.speak(getString(R.string.max_selection_reached), TextToSpeech.QUEUE_FLUSH, null);

                        }

                    } else {
                        textToSpeech.speak(postName + " पद बाट  " + candidatesLists.get(pos).can_nep_name + " को मत हटाउनु भयो |", TextToSpeech.QUEUE_FLUSH, null);
                        view.findViewById(R.id.candidateSwastik).setVisibility(View.INVISIBLE);
                        selectCount++;
                        selectCountTxt.setText(String.valueOf(selectCount));
                        postNameTxtView.setText(postName);
                        postKaLaagi.setText(getString(R.string.post_ma_kunai));
                        matadaanGarnuhos.setText(getString(R.string.matadaan_garnuhos));
                        vote.put(candidatesLists.get(pos).can_id, "0");
                        databaseHelper.removeFromSummary(candidatesLists.get(pos).can_id);
                    }
                    preferences.edit().putInt(postName, selectCount).apply();

                    if (databaseHelper.summaryData() < 1) {
                        greenOn(false);
                    } else {
                        greenOn(true);
                    }

                } else {
                    view.setBackgroundResource(0);
//
                }
            } catch (Exception ex) {

            }


        }
    }

    private void scrollVisualPosition(int pos) {
        nextNprev = true;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                nextNprev = false;
            }
        }, 300);

        Log.i("Container", "viewPos" + pos);

        for (int i = 0; i < candidatesListView.getAdapter().getItemCount(); i++) {

            LinearLayout view = (LinearLayout) candidatesListView.getChildAt(i);
            try {

                if (i == pos) {
                    if (view.findViewById(R.id.candidateSwastik).getVisibility() == View.VISIBLE) {
                        textToSpeech.speak(candidatesLists.get(pos).can_nep_name + " लाई " + postName + " पदमा छानि सकिएको छ| " + getString(R.string.change_tts_vote), TextToSpeech.QUEUE_FLUSH, null);

                    } else {
                        if (i == candidatesListView.getAdapter().getItemCount() - 1) {
                            textToSpeech.speak(" र अन्त्यमा " + candidatesLists.get(pos).can_nep_name + "\t " + postName, TextToSpeech.QUEUE_FLUSH, null);

                        } else {
                            textToSpeech.speak(candidatesLists.get(pos).can_nep_name + "\t " + postName, TextToSpeech.QUEUE_FLUSH, null);

                        }
                    }
                    view.setBackgroundResource(R.drawable.startbtn_bg);
                } else {
                    view.setBackgroundResource(0);
//
                }
            } catch (Exception ex) {

            }


        }
        Log.i("Container", "container" + pos);


    }

    public void goToResults() {
        GeneralUtils.createVoteStartEndTimeFile(preferences.getString(UtilStrings.DEVICE_NUMBER, ""), UtilStrings.BALLOT_PATH_DEVICE, "End");
        GeneralUtils.createVoteStartEndTimeFile(preferences.getString(UtilStrings.DEVICE_NUMBER, ""), UtilStrings.BALLOT_PATH_CARD, "End");

        preferences.edit().putBoolean(UtilStrings.VOTE_STAGE_PASSED, true).apply();
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
                processFinished();
                break;
            case R.id.notFinished:

                processNotFinished();


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

    private void processNotFinished() {
        summaryListView.setVisibility(View.GONE);
        summaryCorrectionTxt.setVisibility(View.GONE);
        voting_cover_image.setVisibility(View.GONE);
        confirmationLayout.setVisibility(View.GONE);
        nextPrevContainer.setVisibility(View.VISIBLE);
        getNameListFromPost(databaseHelper.postsData());
    }

    private void processFinished() {
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

    public class CanAdapter extends RecyclerView.Adapter<CanAdapter.MyViewHolder> {
        private List<CandidatesList> canList;
        private Context context;

        public CanAdapter(List<CandidatesList> summaryLists, Context context) {
            this.canList = summaryLists;
            this.context = context;
        }


        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.candidates_item_layout, parent, false);

            return new MyViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final MyViewHolder holder, final int position) {

            final CandidatesList canList = candidatesLists.get(position);


            DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
            int width = displayMetrics.widthPixels / numberOfColumns;

            LinearLayout.LayoutParams lp =
                    new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, (int) (width * .45));
            holder.canAdapContainer.setLayoutParams(lp);
            holder.canAdapName.setTextSize((float) (28 - (numberOfColumns * 1.5)));

            GeneralUtils.canImagePath(canList.can_id, holder.canAdapImage);

            holder.canAdapName.setText(canList.can_nep_name);


            holder.canAdapContainer.setTag(canList.can_id);
            if (vote == null || vote.get(canList.can_id) == null || vote.get(canList.can_id).equals("0")) {
                holder.canAdapSwastik.setVisibility(View.INVISIBLE);

            } else {
                holder.canAdapSwastik.setVisibility(View.VISIBLE);

            }

            holder.canAdapContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View view) {
                    Log.i("getVIew", canList.can_id + "");
                    view.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            view.setClickable(true);
                        }
                    }, 1000);
                    view.setClickable(false);


                    if (holder.canAdapSwastik.getVisibility() == View.VISIBLE) {
                        holder.canAdapSwastik.setVisibility(View.INVISIBLE);
                        selectCount++;
                        selectCountTxt.setText(String.valueOf(selectCount));
                        postNameTxtView.setText(postName);
                        postKaLaagi.setText(getString(R.string.post_ma_kunai));
                        matadaanGarnuhos.setText(getString(R.string.matadaan_garnuhos));

                        vote.put(canList.can_id, "0");
                        databaseHelper.removeFromSummary(canList.can_id);


                    } else if (selectCount > 0) {

                        holder.canAdapSwastik.setVisibility(View.VISIBLE);
                        selectCount--;
                        selectCountTxt.setText(String.valueOf(selectCount));
                        postNameTxtView.setText(postName);
                        postKaLaagi.setText(getString(R.string.post_ma_kunai));
                        matadaanGarnuhos.setText(getString(R.string.matadaan_garnuhos));
                        vote.put(canList.can_id, "1");
                        databaseHelper.setSummary(postId, postName, canList.can_nep_name, canList.can_id);
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
                            showToast(holder.canAdapContainer);
                    }
                    preferences.edit().putInt(postName, selectCount).apply();

                    if (databaseHelper.summaryData() < 1) {
                        greenOn(false);
                    } else {
                        greenOn(true);
                    }

                }
            });
        }

        @Override
        public int getItemCount() {
            return canList.size();
        }

        @Override
        public int getItemViewType(int position) {
            return position;
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {

            ImageView canAdapImage;
            ImageView canAdapSwastik;
            LinearLayout canAdapContainer;
            TextView canAdapName;


            public MyViewHolder(View itemView) {
                super(itemView);


                canAdapContainer = itemView.findViewById(R.id.ballotPaper);
                canAdapImage = itemView.findViewById(R.id.candidateImage);
                canAdapSwastik = itemView.findViewById(R.id.candidateSwastik);
                canAdapName = itemView.findViewById(R.id.candidateName);


            }
        }


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
            speakSummary = speakSummary + " " + String.valueOf(position + 1) + " " + summaryList.getCanName() + " " + summaryList.getPostNepali() + "|";

            if (position == summaryLists.size() - 1) {
                textToSpeech.speak(getString(R.string.complete_vote), TextToSpeech.QUEUE_FLUSH, null);
            }
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
//        smdtManager.smdtSetExtrnalGpioValue(4, !onOff);
    }

    private void redOn(boolean onOff) {
//        smdtManager.smdtSetExtrnalGpioValue(3, !onOff);
    }


}
