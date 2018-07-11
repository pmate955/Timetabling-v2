package Datatypes;

import java.util.ArrayList;
import java.util.List;

public class Room {
	private String name;
	private int capacity;
	private int days;
	private int slots;
	private List<Combo> courses;
	
	public Room(String name, int days, int times, int capacity){
		this.name = name;
		this.courses = new ArrayList<Combo>();
		this.capacity = capacity;
		this.days = days;
		this.slots = times;
	}
	
	
	public String getName(){return this.name;}
	public int getCapacity(){return this.capacity;}
	public int getDays(){return this.days;}
	public int getSlots(){return this.slots;}
	
	public Course getCourseByPos(int day, int slot){
		TimeSlot t = new TimeSlot(day,slot);
		for(Combo c : courses){
			if(c.getSlotList().contains(t)) return c.getCourse();
		}
		return null;
	};
	
	public boolean isUsed(TimeSlot t){
		for(Combo c : courses){
			if(c.getSlotList().contains(t)) return true;
		}
		return false;
	}
	
	public void addCombo(Combo c){
		courses.add(c);
	}


	public void clearRoom(){
		courses.clear();
	}
	
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + capacity;
		result = prime * result + ((courses == null) ? 0 : courses.hashCode());
		result = prime * result + days;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + slots;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Room other = (Room) obj;
		if (capacity != other.capacity)
			return false;
		if (courses == null) {
			if (other.courses != null)
				return false;
		} else if (!courses.equals(other.courses))
			return false;
		if (days != other.days)
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (slots != other.slots)
			return false;
		return true;
	}

	public void print(){
		System.out.println("Room: " + name);
		System.out.println("=============================================================");
		for(int slot = 0; slot < slots; slot++ ){
			for(int day = 0; day < days; day++){
				if(this.getCourseByPos(day, slot)==null) System.out.print("day: " + day + " slot: " + slot + " is empty || ");
				else System.out.print("day: " + day + " slot: " + slot + " " + this.getCourseByPos(day, slot).toString() + " || ");
			}
			System.out.println();
		}
		System.out.println("=============================================================");
	}
}
