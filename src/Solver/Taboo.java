package Solver;

import java.util.List;
import Datatypes.Combo;

public class Taboo {
	private Combo currentNode;
	private List<Combo> changes;
	private Combo secondNode;
	private int swapMode;
	
	public Taboo(int swapMode, Combo currentNode, Combo secondNode){
		this.swapMode = swapMode;
		this.currentNode = currentNode;
		this.secondNode = secondNode;
	}
	
	public Taboo(Combo currentNode, List<Combo> changes){
		this.swapMode = 3;
		this.currentNode = currentNode;
		this.changes = changes;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((changes == null) ? 0 : changes.hashCode());
		result = prime * result + ((currentNode == null) ? 0 : currentNode.hashCode());
		result = prime * result + ((secondNode == null) ? 0 : secondNode.hashCode());
		result = prime * result + swapMode;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Taboo other = (Taboo) obj;
		if (changes == null) {
			if (other.changes != null)
				return false;
		} else if (!changes.equals(other.changes))
			return false;
		if (currentNode == null) {
			if (other.currentNode != null)
				return false;
		} else if (!currentNode.equals(other.currentNode))
			return false;
		if (secondNode == null) {
			if (other.secondNode != null)
				return false;
		} else if (!secondNode.equals(other.secondNode))
			return false;
		if (swapMode != other.swapMode)
			return false;
		return true;
	}
	
	
}
