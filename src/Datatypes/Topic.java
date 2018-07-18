package Datatypes;

import java.util.ArrayList;
import java.util.List;

public class Topic {
	public List<Course> l;
	private String name;
	
	public Topic(String name){
		this.name = name;
		l = new ArrayList<Course>();
	}
	
	public String getName(){
		return this.name;
	}
	
	public void addCourse(Course c){
		this.l.add(c);
	}
	
	public void print(){
		System.out.println("Curriculum: " + this.name);
		System.out.println("----Courses-------");
		for(Course c:l) System.out.println(c.toString());
	}
	
	public Course getFirstUnfixed(){
		for(Course c : l){
			if(!c.isFixed()) return c;
		}
		return null;
	}
	
	public boolean contains(Course c){
		return l.contains(c);
	}
	
	public boolean isAllFixed(){
		for(Course c: l) if(!c.isFixed()){
			return false;
		}
		return true;
	}
}
