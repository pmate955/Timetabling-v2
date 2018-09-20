package Visualization;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.io.File;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
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
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.filechooser.FileSystemView;

import Datatypes.IndexCombo;
import Solver.GreedySolve;

public class TimeTableFrame extends JFrame implements Runnable{
	
	private JPanel contentPane;
	private File selectedFile;
	private GreedySolve g;
	private JSpinner tryCountSpinner;
	private JSpinner fridayPenalty;
	private JSpinner differentRoomPenalty;
	private JCheckBox checkCompactness;
	private JCheckBox useRandom;
	private JCheckBox useDebugMode;
	private JCheckBox useSlower;
	private JLabel counter;
	private JLabel nameLabel;
	
	public TimeTableFrame(){
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 650, 400);
		setTitle("Timetable Solver");
		contentPane = new JPanel();
		setJMenuBar(addMenu());
		contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.Y_AXIS));
		setContentPane(contentPane);
		this.setContentPanel();
		this.pack();
		setVisible(true);
		
	}

	private JMenuBar addMenu() {
		JMenuBar bar = new JMenuBar();
		JMenu fileMenu = new JMenu("File");		
		JMenuItem exit = new JMenuItem("Exit");
		exit.addActionListener((l)->{
			System.exit(0);
		});
		fileMenu.add(exit);
		bar.add(fileMenu);
		return bar;
	}
	
	private void setContentPanel(){
		JPanel p = new JPanel();
		p.setLayout(new GridLayout(0,1));
		JScrollPane scrollPane = new JScrollPane();
		JPanel solverPanel = new JPanel();
		solverPanel.setBorder(BorderFactory.createTitledBorder("Start solver"));
		solverPanel.setLayout(new GridLayout(1,3));
		JButton open = new JButton("Open txt");
		
		open.addActionListener((l)->{
			JFileChooser jfc = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
			jfc.setAcceptAllFileFilterUsed(false);
			FileNameExtensionFilter filter = new FileNameExtensionFilter("Txt files", "txt");
			jfc.addChoosableFileFilter(filter);
			int returnValue = jfc.showOpenDialog(this);
			if (returnValue == JFileChooser.APPROVE_OPTION) {
				selectedFile = jfc.getSelectedFile();
				g = new GreedySolve(selectedFile.getAbsolutePath());
				nameLabel.setText(selectedFile.getAbsolutePath());
				if(g.saved.size() != 0){
					VisualFrame vf = new VisualFrame(g);
					vf.setVisible(true);
				}
			}
		});
		solverPanel.add(open);
		nameLabel = new JLabel("No file selected");
		solverPanel.add(nameLabel);
		JButton startButton = new JButton("Start solver");
		startButton.addActionListener((e)->{
			if(selectedFile == null) {
				JOptionPane.showMessageDialog(this, "No input file :'(");
			} else {
				Thread t = new Thread(this);
				t.start();
			}
		});
		solverPanel.add(startButton);
		counter = new JLabel("Not running yet");
		solverPanel.add(counter);
		contentPane.add(solverPanel);
		
		JPanel hardPanel = new JPanel();
		hardPanel.setLayout(new BorderLayout());
		hardPanel.setBorder(BorderFactory.createTitledBorder("Hard constraint solver settings"));
		useRandom = new JCheckBox("Use shotgun method");
		hardPanel.add(useRandom, BorderLayout.EAST);
		useSlower = new JCheckBox("Use slower backtrack");
		hardPanel.add(useSlower);
		contentPane.add(hardPanel, BorderLayout.CENTER);
		
		
		JPanel softPanel = new JPanel();
		softPanel.setBorder(BorderFactory.createTitledBorder("Soft solver settings"));
		softPanel.setLayout(new GridLayout(4,2));
		tryCountSpinner = new JSpinner();		
		tryCountSpinner.setValue(1);		
		fridayPenalty = new JSpinner();
		fridayPenalty.setValue(4);
		differentRoomPenalty = new JSpinner();
		differentRoomPenalty.setValue(0);
		checkCompactness = new JCheckBox("Check teacher compactness");
		checkCompactness.setSelected(true);
		useDebugMode = new JCheckBox("Debug mode");
		useDebugMode.setSelected(false);
		softPanel.add(new JLabel("Iteration number: "));
		softPanel.add(tryCountSpinner);
		softPanel.add(new JLabel("Friday penalty"));
		softPanel.add(fridayPenalty);
		softPanel.add(new JLabel("Different room penalty"));
		softPanel.add(differentRoomPenalty);
		softPanel.add(checkCompactness);
		softPanel.add(useDebugMode);
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
		int[] args = new int[7];
		args[0] = (int)tryCountSpinner.getValue();
		args[1] = (int)fridayPenalty.getValue();
		args[2] = (int)differentRoomPenalty.getValue();
		args[3] = (checkCompactness.isSelected()?1:0);
		args[4] = (useRandom.isSelected()?1:0);
		args[5] = (useDebugMode.isSelected()?1:0);
		args[6] = (useSlower.isSelected()?1:0);
		g.setArgs(args);
		Thread tr = new Thread(g);
		tr.start();
		while(tr.isAlive()){
			counter.setText("Running: "  + " " + g.runCount);
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		this.counter.setText("Finished: " + g.runCount);
		//g.testPhase();
		VisualFrame vf = new VisualFrame(g);
		vf.setVisible(true);
		Instant end = Instant.now();				
		System.out.println();
		System.out.println("==========Optimization info============");
		System.out.println(g.runCount + " times started the first phase");
		System.out.println("Time needed: " + Duration.between(start, end)); 
		System.err.println("Best solution found at " + g.bestIteration +". iteration " + g.bestValue);
	}
	
	
	

}
