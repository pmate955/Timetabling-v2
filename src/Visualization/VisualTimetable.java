package Visualization;

import java.awt.BorderLayout;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.border.EmptyBorder;

import Solver.GreedySolve;

public class VisualTimetable extends JFrame {

	private JPanel contentPane;

	public VisualTimetable(GreedySolve gr) {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		setTitle("Simple TT Visualization");
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout());
		JTabbedPane tPane = new JTabbedPane();
		for (int i = 0; i < gr.rooms.size(); i++) {
	        JComponent roomPanel = new VisualRoom(gr.rooms.get(i));
	        tPane.addTab(gr.rooms.get(i).getName(), roomPanel);
		}
		
		contentPane.add(tPane, BorderLayout.CENTER);
		
		setContentPane(contentPane);
	}

}
