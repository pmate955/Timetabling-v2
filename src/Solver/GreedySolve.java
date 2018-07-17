package Solver;

import java.lang.invoke.SwitchPoint;
import java.sql.Time;
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
	

	
	public void solveHillClimb(List<Room> solution){
		List<Combo> nodes = new ArrayList<Combo>();			//Get the list of all node
		for(Room r : solution){
			nodes.addAll(r.getCourses());			
		}
		int iterationNumber = 0;
		boolean foundBetter = true;
		while(foundBetter){
			iterationNumber++;
			int actualNodeIndex = 0;
			int firstNodeIndex = -1;
			Combo secondNode = null;
			Combo currentNode;
			int swapMode = 0;
			int globalMinimum = this.getValue(nodes);
			while(actualNodeIndex < nodes.size()){
				currentNode = nodes.get(actualNodeIndex);
				List<Combo> neighbors = this.getNeighbors(nodes, currentNode);
				int startValue = this.getValue(nodes);
				for(Combo node : neighbors){
					int localSwapMode = 0;
					if(node.getCourse()==null){								//Place the combo to the new place
						nodes.set(actualNodeIndex, node);
						node.setCourse(currentNode);
					} else {
						localSwapMode = 1;
						int neighborIndex = this.getIndex(nodes, node);
						currentNode.swap(node);								
						nodes.set(actualNodeIndex, currentNode);
						nodes.set(neighborIndex, node);	
					}
					int newValue =  this.getValue(nodes);
					/*if(erroneus(nodes)){
						System.out.println("ERRORRRRR " + localSwapMode);
						newValue = Integer.MAX_VALUE;
					}*/
					if(localSwapMode==1){				//only for switch
						currentNode.swap(node);								
						int neighborIndex = this.getIndex(nodes, node);
						nodes.set(actualNodeIndex, currentNode);
						nodes.set(neighborIndex, node);	
					} else if(localSwapMode == 0){
						currentNode.setCourse(node);
					}
					nodes.set(actualNodeIndex, currentNode);
					if(newValue < startValue && newValue < globalMinimum){			//If we found better global value
						firstNodeIndex=actualNodeIndex;
						globalMinimum = newValue;
						startValue = newValue;
						secondNode = node;
						swapMode = localSwapMode;
					}
				}
				actualNodeIndex++;
			}
			if(secondNode != null && swapMode == 0){							//Better course pair to swap
				System.out.println(iterationNumber + ". iteration, better solution: " + this.getValue(nodes));
				currentNode = nodes.get(firstNodeIndex);					//Get the first node
			//secondNode.setC(currentNode.getCourse());					//We set secondNode course empty to new one				
				secondNode.setCourse(currentNode);				
				nodes.set(firstNodeIndex, secondNode);
				solution.get(solution.indexOf(currentNode.getR())).deleteCombo(currentNode);
				solution.get(solution.indexOf(secondNode.getR())).addCombo(secondNode);
				System.out.println(" to " + this.getValue(nodes));
				currentNode.print();
				secondNode.print();
				foundBetter = true;
			} else if(secondNode != null && swapMode == 1){
				System.out.print(iterationNumber + ". iteration, better SWAP solution: " + this.getValue(nodes));
				currentNode = nodes.get(firstNodeIndex);
				solution.get(solution.indexOf(currentNode.getR())).deleteCombo(currentNode);		//Delete courses from room
				solution.get(solution.indexOf(secondNode.getR())).deleteCombo(secondNode);
				currentNode.print();
				secondNode.print();
				int neighborIndex = this.getIndex(nodes, secondNode);								
				currentNode.swap(secondNode);
				nodes.set(neighborIndex, currentNode);	
				nodes.set(firstNodeIndex, secondNode);
				solution.get(solution.indexOf(currentNode.getR())).addCombo(currentNode);
				solution.get(solution.indexOf(secondNode.getR())).addCombo(secondNode);
				nodes.get(firstNodeIndex).print();
				nodes.get(neighborIndex).print();
				System.out.println(" to " + this.getValue(nodes) + " " + globalMinimum);
				foundBetter = true;
			} else {
				foundBetter = false;
			}
		}
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
				List<TimeSlot> in = getSlotTeacher(day, te, input);				
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
	
	private List<TimeSlot> getSlotTeacher(int day, Teacher t, List<Combo> input){		
		for(Combo c : input){
			if(c.getCourse().getT().getName().equals(t.getName())){
				return c.getCourse().getT().getAvailabilityAtDay(day);
			}
		}
		return null;
	}
	
	public List<Combo> getNeighbors(List<Combo> solution, Combo input){
		List<Combo> output = new ArrayList<Combo>();
		for(Combo c : solution){ 				//get the current courses, which are swapable to the given combo
			if(c.getSize() == input.getSize() && c.getCourse().getT().equals(input.getCourse().getT())){
				output.add(c);		
			}
		}
		for(TimeSlot actual : timeslots){			//Get the empty slots
			if(actual.getSlot()+input.getSize()>=INPUT_SLOTS) continue;
			Combo newCombo = null;
			for(Room r : rooms){
				boolean isBad = false;
				newCombo = new Combo(input.getSize(), actual, r);
				for(Combo c : r.getCourses()){
					if(newCombo.hasConflict(c)){
						isBad = true;
						break;
					}
				}
				if(!isBad && input.getCourse().getT().isAvailable(newCombo.getSlotList())){
					output.add(newCombo);
				}
			}
			
		}
		
		return output;
	}

	
	private List<Integer> getTeacherByCourse(String topicName){
		return courseTeacher.get(topicName);
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
