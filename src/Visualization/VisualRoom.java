package Visualization;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.List;

import javax.swing.JPanel;

import Datatypes.Course;
import Datatypes.Room;
import Datatypes.Teacher;

public class VisualRoom extends JPanel{

	public VisualRoom(Room r, List<Teacher> t)
	{
		GridBagLayout gl = new GridBagLayout();
		double[] weights = new double[r.getSlots()];
		for(int i = 0; i < r.getSlots(); i++){
			weights[i] = 2;
		}
		gl.rowWeights = weights;
		setLayout(new GridBagLayout());
		addDummySlot(0,0,"Day/slot",null);
		for(int i = 0; i < r.getSlots(); i++){
			addDummySlot(0,i+1, "Slot", "" + i);
		}
		for(int i = 0; i < r.getDays(); i++){
			addDummySlot(i+1, 0, "Day " + i, null);
		}
		
		
		for (int i = 0; i < r.getDays(); i++) {
			//TODO: check if slots are 0
			Course current = r.getCourseByPos(i, 0);
			int startSlot = 0;
			for (int j = 0; j < r.getSlots(); j++) {
				if(r.getCourseByPos(i, j) == null)
				{
					if(current != null) addCourse(current, i, startSlot,t);
					current = r.getCourseByPos(i, j);
					addCourse(current, i, j,t);
					startSlot = j;
				}
				else if(current != null && r.getCourseByPos(i, j).getName().equals(current.getName())) continue;
				else
				{
					//create visualslot for current
					if(current != null) addCourse(current, i, startSlot,t);
					//init new current
					current = r.getCourseByPos(i, j);
					startSlot = j;
				}
			}
			//add final course
			if(current != null) addCourse(current, i, startSlot,t);
		}
	}
	
	private void addCourse(Course c, int x, int y, List<Teacher> t)
	{
		VisualSlot vs = new VisualSlot(c,t);
		GridBagConstraints gc = new GridBagConstraints();
		gc.weightx = 1;
		gc.weighty = 1;
		gc.gridx = x+1;
		gc.gridy = y+1;
		if(c != null){
			gc.gridheight = c.getSlots();
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
