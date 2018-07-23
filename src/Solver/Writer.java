package Solver;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import Datatypes.Room;
import Datatypes.Teacher;

public class Writer {
	
	public static boolean writeFile(String path, GreedySolve g){
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(new File(path)));
			bw.write(g.printSolution(g.solution));
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

}
