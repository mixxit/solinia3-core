package com.solinia.solinia.Models;

import java.util.HashMap;

public class Effects {
	public static final int LongBuffsLimit = 25;
	public static final int ShortBuffsLimit = 12;
	public static final int DiscBuffsLimit = 1;
	public static final int TotalBuffsLimit = LongBuffsLimit + ShortBuffsLimit + DiscBuffsLimit;
	public static final int NPCBuffsLimit = 60;
	public static final int PetBuffsLimit = 30;
	
	HashMap<Integer,EffectSlot> effectSlots = new HashMap<Integer,EffectSlot>();
	
	public Effects() {
		// TODO Auto-generated constructor stub
	}

	public EffectSlot getSlot(int slot) {
		return effectSlots.get(slot);
	}
	
	public HashMap<Integer,EffectSlot> getSlots() {
		return effectSlots;
	}

}
