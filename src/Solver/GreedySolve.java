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
	public int INPUT_DAYS;
	public int INPUT_SLOTS;
	public static int runCount = 0;
	
	public GreedySolve(String filename){
		this.r = new Reader(filename);
		r.readFile();
		this.INPUT_DAYS = r.days;
		this.INPUT_SLOTS = r.slots;
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
		for(int day = 0; day < INPUT_DAYS; day++){
			for(int slot = 0; slot < INPUT_SLOTS; slot++){
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
	
	
	public void printSolution(){
		System.out.println("Solution: ");
		for(Room r: rooms) r.print();
	}
	
	
					// List of unfixed courses, solution Course/Room/Time/Teacher combo, already used Time/Room, list of teachers
	
	public boolean solveBackTrackHard(List<Course> cs, HashMap<Room,List<Combo>> solved, List<IndexCombo> used, List<Teacher> teachers, IndexCombo newNode){	
		runCount++;
		if(cs.isEmpty()) return true;							//If there's no more unfixed course, end of the recursion
		Course c = cs.get(0);									//Else we get the first unfixed course, and trying to fix
		List<Integer> teacherIndexes = this.getTeacherByCourse(c.getTopicname());
		newNode.slotIndex--;
		do{
			newNode.slotIndex++;
			if(newNode.slotIndex >= timeslots.size()){
				newNode.slotIndex=0;
				newNode.roomIndex++;
			}
			if(newNode.roomIndex >= rooms.size()){
				newNode.roomIndex = 0;										//If there is no more room/time, next teacher
				newNode.teacherIndex++;
			}
			if(newNode.teacherIndex>=teacherIndexes.size()) return false;		//If there are no more teacher, return false
		}while(used.contains(newNode));
		TimeSlot t = timeslots.get(newNode.slotIndex);				//We get the time slot
		Room r = rooms.get(newNode.roomIndex);							//and the room
		Teacher teacher = teachers.get(teacherIndexes.get(newNode.teacherIndex));
		Combo combo = new Combo(c,t,r);
		if(erroneus(solved,combo) || t.getSlot()+c.getSlots() > INPUT_SLOTS || c.getCapacity() > r.getCapacity()){
			newNode.slotIndex++;
			return solveBackTrackHard(cs,solved,used,teachers,newNode);		
		}
		if(teacher.isAvailable(combo.getSlotList())){								//If the course has time/room/teacher, we can add to our solved map
			teachers.get(teacherIndexes.get(newNode.teacherIndex)).addUnavailablePeriod(t, c.getSlots());		//set the teacher unavailable for his course
			combo.getCourse().setT(teachers.get(teacherIndexes.get(newNode.teacherIndex)));					//Save the combos
			if(solved.get(r) == null) {
				List<Combo> list = new ArrayList<Combo>();
				list.add(combo);
				solved.put(r, list);
			} else {
				solved.get(r).add(combo);
			}
			for(int i = 0; i < c.getSlots();i++) used.add(new IndexCombo(newNode.slotIndex+i, newNode.roomIndex, newNode.teacherIndex));			
			cs.remove(c);
			return solveBackTrackHard(cs,solved,used,teachers,new IndexCombo(0,0,0));		//We going down the tree with the next course
		}
		newNode.slotIndex++;
		return solveBackTrackHard(cs,solved,used,teachers,newNode);		//If we didn't find something, we have to check the next time slots/rooms
	}
	
	public boolean solveBackTrackHard2(List<Course> cs, List<Room> solved, List<IndexCombo> used, List<Teacher> teachers,IndexCombo newNode){	
		runCount++;
		if(cs.isEmpty()) return true;							//If there's no more unfixed course, end of the recursion
		Course c = cs.get(0);									//Else we get the first unfixed course, and trying to fix
		List<Integer> teacherIndexes = this.getTeacherByCourse(c.getTopicname());
		newNode.slotIndex--;
		do{
			newNode.slotIndex++;
			if(newNode.slotIndex >= timeslots.size()){
				newNode.slotIndex=0;
				newNode.roomIndex++;
			}
			if(newNode.roomIndex >= rooms.size()){
				newNode.roomIndex = 0;										//If there is no more room/time, next teacher
				newNode.teacherIndex++;
			}
			if(newNode.teacherIndex>=teacherIndexes.size()) return false;		//If there are no more teacher, return false
		}while(used.contains(newNode));
		TimeSlot t = timeslots.get(newNode.slotIndex);				//We get the time slot
		Room r = rooms.get(newNode.roomIndex);							//and the room
		Teacher teacher = teachers.get(teacherIndexes.get(newNode.teacherIndex));
		Combo combo = new Combo(c,t,r);
		if(erroneus(solved,combo) || t.getSlot()+c.getSlots() > INPUT_SLOTS || c.getCapacity() > r.getCapacity()){
			newNode.slotIndex++;
			return solveBackTrackHard2(cs,solved,used,teachers,newNode);		
		}
		if(teacher.isAvailable(combo.getSlotList())){								//If the course has time/room/teacher, we can add to our solved map
			teachers.get(teacherIndexes.get(newNode.teacherIndex)).addUnavailablePeriod(t, c.getSlots());		//set the teacher unavailable for his course
			combo.getCourse().setT(teachers.get(teacherIndexes.get(newNode.teacherIndex)));					//Save the combos
			Room rm = solved.get(solved.lastIndexOf(r));
			rm.addCombo(combo);
			for(int i = 0; i < c.getSlots();i++) used.add(new IndexCombo(newNode.slotIndex+i, newNode.roomIndex,newNode.teacherIndex));			
			cs.remove(c);
			return solveBackTrackHard2(cs,solved,used,teachers,new IndexCombo(0,0,0));		//We going down the tree with the next course
		}
		newNode.slotIndex++;
		return solveBackTrackHard2(cs,solved,used,teachers,newNode);		//If we didn't find something, we have to check the next time slots/rooms
	}
	
	public boolean solveHillClimb2(HashMap<Room,List<Combo>> solution){		//ToDo teachers availability set
		boolean isBetter = true;
		List<Combo> nodes = new ArrayList<Combo>();
		for(Entry<Room,List<Combo>> entry : solution.entrySet()){
			nodes.addAll(entry.getValue());
		}
		int i = 0;
		while(isBetter){
			i++;
			int nodeIndex = 0;
			int firstNodeIndex =  -1;					//Elsõ cserélhetõ combo indexe(legjobb)
			Combo newNode = null;					//Második cserélhetõ combo(legjobb)
			Combo currentNode = nodes.get(nodeIndex);		//Ehhez nézem a jobb szomszédot
			int switchMode = 0;								//Ha 0 - áthelyezés, üres slot. Ha 1 - kurzuscsere.
			int globalMinimum = this.getValue(nodes);		//Lekérem a kezdeti minimumot
			while(nodeIndex < nodes.size()){				//Ciklus a vizsgált csúcshoz
				currentNode = nodes.get(nodeIndex);
				List<Combo> neighbors = this.getNeighbors(solution, currentNode);
				int startValue = this.getValue(nodes);
				for(Combo node : neighbors){				//Megvizsgálom az adott currenthez minden szomszédját, és a legjobbat megkeresem
					currentNode = nodes.get(nodeIndex);
					
					int mode = 0;
					if(node.getCourse()==null){							//Put the actual course to an empty slot
						nodes.set(nodeIndex, node);
						node.setC(currentNode.getCourse());				
					} else {
						mode = 1;										//Switch 2 course 
						int neighborIndex = this.getIndex(nodes, node);
						currentNode.swap(node);								
						nodes.set(nodeIndex, currentNode);
						nodes.set(neighborIndex, node);		
					}
					int newValue =  this.getValue(nodes);
					if(erroneus(nodes)) newValue = Integer.MAX_VALUE;
					if(mode ==1){											//Reset to the original state
						currentNode.swap(node);								
						int neighborIndex = this.getIndex(nodes, node);
						nodes.set(nodeIndex, currentNode);
						nodes.set(neighborIndex, node);	
					}
					nodes.set(nodeIndex, currentNode);	
					if(newValue < startValue && newValue < globalMinimum){	//If the new solution was better, I save it
						firstNodeIndex = nodeIndex;
						globalMinimum = newValue;
						startValue = newValue;
						newNode = node;
						switchMode = mode;
						
					} 
					
				}
				nodeIndex++;				
				
			}
			if(newNode != null && switchMode == 0){						//Új üres helyre rakom a kurzust
				System.out.print(i + ". iteration, better solution: " + this.getValue(nodes));
				isBetter = true;
				currentNode = nodes.get(firstNodeIndex);				//kiveszem a cserélendõ elsõ kurzust
				newNode.setC(currentNode.getCourse());					//Beteszem a másik combo kurzusának, ami ugye üres volt
				nodes.set(firstNodeIndex, newNode);						//Beteszem a régi helyére az újat
				solution.get(currentNode.getR()).remove(currentNode);	//Törlöm az eredmény régi termébõl
				solution.get(newNode.getR()).add(newNode);				//és betszem az új terembe

				System.out.println(" to " + this.getValue(nodes));
				
				currentNode.print();
				newNode.print();
				isBetter = true;
			} else if(newNode != null && switchMode == 1){				//Cserélek két kurzust
				int strt = this.getValue(nodes);
				System.out.println(i + ". iteration, better swap solution: ");
				isBetter = true;
				currentNode = nodes.get(firstNodeIndex);
				solution.get(currentNode.getR()).remove(currentNode);	//Törlöm a termekbõl a node-okat
				solution.get(newNode.getR()).remove(newNode);
				currentNode.print();
				newNode.print();			
				int neighborIndex = this.getIndex(nodes, newNode);								
				currentNode.swap(newNode);
				nodes.set(neighborIndex, currentNode);	
				nodes.set(firstNodeIndex, newNode);
				solution.get(newNode.getR()).add(newNode);
				solution.get(currentNode.getR()).add(currentNode);
				nodes.get(firstNodeIndex).print();
				nodes.get(neighborIndex).print();
				isBetter = true;
				System.out.println(strt + " to " + this.getValue(nodes) + " " + globalMinimum);
			} else isBetter = false;
		}
		
		return isBetter;
	}
	
	private int getIndex(List<Combo> nodes, Combo input){
		for(int i = 0; i < nodes.size(); i++){
			Combo c = nodes.get(i);
			if(c.getCourse().getName().equals(input.getCourse().getName())) return i;
		}
		return -1;
	}
	
	public int getValue(List<Combo> input){
		int value = 0;
		int[] coursesByRoom = new int[rooms.size()];
		for(Combo combo : input){
			if(combo.getFirstSlot().getDay()==4) value+=4;			//Friday constraint penalyties
			coursesByRoom[rooms.indexOf(combo.getR())]+= combo.getSize();
			//if(combo.getFirstSlot().getDay()==4) value-=4;
			//if(combo.getFirstSlot().getSlot() == 0) value-=1;
			//if(combo.getFirstSlot().getSlot()+combo.getSize()-1 >= 1 && combo.getFirstSlot().getSlot() <= 1) value+=40;
		}
		int max1 = coursesByRoom[0];
		int min1 = coursesByRoom[0];
		for(int i = 1; i < coursesByRoom.length; i++) {
			if(coursesByRoom[i] < min1) min1 = coursesByRoom[i];
			if(coursesByRoom[i] > max1) max1 = coursesByRoom[i];
		}
		value += (max1-min1);
		for(Teacher te : teachers){														//TEacher compactness
			
			for(int day = 0; day < INPUT_DAYS; day++){
				int min = 10;
				int max = -1;
				List<TimeSlot> in = getSlotsByTeacher(day, te, input);				
				if(in == null) break;
				for(TimeSlot ts : in){
					if(ts.getSlot() < min) min = ts.getSlot();
					if(ts.getSlot() > max) max = ts.getSlot();
				}
				if(min == max){
					value += max;
					continue;
				}
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
				if(c.getFirstSlot().getDay()==INPUT_SLOTS) value+= c.getSize();
			}
		}
		return value;
	}
	
	public List<Combo> getNeighbors(HashMap<Room,List<Combo>> solution, Combo input){
		List<Combo> output = new ArrayList<Combo>();
		boolean debugmode = false;
		if(input.getCourse().getName().equals("Dimat1_4")) debugmode = true;
		for(List<Combo> combos : solution.values()){		//Neighbor courses
			for(Combo c : combos){
				if(c.getSize() == input.getSize() && !c.getCourse().getT().equals(input.getCourse().getT())){
				/*	boolean hasCourse = false;
					for(List<Combo> combos2 : solution.values()){
						for(Combo c2 : combos2){
							if(c2.getSize() == c.getSize() && c2.getFirstSlot().equals(input.getFirstSlot()) && c2.getCourse().getT().getName().equals(c.getCourse().getT().getName())){
								hasCourse = true;
								break;
							}		
						}
						if(hasCourse) break;
					}
					if(!hasCourse){
						//System.out.println("C : " + c.getCourse().getName() + " " + c.getFirstSlot().toString());
					*/	
					output.add(c);		//get the current courses, which are switchable to the given combo
					
				}
			}
		}
		for(TimeSlot sl : timeslots){						//Free slot selector
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
						if(sl.getSlot()+i>=INPUT_SLOTS){
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
		//if(c.getT().getSlot()>=INPUT_SLOTS) return true;								//Not enough timeslot for the given day
		if(!good.containsKey(c.getR())) return false;
		for(Combo com: good.get(c.getR())){
			//if(gd.getR().equals(c.getR()) && gd.getT().equals(c.getT()))	return true;		//If it collides with another course, which is already in list
			if(com.contains(c.getSlotList())) return true;
		}
		return false;														//Otherwise, it's good :)
	}

	public boolean erroneus(List<Room> rooms, Combo c){
		Room r = null;
		for(Room rm : rooms){
			if(rm.getName().equals(c.getR().getName())){
				r = rm;
				break;
			}
		}
		if(r!=null){
			for(TimeSlot t : c.getSlotList()){
				if(t.getSlot()>=INPUT_SLOTS) return true;
				if(r.isUsed(t)) return true;
			}
		}
		return false;
	}
	
	private boolean erroneus(List<Combo> nodes){
		for(Combo c1 : nodes){
			for(Combo c2 : nodes){
				if(!c2.equals(c1)){				
					if(c1.contains(c2.getSlotList())&& c1.getCourse().getT().getName().equals(c2.getCourse().getT().getName())){
						return true;
					}
				}
			}
		}
		return false;
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
