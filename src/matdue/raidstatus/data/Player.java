package matdue.raidstatus.data;

import java.math.BigDecimal;

public class Player {
	
	private long _id;
	
	private String name;
	private String className;
	private BigDecimal currentDkp;
	
	public long get_id() {
		return _id;
	}
	
	public void set_id(long id) {
		_id = id;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getClassName() {
		return className;
	}
	
	public void setClassName(String className) {
		this.className = className;
	}
	
	public BigDecimal getCurrentDkp() {
		return currentDkp;
	}
	
	public void setCurrentDkp(BigDecimal currentDkp) {
		this.currentDkp = currentDkp;
	}
	
}
