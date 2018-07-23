package Datatypes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Combo {		//Represents a combination of TimeSlot, Course and Room {later teacher}
	
	public int courseIndex;
	public String courseName;
	public int teacherIndex;
	public List<TimeSlot> t;
	public int roomIndex;
	public String roomName;
	
	public Combo(int courseIndex,String courseName, int size, TimeSlot start, int roomIndex, String roomName){
		this.courseIndex = courseIndex;
		this.courseName = courseName;
		this.t = new ArrayList<TimeSlot>();
		for(int i = 0; i < size;i++){
			t.add(new TimeSlot(start.getDay(),start.getSlot()+i));
		}
		this.roomIndex = roomIndex;
		this.roomName = roomName;
	}
	
	public Combo(Combo c){
		this.courseIndex = c.courseIndex;
		this.courseName = c.courseName;
		this.t = new ArrayList<TimeSlot>();
		for(int i = 0; i < c.getSize();i++){
			t.add(new TimeSlot(c.getFirstSlot().getDay(),c.getFirstSlot().getSlot()+i));
		}
		this.roomIndex = c.roomIndex;
		this.roomName = c.roomName;
		this.teacherIndex = c.teacherIndex;
	}
	
	public Combo(int size, TimeSlot start, int roomIndex, String roomName){
		this.courseIndex = -1;
		this.t = new ArrayList<TimeSlot>();
		for(int i = 0; i < size;i++){
			t.add(new TimeSlot(start.getDay(),start.getSlot()+i));
		}
		this.roomIndex = roomIndex;
		this.roomName = roomName;
		this.teacherIndex = -1;
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
		result = prime * result + courseIndex;
		result = prime * result + ((courseName == null) ? 0 : courseName.hashCode());
		result = prime * result + roomIndex;
		result = prime * result + ((roomName == null) ? 0 : roomName.hashCode());
		result = prime * result + ((t == null) ? 0 : t.hashCode());
		result = prime * result + teacherIndex;
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
		if (courseIndex != other.courseIndex)
			return false;
		if (courseName == null) {
			if (other.courseName != null)
				return false;
		} else if (!courseName.equals(other.courseName))
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
		if (teacherIndex != other.teacherIndex)
			return false;
		return true;
	}


	public void print(){
		System.out.println(t.toString() + " | " + (courseIndex==-1?"_":courseName) + " " + roomName + " " + t.size());
	}
	
	public String toString(){
		return t.toString() + " | " + (courseIndex==-1?"_":courseName) + " " + roomName + " " + t.size();
	}
	
	
}
