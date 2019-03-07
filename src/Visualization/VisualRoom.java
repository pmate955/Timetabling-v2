package Visualization;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JPanel;

import Datatypes.Combo;
import Datatypes.Course;
import Datatypes.Room;
import Solver.GreedySolve;

public class VisualRoom extends JPanel{

	public VisualRoom(GreedySolve g, Room r)
	{
		GridBagLayout gl = new GridBagLayout();
		double[] weights = new double[g.getMaxSlot()];
		for(int i = 0; i < g.getMaxSlot(); i++){
			weights[i] = 2;
		}
		gl.rowWeights = weights;
		setLayout(new GridBagLayout());
		addDummySlot(0,0,"Day/slot",null);
		for(int i = 0; i < g.getMaxSlot(); i++){
			addDummySlot(0,i+1, "Slot", "" + i);
		}
		for(int i = 0; i < g.INPUT_DAYS; i++){
			addDummySlot(i+1, 0, "Day " + i, null);
		}
		
		
		for (int i = 0; i < g.INPUT_DAYS; i++) {
			//TODO: check if slots are 0
			Combo current = g.getCourseByPosRoom(g.saved, r, i, 0);
			int startSlot = 0;
			for (int j = 0; j < g.maxSlotByDay.get(i); j++) {
				if(g.getCourseByPosRoom(g.saved, r, i, j) == null)
				{
					if(current != null) addCourse(current,g, i, startSlot);
					current = g.getCourseByPosRoom(g.saved, r, i, j);
					addCourse(current,g, i, j);
					startSlot = j;
				}
				else if(current != null && g.getCourseByPosRoom(g.saved, r, i, j).courseIndex == current.courseIndex) continue;
				else
				{
					//create visualslot for current
					if(current != null) addCourse(current,g, i, startSlot);
					//init new current
					current = g.getCourseByPosRoom(g.saved, r, i, j);
					startSlot = j;
				}
			}
			//add final course
			if(current != null) addCourse(current,g, i, startSlot);
		}
	}
	
	private void addCourse(Combo c,GreedySolve g, int x, int y)
	{
		VisualSlot vs = new VisualSlot(c,g);
		GridBagConstraints gc = new GridBagConstraints();
		gc.weightx = 1;
		gc.weighty = 1;
		gc.gridx = x+1;
		gc.gridy = y+1;
		if(c != null){
			gc.gridheight = c.getSize();
		}
		gc.anchor = GridBagConstraints.CENTER;
		gc.fill = GridBagConstraints.BOTH;
		add(vs, gc);		
	}
	
	private void addDummySlot(int x, int y, String s1, String s2){
		GridBagConstraints gc = new GridBagConstraints();
		gc.weightx = 1;
		gc.weighty = 1;
		gc.gridx = x;
		gc.gridy = y;
		VisualSlot vs;
		if(s2!=null ){
			vs = new VisualSlot(s1,s2, Color.CYAN);	
		} else {
			vs = new VisualSlot(s1, Color.CYAN);	
		}
		gc.anchor = GridBagConstraints.CENTER;
		gc.fill = GridBagConstraints.BOTH;
		add(vs, gc);		
	}
}
