// Copyright (C) 2015 Peter Robinson
package com.nelladragon.scab.data;

import java.nio.charset.Charset;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Base64;
import android.util.Log;

import com.nelladragon.scab.users.UserConfig;
import com.nelladragon.scab.users.UserProfile;


/**
 * Database interaction class.
 *
 * Note all strings saved to the database are base 64 encoded as a way of not having to
 * escape characters.
 *
 */
public class DatabaseHandler extends SQLiteOpenHelper {
	private static final String LOG_TAG = DatabaseHandler.class.getSimpleName();

	private static final Charset UTF8 = Charset.forName("UTF8");

	private static final long WITH_ATTITUDE = 0x01L;
	private static final long WITH_HUMOUR = 0x02L;
	private static final long WITH_THOUGHT = 0x04L;


	private static final String DATABASE_NAME = "showertimer.db";
	private static final int DATABASE_VERSION = 2;
	
	private static final String _ID = "_id";

	// Column names
	public static final String USERID = "uid";		// User's UUID
	public static final String NAME = "name";		// User name
	public static final String PROFILE = "profile";	// Profile description
	public static final String PHOTO = "photo";		// Photo id
	public static final String PHOTOD = "photod";	// Photo description
	public static final String TIME = "time";		// Length of shower
	public static final String LANG = "lang";		// Language
	public static final String ATTR = "attr";		// Attributes

	//int time, boolean attitude, boolean humour, boolean thoughts, int language
	//UUID id, Context String userName, String profileDesc, int drawableid, String photoDesc

	private static final String TABLE_NAME_USERS = "users";
	private static final String CREATE_TABLE_SCORES =
			"CREATE TABLE " + TABLE_NAME_USERS
			+ " (" + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
			+ USERID + " VARCHAR(50),"
			+ NAME + " VARCHAR(300), "
			+ PROFILE + " VARCHAR(300), "
			+ PHOTO + " INTEGER, "
			+ PHOTOD + " VARCHAR(50), "
			+ TIME + " INTEGER, "
			+ LANG + " INTEGER, "
			+ ATTR + " INTEGER);";
	private static final String DROP_TABLE_USERS = "DROP TABLE IF EXISTS " + TABLE_NAME_USERS;
	private static final int NAME_MAX_LEN = 300; // 50 characters, each character could be 4 bytes, base64 encode = 266 bytes

	Context appContext;

	public DatabaseHandler(Context context) {
		super(context.getApplicationContext(), DATABASE_NAME, null, DATABASE_VERSION);
		this.appContext = context.getApplicationContext();
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		Log.i(LOG_TAG, "Creating database");
		db.execSQL(CREATE_TABLE_SCORES);
	}

	// At the moment, upgrade means loose everything!
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.i(LOG_TAG, "Upgrade DB from V: "+ oldVersion + " to V:" + newVersion + ".");
		db.execSQL(DROP_TABLE_USERS);
		onCreate(db);
	}
	






	/**
	 * Get all information for a user.
	 * 
	 */
	public UserProfile getUser(UUID userId) {
        String userIdStr = userId.toString();
		SQLiteDatabase db = getWritableDatabase();
		String SELECT_QUERY = 
				"SELECT * FROM " + TABLE_NAME_USERS
                + " WHERE " + USERID + " =\"" + userIdStr + "\"";
		Cursor dbCursor = db.rawQuery(SELECT_QUERY, null);

		UserProfile user = null;
		
		if (dbCursor.getCount() > 0) {
			dbCursor.moveToFirst();
			user = loadProfile(dbCursor);
		}
		dbCursor.close();
		Log.d(LOG_TAG, "Loaded user: " + userIdStr + ": found: " + (user != null));
		return user;
	}


    /**
     * Get all users.
     *
     * @return
     */
	public Map<UUID, UserProfile> getAllUsers() {
		SQLiteDatabase db = getWritableDatabase();
		String SELECT_QUERY =
				"SELECT * FROM " + TABLE_NAME_USERS;
		Cursor dbCursor = db.rawQuery(SELECT_QUERY, null);

		Map<UUID, UserProfile> allUsers = new TreeMap<>();

		int rowsInCursor = dbCursor.getCount();
        int numRowsLoaded = 0;
        if (rowsInCursor > 0){
            dbCursor.moveToFirst();
            while ((!dbCursor.isAfterLast()) && numRowsLoaded < rowsInCursor) {
                numRowsLoaded++;
				UserProfile user = loadProfile(dbCursor);
                allUsers.put(user.getId(), user);
                dbCursor.moveToNext();
            }
        }
        dbCursor.close();
        Log.d(LOG_TAG, "Loaded " + numRowsLoaded + " users.");
		return allUsers;

	}

	public void deleteUser(UUID userId) {
		String userIdStr = userId.toString();
		SQLiteDatabase db = getWritableDatabase();
		db.delete(TABLE_NAME_USERS, USERID + "=\"" + userIdStr + "\"", null);
	}

	public void addOrUpdateUser(UserProfile profile) {
		UUID userId = profile.getId();
		String userIdStr = userId.toString();
		boolean update = (getUser(userId) != null);

		UserConfig conf = profile.getConfig();
		long attr = 0;
		attr |= (conf.isWithAttitude()) ? WITH_ATTITUDE : 0;
		attr |= (conf.isWithHumour()) ? WITH_HUMOUR : 0;
		attr |= (conf.isWithThoughts()) ? WITH_THOUGHT : 0;

		String profileName = profile.getProfileName();
		if (profileName == null) {
			profileName = "";
		}
		byte[] profileNameBytes = profileName.getBytes(UTF8);
		String name = Base64.encodeToString(profileNameBytes, Base64.DEFAULT);

		String profileDescription = profile.getProfileDescription();
		if (profileDescription == null) {
			profileDescription = "";
		}
		byte[] profileDescriptionBytes = profileDescription.getBytes(UTF8);
		String desc = Base64.encodeToString(profileDescriptionBytes, Base64.DEFAULT);

		String photoDescription = profile.getPhotoDesc();
		if (photoDescription == null) {
			photoDescription = "";
		}
		byte[] photoDescriptionBytes = photoDescription.getBytes(UTF8);
		String photoDesc = Base64.encodeToString(photoDescriptionBytes, Base64.DEFAULT);

		ContentValues dataToInsert = new ContentValues();
		dataToInsert.put(USERID, userIdStr);
		dataToInsert.put(NAME, name);
		dataToInsert.put(PROFILE, desc);
		dataToInsert.put(PHOTO, profile.getPhotoId());
		dataToInsert.put(PHOTOD, photoDesc);
		dataToInsert.put(TIME, conf.getShowerLenInSeconds());
		dataToInsert.put(LANG, conf.getLanguage());
		dataToInsert.put(ATTR, attr);
		SQLiteDatabase db = getWritableDatabase();

		if (update) {
			// Update
			String WHERE = USERID + "=\"" + userIdStr + "\"";
			db.update(TABLE_NAME_USERS, dataToInsert, WHERE, null);
			Log.d(LOG_TAG, "Update comp: " + userIdStr);
		} else{
			// Insert
			dataToInsert.put(USERID, userIdStr);
			db.insert(TABLE_NAME_USERS, null, dataToInsert);
			Log.d(LOG_TAG, "Insert comp: " + userIdStr);
		}
	}



	private UserProfile loadProfile(Cursor dbCursor) {
		String userid = dbCursor.getString(1);
		String nameB64 = dbCursor.getString(2);
		String profileB64 = dbCursor.getString(3);
		int photoId = dbCursor.getInt(4);
		String photoDescB64 = dbCursor.getString(5);
		int time = dbCursor.getInt(6);
		int lang = dbCursor.getInt(7);
		long attr = dbCursor.getLong(8);

		UUID uId = UUID.fromString(userid);
		boolean withAttitude = (attr & WITH_ATTITUDE) != 0;
		boolean withHumour = (attr & WITH_HUMOUR) != 0;
		boolean withThought = (attr & WITH_THOUGHT) != 0;

		byte[] nameBytes = Base64.decode(nameB64, Base64.DEFAULT);
		String name = new String(nameBytes, UTF8);
		byte[] progileBytes = Base64.decode(profileB64, Base64.DEFAULT);
		String profile = new String(progileBytes, UTF8);
		byte[] photoDescBytes = Base64.decode(photoDescB64, Base64.DEFAULT);
		String photoDesc = new String(photoDescBytes, UTF8);

		UserConfig conf = new UserConfig(time, withAttitude, withHumour, withThought, lang);
		return new UserProfile(uId, this.appContext, name, profile, photoId, photoDesc, conf);
	}

}
