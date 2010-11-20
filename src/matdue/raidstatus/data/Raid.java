package matdue.raidstatus.data;

import java.util.Collection;
import java.util.Date;

public class Raid {

	private long _id;
	
	private String name;
	private String icon;
	private String note;
	
	private Date start;
	private Date finish;
	private Date invite;
	private Date subscription;
	
	private int attendees;
	
	private Collection<RaidMember> raidMembers;
	private Collection<RaidClass> raidClasses;
	
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
	
	public String getIcon() {
		return icon;
	}
	
	public void setIcon(String icon) {
		this.icon = icon;
	}
	
	public String getNote() {
		return note;
	}
	
	public void setNote(String note) {
		this.note = note;
	}
	
	public Date getStart() {
		return start;
	}
	
	public void setStart(Date start) {
		this.start = start;
	}
	
	public Date getFinish() {
		return finish;
	}
	
	public void setFinish(Date finish) {
		this.finish = finish;
	}
	
	public Date getInvite() {
		return invite;
	}
	
	public void setInvite(Date invite) {
		this.invite = invite;
	}
	
	public Date getSubscription() {
		return subscription;
	}
	
	public void setSubscription(Date subscription) {
		this.subscription = subscription;
	}
	
	public int getAttendees() {
		return attendees;
	}
	
	public void setAttendees(int attendees) {
		this.attendees = attendees;
	}
	
	public Collection<RaidMember> getRaidMembers() {
		return raidMembers;
	}
	
	public void setRaidMembers(Collection<RaidMember> raidMembers) {
		this.raidMembers = raidMembers;
	}
	
	public Collection<RaidClass> getRaidClasses() {
		return raidClasses;
	}
	
	public void setRaidClasses(Collection<RaidClass> raidClasses) {
		this.raidClasses = raidClasses;
	}
	
}
