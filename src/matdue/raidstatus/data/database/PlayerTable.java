package matdue.raidstatus.data.database;

public class PlayerTable implements PlayerColumns {

	public static final String TABLE_NAME = "player";
	
	public static final String SQL_CREATE =
		"CREATE TABLE " + TABLE_NAME + " (" +
		ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
		NAME + " TEXT," +
		CLASSNAME + " TEXT," +
		CURRENTDKP + " NUMERIC" +
		")";
	
	public static final String SQL_DROP = 
		"DROP TABLE IF EXISTS " + TABLE_NAME;
	
	public static final String STMT_CLEAR = 
		"DELETE FROM " + TABLE_NAME;
	
}
