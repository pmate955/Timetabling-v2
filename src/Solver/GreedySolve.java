package Solver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

import Datatypes.Combo;
import Datatypes.Course;
import Datatypes.IndexCombo;
//import Datatypes.Curriculum;
import Datatypes.Room;
import Datatypes.Teacher;
import Datatypes.TimeSlot;
import Datatypes.Topic;

public class GreedySolve implements Runnable{
	public Reader r;
	public ReaderCTT rc;
	public int[] penalties;					//0 - runCount, 1 - fridayPenalty, 2 - differentRoomPenalty, 3 - 1- check compactness
	public int[] args;
	public int bestIteration;
	public boolean isDebug;
	public List<Integer> courseIndexes;
	public List<Room> rooms;
	public List<Combo> solution;
	public List<Combo> saved;
	public List<Teacher> teachers;
	public List<Course> courses;
	public List<Topic> topics;
	public List<TimeSlot> timeslots;
	public Map<String,List<Integer>> courseTeacher;
	public Thread thread;
	public int INPUT_DAYS;
	public int INPUT_SLOTS;
	public int runCount = 0;
	public int bestValue;
	public int softStatus;
	public int softMax;
	private boolean useNew;
	
	public GreedySolve(String filename, boolean useNewCTT){
		this.courseIndexes = new ArrayList<Integer>();
		this.rooms = new ArrayList<Room>();
		this.saved = new ArrayList<Combo>();
		this.solution = new ArrayList<Combo>();
		this.courses = new ArrayList<Course>();
		this.teachers = new ArrayList<Teacher>();
		this.topics = new ArrayList<Topic>();
		this.penalties = new int[4];
		this.args = new int[7];
		this.isDebug = false;
		this.courseTeacher = new HashMap<String,List<Integer>>();
		this.useNew = useNewCTT;
		if(!useNewCTT) {
			this.r = new Reader(filename);
			r.readFile();
			this.INPUT_DAYS = r.days;
			this.INPUT_SLOTS = r.slots;
			this.bestValue = r.bestValue;	
			this.copyListR(this.rooms, r.rooms);
			this.copyListT(this.teachers, r.teachers);
			this.copyListC(this.courses, r.courses);
			this.copyListTp(this.topics, r.topics);
			this.copyListSaved(this.saved, r.saved);
		} else {
			this.rc = new ReaderCTT(filename);
			rc.read();
			this.INPUT_DAYS = rc.days;
			this.INPUT_SLOTS = rc.slots;
			this.bestValue = rc.bestValue;	
			this.copyListR(this.rooms, rc.rooms);
			this.copyListT(this.teachers, rc.teachers);
			this.copyListC(this.courses, rc.courses);
			this.copyListTp(this.topics, rc.topics);
			this.copyListSaved(this.saved, rc.saved);
		}
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
		this.softMax = courses.size();
	}
	
	public void clearData(){
		solution.clear();
		for(Teacher t:teachers) t.clearAvailability();
	}
	
	public void printSolution(){
		System.out.println("Solution: ");
		System.out.println(this.printSolution(this.solution));;
	}
	
	public void printTeachers(){
		for(Teacher t : teachers) t.print();
	}
	
	
	public boolean fasterFirstPhase(List<Course> cs, int courseIndex, List<Combo> solved, List<Combo> notAllowed, List<IndexCombo> used, List<Teacher> teachers,IndexCombo newNode){	
		runCount++;
		if(courseIndex >= cs.size()) return true;							//If there's no more unfixed course, end of the recursion
		Course c = cs.get(courseIndexes.get(courseIndex));									//Else we get the first unfixed course, and trying to fix
		List<Integer> teacherIndexes = this.getTeacherByCourse(c.getTopicname());
		newNode.slotIndex--;
		do{
			newNode.slotIndex++;
			if(newNode.slotIndex >= timeslots.size()){
				newNode.slotIndex=0;
				newNode.roomIndex++;
			}
			while(c.isUnavailable(timeslots.get(newNode.slotIndex))) {				//Check later
				newNode.slotIndex++;
				if(newNode.slotIndex>=timeslots.size()) {
					newNode.slotIndex=0;
					newNode.roomIndex++;
					break;
				}
			}
			
			if(newNode.roomIndex >= rooms.size()){
				newNode.roomIndex = 0;										//If there is no more room/time, next teacher
				newNode.teacherIndex++;
			}
			if(newNode.teacherIndex>=teacherIndexes.size()) {
				this.printSolution();
				System.out.println("Exit with" + c.getName() + " " + teacherIndexes.size());
				return false;		//If there are no more teacher, return false
			}
		}while(used.contains(newNode));
		TimeSlot t = timeslots.get(newNode.slotIndex);				//We get the time slot
		Room r = rooms.get(newNode.roomIndex);							//and the room
		Teacher teacher = teachers.get(teacherIndexes.get(newNode.teacherIndex));
		Combo combo = new Combo(courseIndexes.get(courseIndex),c.getName(),c.getSlots(),t, rooms.indexOf(r), r.getName());
		if(notAllowed.contains(combo) || erroneus(solved,combo) || t.getSlot()+c.getSlots() > INPUT_SLOTS || c.getCapacity() > r.getCapacity()){
			newNode.slotIndex++;
			return fasterFirstPhase(cs,courseIndex,solved,notAllowed,used,teachers,newNode);		
		}
		if(teacher.isAvailable(combo.getSlotList())){								//If the course has time/room/teacher, we can add to our solved map
			teachers.get(teacherIndexes.get(newNode.teacherIndex)).addUnavailablePeriod(t, c.getSlots());		//set the teacher unavailable for his course
			combo.teacherIndex = teacherIndexes.get(newNode.teacherIndex);
			solved.add(combo);
			for(int i = 0; i < c.getSlots();i++) used.add(new IndexCombo(newNode.slotIndex+i, newNode.roomIndex,newNode.teacherIndex));			
			
			return fasterFirstPhase(cs,++courseIndex,solved,notAllowed,used,teachers,new IndexCombo(0,0,0));		//We going down the tree with the next course
		}
		newNode.slotIndex++;
		return fasterFirstPhase(cs,courseIndex,solved,notAllowed,used,teachers,newNode);		//If we didn't find something, we have to check the next time slots/rooms
	}

	public boolean backtrackFirstPhase(List<Course> cs, int courseIndex, List<Combo> solved, List<Combo> notAllowed, List<IndexCombo> used, List<Teacher> teachers,IndexCombo newNode){	
		runCount++;
		if(isDebug) System.out.println(runCount);
		if(courseIndex >= cs.size()) return true;	
		else {
			Course c = cs.get(courseIndexes.get(courseIndex));									//Else we get the first unfixed course, and trying to fix
			List<Integer> teacherIndexes = this.getTeacherByCourse(c.getTopicname());
			while(hasNext(newNode, used, teacherIndexes.size())){
				TimeSlot t = timeslots.get(newNode.slotIndex);				//We get the time slot
				Room r = rooms.get(newNode.roomIndex);							//and the room
				Teacher teacher = teachers.get(teacherIndexes.get(newNode.teacherIndex));
				Combo combo = new Combo(courseIndexes.get(courseIndex),c.getName(),c.getSlots(),t, rooms.indexOf(r), r.getName());
				if(isValid(combo, solved, t,c,r,teacher)){
					List<IndexCombo> added = new ArrayList<IndexCombo>();
					teachers.get(teacherIndexes.get(newNode.teacherIndex)).addUnavailablePeriod(t, c.getSlots());
					combo.teacherIndex = teacherIndexes.get(newNode.teacherIndex);
					solved.add(combo);
					for(int i = 0; i < c.getSlots();i++){
						IndexCombo act = new IndexCombo(newNode.slotIndex+i, newNode.roomIndex,newNode.teacherIndex); 
						used.add(act);
						added.add(act);
					}
					if(backtrackFirstPhase(cs, ++courseIndex, solved, notAllowed, used, teachers, new IndexCombo(0,0,0))){						
						return true;
					} 
					--courseIndex;
					teachers.get(teacherIndexes.get(newNode.teacherIndex)).deleteUnavailablePeriod(t, c.getSlots());
					solved.remove(combo);
					used.removeAll(added);
				} else {
					
				}
			}
		}
		return false;
	}
	
	public boolean isValid(Combo combo, List<Combo> solved, TimeSlot t, Course c, Room r, Teacher teacher){
		if(erroneus(solved,combo) || t.getSlot()+c.getSlots() > INPUT_SLOTS || c.getCapacity() > r.getCapacity()){
			return false;	
		}
		if(!teacher.isAvailable(combo.getSlotList())){
			return false;
		}
		return true;
	}
	
	public boolean hasNext(IndexCombo newNode, List<IndexCombo> used, int teacherSize){
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
			if(newNode.teacherIndex>=teacherSize) return false;		//If there are no more teacher, return false
		}while(used.contains(newNode));
		return true;
	}
	
	public void generateNormal(){
		this.courseIndexes.clear();
		for(int i = 0; i < courses.size(); i++) this.courseIndexes.add(i);
	}
	
	public void generateRandom(){
		this.courseIndexes.clear();
		while(courseIndexes.size()<courses.size()){
			int num = ThreadLocalRandom.current().nextInt(0, courses.size());
			if(!courseIndexes.contains(num)){
				courseIndexes.add(num);
			}
		}
	}
	
	public boolean isValidList(){
		for(int i = 0; i < courses.size(); i++){
			if(!courseIndexes.contains(i)) {
				System.out.println(i);
				return false;
			}
		}
		return true;
	}
	

	
	public int solver(){
		for(int i = 0; i < 4; i++){
			this.penalties[i] = args[i];
		}
		this.isDebug = (args[5]==1);
		int globalMinimum = -1;
		this.generateNormal();
		if(!this.fasterFirstPhase(this.courses, 0, this.solution, new ArrayList<Combo>(), new ArrayList<IndexCombo>(), this.teachers, new IndexCombo(0,0,0))) {
			System.out.println("Ez már rossz");
		};
		this.saveSolution(solution);
		globalMinimum = this.getValue(solution);
		List<Taboo> taboos = new ArrayList<Taboo>();
		int bestIndex = 0;
		for(int i = 0; i < penalties[0]; i++){
			int val = this.solveHillClimb(solution, taboos);
			if(val < globalMinimum){
				for(Course c : courses){
					boolean found = false;
					for(Combo com : solution){
						if(courses.get(com.courseIndex).equals(c)){
							found = true;
							break;
						}
					}
					if(!found) System.out.println("Error, not found " + c.toString());
				}
				this.saveSolution(solution);
				globalMinimum = val;
				bestIndex = i;
			}
			this.clearData();
			if(args[4] == 1){
				this.generateRandom();
			}
			if(args[6]==1){
				this.backtrackFirstPhase(this.courses, 0, this.solution, new ArrayList<Combo>(), new ArrayList<IndexCombo>(), this.teachers, new IndexCombo(0,0,0));
			} else {
				while(!this.fasterFirstPhase(this.courses, 0, this.solution, new ArrayList<Combo>(), new ArrayList<IndexCombo>(), this.teachers, new IndexCombo(0,0,0))){
					System.out.println("Regen");
					if(args[4] == 1){
						this.generateRandom();
					}
					this.clearData();
				}
			}
		}
		this.bestValue = globalMinimum;
		return bestIndex;
	}
	
	public void saveSolution(List<Combo> solution){
		saved.clear();
		for(Combo c : solution){
			saved.add(new Combo(c));
		}
	}
	
	public void testPhase(){
		for(int i = 0; i < 10; i++){
			this.generateRandom();
			if(!this.isValidList())  {
				System.out.println("Error with list");
			}
			if(!this.backtrackFirstPhase(this.courses, 0, this.solution, new ArrayList<Combo>(), new ArrayList<IndexCombo>(), this.teachers, new IndexCombo(0,0,0))){
				for(int j = 0; j < courses.size();j++) System.out.print(courseIndexes.get(j) + " ");
				System.out.println("");
				this.clearData();
				this.backtrackFirstPhase(this.courses, 0, this.solution, new ArrayList<Combo>(), new ArrayList<IndexCombo>(), this.teachers, new IndexCombo(0,0,0));
				break;
			} else {
				System.out.println("Win" + this.solution.size());
				for(Course c : courses){
					boolean found = false;
					for(Combo com : solution){
						if(courses.get(com.courseIndex).equals(c)){
							found = true;
							break;
						}
					}
					if(!found) System.out.println("Error, not found " + c.toString());
				}
				
			}
			this.clearData();
		}
	}
	
	private void resetTeachers(List<Combo> solution) {
		for(Teacher te : teachers) te.clearAvailability();
		for(Combo c : solution){
			teachers.get(c.teacherIndex).addUnavailablePeriod(c.t);
		}
		
	}
	
	public int solveHillClimb(List<Combo> nodes, List<Taboo> taboos){
		
		int iterationNumber = 0;
		int globalMinimum = this.getValue(nodes);
		boolean foundBetter = true;
		while(foundBetter){
			iterationNumber++;
			if(isDebug) System.out.println(iterationNumber);
			int actualNodeIndex = 0;
			int firstNodeIndex = -1;
			Combo secondNode = null;
			Combo currentNode;
			List<Combo> differentBetterNeighbors = null;
			int swapMode = 0;
			globalMinimum = this.getValue(nodes);
			while(actualNodeIndex < nodes.size()){
				currentNode = nodes.get(actualNodeIndex);
				List<Combo> neighbors = this.getNeighbors(nodes, currentNode);
				int startValue = this.getValue(nodes);
				//if(isDebug) System.out.println("Mid cyc " + actualNodeIndex);
				this.softStatus = actualNodeIndex;
				for(Combo node : neighbors){
					int localSwapMode = 0;
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
					Taboo tb = new Taboo(localSwapMode,currentNode,node);
					if(!taboos.contains(tb) && (newValue < startValue && newValue < globalMinimum)){			//If we found better global value
						firstNodeIndex=actualNodeIndex;
						globalMinimum = newValue;
						startValue = newValue;
						secondNode = node;
						swapMode = localSwapMode;
					}
				}
				List<List<Combo>> diffNeighbors = this.getDifferentNeighbors(nodes, currentNode);
				for(List<Combo> list: diffNeighbors){
					this.changeDifferentNeighbors(currentNode, list);
					int newValue = this.getValue(nodes);
					this.changeDifferentNeighbors(currentNode, list);
					Taboo tb = new Taboo(currentNode,list);
					if(!taboos.contains(tb) && newValue < startValue && newValue < globalMinimum){
						firstNodeIndex=actualNodeIndex;
						globalMinimum = newValue;
						startValue = newValue;
						differentBetterNeighbors = list;
						swapMode = 3;
					}
				}
				
				actualNodeIndex++;
			}
			if(secondNode != null && swapMode == 0){							//Better course pair to swap
				if(isDebug) System.out.print(iterationNumber + ". iteration, better solution: " + this.getValue(nodes));			
				currentNode = nodes.get(firstNodeIndex);
				taboos.add(new Taboo(swapMode,currentNode, secondNode));
				this.setCourse(secondNode, currentNode);
				nodes.set(firstNodeIndex, secondNode);
				if(isDebug) System.out.println(" to " + this.getValue(nodes));
				if(isDebug) currentNode.print();
				if(isDebug) secondNode.print();
				foundBetter = true;
			} else if(secondNode != null && swapMode == 1){
				if(isDebug) System.out.println(iterationNumber + ". iteration, better SWAP solution: " + this.getValue(nodes));
				currentNode = nodes.get(firstNodeIndex);
				taboos.add(new Taboo(swapMode,currentNode, secondNode));
				if(isDebug) currentNode.print();
				if(isDebug) secondNode.print();
				int neighborIndex = this.getIndex(nodes, secondNode);	
				this.swap(currentNode, secondNode);
				nodes.set(neighborIndex, currentNode);	
				nodes.set(firstNodeIndex, secondNode);
				if(isDebug) nodes.get(firstNodeIndex).print();
				if(isDebug) nodes.get(neighborIndex).print();
				if(isDebug) System.out.println(" to " + this.getValue(nodes) + " " + globalMinimum);
				foundBetter = true;
			} else if(swapMode == 3 && differentBetterNeighbors != null){
				if(isDebug) System.out.println(iterationNumber + ". iteration, better Different size solution: " + this.getValue(nodes));
				currentNode = nodes.get(firstNodeIndex);
				taboos.add(new Taboo(currentNode, differentBetterNeighbors));
				if(isDebug) currentNode.print();
				for(Combo c : differentBetterNeighbors){
					if(isDebug) 	c.print();
				}
				this.changeDifferentNeighbors(currentNode, differentBetterNeighbors);
				if(isDebug) 	currentNode.print();
				for(Combo c : differentBetterNeighbors){
					if(isDebug) 	c.print();
				}
				if(isDebug) System.out.println(" TO " + this.getValue(nodes));
				foundBetter = true;
			}else {
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
			if(combo.getFirstSlot().getDay()==4) value+=penalties[1];			//Friday constraint penalties
			coursesByRoom[combo.roomIndex]+= combo.getSize();
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
				//value += this.getPenaltyByTeacherDay(input, te, day);
				int min = 10;
				int max = -1;
				List<TimeSlot> in = te.getAvailabilityAtDay(day);				
				if(in == null) break;
				for(TimeSlot ts : in){
					if(ts.getSlot() < min) min = ts.getSlot();
					if(ts.getSlot() > max) max = ts.getSlot();
				}
				if(min == max){
					if(penalties[3]==1) value += max;
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
	
	private int getPenaltyByTeacherDay(List<Combo> input, Teacher te, int day){
		int output = 0;
		int teacherIndex = teachers.indexOf(te);
		Combo last = null;
		for(Combo c : input){
			if(c.teacherIndex == teacherIndex /*&& c.getFirstSlot().getDay() == day*/){
				if(last == null) last = c;
				else if(last.roomIndex != c.roomIndex){
					output += penalties[2];
					last = c;
				}
			}
		}
		return 0;
	}
	
	public void changeDifferentNeighbors(Combo bigger, List<Combo> neighbor){
		List<TimeSlot> oldBigger = bigger.getSlotList();
		List<TimeSlot> newBigger = new ArrayList<TimeSlot>();	
		teachers.get(bigger.teacherIndex).deleteUnavailablePeriod(bigger.t);
		for(int i = 0; i < neighbor.size(); i++){
			Combo actual = neighbor.get(i);
			int actSize = actual.getSize();
			newBigger.addAll(actual.getSlotList());
			if(actual.courseIndex >= 0) teachers.get(actual.teacherIndex).deleteUnavailablePeriod(actual.t);
			actual.t = new ArrayList<TimeSlot>();
			List<TimeSlot> sub = oldBigger.subList(0, actSize);
			actual.t.addAll(sub);
			if(actual.courseIndex >= 0) teachers.get(actual.teacherIndex).addUnavailablePeriod(actual.t);
			oldBigger.removeAll(sub);
		}
		bigger.setList(newBigger);
		teachers.get(bigger.teacherIndex).addUnavailablePeriod(bigger.t);
		int biggerRoom = bigger.roomIndex;
		bigger.roomIndex = neighbor.get(0).roomIndex;
		for(Combo c : neighbor){
			c.roomIndex = biggerRoom;
		}
	}
	
	
	public List<Combo> getNeighbors(List<Combo> solution, Combo input){
		List<Combo> output = new ArrayList<Combo>();
		for(Combo c : solution){ 				//get the current courses, which are swapable to the given combo
			int firstIndex = input.teacherIndex;
			int cIndex = c.teacherIndex;
			if(!c.equals(input) && c.getSize() == input.getSize() && firstIndex != cIndex && !courses.get(c.courseIndex).isUnavailable(input.t) && !courses.get(input.courseIndex).isUnavailable(c.t)){
				Set<Integer> tmp = new HashSet<Integer>(courses.get(input.courseIndex).getCurricula());
				tmp.retainAll(courses.get(c.courseIndex).getCurricula());
				if(tmp.size() > 0) continue;
				if(teachers.get(firstIndex).isAvailable(c.getSlotList()) && teachers.get(cIndex).isAvailable(input.getSlotList())){
					output.add(c);	
				}					
			}
		}
		for(TimeSlot actual : timeslots){			//Get the empty slots
			if(courses.get(input.courseIndex).isUnavailable(actual)) continue;
			if(actual.getSlot()+input.getSize()>=INPUT_SLOTS) continue;
			Combo newCombo = null;
			for(Room r : rooms){
				boolean isBad = false;
				if(r.getCapacity() < input.getSize()) continue;
				newCombo = new Combo(input.getSize(), actual, rooms.indexOf(r), r.getName());
				for(Combo c : this.getComboByRoom(r, solution)){
					if(newCombo.hasConflict(c)){
						isBad = true;
						break;
					}
				}
				if(!isBad && teachers.get(input.teacherIndex).isAvailable(newCombo.getSlotList())){
					if(r.getCapacity() >= newCombo.getSize() ) {
						if(hasConflictCurriculum(solution, newCombo, input.courseIndex));
						output.add(newCombo);
					}
				}
			}
			
		}
		
		return output;
	}

	public List<List<Combo>> getDifferentNeighbors(List<Combo> solution, Combo input){
		List<List<Combo>> out = new ArrayList<List<Combo>>();
		if(input.getSize() == 1) return out;
		for(Room r : rooms){
			if(r.getCapacity() < input.getSize()) continue;
			for(TimeSlot t : timeslots){
				List<Combo> act = new ArrayList<Combo>();
				boolean isOK = true;
				for(int i = 0; i < input.getSize();){
					if(t.getSlot()+i >= INPUT_SLOTS) {
						isOK = false;
						break;
					}
					Combo actual = this.getCourseByPosRoom(solution, r, t.getDay(), t.getSlot()+i);
					if(actual != null && actual.getSize() == input.getSize()){
						isOK = false;
						break;
					}
					if(actual==null){
						if(teachers.get(input.teacherIndex).isAvailable(new TimeSlot(t.getDay(),t.getSlot()+i))){
							act.add(new Combo(-1,"null",1,new TimeSlot(t.getDay(),t.getSlot()+i),rooms.indexOf(r),r.getName()));
							i++;						//If the actual slot is empty, then check input teacher availability
						} else {					//The input teacher is not available, that's problem
							isOK = false;
							break;
						}
					} else if(actual.getSize()>input.getSize()-i){
						isOK = false;					//If the actual course is too big, break
						break;
					} else {							//The actual combo seems OK, check the teachers
						if(input.teacherIndex == actual.teacherIndex || teachers.get(input.teacherIndex).isAvailable(actual.t) && teachers.get(actual.teacherIndex).isAvailable(input.t.subList(i, i+actual.getSize()))){
							act.add(actual);
							i+=actual.getSize();
						} else {
							isOK = false;
							break;
						}
					}
				}
				if(isOK){
					out.add(act);
				}
			}
		}
		return out;
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
	
	

	private boolean hasConflictCurriculum(List<Combo> solution, Combo in, int courseIndex){
		boolean hasSame = false;
		Course act = courses.get(courseIndex);
		Set<Integer> firstCurricula = act.getCurricula();
		for(Combo c : solution) {
			Course a = courses.get(c.courseIndex);
			if(in.contains(c.t)) {
				Set<Integer> actualCurr = new HashSet<Integer>(a.getCurricula());
				actualCurr.retainAll(firstCurricula);
				if(actualCurr.size() > 0) return true;
			}
		}
		return false;
	}
	
	public boolean erroneus(List<Combo> solution, Combo c){
		Room r = rooms.get(c.roomIndex);
		if(r!=null){
			for(TimeSlot t : c.getSlotList()){
				if(t.getSlot()>=INPUT_SLOTS) return true;
				if(this.roomIsUsed(solution, c.roomIndex, t)) return true;
				for(Combo co : solution) {
					if(co.contains(c.t)) {
						Set<Integer> tmp = new HashSet<Integer>(courses.get(c.courseIndex).getCurricula());
						tmp.retainAll(courses.get(co.courseIndex).getCurricula());
						if(tmp.size() > 0) {
							return true;
						}
					}
				}
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
	
	private void copyListSaved(List<Combo> dest, List<Combo> src){
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
			out += "Room: " + r.getName() + " " + r.getCapacity() + "\r\n";
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
					if(t.getDay() == INPUT_DAYS-1) out += "\r\n";
				}
			}
		
			out += "=============================================================\r\n";
		}
		return out;
	}
	
	public String save(){
		String out = "";
		if(!useNew) {
			out += r.readed;
			for(Combo c : saved){
				out += "Combo;" + c.courseIndex + ";" + courses.get(c.courseIndex).getName() + ";" + c.getSize() + ";" + c.getFirstSlot().getDay() + ";" 
						+ c.getFirstSlot().getSlot() + ";" + c.roomIndex + ";" + rooms.get(c.roomIndex).getName() + ";" + c.teacherIndex +"\r\n";
			}
			out += "BestValue;" + this.bestValue + "\r\n";
		} else {
			out += rc.readed.toString();
			for(Combo c : saved) {
				out += courses.get(c.courseIndex).getTopicname() + " " + rooms.get(c.roomIndex).getName() + " " + c.getFirstSlot().getDay() + " " + c.getFirstSlot().getSlot() + "\r\n";
			}
		}
		return out;
	}
	
	public void setArgs(int[] in){
		for(int i = 0; i < args.length; i++){
			this.args[i] = in[i];
		}
	}

	@Override
	public void run() {
		this.bestIteration = this.solver();		
	}
}