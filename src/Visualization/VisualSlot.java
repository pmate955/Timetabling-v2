package Visualization;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.LineBorder;

import Datatypes.Combo;
import Solver.GreedySolve;

public class VisualSlot extends JPanel {
	
	public VisualSlot(Combo c, GreedySolve g) {
		
		setLayout(new BorderLayout());
		setBorder(new LineBorder(Color.BLACK));
		JLabel lbl = null;
		JLabel tLbl = null;
		JLabel curr = null;
		if(c == null){
			lbl = new JLabel("empty");
			tLbl = new JLabel("No teacher");
			curr = new JLabel("-");
		} 
		else{
			lbl = new JLabel(g.courses.get(c.courseIndex).getName());
			tLbl = new JLabel(c.teacherIndex == -1?"No teacher":g.teachers.get(c.teacherIndex).getName());
			if(c.teacherIndex == -1) tLbl.setForeground(Color.RED);
			else tLbl.setForeground(Color.BLUE);
			curr = new JLabel(g.courses.get(c.courseIndex).getCurricula() + "");
		}
		
		lbl.setHorizontalAlignment(JLabel.CENTER);
		lbl.setVerticalAlignment(JLabel.CENTER);		
		add(lbl, BorderLayout.NORTH);
		curr.setHorizontalAlignment(JLabel.CENTER);
		curr.setVerticalAlignment(JLabel.CENTER);
		add(curr, BorderLayout.CENTER);
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
