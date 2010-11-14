package matdue.raidstatus.data.database;

public class RaidTable implements RaidColumns {

	public static final String TABLE_NAME = "raid";
	
	public static final String SQL_CREATE =
		"CREATE TABLE " + TABLE_NAME + " (" +
		ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
		NAME + " TEXT," +
		ICON + " TEXT," +
		NOTE + " TEXT," +
		START + " INTEGER," +
		FINISH + " INTEGER," +
		INVITE + " INTEGER," +
		SUBSCRIPTION + " INTEGER," +
		ATTENDEES + " INTEGER" +
		")";
	
	public static final String SQL_DROP = 
		"DROP TABLE IF EXISTS " + TABLE_NAME;
	
	public static final String STMT_CLEAR = 
		"DELETE FROM " + TABLE_NAME;
	
}
