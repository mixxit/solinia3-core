package com.solinia.solinia.Models;

public class Ideal {
	public int id;
	public String ideal;
	public String description;
	public AlignmentType alignmentType;
	public Ideal(int id,String ideal, String description, AlignmentType alignmentType) {
		this.id = id;
		this.ideal = ideal;
		this.description = description;
		this.alignmentType = alignmentType;
	}

}
