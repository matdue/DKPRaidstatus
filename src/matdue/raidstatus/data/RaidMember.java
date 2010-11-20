package matdue.raidstatus.data;

public class RaidMember {

	public static final int SUBSCRIPTION_CONFIRMED = 1;
	public static final int SUBSCRIPTION_SIGNED = 2;
	public static final int SUBSCRIPTION_UNSIGNED = 3;
	public static final int SUBSCRIPTION_BACKUP = 4;
	public static final int SUBSCRIPTION_NOT_SIGNED = 5;

	private long _id;
	
	private Player player;
	private int subscribed;
	private String note;
	private String role;
	
	public long get_id() {
		return _id;
	}
	
	public void set_id(long id) {
		_id = id;
	}
	
	public Player getPlayer() {
		return player;
	}
	
	public void setPlayer(Player player) {
		this.player = player;
	}
	
	public int getSubscribed() {
		return subscribed;
	}
	
	public void setSubscribed(int subscribed) {
		this.subscribed = subscribed;
	}
	
	public String getNote() {
		return note;
	}
	
	public void setNote(String note) {
		this.note = note;
	}
	
	public String getRole() {
		return role;
	}
	
	public void setRole(String role) {
		this.role = role;
	}
	
}
