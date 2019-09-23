package com.solinia.solinia.Models;

public class EffectSlot {
	public int SpellId = 0;
	public String Name = "";
	public int Icon;
	public int MemIcon;
	public int NewIcon;
	
	public EffectSlot(int spellId, int icon, int memIcon, int newIcon, String name)
	{
		this.SpellId = spellId;
		this.Icon = icon;
		this.MemIcon = memIcon;
		this.NewIcon = newIcon;
		this.Name = name;
	}

}