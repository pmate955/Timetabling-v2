package Datatypes;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Teacher {
	private String name;
	private List<String> specialities;
	private Set<TimeSlot> availability;
	
	public Teacher(String name){
		this.name = name;
		this.specialities = new ArrayList<String>();
		this.availability = new HashSet<TimeSlot>();
	}
	
	public Teacher(Teacher input){
		this.name = input.name;
	}

	
	
	public String getName() {
		return name;
	}
	
	public int getUnavailabelCount(){
		return availability.size();
	}

	public void setName(String _name) {
		this.name = _name;
	}
	
	public void addSpeciality(String spec){
		if(!specialities.contains(spec)) specialities.add(spec);
	}
	
	public boolean containsTopic(String spec){
		return specialities.contains(spec);
	}
	
	public void print(){
		System.out.println("Name: " + this.name + " av " + availability.size());
		for(String s: specialities) System.out.println("Spec: " + s);
		for(TimeSlot t : availability) System.out.println("Unav: " + t.toString());
		System.out.println("---------------------------------");
	}
	
	public void addUnavailablePeriod(TimeSlot t, int slots){
		for(int i = 0; i < slots; i++){
			TimeSlot ts = new TimeSlot(t.getDay(),t.getSlot()+i);
			if(!availability.contains(ts)) availability.add(ts);
		}
	}
	
	public void addUnavailablePeriod(List<TimeSlot> in){
		this.availability.addAll(in);
	}
	
	public void deleteUnavailablePeriod(List<TimeSlot> in){
		this.availability.removeAll(in);
	}
	
	public boolean isAvailable(List<TimeSlot> input){
		for(TimeSlot t : input){
			if(availability.contains(t)) return false;
		}
		return true;
	}
	
	public void printUnavailable(){
		System.out.println(this.name);
		for(TimeSlot t: availability) System.out.println(t.toString());
	}
	
	/**
	 * @return the availability
	 */
	public Set<TimeSlot> getAvailability() {
		return availability;
	}
	
	public List<TimeSlot> getAvailabilityAtDay(int day){
		List<TimeSlot> out = new ArrayList<TimeSlot>();
		for(TimeSlot t : availability){
			if(t.getDay() == day) out.add(t);
		}
		return out;
	}
	

	public void clearAvailability(){
		this.availability.clear();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((availability == null) ? 0 : availability.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((specialities == null) ? 0 : specialities.hashCode());
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
		Teacher other = (Teacher) obj;
		if (availability == null) {
			if (other.availability != null)
				return false;
		} else if (!availability.equals(other.availability))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (specialities == null) {
			if (other.specialities != null)
				return false;
		} else if (!specialities.equals(other.specialities))
			return false;
		return true;
	}

	
	
	
}
