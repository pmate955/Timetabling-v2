package Datatypes;

public class IndexCombo {
	private int slotIndex,roomIndex,teacherIndex;

	public IndexCombo(int slotIndex, int roomIndex, int teacherIndex) {
		this.slotIndex = slotIndex;
		this.roomIndex = roomIndex;
		this.teacherIndex = teacherIndex;
	}

	public int getSlotIndex() {
		return slotIndex;
	}

	public int getRoomIndex() {
		return roomIndex;
	}

	public int getTeacherIndex() {
		return teacherIndex;
	}

	@Override
	public String toString() {
		return "IndexCombo [slotIndex=" + slotIndex + ", roomIndex=" + roomIndex + ", teacherIndex=" + teacherIndex
				+ "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + roomIndex;
		result = prime * result + slotIndex;
		result = prime * result + teacherIndex;
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
		IndexCombo other = (IndexCombo) obj;
		if (roomIndex != other.roomIndex)
			return false;
		if (slotIndex != other.slotIndex)
			return false;
		if (teacherIndex != other.teacherIndex)
			return false;
		return true;
	}
	
	
}
