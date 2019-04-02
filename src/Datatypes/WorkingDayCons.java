package Datatypes;

import java.util.HashSet;
import java.util.Set;

public class WorkingDayCons {
		public boolean[] arr;
		public Set<Integer> rooms;
		public int minWorkingDay;
		
		public WorkingDayCons(int days, int minWorkingDay) {
			arr = new boolean[days];
			this.minWorkingDay = minWorkingDay;
			rooms = new HashSet<>();
		}
		
		public int getPenalty() {
			int out = 0;
			int count = 0;
			for(int i = 0; i < arr.length; i++) {
				if(arr[i]) count++;
			}
			int diff = count - minWorkingDay;
			out = (diff >= 0?0:(-1)*diff*5);
			out += rooms.size()-1;
			return out;
		}
}
