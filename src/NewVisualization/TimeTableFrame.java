package NewVisualization;

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
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.filechooser.FileSystemView;

import Datatypes.Course;
import Datatypes.IndexCombo;
import Datatypes.Room;
import Solver.GreedySolve;

public class TimeTableFrame extends JFrame {
	
	private JPanel contentPane;
	private File selectedFile;
	
	public TimeTableFrame(){
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 650, 400);
		setTitle("Timetable Solver");
		contentPane = new JPanel();
		setJMenuBar(addMenu());
		contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.Y_AXIS));
		//setContentPane(contentPane);
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
		fileMenu.add(open);
		JMenu solverMenu = new JMenu("Solver");
		JMenuItem startSolver = new JMenuItem("Start");
		startSolver.addActionListener((l)->{
			GreedySolve g = new GreedySolve(selectedFile.getAbsolutePath());		
			Instant start = Instant.now();
			List<IndexCombo> bad = new ArrayList<IndexCombo>();
			System.out.println("Start to test");
			if(g.solveBackTrackHard2(g.courses, g.rooms, bad, g.teachers, new IndexCombo(0,0,0))){
			//	g.printSolution();
				showSolution(g);
				System.out.println("Success");
			}
			Instant end = Instant.now();				
			System.out.println();
			System.out.println("==========Optimization info============");
			System.out.println(g.runCount + " times started the method");
			System.out.println("Time needed: " + Duration.between(start, end)); 
		});
		solverMenu.add(startSolver);
		
		
		bar.add(fileMenu);
		bar.add(solverMenu);
		return bar;
	}
	
	private void showSolution(GreedySolve g){
		contentPane.removeAll();
		JScrollPane scrollPane = new JScrollPane(contentPane);
		String[] columns = new String[g.INPUT_DAYS];
		for(int i = 0; i < g.INPUT_DAYS; i++) columns[i]="Day " + i;
		for(Room r : g.rooms){
			r.print();
			System.out.println(r.getSlots());
			JPanel panel = new JPanel();
			panel.setBorder(BorderFactory.createTitledBorder(r.getName()));
			Object[][] data = new Object[g.INPUT_SLOTS][g.INPUT_DAYS];
			for(int slot = 0; slot < g.INPUT_SLOTS;slot++){
				for(int day = 0; day < g.INPUT_DAYS; day++){
					Course c = r.getCourseByPos(day, slot);
					if(c == null) data[slot][day]="Empty slot";
					else data[slot][day]=c.getName();
				}
			}
			JTable table = new JTable(data, columns);
			panel.add(table);
			contentPane.add(panel);
		}
		this.getContentPane().add(scrollPane);
		this.revalidate();
		this.repaint();
	}
	
	
	

}
