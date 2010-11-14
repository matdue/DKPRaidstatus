package matdue.raidstatus.data.database;

public class RaidMemberTable implements RaidMemberColumns {

	public static final String TABLE_NAME = "raidmember";
	
	public static final String SQL_CREATE =
		"CREATE TABLE " + TABLE_NAME + " (" +
		ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
		PLAYERID + " INTEGER," +
		RAIDID + " INTEGER," +
		SUBSCRIBED + " INTEGER," +
		NOTE + " TEXT," +
		ROLE + " TEXT" +
		")";
	
	public static final String SQL_DROP = 
		"DROP TABLE IF EXISTS " + TABLE_NAME;
	
	public static final String STMT_CLEAR = 
		"DELETE FROM " + TABLE_NAME;
	
}
