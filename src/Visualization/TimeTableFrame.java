package Visualization;

import java.awt.GridLayout;
import java.io.File;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.filechooser.FileSystemView;

import Datatypes.IndexCombo;
import Datatypes.Room;
import Solver.GreedySolve;
import Solver.Writer;

public class TimeTableFrame extends JFrame implements Runnable{
	
	private JPanel contentPane;
	private File selectedFile;
	private GreedySolve g;
	
	public TimeTableFrame(){
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 650, 400);
		setTitle("Timetable Solver");
		contentPane = new JPanel();
		setJMenuBar(addMenu());
		contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.Y_AXIS));
		setContentPane(contentPane);
		setVisible(true);
	}

	private JMenuBar addMenu() {
		JMenuBar bar = new JMenuBar();
		JMenu fileMenu = new JMenu("File");
		JMenuItem open = new JMenuItem("Open txt");
		open.addActionListener((l)->{
			JFileChooser jfc = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
			jfc.setAcceptAllFileFilterUsed(false);
			FileNameExtensionFilter filter = new FileNameExtensionFilter("Txt files", "txt");
			jfc.addChoosableFileFilter(filter);
			int returnValue = jfc.showOpenDialog(this);
			if (returnValue == JFileChooser.APPROVE_OPTION) {
				selectedFile = jfc.getSelectedFile();
				System.out.println(selectedFile.getAbsolutePath());
				
			}
		});
		JMenuItem save = new JMenuItem("Save txt");
		save.addActionListener((l)->{
			if(g!=null){
				if(Writer.writeFile("out.txt", g.rooms)){
					JOptionPane.showMessageDialog(this, "Saving succesful");
				} else {
					JOptionPane.showMessageDialog(this, "Error while saving");
				};
			}
		});
		JMenuItem exit = new JMenuItem("Exit");
		exit.addActionListener((l)->{
			System.exit(0);
		});
		fileMenu.add(open);
		fileMenu.add(save);
		fileMenu.add(exit);
		JMenu solverMenu = new JMenu("Solver");
		JMenuItem startSolver = new JMenuItem("Start");
		startSolver.addActionListener((l)->{
			Thread t = new Thread(this);
			t.start();
		});
		solverMenu.add(startSolver);
		
		
		bar.add(fileMenu);
		bar.add(solverMenu);
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
			r.print();
			VisualRoom vr = new VisualRoom(r);
			vr.setBorder(BorderFactory.createTitledBorder("Room: " + r.getName()));
			p.add(vr);
		}
		scrollPane.setViewportView(p);
		this.getContentPane().add(scrollPane);
		this.revalidate();
		this.repaint();
	}

	@Override
	public void run() {
		g = new GreedySolve(selectedFile.getAbsolutePath());		
		Instant start = Instant.now();
		List<IndexCombo> bad = new ArrayList<IndexCombo>();
		if(g.solveBackTrackHard2(g.courses, g.rooms, bad, g.teachers, new IndexCombo(0,0,0))){
			
			System.out.println("Success");
			g.solveHillClimb(g.rooms);
			showSolution(g);
		}
		Instant end = Instant.now();				
		System.out.println();
		System.out.println("==========Optimization info============");
		System.out.println(g.runCount + " times started the method");
		System.out.println("Time needed: " + Duration.between(start, end)); 
		
	}
	
	
	

}
