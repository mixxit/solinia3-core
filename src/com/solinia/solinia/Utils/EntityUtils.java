package com.solinia.solinia.Utils;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Interfaces.ISoliniaClass;
import com.solinia.solinia.Interfaces.ISoliniaRace;
import com.solinia.solinia.Managers.StateManager;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;

public class EntityUtils {
	private static final List<Material> SAFE_TO_SHARE;
	private static final List<Material> DONT_STAND_ON;
	private static final List<Material> HALF_HEIGHT;
	private static final List<Material> HEIGHT_AND_HALF;
	
	static
	  {
	    SAFE_TO_SHARE = new ArrayList();
	    DONT_STAND_ON = new ArrayList();
	    HALF_HEIGHT = new ArrayList();
	    HEIGHT_AND_HALF = new ArrayList();
	    SAFE_TO_SHARE.add(Material.RED_MUSHROOM);
	    SAFE_TO_SHARE.add(Material.BROWN_MUSHROOM);
	    SAFE_TO_SHARE.add(Material.SNOW);
	    SAFE_TO_SHARE.add(Material.LEGACY_SAPLING);
	    SAFE_TO_SHARE.add(Material.TORCH);
	    SAFE_TO_SHARE.add(Material.REDSTONE);
	    SAFE_TO_SHARE.add(Material.LEGACY_RED_ROSE);
	    SAFE_TO_SHARE.add(Material.LEGACY_YELLOW_FLOWER);
	    SAFE_TO_SHARE.add(Material.WHEAT);
	    SAFE_TO_SHARE.add(Material.PUMPKIN_STEM);
	    SAFE_TO_SHARE.add(Material.LEGACY_WATER_LILY);
	    SAFE_TO_SHARE.add(Material.MELON_STEM);
	    SAFE_TO_SHARE.add(Material.SUGAR_CANE);
	    SAFE_TO_SHARE.add(Material.DEAD_BUSH);
	    SAFE_TO_SHARE.add(Material.LEGACY_LONG_GRASS);
	    SAFE_TO_SHARE.add(Material.ACACIA_SIGN);
	    SAFE_TO_SHARE.add(Material.BIRCH_SIGN);
	    SAFE_TO_SHARE.add(Material.JUNGLE_SIGN);
	    SAFE_TO_SHARE.add(Material.LEGACY_SIGN);
	    SAFE_TO_SHARE.add(Material.OAK_SIGN);
	    SAFE_TO_SHARE.add(Material.SPRUCE_SIGN);
	    SAFE_TO_SHARE.add(Material.DARK_OAK_SIGN);
	    SAFE_TO_SHARE.add(Material.LEGACY_SIGN_POST);
	    SAFE_TO_SHARE.add(Material.STONE_BUTTON);
	    SAFE_TO_SHARE.add(Material.LEVER);
	    SAFE_TO_SHARE.add(Material.LEGACY_RAILS);
	    SAFE_TO_SHARE.add(Material.LEGACY_WOOD_PLATE);
	    SAFE_TO_SHARE.add(Material.LEGACY_STONE_PLATE);
	    
	    DONT_STAND_ON.add(Material.WATER);
	    DONT_STAND_ON.add(Material.LAVA);
	    DONT_STAND_ON.add(Material.FIRE);
	    DONT_STAND_ON.add(Material.CACTUS);
	    DONT_STAND_ON.add(Material.LEGACY_STATIONARY_LAVA);
	    DONT_STAND_ON.add(Material.LEGACY_STATIONARY_WATER);
	    
	    HALF_HEIGHT.add(Material.LEGACY_STEP);
	    HALF_HEIGHT.add(Material.LEGACY_BED);
	    
	    HEIGHT_AND_HALF.add(Material.LEGACY_FENCE);
	    HEIGHT_AND_HALF.add(Material.LEGACY_FENCE_GATE);
	  }
	
	public static boolean isStunned(LivingEntity livingEntity)
	{
		try {
			Timestamp stExpiry = StateManager.getInstance().getEntityManager()
					.getStunned(livingEntity);
			if (stExpiry != null) {
				if (livingEntity instanceof Player) {
					((Player) livingEntity).spigot().sendMessage(ChatMessageType.ACTION_BAR,
							new TextComponent(ChatColor.GRAY + "* You are stunned!"));
				}
				return true;
			}
		} catch (CoreStateInitException e) {

		}
		
		return false;
	}

	
	public static boolean isMezzed(LivingEntity livingEntity)
	{
		try {
			Timestamp mzExpiry = StateManager.getInstance().getEntityManager()
					.getMezzed(livingEntity);
			if (mzExpiry != null) {
				if (livingEntity instanceof Player) {
					((Player)livingEntity).spigot().sendMessage(ChatMessageType.ACTION_BAR,new TextComponent(ChatColor.GRAY + "* You are mezzed!"));
				}
				return true;
			}
		} catch (CoreStateInitException e) {

		}
		
		return false;
	}
	
	public static void tryFollow(Player source, Player target, int preferredDistance) {
		Location stalkerLocation = source.getLocation();
		
		World w = target.getWorld();
		if (!stalkerLocation.getWorld().getName().equalsIgnoreCase(w.getName()))
		{
		  stalkerLocation.setWorld(w);
		}
		
		double deltax = target.getLocation().getX() - stalkerLocation.getX();
		double deltaz = target.getLocation().getZ() - stalkerLocation.getZ();
		
		
		double actualDistance = Math.sqrt(deltax * deltax + deltaz * deltaz);
		double ratio = preferredDistance / actualDistance;
		
		
		double x = target.getLocation().getX() - deltax * ratio;
		double z = target.getLocation().getZ() - deltaz * ratio;
		double y = target.getLocation().getY();
		
		y = makeSafeFollowY(w, x, y, z, source.isFlying());
		
		if (y < 1.0D)
		{
		
		  y = w.getHighestBlockYAt((int)Math.round(Math.floor(x)), (int)Math.round(Math.floor(z)));
		}
		
		double deltay = target.getLocation().getY() - y;
		
		stalkerLocation.setX(x);
	    stalkerLocation.setY(y);
	    stalkerLocation.setZ(z);
	    stalkerLocation.setYaw((float)Utils.calculateYaw(deltax, deltaz));
	    stalkerLocation.setPitch((float)Utils.calculatePitch(deltax, deltay, deltaz));
	    
	    source.teleport(stalkerLocation);
    }
	
	public static boolean safeFollow(World w, int x, int y, int z)
	  {
	    Block bottom = w.getBlockAt(x, y, z);
	    Block top = w.getBlockAt(x, y + 1, z);
	    

	    Material bottomMaterial = bottom.getType();
	    Material topMaterial = top.getType();
	    

	    boolean safe = ((bottom.isEmpty()) || (SAFE_TO_SHARE.contains(bottomMaterial))) && (
	      (top.isEmpty()) || (SAFE_TO_SHARE.contains(topMaterial)));
	    
	    return safe;
	  }

	public static int getSkillCap(String skillname, ISoliniaClass profession, int level, String specialisation) {
		skillname = skillname.toUpperCase();

		if (!Utils.isValidSkill(skillname.toUpperCase()))
			return 0;

		// If the skill being queried happens to be a race name, the cap for
		// language is always 100
		try {
			List<ISoliniaRace> races = StateManager.getInstance().getConfigurationManager().getRaces();
			for (ISoliniaRace race : races) {
				if (race.getName().toUpperCase().equals(skillname.toUpperCase())) {
					return 100;
				}
			}

		} catch (CoreStateInitException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// TODO - Move all these skill cap bonuses to the race configuration
		// classes

		if (skillname.toUpperCase().equals("SLASHING")) {
			if (profession != null)
				if ((profession.getName().toUpperCase().equals("RANGER")
						|| profession.getName().toUpperCase().equals("PALADIN")
						|| profession.getName().toUpperCase().equals("ROGUE")
						|| profession.getName().toUpperCase().equals("WARRIOR")
						|| profession.getName().toUpperCase().equals("BERSERKER")
						|| profession.getName().toUpperCase().equals("SHADOWKNIGHT")
						|| profession.getName().toUpperCase().equals("HUNTER")
						|| profession.getName().toUpperCase().equals("KNIGHT"))) {
					int cap = (int) ((5 * level) + 5);
					if (cap > Utils.getMaxSkillValue())
						return Utils.getMaxSkillValue();
					return cap;
				}
		}

		if (skillname.toUpperCase().equals("BACKSTAB")) {
			if (profession != null)
				if (profession.getName().toUpperCase().equals("ROGUE")) {
					int cap = (int) ((5 * level) + 5);
					if (cap > Utils.getMaxSkillValue())
						return Utils.getMaxSkillValue();
					return cap;
				}
		}

		if (skillname.toUpperCase().equals("TAUNT")) {
			if (profession != null)
				if ((profession.getName().toUpperCase().equals("WARRIOR")
						|| profession.getName().toUpperCase().equals("RANGER")
						|| profession.getName().toUpperCase().equals("SHADOWKNIGHT")
						|| profession.getName().toUpperCase().equals("BERSERKER")
						|| profession.getName().toUpperCase().equals("BEASTLORD")
						|| profession.getName().toUpperCase().equals("PALADIN"))) {
					int cap = (int) ((5 * level) + 5);
					if (cap > Utils.getMaxSkillValue())
						return Utils.getMaxSkillValue();
					return cap;
				}
		}

		if (skillname.toUpperCase().equals("BINDWOUND")) {
			if (profession != null) {
				if ((profession.getName().toUpperCase().equals("WARRIOR")
						|| profession.getName().toUpperCase().equals("MONK")
						|| profession.getName().toUpperCase().equals("ROGUE")
						|| profession.getName().toUpperCase().equals("BERSERKER"))) {
					int cap = (int) ((5 * level) + 5);
					if (cap > 210)
						return 210;
					return cap;
				}

				if ((profession.getName().toUpperCase().equals("RANGER")
						|| profession.getName().toUpperCase().equals("BEASTLORD")
						|| profession.getName().toUpperCase().equals("BARD")
						|| profession.getName().toUpperCase().equals("PALADIN")
						|| profession.getName().toUpperCase().equals("SHADOWKNIGHT")
						|| profession.getName().toUpperCase().equals("SHAMAN")
						|| profession.getName().toUpperCase().equals("CLERIC")
						|| profession.getName().toUpperCase().equals("DRUID"))) {
					int cap = (int) ((5 * level) + 5);
					if (cap > 200)
						return 200;
					return cap;
				}

				if ((profession.getName().toUpperCase().equals("ENCHANTER")
						|| profession.getName().toUpperCase().equals("MAGICIAN")
						|| profession.getName().toUpperCase().equals("NECROMANCER")
						|| profession.getName().toUpperCase().equals("WIZARD"))) {
					int cap = (int) ((5 * level) + 5);
					if (cap > 100)
						return 100;
					return cap;
				}
			}

		}

		if (skillname.toUpperCase().equals("CRUSHING")) {
			if (profession != null)
				if ((profession.getName().toUpperCase().equals("RANGER")
						|| profession.getName().toUpperCase().equals("ROGUE")
						|| profession.getName().toUpperCase().equals("PALADIN")
						|| profession.getName().toUpperCase().equals("WARRIOR")
						|| profession.getName().toUpperCase().equals("SHADOWKNIGHT")
						|| profession.getName().toUpperCase().equals("BERSERKER")
						|| profession.getName().toUpperCase().equals("BEASTLORD")
						|| profession.getName().toUpperCase().equals("MONK")
						|| profession.getName().toUpperCase().equals("HUNTER")
						|| profession.getName().toUpperCase().equals("KNIGHT"))) {
					int cap = (int) ((5 * level) + 5);
					if (cap > Utils.getMaxSkillValue())
						return Utils.getMaxSkillValue();

					return cap;
				}
		}

		if (skillname.toUpperCase().equals("DODGE")) {
			if (profession != null)
				if ((profession.getName().toUpperCase().equals("RANGER")
						|| profession.getName().toUpperCase().equals("ROGUE")
						|| profession.getName().toUpperCase().equals("PALADIN")
						|| profession.getName().toUpperCase().equals("WARRIOR")
						|| profession.getName().toUpperCase().equals("SHADOWKNIGHT")
						|| profession.getName().toUpperCase().equals("BERSERKER")
						|| profession.getName().toUpperCase().equals("BEASTLORD")
						|| profession.getName().toUpperCase().equals("MONK")
						|| profession.getName().toUpperCase().equals("HUNTER")
						|| profession.getName().toUpperCase().equals("KNIGHT"))) {
					int cap = (int) ((5 * level) + 5);
					if (cap > Utils.getMaxSkillValue())
						return Utils.getMaxSkillValue();

					return cap;
				}
		}

		if (skillname.toUpperCase().equals("RIPOSTE")) {
			if (profession != null)
				if ((profession.getName().toUpperCase().equals("RANGER")
						|| profession.getName().toUpperCase().equals("ROGUE")
						|| profession.getName().toUpperCase().equals("PALADIN")
						|| profession.getName().toUpperCase().equals("WARRIOR")
						|| profession.getName().toUpperCase().equals("SHADOWKNIGHT")
						|| profession.getName().toUpperCase().equals("MONK")
						|| profession.getName().toUpperCase().equals("BERSERKER")
						|| profession.getName().toUpperCase().equals("BEASTLORD")
						|| profession.getName().toUpperCase().equals("HUNTER")
						|| profession.getName().toUpperCase().equals("KNIGHT"))) {
					int cap = (int) ((5 * level) + 5);
					if (cap > Utils.getMaxSkillValue())
						return Utils.getMaxSkillValue();

					return cap;
				}
		}

		if (skillname.toUpperCase().equals("DOUBLEATTACK")) {
			if (profession != null)
				if ((profession.getName().toUpperCase().equals("RANGER")
						|| profession.getName().toUpperCase().equals("ROGUE")
						|| profession.getName().toUpperCase().equals("PALADIN")
						|| profession.getName().toUpperCase().equals("WARRIOR")
						|| profession.getName().toUpperCase().equals("SHADOWKNIGHT")
						|| profession.getName().toUpperCase().equals("MONK")
						|| profession.getName().toUpperCase().equals("BERSERKER")
						|| profession.getName().toUpperCase().equals("HUNTER")
						|| profession.getName().toUpperCase().equals("KNIGHT"))) {
					int cap = (int) ((5 * level) + 5);
					if (cap > Utils.getMaxSkillValue())
						return Utils.getMaxSkillValue();

					return cap;
				}
		}

		if (skillname.toUpperCase().equals("ARCHERY")) {
			if (profession != null)
				if ((profession.getName().toUpperCase().equals("RANGER")
						|| profession.getName().toUpperCase().equals("ROGUE")
						|| profession.getName().toUpperCase().equals("HUNTER"))) {
					int cap = (int) ((5 * level) + 5);
					if (cap > Utils.getMaxSkillValue())
						return Utils.getMaxSkillValue();

					return cap;
				}
		}

		if (skillname.toUpperCase().equals("MEDITATION")) {
			if (profession != null)
				if ((profession.getName().toUpperCase().equals("DRUID")
						|| profession.getName().toUpperCase().equals("WIZARD")
						|| profession.getName().toUpperCase().equals("MAGICIAN")
						|| profession.getName().toUpperCase().equals("NECROMANCER")
						|| profession.getName().toUpperCase().equals("ENCHANTER")
						|| profession.getName().toUpperCase().equals("MONK")
						|| profession.getName().toUpperCase().equals("ARCANIST")
						|| profession.getName().toUpperCase().equals("EXARCH"))) {
					int cap = (int) ((5 * level) + 5);
					if (cap > Utils.getMaxSkillValue())
						return Utils.getMaxSkillValue();

					return cap;
				}
		}

		if (skillname.toUpperCase().equals("OFFENSE")) {
			if (profession != null)
				if ((profession.getName().toUpperCase().equals("RANGER")
						|| profession.getName().toUpperCase().equals("ROGUE")
						|| profession.getName().toUpperCase().equals("PALADIN")
						|| profession.getName().toUpperCase().equals("WARRIOR")
						|| profession.getName().toUpperCase().equals("SHADOWKNIGHT")
						|| profession.getName().toUpperCase().equals("MONK")
						|| profession.getName().toUpperCase().equals("HUNTER")
						|| profession.getName().toUpperCase().equals("BERSERKER")
						|| profession.getName().toUpperCase().equals("BEASTLORD")
						|| profession.getName().toUpperCase().equals("KNIGHT"))) {
					int cap = (int) ((5 * level) + 5);
					if (cap > Utils.getMaxSkillValue())
						return Utils.getMaxSkillValue();

					return cap;
				}
		}

		if (skillname.toUpperCase().equals("DEFENSE")) {
			if (profession != null)
				if ((profession.getName().toUpperCase().equals("RANGER")
						|| profession.getName().toUpperCase().equals("ROGUE")
						|| profession.getName().toUpperCase().equals("PALADIN")
						|| profession.getName().toUpperCase().equals("WARRIOR")
						|| profession.getName().toUpperCase().equals("SHADOWKNIGHT")
						|| profession.getName().toUpperCase().equals("MONK")
						|| profession.getName().toUpperCase().equals("HUNTER")
						|| profession.getName().toUpperCase().equals("BEASTLORD")
						|| profession.getName().toUpperCase().equals("BERSERKER")
						|| profession.getName().toUpperCase().equals("KNIGHT"))) {
					int cap = (int) ((5 * level) + 5);
					if (cap > Utils.getMaxSkillValue())
						return Utils.getMaxSkillValue();

					return cap;
				}
		}

		if (skillname.toUpperCase().equals("SPECIALISEABJURATION")) {
			if (profession != null) {
				if (profession.getSpecialiselevel() < 1)
					return 0;

				if (specialisation == null || specialisation.equals(""))
					return 0;

				if (level >= profession.getSpecialiselevel()) {
					int cap = (int) ((2 * level) + 2);
					if (cap > Utils.getMaxSkillValue())
						return Utils.getMaxSkillValue();

					return cap;
				} else {
					return 0;
				}
			}
		}

		if (skillname.toUpperCase().equals("SPECIALISEALTERATION")) {
			if (profession != null) {
				if (profession.getSpecialiselevel() < 1)
					return 0;

				if (specialisation == null || specialisation.equals(""))
					return 0;

				if (level >= profession.getSpecialiselevel()) {
					int cap = (int) ((2 * level) + 2);
					if (cap > Utils.getMaxSkillValue())
						return Utils.getMaxSkillValue();

					return cap;
				} else {
					return 0;
				}
			}
		}

		if (skillname.toUpperCase().equals("SPECIALISECONJURATION")) {
			if (profession != null) {
				if (profession.getSpecialiselevel() < 1)
					return 0;

				if (specialisation == null || specialisation.equals(""))
					return 0;

				if (level >= profession.getSpecialiselevel()) {
					int cap = (int) ((2 * level) + 2);
					if (cap > Utils.getMaxSkillValue())
						return Utils.getMaxSkillValue();

					return cap;
				} else {
					return 0;
				}
			}
		}

		if (skillname.toUpperCase().toUpperCase().equals("SPECIALISEDIVINATION")) {
			if (profession != null) {
				if (profession.getSpecialiselevel() < 1)
					return 0;

				if (specialisation == null || specialisation.equals(""))
					return 0;

				if (level >= profession.getSpecialiselevel()) {
					int cap = (int) ((2 * level) + 2);
					if (cap > Utils.getMaxSkillValue())
						return Utils.getMaxSkillValue();

					return cap;
				} else {
					return 0;
				}
			}
		}

		if (skillname.toUpperCase().equals("ALCHEMY")) {
			if (profession != null)
				if ((profession.getName().toUpperCase().equals("SHAMAN"))) {
					int cap = (int) ((5 * level) + 5);
					if (cap > Utils.getMaxSkillValue())
						return Utils.getMaxSkillValue();

					return cap;
				} else {
					return 0;
				}
		}

		if (skillname.equals("JEWELRYMAKING")) {
			if (profession != null)
				if ((profession.getName().toUpperCase().equals("ENCHANTER"))) {
					int cap = (int) ((5 * level) + 5);
					if (cap > Utils.getMaxSkillValue())
						return Utils.getMaxSkillValue();

					return cap;
				} else {
					return 0;
				}
		}

		if (skillname.toUpperCase().equals("TAILORING")) {
			int cap = (int) ((5 * level) + 5);
			if (cap > Utils.getMaxSkillValue())
				return Utils.getMaxSkillValue();
		}

		if (skillname.toUpperCase().equals("FLETCHING")) {
			if (profession != null)
				if ((profession.getName().toUpperCase().equals("RANGER"))) {
					int cap = (int) ((5 * level) + 5);
					if (cap > Utils.getMaxSkillValue())
						return Utils.getMaxSkillValue();

					return cap;
				} else {
					return 0;
				}
		}

		if (skillname.equals("BLACKSMITHING")) {
			int cap = (int) ((5 * level) + 5);
			if (cap > Utils.getMaxSkillValue())
				return Utils.getMaxSkillValue();
		}

		if (skillname.toUpperCase().equals("TINKERING")) {
			int cap = (int) ((5 * level) + 5);
			if (cap > Utils.getMaxSkillValue())
				return Utils.getMaxSkillValue();
		}

		if (skillname.toUpperCase().equals("MAKEPOISON")) {
			if (profession != null)
				if ((profession.getName().toUpperCase().equals("ROGUE"))) {
					int cap = (int) ((5 * level) + 5);
					if (cap > Utils.getMaxSkillValue())
						return Utils.getMaxSkillValue();

					return cap;
				} else {
					return 0;
				}
		}

		if (skillname.toUpperCase().equals("SPECIALISEEVOCATION")) {
			if (profession != null) {
				if (profession.getSpecialiselevel() < 1)
					return 0;

				if (specialisation == null || specialisation.equals(""))
					return 0;

				if (level >= profession.getSpecialiselevel()) {
					int cap = (int) ((2 * level) + 2);
					if (cap > Utils.getMaxSkillValue())
						return Utils.getMaxSkillValue();

					return cap;
				} else {
					return 0;
				}
			}
		}

		int cap = (int) ((2 * level) + 2);
		if (cap > Utils.getMaxSkillValue())
			return Utils.getMaxSkillValue();

		return cap;
	}
	
	public static double makeSafeFollowY(World w, double dx, double dy, double dz, boolean flying) {
		int x = (int) Math.floor(dx);
		int y = (int) Math.floor(dy);
		int z = (int) Math.floor(dz);

		Double newy = Double.valueOf(0.0D);

		while ((!safeFollow(w, x, y, z)) && (y <= w.getHighestBlockYAt(x, z))) {
			y++;
		}

		do {
			y--;
		} while ((safeFollow(w, x, y, z)) && (y > 1) && (!flying));

		if (y < w.getMaxHeight()) {
			if (DONT_STAND_ON.contains(w.getBlockAt(x, y, z).getType())) {
				newy = Double.valueOf(0.0D);
			} else if (HALF_HEIGHT.contains(w.getBlockAt(x, y, z).getType())) {
				newy = Double.valueOf(y + 0.5626D);
			} else if (HEIGHT_AND_HALF.contains(w.getBlockAt(x, y, z).getType())) {
				newy = Double.valueOf(y + 1.5001D);
			} else {
				newy = Double.valueOf(y + 1.0D);
			}
		}

		return newy.doubleValue();
	}
}
