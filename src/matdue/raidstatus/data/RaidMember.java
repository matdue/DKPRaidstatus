package matdue.raidstatus.data;

public class RaidMember {

	public static final int SUBSCRIPTION_CONFIRMED = 1;
	public static final int SUBSCRIPTION_SIGNED = 2;
	public static final int SUBSCRIPTION_UNSIGNED = 3;
	public static final int SUBSCRIPTION_BACKUP = 4;
	public static final int SUBSCRIPTION_NOT_SIGNED = 5;

	// No getters and setters
	public long _id;
	
	public Player player;
	public int subscribed;
	public String note;
	public String role;
	
}
