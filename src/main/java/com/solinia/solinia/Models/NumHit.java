package com.solinia.solinia.Models;

public enum NumHit {
	None,
	IncomingHitAttempts,  // Attempted incoming melee attacks (hit or miss) on YOU.
	OutgoingHitAttempts,  // Attempted outgoing melee attacks (hit or miss) on YOUR TARGET.
	IncomingSpells,       // Incoming detrimental spells
	OutgoingSpells,       // Outgoing detrimental spells
	OutgoingHitSuccess,   // Successful outgoing melee attack HIT on YOUR TARGET.
	IncomingHitSuccess,   // Successful incoming melee attack HIT on YOU.
	MatchingSpells,       // Any casted spell matching/triggering a focus effect.
	IncomingDamage,       // Successful incoming spell or melee dmg attack on YOU
	ReflectSpell,	 // Incoming Reflected spells.
	DefensiveSpellProcs, // Defensive buff procs
	OffensiveSpellProcs  // Offensive buff procs
}
