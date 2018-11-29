package com.solinia.solinia.Models;

public enum NumHit {
	None = 0,
	IncomingHitAttempts = 1,  // Attempted incoming melee attacks (hit or miss) on YOU.
	OutgoingHitAttempts = 2,  // Attempted outgoing melee attacks (hit or miss) on YOUR TARGET.
	IncomingSpells = 3,       // Incoming detrimental spells
	OutgoingSpells = 4,       // Outgoing detrimental spells
	OutgoingHitSuccess = 5,   // Successful outgoing melee attack HIT on YOUR TARGET.
	IncomingHitSuccess = 6,   // Successful incoming melee attack HIT on YOU.
	MatchingSpells = 7,       // Any casted spell matching/triggering a focus effect.
	IncomingDamage = 8,       // Successful incoming spell or melee dmg attack on YOU
	ReflectSpell = 9,	 // Incoming Reflected spells.
	DefensiveSpellProcs = 10, // Defensive buff procs
	OffensiveSpellProcs = 11  // Offensive buff procs
}
