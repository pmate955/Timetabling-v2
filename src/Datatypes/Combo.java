package Datatypes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Combo {		//Represents a combination of TimeSlot, Course and Room {later teacher}
	
	private Course c;
	private List<TimeSlot> t;
	private Room r;
	
	public Combo(Course c, TimeSlot start, Room r){
		this.c = c;
		this.r = r;
		this.t = new ArrayList<TimeSlot>();
		for(int i = 0; i < c.getSlots();i++){
			t.add(new TimeSlot(start.getDay(),start.getSlot()+i));
		}
	}
	
	public Combo(int size, TimeSlot start, Room r){
		this.c = null;
		this.r = r;
		this.t = new ArrayList<TimeSlot>();
		for(int i = 0; i < size;i++){
			t.add(new TimeSlot(start.getDay(),start.getSlot()+i));
		}
	}
	
	public Course getCourse(){
		return this.c;
	}

	public List<TimeSlot> getSlotList() {
		return t;
	}

	public boolean contains(List<TimeSlot> input){
		return !Collections.disjoint(t, input);
	}
	
	public Room getR() {
		return r;
	}
	
	public void setC(Course c) {
		this.c = c;
	}

	public TimeSlot getFirstSlot(){
		return this.t.get(0);
	}
	
	public int getSize(){
		return t.size();
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((c == null) ? 0 : c.hashCode());
		result = prime * result + ((r == null) ? 0 : r.hashCode());
		result = prime * result + ((t == null) ? 0 : t.hashCode());
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
		Combo other = (Combo) obj;
		if (c == null) {
			if (other.c != null)
				return false;
		} else if (!c.equals(other.c))
			return false;
		if (r == null) {
			if (other.r != null)
				return false;
		} else if (!r.equals(other.r))
			return false;
		if (t == null) {
			if (other.t != null)
				return false;
		} else if (!t.equals(other.t))
			return false;
		return true;
	}

	public void setR(Room r) {
		this.r = r;
	}

	public void print(){
		System.out.println(t.toString() + " | " + (c==null?"_":c.toString()) + " " + r.getName() + " " + t.size());
	}
	
	public void setFixed(){
		this.c.setFixed();
	}
	
	
}
