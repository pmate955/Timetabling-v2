package Datatypes;

import java.util.Arrays;

public class Room {
	private String name;
	private Course[][] used;
	private int capacity;
	private int days;
	private int slots;
	
	public Room(String name, int days, int times, int capacity){
		this.name = name;
		this.used = new Course[days][times];
		this.capacity = capacity;
		this.days = days;
		this.slots = times;
	}
	
	public String getName(){return this.name;}
	public int getCapacity(){return this.capacity;}
	public int getDays(){return this.days;}
	public int getSlots(){return this.slots;}
	public Course getCourseByPos(int day, int slot){return used[day][slot];};
	
	public boolean isUsed(TimeSlot t){
		return used[t.getDay()][t.getSlot()]!=null;
	}
	
	public void addCourse(Course c, TimeSlot t){
		this.used[t.getDay()][t.getSlot()] = c;		
	}
	
	public Course getCourse(TimeSlot t){
		if(t.getSlot()<0) return null;
		Course now = used[t.getDay()][t.getSlot()];
		if(now == null) return null; 
		for(int slot = t.getSlot();slot>=0;slot--){
			if(used[t.getDay()][slot].equals(now)){
				now = used[t.getDay()][slot];
			} else break;
		}
		return now;
	}
	
	public void deleteCourse(Course c,TimeSlot t){
		for(int i = 0; i < c.getSlots();i++){
			used[t.getDay()][t.getSlot()+i] = null;
		}
	}
	
	public void addFullCourse(Course c, TimeSlot t){
		for(int i = 0; i < c.getSlots(); i++){
			this.used[t.getDay()][t.getSlot()+i] = c;			
		}
	}

	public void clearRoom(){
		this.used = new Course[days][slots];
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + capacity;
		result = prime * result + days;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + slots;
		result = prime * result + Arrays.deepHashCode(used);
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
		if (days != other.days)
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (slots != other.slots)
			return false;
		if (!Arrays.deepEquals(used, other.used))
			return false;
		return true;
	}

	public void print(){
		System.out.println("Room: " + name);
		System.out.println("=============================================================");
		for(int slot = 0; slot < slots; slot++ ){
			for(int day = 0; day < days; day++){
				if(used[day][slot]==null) System.out.print("day: " + day + " slot: " + slot + " is empty || ");
				else System.out.print("day: " + day + " slot: " + slot + " " + used[day][slot].toString() + " || ");
			}
			System.out.println();
		}
		System.out.println("=============================================================");
	}
}
