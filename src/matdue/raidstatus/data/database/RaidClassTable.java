package matdue.raidstatus.data.database;

public class RaidClassTable implements RaidClassColumns {

	public static final String TABLE_NAME = "raidclass";
	
	public static final String SQL_CREATE =
		"CREATE TABLE " + TABLE_NAME + " (" +
		ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
		RAIDID + " INTEGER," +
		CLASSNAME + " TEXT," +
		COUNT + " INTEGER" +
		")";
	
	public static final String SQL_DROP = 
		"DROP TABLE IF EXISTS " + TABLE_NAME;
	
	public static final String STMT_CLEAR = 
		"DELETE FROM " + TABLE_NAME;
	
}
