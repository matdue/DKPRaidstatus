package matdue.raidstatus.data.database;

import java.util.Collection;
import java.util.Date;

import matdue.raidstatus.data.Player;
import matdue.raidstatus.data.Raid;
import matdue.raidstatus.data.RaidClass;
import matdue.raidstatus.data.RaidMember;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils.InsertHelper;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class RaidDatabase extends SQLiteOpenHelper {

	private static final String DATABASE_NAME = "raid.db";
	private static final int DATABASE_VERSION = 1;
	
	public RaidDatabase(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(PlayerTable.SQL_CREATE);
		db.execSQL(RaidTable.SQL_CREATE);
		db.execSQL(RaidClassTable.SQL_CREATE);
		db.execSQL(RaidMemberTable.SQL_CREATE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL(RaidMemberTable.SQL_DROP);
		db.execSQL(RaidClassTable.SQL_DROP);
		db.execSQL(RaidTable.SQL_DROP);
		db.execSQL(PlayerTable.SQL_DROP);
		onCreate(db);
	}
	
	public void deleteAllData() {
		SQLiteDatabase db = getWritableDatabase();
		try {
			db.execSQL(RaidMemberTable.STMT_CLEAR);
			db.execSQL(RaidClassTable.STMT_CLEAR);
			db.execSQL(RaidTable.STMT_CLEAR);
			db.execSQL(PlayerTable.STMT_CLEAR);
		} finally {
			db.close();
		}
	}

	/**
	 * Stores all players in database and sets property "_id" to
	 * primary key of that entity in database.
	 * 
	 * @param players Collection of Player entity objects
	 */
	public void insertPlayers(Collection<Player> players) {
		SQLiteDatabase db = getWritableDatabase();
		InsertHelper insertHelper = new InsertHelper(db, PlayerTable.TABLE_NAME);
		try {
			db.beginTransaction();
			for (Player player : players) {
				ContentValues values = new ContentValues();
				values.put(PlayerColumns.NAME, player.name);
				values.put(PlayerColumns.CLASSNAME, player.className);
				values.put(PlayerColumns.CURRENTDKP, player.currentDkp.doubleValue());
				player._id = insertHelper.insert(values);
			}
			db.setTransactionSuccessful();
			db.endTransaction();
		} finally {
			insertHelper.close();
			db.close();
		}
	}
	
	public void insertRaids(Collection<Raid> raids) {
		SQLiteDatabase db = getWritableDatabase();
		InsertHelper insertHelper = new InsertHelper(db, RaidTable.TABLE_NAME);
		try {
			db.beginTransaction();
			for (Raid raid : raids) {
				ContentValues values = new ContentValues();
				values.put(RaidColumns.NAME, raid.name);
				values.put(RaidColumns.ICON, raid.icon);
				values.put(RaidColumns.NOTE, raid.note);
				values.put(RaidColumns.START, raid.start.getTime() / 1000);
				values.put(RaidColumns.FINISH, raid.finish.getTime() / 1000);
				values.put(RaidColumns.INVITE, raid.invite.getTime() / 1000);
				values.put(RaidColumns.SUBSCRIPTION, raid.subscription.getTime() / 1000);
				values.put(RaidColumns.ATTENDEES, raid.attendees);
				raid._id = insertHelper.insert(values);

				insertRaidClasses(db, raid.raidClasses, raid._id);
				insertRaidMembers(db, raid.raidMembers, raid._id);
			}
			db.setTransactionSuccessful();
			db.endTransaction();
		} finally {
			insertHelper.close();
			db.close();
		}
	}
	
	public void insertRaidClasses(SQLiteDatabase db, Collection<RaidClass> raidClasses, long raidID) {
		InsertHelper insertHelper = new InsertHelper(db, RaidClassTable.TABLE_NAME);
		try {
			for (RaidClass raidClass : raidClasses) {
				ContentValues values = new ContentValues();
				values.put(RaidClassColumns.RAIDID, raidID);
				values.put(RaidClassColumns.CLASSNAME, raidClass.className);
				values.put(RaidClassColumns.COUNT, raidClass.count);
				raidClass._id = insertHelper.insert(values);
			}
		} finally {
			insertHelper.close();
		}
	}
	
	public void insertRaidMembers(SQLiteDatabase db, Collection<RaidMember> raidMembers, long raidID) {
		InsertHelper insertHelper = new InsertHelper(db, RaidMemberTable.TABLE_NAME);
		try {
			for (RaidMember raidMember : raidMembers) {
				ContentValues values = new ContentValues();
				values.put(RaidMemberColumns.SUBSCRIBED, raidMember.subscribed);
				values.put(RaidMemberColumns.PLAYERID, raidMember.player._id);
				values.put(RaidMemberColumns.RAIDID, raidID);
				values.put(RaidMemberColumns.NOTE, raidMember.note);
				values.put(RaidMemberColumns.ROLE, raidMember.role);
				raidMember._id = insertHelper.insert(values);
			}
		} finally {
			insertHelper.close();
		}
	}
	
	public Raid loadRaid() {
		SQLiteDatabase db = getReadableDatabase();
		Raid result = null;
		Cursor cursor = db.query(RaidTable.TABLE_NAME, 
				new String[] { 
					RaidColumns.NAME, 
					RaidColumns.START }, 
				null, null, null, null, RaidColumns.START, "1");
		if (cursor.moveToNext()) {
			result = new Raid();
			result.name = cursor.getString(0);
			result.start = new Date(1000L * cursor.getInt(1));
		}
		cursor.close();
		db.close();
		
		return result;
	}
}
