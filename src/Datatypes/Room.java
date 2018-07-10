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
	
	public Room(Room input){
		this.name = input.getName();
		this.days = input.days;
		this.slots = input.slots;
		this.used = new Course[days][slots];
		for(int day = 0; day < days; day++){
			for(int sl = 0; sl < slots; sl++){
				if(input.used[day][sl]!= null) {
					used[day][sl] = new Course(input.used[day][sl]);
				}
				
			}
		}
		this.capacity = input.capacity;
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
	
	public void addCombo(Combo c){
		for(int i =0 ; i < c.getSize();i++){
			used[c.getFirstSlot().getDay()][c.getFirstSlot().getSlot()+i] = c.getCourse();
		}
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
		try{
			for(int i = 0; i < c.getSlots(); i++){
				this.used[t.getDay()][t.getSlot()+i] = c;			
			}
		} catch (Exception e){
			System.out.println("Exception at Room class " + this.name);
			System.out.println(c.toString() );
			System.out.println(t.toString());
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
		Room other = (Room) obj;
		if(other.name.equals(this.name)) return true;
		return false;
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
