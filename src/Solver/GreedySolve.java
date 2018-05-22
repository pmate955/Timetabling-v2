package Solver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

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
	
	/*public void NoFridaySlot(){
		this.timeslots.clear();
		for(int day = 0; day < 4; day++){
			for(int slot = 0; slot < 4; slot++){
				TimeSlot t = new TimeSlot(day,slot);
				this.timeslots.add(t);
			}
		}
	}*/
	
	
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
		IndexCombo p = null;
		timeSlotIndex--;
		do{
			timeSlotIndex++;
			if(timeSlotIndex >= timeslots.size()){					//If "timeIndex" > maximum time, we're going to next room
				timeSlotIndex = 0;
				roomIndex++;
			}
			if(roomIndex >= rooms.size()){
				roomIndex = 0;									//If there is no more room/time, next teacher
				teacherIndex++;
			}
			if(teacherIndex>=teacherIndexes.size()) return false;		//If there are no more teacher, return false
			p = new IndexCombo(timeSlotIndex, roomIndex, teacherIndex);			//We're checking the given time/room combo		
		} while (used.contains(p));		
		TimeSlot t = timeslots.get(timeSlotIndex);				//We get the time slot
		Room r = rooms.get(roomIndex);							//and the room
		Teacher teacher = teachers.get(teacherIndexes.get(teacherIndex));
		Combo combo = new Combo(c,t,r);
		if(erroneus(solved,combo) || t.getSlot()+c.getSlots() > 4 || c.getCapacity() > r.getCapacity()) return solveBackTrackHard(cs,solved,used,teachers,++timeSlotIndex,roomIndex,teacherIndex);		
		if(teacher.isAvailable(combo.getSlotList())){								//If the course has time/room/teacher, we can add to our solved map
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
	
	public boolean solveHillClimb2(HashMap<Room,List<Combo>> solution){		//ToDo teachers availability set
		boolean isBetter = false;
		List<Combo> nodes = new ArrayList<Combo>();
		for(Entry<Room,List<Combo>> entry : solution.entrySet()){
			nodes.addAll(entry.getValue());
		}
		for(int i = 0; i < 100; i++){
			int nodeIndex = 0;
			int firstNodeIndex =  -1;					//Elsõ cserélhetõ combo indexe(legjobb)
			Combo newNode = null;						//Második cserélhetõ combo(legjobb)
			Combo currentNode = nodes.get(nodeIndex);		//Ehhez nézem a jobb szomszédot
			int switchMode = 0;								//Ha 0 - áthelyezés, üres slot. Ha 1 - kurzuscsere.
			while(nodeIndex < nodes.size()-1){				//Ciklus a vizsgált csúcshoz
				List<Combo> neighbors = this.getNeighbors(solution, currentNode);
				int startValue = this.getValue(nodes);
				
				for(Combo node : neighbors){				//Megvizsgálom az adott currenthez minden szomszédját, és a legjobbat megkeresem
					int mode = 0;
					if(node.getCourse()==null){
						mode = 0;
						nodes.set(nodeIndex, node);
						node.setC(currentNode.getCourse());				
					} else {
						mode = 1;					//Switch 2 course (later implemented)
						Course temp = currentNode.getCourse();			//Lekérem az elsõ kombó kurzusát
						currentNode.setC(node.getCourse());				//Ez lesz a másiké
						int neighborIndex = nodes.lastIndexOf(node);
						node.setC(temp);								//Visszateszem az elsõbe a másodikét
						nodes.set(neighborIndex, node);					//
					}
					int newValue =  this.getValue(nodes);
					if(newValue < startValue){
						firstNodeIndex = nodeIndex;
						startValue = newValue;
						newNode = node;
						switchMode = mode;
						
					} 
					Course temp = currentNode.getCourse();			//Lekérem az elsõ kombó kurzusát
					currentNode.setC(node.getCourse());				//Ez lesz a másiké
					int last = nodes.lastIndexOf(node);
					node.setC(temp);				
					nodes.set(last, node);
				}
				nodes.set(nodeIndex, currentNode);						//Visszaállitás		
				currentNode = nodes.get(++nodeIndex);
			}
			if(newNode != null && switchMode == 0){						//Új üres helyre rakom a kurzust
				System.out.println(i + ". iteration, better solution: ");
				isBetter = true;
				currentNode = nodes.get(firstNodeIndex);				//kiveszem a cserélendõ elsõ kurzust
				newNode.setC(currentNode.getCourse());					//Beteszem a másik combo kurzusának, ami ugye üres volt
				nodes.set(firstNodeIndex, newNode);						//Beteszem a régi helyére az újat
				solution.get(currentNode.getR()).remove(currentNode);	//Törlöm az eredmény régi termébõl
				solution.get(newNode.getR()).add(newNode);				//és betszem az új terembe
				currentNode.print();
				newNode.print();
			} else if(newNode != null && switchMode == 1){				//Cserélek két kurzust
				System.out.println(i + ". iteration, better swap solution: ");
				isBetter = true;
				currentNode = nodes.get(firstNodeIndex);
				Course temp = currentNode.getCourse();
				currentNode.setC(newNode.getCourse());				//Ez lesz a másiké
				int neighborIndex = nodes.lastIndexOf(newNode);
				newNode.setC(temp);								
				nodes.set(neighborIndex, newNode);	
				currentNode.print();
				newNode.print();
			}
		}
		System.out.println("New value: " + this.getValue(nodes));
		return isBetter;
	}
	
	
	public int getValue(List<Combo> input){
		int value = 0;
		for(Combo combo : input) if(combo.getFirstSlot().getDay()==4) value++;			//Friday constraint penalyties
		
		for(Teacher te : teachers){														//TEacher compactness
			for(int day = 0; day < 5; day++){
				int min = 10;
				int max = -1;
				List<TimeSlot> in = getSlotsByTeacher(day, te, input);
				if(in == null) break;
				for(TimeSlot ts : in){
					if(ts.getSlot() < min) min = ts.getSlot();
					if(ts.getSlot() > max) max = ts.getSlot();
				}
				if(min == max) break;
				for(int slot = 0; slot < max; slot++){
					if(!in.contains(new TimeSlot(day,slot))){
						value++;
					}
				}
			}
		}
		return value;
	}
	
	private List<TimeSlot> getSlotsByTeacher(int day, Teacher t, List<Combo> input){
		List<TimeSlot> output = new ArrayList<TimeSlot>();
		for(Combo c : input){
			if(day == c.getFirstSlot().getDay() && c.getCourse().getT().getName().equals(t.getName())) output.addAll(c.getSlotList());
		}
		return output;
	}
	
	public int getValue(HashMap<Room,List<Combo>> solution){

		
		int value = 0;
		for(List<Combo> combos : solution.values()){
			for(Combo c : combos){
				if(c.getFirstSlot().getDay()==4) value+= c.getSize();
			}
		}
		return value;
	}
	
	public List<Combo> getNeighbors(HashMap<Room,List<Combo>> solution, Combo input){
		List<Combo> output = new ArrayList<Combo>();
		for(List<Combo> combos : solution.values()){
			for(Combo c : combos){
				if(c.getSize() == input.getSize() && !c.getCourse().getT().equals(input.getCourse().getT())) output.add(c);		//get the current courses, which are switchable to the given combo
			}
		}
		for(TimeSlot sl : timeslots){
			Combo newCombo = null;
			for(Room r : solution.keySet()){			
				boolean freeRoom = true;
				for(Combo combo : solution.get(r)){		
					if(combo.getSlotList().contains(sl)){
						freeRoom = false;
						break;			//If we have course in that room, we go to next room
					}
				}
				if(freeRoom){
					boolean isLongEnough = true;
					for(int i = 0; i < input.getSize(); i++){
						TimeSlot slot = new TimeSlot(sl.getDay(), sl.getSlot()+i);
						if(sl.getSlot()+i>=4){
							isLongEnough = false;
						}
						for(Combo combo : solution.get(r)){		
							if(combo.getSlotList().contains(slot)){
								isLongEnough = false;
								break;			//If we have course in that room, we go to next room
							}
						}
						if(!isLongEnough) break;
					}
					if(isLongEnough){
						newCombo = new Combo(input.getSize(),sl,r);						
					}
				}
			}
			if(newCombo != null){
				for(Entry<Room,List<Combo>> entry : solution.entrySet()){
					for(Combo c : entry.getValue()){
						if(c != null && newCombo != null && c.contains(newCombo.getSlotList()) && c.getCourse().getT().getName().equals(input.getCourse().getT().getName())) newCombo = null;
					}
				}
			}
			if(newCombo != null){
				output.add(newCombo);
			}
		}
		
		return output;
	}
	
	public void setSolution(HashMap<Room,List<Combo>> input){
		for(Room r : rooms){
			if(input.get(r) == null) continue;
			for(Combo c : input.get(r)){
				r.addFullCourse(c.getCourse(), c.getFirstSlot());
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
