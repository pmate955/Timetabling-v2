package Solver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Datatypes.Combo;
import Datatypes.Course;
import Datatypes.IndexCombo;
//import Datatypes.Curriculum;
import Datatypes.Room;
import Datatypes.Teacher;
import Datatypes.TimeSlot;
import Datatypes.Topic;

public class GreedySolve {
	private Reader r;
	public List<Room> rooms;
	public List<Teacher> teachers;
	public List<Course> courses;
	public List<Topic> topics;
	public List<TimeSlot> timeslots;
	public Map<String,List<Integer>> courseTeacher;
	public static int runCount = 0;
	
	public GreedySolve(String filename){
		this.r = new Reader(filename);
		r.readFile();
		this.rooms = new ArrayList<Room>();
		this.courses = new ArrayList<Course>();
		this.teachers = new ArrayList<Teacher>();
		this.topics = new ArrayList<Topic>();
		this.copyListR(this.rooms, r.rooms);
		this.copyListT(this.teachers, r.teachers);
		this.copyListC(this.courses, r.courses);
		this.copyListTp(this.topics, r.topics);
		this.courseTeacher = new HashMap<String,List<Integer>>();
		for(Topic to:topics){
			List<Integer> tIndexes = new ArrayList<Integer>();
			for(Teacher t:teachers){
				if(t.containsTopic(to.getName())) tIndexes.add(teachers.indexOf(t));
			}
			courseTeacher.put(to.getName(), tIndexes);
		}
		this.timeslots = new ArrayList<TimeSlot>();
		for(int day = 0; day < 5; day++){
			for(int slot = 0; slot < 4; slot++){
				TimeSlot t = new TimeSlot(day,slot);
				this.timeslots.add(t);
			}
		}
	}
	
	public void clearData(){
		for(Room ro:rooms) ro.clearRoom();
		for(Teacher t:teachers) t.clearAvailability();
		this.copyListC(this.courses, r.courses);
	}
	
	public void NoFridaySlot(){
		this.timeslots.clear();
		for(int day = 0; day < 4; day++){
			for(int slot = 0; slot < 4; slot++){
				TimeSlot t = new TimeSlot(day,slot);
				this.timeslots.add(t);
			}
		}
	}
	
	
	public void printSolution(){
		System.out.println("Solution: ");
		for(Room r: rooms) r.print();
	}
	
	
					// List of unfixed courses, solution Course/Room/Time/Teacher combo, already used Time/Room, list of teachers
	
	public boolean solveBackTrackHard(List<Course> cs, HashMap<Room,List<Combo>> solved, List<IndexCombo> used, List<Teacher> teachers, int timeSlotIndex, int roomIndex, int teacherIndex){	
		runCount++;
		if(cs.isEmpty()) return true;							//If there's no more unfixed course, end of the recursion
		Course c = cs.get(0);									//Else we get the first unfixed course, and trying to fix
		List<Integer> teacherIndexes = this.getTeacherByCourse(c.getTopicname());
		if(timeSlotIndex >= timeslots.size()){					//If "timeIndex" > maximum time, we're going to next room
			timeSlotIndex = 0;
			roomIndex++;
		}
		if(roomIndex >= rooms.size()){
			roomIndex = 0;									//If there is no more room/time, next teacher
			teacherIndex++;
		}
		if(teacherIndex>=teacherIndexes.size()) return false;		//If there are no more teacher, return false
		IndexCombo p = new IndexCombo(timeSlotIndex, roomIndex, teacherIndex);			//We're checking the given time/room combo
		while(used.contains(p)){								//If it's already used, no need recursion, while we don't find an available slot
			timeSlotIndex++;		
			if(timeSlotIndex >= timeslots.size()){					//If "timeIndex" > maximum time, we're going to next room
				timeSlotIndex = 0;
				roomIndex++;
			}
			if(roomIndex >= rooms.size()){
				roomIndex = 0;									//If there is no more room/time, next teacher
				teacherIndex++;
			}
			if(teacherIndex>=teacherIndexes.size()) return false;	
			p = new IndexCombo(timeSlotIndex, roomIndex, teacherIndex);
		}
		TimeSlot t = timeslots.get(timeSlotIndex);				//We get the time slot
		Room r = rooms.get(roomIndex);							//and the room
		Teacher teacher = teachers.get(teacherIndexes.get(teacherIndex));
		boolean good = true;
		Combo combo = new Combo(c,t,r);
		if(erroneus(solved,combo)) good = false;		
		boolean foundTeacher = false;		
		if(good){										//We're trying to find a teacher to combo 
			
			if(teacher.isAvailable(combo.getSlotList())){
				foundTeacher = true;				//We have to check the teacher availability 
			}
		}
		if(good && foundTeacher){								//If the course has time/room/teacher, we can add to our solved map
			teachers.get(teacherIndexes.get(teacherIndex)).addUnavailablePeriod(t, c.getSlots());		//set the teacher unavailable for his course
			combo.getCourse().setT(teachers.get(teacherIndexes.get(teacherIndex)));					//Save the combos
			if(solved.get(r) == null) {
				List<Combo> list = new ArrayList<Combo>();
				list.add(combo);
				solved.put(r, list);
			} else {
				solved.get(r).add(combo);
			}
			for(int i = 0; i < c.getSlots();i++) used.add(new IndexCombo(timeSlotIndex+i, roomIndex,teacherIndex));
			
			cs.remove(c);
			return solveBackTrackHard(cs,solved,used,teachers,0,0,0);		//We going down the tree with the next course
		}
		return solveBackTrackHard(cs,solved,used,teachers,++timeSlotIndex,roomIndex,teacherIndex);		//If we didn't find something, we have to check the next time slots/rooms
	}
	
	
	public void setSolution(HashMap<Room,List<Combo>> input){
		for(Room r : rooms){
			if(input.get(r) == null) continue;
			for(Combo c : input.get(r)){
				
			}
		}
	}
	
	
	
	
	private List<Integer> getTeacherByCourse(String topicName){
		return courseTeacher.get(topicName);
	}
	
	private boolean erroneus(HashMap<Room,List<Combo>> good, Combo c){					//True, if the solution is not correct
		//if(c.getT().getSlot()>=4) return true;								//Not enough timeslot for the given day
		if(!good.containsKey(c.getR())) return false;
		for(Combo com: good.get(c.getR())){
			//if(gd.getR().equals(c.getR()) && gd.getT().equals(c.getT()))	return true;		//If it collides with another course, which is already in list
			if(com.contains(c.getSlotList())) return true;
		}
		return false;														//Otherwise, it's good :)
	}
	
	private void copyListC(List<Course> dest, List<Course> src){
		for(int i = 0; i < src.size();i++){
			dest.add(src.get(i));
		}
	}
	public void copyListR(List<Room> dest, List<Room> src){
		for(int i = 0; i < src.size();i++){
			dest.add(src.get(i));
		}
	}
	
	private void copyListT(List<Teacher> dest, List<Teacher> src){
		for(int i = 0; i < src.size();i++){
			dest.add(src.get(i));
		}
	}
	
	private void copyListTp(List<Topic> dest, List<Topic> src){
		for(int i = 0; i < src.size();i++){
			dest.add(src.get(i));
		}
	}
}
