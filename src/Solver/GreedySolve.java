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
	public List<Combo> solution;
	public List<Combo> saved;
	public List<Teacher> teachers;
	public List<Course> courses;
	public List<Topic> topics;
	public List<TimeSlot> timeslots;
	public Map<String,List<Integer>> courseTeacher;
	public int INPUT_DAYS;
	public int INPUT_SLOTS;
	public int runCount = 0;
	public int bestValue;
	
	public GreedySolve(String filename){
		this.r = new Reader(filename);
		r.readFile();
		this.INPUT_DAYS = r.days;
		this.INPUT_SLOTS = r.slots;
		this.rooms = new ArrayList<Room>();
		this.saved = new ArrayList<Combo>();
		this.solution = new ArrayList<Combo>();
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
		solution.clear();
		for(Teacher t:teachers) t.clearAvailability();
		this.copyListC(this.courses, r.courses);
	}
	
	public void printSolution(){
		System.out.println("Solution: ");
		System.out.println(this.printSolution(this.solution));;
	}
	
	public void printTeachers(){
		for(Teacher t : teachers) t.print();
	}
	
	public boolean solveBackTrackHard2(List<Course> cs, int courseIndex, List<Combo> solved, List<Combo> notAllowed, List<IndexCombo> used, List<Teacher> teachers,IndexCombo newNode){	
		runCount++;
		if(courseIndex >= cs.size()) return true;							//If there's no more unfixed course, end of the recursion
		Course c = cs.get(courseIndex);									//Else we get the first unfixed course, and trying to fix
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
		Combo combo = new Combo(courseIndex,c.getName(),c.getSlots(),t, rooms.indexOf(r), r.getName());
		if(notAllowed.contains(combo) || erroneus(solved,combo) || t.getSlot()+c.getSlots() > INPUT_SLOTS || c.getCapacity() > r.getCapacity()){
			newNode.slotIndex++;
			return solveBackTrackHard2(cs,courseIndex,solved,notAllowed,used,teachers,newNode);		
		}
		if(teacher.isAvailable(combo.getSlotList())){								//If the course has time/room/teacher, we can add to our solved map
			teachers.get(teacherIndexes.get(newNode.teacherIndex)).addUnavailablePeriod(t, c.getSlots());		//set the teacher unavailable for his course
			combo.teacherIndex = teacherIndexes.get(newNode.teacherIndex);
			solved.add(combo);
			for(int i = 0; i < c.getSlots();i++) used.add(new IndexCombo(newNode.slotIndex+i, newNode.roomIndex,newNode.teacherIndex));			
			
			return solveBackTrackHard2(cs,++courseIndex,solved,notAllowed,used,teachers,new IndexCombo(0,0,0));		//We going down the tree with the next course
		}
		newNode.slotIndex++;
		return solveBackTrackHard2(cs,courseIndex,solved,notAllowed,used,teachers,newNode);		//If we didn't find something, we have to check the next time slots/rooms
	}
	
	
	/*
	public boolean secondPhase2(List<Room> solution, int[] inputArgs){
		List<Combo> tabo = new ArrayList<Combo>();
		List<Combo> nodes = this.getNodes(solution);
		int startValue = this.getValue(nodes);
		int counter = 0;
		for(int i = 0; i < inputArgs[0]; i++){
			if(inputArgs[2]!=1){
				tabo.addAll(nodes.subList(0, inputArgs[1]));
			} else {
				if(counter+inputArgs[1]>=nodes.size()) counter = 0;
				tabo.addAll(nodes.subList(counter, counter+inputArgs[1]));
				counter++;
			}
			
			int endValue = this.solveHillClimb(solution);
			if(endValue<startValue){
				System.out.println("-SAVE--------------------SAVE-------------");
				saveSolution(solution);
				startValue = endValue;
				bestValue = endValue;
			} 
			if(i==inputArgs[0]-1) break;
			this.clearData();
			if(!this.solveBackTrackHard2(this.courses, 0, this.solution, tabo, new ArrayList<IndexCombo>(), this.teachers, new IndexCombo(0,0,0))){
				System.out.println("Not find solution :(");
				return false;
			};
			nodes = this.getNodes(solution);
		}
		return true;
	}
	*/
	public void saveSolution(List<Combo> solution){
		saved.clear();
		for(Combo c : solution){
			saved.add(new Combo(c));
		}
	}
	
	private void resetTeachers(List<Combo> solution) {
		for(Teacher te : teachers) te.clearAvailability();
		for(Combo c : solution){
			teachers.get(c.teacherIndex).addUnavailablePeriod(c.t);
		}
		
	}
	
	public int solveHillClimb(List<Combo> nodes){
		
		int iterationNumber = 0;
		int globalMinimum = this.getValue(nodes);
		System.out.println("Start value: " + globalMinimum);
		boolean foundBetter = true;
		while(foundBetter){
			iterationNumber++;
			int actualNodeIndex = 0;
			int firstNodeIndex = -1;
			Combo secondNode = null;
			Combo currentNode;
			int swapMode = 0;
			globalMinimum = this.getValue(nodes);
			System.out.println("New global: " + globalMinimum);
			while(actualNodeIndex < nodes.size()){
				currentNode = nodes.get(actualNodeIndex);
				List<Combo> neighbors = this.getNeighbors(nodes, currentNode);
				int startValue = this.getValue(nodes);
				for(Combo node : neighbors){
					int localSwapMode = 0;
					int oldValue = this.getValue(nodes);
					if(node.courseIndex==-1){								//Place the combo to the new place
						this.setCourse(node, currentNode);
						nodes.set(actualNodeIndex, node);							
					} else {
						localSwapMode = 1;
						int neighborIndex = this.getIndex(nodes, node);
						this.swap(currentNode, node);
						nodes.set(actualNodeIndex, currentNode);
						nodes.set(neighborIndex, node);	
					}
					int newValue =  this.getValue(nodes);
					if(localSwapMode==1){				//only for switch
						this.swap(currentNode, node);					
						int neighborIndex = this.getIndex(nodes, node);
						nodes.set(actualNodeIndex, currentNode);
						nodes.set(neighborIndex, node);	
				    } else if(localSwapMode == 0){
						nodes.set(actualNodeIndex, currentNode);
						this.setCourse(currentNode, node);
					}
					
					int newOld = this.getValue(nodes);
					if(oldValue != newOld) System.out.println(":( " + oldValue + " -> "+ newValue + " . " + newOld);
					if((newValue < startValue && newValue < globalMinimum)){			//If we found better global value
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
				System.out.println("Global minimum: " + globalMinimum);
				System.out.print(iterationNumber + ". iteration, better solution: " + this.getValue(nodes));
			
				currentNode = nodes.get(firstNodeIndex);
				this.setCourse(secondNode, currentNode);
				nodes.set(firstNodeIndex, secondNode);
				System.out.println(" to " + this.getValue(nodes));
				currentNode.print();
				secondNode.print();
				foundBetter = true;
			} else if(secondNode != null && swapMode == 1){
				System.out.print(iterationNumber + ". iteration, better SWAP solution: " + this.getValue(nodes));
				currentNode = nodes.get(firstNodeIndex);
				currentNode.print();
				secondNode.print();
				int neighborIndex = this.getIndex(nodes, secondNode);	
				this.swap(currentNode, secondNode);
				nodes.set(neighborIndex, currentNode);	
				nodes.set(firstNodeIndex, secondNode);
				nodes.get(firstNodeIndex).print();
				nodes.get(neighborIndex).print();
				System.out.println(" to " + this.getValue(nodes) + " " + globalMinimum);
				foundBetter = true;
			} else {
				foundBetter = false;
			}
		}
		return globalMinimum;
		
	}
	
	private int getIndex(List<Combo> nodes, Combo input){
		for(int i = 0; i < nodes.size(); i++){
			Combo c = nodes.get(i);
			if(c.courseIndex == input.courseIndex) return i;
		}
		return -1;
	}
	
	public int getValue(List<Combo> input){
		int value = 0;
		int[] coursesByRoom = new int[rooms.size()];
		for(Combo combo : input){
			if(combo.getFirstSlot().getDay()==4) value+=4;			//Friday constraint penalyties
			coursesByRoom[combo.roomIndex]+= combo.getSize();
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
				List<TimeSlot> in = te.getAvailabilityAtDay(day);				
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
	
	
	
	
	public List<Combo> getNeighbors(List<Combo> solution, Combo input){
		List<Combo> output = new ArrayList<Combo>();
		for(Combo c : solution){ 				//get the current courses, which are swapable to the given combo
			int firstIndex = input.teacherIndex;
			int cIndex = c.teacherIndex;
			if(!c.equals(input) && c.getSize() == input.getSize() && firstIndex != cIndex){
				if(teachers.get(firstIndex).isAvailable(c.getSlotList()) && teachers.get(cIndex).isAvailable(input.getSlotList())){
					output.add(c);	
				}					
			}
		}
		for(TimeSlot actual : timeslots){			//Get the empty slots
			if(actual.getSlot()+input.getSize()>=INPUT_SLOTS) continue;
			Combo newCombo = null;
			for(Room r : rooms){
				boolean isBad = false;
				newCombo = new Combo(input.getSize(), actual, rooms.indexOf(r), r.getName());
				for(Combo c : this.getComboByRoom(r, solution)){
					if(newCombo.hasConflict(c)){
						isBad = true;
						break;
					}
				}
				if(!isBad && teachers.get(input.teacherIndex).isAvailable(newCombo.getSlotList())){
					output.add(newCombo);
				}
			}
			
		}
		
		return output;
	}

	public List<Combo> getComboByRoom(Room r, List<Combo> solution){
		List<Combo> output = new ArrayList<Combo>();
		for(Combo c : solution){
			if(c.roomIndex == rooms.indexOf(r)) output.add(c);
		}
		return output;
	}
	
	
	private List<Integer> getTeacherByCourse(String topicName){
		return courseTeacher.get(topicName);
	}
	
	

	
	
	public boolean erroneus(List<Combo> solution, Combo c){
		Room r = rooms.get(c.roomIndex);
	/*	for(Room rm : rooms){
			if(rm.getName().equals(c.getR().getName())){
				r = rm;
				break;
			}
		}*/
		if(r!=null){
			for(TimeSlot t : c.getSlotList()){
				if(t.getSlot()>=INPUT_SLOTS) return true;
				if(this.roomIsUsed(solution, c.roomIndex, t)) return true;
			}
		}
		return false;
	}
	
	private boolean roomIsUsed(List<Combo> sol, int roomIndex, TimeSlot t){
		for(Combo c : sol){
			if(c.roomIndex == roomIndex && c.t.contains(t)) return true;
		}
		return false;
	}
	
	private boolean erroneus(List<Combo> nodes){
		for(Combo c1 : nodes){
			for(Combo c2 : nodes){
				if(!c2.equals(c1)){				
					if(c1.contains(c2.getSlotList())&& c1.teacherIndex == c2.teacherIndex){
						return true;
					}
				}
			}
		}
		return false;
	}
	
	private void swap(Combo first, Combo input){
		int c = input.courseIndex;
		int fC = input.teacherIndex;
		int firstIndex = first.teacherIndex;
		int secIndex = input.teacherIndex;
		if(firstIndex != secIndex){
			teachers.get(firstIndex).deleteUnavailablePeriod(first.t);
			teachers.get(firstIndex).addUnavailablePeriod(input.t);
			teachers.get(secIndex).deleteUnavailablePeriod(input.t);
			teachers.get(secIndex).addUnavailablePeriod(first.t);
		}
		input.courseIndex = first.courseIndex;
		first.courseIndex = c;
		input.teacherIndex = first.teacherIndex;
		first.teacherIndex = fC;
	}
	
	private void setCourse(Combo first, Combo c){
		teachers.get(c.teacherIndex).deleteUnavailablePeriod(c.t);		//Delete unavailable periods from last course
		first.courseIndex = c.courseIndex;										//Add course to current
		first.teacherIndex = c.teacherIndex;
		teachers.get(first.teacherIndex).addUnavailablePeriod(first.t);					//Add unavailable periods to teacher
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
	
	public Combo getCourseByPosRoom(List<Combo> sol, Room r, int day, int slot){
		for(Combo c : sol){
			if(c.roomIndex == rooms.indexOf(r) && c.t.contains(new TimeSlot(day,slot))){
				return c;
			}
		}
		return null;
	}
	
	public String printSolution(List<Combo> soltuion){
		String out = "";
		for(Room r : rooms){
			out += "Room: " + r.getName() + "\r\n";
			out += "=============================================================\r\n";
			List<Combo> combosByR = this.getComboByRoom(r, soltuion);
			for(int slot = 0; slot < INPUT_SLOTS; slot++){
				for(int day = 0; day < INPUT_DAYS; day++){
					TimeSlot t = new TimeSlot(day,slot);
					boolean foundCombo = false;
					for(Combo c : combosByR){
						if(c.t.contains(t)) {
							out += t.toString() + " Name: " + courses.get(c.courseIndex).getName() + " Teacher: " + teachers.get(c.teacherIndex).getName() 
									+ " Students: " + courses.get(c.courseIndex).getCapacity() + " slots: " + c.getSize() + " || ";
							
							foundCombo = true;
							break;
						}
					}
					if(!foundCombo) out += t.toString() + " is empty || ";
					if(t.getDay() == 4) out += "\r\n";
				}
			}
		
			out += "=============================================================\r\n";
		}
		return out;
	}
}
