package com.solinia.solinia.Models;

public class GenericPacketMessage {
	public MemorisedSpells MemorisedSpellSlots = null;
	public SpellbookPage SpellbookPage;

	public void setMemorisedSpellSlots(MemorisedSpells memorisedSpellSlots) {
		this.MemorisedSpellSlots = memorisedSpellSlots;
	}
	
	public void setSpellbookPage(SpellbookPage spellbookPage) {
		this.SpellbookPage = spellbookPage;
	}

}
