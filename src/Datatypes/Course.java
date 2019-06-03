package Datatypes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Course {
	private String name;
	private int capacity;
	private int slots;
	private Set<Integer> curricula;
	private boolean isFixed;
	private String topicname;
	private int minWorkingDays;
	public List<TimeSlot> unavailability;
	
	
	public Course(String name, String topicname,  int slots, int capacity, int minWorkingDays){
		this.name = name;
		this.capacity = capacity;
		this.slots = slots;
		this.isFixed = false;
		this.topicname = topicname;
		this.unavailability = new ArrayList<TimeSlot>();
		this.curricula = new HashSet<Integer>();
		this.minWorkingDays = minWorkingDays;
	}
	
	public void setUnavailabilty(List<TimeSlot> input) {
		this.unavailability.addAll(input);
	}
	
	
	public void addUnavailability(TimeSlot t) {
		this.unavailability.add(t);
	}
	
	public void printUnavailability() {
		for(TimeSlot t : unavailability) {
			System.out.print(" | " + t.toString());
		}
	}
	
	public boolean isUnavailable(TimeSlot input) {
		return this.unavailability.contains(input);
	}
	
	public boolean isUnavailable(List<TimeSlot> input) {
		return !Collections.disjoint(unavailability, input);
	}
	
	public Course(Course input){
		this.name = input.name;
		this.capacity = input.capacity;
		this.slots = input.slots;
		this.isFixed = input.isFixed();
		this.topicname = input.topicname;
		this.curricula = input.curricula;
	}

	public String getTopicname() {
		return topicname;
	}

	public int getSlots() {
		return slots;
	}

	public Set<Integer> getCurricula() {
		return this.curricula;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public void addCurriculum(int num) {
		this.curricula.add(num);
	}

	public int getCapacity() {
		return capacity;
	}

	public void setCapacity(int capacity) {
		this.capacity = capacity;
	}

	public String toString(){
		return "Name: " + this.name + " Students: " + this.capacity + " slots: " + this.slots;
	}
	
	public boolean isFixed(){
		return isFixed;
	}
	
	public void setFixed(){
		this.isFixed = true;
	}
	
	
	public int getMinWorkingDays() {
		return minWorkingDays;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + capacity;
		result = prime * result + (isFixed ? 1231 : 1237);
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + slots;
		result = prime * result + ((topicname == null) ? 0 : topicname.hashCode());
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
		Course other = (Course) obj;
		if (capacity != other.capacity)
			return false;
		if (isFixed != other.isFixed)
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (slots != other.slots)
			return false;
		if (topicname == null) {
			if (other.topicname != null)
				return false;
		} else if (!topicname.equals(other.topicname))
			return false;
		return true;
	}
	
}
