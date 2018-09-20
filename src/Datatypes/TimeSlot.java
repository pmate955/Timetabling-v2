package Datatypes;

public class TimeSlot {
	private int day;
	private int slot;
	
	public TimeSlot(int day, int slot) {
		this.day = day;
		this.slot = slot;
	}
	
	public int getDay() {
		return day;
	}
	
	public void setDay(int day)
	{
		this.day = day;
	}
	
	public void setSlot(int slot){
		this.slot = slot;
	}
	
	public int getSlot() {
		return slot;
	}
	
	public String toString(){
		return "day: " + this.day + " slot: " + this.slot; 
	}
	
	public boolean equals(Object o){
		if(!(o instanceof TimeSlot)) return false;
		return (this.day == (((TimeSlot)o).getDay())) && (this.slot == ((TimeSlot)o).getSlot());
	}
	
	public int hashCode(){
		int hash = 17;
		hash = hash * 31 + this.day;
		hash = hash * 31 + this.slot;
		return hash;
	}
	
}
