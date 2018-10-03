package Solver;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import Datatypes.*;
public class ReaderCTT {
	
	public List<Room> rooms;
	public List<Teacher> teachers;
	public List<Course> courses;
	public List<Topic> topics;
	public List<Combo> saved;
	public int days = 0;
	public int slots = 0;
	public int bestValue;
	private String filename;
	public String readed;
	
	public ReaderCTT(String filename) {
		this.filename = filename;
		this.rooms = new ArrayList<Room>();
		this.teachers = new ArrayList<Teacher>();
		this.courses = new ArrayList<Course>();
		this.topics = new ArrayList<Topic>();
		this.saved = new ArrayList<Combo>();
		this.readed = "";
	}
	
	public boolean read() {
		try {
			BufferedReader br = new BufferedReader(new FileReader(new File(filename)));
			String s = br.readLine();
			while(s != null) {
				if(s.contains("Days: ")) {							//Read days number
					String[] arr = s.split(" ");
					this.days = Integer.parseInt(arr[1]);
				} else if(s.contains("Periods_per_day: ")) {		//Read slots number
					String[] arr = s.split(" ");
					this.slots = Integer.parseInt(arr[1]);
				} else if(s.contains("COURSES:")) {					//Read lis of topics, and generate courses
					s = br.readLine();
					while(!s.equals("")) {
						String[] arr = s.split(" ");
						Teacher t = this.getTeacherByName(arr[1]);
						if(t != null) {
							t.addSpeciality(arr[0]);
						} else {
							t = new Teacher(arr[1]);
							t.addSpeciality(arr[0]);
							teachers.add(t);
						}
						topics.add(new Topic(arr[0]));
						this.generateCourses(arr[0], Integer.parseInt(arr[2]), Integer.parseInt(arr[4]));
						s = br.readLine();
					}
				} else if(s.contains("ROOMS:")) {					//Read rooms
					s = br.readLine();
					while(!s.equals("")) {
						String[] arr = s.split("\\t");
						Room r = new Room(arr[0], days, slots, Integer.parseInt(arr[1]));
						rooms.add(r);
						s = br.readLine();
					}
				} else if(s.equals("END.")) {
					br.close();
					break;
				}
				
				s = br.readLine();
			}
		} catch (IOException e) {
			System.out.println("Error in reader");
			return false;
		}
		Collections.sort(rooms, Comparator.comparingInt(Room ::getCapacity));
		return true;
	}
	
	
	private void generateCourses(String baseName, int count, int students) {
		for(int i = 0;  i < count; i++) {
			Course c = new Course(baseName + "_" + i, baseName, 1, students);
			courses.add(c);
			topics.get(topics.size()-1).addCourse(c);
		}
	}
	
	private Teacher getTeacherByName(String name) {
		for(Teacher t : teachers) {
			if(t.getName().equals(name)) {
				return t;
			}
		}
		return null;
	}
	
	public void printAll() {
		System.out.println("Days: " + days);
		System.out.println("Slots: " + slots);
		System.out.println("----Teachers-------");
		for(Teacher t : teachers) {
			System.out.println(t.getName());
		}
		System.out.println("------Rooms--------");
		for(Room r : rooms) {
			System.out.println(r.getName() + " " + r.getCapacity());
		}
		System.out.println("-----Courses-------");
		for(Course c : courses) {
			System.out.println(c.toString() + " _ "+ c.getTopicname());
		}
	}

}
