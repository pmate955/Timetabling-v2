package Datatypes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Combo {		//Represents a combination of TimeSlot, Course and Room {later teacher}
	
	public Course c;
	public List<TimeSlot> t;
	public int roomIndex;
	public String roomName;
	
	public Combo(Course c, TimeSlot start, int roomIndex, String roomName){
		this.c = c;
		this.t = new ArrayList<TimeSlot>();
		for(int i = 0; i < c.getSlots();i++){
			t.add(new TimeSlot(start.getDay(),start.getSlot()+i));
		}
		this.roomIndex = roomIndex;
		this.roomName = roomName;
	}
	
	public Combo(int size, TimeSlot start, int roomIndex, String roomName){
		this.c = null;
		this.t = new ArrayList<TimeSlot>();
		for(int i = 0; i < size;i++){
			t.add(new TimeSlot(start.getDay(),start.getSlot()+i));
		}
		this.roomIndex = roomIndex;
		this.roomName = roomName;
	}
	
	public Course getCourse(){
		return this.c;
	}

	public boolean hasConflict(Combo input){
		boolean out = false;
		if(this.roomIndex==input.roomIndex){
			if(this.contains(input.getSlotList())) return true;
		}
		return out;
	}
	
	public List<TimeSlot> getSlotList() {
		return t;
	}
	
	public void setList(List<TimeSlot> t){
		this.t.clear();
		this.t.addAll(t);
	}

	public boolean contains(List<TimeSlot> input){
		return !Collections.disjoint(t, input);
	}
	
	
	public void setC(Course c) {
		this.c = c;
	}
	
	public TimeSlot getFirstSlot(){
		return this.t.get(0);
	}
	
	public TimeSlot getLastSlot(){
		return this.t.get(t.size()-1);
	}
	
	public List<TimeSlot> getFirstNSlot(int n){
		return t.subList(0, n-1);
	}
	
	public List<TimeSlot> getLeftSlots(int from){
		return t.subList(from, t.size()-1);
	}
	
	public int getSize(){
		return t.size();
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((c == null) ? 0 : c.hashCode());
		result = prime * result + roomIndex;
		result = prime * result + ((roomName == null) ? 0 : roomName.hashCode());
		result = prime * result + ((t == null) ? 0 : t.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
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
		if (roomIndex != other.roomIndex)
			return false;
		if (roomName == null) {
			if (other.roomName != null)
				return false;
		} else if (!roomName.equals(other.roomName))
			return false;
		if (t == null) {
			if (other.t != null)
				return false;
		} else if (!t.equals(other.t))
			return false;
		return true;
	}


	public void print(){
		System.out.println(t.toString() + " | " + (c==null?"_":c.toString()) + " " + roomName + " " + t.size());
	}
	
	public String toString(){
		return t.toString() + " | " + (c==null?"_":c.toString()) + " " + roomName + " " + t.size();
	}
	
	public void setFixed(){
		this.c.setFixed();
	}
	
	
}
