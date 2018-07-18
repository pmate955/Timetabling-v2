package Solver;

import java.awt.EventQueue;
import java.util.ArrayList;
import java.util.List;

import Datatypes.IndexCombo;
import Visualization.TimeTableFrame;



public class Main {

	public static void main(String[] args) {		
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					TimeTableFrame tf = new TimeTableFrame();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
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
