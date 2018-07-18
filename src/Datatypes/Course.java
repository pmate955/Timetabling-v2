package Datatypes;

import java.util.List;

public class Course {
	private String name;
	private Teacher T;
	private int capacity;
	private int slots;
	private boolean isFixed;
	private String topicname;
	private int teacherIndex;
	private String teacherName;
	
	public Course(String name, String topicname,  int slots, int capacity, int teacherIndex, String teacherName){
		this.name = name;
		this.capacity = capacity;
		this.slots = slots;
		this.isFixed = false;
		this.topicname = topicname;
		this.teacherIndex = teacherIndex;
		this.teacherName = teacherName;
	}
	
	public Course(Course input){
		this.name = input.name;
		this.capacity = input.capacity;
		this.slots = input.slots;
		this.isFixed = input.isFixed();
		this.topicname = input.topicname;
		this.T = input.getT();
		this.teacherIndex = input.teacherIndex;
		this.teacherName = input.teacherName;
	}

	public String getTopicname() {
		return topicname;
	}

	public int getSlots() {
		return slots;
	}

	public Teacher getT() {
		return T;
	}

	public void setT(Teacher T) {
		this.T = T;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public String getTeacherName() {
		return teacherName;
	}

	public int getCapacity() {
		return capacity;
	}

	public void setCapacity(int capacity) {
		this.capacity = capacity;
	}

	public String toString(){
		return "Name: " + this.name + " Teacher: " + (teacherIndex==-1?" null ":teacherName) + " Students: " + this.capacity + " slots: " + this.slots;
	}
	
	public boolean isFixed(){
		return isFixed;
	}
	
	public void setFixed(){
		this.isFixed = true;
	}
	
	public int getTeacherIndex() {
		return teacherIndex;
	}

	public void setTeacherIndex(int teacherIndex, String teacherName) {
		this.teacherIndex = teacherIndex;
		this.teacherName = teacherName;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((T == null) ? 0 : T.hashCode());
		result = prime * result + capacity;
		result = prime * result + (isFixed ? 1231 : 1237);
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + slots;
		result = prime * result + ((topicname == null) ? 0 : topicname.hashCode());
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
		Course other = (Course) obj;
		if (T == null) {
			if (other.T != null)
				return false;
		} else if (!T.equals(other.T))
			return false;
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
