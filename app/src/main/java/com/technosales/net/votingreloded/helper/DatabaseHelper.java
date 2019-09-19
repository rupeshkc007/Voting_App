package com.technosales.net.votingreloded.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.technosales.net.votingreloded.pojoModels.CandidatesList;
import com.technosales.net.votingreloded.pojoModels.PostList;
import com.technosales.net.votingreloded.pojoModels.SummaryList;
import com.technosales.net.votingreloded.utils.GeneralUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static String DATABASE_NAME = "votingDb";
    public static int DB_VERSION = 2;

    public static String CANDIDATES_TABLE = "candidates";
    public static String CANDIDATES_NAME_NEPALI = "can_nep_name";
    public static String CANDIDATES_NAME_ENGLISH = "can_eng_name";
    public static String CANDIDATES_POST_ID = "can_post_id";
    public static String CANDIDATES_ID = "can_id";
    public static String CANDIDATES_VOTE = "votes";


    public static String POST_TABLE = "posts";
    public static String POST_ID = "post_id";
    public static String POST_NAME_NEPALI = "post_nep_name";
    public static String POST_NAME_ENGLISH = "post_eng_name";
    public static String POST_COUNT = "post_count";


    public static String SUMMARY_TABLE = "summary";
    public static String SUMMARY_POST_ID = "sum_post_id";
    public static String SUMMARY_POST_NEPALI_NAME = "sum_post_nepali";
    public static String SUMMARY_POST_ENGLISH_NAME = "sum_post_english";
    public static String SUMMARY_CAN_NEPALI_NAME = "sum_can_nepali";
    public static String SUMMARY_CAN_ENGLISH_NAME = "sum_can_english";
    public static String SUMMARY_CAN_ID = "sum_can_id";


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        createTables(db);


    }

    private void createTables(SQLiteDatabase db) {
        db.execSQL("create table " + CANDIDATES_TABLE +
                "(id integer primary key autoincrement," +
                CANDIDATES_NAME_NEPALI + " text," +
                CANDIDATES_NAME_ENGLISH + " text," +
                CANDIDATES_POST_ID + " text," +
                CANDIDATES_ID + " text," +
                CANDIDATES_VOTE + " integer" +
                ")"
        );
        db.execSQL("create table " + POST_TABLE +
                "(id integer primary key autoincrement," +
                POST_ID + " integer," +
                POST_NAME_NEPALI + " text," +
                POST_NAME_ENGLISH + " text," +
                POST_COUNT + " integer" +
                ")"
        );
        db.execSQL("create table " + SUMMARY_TABLE +
                "(id integer primary key autoincrement," +
                SUMMARY_POST_ID + " integer," +
                SUMMARY_POST_NEPALI_NAME + " text," +
                SUMMARY_POST_ENGLISH_NAME + " text," +
                SUMMARY_CAN_NEPALI_NAME + " text," +
                SUMMARY_CAN_ENGLISH_NAME + " text," +
                SUMMARY_CAN_ID + " text" +
                ")"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS candidates");
        db.execSQL("DROP TABLE IF EXISTS posts");
        db.execSQL("DROP TABLE IF EXISTS summary");
        createTables(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS candidates");
        db.execSQL("DROP TABLE IF EXISTS posts");
        db.execSQL("DROP TABLE IF EXISTS summary");
        createTables(db);
    }
    /*inserting Queries*/

    public void insertCandidates(int post_id, String can_id, String nepaliName, String englishName, Integer vote) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(CANDIDATES_POST_ID, post_id);
        contentValues.put(CANDIDATES_ID, can_id);
        contentValues.put(CANDIDATES_NAME_NEPALI, nepaliName);
        contentValues.put(CANDIDATES_NAME_ENGLISH, englishName);
        contentValues.put(CANDIDATES_VOTE, vote);

        db.insert(CANDIDATES_TABLE, null, contentValues);
        Log.i("InsertingData", "candidates" + contentValues.toString());


    }

    public void insertPosts(int postid, String nameNepali, String nameng, int count) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(POST_ID, postid);
        contentValues.put(POST_NAME_NEPALI, nameNepali);
        contentValues.put(POST_NAME_ENGLISH, nameng);
        contentValues.put(POST_COUNT, count);
        db.insert(POST_TABLE, null, contentValues);
        Log.i("InsertingData", "posts" + contentValues.toString());
    }

    public void setSummary(int post_id, String postNepali, String postEnglish, String canNepali, String canEnglish, String canId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(SUMMARY_POST_ID, post_id);
        contentValues.put(SUMMARY_POST_NEPALI_NAME, postNepali);
        contentValues.put(SUMMARY_POST_ENGLISH_NAME, postEnglish);
        contentValues.put(SUMMARY_CAN_NEPALI_NAME, canNepali);
        contentValues.put(SUMMARY_CAN_ENGLISH_NAME, canEnglish);
        contentValues.put(SUMMARY_CAN_ID, canId);

        db.insert(SUMMARY_TABLE, null, contentValues);
        Log.i("InsertingData", "Summary" + contentValues.toString());


    }


    public void clearSummary() {
        String sql = "DELETE FROM " + SUMMARY_TABLE;
        getWritableDatabase().execSQL(sql);
    }

    public void removeFromSummary(String canId) {
        String sql = "DELETE  FROM " + SUMMARY_TABLE + " WHERE " + SUMMARY_CAN_ID + " ='" + canId + "'";
        getWritableDatabase().execSQL(sql);
        Log.i("InsertingData", "RemovingCan-" + canId);
    }

    public void removeFromSummaryPost(int postId) {
        String sql = "DELETE  FROM " + SUMMARY_TABLE + " WHERE " + SUMMARY_POST_ID + " =" + postId;
        getWritableDatabase().execSQL(sql);
        Log.i("InsertingData", "RemovingPost-" + postId);
    }

    public void clearCandidates() {
        String sql = "DELETE  FROM " + CANDIDATES_TABLE;
        getWritableDatabase().execSQL(sql);
    }

    public void clearPosts() {
        String sql = "DELETE  FROM " + POST_TABLE;
        getWritableDatabase().execSQL(sql);
    }

    /*getting no of data*/
    public int postsData() {
        SQLiteDatabase db = this.getReadableDatabase();
        int numRows = (int) DatabaseUtils.queryNumEntries(db, POST_TABLE);
        return numRows;
    }

    public int candidatesData() {
        SQLiteDatabase db = this.getReadableDatabase();
        int numRows = (int) DatabaseUtils.queryNumEntries(db, CANDIDATES_TABLE);
        return numRows;
    }

    public int summaryData() {
        SQLiteDatabase db = this.getReadableDatabase();
        int numRows = (int) DatabaseUtils.queryNumEntries(db, SUMMARY_TABLE);
        return numRows;
    }


    /*update Vote Count*/

    public void updateVoteCount(int count, String can_id) {

        String sql = "update " + CANDIDATES_TABLE + " set votes = votes+" + count + " where can_id ='" + can_id + "'";
        Log.i("sql", "sql:" + sql);
        getWritableDatabase().execSQL(sql);


    }


    /*getCandidatesListForPostId=?*/
    public List<CandidatesList> voteProcess(int postId) {
        String sql = "SELECT * FROM " + CANDIDATES_TABLE + " WHERE " + CANDIDATES_POST_ID + " =" + postId;
        Cursor c = getWritableDatabase().rawQuery(sql, null);

        ArrayList<CandidatesList> list = new ArrayList<CandidatesList>();
        while (c.moveToNext()) {
            CandidatesList candidatesPojo = new CandidatesList();
            candidatesPojo.can_post_id = (c.getInt(c.getColumnIndex(CANDIDATES_POST_ID)));
            candidatesPojo.can_id = (c.getString(c.getColumnIndex(CANDIDATES_ID)));
            candidatesPojo.can_eng_name = (c.getString(c.getColumnIndex(CANDIDATES_NAME_ENGLISH)));
            candidatesPojo.can_nep_name = (c.getString(c.getColumnIndex(CANDIDATES_NAME_NEPALI)));
            candidatesPojo.vote_count = (c.getInt(c.getColumnIndex(CANDIDATES_VOTE)));
            list.add(candidatesPojo);
        }
        c.close();
        Log.i("listszie", "" + list.size());
        return list;
    }

    /*get postname and max count*/
    public String getPost(int postId) {
        String data = "";
        String sql = "SELECT " + POST_NAME_NEPALI + " FROM " + POST_TABLE + " WHERE " + POST_ID + " =" + postId;

        Cursor c = getWritableDatabase().rawQuery(sql, null);
        while (c.moveToNext()) {
            data = c.getString(c.getColumnIndex(POST_NAME_NEPALI));
        }
        c.close();

        return data;
    }

    public String getPostEnglish(int postId) {
        String data = "";
        String sql = "SELECT " + POST_NAME_ENGLISH + " FROM " + POST_TABLE + " WHERE " + POST_ID + " =" + postId;

        Cursor c = getWritableDatabase().rawQuery(sql, null);
        while (c.moveToNext()) {
            data = c.getString(c.getColumnIndex(POST_NAME_ENGLISH));
        }
        c.close();

        return data;
    }

    public int getMax(int postId) {
        int data = 0;
        String sql = "SELECT " + POST_COUNT + " FROM " + POST_TABLE + " WHERE " + POST_ID + " =" + postId;

        Cursor c = getWritableDatabase().rawQuery(sql, null);
        while (c.moveToNext()) {
            data = c.getInt(c.getColumnIndex(POST_COUNT));
        }
        c.close();

        return data;
    }

    public List<SummaryList> summaryLists() {
        String sql = "SELECT * FROM " + SUMMARY_TABLE + " ORDER BY sum_post_id";
        Cursor c = getWritableDatabase().rawQuery(sql, null);

        ArrayList<SummaryList> list = new ArrayList<SummaryList>();
        while (c.moveToNext()) {
            SummaryList summaryList = new SummaryList();
            summaryList.setPostId(c.getInt(c.getColumnIndex(SUMMARY_POST_ID)));
            summaryList.setPostNepali(c.getString(c.getColumnIndex(SUMMARY_POST_NEPALI_NAME)));
            summaryList.setPostEnglish(c.getString(c.getColumnIndex(SUMMARY_POST_ENGLISH_NAME)));
            summaryList.setCanName(c.getString(c.getColumnIndex(SUMMARY_CAN_NEPALI_NAME)));
            summaryList.setCanNameEnglish(c.getString(c.getColumnIndex(SUMMARY_CAN_ENGLISH_NAME)));
            summaryList.setCanId(c.getString(c.getColumnIndex(SUMMARY_CAN_ID)));
            list.add(summaryList);
        }
        c.close();
        Log.i("summarySize", "" + list.size());
        return list;
    }

    public int summaryPostCount(int postId) {
        ArrayList<String> stringArrayList = new ArrayList<>();
        String sql = "SELECT * FROM " + SUMMARY_TABLE + " where " + SUMMARY_POST_ID + " =" + postId;
        Cursor c = getWritableDatabase().rawQuery(sql, null);

        while (c.moveToNext()) {
            stringArrayList.add(c.getString(c.getColumnIndex(SUMMARY_CAN_ID)));
        }
        c.close();
        return stringArrayList.size();
    }

    public int getPostMaxCount(int postId) {
        int count = 0;
        String sql = "SELECT " + POST_COUNT + " FROM " + POST_TABLE + " WHERE " + POST_ID + " =" + postId;
        Cursor c = getWritableDatabase().rawQuery(sql, null);

        while (c.moveToNext()) {
            count = (c.getInt(c.getColumnIndex(POST_COUNT)));
        }
        c.close();
        return count;

    }


    public List<PostList> postLists() {
        String sql = "SELECT * FROM " + POST_TABLE;
        Cursor c = getWritableDatabase().rawQuery(sql, null);
        ArrayList<PostList> lists = new ArrayList<>();
        while (c.moveToNext()) {
            PostList postList = new PostList();
            postList.postId = (c.getInt(c.getColumnIndex(POST_ID)));
            postList.postNepali = (c.getString(c.getColumnIndex(POST_NAME_NEPALI)));
            postList.postEnglish = (c.getString(c.getColumnIndex(POST_NAME_ENGLISH)));
            postList.postCount = (c.getInt(c.getColumnIndex(POST_COUNT)));
            lists.add(postList);
        }
        c.close();
        return lists;
    }

    public List<CandidatesList> resultProcess(int postId) {
        String sql = "SELECT * FROM " + CANDIDATES_TABLE + " WHERE " + CANDIDATES_POST_ID + " =" + postId /*+ " ORDER BY votes DESC"*/;
        Cursor c = getWritableDatabase().rawQuery(sql, null);

        ArrayList<CandidatesList> list = new ArrayList<CandidatesList>();
        while (c.moveToNext()) {
            CandidatesList candidatesPojo = new CandidatesList();
            candidatesPojo.can_post_id = (c.getInt(c.getColumnIndex(CANDIDATES_POST_ID)));
            candidatesPojo.can_id = (c.getString(c.getColumnIndex(CANDIDATES_ID)));
            candidatesPojo.can_eng_name = (c.getString(c.getColumnIndex(CANDIDATES_NAME_ENGLISH)));
            candidatesPojo.can_nep_name = (c.getString(c.getColumnIndex(CANDIDATES_NAME_NEPALI)));
            candidatesPojo.vote_count = (c.getInt(c.getColumnIndex(CANDIDATES_VOTE)));
            list.add(candidatesPojo);
        }
        c.close();
        Log.i("listszie", "" + list.size());
        return list;
    }

    /*getwritabletxtData*/
    public String getWriteData() {
        String sql = "SELECT * FROM " + CANDIDATES_TABLE;
        String data = "";
        Cursor c = getWritableDatabase().rawQuery(sql, null);
        while (c.moveToNext()) {
            data = data + c.getString(c.getColumnIndex(CANDIDATES_ID)) + "\t" +/* c.getString(c.getColumnIndex(CANDIDATES_NAME_NEPALI)) +*/ "\t" + c.getString(c.getColumnIndex(CANDIDATES_VOTE)) + "\n";
        }
        c.close();
        return data;

    }

    public void writeToFile(String device_id, String path) {
        String data = getWriteData();
        Log.i("Data", "Data:" + data);

        File txtFile = new File(path + "/" + device_id + ".txt");

        if (!txtFile.exists()) {
            try {
                txtFile.createNewFile();
                GeneralUtils.writeInTxt(txtFile, data);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            GeneralUtils.writeInTxt(txtFile, data);
        }

    }


}
