package matdue.raidstatus.data;

import java.util.Collection;
import java.util.Date;

public class Raid {

	// No getters and setters
	public long _id;
	
	public String name;
	public String icon;
	public String note;
	
	public Date start;
	public Date finish;
	public Date invite;
	public Date subscription;
	
	public int attendees;
	
	public Collection<RaidMember> raidMembers;
	public Collection<RaidClass> raidClasses;
	
}
