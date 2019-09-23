package com.solinia.solinia.Models;

public class RaceChoice {

	public int RaceId = 0;
	public int ClassId = 0;
	public String RaceName = "";
	public String ClassName = "";
	public String RaceShort = "";
	public String ClassShort = "";
	public String RaceDescription = "";
	public String ClassDescription = "";
	public int STR = 0;
	public int STA = 0;
	public int AGI = 0;
	public int DEX = 0;
	public int INT = 0;
	public int WIS = 0;
	public int CHA = 0;

	public RaceChoice(int raceId, int classId, String raceName, String className, String raceShort, String classShort,
			String raceDescription, String classDescription, int str, int sta, int agi, int dex, int iint, int wis,
			int cha) {
		this.RaceId = raceId;
		this.ClassId = classId;
		this.RaceName = raceName;
		this.ClassName = className;
		this.RaceShort = raceShort;
		this.ClassShort = classShort;
		this.RaceDescription = raceDescription;
		this.ClassDescription = classDescription;
		this.STR = str;
		this.STA = sta;
		this.AGI = agi;
		this.DEX = dex;
		this.INT = iint;
		this.WIS = wis;
		this.CHA = cha;
	}

}
