package Solver;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import Datatypes.Room;

public class Writer {
	
	public static boolean writeFile(String path, List<Room> solution){
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(new File(path)));
			for(Room r : solution){
				bw.write(r.toString());
			}
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

}
