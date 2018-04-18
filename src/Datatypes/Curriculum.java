package Datatypes;

import java.util.ArrayList;
import java.util.List;

public class Curriculum {
	private List<Course> courses;
	private String name;
	
	public Curriculum(String name){
		this.name = name;
		this.courses = new ArrayList();
	}
	
	public void addCourse(Course c){
		this.courses.add(c);
	}
	
	public String getString(){return this.name;}
	
	public void print(){
		System.out.println("Curriculum: " + this.name);
		System.out.println("----Courses-------");
		for(Course c:courses) System.out.println(c.toString());
	}
	
}
