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
	
	public void printTeachers(){
		for(Teacher t : teachers) t.print();
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
			//combo.getCourse().setT(teachers.get(teacherIndexes.get(newNode.teacherIndex)));					//Save the combos
			combo.getCourse().setTeacherIndex(teacherIndexes.get(newNode.teacherIndex), teacher.getName());
			Room rm = solved.get(solved.lastIndexOf(r));
			rm.addCombo(combo);
			for(int i = 0; i < c.getSlots();i++) used.add(new IndexCombo(newNode.slotIndex+i, newNode.roomIndex,newNode.teacherIndex));			
			cs.remove(c);
			return solveBackTrackHard2(cs,solved,used,teachers,new IndexCombo(0,0,0));		//We going down the tree with the next course
		}
		newNode.slotIndex++;
		return solveBackTrackHard2(cs,solved,used,teachers,newNode);		//If we didn't find something, we have to check the next time slots/rooms
	}
	
	
	public void secondPhase(List<Room> solution){
		List<Combo> nodes = this.getNodes(solution);		//Load the current solution
		HashMap<String,List<String>> taboo = new HashMap<String,List<String>>();
		int beforeValue = this.getValue(nodes);
		int n = 1;
		while(true){
			System.out.println("---------------------New Iteration-------------------");
			this.solveHillClimb2(nodes);
			int secValue = this.getValue(nodes);
			if(secValue < beforeValue) {
				this.setSolution(nodes, solution);
				this.resetTeachers(solution);
				beforeValue = secValue;
				n = 1;
				taboo = new HashMap<String,List<String>>();
				System.out.println("Save solution");
			} else {
				nodes = this.getNodes(solution);
			}
			while(secValue!=0 && n < nodes.size() && !this.stepN(n, nodes, taboo)){
				n++;
			};
			System.out.println(n);
			if(n >= nodes.size() || secValue == 0) break;
		}
		
	}
	
	private void resetTeachers(List<Room> solution) {
		for(Teacher te : teachers) te.clearAvailability();
		for(Room r : solution){
			for(Combo c : r.getCourses()){
				teachers.get(c.c.getTeacherIndex()).addUnavailablePeriod(c.t);
			}
		}
		
	}

	private boolean stepN(int n, List<Combo> nodes, HashMap<String,List<String>> taboo){
		int startIndex = n;
		Combo start = nodes.get(startIndex);			//first node
		List<Combo> neighbors = this.getNeighbors2(nodes, start);
		for(Combo node : neighbors){
			if(taboo.get(start.toString()) == null){
				taboo.put(start.toString(),new ArrayList<String>());
			} else if(taboo.get(start.toString()).contains(node.toString())) continue;
			taboo.get(start.toString()).add(node.toString());
			if(node.getCourse()==null){								//Place the combo to the new place
				nodes.set(startIndex, node);
				this.setCourse(node, start);
			} else {
				int neighborIndex = this.getIndex(nodes, node);
				this.swap(start, node);
				nodes.set(startIndex, start);
				nodes.set(neighborIndex, node);	
			}
			return true;
		}
		return false;
		
	}
	
	public List<Combo> getNodes(List<Room> input){
		List<Combo> nodes = new ArrayList<Combo>();			//Get the list of all node
		for(Room r : input){
			nodes.addAll(r.getCourses());			
		}
		return nodes;
	}
	
	public void setSolution(List<Combo> nodes, List<Room> solution){
		for(Room r : solution){
			r.clearRoom();
		}
		for(Combo c : nodes){
			solution.get(solution.indexOf(c.getR())).addCombo(c);
		}
	}
	
	public void solveHillClimb2(List<Combo> nodes){
		
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
				List<Combo> neighbors = this.getNeighbors2(nodes, currentNode);
				int startValue = this.getValue(nodes);
				for(Combo node : neighbors){
					int localSwapMode = 0;
					if(node.getCourse()==null){								//Place the combo to the new place
						nodes.set(actualNodeIndex, node);
						this.setCourse(node, currentNode);
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
						this.setCourse(currentNode, node);
					}
					nodes.set(actualNodeIndex, currentNode);
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
						this.setCourse(node, currentNode);
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
						this.setCourse(currentNode, node);
					}
					nodes.set(actualNodeIndex, currentNode);
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
				System.out.print(iterationNumber + ". iteration, better solution: " + this.getValue(nodes));
			
				currentNode = nodes.get(firstNodeIndex);
				this.setCourse(secondNode, currentNode);
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
				this.swap(currentNode, secondNode);
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
	
	public List<Combo> getNeighbors2(List<Combo> solution, Combo input){
		List<Combo> output = new ArrayList<Combo>();
		for(Combo c : solution){ 				//get the current courses, which are swapable to the given combo
			int firstIndex = input.getCourse().getTeacherIndex();
			int cIndex = c.getCourse().getTeacherIndex();
			if(c.getSize() == input.getSize() && firstIndex != cIndex){
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
				newCombo = new Combo(input.getSize(), actual, r);
				for(Combo c : solution){
					if(c.getR().getName().equals(newCombo.getR().getName()) && newCombo.hasConflict(c)){
						isBad = true;
						break;
					}
				}
				if(!isBad && teachers.get(input.getCourse().getTeacherIndex()).isAvailable(newCombo.getSlotList())){
					output.add(newCombo);
				}
			}
			
		}
		return output;
	}
	
	public List<Combo> getNeighbors(List<Combo> solution, Combo input){
		List<Combo> output = new ArrayList<Combo>();
		for(Combo c : solution){ 				//get the current courses, which are swapable to the given combo
			int firstIndex = input.getCourse().getTeacherIndex();
			int cIndex = c.getCourse().getTeacherIndex();
			if(c.getSize() == input.getSize() && firstIndex != cIndex){
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
				newCombo = new Combo(input.getSize(), actual, r);
				for(Combo c : r.getCourses()){
					if(newCombo.hasConflict(c)){
						isBad = true;
						break;
					}
				}
				if(!isBad && teachers.get(input.getCourse().getTeacherIndex()).isAvailable(newCombo.getSlotList())){
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
	
	private void swap(Combo first, Combo input){
		Course c = input.getCourse();
		int firstIndex = first.getCourse().getTeacherIndex();
		int secIndex = input.getCourse().getTeacherIndex();
		if(firstIndex != secIndex){
			teachers.get(firstIndex).deleteUnavailablePeriod(first.t);
			teachers.get(firstIndex).addUnavailablePeriod(input.t);
			teachers.get(secIndex).deleteUnavailablePeriod(input.t);
			teachers.get(secIndex).addUnavailablePeriod(first.t);
		}
		input.setC(new Course(first.c));
		first.c = new Course(c);
	}
	
	private void setCourse(Combo first, Combo c){
		teachers.get(c.c.getTeacherIndex()).deleteUnavailablePeriod(c.getSlotList());		//Delete unavailable periods from last course
		first.c = new Course(c.c);										//Add course to current
		teachers.get(first.c.getTeacherIndex()).addUnavailablePeriod(first.t);					//Add unavailable periods to teacher
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
