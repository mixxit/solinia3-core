package com.solinia.solinia.Models;

public class SpellType {
	public static final int Nuke = (1 << 0);
	public static final int Heal = (1 << 1);
	public static final int Root = (1 << 2);
	public static final int Buff = (1 << 3);
	public static final int Escape = (1 << 4);
	public static final int Pet = (1 << 5);
	public static final int Lifetap = (1 << 6);
	public static final int Snare = (1 << 7);
	public static final int DOT = (1 << 8);
	public static final int Dispel = (1 << 9);
	public static final int InCombatBuff = (1 << 10);
	public static final int Mez = (1 << 11);
	public static final int Charm = (1 << 12);
	public static final int Slow = (1 << 13);
	public static final int Debuff = (1 << 14);
	public static final int Cure = (1 << 15);
	public static final int Resurrect = (1 << 16);
	public static final int HateRedux = (1 << 17);
	public static final int InCombatBuffSong = (1 << 18); // bard in-combat group/ae buffs
	public static final int OutOfCombatBuffSong = (1 << 19); // bard out-of-combat group/ae buffs
	public static final int PreCombatBuff = (1 << 20);
	public static final int PreCombatBuffSong = (1 << 21);
	public static final int Detrimental = (SpellType.Nuke | SpellType.Root | SpellType.Lifetap | SpellType.Snare | SpellType.DOT | SpellType.Dispel | SpellType.Mez | SpellType.Charm | SpellType.Debuff | SpellType.Slow);
	public static final int Beneficial = (SpellType.Heal | SpellType.Buff | SpellType.Escape | SpellType.Pet | SpellType.InCombatBuff | SpellType.Cure | SpellType.HateRedux | SpellType.InCombatBuffSong | SpellType.OutOfCombatBuffSong | SpellType.PreCombatBuff | SpellType.PreCombatBuffSong);
	public static final int Any = 0xFFFFFFFF;
}
