package Datatypes;

import java.util.ArrayList;
import java.util.List;

public class Curriculum {
	private String name;
	
	public Curriculum(String name){
		this.name = name;
	}
	
	
	public String getString(){return this.name;}
	
	public void print(){
		System.out.println("Curriculum: " + this.name);
	}
	
}
