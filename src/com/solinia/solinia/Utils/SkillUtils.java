package com.solinia.solinia.Utils;

import java.util.ArrayList;
import java.util.List;

import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Interfaces.ISoliniaLivingEntity;
import com.solinia.solinia.Interfaces.ISoliniaRace;
import com.solinia.solinia.Managers.StateManager;
import com.solinia.solinia.Models.SkillType;
import com.solinia.solinia.Models.SpellEffectType;

public class SkillUtils {
	public static boolean IsValidLanguage(SkillType targetLanguage) {
		if (targetLanguage == null || targetLanguage.equals(SkillType.None) || targetLanguage.equals(SkillType.UnknownTongue))
			return false;
		
		try
		{
			for (ISoliniaRace race: StateManager.getInstance().getConfigurationManager().getRaces())
			{
				if (race.getLanguage().equals(targetLanguage))
					return true;
			}
		} catch (CoreStateInitException e)
		{
			
		}
		
		return false;
	}

	
	public static List<String> getSpecialisationSkills() {
		List<String> validSpecialisationSkills = new ArrayList<String>();
		validSpecialisationSkills.add("ABJURATION");
		validSpecialisationSkills.add("ALTERATION");
		validSpecialisationSkills.add("CONJURATION");
		validSpecialisationSkills.add("DIVINATION");
		validSpecialisationSkills.add("EVOCATION");
		return validSpecialisationSkills;
	}
	
	public static boolean IsBardInstrumentSkill(SkillType skill) {
		switch (skill) {
		case BrassInstruments:
		case Singing:
		case StringedInstruments:
		case WindInstruments:
		case PercussionInstruments:
			return true;
		default:
			return false;
		}
	}
	
	public static int getBaseInstrumentSoftCap() {
		// TODO Auto-generated method stub
		return 36;
	}
	public static int getCriticalChanceBonus(ISoliniaLivingEntity entity, SkillType skillType) {
		int critical_chance = 0;

		// All skills + Skill specific
		critical_chance += entity.getSpellBonuses(SpellEffectType.CriticalHitChance)
				+ entity.getAABonuses(SpellEffectType.CriticalHitChance) + entity.getItemBonuses(SpellEffectType.CriticalHitChance)
				;

		// TODO - take items, aa spells etc into account
		if (critical_chance < -100)
			critical_chance = -100;

		return critical_chance;
	}

	public static int getCritDmgMod(SkillType skillType) {
		int critDmg_mod = 0;
		// TODO take aa and item bonuses into affect
		return critDmg_mod;
	}

	public static boolean isValidSkill(String skillname) {
		if (skillname == null || skillname.equals(""))
			return false;
		
		for (SkillType skillType : SkillType.values()) {
			if (skillType.name().toUpperCase().equals(skillname.toUpperCase()))
				return true;
		}

		try {
			for (ISoliniaRace race : StateManager.getInstance().getConfigurationManager().getRaces()) {
				if (skillname.toUpperCase().equals(race.getName().toUpperCase()))
					return true;
			}
		} catch (CoreStateInitException e) {
			return false;
		}

		return false;
	}

	public static SkillType getSkillType(Integer skill) {
		switch (skill) {
		case 0:
			return SkillType.Slashing;
		case 1:
			return SkillType.Crushing;
		case 2:
			return SkillType.TwoHandBlunt;
		case 3:
			return SkillType.TwoHandSlashing;
		case 4:
			return SkillType.Abjuration;
		case 5:
			return SkillType.Alteration;
		case 6:
			return SkillType.ApplyPoison;
		case 7:
			return SkillType.Archery;
		case 8:
			return SkillType.Backstab;
		case 9:
			return SkillType.BindWound;
		case 10:
			return SkillType.Bash;
		case 11:
			return SkillType.Block;
		case 12:
			return SkillType.BrassInstruments;
		case 13:
			return SkillType.Channeling;
		case 14:
			return SkillType.Conjuration;
		case 15:
			return SkillType.Defense;
		case 16:
			return SkillType.Disarm;
		case 17:
			return SkillType.DisarmTraps;
		case 18:
			return SkillType.Divination;
		case 19:
			return SkillType.Dodge;
		case 20:
			return SkillType.DoubleAttack;
		case 21:
			return SkillType.DragonPunch;
		case 22:
			return SkillType.DualWield;
		case 23:
			return SkillType.EagleStrike;
		case 24:
			return SkillType.Evocation;
		case 25:
			return SkillType.FeignDeath;
		case 26:
			return SkillType.FlyingKick;
		case 27:
			return SkillType.Forage;
		case 28:
			return SkillType.HandtoHand;
		case 29:
			return SkillType.Hide;
		case 30:
			return SkillType.Kick;
		case 31:
			return SkillType.Meditation;
		case 32:
			return SkillType.Mend;
		case 33:
			return SkillType.Offense;
		case 34:
			return SkillType.Parry;
		case 35:
			return SkillType.PickLock;
		case 36:
			return SkillType.OneHandPiercing;
		case 37:
			return SkillType.Riposte;
		case 38:
			return SkillType.RoundKick;
		case 39:
			return SkillType.SafeFall;
		case 40:
			return SkillType.SenseHeading;
		case 41:
			return SkillType.Singing;
		case 42:
			return SkillType.Sneak;
		case 43:
			return SkillType.SpecialiseAbjuration;
		case 44:
			return SkillType.SpecialiseAlteration;
		case 45:
			return SkillType.SpecialiseConjuration;
		case 46:
			return SkillType.SpecialiseDivination;
		case 47:
			return SkillType.SpecialiseEvocation;
		case 48:
			return SkillType.PickPockets;
		case 49:
			return SkillType.StringedInstruments;
		case 50:
			return SkillType.Swimming;
		case 51:
			return SkillType.Throwing;
		case 52:
			return SkillType.TigerClaw;
		case 53:
			return SkillType.Tracking;
		case 54:
			return SkillType.WindInstruments;
		case 55:
			return SkillType.Fishing;
		case 56:
			return SkillType.MakePoison;
		case 57:
			return SkillType.Tinkering;
		case 58:
			return SkillType.Research;
		case 59:
			return SkillType.Alchemy;
		case 60:
			return SkillType.Baking;
		case 61:
			return SkillType.Tailoring;
		case 62:
			return SkillType.SenseTraps;
		case 63:
			return SkillType.Blacksmithing;
		case 64:
			return SkillType.Fletching;
		case 65:
			return SkillType.Brewing;
		case 66:
			return SkillType.AlcoholTolerance;
		case 67:
			return SkillType.Begging;
		case 68:
			return SkillType.JewelryMaking;
		case 69:
			return SkillType.Pottery;
		case 70:
			return SkillType.PercussionInstruments;
		case 71:
			return SkillType.Intimidation;
		case 72:
			return SkillType.Berserking;
		case 73:
			return SkillType.Taunt;
		case 74:
			return SkillType.Frenzy;
		case 75:
			return SkillType.RemoveTraps;
		case 76:
			return SkillType.TripleAttack;
		case 77:
			return SkillType.TwoHandPiercing;
		case 78:
			return SkillType.None;
		case 79:
			return SkillType.Count;
		case 80:
			return SkillType.TailRake;
		default:
			return SkillType.None;
		}
	}

	public static int getSkillTypeId(SkillType skillType) {
		switch (skillType) {
		case Slashing:
			return 0;
		case Crushing:
			return 1;
		case TwoHandBlunt:
			return 2;
		case TwoHandSlashing:
			return 3;
		case Abjuration:
			return 4;
		case Alteration:
			return 5;
		case ApplyPoison:
			return 6;
		case Archery:
			return 7;
		case Backstab:
			return 8;
		case BindWound:
			return 9;
		case Bash:
			return 10;
		case Block:
			return 11;
		case BrassInstruments:
			return 12;
		case Channeling:
			return 13;
		case Conjuration:
			return 14;
		case Defense:
			return 15;
		case Disarm:
			return 16;
		case DisarmTraps:
			return 17;
		case Divination:
			return 18;
		case Dodge:
			return 19;
		case DoubleAttack:
			return 20;
		case DragonPunch:
			return 21;
		case DualWield:
			return 22;
		case EagleStrike:
			return 23;
		case Evocation:
			return 24;
		case FeignDeath:
			return 25;
		case FlyingKick:
			return 26;
		case Forage:
			return 27;
		case HandtoHand:
			return 28;
		case Hide:
			return 29;
		case Kick:
			return 30;
		case Meditation:
			return 31;
		case Mend:
			return 32;
		case Offense:
			return 33;
		case Parry:
			return 34;
		case PickLock:
			return 35;
		case OneHandPiercing:
			return 36;
		case Riposte:
			return 37;
		case RoundKick:
			return 38;
		case SafeFall:
			return 39;
		case SenseHeading:
			return 40;
		case Singing:
			return 41;
		case Sneak:
			return 42;
		case SpecialiseAbjuration:
			return 43;
		case SpecialiseAlteration:
			return 44;
		case SpecialiseConjuration:
			return 45;
		case SpecialiseDivination:
			return 46;
		case SpecialiseEvocation:
			return 47;
		case PickPockets:
			return 48;
		case StringedInstruments:
			return 49;
		case Swimming:
			return 50;
		case Throwing:
			return 51;
		case TigerClaw:
			return 52;
		case Tracking:
			return 53;
		case WindInstruments:
			return 54;
		case Fishing:
			return 55;
		case MakePoison:
			return 56;
		case Tinkering:
			return 57;
		case Research:
			return 58;
		case Alchemy:
			return 59;
		case Baking:
			return 60;
		case Tailoring:
			return 61;
		case SenseTraps:
			return 62;
		case Blacksmithing:
			return 63;
		case Fletching:
			return 64;
		case Brewing:
			return 65;
		case AlcoholTolerance:
			return 66;
		case Begging:
			return 67;
		case JewelryMaking:
			return 68;
		case Pottery:
			return 69;
		case PercussionInstruments:
			return 70;
		case Intimidation:
			return 71;
		case Berserking:
			return 72;
		case Taunt:
			return 73;
		case Frenzy:
			return 74;
		case RemoveTraps:
			return 75;
		case TripleAttack:
			return 76;
		case TwoHandPiercing:
			return 77;
		case None:
			return 78;
		case Count:
			return 79;
		case TailRake:
			return 80;
		default:
			return -1;
		}
	}

	
	public static boolean IsTradeskill(SkillType skill)
	{
		switch (skill) {
		case Fishing:
		case MakePoison:
		case Tinkering:
		case Research:
		case Alchemy:
		case Baking:
		case Tailoring:
		case Blacksmithing:
		case Fletching:
		case Brewing:
		case Pottery:
		case JewelryMaking:
			return true;
		default:
			return false;
		}
	}
	
	public static SkillType getSkillType2(String skillName)
	{
		SkillType result = SkillType.None;
		for(SkillType type : SkillType.values())
		{
			if (type.name().toUpperCase().equals(skillName.toUpperCase()))
				return type;
		}
		return result;
	}
}
