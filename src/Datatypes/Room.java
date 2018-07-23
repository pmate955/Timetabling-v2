package Datatypes;

import java.util.ArrayList;
import java.util.List;

public class Room {
	private String name;
	private int capacity;
	private int days;
	private int slots;
	
	public Room(String name, int days, int times, int capacity){
		this.name = name;
		this.capacity = capacity;
		this.days = days;
		this.slots = times;
	}
	
	public Room(Room r){
		this.name = r.name;
		this.capacity = r.capacity;
		this.days = r.days;
		this.slots = r.slots;
	}

	public String getName(){return this.name;}
	public int getCapacity(){return this.capacity;}
	public int getDays(){return this.days;}
	public int getSlots(){return this.slots;}
	
	

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + capacity;
		result = prime * result + days;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + slots;
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
		return true;
	}

	
}
