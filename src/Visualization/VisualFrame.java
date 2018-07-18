package Visualization;

import java.awt.GridLayout;
import java.sql.Timestamp;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import Datatypes.Room;
import Solver.GreedySolve;
import Solver.Writer;

public class VisualFrame extends JFrame {
	
	private GreedySolve g;
	private JPanel contentPane;
	
	public VisualFrame(GreedySolve g){
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 650, 400);
		Timestamp timestamp = new Timestamp(System.currentTimeMillis());
		setTitle("Solution " + timestamp.toString());
		this.g=g;
		contentPane = new JPanel();
		contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.Y_AXIS));
		this.setContentPane(contentPane);
		setJMenuBar(addMenu());
		this.showSolution(g);
	}

	private JMenuBar addMenu() {
		JMenuBar bar = new JMenuBar();
		JMenu fileMenu = new JMenu("Save");
		JMenuItem save = new JMenuItem("Save txt");
		save.addActionListener((l)->{
			if(g!=null){
				if(Writer.writeFile("out.txt", g.rooms, g.teachers)){
					JOptionPane.showMessageDialog(this, "Saving succesful");
				} else {
					JOptionPane.showMessageDialog(this, "Error while saving");
				};
			}
		});
		fileMenu.add(save);
		bar.add(fileMenu);
		return bar;
	}
	
	private void showSolution(GreedySolve g){
		JPanel p = new JPanel();
		p.setLayout(new GridLayout(0,1));
		contentPane.removeAll();
		JScrollPane scrollPane = new JScrollPane();
		String[] columns = new String[g.INPUT_DAYS];
		for(int i = 0; i < g.INPUT_DAYS; i++) columns[i]="Day " + i;
		for(Room r : g.rooms){
			VisualRoom vr = new VisualRoom(r);
			vr.setBorder(BorderFactory.createTitledBorder("Room: " + r.getName()));
			p.add(vr);
		}
		scrollPane.setViewportView(p);
		this.getContentPane().add(scrollPane);
		this.revalidate();
		this.repaint();
	}
}
