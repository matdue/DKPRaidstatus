package matdue.raidstatus.data;

public class RaidClass {
	
	private long _id;
	
	private String className;
	private int count;
	
	public long get_id() {
		return _id;
	}
	
	public void set_id(long id) {
		_id = id;
	}
	
	public String getClassName() {
		return className;
	}
	
	public void setClassName(String className) {
		this.className = className;
	}
	
	public int getCount() {
		return count;
	}
	
	public void setCount(int count) {
		this.count = count;
	}

}
