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
	
}
