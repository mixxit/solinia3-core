package com.solinia.solinia.Models;

public class Ideal {
	int id;
	String ideal;
	String description;
	AlignmentType alignmentType;
	public Ideal(int id,String ideal, String description, AlignmentType alignmentType) {
		this.id = id;
		this.ideal = ideal;
		this.description = description;
		this.alignmentType = alignmentType;
	}

}
