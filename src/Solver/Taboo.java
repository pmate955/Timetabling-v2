package Solver;

import java.util.List;
import Datatypes.Combo;

public class Taboo {
	private Combo currentNode;
	private List<Combo> changes;
	private Combo secondNode;
	private int secIndex;
	private int firstIndex;
	private int swapMode;
	
	public Taboo(int swapMode, Combo currentNode, Combo secondNode){
		this.swapMode = swapMode;
		this.currentNode = currentNode;
		this.secondNode = secondNode;
	}
	
	public Taboo(int swapMode, int firstIndex, int secIndex) {
		this.swapMode = swapMode;
		this.firstIndex = firstIndex;
		this.secIndex = secIndex;
	}
	
	public Taboo(Combo currentNode, List<Combo> changes){
		this.swapMode = 3;
		this.currentNode = currentNode;
		this.changes = changes;
	}


	
	
}
