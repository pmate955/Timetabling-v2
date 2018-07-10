package Solver;

import java.awt.EventQueue;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Datatypes.Combo;
import Datatypes.Course;
import Datatypes.IndexCombo;
import Datatypes.Room;
import Datatypes.TimeSlot;
import NewVisualization.TimeTableFrame;
import Visualization.VisualTimetable;



public class Main {

	public static void main(String[] args) {
		GreedySolve g = new GreedySolve("time2.txt");		
		Instant start = Instant.now();
		TimeTableFrame tf = new TimeTableFrame();
		
	//	solveNew(g);
		Instant end = Instant.now();				
		System.out.println();
		System.out.println("==========Optimization info============");
		System.out.println(g.runCount + " times started the method");
		System.out.println("Time needed: " + Duration.between(start, end)); 
		
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
		//			VisualTimetable frame = new VisualTimetable(g);
		//			frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	
	public static void solveNew(GreedySolve g){
		HashMap<Room, List<Combo>> solved = new HashMap<Room,List<Combo>>();
		List<IndexCombo> bad = new ArrayList<IndexCombo>();
		System.out.println(g.courses.size());
		if(g.solveBackTrackHard(g.courses,solved,bad,g.teachers,new IndexCombo(0,0,0))){
			for(Map.Entry<Room,List<Combo>>element:solved.entrySet()){
				System.out.println("Room - --- - " + element.getKey().getName());
				for(Combo combo : element.getValue()){
					combo.print();
				}
			}
			for(Room r : g.rooms){
				if(!solved.containsKey(r)) solved.put(r, new ArrayList<Combo>());
			}
			
			//if(g.solveHillClimb2(solved)) System.out.println("Found a better soft solution");;
			g.setSolution(solved);
		}
	}
	
	public static void solveTest(GreedySolve g){
		List<IndexCombo> bad = new ArrayList<IndexCombo>();
		System.out.println("Start to test");
		if(g.solveBackTrackHard2(g.courses, g.rooms, bad, g.teachers, new IndexCombo(0,0,0))){
			g.printSolution();
			System.out.println("Success");
		}
	}
	
}
