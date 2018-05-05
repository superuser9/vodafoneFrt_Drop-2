package com.vodafone.frt.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.util.Log;

import com.vodafone.frt.models.PTRMessageModel;
import com.vodafone.frt.models.PTRUserModel;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ashutosh Kumar on 30-Jan-18.
 */

public class FRTChatDb extends SQLiteOpenHelper {

    private Context context;
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "chatManager";
    private static final String TABLE_USERS = "users";
    private static final String KEY_ID = "id";
    private static final String KEY_USER_ID = "userId";
    private static final String KEY_USER_NAME = "userName";
    private static final String KEY_FRIEND_ID = "friendId";
    private static final String TABLE_MESSAGES = "messages";
    private static final String KEY_MESSAGE_ID = "messageId";
    private static final String KEY_MESSAGE_FROM = "messageFrom";
    private static final String KEY_MESSAGE_TO = "messageTo";
    private static final String KEY_MESSAGE_TIME = "messageTime";
    private static final String KEY_MESSAGE_TYPE = "messageType";
    private static final String KEY_MESSAGE_TEXT = "messageText";

    public FRTChatDb(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_USERS_TABLE = "CREATE TABLE " + TABLE_USERS + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_USER_ID + " TEXT," + KEY_FRIEND_ID + " TEXT)";
        db.execSQL(CREATE_USERS_TABLE);

        String CREATE_MESSAGES_TABLE = "CREATE TABLE " + TABLE_MESSAGES + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_MESSAGE_ID + " TEXT,"
                + KEY_MESSAGE_FROM + " TEXT," + KEY_MESSAGE_TO + " TEXT," + KEY_MESSAGE_TIME + " TEXT," + KEY_MESSAGE_TYPE + " TEXT," + KEY_MESSAGE_TEXT + " TEXT)";
        db.execSQL(CREATE_MESSAGES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MESSAGES);

        // Create tables again
        onCreate(db);
    }

    //exporting database
    public void exportDB(Context context) {
        File sd = Environment.getExternalStorageDirectory();
        File data = Environment.getDataDirectory();
        FileChannel source;
        FileChannel destination;
        String currentDBPath = "/data/" + "com.vodafone.frt" + "/databases/" + DATABASE_NAME;
        String backupDBPath = DATABASE_NAME;
        File currentDB = new File(data, currentDBPath);
        File backupDB = new File(sd, backupDBPath);
        try {
            source = new FileInputStream(currentDB).getChannel();
            destination = new FileOutputStream(backupDB).getChannel();
            destination.transferFrom(source, 0, source.size());
            source.close();
            destination.close();
            Log.d("DB_EXPORT ", "DB Exported");
        } catch (IOException e) {
        }
    }

    /**
     * inset users list
     * @param frtUserModel
     * @author ashutosh kumar
     *
     */
    public void insertUser(List<PTRUserModel> frtUserModel) {

        SQLiteDatabase db = getWritableDatabase();
        for (PTRUserModel userData : frtUserModel) {
            ContentValues values = new ContentValues();
            values.put(KEY_USER_ID, userData.getUserId());
            values.put(KEY_USER_NAME, userData.getUserName());

            db.insert(TABLE_MESSAGES, null, values);
        }
        Log.d("DB_PATH ", "DB Created @ " + db.getPath());
        db.close();
    }

    /**
     * get users list
     * @return List<PTRUserModel>
     * @author ashutosh kumar
     */
    public List<PTRUserModel> getUserList() {
        List<PTRUserModel> userModelList = new ArrayList<>();
        Cursor cursor = null;
        try {
            SQLiteDatabase db = getWritableDatabase();
            cursor = db.rawQuery("Select * from " + TABLE_MESSAGES , null);
            PTRUserModel frtMessageModel;
            if (cursor.moveToFirst()) {
                do {
                    frtMessageModel = new PTRUserModel();
                    frtMessageModel.setUserId(cursor.getString(cursor.getColumnIndex(KEY_USER_ID)));
                    frtMessageModel.setUserName(cursor.getString(cursor.getColumnIndex(KEY_USER_NAME)));
                    userModelList.add(frtMessageModel);
                } while (cursor.moveToNext());
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return userModelList;
    }

    /**
     * insert message
     * @param frtMessageModel
     * @author ashutosh kumar
     */
    public void insertMessage(PTRMessageModel frtMessageModel) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_MESSAGE_ID, frtMessageModel.getMessageId());
        values.put(KEY_MESSAGE_FROM, frtMessageModel.getMessageFrom());
        values.put(KEY_MESSAGE_TO, frtMessageModel.getMessageTo());
        values.put(KEY_MESSAGE_TEXT, frtMessageModel.getMessageText());
        values.put(KEY_MESSAGE_TIME, frtMessageModel.getMessageTime());
        values.put(KEY_MESSAGE_TYPE, frtMessageModel.getMessageType());

        db.insert(TABLE_MESSAGES, null, values);
        Log.d("DB_PATH ", "DB Created @ " + db.getPath());
        db.close();
    }

    /**
     *
     * @param messageTo
     * @param messageFrom
     * @return List<PTRMessageModel>
     * @author ashutosh kumar
     */
    public List<PTRMessageModel> getMessages(String messageTo, String messageFrom) {
        List<PTRMessageModel> messageModels = new ArrayList<>();
        Cursor cursor = null;
        try {
            SQLiteDatabase db = getWritableDatabase();
            cursor = db.rawQuery("Select * from " + TABLE_MESSAGES + " where " + KEY_MESSAGE_FROM + " = '" + messageTo + "' and " + KEY_MESSAGE_TO + " = '" + messageFrom  + "'", null);
            PTRMessageModel frtMessageModel1;
            if (cursor.moveToFirst()) {
                do {
                    frtMessageModel1 = new PTRMessageModel();
                    frtMessageModel1.setMessageId(cursor.getInt(cursor.getColumnIndex(KEY_MESSAGE_ID)));
                    frtMessageModel1.setMessageFrom(cursor.getInt(cursor.getColumnIndex(KEY_MESSAGE_FROM)));
                    frtMessageModel1.setMessageTo(cursor.getInt(cursor.getColumnIndex(KEY_MESSAGE_TO)));
                    frtMessageModel1.setMessageTime(cursor.getString(cursor.getColumnIndex(KEY_MESSAGE_TIME)));
                    frtMessageModel1.setMessageType(cursor.getString(cursor.getColumnIndex(KEY_MESSAGE_TYPE)));
                    frtMessageModel1.setMessageText(cursor.getString(cursor.getColumnIndex(KEY_MESSAGE_TEXT)));
                    messageModels.add(frtMessageModel1);
                } while (cursor.moveToNext());
            }
            cursor = db.rawQuery("Select * from " + TABLE_MESSAGES + " where " + KEY_MESSAGE_FROM + " = '" + messageFrom + "' and " + KEY_MESSAGE_TO + " = '" + messageTo + "'", null);
            PTRMessageModel frtMessageModel2;
            if (cursor.moveToFirst()) {
                do {
                    frtMessageModel2 = new PTRMessageModel();
                    frtMessageModel2.setMessageId(cursor.getInt(cursor.getColumnIndex(KEY_MESSAGE_ID)));
                    frtMessageModel2.setMessageFrom(cursor.getInt(cursor.getColumnIndex(KEY_MESSAGE_FROM)));
                    frtMessageModel2.setMessageTo(cursor.getInt(cursor.getColumnIndex(KEY_MESSAGE_TO)));
                    frtMessageModel2.setMessageTime(cursor.getString(cursor.getColumnIndex(KEY_MESSAGE_TIME)));
                    frtMessageModel2.setMessageType(cursor.getString(cursor.getColumnIndex(KEY_MESSAGE_TYPE)));
                    frtMessageModel2.setMessageText(cursor.getString(cursor.getColumnIndex(KEY_MESSAGE_TEXT)));
                    messageModels.add(frtMessageModel2);
                } while (cursor.moveToNext());
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return messageModels;
    }
}
