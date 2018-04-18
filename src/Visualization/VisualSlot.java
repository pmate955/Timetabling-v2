package Visualization;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.LineBorder;

import Datatypes.Course;

public class VisualSlot extends JPanel {

	private String name;
	private int slot;
	private int length;
	
	public VisualSlot(Course c) {
		setLayout(new BorderLayout());
		setBorder(new LineBorder(Color.BLACK));
		JLabel lbl = null;
		JLabel tLbl = null;
		if(c == null){
			lbl = new JLabel("empty");
			tLbl = new JLabel("No teacher");
		}
		else{
			lbl = new JLabel(c.getName());
			tLbl = new JLabel(c.getT() == null?"No teacher":c.getT().getName());
			if(c.getT()==null) tLbl.setForeground(Color.RED);
			else tLbl.setForeground(Color.GREEN);
		}
		
		lbl.setHorizontalAlignment(JLabel.CENTER);
		lbl.setVerticalAlignment(JLabel.CENTER);		
		add(lbl, BorderLayout.CENTER);
		tLbl.setHorizontalAlignment(JLabel.CENTER);
		tLbl.setVerticalAlignment(JLabel.CENTER);
		add(tLbl, BorderLayout.SOUTH);
	}

}
