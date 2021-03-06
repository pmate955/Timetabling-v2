package Solver;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import Datatypes.*;
public class ReaderCTT {
	
	public List<Room> rooms;
	public List<Teacher> teachers;
	public List<Course> courses;
	public List<Topic> topics;
	public List<Combo> saved;
	public List<String> curriculas;
	public int days = 0;
	public int slots = 0;
	public int bestValue;
	private String filename;
	public StringBuilder readed;
	
	public ReaderCTT(String filename) {
		this.filename = filename;
		this.rooms = new ArrayList<Room>();
		this.teachers = new ArrayList<Teacher>();
		this.courses = new ArrayList<Course>();
		this.topics = new ArrayList<Topic>();
		this.saved = new ArrayList<Combo>();
		this.curriculas = new ArrayList<String>();
		this.readed = new StringBuilder("");
	}
	
	public boolean read() {
		try {
			BufferedReader br = new BufferedReader(new FileReader(new File(filename)));
			String s = br.readLine();
			while(s != null) {
				readed.append(s + "\r\n");
				if(s.contains("Days: ")) {							//Read days number
					String[] arr = s.split(" ");
					this.days = Integer.parseInt(arr[1]);
				} else if(s.contains("Periods_per_day: ")) {		//Read slots number
					String[] arr = s.split(" ");
					this.slots = Integer.parseInt(arr[1]);
				} else if(s.contains("COURSES:")) {					//Read lis of topics, and generate courses
					s = br.readLine();
					readed.append(s + "\r\n");
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
						this.generateCourses(arr[0], Integer.parseInt(arr[2]), Integer.parseInt(arr[3]) ,Integer.parseInt(arr[4]));
						s = br.readLine();
						readed.append(s + "\r\n");
					}
				} else if(s.contains("ROOMS:")) {					//Read rooms
					s = br.readLine();
					readed.append(s + "\r\n");
					while(!s.equals("")) {
						String[] arr = s.split("\\t");
						Room r = new Room(arr[0], days, slots, Integer.parseInt(arr[1]));
						rooms.add(r);
						s = br.readLine();
						readed.append(s + "\r\n");
					}
					
				} else if(s.contains("UNAVAILABILITY_CONSTRAINTS:")) {
					s = br.readLine();
					readed.append(s + "\r\n");
					while(!s.equals("")) {
						String[] arr = s.split(" ");
						this.addUnavailability(arr[0], arr[1], arr[2]);						
						s = br.readLine();
						readed.append(s + "\r\n");
						
					}
					
				} else if(s.equals("END.")) {
					br.close();
					break;
				} else if(s.contains("CURRICULA:")) {
					s = br.readLine();
					readed.append(s + "\r\n");
					while(!s.equals("")) {
						String[] arr = s.split(" ");
						this.curriculas.add(arr[0]);
						this.addCurricula(arr);				
						s = br.readLine();
						readed.append(s + "\r\n");
						
					}
				}
				
				s = br.readLine();
			}
		} catch (IOException e) {
			System.out.println("Error in reader");
			return false;
		}
		Collections.sort(rooms, Comparator.comparingInt(Room ::getCapacity));
		//System.out.println(readed.toString());
		/*//this.printAll();

		System.out.println(courses.get(0).getCurricula());
		System.out.println(courses.get(3).getCurricula());
		Set<Integer> tmp = new HashSet<Integer>(courses.get(0).getCurricula());
		tmp.retainAll(courses.get(3).getCurricula());
		System.out.println(tmp.size());
		System.out.println(courses.get(0).getCurricula());
		System.out.println(courses.get(3).getCurricula());*/
		return true;
	}
	
	private void addCurricula(String[] arr) {
		int index = this.curriculas.indexOf(arr[0]);
		for(int i = 3; i < 3+Integer.parseInt(arr[2]); i++) {
			String topicname = arr[i];
			for(Course c : courses) {
				if(c.getTopicname().equals(topicname)) {
					c.addCurriculum(index);
				}
			}
		}
	}

	private void addUnavailability(String topicname, String day, String slot) {
		TimeSlot t = new TimeSlot(Integer.parseInt(day), Integer.parseInt(slot));
		for(Course c : courses) {
			if(c.getTopicname().equals(topicname)) {
				c.addUnavailability(t);
			}
		}
	}
	
	private void generateCourses(String baseName, int count,int minWorkingDays, int students) {
		for(int i = 0;  i < count; i++) {
			Course c = new Course(baseName + "_" + i, baseName, 1, students, minWorkingDays);
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
			System.out.print(c.toString() + " _ "+ c.getTopicname() + " " );
			for(int i : c.getCurricula()) {
				System.out.print(curriculas.get(i) + " ");
			}
			System.out.println();
			//c.printUnavailability();
		}
		
	}

}
