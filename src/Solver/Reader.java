package Solver;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import Datatypes.*;

public class Reader {
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
	
	public Reader(String filename){
		this.filename = filename;
		this.rooms = new ArrayList<Room>();
		this.teachers = new ArrayList<Teacher>();
		this.courses = new ArrayList<Course>();
		this.topics = new ArrayList<Topic>();
		this.saved = new ArrayList<Combo>();
		this.readed = "";
	}
	
	public boolean readFile(){
		try{
			BufferedReader br = new BufferedReader(new FileReader(new File(filename)));
			String s = br.readLine();			
			while(s!=null){				
				String[] token = s.split(";");
				if(!token[0].equals("Combo"))  readed += s + "\r\n";
				if(token[0].equals("Days")) this.days = Integer.parseInt(token[1]);
				else if(token[0].equals("Slots")) this.slots = Integer.parseInt(token[1]);
				else if(token[0].equals("Room")) this.addRoom(token);
				else if(token[0].equals("Teacher")) this.addTeacher(token);
				else if(token[0].equals("Topic")) this.addTopic(token);
				else if(token[0].equals("Course")) this.addCourse(token);
				else if(token[0].equals("Speciality")) this.addSpeciality(token);
				else if(token[0].equals("Combo")) this.addCombo(token);
				else if(token[0].equals("BestValue")) this.bestValue = Integer.parseInt(token[1]);
				s = br.readLine();
			}
			br.close();
		} catch (IOException e){
			System.out.println("Error while reading");
			return false;
		}
		return true;
	}
	
	private void addCombo(String[] token) {
		this.saved.add(new Combo(Integer.parseInt(token[1]), token[2], Integer.parseInt(token[3]), new TimeSlot(Integer.parseInt(token[4]), Integer.parseInt(token[5])), Integer.parseInt(token[6]), token[7]));
	}

	private void addRoom(String[] str){
		String name = str[1];
		int capacity = Integer.parseInt(str[2]);
		Room r = new Room(name, days, slots, capacity);
		rooms.add(r);
	}
	
	private void addTeacher(String[] str){
		String name = str[1];
		Teacher t = new Teacher(name);
		teachers.add(t);
	}
	
	private void addTopic(String[] str){
		String name = str[1];
		Topic t = new Topic(name);
		topics.add(t);
	}
	
	private void addCourse(String[] str){
		String name = str[1];
		int capacity = Integer.parseInt(str[3]);
		int slots = Integer.parseInt(str[2]);
		Course c = new Course(name,topics.get(topics.size()-1).getName(), slots, capacity);
		courses.add(c);
		topics.get(topics.size()-1).addCourse(c);
	}
	
	private void addSpeciality(String[] str){
		String spec = str[1];
		teachers.get(teachers.size()-1).addSpeciality(spec);
	}
	
	public void print(){
		System.out.println("Rooms: ");
		for(Room r:rooms) System.out.println(r.getName());
		System.out.println("Teachers: ");
		for(Teacher t:teachers) t.print();
		System.out.println("Topics: ");
		for(Topic c: topics) {
			System.out.println("---------------------------");
			c.print();
		}
	}
}
