package Visualization;

import java.awt.GridLayout;
import java.io.File;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.filechooser.FileSystemView;

import Datatypes.Combo;
import Datatypes.IndexCombo;
import Solver.GreedySolve;

public class TimeTableFrame extends JFrame implements Runnable{
	
	private JPanel contentPane;
	private File selectedFile;
	private GreedySolve g;
	private boolean useNewMethod;
	private JSpinner tryCountSpinner;
	private JSpinner switchCountSpinner;
	
	public TimeTableFrame(){
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 650, 400);
		setTitle("Timetable Solver");
		contentPane = new JPanel();
		setJMenuBar(addMenu());
		contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.Y_AXIS));
		
		setContentPane(contentPane);
		this.useNewMethod = true;
		this.setContentPanel();
		this.pack();
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
				g = new GreedySolve(selectedFile.getAbsolutePath());
				SpinnerModel model =
				        new SpinnerNumberModel(1,1,g.courses.size(),1);   
				switchCountSpinner.setModel(model);
			}
		});
		
		JMenuItem exit = new JMenuItem("Exit");
		exit.addActionListener((l)->{
			System.exit(0);
		});
		fileMenu.add(open);
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
	
	private void setContentPanel(){
		JPanel p = new JPanel();
		p.setLayout(new GridLayout(0,1));
		JScrollPane scrollPane = new JScrollPane();
		JPanel softPanel = new JPanel();
		softPanel.setBorder(BorderFactory.createTitledBorder("Soft solver settings"));
		softPanel.setLayout(new GridLayout(3,2));
		tryCountSpinner = new JSpinner();		
		switchCountSpinner = new JSpinner();
		JCheckBox useNewBox = new JCheckBox("Use new shotgun method");
		useNewBox.setSelected(true);
		useNewBox.addActionListener((l)->{

			tryCountSpinner.setEnabled(this.useNewMethod);
			switchCountSpinner.setEnabled(this.useNewMethod);
			this.useNewMethod = !this.useNewMethod;
		});
		
		softPanel.add(useNewBox);
		softPanel.add(new JLabel(""));
		softPanel.add(new JLabel("Iteration number: "));
		softPanel.add(tryCountSpinner);
		softPanel.add(new JLabel("Courses to switch"));
		softPanel.add(switchCountSpinner);
		p.add(softPanel);
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
		if(g.solveBackTrackHard2(g.courses, g.rooms,new ArrayList<Combo>(), bad, g.teachers, new IndexCombo(0,0,0))){
			System.out.println("Success");
			//g.solveHillClimb(g.rooms);
			if(useNewMethod){
				int iterations = (int)tryCountSpinner.getValue();
				int[] args = new int[2];
				args[0] = iterations;
				args[1] = (int)switchCountSpinner.getValue();
				if(iterations > 0) {
					if(!g.secondPhase2(g.rooms,args)){
						JOptionPane.showMessageDialog(this, "Too much iterations, but the best solution you will see ");
					}
				} else {
					JOptionPane.showMessageDialog(this, "Error, not positive iteration number!");
				}
			} else g.solveHillClimb(g.rooms);
			VisualFrame vf = new VisualFrame(g);
			vf.setVisible(true);
		}
		Instant end = Instant.now();				
		System.out.println();
		System.out.println("==========Optimization info============");
		System.out.println(g.runCount + " times started the first phase");
		System.out.println("Time needed: " + Duration.between(start, end)); 
		
	}
	
	
	

}
