package Visualization;

import java.awt.BorderLayout;
import java.awt.Color;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.LineBorder;

import Datatypes.Course;
import Datatypes.Teacher;

public class VisualSlot extends JPanel {
	
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
			tLbl = new JLabel(c.getTeacherIndex() == -1?"No teacher":c.getTeacherName() + " " + c.getTeacherIndex());
			if(c.getTeacherIndex() == -1) tLbl.setForeground(Color.RED);
			else tLbl.setForeground(Color.BLUE);
		}
		
		lbl.setHorizontalAlignment(JLabel.CENTER);
		lbl.setVerticalAlignment(JLabel.CENTER);		
		add(lbl, BorderLayout.CENTER);
		tLbl.setHorizontalAlignment(JLabel.CENTER);
		tLbl.setVerticalAlignment(JLabel.CENTER);
		add(tLbl, BorderLayout.SOUTH);
	}
	
	public VisualSlot(String s, Color c) {		
		setLayout(new BorderLayout());
		setBorder(new LineBorder(Color.BLACK));
		setBackground(c);
		JLabel lbl = new JLabel(s);		
		lbl.setHorizontalAlignment(JLabel.CENTER);
		lbl.setVerticalAlignment(JLabel.CENTER);		
		add(lbl, BorderLayout.CENTER);
	}
	
	public VisualSlot(String s1, String s2, Color c) {		
		setLayout(new BorderLayout());
		setBorder(new LineBorder(Color.BLACK));
		setBackground(c);
		JLabel lbl = new JLabel(s1);
		JLabel tLbl = new JLabel(s2);		
		lbl.setHorizontalAlignment(JLabel.CENTER);
		lbl.setVerticalAlignment(JLabel.CENTER);		
		add(lbl, BorderLayout.CENTER);
		tLbl.setHorizontalAlignment(JLabel.CENTER);
		tLbl.setVerticalAlignment(JLabel.CENTER);
		add(tLbl, BorderLayout.SOUTH);
	}
	
	

}
