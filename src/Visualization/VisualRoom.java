package Visualization;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JPanel;

import Datatypes.Course;
import Datatypes.Room;

public class VisualRoom extends JPanel{

	public VisualRoom(Room r)
	{
		setLayout(new GridBagLayout());
		
		for (int i = 0; i < r.getDays(); i++) {
			//TODO: check if slots are 0
			Course current = r.getCourseByPos(i, 0);
			int startSlot = 0;
			for (int j = 0; j < r.getSlots(); j++) {
				if(r.getCourseByPos(i, j) == null)
				{
					if(current != null) addCourse(current, i, startSlot);
					current = r.getCourseByPos(i, j);
					addCourse(current, i, j);
					startSlot = j;
				}
				else if(current != null && r.getCourseByPos(i, j).getName().equals(current.getName())) continue;
				else
				{
					//create visualslot for current
					if(current != null) addCourse(current, i, startSlot);
					//init new current
					current = r.getCourseByPos(i, j);
					startSlot = j;
				}
			}
			//add final course
			if(current != null) addCourse(current, i, startSlot);
		}
	}
	
	private void addCourse(Course c, int x, int y)
	{
		VisualSlot vs = new VisualSlot(c);
		GridBagConstraints gc = new GridBagConstraints();
		gc.weightx = gc.weighty = 1;
		gc.gridx = x;
		gc.gridy = y;
		if(c != null) gc.gridheight = c.getSlots();
		gc.anchor = GridBagConstraints.CENTER;
		gc.fill = GridBagConstraints.BOTH;
		add(vs, gc);		
	}
	
}
