package com.solinia.solinia.Models;

public class PartyWindowPlayer
{
	public String Name = "";
	public double HealthPercent = 0D;
	public double ManaPercent = 0D;
	
	public PartyWindowPlayer(String name, double healthPercent,double manaPercent)
	{
		this.Name = name;
		this.HealthPercent = healthPercent;
		this.ManaPercent = manaPercent;
	}
}