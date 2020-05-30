package com.solinia.solinia.Utils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import org.bukkit.Location;
import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Managers.StateManager;
import com.solinia.solinia.Models.DisguisePackage;
import com.solinia.solinia.Models.SoliniaZone;
import me.libraryaddict.disguise.disguisetypes.DisguiseType;

public class Utils {
	public static final int CALL_FOR_ASSIST_RANGE = 4;

	public static final int HP_REGEN_CAP = 48;
	public static final int MP_REGEN_CAP = 48;
	public static final int MAX_ENTITY_AGGRORANGE = 100;
	public static final int MAX_LIMIT_INCLUDE = 16;
	public static final int DMG_INVULNERABLE = -5;

	public static float clamp(float val, float min, float max) {
		return Math.max(min, Math.min(max, val));
	}
	
	public static final int TICKS_PER_SECOND = 20;
	public static final long MAXDAYTICK = 24000L;
	public static final double MaxRangeForExperience = 100;

	public static final int HIGHESTSKILL = 255;

	public static final int PetAttackMagicLevel = 11;

	public static final int SPELL_UNKNOWN = 0xFFFF;

	public static final double BaseProcChance = 0.0350000001490;

	public static final float ProcDexDivideBy = 11000;

	public static final int MinHastedDelay = 400;

	public static final double MinRangedAttackDist = 2;

	public static final double DefProcPerMinAgiContrib = 0.075;

	public static final float AvgDefProcsPerMinute = 2;

	public static final boolean ClassicMasterWu = false;

	public static final int DMG_RUNE = -6;

	public static final int FRENZY_REUSETIME = 10;

	public static final int HASTE_CAP = 200;

	public static DisguisePackage getDisguiseTypeFromDisguiseId(int disguiseid, int parameter) {
		switch (disguiseid) {
		case 1:
			return new DisguisePackage(DisguiseType.PLAYER, "human", "k0h"); // human
		case 2:
			return new DisguisePackage(DisguiseType.PLAYER, "barbarian", "Lemoh"); // barbarian
		case 3:
			return new DisguisePackage(DisguiseType.PLAYER, "highhuman", "hiccupwindwalker"); // high human
		case 4:
			return new DisguisePackage(DisguiseType.PLAYER, "woodelf", "Knogi"); // wood elf
		case 5:
			return new DisguisePackage(DisguiseType.PLAYER, "highelf", "MoistWater"); // highelf
		case 6:
			return new DisguisePackage(DisguiseType.PLAYER, "darkelf", "Kenzo74_"); // dark elf
		case 7:
			return new DisguisePackage(DisguiseType.PLAYER, "halfelf", "Maechu_"); // halfelf
		case 8:
			return new DisguisePackage(DisguiseType.PLAYER, "dwarf", "Faenon"); // dwarf
		case 9:
			return new DisguisePackage(DisguiseType.PLAYER, "troll", "Gronghk"); // troll
		case 10:
			return new DisguisePackage(DisguiseType.PLAYER, "ogre", "theorc"); // ogre
		case 11:
			return new DisguisePackage(DisguiseType.PLAYER, "halfling", "Yeus"); // halfling
		case 12:
			return new DisguisePackage(DisguiseType.PLAYER, "gnome", "Yeus"); // gnome
		case 13:
			return new DisguisePackage(DisguiseType.PLAYER, "aviak", "Titus_Vogel"); // aviak
		case 14:
			return new DisguisePackage(DisguiseType.PLAYER, "werewolf", "Apiii"); // werewolf
		case 15:
			return new DisguisePackage(DisguiseType.PLAYER, "Brownie");
		case 16:
			return new DisguisePackage(DisguiseType.PLAYER, "Centaur");
		case 17:
			return new DisguisePackage(DisguiseType.PLAYER, "katxxx");
		case 18:
			return new DisguisePackage(DisguiseType.GIANT, "Giant/Cyclops");
		case 19:
			return new DisguisePackage(DisguiseType.ENDER_DRAGON, "Poison Dragon");
		case 20:
			return new DisguisePackage(DisguiseType.PLAYER, "Doppleganger");
		case 21:
			return new DisguisePackage(DisguiseType.GUARDIAN, "Evil Eye");
		case 22:
			return new DisguisePackage(DisguiseType.SILVERFISH, "Beetle");
		case 23:
			return new DisguisePackage(DisguiseType.PLAYER, "Kerra");
		case 24:
			return new DisguisePackage(DisguiseType.GUARDIAN, "Fish");
		case 25:
			return new DisguisePackage(DisguiseType.VEX, "Fairy");
		case 26:
			return new DisguisePackage(DisguiseType.PLAYER, "Frog man");
		case 27:
			return new DisguisePackage(DisguiseType.PLAYER, "Frogman ghoul");
		case 28:
			return new DisguisePackage(DisguiseType.PLAYER, "Fungusman");
		case 29:
			return new DisguisePackage(DisguiseType.PLAYER, "Gargoyle");
		case 31:
			return new DisguisePackage(DisguiseType.SLIME, "Slime cube");
		case 32:
			return new DisguisePackage(DisguiseType.PLAYER, "ghost", "Orbitly"); // ghost
		case 33:
			return new DisguisePackage(DisguiseType.PLAYER, "Ghoul");
		case 34:
			return new DisguisePackage(DisguiseType.BAT, "Giant Bat");
		case 35:
			return new DisguisePackage(DisguiseType.GUARDIAN, "Giant Eel");
		case 36:
			return new DisguisePackage(DisguiseType.PLAYER, "Giant Rat");
		case 37:
			return new DisguisePackage(DisguiseType.PLAYER, "Giant Snake");
		case 38:
			return new DisguisePackage(DisguiseType.PLAYER, "Giant Spider");
		case 39:
			return new DisguisePackage(DisguiseType.PLAYER, "Gnoll");
		case 40:
			return new DisguisePackage(DisguiseType.PLAYER, "Goblin");
		case 41:
			return new DisguisePackage(DisguiseType.PLAYER, "Gorilla");
		case 42:
			return new DisguisePackage(DisguiseType.WOLF, "Wolf");
		case 43:
			return new DisguisePackage(DisguiseType.POLAR_BEAR, "Bear");
		case 44:
			return new DisguisePackage(DisguiseType.PLAYER, "Human Guards");
		case 45:
			return new DisguisePackage(DisguiseType.WITHER_SKELETON, "Demi Lich");
		case 46:
			return new DisguisePackage(DisguiseType.VEX, "Imp");
		case 47:
			return new DisguisePackage(DisguiseType.PLAYER, "Griffin");
		case 48:
			return new DisguisePackage(DisguiseType.PLAYER, "kobold", "PeterPugger");// kobold
		case 49:
			return new DisguisePackage(DisguiseType.ENDER_DRAGON, "Lava Dragon");
		case 50:
			return new DisguisePackage(DisguiseType.OCELOT, "Lion");
		case 51:
			return new DisguisePackage(DisguiseType.PLAYER, "Lizard Man");
		case 52:
			return new DisguisePackage(DisguiseType.PLAYER, "Mimic");
		case 53:
			return new DisguisePackage(DisguiseType.PLAYER, "minotaur", "_CrimsonBlade_"); // minotaur
		case 54:
			return new DisguisePackage(DisguiseType.PLAYER, "orc", "Jeeorc"); // orc
		case 55:
			return new DisguisePackage(DisguiseType.PLAYER, "Human Beggar");
		case 56:
			return new DisguisePackage(DisguiseType.PLAYER, "Pixie");
		case 57:
			return new DisguisePackage(DisguiseType.SPIDER, "half human spider");
		case 58:
			return new DisguisePackage(DisguiseType.PLAYER, "Fire prince");
		case 59:
			return new DisguisePackage(DisguiseType.PLAYER, "goblin", "AllOgreNow"); // goblin
		case 60:
			return new DisguisePackage(DisguiseType.SKELETON, "Skeleton");
		case 61:
			return new DisguisePackage(DisguiseType.PLAYER, "Shark");
		case 62:
			return new DisguisePackage(DisguiseType.PLAYER, "Elf priestess");
		case 63:
			return new DisguisePackage(DisguiseType.OCELOT, "Tiger");
		case 64:
			return new DisguisePackage(DisguiseType.PLAYER, "treant", "zero_kage"); // treant
		case 65:
			return new DisguisePackage(DisguiseType.PLAYER, "vampire", "Kenzo74_"); // vampire
		case 66:
			return new DisguisePackage(DisguiseType.PLAYER, "Iron knight");
		case 67:
			return new DisguisePackage(DisguiseType.PLAYER, "Human Citizen");
		case 68:
			return new DisguisePackage(DisguiseType.PLAYER, "Tentacle");
		case 69:
			return new DisguisePackage(DisguiseType.PLAYER, "Wisp");
		case 70:
			return new DisguisePackage(DisguiseType.ZOMBIE, "Zombie");
		case 71:
			return new DisguisePackage(DisguiseType.PLAYER, "Citizen");
		case 72:
			return new DisguisePackage(DisguiseType.BOAT, "Ship");
		case 73:
			return new DisguisePackage(DisguiseType.PLAYER, "Launch");
		case 74:
			return new DisguisePackage(DisguiseType.PLAYER, "Piranha");
		case 75:
			if (parameter == 0) // (earth)
			return new DisguisePackage(DisguiseType.IRON_GOLEM, "Earth Elemental");
			if (parameter == 1) // 
			return new DisguisePackage(DisguiseType.SQUID, "Water Elemental");
			if (parameter == 2)
			return new DisguisePackage(DisguiseType.PARROT, "Air Elemental");
			if (parameter == 3) // fire
			return new DisguisePackage(DisguiseType.BLAZE, "Fire Elemental");
		case 76:
			return new DisguisePackage(DisguiseType.OCELOT, "Puma");
		case 77:
			return new DisguisePackage(DisguiseType.PLAYER, "Dark elf Citizen");
		case 78:
			return new DisguisePackage(DisguiseType.PLAYER, "High human Citizen");
		case 79:
			return new DisguisePackage(DisguiseType.VEX, "Bixie");
		case 80:
			return new DisguisePackage(DisguiseType.PLAYER, "Reanimated Hand");
		case 81:
			return new DisguisePackage(DisguiseType.PLAYER, "Halfling Citizen");
		case 82:
			return new DisguisePackage(DisguiseType.PLAYER, "Scarecrow");
		case 83:
			return new DisguisePackage(DisguiseType.PLAYER, "Skunk");
		case 84:
			return new DisguisePackage(DisguiseType.PLAYER, "Snake Elemental");
		case 85:
			return new DisguisePackage(DisguiseType.WITHER, "Spectre");
		case 86:
			return new DisguisePackage(DisguiseType.PLAYER, "Sphinx");
		case 87:
			return new DisguisePackage(DisguiseType.PLAYER, "Armadillo");
		case 88:
			return new DisguisePackage(DisguiseType.PLAYER, "Clockwork Gnome");
		case 89:
			return new DisguisePackage(DisguiseType.PLAYER, "Drake");
		case 90:
			return new DisguisePackage(DisguiseType.PLAYER, "Barbarian Citizen");
		case 91:
			return new DisguisePackage(DisguiseType.PLAYER, "Alligator");
		case 92:
			return new DisguisePackage(DisguiseType.PLAYER, "Troll Citizen");
		case 93:
			return new DisguisePackage(DisguiseType.PLAYER, "ogre", "theorc"); // ogre
		case 94:
			return new DisguisePackage(DisguiseType.PLAYER, "dwarf", "Faenon"); // dwarf
		case 95:
			return new DisguisePackage(DisguiseType.PLAYER, "God of fear");
		case 96:
			return new DisguisePackage(DisguiseType.PLAYER, "Cockatrice");
		case 97:
			return new DisguisePackage(DisguiseType.PLAYER, "Daisy Man");
		case 98:
			return new DisguisePackage(DisguiseType.PLAYER, "elf", "Kenzo74_"); // elf vampire
		case 99:
			return new DisguisePackage(DisguiseType.PLAYER, "darkelf", "Kenzo74_"); // dark elf
		case 100:
			return new DisguisePackage(DisguiseType.PLAYER, "Dervish");
		case 101:
			return new DisguisePackage(DisguiseType.PLAYER, "Efreeti");
		case 102:
			return new DisguisePackage(DisguiseType.PLAYER, "Tadpole");
		case 103:
			return new DisguisePackage(DisguiseType.PLAYER, "Fish man");
		case 104:
			return new DisguisePackage(DisguiseType.PLAYER, "Leech");
		case 105:
			return new DisguisePackage(DisguiseType.PLAYER, "Swordfish");
		case 106:
			return new DisguisePackage(DisguiseType.PLAYER, "Elf guard");
		case 107:
			return new DisguisePackage(DisguiseType.PLAYER, "Mammoth");
		case 108:
			return new DisguisePackage(DisguiseType.ELDER_GUARDIAN, "Floating eye");
		case 109:
			return new DisguisePackage(DisguiseType.PLAYER, "Wasp");
		case 110:
			return new DisguisePackage(DisguiseType.PLAYER, "mermaid", "Suim67"); // mermaid
		case 111:
			return new DisguisePackage(DisguiseType.PLAYER, "Harpie");
		case 112:
			return new DisguisePackage(DisguiseType.PLAYER, "Pixie guard");
		case 113:
			return new DisguisePackage(DisguiseType.PLAYER, "Drixie");
		case 114:
			return new DisguisePackage(DisguiseType.PLAYER, "Ghost Ship");
		case 115:
			return new DisguisePackage(DisguiseType.PLAYER, "Clam");
		case 116:
			return new DisguisePackage(DisguiseType.PLAYER, "Sea Horse");
		case 117:
			return new DisguisePackage(DisguiseType.WITHER, "ghost dwarf", "Faenon"); // ghost dwarf
		case 118:
			return new DisguisePackage(DisguiseType.WITHER, "ghost high human", "Orbitly"); // ghost high human
		case 119:
			return new DisguisePackage(DisguiseType.OCELOT, "Sabertooth Cat");
		case 120:
			return new DisguisePackage(DisguiseType.WOLF, "Wolf Elemental");
		case 121:
			return new DisguisePackage(DisguiseType.PLAYER, "Gorgon");
		case 122:
			return new DisguisePackage(DisguiseType.ENDER_DRAGON, "Dragon Skeleton");
		case 123:
			return new DisguisePackage(DisguiseType.PLAYER, "God of hate");
		case 124:
			return new DisguisePackage(DisguiseType.HORSE, "Unicorn");
		case 125:
			return new DisguisePackage(DisguiseType.PLAYER, "Pegasus");
		case 126:
			return new DisguisePackage(DisguiseType.PLAYER, "Genie");
		case 127:
			return new DisguisePackage(DisguiseType.PLAYER, "Invisible Man");
		case 128:
			return new DisguisePackage(DisguiseType.PLAYER, "lidkim", "CalangoMC"); // iksar
		case 129:
			return new DisguisePackage(DisguiseType.PLAYER, "Scorpion");
		case 130:
			return new DisguisePackage(DisguiseType.PLAYER, "Cat man");
		case 131:
			return new DisguisePackage(DisguiseType.PLAYER, "Sarnak");
		case 132:
			return new DisguisePackage(DisguiseType.PLAYER, "Dragon frog");
		case 133:
			return new DisguisePackage(DisguiseType.PLAYER, "Lycanthrope");
		case 134:
			return new DisguisePackage(DisguiseType.PLAYER, "Mosquito");
		case 135:
			return new DisguisePackage(DisguiseType.PLAYER, "Rhino");
		case 136:
			return new DisguisePackage(DisguiseType.PLAYER, "Half human dragon");
		case 137:
			return new DisguisePackage(DisguiseType.PLAYER, "goblin", "AllOgreNow"); // goblin
		case 138:
			return new DisguisePackage(DisguiseType.PLAYER, "Yeti");
		case 139:
			return new DisguisePackage(DisguiseType.PLAYER, "Scaled human Citizen");
		case 140:
			return new DisguisePackage(DisguiseType.GIANT, "Forest Giant");
		case 141:
			return new DisguisePackage(DisguiseType.BOAT, "Boat");
		case 142:
			return new DisguisePackage(DisguiseType.THROWN_EXP_BOTTLE, "Minor Illusion"); // supposed to be random stuff like chairs etc
		case 143:
			return new DisguisePackage(DisguiseType.PLAYER, "tree", "zero_kage"); // tree
		case 144:
			return new DisguisePackage(DisguiseType.PLAYER, "Badger man");
		case 145:
			return new DisguisePackage(DisguiseType.SLIME, "Goo");
		case 146:
			return new DisguisePackage(DisguiseType.PLAYER, "Spectral Half dragon");
		case 147:
			return new DisguisePackage(DisguiseType.PLAYER, "Spectral Scaled Human");
		case 148:
			return new DisguisePackage(DisguiseType.PLAYER, "Fish");
		case 149:
			return new DisguisePackage(DisguiseType.PLAYER, "Scorpion");
		case 150:
			return new DisguisePackage(DisguiseType.PLAYER, "Plant fiend");
		case 151:
			return new DisguisePackage(DisguiseType.PLAYER, "God of justice");
		case 152:
			return new DisguisePackage(DisguiseType.PLAYER, "God of disease");
		case 153:
			return new DisguisePackage(DisguiseType.PLAYER, "God of mischieve");
		case 154:
			return new DisguisePackage(DisguiseType.PLAYER, "Butterfly Drake");
		case 155:
			return new DisguisePackage(DisguiseType.PLAYER, "Half dragon Skeleton");
		case 156:
			return new DisguisePackage(DisguiseType.PLAYER, "Ratman");
		case 157:
			return new DisguisePackage(DisguiseType.PLAYER, "Wyvern");
		case 158:
			return new DisguisePackage(DisguiseType.PLAYER, "Wurm");
		case 159:
			return new DisguisePackage(DisguiseType.PLAYER, "Devourer");
		case 160:
			return new DisguisePackage(DisguiseType.IRON_GOLEM, "Scaled man Golem");
		case 161:
			return new DisguisePackage(DisguiseType.PLAYER, "Scaled man Skeleton");
		case 162:
			return new DisguisePackage(DisguiseType.PLAYER, "Man Eating Plant");
		case 163:
			return new DisguisePackage(DisguiseType.PLAYER, "Raptor");
		case 164:
			return new DisguisePackage(DisguiseType.PLAYER, "Half dragon Golem");
		case 165:
			return new DisguisePackage(DisguiseType.ENDER_DRAGON, "Water Dragon");
		case 166:
			return new DisguisePackage(DisguiseType.PLAYER, "Scaled man Hand");
		case 167:
			return new DisguisePackage(DisguiseType.PLAYER, "Plant monster");
		case 168:
			return new DisguisePackage(DisguiseType.PLAYER, "Flying Monkey");
		case 169:
			return new DisguisePackage(DisguiseType.PLAYER, "Rhino");
		case 170:
			return new DisguisePackage(DisguiseType.PLAYER, "Snow air elemental");
		case 171:
			return new DisguisePackage(DisguiseType.WOLF, "Dire Wolf");
		case 172:
			return new DisguisePackage(DisguiseType.PLAYER, "Manticore");
		case 173:
			return new DisguisePackage(DisguiseType.PLAYER, "Totem");
		case 174:
			return new DisguisePackage(DisguiseType.WITHER, "Cold Spectre");
		case 175:
			return new DisguisePackage(DisguiseType.PLAYER, "Enchanted Armor");
		case 176:
			return new DisguisePackage(DisguiseType.RABBIT, "Snow Bunny");
		case 177:
			return new DisguisePackage(DisguiseType.PLAYER, "Walrus");
		case 178:
			return new DisguisePackage(DisguiseType.PLAYER, "Rock-gem Men");
		case 179:
			return new DisguisePackage(DisguiseType.PLAYER, "Unknown");
		case 180:
			return new DisguisePackage(DisguiseType.PLAYER, "Unknown");
		case 181:
			return new DisguisePackage(DisguiseType.PLAYER, "bullman", "_CrimsonBlade_"); // bullman
		case 182:
			return new DisguisePackage(DisguiseType.PLAYER, "Faun");
		case 183:
			return new DisguisePackage(DisguiseType.PLAYER, "Coldain");
		case 184:
			return new DisguisePackage(DisguiseType.ENDER_DRAGON, "Dragons");
		case 185:
			return new DisguisePackage(DisguiseType.WITCH, "Hag");
		case 186:
			return new DisguisePackage(DisguiseType.PLAYER, "Hippogriff");
		case 187:
			return new DisguisePackage(DisguiseType.PLAYER, "Siren");
		case 188:
			return new DisguisePackage(DisguiseType.GIANT, "Frost Giant");
		case 189:
			return new DisguisePackage(DisguiseType.GIANT, "Storm Giant");
		case 190:
			return new DisguisePackage(DisguiseType.PLAYER, "Ottermen");
		case 191:
			return new DisguisePackage(DisguiseType.PLAYER, "Walrus Man");
		case 192:
			return new DisguisePackage(DisguiseType.ENDER_DRAGON, "Clockwork Dragon");
		case 193:
			return new DisguisePackage(DisguiseType.PLAYER, "Abhorent");
		case 194:
			return new DisguisePackage(DisguiseType.PLAYER, "Sea Turtle");
		case 195:
			return new DisguisePackage(DisguiseType.ENDER_DRAGON, "Black and White Dragons");
		case 196:
			return new DisguisePackage(DisguiseType.ENDER_DRAGON, "Ghost Dragon");
		case 197:
			return new DisguisePackage(DisguiseType.PLAYER, "Ronnie Test");
		case 198:
			return new DisguisePackage(DisguiseType.ENDER_DRAGON, "Prismatic Dragon");
		case 199:
			return new DisguisePackage(DisguiseType.PLAYER, "Bug");
		case 200:
			return new DisguisePackage(DisguiseType.PLAYER, "Raptor");
		case 201:
			return new DisguisePackage(DisguiseType.PLAYER, "Bug");
		case 202:
			return new DisguisePackage(DisguiseType.PLAYER, "Weird man");
		case 203:
			return new DisguisePackage(DisguiseType.PLAYER, "Worm");
		case 204:
			return new DisguisePackage(DisguiseType.PLAYER, "Unknown");
		case 205:
			return new DisguisePackage(DisguiseType.PLAYER, "Unknown");
		case 206:
			return new DisguisePackage(DisguiseType.PLAYER, "Owlbear");
		case 207:
			return new DisguisePackage(DisguiseType.PLAYER, "Rhino Beetle");
		case 208:
			return new DisguisePackage(DisguiseType.PLAYER, "vampire", "Kenzo74_"); // vampyre
		case 209:
			return new DisguisePackage(DisguiseType.IRON_GOLEM, "Earth Elemental");
		case 210:
			return new DisguisePackage(DisguiseType.PARROT, "Air Elemental");
		case 211:
			return new DisguisePackage(DisguiseType.GUARDIAN, "Water Elemental");
		case 212:
			return new DisguisePackage(DisguiseType.BLAZE, "Fire Elemental");
		case 213:
			return new DisguisePackage(DisguiseType.PLAYER, "Fish");
		case 214:
			return new DisguisePackage(DisguiseType.WITHER, "Horror");
		case 215:
			return new DisguisePackage(DisguiseType.PLAYER, "Small man");
		case 216:
			return new DisguisePackage(DisguiseType.HORSE, "Horse");
		case 217:
			return new DisguisePackage(DisguiseType.PLAYER, "Snake person");
		case 218:
			return new DisguisePackage(DisguiseType.PLAYER, "Fungus");
		case 219:
			return new DisguisePackage(DisguiseType.PLAYER, "vampire", "Kenzo74_"); // vampire
		case 220:
			return new DisguisePackage(DisguiseType.IRON_GOLEM, "Stone golem");
		case 221:
			return new DisguisePackage(DisguiseType.OCELOT, "Red Cheetah");
		case 222:
			return new DisguisePackage(DisguiseType.PLAYER, "Camel");
		case 223:
			return new DisguisePackage(DisguiseType.PLAYER, "Millipede");
		case 224:
			return new DisguisePackage(DisguiseType.PLAYER, "shade", "RainbowF"); // shade
		case 225:
			return new DisguisePackage(DisguiseType.PLAYER, "Exotic plant creature");
		case 226:
			return new DisguisePackage(DisguiseType.PLAYER, "assassin", "epicafroninja"); // assassin
		case 227:
			return new DisguisePackage(DisguiseType.PLAYER, "ghost", "Orbitly"); // ghost
		case 228:
			return new DisguisePackage(DisguiseType.PLAYER, "ogre", "theorc"); // ogre
		case 229:
			return new DisguisePackage(DisguiseType.PLAYER, "Alien");
		case 230:
			return new DisguisePackage(DisguiseType.PLAYER, "4 armed human");
		case 231:
			return new DisguisePackage(DisguiseType.WITHER_SKELETON, "Ghost");
		case 232:
			return new DisguisePackage(DisguiseType.WOLF, "Wolf bat");
		case 233:
			return new DisguisePackage(DisguiseType.IRON_GOLEM, "Ground Shaker");
		case 234:
			return new DisguisePackage(DisguiseType.PLAYER, "Cat man Skeleton");
		case 235:
			return new DisguisePackage(DisguiseType.PLAYER, "Mutant Human");
		case 236:
			return new DisguisePackage(DisguiseType.PLAYER, "Human king");
		case 237:
			return new DisguisePackage(DisguiseType.PLAYER, "Bandit");
		case 238:
			return new DisguisePackage(DisguiseType.PLAYER, "Catman King");
		case 239:
			return new DisguisePackage(DisguiseType.PLAYER, "Catman Guard");
		case 240:
			return new DisguisePackage(DisguiseType.PLAYER, "Teleporter man");
		case 241:
			return new DisguisePackage(DisguiseType.PLAYER, "werewolf", "Apiii"); // were wolf
		case 242:
			return new DisguisePackage(DisguiseType.PLAYER, "naiad", "Suim67"); // naiad
		case 243:
			return new DisguisePackage(DisguiseType.PLAYER, "nymph", "Suim67"); // nymph
		case 244:
			return new DisguisePackage(DisguiseType.PLAYER, "ent", "zero_kage"); // ent
		case 245:
			return new DisguisePackage(DisguiseType.PLAYER, "Fly Man");
		case 246:
			return new DisguisePackage(DisguiseType.PLAYER, "God of water");
		case 247:
			return new DisguisePackage(DisguiseType.PLAYER, "God of fire");
		case 248:
			return new DisguisePackage(DisguiseType.IRON_GOLEM, "Clockwork Golem");
		case 249:
			return new DisguisePackage(DisguiseType.PLAYER, "Clockwork Brain");
		case 250:
			return new DisguisePackage(DisguiseType.PLAYER, "Banshee");
		case 251:
			return new DisguisePackage(DisguiseType.PLAYER, "human hooded guard");
		case 252:
			return new DisguisePackage(DisguiseType.PLAYER, "Unknown");
		case 253:
			return new DisguisePackage(DisguiseType.PLAYER, "fat disease man");
		case 254:
			return new DisguisePackage(DisguiseType.PLAYER, "Fire God Guard");
		case 255:
			return new DisguisePackage(DisguiseType.PLAYER, "GOd of disease");
		case 269:
			return new DisguisePackage(DisguiseType.PLAYER, "Rat creature");
		case 270:
			return new DisguisePackage(DisguiseType.PLAYER, "Disease");
		case 272:
			return new DisguisePackage(DisguiseType.PLAYER, "Mounted Undead");
		case 273:
			return new DisguisePackage(DisguiseType.PLAYER, "Clockwork beast");
		case 274:
			return new DisguisePackage(DisguiseType.PLAYER, "Broken Clockwork");
		case 275:
			return new DisguisePackage(DisguiseType.PLAYER, "Giant Clockwork");
		case 276:
			return new DisguisePackage(DisguiseType.PLAYER, "Clockwork Beetle");
		case 277:
			return new DisguisePackage(DisguiseType.PLAYER, "goblin", "AllOgreNow"); // goblin
		case 278:
			return new DisguisePackage(DisguiseType.PLAYER, "God of storms");
		case 279:
			return new DisguisePackage(DisguiseType.PLAYER, "Blood Raven");
		case 280:
			return new DisguisePackage(DisguiseType.PLAYER, "Gargoyle");
		case 281:
			return new DisguisePackage(DisguiseType.PLAYER, "Mouth monster");
		case 282:
			return new DisguisePackage(DisguiseType.HORSE, "Skeletal Horse");
		case 283:
			return new DisguisePackage(DisguiseType.PLAYER, "God of pain");
		case 284:
			return new DisguisePackage(DisguiseType.PLAYER, "God of fire");
		case 285:
			return new DisguisePackage(DisguiseType.PLAYER, "torment inquisitor");
		case 286:
			return new DisguisePackage(DisguiseType.PLAYER, "necromancer priest");
		case 287:
			return new DisguisePackage(DisguiseType.HORSE, "Nightmare");
		case 288:
			return new DisguisePackage(DisguiseType.PLAYER, "god of war");
		case 289:
			return new DisguisePackage(DisguiseType.PLAYER, "god of tactics");
		case 290:
			return new DisguisePackage(DisguiseType.PLAYER, "god of strategy");
		case 291:
			return new DisguisePackage(DisguiseType.PARROT, "Air Mephit");
		case 292:
			return new DisguisePackage(DisguiseType.IRON_GOLEM, "Earth Mephit");
		case 293:
			return new DisguisePackage(DisguiseType.BLAZE, "Fire Mephit");
		case 294:
			return new DisguisePackage(DisguiseType.WITHER, "Nightmare Mephit");
		case 295:
			return new DisguisePackage(DisguiseType.PLAYER, "god of knowledge");
		case 296:
			return new DisguisePackage(DisguiseType.PLAYER, "god of truth");
		case 297:
			return new DisguisePackage(DisguiseType.SKELETON, "Undead Knight");
		case 298:
			return new DisguisePackage(DisguiseType.PLAYER, "god of earth");
		case 299:
			return new DisguisePackage(DisguiseType.PLAYER, "god of air");
		case 300:
			return new DisguisePackage(DisguiseType.PLAYER, "Fiend");
		case 301:
			return new DisguisePackage(DisguiseType.PLAYER, "Test");
		case 302:
			return new DisguisePackage(DisguiseType.PLAYER, "Crab");
		case 303:
			return new DisguisePackage(DisguiseType.PLAYER, "Phoenix");
		case 304:
			return new DisguisePackage(DisguiseType.ENDER_DRAGON, "Dragon");
		case 305:
			return new DisguisePackage(DisguiseType.POLAR_BEAR, "Bear");
		case 306:
			return new DisguisePackage(DisguiseType.GIANT, "Storm Giant");
		case 307:
			return new DisguisePackage(DisguiseType.GIANT, "Storm Giant");
		case 308:
			return new DisguisePackage(DisguiseType.GIANT, "Storm Giant");
		case 309:
			return new DisguisePackage(DisguiseType.GIANT, "Storm Giant");
		case 310:
			return new DisguisePackage(DisguiseType.BLAZE, "Storm Mana");
		case 311:
			return new DisguisePackage(DisguiseType.BLAZE, "Storm Fire");
		case 312:
			return new DisguisePackage(DisguiseType.BLAZE, "Storm Celestial");
		case 313:
			return new DisguisePackage(DisguiseType.PLAYER, "War Wraith");
		case 314:
			return new DisguisePackage(DisguiseType.PLAYER, "paladin guard");
		case 315:
			return new DisguisePackage(DisguiseType.PLAYER, "Kraken");
		case 316:
			return new DisguisePackage(DisguiseType.PLAYER, "Poison Frog");
		case 317:
			return new DisguisePackage(DisguiseType.PLAYER, "Quezticoatal");
		case 318:
			return new DisguisePackage(DisguiseType.PLAYER, "paladin guard");
		case 319:
			return new DisguisePackage(DisguiseType.PLAYER, "War Boar");
		case 320:
			return new DisguisePackage(DisguiseType.BLAZE, "Efreeti");
		case 321:
			return new DisguisePackage(DisguiseType.PLAYER, "War Boar Unarmored");
		case 322:
			return new DisguisePackage(DisguiseType.PLAYER, "Black Knight");
		case 323:
			return new DisguisePackage(DisguiseType.PLAYER, "Animated Armor");
		case 324:
			return new DisguisePackage(DisguiseType.PLAYER, "Undead Footman");
		case 325:
			return new DisguisePackage(DisguiseType.PLAYER, "Rallos Zek Minion");
		case 326:
			return new DisguisePackage(DisguiseType.SPIDER, "Arachnid");
		case 327:
			return new DisguisePackage(DisguiseType.SPIDER, "Crystal Spider");
		case 328:
			return new DisguisePackage(DisguiseType.PLAYER, "Cage");
		case 329:
			return new DisguisePackage(DisguiseType.PLAYER, "Portal");
		case 330:
			return new DisguisePackage(DisguiseType.PLAYER, "Frogman");
		case 331:
			return new DisguisePackage(DisguiseType.PLAYER, "Troll seaman");
		case 332:
			return new DisguisePackage(DisguiseType.PLAYER, "Troll seaman");
		case 333:
			return new DisguisePackage(DisguiseType.PLAYER, "Troll seaman");
		case 334:
			return new DisguisePackage(DisguiseType.PLAYER, "Spectre Pirate Boss");
		case 335:
			return new DisguisePackage(DisguiseType.PLAYER, "Pirate Boss");
		case 336:
			return new DisguisePackage(DisguiseType.PLAYER, "Pirate Dark Shaman");
		case 337:
			return new DisguisePackage(DisguiseType.PLAYER, "Pirate Officer");
		case 338:
			return new DisguisePackage(DisguiseType.PLAYER, "Gnome Pirate");
		case 339:
			return new DisguisePackage(DisguiseType.PLAYER, "Dark Elf Pirate");
		case 340:
			return new DisguisePackage(DisguiseType.PLAYER, "ogre pirate", "theorc"); // ogre pirate
		case 341:
			return new DisguisePackage(DisguiseType.PLAYER, "Human Pirate");
		case 342:
			return new DisguisePackage(DisguiseType.PLAYER, "High Human Pirate");
		case 343:
			return new DisguisePackage(DisguiseType.PLAYER, "Poison Dart Frog");
		case 344:
			return new DisguisePackage(DisguiseType.PLAYER, "Troll Zombie");
		case 345:
			return new DisguisePackage(DisguiseType.PLAYER, "Sea creature man Land");
		case 346:
			return new DisguisePackage(DisguiseType.PLAYER, "Sea creature man Armored");
		case 347:
			return new DisguisePackage(DisguiseType.PLAYER, "Sea creature man Robed");
		case 348:
			return new DisguisePackage(DisguiseType.PLAYER, "Frogman Mount");
		case 349:
			return new DisguisePackage(DisguiseType.PLAYER, "Frogman Skeleton");
		case 350:
			return new DisguisePackage(DisguiseType.PLAYER, "Undead Frogman");
		case 351:
			return new DisguisePackage(DisguiseType.PLAYER, "Chosen Warrior");
		case 352:
			return new DisguisePackage(DisguiseType.PLAYER, "chosen wizard", "Zelinx"); // chosen wizard
		case 353:
			return new DisguisePackage(DisguiseType.PLAYER, "Lizard creature");
		case 354:
			return new DisguisePackage(DisguiseType.PLAYER, "Greater Lizard creature");
		case 355:
			return new DisguisePackage(DisguiseType.PLAYER, "Lizard creature Boss");
		case 356:
			return new DisguisePackage(DisguiseType.PLAYER, "scaled dog");
		case 357:
			return new DisguisePackage(DisguiseType.PLAYER, "Undead scaled dog");
		case 358:
			return new DisguisePackage(DisguiseType.PLAYER, "Undead lizard creature");
		case 359:
			return new DisguisePackage(DisguiseType.PLAYER, "lesser vampire", "Kenzo74_"); // vampire lesser
		case 360:
			return new DisguisePackage(DisguiseType.PLAYER, "elite vampire", "Kenzo74_"); // vampire elite
		case 361:
			return new DisguisePackage(DisguiseType.PLAYER, "orc", "Jeeorc"); // orc
		case 362:
			return new DisguisePackage(DisguiseType.WITHER_SKELETON, "Bone Golem");
		case 363:
			return new DisguisePackage(DisguiseType.PLAYER, "Huge gargoyle");
		case 364:
			return new DisguisePackage(DisguiseType.PLAYER, "Sand Elf");
		case 365:
			return new DisguisePackage(DisguiseType.PLAYER, "Vampire Master");
		case 366:
			return new DisguisePackage(DisguiseType.PLAYER, "orc", "Jeeorc"); // orc
		case 367:
			return new DisguisePackage(DisguiseType.SKELETON, "Skeleton New");
		case 368:
			return new DisguisePackage(DisguiseType.ZOMBIE, "mummy", "Greng"); // mummy
		case 369:
			return new DisguisePackage(DisguiseType.PLAYER, "goblin", "AllOgreNow"); // goblin
		case 370:
			return new DisguisePackage(DisguiseType.PLAYER, "Insect");
		case 371:
			return new DisguisePackage(DisguiseType.PLAYER, "Frogman Ghost");
		case 372:
			return new DisguisePackage(DisguiseType.PLAYER, "Spinning Air Elemental");
		case 373:
			return new DisguisePackage(DisguiseType.PLAYER, "Shadow Creatue");
		case 374:
			return new DisguisePackage(DisguiseType.IRON_GOLEM, "Golem New");
		case 375:
			return new DisguisePackage(DisguiseType.PLAYER, "Evil Eye New");
		case 376:
			return new DisguisePackage(DisguiseType.PLAYER, "Box");
		case 377:
			return new DisguisePackage(DisguiseType.PLAYER, "Barrel");
		case 378:
			return new DisguisePackage(DisguiseType.PLAYER, "Chest");
		case 379:
			return new DisguisePackage(DisguiseType.PLAYER, "Vase");
		case 380:
			return new DisguisePackage(DisguiseType.PLAYER, "Table");
		case 381:
			return new DisguisePackage(DisguiseType.PLAYER, "Weapons Rack");
		case 382:
			return new DisguisePackage(DisguiseType.PLAYER, "Coffin");
		case 383:
			return new DisguisePackage(DisguiseType.PLAYER, "Bones");
		case 384:
			return new DisguisePackage(DisguiseType.PLAYER, "Joker");
		case 454:
			return new DisguisePackage(DisguiseType.PLAYER, "Apiii");
		case 485:
			return new DisguisePackage(DisguiseType.SKELETON, "Demi Lich");
		default:
			return new DisguisePackage(DisguiseType.UNKNOWN, "Unknown");
		}
	}

	public static String getHttpUrlAsString(String urlLink) {
		try {
			URL url = new URL(urlLink);
			URLConnection con = url.openConnection();
			con.setConnectTimeout(15000);
			con.setReadTimeout(15000);
			con.setRequestProperty("User-Agent",
					"Mozilla/5.0 (Windows; U; Windows NT 6.0; ru; rv:1.9.0.11) Gecko/2009060215 Firefox/3.0.11 (.NET CLR 3.5.30729)");
			con.connect();
			InputStream is = con.getInputStream();
			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));

			StringBuilder response = new StringBuilder();
			String inputLine;

			while ((inputLine = in.readLine()) != null)
				response.append(inputLine);
			is.close();
			in.close();
			return response.toString();
		} catch (Exception e) {
			return "";
		}
	}


	
	
	

	



	public static boolean isLocationInZone(Location location, SoliniaZone zone) {
		return zone.isLocationInside(location);
	}

	public static boolean isLocationInZone(Location location, int zoneId) {
		if (zoneId < 1)
			return false;
		
		try
		{
			SoliniaZone zone = StateManager.getInstance().getConfigurationManager().getZone(zoneId);
			if (zone == null)
				return false;
			return isLocationInZone(location,zone);
		} catch (CoreStateInitException e)
		{
		}
		
		return false;
		
	}

	

}
