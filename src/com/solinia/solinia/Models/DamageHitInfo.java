package com.solinia.solinia.Models;

public class DamageHitInfo {

	public SkillType skill = SkillType.HandtoHand;
	public int damage_done = 0;
	public int min_damage = 0;
	public int base_damage = 0;
	public int offense = 0;
	public int tohit = 0;
	public boolean avoided = false;
	public boolean dodged = false;
	public boolean riposted = false;
	public int hand = 0; // 0 = primary, 1 = secondary

}
