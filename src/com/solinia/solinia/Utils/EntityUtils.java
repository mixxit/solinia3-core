package com.solinia.solinia.Utils;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.craftbukkit.v1_14_R1.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_14_R1.entity.CraftLivingEntity;
import org.bukkit.craftbukkit.v1_14_R1.entity.CraftPlayer;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.User;
import com.solinia.solinia.Solinia3CorePlugin;
import com.solinia.solinia.Adapters.SoliniaLivingEntityAdapter;
import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Interfaces.ISoliniaClass;
import com.solinia.solinia.Interfaces.ISoliniaLivingEntity;
import com.solinia.solinia.Interfaces.ISoliniaRace;
import com.solinia.solinia.Managers.StateManager;
import com.solinia.solinia.Models.SkillType;
import com.solinia.solinia.Models.SolAnimationType;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import net.minecraft.server.v1_14_R1.DamageSource;
import net.minecraft.server.v1_14_R1.EntityLiving;
import net.minecraft.server.v1_14_R1.EntityPlayer;
import net.minecraft.server.v1_14_R1.PacketPlayOutAnimation;
import net.minecraft.server.v1_14_R1.PacketPlayOutEntityMetadata;

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

	public static boolean isFeared(LivingEntity livingEntity)
	{
		try {
			ISoliniaLivingEntity solLiv = SoliniaLivingEntityAdapter.Adapt(livingEntity);
			return solLiv.isFeared();

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
	    
	    EntityUtils.teleportSafely(source,stalkerLocation);
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

	public static int getSkillCap(SkillType skillType, ISoliniaClass profession, int level, String specialisation, int currentskillamount) {

		if (!Utils.isValidSkill(skillType.name().toUpperCase()))
			return 0;

		// If the skill being queried happens to be a race name, the cap for
		// language is always 100
		try {
			List<ISoliniaRace> races = StateManager.getInstance().getConfigurationManager().getRaces();
			for (ISoliniaRace race : races) {
				if (race.getName().toUpperCase().equals(skillType.name().toUpperCase())) {
					return 100;
				}
			}

		} catch (CoreStateInitException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// TODO - Move all these skill cap bonuses to the race configuration
		// classes
		
		if (skillType.name().toUpperCase().startsWith("SPECIALISE")) {
			if (profession != null) {
				if (profession.getSpecialiselevel() < 1)
					return 0;

				if (specialisation == null || specialisation.equals(""))
					return 0;

				if (level >= profession.getSpecialiselevel()) {
				return EntityUtils.maxSkill(skillType, profession.getName().toUpperCase(),level, currentskillamount);
				} else {
					return 0;
				}
			}
		}

		if (profession != null)
			return EntityUtils.maxSkill(skillType, profession.getName().toUpperCase(),level, currentskillamount);

		int cap = (int) ((2 * level) + 2);
		if (cap > Utils.HIGHESTSKILL)
			return Utils.HIGHESTSKILL;

		return cap;
	}

	private static int maxSkillClass(SkillType skillType, String classname, int level) {
	    int r_value = 0;
	    switch (skillType) {
	        // Rogue
	        case MakePoison:
	        case PickPockets:
	        case Backstab:
	            {
	                switch (classname) {
	                    // Melee
	                    case "ROGUE":
	                    case "ROGUEGM":
	                        {
	                            r_value = ((level * 5) + 5);
	                            switch (skillType) {
	                                case MakePoison:
	                                    {
	                                        // 18 200 200
	                                        if (level < 18) {
	                                            r_value = 0;
	                                        }
	                                        if (r_value > 200) {
	                                            r_value = 200;
	                                        }
	                                        break;
	                                    }
	                                /*case "MAKEPOISON":
	                                    {
	                                        // 20 200 250
	                                        if (level < 20) {
	                                            r_value = 0;
	                                        }
	                                        if (level < 51 && r_value > 200) {
	                                            r_value = 200;
	                                        }
	                                        if (r_value > 250) {
	                                            r_value = 250;
	                                        }
	                                        break;
	                                    }*/
	                                case PickPockets:
	                                    {
	                                        // 7 200 210
	                                        if (level < 7) {
	                                            r_value = 0;
	                                        }
	                                        if (level < 51 && r_value > 200) {
	                                            r_value = 200;
	                                        }
	                                        if (r_value > 210) {
	                                            r_value = 210;
	                                        }
	                                        break;
	                                    }
	                                case Backstab:
	                                    {
	                                        // 10 200 225
	                                        if (level < 10) {
	                                            r_value = 0;
	                                        }
	                                        if (level < 51 && r_value > 200) {
	                                            r_value = 200;
	                                        }
	                                        if (r_value > 225) {
	                                            r_value = 225;
	                                        }
	                                        break;
	                                    }
	                                default:
	                                    r_value = 0;
	                                    break;
	                            }
	                            break;
	                        }
	                    default:
	                        r_value = 0;
	                        break;
	                } // Class Switch
	                break;
	            }
	            // Monk
	        case Block:
	            {
	                switch (classname) {
	                    case "BEASTLORD":
	                    case "BEASTLORDGM":
	                        {
	                            r_value = (((level - 25) * 5) + 5);
	                            // 12 200 230
	                            if (level < 25) {
	                                r_value = 0;
	                            }
	                            if (level < 51 && r_value > 200) {
	                                r_value = 200;
	                            }
	                            if (r_value > 230) {
	                                r_value = 230;
	                            }
	                            break;
	                        }
	                    case "MONK":
	                    case "MONKGM":
	                        {
	                            r_value = ((level * 5) + 5);
	                            // 12 200 230
	                            if (level < 12) {
	                                r_value = 0;
	                            }
	                            if (level < 51 && r_value > 200) {
	                                r_value = 200;
	                            }
	                            if (r_value > 230) {
	                                r_value = 230;
	                            }
	                            break;
	                        }
	                }
	                break;
	            }
	        case FeignDeath:
	        case Mend:
	        case DragonPunch:
	        case EagleStrike:
	        case FlyingKick:
	        case RoundKick:
	        case TigerClaw:
	            {
	                switch (classname) {
	                    case "MONK":
	                    case "MONKGM":
	                        {
	                            r_value = ((level * 5) + 5);
	                            switch (skillType) {
	                                case Mend:
	                                    {
	                                        // 1 200 200
	                                        if (r_value > 200) {
	                                            r_value = 200;
	                                        }
	                                        break;
	                                    }
	                                case RoundKick:
	                                    {
	                                        // 5 200 225
	                                        if (level < 5) {
	                                            r_value = 0;
	                                        }
	                                        if (level < 51 && r_value > 200) {
	                                            r_value = 200;
	                                        }
	                                        if (r_value > 225) {
	                                            r_value = 225;
	                                        }
	                                        break;
	                                    }
	                                case TigerClaw:
	                                    {
	                                        // 10 200 225
	                                        if (level < 10) {
	                                            r_value = 0;
	                                        }
	                                        if (level < 51 && r_value > 200) {
	                                            r_value = 200;
	                                        }
	                                        if (r_value > 225) {
	                                            r_value = 225;
	                                        }
	                                        break;
	                                    }
	                                case FeignDeath:
	                                    {
	                                        // 17 200 200
	                                        if (level < 17) {
	                                            r_value = 0;
	                                        }
	                                        if (r_value > 200) {
	                                            r_value = 200;
	                                        }
	                                        break;
	                                    }
	                                case EagleStrike:
	                                    {
	                                        // 20 200 225
	                                        if (level < 20) {
	                                            r_value = 0;
	                                        }
	                                        if (level < 51 && r_value > 200) {
	                                            r_value = 200;
	                                        }
	                                        if (r_value > 225) {
	                                            r_value = 225;
	                                        }
	                                        break;
	                                    }
	                                case DragonPunch:
	                                    {
	                                        // 25 200 225
	                                        if (level < 25) {
	                                            r_value = 0;
	                                        }
	                                        if (level < 51 && r_value > 200) {
	                                            r_value = 200;
	                                        }
	                                        if (r_value > 225) {
	                                            r_value = 225;
	                                        }
	                                        break;
	                                    }
	                                case FlyingKick:
	                                    {
	                                        // 30 200 225
	                                        if (level < 30) {
	                                            r_value = 0;
	                                        }
	                                        if (level < 51 && r_value > 200) {
	                                            r_value = 200;
	                                        }
	                                        if (r_value > 225) {
	                                            r_value = 225;
	                                        }
	                                        break;
	                                    }
	                                default:
	                                    r_value = 0;
	                                    break;
	                            }
	                            break;
	                        }
	                    default:
	                        r_value = 0;
	                        break;
	                } // Class Switch
	                break;
	            }
	            //Berzerkers
	        case Berserking:
	            {
	                switch (classname) {
	                    case "BERSERKER":
	                    case "BERSERKERGM":
	                        {
	                            r_value = ((level * 5) + 5);
	                            if (r_value > 200) {
	                                r_value = 200;
	                            }
	                        }
	                    default:
	                        r_value = 0;
	                        break;
	                }
	                break;
	            }
	            // Shaman
	        case Alchemy:
	            {
	                switch (classname) {
	                    case "SHAMAN":
	                    case "SHAMANGM":
	                        {
	                            // 25 130 180
	                            r_value = ((level * 5) + 5);
	                            if (level < 25) {
	                                r_value = 0;
	                            }
	                            if (level < 51 && r_value > 130) {
	                                r_value = 130;
	                            }
	                            if (r_value > 180) {
	                                r_value = 180;
	                            }
	                            break;
	                        }
	                    default:
	                        r_value = 0;
	                        break;
	                } // Class Switch
	                break;
	            }
	            ///////////////////////////////////////////
	            //////////////////////////////////////////
	            // Shared skill
	            // Shared Rogue
	        case Hide:
	            {
	                switch (classname) {
	                    // True class
	                    case "ROGUE":
	                    case "ROGUEGM":
	                        {
	                            r_value = ((level * 5) + 5);
	                            if (r_value > 200) {
	                                r_value = 200;
	                            }
	                            break;
	                        }
	                        // Hybrids
	                    case "RANGER":
	                    case "RANGERGM":
	                    case "SHADOWKNIGHT":
	                    case "SHADOWKNIGHTGM":
	                        { //75 cap
	                            if (level >= 35) {
	                                r_value = (((level - 35) * 5) + 5);
	                                if (r_value > 75) {
	                                    r_value = 75;
	                                }
	                            }
	                            break;
	                        }
	                    case "BARD":
	                    case "BARDGM":
	                        { //40 cap
	                            if (level > 25) {
	                                r_value = (((level - 25) * 5) + 5);
	                                if (r_value > 40) {
	                                    r_value = 40;
	                                }
	                            }
	                            break;
	                        }
	                    default:
	                        r_value = 0;
	                        break;
	                } // Class Switch
	                break;
	            }
	        case Sneak:
	            {
	                switch (classname) {
	                    // True class
	                    case "ROGUE":
	                    case "ROGUEGM":
	                        {
	                            r_value = ((level * 5) + 5);
	                            if (r_value > 200) {
	                                r_value = 200;
	                            }
	                            break;
	                        }
	                        // Hybrids
	                    case "MONK":
	                    case "MONKGM":
	                        { //113 cap
	                            if (level >= 8) {
	                                r_value = (((level - 8) * 5) + 5);
	                                if (r_value > 113) {
	                                    r_value = 113;
	                                }
	                            }
	                            break;
	                        }
	                    case "RANGER":
	                    case "RANGERGM":
	                        { //75 cap
	                            if (level >= 10) {
	                                r_value = (((level - 10) * 5) + 5);
	                                if (r_value > 75) {
	                                    r_value = 75;
	                                }
	                            }
	                            break;
	                        }
	                    case "BARD":
	                    case "BARDGM":
	                        { //75 cap
	                            if (level >= 17) {
	                                r_value = (((level - 17) * 5) + 5);
	                                if (r_value > 75) {
	                                    r_value = 75;
	                                }
	                            }
	                            break;
	                        }
	                    case "BEASTLORD":
	                    case "BEASTLORDGM":
	                        { //50 cap
	                            if (level >= 50) {
	                                r_value = (((level - 50) * 5) + 5);
	                                if (r_value > 50) {
	                                    r_value = 50;
	                                }
	                            }
	                            break;
	                        }
	                    default:
	                        r_value = 0;
	                        break;
	                } // Class Switch
	                break;
	            }
	        case SenseTraps:
	        case PickLock:
	        case DisarmTraps:
	            {
	                switch (classname) {
	                    // True class
	                    case "ROGUE":
	                    case "ROGUEGM":
	                        {
	                            r_value = ((level * 5) + 5);
	                            if (r_value > 200) {
	                                r_value = 200;
	                            }
	                            break;
	                        }
	                        // Hybrids
	                    case "BARD":
	                    case "BARDGM":
	                        { //100 cap
	                            if (level >= 30) { //this is wrong I think...
	                                r_value = (((level - 30) * 5) + 5);
	                                if (r_value > 100) {
	                                    r_value = 100;
	                                }
	                            }
	                            break;
	                        }
	                    default:
	                        r_value = 0;
	                        break;
	                } // Class Switch
	                break;
	            }
	        case SafeFall:
	            {
	                switch (classname) {
	                    // Hybrids
	                    case "BARD":
	                    case "BARDGM":
	                        { //40 cap
	                            if (level >= 24) {
	                                r_value = (((level - 24) * 5) + 5);
	                                if (r_value > 40) {
	                                    r_value = 40;
	                                }
	                            }
	                            break;
	                        }
	                        // Melee
	                    case "MONK":
	                    case "MONKGM":
	                        {
	                            if (level >= 3) {
	                                r_value = (((level - 3) * 5) + 5);
	                                if (r_value > 200) {
	                                    r_value = 200;
	                                }
	                            }
	                            break;
	                        }
	                    case "ROGUE":
	                    case "ROGUEGM":
	                        { //100 cap
	                            if (level >= 12) {
	                                r_value = (((level - 12) * 5) + 5);
	                                if (r_value > 100) {
	                                    r_value = 100;
	                                }
	                            }
	                            break;
	                        }
	                    default:
	                        r_value = 0;
	                        break;
	                } // Class Switch
	                break;
	            }
	        case Intimidation:
	            {
	                switch (classname) {
	                    case "BARD":
	                    case "BARDGM":
	                        { //100 cap
	                            if (level >= 26) {
	                                r_value = (((level - 26) * 5) + 5);
	                                if (r_value > 100) {
	                                    r_value = 100;
	                                }
	                            }
	                            break;
	                        }
	                        // Melee
	                    case "MONK":
	                    case "MONKGM":
	                        {
	                            if (level >= 18) {
	                                r_value = (((level - 18) * 5) + 5);
	                                if (r_value > 200) {
	                                    r_value = 200;
	                                }
	                            }
	                            break;
	                        }
	                    case "BERSERKER":
	                    case "BERSERKERGM":
	                        {
	                            if (level >= 20) {
	                                r_value = (((level - 20) * 5) + 5);
	                                if (r_value > 200) {
	                                    r_value = 200;
	                                }
	                            }
	                            break;
	                        }
	                    case "ROGUE":
	                    case "ROGUEGM":
	                        {
	                            if (level >= 22) {
	                                r_value = (((level - 22) * 5) + 5);
	                                if (r_value > 200) {
	                                    r_value = 200;
	                                }
	                            }
	                            break;
	                        }
	                    default:
	                        r_value = 0;
	                        break;
	                } // Class Switch
	                break;
	            }
	            // Druid/Ranger/Bard
	        case Forage:
	            {
	                switch (classname) {
	                    case "RANGER":
	                    case "RANGERGM":
	                        {
	                            if (level > 3) {
	                                r_value = (((level - 3) * 5) + 5);
	                                if (r_value > 200) {
	                                    r_value = 200;
	                                }
	                            }
	                            break;
	                        }
	                    case "DRUID":
	                    case "DRUIDGM":
	                        {
	                            r_value = ((level * 5) + 5);
	                            if (r_value > 200) {
	                                r_value = 200;
	                            }
	                            break;
	                        }
	                    case "MONK":
	                    case "MONKGM":
	                    case "BARD":
	                    case "BARDGM":
	                        r_value = 55;
	                        break;
	                    default:
	                        r_value = 50;
	                        break;
	                } // Class Switch
	                break;
	            }
	        case Tracking:
	            {
	                switch (classname) {
	                    case "RANGER":
	                    case "RANGERGM":
	                    case "BARD":
	                    case "BARDGM":
	                    case "DRUID":
	                    case "DRUIDGM":
	                        r_value = 200;
	                        break;
	                    default:
	                        r_value = 0;
	                        break;
	                } // Class Switch
	                break;
	            }
	            ////////////////////////////////////////////////////////
	        default:
	            System.out.println("Unknown skill");
	            break;
	    } // Switch skill
	    if (r_value > 252) {
	        r_value = 252;
	    }
	    return r_value;
	}		
	public static int maxSkill(SkillType skillType, String classname, int level, int currentskillamount)
	{
		int r_value = 0;
		switch (skillType) {
			case TwoHandBlunt:
			case Piercing:
			case HandtoHand:
			case Crushing:
			case TwoHandSlashing:
			case Slashing: {
					r_value = EntityUtils.maxSkillWeapon(skillType, classname, level);
					break;
				}
			case Offense:
			case Throwing:
			case Archery:
			case DoubleAttack:
			case DualWield:
			case Kick:
			case Bash: {
					r_value = EntityUtils.maxSkillOffensive(skillType, classname, level);
					break;
				}
			case Defense:
			case Parry:
			case Riposte:
			case Dodge:
			case Taunt:
			case Disarm: {
					r_value = EntityUtils.maxSkillDefensive(skillType, classname, level);
					break;
				}
			case Meditation:
			case Abjuration:
			case Alteration:
			case Channeling:
			case Conjuration:
			case Divination:
			case Evocation:
			case SpecialiseAbjuration:
			case SpecialiseAlteration:
			case SpecialiseConjuration:
			case SpecialiseDivination:
			case SpecialiseEvocation:
			case Research:
			case BrassInstruments:
			case Singing:
			case StringedInstruments:
			case WindInstruments:
			case PercussionInstruments: {
					r_value = maxSkillArcane(skillType, classname, level,currentskillamount);
					break;
				}
	///////////////////////////////////////////
	///////////////////////////////////////////
	// Class skills
			// Rogue
			//case "MAKEPOISON":
			case MakePoison:
			case PickPockets:
			case Backstab:
			// Monk
			case FeignDeath:
			case Mend:
			case DragonPunch:
			case EagleStrike:
			case FlyingKick:
			case RoundKick:
			case TigerClaw:
			case Block:
			case Alchemy:
			case Hide:
			case Sneak:
			case SenseTraps:
			case PickLock:
			case DisarmTraps:
			case SafeFall:
			case Intimidation:
			// Druid/Ranger/Bard
			case Forage:
			case Tracking: {
					r_value = maxSkillClass(skillType, classname, level);
					break;
				}
	///////////////////////////////////////////
	///////////////////////////////////////////
	// Tradeskills
			case Logging:
			case Mining:
			case Baking:
			case Tailoring:
			case Blacksmithing:
			case Fletching:
			case Brewing:
			case JewelryMaking:
			case Pottery:
			case Fishing: {
					// Check for Any Trade above 200, check for X (aa skill) Trades above 200
					r_value = 250;
					break;
				}
	/////////////////////////////////////
	/////////////////////////////////////
			// Gnome
			case Tinkering: {
					r_value = ((level * 5) + 5);
					break;
				}
	/////////////////////////////////////////
	// Common
	/////////////////////////////////////////
			case BindWound:
			{
					switch (classname) {
						case "BARD":
						case "BARDGM": {
								r_value = ((level * 5) + 5);
								if (level >= 50) {
									if (r_value > 210) {
										r_value = 210;
									}
								} else {
									if (r_value > 200) {
										r_value = 200;
									}
								}
								break;
							}
						case "CLERIC":
						case "CLERICGM": {
								r_value = ((level * 5) + 5);
								if (level >= 50) {
									if (r_value > 201) {
										r_value = 201;
									}
								} else {
									if (r_value > 200) {
										r_value = 200;
									}
								}
								break;
							}
						case "DRUID":
						case "DRUIDGM":
						case "SHAMAN":
						case "SHAMANGM": {
								r_value = ((level * 5) + 5);
								if (r_value > 200) {
									r_value = 200;
								}
								break;
							}
						case "MAGICIAN":
						case "MAGICIANGM":
						case "ENCHANTER":
						case "ENCHANTERGM":
						case "NECROMANCER":
						case "NECROMANCERGM":
						case "WIZARD":
						case "WIZARDGM": {
								r_value = ((level * 5) + 5);
								if (r_value > 100) {
									r_value = 100;
								}
								break;
							}
						case "BEASTLORD":
						case "BEASTLORDGM":
						case "BERSERKER":
						case "BERSERKERGM":
						case "MONK":
						case "MONKGM": {
								r_value = ((level * 5) + 5);
								if (level >= 50) {
									if (r_value > 210) {
										r_value = 210;
									}
								} else {
									if (r_value > 200) {
										r_value = 200;
									}
								}
								break;
							}
						case "PALADIN":
						case "PALADINGM": {
								if (level > 10) {
									r_value = (((level - 10) * 5) + 5);
									if (level >= 50) {
										if (r_value > 210) {
											r_value = 210;
										}
									} else {
										if (r_value > 200) {
											r_value = 200;
										}
									}
								}
								break;
							}
						case "RANGER":
						case "RANGERGM": {
								if (level > 15) {
									r_value = (((level - 15) * 5) + 5);
									if (level >= 50) {
										if (r_value > 200) {
											r_value = 200;
										}
									} else {
										if (r_value > 150) {
											r_value = 150;
										}
									}
								}
								break;
							}
						case "ROGUE":
						case "ROGUEGM": {
								r_value = ((level * 5) + 5);
								if (level >= 50) {
									if (r_value > 210) {
										r_value = 210;
									}
								} else {
									if (r_value > 176) {
										r_value = 176;
									}
								}
								break;
							}
						case "SHADOWKNIGHT":
						case "SHADOWKNIGHTGM": {
								r_value = ((level * 5) + 5);
								if (level >= 50) {
									if (r_value > 200) {
										r_value = 200;
									}
								} else {
									if (r_value > 150) {
										r_value = 150;
									}
								}
								break;
							}
						case "WARRIOR":
						case "WARRIORGM": {
								if (level > 5) {
									r_value = (((level - 5) * 5) + 5);
									if (level >= 50) {
										if (r_value > 210) {
											r_value = 210;
										}
									} else {
										if (r_value > 175) {
											r_value = 175;
										}
									}
								}
								break;
							}
						default:
							r_value = 0;
							break;
					}
					break;
				}
			case SenseHeading:
			case Swimming:
			case AlcoholTolerance:
			case Begging: {
					r_value = 5 + (level * 5);
					if (r_value > 200) {
						r_value = 200;
					}
					break;
				}
			//case "BERSERKING":
			default: {
					// Unknown skill we should like print something to a log/debug here
					r_value = 0;
					break;
				}
		}
		if (r_value >= 253) {
			r_value = 252;
		}
		return  r_value;
	}
	
	public static int maxSkillArcane(SkillType skillType, String classname, int level, int currentskillamount)
	{
		int r_value = 0;
		switch (skillType) {
			case Meditation:
			case Abjuration:
			case Alteration:
			case Channeling:
			case Conjuration:
			case Divination:
			case Evocation: {
					r_value = ((level * 5) + 5);
					switch (classname) {
						// Hybrid
						case "RANGER":
						case "RANGERGM": {
								// 9 235 235
								// Channel 9 200 215
								// Med 12 185 235
								if (level < 9) {
									r_value = 0;
								}
								if (level < 12 && skillType.equals(SkillType.Meditation)) {
									r_value = 0;
								}
								if (r_value > 0 && skillType.equals(SkillType.Channeling)) {
									if ( level < 51 && r_value > 200) {
										r_value = 200;
									}
									if (r_value > 215) {
										r_value = 215;
									}
								}
								if (r_value > 0 && skillType.equals(SkillType.Meditation)) {
									if ( level < 51 && r_value > 185) {
										r_value = 185;
									}
									if (r_value > 235) {
										r_value = 235;
									}
								}
								break;
							}
						// temporary until endurance is added
						case "BEASTLORD":
						case "BEASTLORDGM":
						case "PALADIN":
						case "PALADINGM":
						case "SHADOWKNIGHT":
						case "SHADOWKNIGHTGM": {
								// 9 235 235
								// Channel 9 200 220
								// Med 12 185 235
								if (level < 9) {
									r_value = 0;
								}
								if (level < 12 && skillType.equals(SkillType.Meditation)) {
									r_value = 0;
								}
								if (r_value > 0 && skillType.equals(SkillType.Channeling)) {
									if ( level < 51 && r_value > 185) {
										r_value = 185;
									}
									if (r_value > 220) {
										r_value = 220;
									}
								}
								if (r_value > 0 && skillType.equals(SkillType.Meditation)) {
									if ( level < 51 && r_value > 185) {
										r_value = 185;
									}
									if (r_value > 235) {
										r_value = 235;
									}
								}
								break;
							}
						// Priest
						case "CLERIC":
						case "CLERICGM":
						case "DRUID":
						case "DRUIDGM":
						case "SHAMAN":
						case "SHAMANGM": {
								// 1 235 235
								// Channel 4 200 220
								// Med 8 235 252
								if (level < 4 && skillType.equals(SkillType.Channeling)) {
									r_value = 0;
								}
								if (level < 8 && skillType.equals(SkillType.Meditation)) {
									r_value = 0;
								}
								if (r_value > 0 && skillType.equals(SkillType.Channeling)) {
									if ( level < 51 && r_value > 200) {
										r_value = 200;
									}
									if (r_value > 220) {
										r_value = 220;
									}
								}
								if (r_value > 0 && skillType.equals(SkillType.Meditation)) {
									if ( level < 51 && r_value > 235) {
										r_value = 235;
									}
									if (r_value > 252) {
										r_value = 252;
									}
								}
								break;
							}
						// Int caster
						case "ENCHANTER":
						case "ENCHANTERGM":
						case "MAGICIAN":
						case "MAGICIANGM":
						case "NECROMANCER":
						case "NECROMANCERGM":
						case "WIZARD":
						case "WIZARDGM": {
								// 1 235 235
								// Channel 1 200 220
								// Med 4 235 252
								if (level < 4 && skillType.equals(SkillType.Meditation)) {
									r_value = 0;
								}
								if (r_value > 0 && skillType.equals(SkillType.Channeling)) {
									if ( level < 51 && r_value > 200) {
										r_value = 200;
									}
									if (r_value > 220) {
										r_value = 220;
									}
								}
								if (r_value > 0 && skillType.equals(SkillType.Meditation)) {
									if ( level < 51 && r_value > 235) {
										r_value = 235;
									}
									if (r_value > 252) {
										r_value = 252;
									}
								}
								break;
							}
						/*case "BARD":
						case "BARDGM": {
								r_value = 0;
								if (level > 9 && skillType.equals(SkillType.Meditation)) {
									r_value = 1;
								}
								break;
							}*/
						default:
							// Unknown class
							// TODO temporary until endurance is added
							// 9 235 235
							// Channel 9 200 220
							// Med 12 185 235
							if (level < 9) {
								r_value = 0;
							}
							if (level < 12 && skillType.equals(SkillType.Meditation)) {
								r_value = 0;
							}
							if (r_value > 0 && skillType.equals(SkillType.Channeling)) {
								if ( level < 51 && r_value > 185) {
									r_value = 185;
								}
								if (r_value > 220) {
									r_value = 220;
								}
							}
							if (r_value > 0 && skillType.equals(SkillType.Meditation)) {
								if ( level < 51 && r_value > 185) {
									r_value = 185;
								}
								if (r_value > 235) {
									r_value = 235;
								}
							}
							break;
							//r_value = 0;
							//break;
					}// Class Switch
					break;
				}
			case SpecialiseAbjuration:
			case SpecialiseAlteration:
			case SpecialiseConjuration:
			case SpecialiseDivination:
			case SpecialiseEvocation:
			{
					r_value = ((level * 5) + 5);
					switch (classname) {
						// Non-int casters
						case "CLERIC":
						case "CLERICGM":
						case "DRUID":
						case "DRUIDGM":
						case "SHAMAN":
						case "SHAMANGM":
							if (level < 30) {
								r_value = 0;
								break;
							}
						// Int caster
						case "ENCHANTER":
						case "ENCHANTERGM":
						case "MAGICIAN":
						case "MAGICIANGM":
						case "NECROMANCER":
						case "NECROMANCERGM":
						case "WIZARD":
						case "WIZARDGM": {
								if (level < 20) {
									r_value = 0;
									break;
								}
								//make sure only 1 skill can be over 50
								SkillType hskillType = SkillType.None;
								int high = 0;
								int cur;
								cur = currentskillamount;
								if (cur > high) {
									hskillType = SkillType.SpecialiseAbjuration;
									high = cur;
								}
								cur = currentskillamount;
								if (cur > high) {
									hskillType = SkillType.SpecialiseAlteration;
									high = cur;
								}
								cur = currentskillamount;
								if (cur > high) {
									hskillType = SkillType.SpecialiseConjuration;
									high = cur;
								}
								cur = currentskillamount;
								if (cur > high) {
									hskillType = SkillType.SpecialiseDivination;
									high = cur;
								}
								cur = currentskillamount;
								if (cur > high) {
									hskillType = SkillType.SpecialiseEvocation;
									high = cur;
								}
								if (high > 50 && hskillType.equals(skillType)) {
									r_value = 50;
									break;
								}
								if (r_value > 200) {
									r_value = 200;
								}
								break;
							}
						default: {
								r_value = 0;
								break;
							}
					}// Class Switch
					break;
				}
			case Research: {
					r_value = ((level * 5) + 5);
					switch (classname) {
						// Int caster
						case "ENCHANTER":
						case "ENCHANTERGM":
						case "MAGICIAN":
						case "MAGICIANGM":
						case "NECROMANCER":
						case "NECROMANCERGM":
						case "WIZARD":
						case "WIZARDGM": {
								// Res 16 200 200
								if (level < 16) {
									r_value = 0;
								}
								if (r_value > 200) {
									r_value = 200;
								}
								// FIXME Only let one SPEC go above what ever limit theres supposed to be
								break;
							}
						default: {
								r_value = 0;
								break;
							}
					}// Class Switch
					break;
				}
			case BrassInstruments:
			case Singing:
			case StringedInstruments:
			case WindInstruments:
			case PercussionInstruments: {
					switch (classname) {
						case "BARD":
						case "BARDGM": {
								r_value = ((level * 5) + 5);
								if (level < 5 && skillType.equals(SkillType.PercussionInstruments)) {
									r_value = 0;
								}
								if (level < 8 && skillType.equals(SkillType.StringedInstruments)) {
									r_value = 0;
								}
								if (level < 11 && skillType.equals(SkillType.BrassInstruments)) {
									r_value = 0;
								}
								if (level < 14 && skillType.equals(SkillType.WindInstruments)) {
									r_value = 0;
								}
								if (r_value > 235) {
									r_value = 235;
								}
								break;
							}
						default:
							r_value = 0;
							break;
					}// Class Switch
					break;
				}
	////////////////////////////////////////////////////////
			default:
				break;
		}// Switch skill
	
		if (r_value > 252) {
			r_value = 252;
		}
		return r_value;
	}
	
	public static int maxSkillWeapon(SkillType skillType, String classname, int level) 
	{
		int r_value = 0;
		switch (skillType) {
			case TwoHandBlunt:
			case Piercing:
			case HandtoHand:
			case Crushing:
			case Slashing:
			case TwoHandSlashing: {
					switch (classname) {
						// Pure melee classes
						case "WARRIOR":
						case "WARRIORGM": {
								r_value = 5 + (level * 5);
								if ( level < 51 && r_value > 200) {
									r_value = 200;
								}
								if ( level > 50 && r_value > 250 ) {
									r_value = 250;
								}
								switch (skillType) {
									case Piercing: {
											if ( r_value > 240 ) {
												r_value = 240;
											}
											break;
										}
									case HandtoHand: {
											if ( r_value > 100 ) {
												r_value = 100;
											}
											break;
										}
									default:
										break;
								}
								break;
							}
						case "MONK":
						case "MONKGM": {
								r_value = 5 + (level * 5);
								if ( level < 51 && r_value > 240)
									if ( r_value > 240 ) {
										r_value = 240;
									}
								switch (skillType) {
									case Crushing:
									case HandtoHand: {
											if ( r_value > 225 && level < 51 ) {
												r_value = 225;
											}
											break;
										}
									case Piercing:
									case Slashing:
									case TwoHandSlashing: {
											r_value = 0;
											break;
										}
									default:
										break;
								}
								break;
							}
						case "ROGUE":
						case "ROGUEGM": {
								r_value = 5 + (level * 5);
								if ( level > 50 && r_value > 250 ) {
									r_value = 250;
								}
								if ( level < 51 ) {
									if ( r_value > 200 && !skillType.equals(SkillType.Piercing)) {
										r_value = 200;
									}
									if ( r_value > 210 && skillType.equals(SkillType.Piercing) ) {
										r_value = 210;
									}
								}
								if (skillType.equals(SkillType.HandtoHand) && r_value > 100) {
									r_value = 100;
								}
								break;
							}
						case "BERSERKER":
						case "BERSERKERGM": {
								r_value = 5 + (level * 5);
								if ( level < 51 && r_value > 240) {
									r_value = 240;
								}
								switch (skillType) {
									case HandtoHand: {
											if ( r_value > 198) {
												r_value = 198;
											}
											break;
										}
									case Piercing: {
											if ( r_value > 240) {
												r_value = 240;
											}
											break;
										}
									case Slashing:
									case Crushing:
									case TwoHandBlunt:
									case TwoHandSlashing: {
											if ( r_value > 252 ) {
												r_value = 252;
											}
											break;
										}
									default:
										r_value = 0;
										break;
								}
								break;
							}
						// Priest classes
						case "CLERIC":
						case "CLERICGM": {
								r_value = 4 + (level * 4);
								if ( r_value > 175 ) {
									r_value = 175;
								}
								switch (skillType) {
									case HandtoHand: {
											if ( r_value > 75 ) {
												r_value = 75;
											}
											break;
										}
									case Piercing:
									case Slashing:
									case TwoHandSlashing: {
											r_value = 0;
											break;
										}
									default:
										break;
								}
								break;
							}
						case "DRUID":
						case "DRUIDGM": {
								r_value = 4 + (level * 4);
								if ( r_value > 175 ) {
									r_value = 175;
								}
								switch (skillType) {
									case HandtoHand: {
											if ( r_value > 75 ) {
												r_value = 75;
											}
										}
									case Piercing:
									case Slashing:
									case TwoHandSlashing: {
											r_value = 0;
											break;
										}
									default:
										break;
								}
								break;
							}
						case "SHAMAN":
						case "SHAMANGM": {
								r_value = 4 + (level * 4);
								if ( r_value > 200 ) {
									r_value = 200;
								}
								switch (skillType) {
									case HandtoHand: {
											if ( r_value > 75 ) {
												r_value = 75;
											}
										}
									case Slashing:
									case TwoHandSlashing: {
											r_value = 0;
											break;
										}
									default:
										break;
								}
								break;
							}
						// Hybrids
						case "RANGER":
						case "RANGERGM": {
								r_value = 5 + (level * 5);
								if ( level > 50 ) {
									if ( r_value > 250 ) {
										r_value = 250;
									}
									switch (skillType) {
										case Piercing: {
												if ( r_value > 240 ) {
													r_value = 240;
												}
												break;
											}
										default:
											break;
									}
								} else if ( level < 51 ) {
									if ( r_value > 200 ) {
										r_value = 200;
									}
								}
								switch (skillType) {
									case HandtoHand: {
											if ( r_value > 100 ) {
												r_value = 100;
											}
											break;
										}
									default:
										break;
								}
								break;
							}
						case "PALADIN":
						case "PALADINGM":
						case "SHADOWKNIGHT":
						case "SHADOWKNIGHTGM": {
								r_value = 5 + (level * 5);
								if ( level > 50 && r_value > 225 ) {
									r_value = 225;
								}
								if ( level < 51 && r_value > 200 ) {
									r_value = 200;
								}
								switch (skillType) {
									case HandtoHand: {
											if ( r_value > 100 ) {
												r_value = 100;
											}
											break;
										}
									default:
										break;
								}
								break;
							}
						case "BARD":
						case "BARDGM": {
								r_value = 5 + (level * 5);
								if ( level > 51 && r_value > 225 ) {
									r_value = 225;
								}
								if ( level < 51 && r_value > 200 ) {
									r_value = 200;
								}
								switch (skillType) {
									case HandtoHand: {
											if ( r_value > 100 ) {
												r_value = 100;
											}
											break;
										}
									case TwoHandBlunt:
									case TwoHandSlashing: {
											r_value = 0;
										}
									default:
										break;
								}
								break;
							}
						case "BEASTLORD":
						case "BEASTLORDGM": {
								r_value = 4 + (level * 4);
								if ( level > 51 ) {
									if ( r_value > 225 ) {
										r_value = 225;
									}
								}
								if ( level < 51 && r_value > 200 ) {
									r_value = 200;
								}
								switch (skillType) {
									case HandtoHand: {
											r_value = 5 + (level * 5); // Beastlords use different max skill formula only for h2h 200/250
											if ( level < 51 ) {
												r_value = 200;
											}
											break;
										}
									case Slashing:
									case TwoHandSlashing: {
											r_value = 0;
											break;
										}
									default:
										break;
								}
								if ( r_value > 250 ) {
									r_value = 250;
								}
								break;
							}
						// Pure casters
						case "NECROMANCER":
						case "NECROMANCERGM":
						case "WIZARD":
						case "WIZARDGM":
						case "MAGICIAN":
						case "MAGICIANGM":
						case "ENCHANTER":
						case "ENCHANTERGM": {
								r_value = 3 + (level * 3);
								if ( r_value > 110 ) {
									r_value = 110;
								}
								switch (skillType) {
									case HandtoHand: {
											if ( r_value > 75 ) {
												r_value = 75;
											}
										}
									case Slashing:
									case TwoHandSlashing: {
											r_value = 0;
											break;
										}
									default:
										break;
								}
								break;
							}
						default:
							break;
					}
					break;// Switch Class
				}
			default:
				break;
		}// Switch skill
		if (r_value > 252) {
			r_value = 252;
		}
		return r_value;
	}

	public static int maxSkillOffensive(SkillType skillType, String classname, int level)
	{
		int r_value = 0;
		switch (skillType) {
			case Offense: {
					switch (classname) {
						// Melee
						case "WARRIOR":
						case "WARRIORGM":
						case "BERSERKER":
						case "BERSERKERGM":
						case "ROGUE":
						case "ROGUEGM": {
								// 210 252 5*level+5
								r_value = ((level * 5) + 5);
								if ( level < 51 ) {
									if (r_value > 210) {
										r_value = 210;
									}
								}
								if (r_value > 252) {
									r_value = 252;
								}
								break;
							}
						case "MONK":
						case "MONKGM": {
								// 230 252 5*level+5
								r_value = ((level * 5) + 5);
								if ( level < 51 ) {
									if (r_value > 230) {
										r_value = 230;
									}
								}
								if (r_value > 252) {
									r_value = 252;
								}
								break;
							}
						// Priest
						case "DRUID":
						case "DRUIDGM":
						case "SHAMAN":
						case "SHAMANGM":
						case "CLERIC":
						case "CLERICGM": {
								// 200 200 4*level+4
								r_value = ((level * 4) + 4);
								if (r_value > 200) {
									r_value = 200;
								}
								break;
							}
						// Hybrid
						case "BEASTLORD":
						case "BEASTLORDGM": {
								// 200 252 5*level+5
								r_value = ((level * 5) + 5);
								if ( level < 51 ) {
									if (r_value > 200) {
										r_value = 200;
									}
								}
								if (r_value > 252) {
									r_value = 252;
								}
								break;
							}
						case "PALADIN":
						case "PALADINGM":
						case "SHADOWKNIGHT":
						case "SHADOWKNIGHTGM":
						case "BARD":
						case "BARDGM": {
								// 200 225 5*level+5
								r_value = ((level * 5) + 5);
								if ( level < 51 ) {
									if (r_value > 200) {
										r_value = 200;
									}
								}
								if (r_value > 225) {
									r_value = 225;
								}
								break;
							}
						case "RANGER":
						case "RANGERGM": {
								// 210 252 5*level+5
								r_value = ((level * 5) + 5);
								if ( level < 51 ) {
									if (r_value > 210) {
										r_value = 210;
									}
								}
								if (r_value > 252) {
									r_value = 252;
								}
								break;
							}
						// Pure
						case "NECROMANCER":
						case "NECROMANCERGM":
						case "WIZARD":
						case "WIZARDGM":
						case "MAGICIAN":
						case "MAGICIANGM":
						case "ENCHANTER":
						case "ENCHANTERGM": {
								// 140 140 level*4
								r_value = (level * 4);
								if (r_value > 140) {
									r_value = 140;
								}
								break;
							}
						default:
							break;
					}
					break;
				}
			case Throwing: {
					switch (classname) {
						// Melee
						case "BERSERKER":
						case "BERSERKERGM":
						case "ROGUE":
						case "ROGUEGM": {
								// 220 250
								r_value = ((level * 5) + 5);
								if ( level < 51 ) {
									if (r_value > 220) {
										r_value = 220;
									}
								}
								if (r_value > 250) {
									r_value = 250;
								}
								break;
							}
						case "WARRIOR":
						case "WARRIORGM":
						case "MONK":
						case "MONKGM": {
								// 113 200
								r_value = ((level * 5) + 5);
								if ( level < 51 ) {
									if (r_value > 113) {
										r_value = 113;
									}
								}
								if (r_value > 200) {
									r_value = 200;
								}
								break;
							}
						// Hybrid
						case "BEASTLORD":
						case "BEASTLORDGM":
						case "BARD":
						case "BARDGM":
						case "RANGER":
						case "RANGERGM": {
								// 113
								r_value = ((level * 5) + 5);
								if ( r_value > 113 ) {
									r_value = 113;
								}
								break;
							}
						// Pure
						case "NECROMANCER":
						case "NECROMANCERGM":
						case "WIZARD":
						case "WIZARDGM":
						case "MAGICIAN":
						case "MAGICIANGM":
						case "ENCHANTER":
						case "ENCHANTERGM": {
								// 75
								r_value = ((level * 3) + 3);
								if ( r_value > 75 ) {
									r_value = 75;
								}
								break;
							}
						// No skill classes
						case "DRUID":
						case "DRUIDGM":
						case "SHAMAN":
						case "SHAMANGM":
						case "CLERIC":
						case "CLERICGM":
						case "PALADIN":
						case "PALADINGM":
						case "SHADOWKNIGHT":
						case "SHADOWKNIGHTGM":
						default:
							r_value = 0;
							break;
					}
					break;
				}
	/////////////////////////////////////////////////
			case Archery: {
					switch (classname) {
						// Melee
						case "ROGUE":
						case "ROGUEGM":
						case "WARRIOR":
						case "WARRIORGM": {
								// 200 240
								r_value = ((level * 5) + 5);
								if ( level < 51 && r_value > 200) {
									r_value = 200;
								}
								if (r_value > 240) {
									r_value = 240;
								}
								break;
							}
						// Hybrid
						case "PALADIN":
						case "PALADINGM":
						case "SHADOWKNIGHT":
						case "SHADOWKNIGHTGM": {
								// 75 75
								r_value = ((level * 5) + 5);
								if ( r_value > 75 ) {
									r_value = 75;
								}
								break;
							}
						case "RANGER":
						case "RANGERGM": {
								// 240 240
								r_value = ((level * 5) + 5);
								if ( r_value > 240 ) {
									r_value = 240;
								}
								break;
							}
						// Pure
						// No skill classes
						// Melee
						case "MONK":
						case "MONKGM":
						// Priest
						case "DRUID":
						case "DRUIDGM":
						case "SHAMAN":
						case "SHAMANGM":
						case "CLERIC":
						case "CLERICGM":
						// Pure
						case "NECROMANCER":
						case "NECROMANCERGM":
						case "WIZARD":
						case "WIZARDGM":
						case "MAGICIAN":
						case "MAGICIANGM":
						case "ENCHANTER":
						case "ENCHANTERGM":
						// Hybrid
						case "BEASTLORD":
						case "BEASTLORDGM":
						case "BARD":
						case "BARDGM":
						default:
							r_value = 0;
							break;
					}
					break;
				}
	/////////////////////////////////////////////////
			case DoubleAttack: {
					switch (classname) {
						// Melee
						case "ROGUE":
						case "ROGUEGM": {
								// 16 200 240
								r_value = ((level * 5) + 5);
								if ( level < 16 ) {
									r_value = 0;
								}
								if ( level < 51 ) {
									if (r_value > 200) {
										r_value = 200;
									}
								}
								if (r_value > 240) {
									r_value = 240;
								}
								break;
							}
						case "BERSERKER":
						case "BERSERKERGM":
						case "WARRIOR":
						case "WARRIORGM": {
								// 15 205 245
								r_value = ((level * 5) + 5);
								if ( level < 15 ) {
									r_value = 0;
								}
								if ( level < 51 ) {
									if (r_value > 200) {
										r_value = 200;
									}
								}
								if (r_value > 245) {
									r_value = 245;
								}
								break;
							}
						case "MONK":
						case "MONKGM": {
								// 15 210 250
								r_value = ((level * 5) + 5);
								if ( level < 15 ) {
									r_value = 0;
								}
								if ( level < 51 ) {
									if (r_value > 210) {
										r_value = 210;
									}
								}
								if (r_value > 250) {
									r_value = 250;
								}
								break;
							}
						// Hybrid
						case "PALADIN":
						case "PALADINGM":
						case "SHADOWKNIGHT":
						case "SHADOWKNIGHTGM": {
								// 20 200 235
								r_value = ((level * 5) + 5);
								if ( level < 20 ) {
									r_value = 0;
								}
								if ( level < 51 ) {
									if (r_value > 200) {
										r_value = 200;
									}
								}
								if (r_value > 235) {
									r_value = 235;
								}
								break;
							}
						case "RANGER":
						case "RANGERGM": {
								// 20 200 245
								r_value = ((level * 5) + 5);
								if ( level < 20 ) {
									r_value = 0;
								}
								if ( level < 51 ) {
									if (r_value > 200) {
										r_value = 200;
									}
								}
								if (r_value > 245) {
									r_value = 245;
								}
								break;
							}
						// Pure
						// No skill classes
						// Melee
						// Priest
						case "DRUID":
						case "DRUIDGM":
						case "SHAMAN":
						case "SHAMANGM":
						case "CLERIC":
						case "CLERICGM":
						// Pure
						case "NECROMANCER":
						case "NECROMANCERGM":
						case "WIZARD":
						case "WIZARDGM":
						case "MAGICIAN":
						case "MAGICIANGM":
						case "ENCHANTER":
						case "ENCHANTERGM":
						// Hybrid
						case "BEASTLORD":
						case "BEASTLORDGM":
						case "BARD":
						case "BARDGM":
						default:
							r_value = 0;
							break;
					}
					break;
				}
	/////////////////////////////////////////////////
			case DualWield: {
					switch (classname) {
						// Melee
						case "MONK":
						case "MONKGM": {
								// 1 252 252
								r_value = level * 7; // This can't be right can it?
								break
								;
							}
						case "WARRIOR":
						case "WARRIORGM":
						case "ROGUE":
						case "ROGUEGM": {
								// 15 210 245
								r_value = ((level * 5) + 5);
								if ( level < 13 ) {
									r_value = 0;
								}
								if ( level < 51 ) {
									if (r_value > 210) {
										r_value = 210;
									}
								}
								if (r_value > 245) {
									r_value = 245;
								}
								break;
							}
						// Hybrid
						case "BEASTLORD":
						case "BEASTLORDGM":
						// 17 210 245
						case "RANGER":
						case "RANGERGM": {
								// 17 210 245
								r_value = ((level * 5) + 5);
								if ( level < 17 ) {
									r_value = 0;
								}
								if ( level < 51 ) {
									if (r_value > 210) {
										r_value = 210;
									}
								}
								if (r_value > 245) {
									r_value = 245;
								}
								break;
							}
						case "BARD":
						case "BARDGM": {
								// 17 210 210
								r_value = ((level * 5) + 5);
								if ( level < 17 ) {
									r_value = 0;
								}
								if (r_value > 210) {
									r_value = 210;
								}
								break;
							}
						// No skill classes
						// Melee
						// Priest
						case "DRUID":
						case "DRUIDGM":
						case "SHAMAN":
						case "SHAMANGM":
						case "CLERIC":
						case "CLERICGM":
						// Pure
						case "NECROMANCER":
						case "NECROMANCERGM":
						case "WIZARD":
						case "WIZARDGM":
						case "MAGICIAN":
						case "MAGICIANGM":
						case "ENCHANTER":
						case "ENCHANTERGM":
						// Hybrid
						case "PALADIN":
						case "PALADINGM":
						case "SHADOWKNIGHT":
						case "SHADOWKNIGHTGM":
						default: {
								r_value = 0;
								break;
							}
					}// end Class switch
					break;
				} // end case "DUALWIELD":
	////////////////////////////////////////////////////////
			case Kick: {
					switch (classname) {
						// Melee
						case "BERSERKER":
						case "BERSERKERGM":
						case "WARRIOR":
						case "WARRIORGM": {
								// 1 149 210
								r_value = ((level * 5) + 5);
								if ( level < 51 ) {
									if (r_value > 149) {
										r_value = 149;
									}
								}
								if (r_value > 210) {
									r_value = 210;
								}
								break;
							}
						case "MONK":
						case "MONKGM": {
								// 1 200 250
								r_value = ((level * 5) + 5);
								if ( level < 51 ) {
									if (r_value > 200) {
										r_value = 200;
									}
								}
								if (r_value > 250) {
									r_value = 250;
								}
								break;
							}
						// Hybrid
						case "RANGER":
						case "RANGERGM": {
								// 5 149 205
								r_value = ((level * 5) + 5);
								if ( level < 5 ) {
									r_value = 0;
								}
								if ( level < 51 ) {
									if (r_value > 149) {
										r_value = 149;
									}
								}
								if (r_value > 205) {
									r_value = 205;
								}
								break;
							}
						case "BEASTLORD":
						case "BEASTLORDGM": {
								// 5 180 230
								r_value = ((level * 5) + 5);
								if ( level < 5 ) {
									r_value = 0;
								}
								if ( level < 51 ) {
									if (r_value > 180) {
										r_value = 180;
									}
								}
								if (r_value > 230) {
									r_value = 230;
								}
								break;
							}
						// Pure
						// No skill classes
						case "ROGUE":
						case "ROGUEGM":
						// Melee
						// Priest
						case "DRUID":
						case "DRUIDGM":
						case "SHAMAN":
						case "SHAMANGM":
						case "CLERIC":
						case "CLERICGM":
						// Pure
						case "NECROMANCER":
						case "NECROMANCERGM":
						case "WIZARD":
						case "WIZARDGM":
						case "MAGICIAN":
						case "MAGICIANGM":
						case "ENCHANTER":
						case "ENCHANTERGM":
						// Hybrid
						case "PALADIN":
						case "PALADINGM":
						case "SHADOWKNIGHT":
						case "SHADOWKNIGHTGM":
						case "BARD":
						case "BARDGM":
						default:
							r_value = 0;
							break;
					}
					break;
				}
	////////////////////////////////////////////////////////
			case Bash: {
					r_value = ((level * 5) + 5);
					switch (classname) {
						// Melee
						case "WARRIOR":
						case "WARRIORGM": {
								// 6 220 240
								if (level < 6) {
									r_value = 0;
								}
								if (level < 51 && r_value > 220) {
									r_value = 220;
								}
								if (r_value > 240) {
									r_value = 240;
								}
								break;
							}
						// Priest
						case "CLERIC":
						case "CLERICGM": {
								// 25 180 200
								if (level < 25) {
									r_value = 0;
								}
								if (level < 51 && r_value > 180) {
									r_value = 180;
								}
								if (r_value > 200) {
									r_value = 200;
								}
								break;
							}
						// Hybrid
						case "PALADIN":
						case "PALADINGM":
						case "SHADOWKNIGHT":
						case "SHADOWKNIGHTGM": {
								// 6 175 200
								if (level < 6) {
									r_value = 0;
								}
								if (level < 51 && r_value > 175) {
									r_value = 175;
								}
								if (r_value > 200) {
									r_value = 200;
								}
								break;
							}
						// Pure
						// No skill classes
						// Melee
						case "MONK":
						case "MONKGM":
						case "ROGUE":
						case "ROGUEGM":
						// Priest
						case "DRUID":
						case "DRUIDGM":
						case "SHAMAN":
						case "SHAMANGM":
						// Pure
						case "NECROMANCER":
						case "NECROMANCERGM":
						case "WIZARD":
						case "WIZARDGM":
						case "MAGICIAN":
						case "MAGICIANGM":
						case "ENCHANTER":
						case "ENCHANTERGM":
						// Hybrid
						case "BEASTLORD":
						case "BEASTLORDGM":
						case "RANGER":
						case "RANGERGM":
						case "BARD":
						case "BARDGM": {
								// switch (race) {
								//  case "BARBARIAN":
								// case "TROLL":
								//case "OGRE":{
								// r_value = 50;
								//break;
								//}
								//default: break;
								//}
								r_value = 0;
								break;
							}
					}
					break;
				}
	////////////////////////////////////////////////////////
			default:
				break;
		}// Switch skill
		if (r_value > 252) {
			r_value = 252;
		}
		return r_value;
	}

	public static int maxSkillDefensive(SkillType skillType, String classname, int level)
	{
		int r_value = 0;
		switch (skillType) {
			case Defense: {
					switch (classname) {
						// Melee
						case "WARRIOR":
						case "WARRIORGM": {
								// 210 252 5*level+5
								r_value = ((level * 5) + 5);
								if ( level < 51 ) {
									if (r_value > 210) {
										r_value = 210;
									}
								}
								if (r_value > 252) {
									r_value = 252;
	 							}
								break;
							}
						case "ROGUE":
						case "ROGUEGM": {
								// 200 252 5*level+5
								r_value = ((level * 5) + 5);
								if ( level < 51 ) {
									if (r_value > 200) {
										r_value = 200;
									}
								}
								if (r_value > 252) {
									r_value = 252;
								}
								break;
							}
						case "MONK":
						case "MONKGM": {
								// 230 252 5*level+5
								r_value = ((level * 5) + 5);
								if ( level < 51 ) {
									if (r_value > 230) {
										r_value = 230;
									}
								}
								if (r_value > 252) {
									r_value = 252;
								}
								break;
							}
						case "BERSERKER":
						case "BERSERKERGM": {
								// 230 252 5*level+5
								r_value = ((level * 5) + 5);
								if ( level < 51 ) {
									if (r_value > 230) {
										r_value = 230;
									}
								}
								if (r_value > 252) {
									r_value = 252;
								}
								break;
							}
						// Priest
						case "DRUID":
						case "DRUIDGM":
						case "SHAMAN":
						case "SHAMANGM":
						case "CLERIC":
						case "CLERICGM": {
								// 200 200 4*level+4
								r_value = ((level * 4) + 4);
								if (r_value > 200) {
									r_value = 200;
								}
								break;
							}
						// Hybrid
						case "BEASTLORD":
						case "BEASTLORDGM": {
								// 210 252 5*level+5
								r_value = ((level * 5) + 5);
								if ( level < 51 ) {
									if (r_value > 210) {
										r_value = 210;
									}
								}
								if (r_value > 252) {
									r_value = 252;
								}
								break;
							}
						case "PALADIN":
						case "PALADINGM":
						case "SHADOWKNIGHT":
						case "SHADOWKNIGHTGM": {
								// 210 252 5*level+5
								r_value = ((level * 5) + 5);
								if ( level < 51 ) {
									if (r_value > 210) {
										r_value = 210;
									}
								}
								if (r_value > 252) {
									r_value = 252;
								}
								break;
							}
						case "BARD":
						case "BARDGM": {
								// 200 252 5*level+5
								r_value = ((level * 5) + 5);
								if ( level < 51 ) {
									if (r_value > 200) {
										r_value = 200;
									}
								}
								if (r_value > 252) {
									r_value = 252;
								}
								break;
							}
						case "RANGER":
						case "RANGERGM": {
								// 200 200 5*level+5
								r_value = ((level * 5) + 5);
								if (r_value > 200) {
									r_value = 200;
								}
								break;
							}
						// Pure
						case "NECROMANCER":
						case "NECROMANCERGM":
						case "WIZARD":
						case "WIZARDGM":
						case "MAGICIAN":
						case "MAGICIANGM":
						case "ENCHANTER":
						case "ENCHANTERGM": {
								// 145 145 level*4
								r_value = (level * 4);
								if (r_value > 140) {
									r_value = 140;
								}
								break;
							}
						default:
							break;
					}
					break;
				}
			case Parry: {
					switch (classname) {
						// Melee
						case "ROGUE":
						case "ROGUEGM": {
								// 12 200 230
								r_value = ((level * 5) + 5);
								if ( level < 12 ) {
									r_value = 0;
								}
								if (r_value > 200 && level < 51 ) {
									r_value = 200;
								}
								if (r_value > 230) {
									r_value = 230;
								}
								break;
							}
						case "WARRIOR":
						case "WARRIORGM": {
								// 10 200 230
								r_value = ((level * 5) + 5);
								if ( level < 10 ) {
									r_value = 0;
								}
								if (r_value > 200 && level < 51 ) {
									r_value = 200;
								}
								if (r_value > 230) {
									r_value = 230;
								}
								break;
							}
						case "BERSERKER":
						case "BERSERKERGM": {
								r_value = ((level * 5) + 5);
								if ( level < 10 ) {
									r_value = 0;
								}
								if (r_value > 175) {
									r_value = 175;
								}
								break;
							}
						// Hybrid
						case "BARD":
						case "BARDGM": {
								// 53 0 75
								r_value = ((level * 5) + 5);
								if ( level < 53 ) {
									r_value = 0;
								}
								if (r_value > 75) {
									r_value = 75;
								}
								break;
							}
						case "PALADIN":
						case "PALADINGM":
						case "SHADOWKNIGHT":
						case "SHADOWKNIGHTGM": {
								// 17 175 205
								r_value = ((level * 5) + 5);
								if ( level < 17 ) {
									r_value = 0;
								}
								if (r_value > 175 && level < 51 ) {
									r_value = 175;
								}
								if (r_value > 205) {
									r_value = 205;
								}
								break;
							}
						case "RANGER":
						case "RANGERGM": {
								// 18 185 220
								r_value = ((level * 5) + 5);
								if ( level < 18 ) {
									r_value = 0;
								}
								if (r_value > 185 && level < 51 ) {
									r_value = 185;
								}
								if (r_value > 220) {
									r_value = 220;
								}
								break;
							}
						// Pure
						// No skill classes
						// Melee
						case "MONK":
						case "MONKGM":
						// Priest
						case "DRUID":
						case "DRUIDGM":
						case "SHAMAN":
						case "SHAMANGM":
						case "CLERIC":
						case "CLERICGM":
						// Pure
						case "NECROMANCER":
						case "NECROMANCERGM":
						case "WIZARD":
						case "WIZARDGM":
						case "MAGICIAN":
						case "MAGICIANGM":
						case "ENCHANTER":
						case "ENCHANTERGM":
						// Hybrid
						case "BEASTLORD":
						case "BEASTLORDGM":
						default:
							r_value = 0;
							break;
					}
					break;
				}
			case Riposte: {
					switch (classname) {
						// Melee
						case "BERSERKER":
						case "BERSERKERGM":
						case "WARRIOR":
						case "WARRIORGM": {
								// 25 200 225
								r_value = ((level * 5) + 5);
								if ( level < 25 ) {
									r_value = 0;
								}
								if (r_value > 200 && level < 51 ) {
									r_value = 200;
								}
								if (r_value > 225) {
									r_value = 225;
								}
								break;
							}
						case "ROGUE":
						case "ROGUEGM": {
								// 30 200 225
								r_value = ((level * 5) + 5);
								if ( level < 30 ) {
									r_value = 0;
								}
								if (r_value > 200 && level < 51 ) {
									r_value = 200;
								}
								if (r_value > 225) {
									r_value = 225;
								}
								break;
							}
						case "MONK":
						case "MONKGM": {
								// 35 200 225
								r_value = ((level * 5) + 5);
								if ( level < 35 ) {
									r_value = 0;
								}
								if (r_value > 200 && level < 51 ) {
									r_value = 200;
								}
								if (r_value > 225) {
									r_value = 225;
								}
								break;
							}
						// Hybrid
						case "BEASTLORD":
						case "BEASTLORDGM": {
								// 40 150 185
								r_value = ((level * 5) + 5);
								if ( level < 40 ) {
									r_value = 0;
								}
								if (r_value > 150 && level < 51 ) {
									r_value = 150;
								}
								if (r_value > 185) {
									r_value = 185;
								}
								break;
							}
						case "BARD":
						case "BARDGM": {
								// 58 75 75
								r_value = ((level * 5) + 5);
								if ( level < 58 ) {
									r_value = 0;
								}
								if (r_value > 75) {
									r_value = 75;
								}
								break;
							}
						case "PALADIN":
						case "PALADINGM":
						case "SHADOWKNIGHT":
						case "SHADOWKNIGHTGM": {
								// 30 175 200
								r_value = ((level * 5) + 5);
								if ( level < 30 ) {
									r_value = 0;
								}
								if (r_value > 175 && level < 51 ) {
									r_value = 175;
								}
								if (r_value > 200) {
									r_value = 200;
								}
								break;
							}
						case "RANGER":
						case "RANGERGM": {
								// 35 150 150
								r_value = ((level * 5) + 5);
								if ( level < 35 ) {
									r_value = 0;
								}
								if (r_value > 150) {
									r_value = 150;
								}
								break;
							}
						// Pure
						// No skill classes
						// Melee
						// Priest
						case "DRUID":
						case "DRUIDGM":
						case "SHAMAN":
						case "SHAMANGM":
						case "CLERIC":
						case "CLERICGM":
						// Pure
						case "NECROMANCER":
						case "NECROMANCERGM":
						case "WIZARD":
						case "WIZARDGM":
						case "MAGICIAN":
						case "MAGICIANGM":
						case "ENCHANTER":
						case "ENCHANTERGM":
						// Hybrid
						default:
							r_value = 0;
							break;
					}
					break;
				}
			case Dodge: {
					switch (classname) {
						// Melee
						case "BERSERKER":
						case "BERSERKERGM":
						case "WARRIOR":
						case "WARRIORGM": {
								// 6 140 175
								r_value = ((level * 5) + 5);
								if ( level < 6 ) {
									r_value = 0;
								}
								if (r_value > 140 && level < 51 ) {
									r_value = 140;
								}
								if (r_value > 175) {
									r_value = 175;
								}
								break;
							}
						case "ROGUE":
						case "ROGUEGM": {
								// 4 150 210
								r_value = ((level * 5) + 5);
								if ( level < 4 ) {
									r_value = 0;
								}
								if (r_value > 150 && level < 51 ) {
									r_value = 150;
								}
								if (r_value > 210) {
									r_value = 210;
								}
								break;
							}
						case "MONK":
						case "MONKGM": {
								// 1 200 230
								r_value = ((level * 5) + 5);
								if (r_value > 200) {
									r_value = 200;
								}
								if (r_value > 230) {
									r_value = 230;
								}
								break;
							}
						// Priest
						case "DRUID":
						case "DRUIDGM":
						case "SHAMAN":
						case "SHAMANGM":
						case "CLERIC":
						case "CLERICGM": {
								// 15 75 75 4*level+4
								r_value = ((level * 4) + 4);
								if ( level < 15 ) {
									r_value = 0;
								}
								if (r_value > 75) {
									r_value = 75;
								}
								break;
							}
						// Hybrid
						case "BEASTLORD":
						case "BEASTLORDGM":
						case "PALADIN":
						case "PALADINGM":
						case "SHADOWKNIGHT":
						case "SHADOWKNIGHTGM":
						case "BARD":
						case "BARDGM": {
								// 10 125 155 5*level+5
								r_value = ((level * 5) + 5);
								if ( level < 10 ) {
									r_value = 0;
								}
								if (r_value > 125 && level < 51 ) {
									r_value = 125;
								}
								if (r_value > 155) {
									r_value = 155;
								}
								break;
							}
						case "RANGER":
						case "RANGERGM": {
								// 8 137 170 5*level+5
								r_value = ((level * 5) + 5);
								if ( level < 8 ) {
									r_value = 0;
								}
								if (r_value > 137 && level < 51 ) {
									r_value = 137;
								}
								if (r_value > 170) {
									r_value = 170;
								}
								break;
							}
						// Pure
						case "NECROMANCER":
						case "NECROMANCERGM":
						case "WIZARD":
						case "WIZARDGM":
						case "MAGICIAN":
						case "MAGICIANGM":
						case "ENCHANTER":
						case "ENCHANTERGM": {
								// 22 75 75 3*level+3
								r_value = ((level * 3) + 3);
								if ( level < 22 ) {
									r_value = 0;
								}
								if (r_value > 75) {
									r_value = 75;
								}
								break;
							}
						// No skill classes
						// Melee
						// Priest
						// Pure
						// Hybrid
						default:
							break;
					}
					break;
				}
			// Other
			case Taunt: {
					switch (classname) {
						// Melee
						case "WARRIOR":
						case "WARRIORGM": {
								// 1 200 200
								r_value = ((level * 5) + 5);
								if (r_value > 200) {
									r_value = 200;
								}
								break;
							}
						// Priest
						// Hybrid
						case "PALADIN":
						case "PALADINGM":
						case "SHADOWKNIGHT":
						case "SHADOWKNIGHTGM": {
								// 1 180 180
								r_value = ((level * 5) + 5);
								if (r_value > 180) {
									r_value = 180;
								}
								break;
							}
						case "RANGER":
						case "RANGERGM": {
								// 1 150 150
								r_value = ((level * 5) + 5);
								if (r_value > 150) {
									r_value = 150;
								}
								break;
							}
						// Pure
						// No skill classes
						// Melee
						case "ROGUE":
						case "ROGUEGM":
						case "MONK":
						case "MONKGM":
						// Priest
						case "DRUID":
						case "DRUIDGM":
						case "SHAMAN":
						case "SHAMANGM":
						case "CLERIC":
						case "CLERICGM":
						// Pure
						case "NECROMANCER":
						case "NECROMANCERGM":
						case "WIZARD":
						case "WIZARDGM":
						case "MAGICIAN":
						case "MAGICIANGM":
						case "ENCHANTER":
						case "ENCHANTERGM":
						// Hybrid
						case "BEASTLORD":
						case "BEASTLORDGM":
						case "BARD":
						case "BARDGM":
						default:
							break;
					}
					break;
				}
			case Disarm: {
					switch (classname) {
						// Melee
						case "WARRIOR":
						case "WARRIORGM": {
								// 35 200 200
								r_value = ((level * 5) + 5);
								if (level < 35) {
									r_value = 0;
								}
								if (r_value > 200) {
									r_value = 200;
								}
								break;
							}
						case "ROGUE":
						case "ROGUEGM":
						case "MONK":
						case "MONKGM": {
								// 27 200 200
								r_value = ((level * 5) + 5);
								if (level < 27) {
									r_value = 0;
								}
								if (r_value > 200) {
									r_value = 200;
								}
								break;
							}
						case "BERSERKER":
						case "BERSERKERGM": {
								// 35 65 65
								r_value = ((level * 5) + 5);
								if (level < 35) {
									r_value = 0;
								}
								if (r_value > 65) {
									r_value = 65;
								}
								break;
							}
						// Priest
						// Hybrid
						case "PALADIN":
						case "PALADINGM":
						case "SHADOWKNIGHT":
						case "SHADOWKNIGHTGM": {
								// 40 70 70
								r_value = ((level * 5) + 5);
								if (level < 40) {
									r_value = 0;
								}
								if (r_value > 70) {
									r_value = 70;
								}
								break;
							}
						case "RANGER":
						case "RANGERGM": {
								// 35 55 55
								r_value = ((level * 5) + 5);
								if (level < 35) {
									r_value = 0;
								}
								if (r_value > 55) {
									r_value = 55;
								}
								break;
							}
						// Pure
						// No skill classes
						// Melee
						// Priest
						case "DRUID":
						case "DRUIDGM":
						case "SHAMAN":
						case "SHAMANGM":
						case "CLERIC":
						case "CLERICGM":
						// Pure
						case "NECROMANCER":
						case "NECROMANCERGM":
						case "WIZARD":
						case "WIZARDGM":
						case "MAGICIAN":
						case "MAGICIANGM":
						case "ENCHANTER":
						case "ENCHANTERGM":
						// Hybrid
						case "BARD":
						case "BARDGM":
						case "BEASTLORD":
						case "BEASTLORDGM":
						default:
							break;
					}
					break;
				}
	////////////////////////////////////////////////////////
			default:
				break;
		}// Switch skill
		if (r_value > 252) {
			r_value = 252;
		}
		return r_value;
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

	public static void PSetHPChange(LivingEntity targetToDamage, int hpchange, LivingEntity sourceEntityOfChange, boolean playHurtSound) {
		PSetHPChange(targetToDamage, (double)hpchange, sourceEntityOfChange, playHurtSound);
	}

	public static void PSetHPChange(LivingEntity targetToDamage, Double hpchange, LivingEntity sourceEntityOfChange, boolean playHurtSound) {
		if (targetToDamage instanceof ArmorStand || sourceEntityOfChange instanceof ArmorStand)
			return;
		
		if (hpchange == 0)
			return;
		
		//This will check both their invulnerability from minecraft and godmode from essentials
		if (hpchange < 0 && EntityUtils.IsInvulnerable(targetToDamage))
			return;
		
		float cur_hp =  ((EntityLiving)((CraftLivingEntity) targetToDamage).getHandle()).getHealth();
		float max_hp = ((EntityLiving)((CraftLivingEntity) targetToDamage).getHandle()).getMaxHealth();
		
		float hp = cur_hp + hpchange.floatValue();
		if (hp >= max_hp) 
			cur_hp = max_hp; 
		else 
			cur_hp = hp;

		((EntityLiving)((CraftLivingEntity) targetToDamage).getHandle()).setHealth(cur_hp);
		float soundVolume = 1.0F;
		if (hpchange < 0)
		{
			DamageSource damagesource = net.minecraft.server.v1_14_R1.DamageSource.mobAttack(((EntityLiving)((CraftLivingEntity) sourceEntityOfChange).getHandle()));
			DamageCause damagecause = DamageCause.ENTITY_ATTACK;
			EntityDamageByEntityEvent event = new EntityDamageByEntityEvent(sourceEntityOfChange,targetToDamage,damagecause,hpchange);
			
			((CraftLivingEntity)targetToDamage).setLastDamage(hpchange.doubleValue());
			((CraftLivingEntity)targetToDamage).setLastDamageCause(event);
			((CraftLivingEntity)targetToDamage).setNoDamageTicks(20);
			((EntityLiving)((CraftLivingEntity) targetToDamage).getHandle()).hurtDuration = 10;
			((EntityLiving)((CraftLivingEntity) targetToDamage).getHandle()).hurtTicks = ((EntityLiving)((CraftLivingEntity) targetToDamage).getHandle()).hurtDuration;

			((EntityLiving)((CraftLivingEntity) targetToDamage).getHandle()).getCombatTracker().trackDamage(damagesource, cur_hp, hpchange.floatValue());
			
			if (((EntityLiving)((CraftLivingEntity) targetToDamage).getHandle()).getHealth() <= 0.0F) {
	        	targetToDamage.getWorld().playSound(targetToDamage.getLocation(), Sound.ENTITY_GENERIC_DEATH, soundVolume, GetSoundPitch(((EntityLiving)((CraftLivingEntity) targetToDamage).getHandle())));
	
	            ((EntityLiving)((CraftLivingEntity) targetToDamage).getHandle()).die(damagesource);
	            
	        } else {
	        	if (targetToDamage instanceof Player)
	        	{
		        	PacketPlayOutAnimation packet = new PacketPlayOutAnimation(((CraftPlayer)targetToDamage).getHandle(), 1);
		        	((CraftPlayer)targetToDamage).getHandle().playerConnection.sendPacket(packet);
	        	}
	        	if (playHurtSound)
	        	targetToDamage.getWorld().playSound(targetToDamage.getLocation(), Sound.ENTITY_GENERIC_HURT, soundVolume, GetSoundPitch(((EntityLiving)((CraftLivingEntity) targetToDamage).getHandle())));
	        }
		}
	}


	private static float GetSoundPitch(EntityLiving targetToDamage) {
        return targetToDamage.isBaby() ? (targetToDamage.getRandom().nextFloat() - targetToDamage.getRandom().nextFloat()) * 0.2F + 1.5F : (targetToDamage.getRandom().nextFloat() - targetToDamage.getRandom().nextFloat()) * 0.2F + 1.0F;

	}


	public static boolean IsInvulnerable(Entity entity) {
		if (entity.isInvulnerable())
			return true;
		
		if (entity instanceof Player)
		{
			if (!((Player)entity).getGameMode().equals(GameMode.SURVIVAL))
				return true;
			
			Essentials ess = (Essentials) Bukkit.getPluginManager().getPlugin("Essentials");
			if (ess != null)
			{
				User user = ess.getUser((Player) entity);
				if (user != null && user.isGodModeEnabled())
					return true;
			}
		}
		
		return false;
	}
	
	public static void sendEntityDataPacket(LivingEntity entity, Player packetReceiverPlayer) {
		if (entity == null)
			return;
		
		if (packetReceiverPlayer == null)
			return;
		
		try {
			((EntityPlayer)((CraftPlayer)packetReceiverPlayer).getHandle()).playerConnection.sendPacket(new PacketPlayOutEntityMetadata(((net.minecraft.server.v1_14_R1.Entity)((CraftEntity)entity).getHandle()).getId(), ((net.minecraft.server.v1_14_R1.Entity)((CraftEntity)entity).getHandle()).getDataWatcher(), true));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
	}

	public static void sendAnimationPacket(LivingEntity entityForAnimation, Player packetReceiverPlayer, SolAnimationType animationType) {
		try {
			if (animationType == SolAnimationType.SwingArm)
			{
				PacketContainer packet = Solinia3CorePlugin.getProtocolManager().createPacket(PacketType.Play.Server.ANIMATION);
				packet.getEntityModifier(entityForAnimation.getWorld()).write(0, entityForAnimation);
				packet.getIntegers().write(1, 0);
				packet.setMeta("sol", 1);
	        	Solinia3CorePlugin.getProtocolManager().sendServerPacket((Player)packetReceiverPlayer, packet);
			}
			
			if (animationType == SolAnimationType.SwingOffArm)
			{
				PacketContainer packet = Solinia3CorePlugin.getProtocolManager().createPacket(PacketType.Play.Server.ANIMATION);
				packet.getEntityModifier(entityForAnimation.getWorld()).write(0, entityForAnimation);
				packet.getIntegers().write(1, 3);
				packet.setMeta("sol", 1);
	        	Solinia3CorePlugin.getProtocolManager().sendServerPacket((Player)packetReceiverPlayer, packet);
			}
        } catch (Exception ex) {
            ex.printStackTrace();
        }
	}
	
	public static Location getGroundLocationAt(Location location){
		final World world = location.getWorld();

        // Get the highest block in this world or null if no world
        final Block highest = world != null ? world.getHighestBlockAt(location).getRelative(BlockFace.DOWN) : null; // Get the highest block in this world or null if no world

        // If the highest block is not null and under the given location keep it if not get the block at given location
        Block block = highest != null && highest.getY() < location.getY() ? highest : location.getBlock();

        // Iterate all block under location until we find a solid block or reach Y == 0
        while(!block.getType().isSolid() && block.getLocation().getY() >= 0)
        {
            // Get the block under the current block
            block = block.getRelative(BlockFace.DOWN);
        }

        // Create a new Location instance with de Y of the block found or Y of given location if no block found
        return new Location(location.getWorld(), location.getX(), block.getY() >= 0 ? block.getY() + 1 : location.getY(), location.getZ(), location.getYaw(), location.getPitch());
    }


	public static void sit(Player bukkitPlayer) {
		if (bukkitPlayer == null)
			return;
		
		if (bukkitPlayer.getVehicle() != null)
			return;
		
		// Slam player to the floor
		EntityUtils.teleportSafely(bukkitPlayer,getGroundLocationAt(bukkitPlayer.getLocation()));
		
		Entity entity = bukkitPlayer.getWorld().spawnEntity(bukkitPlayer.getLocation().subtract(0, 0.5, 0),EntityType.ARROW);
		entity.setSilent(true);
		entity.setInvulnerable(true);
		entity.addPassenger(bukkitPlayer);
		bukkitPlayer.sendMessage("* You are no longer standing - Use /stand to get back up");
	}


	public static void teleportSafely(Entity targetEntity, Location targetLoc) {
		if (targetEntity == null)
			return;
		
		if (targetLoc == null)
			return;
		
		
		final UUID entityUuid = targetEntity.getUniqueId();
		final Location loc = targetLoc.clone();
		
		Bukkit.getScheduler().runTask(StateManager.getInstance().getPlugin(), new Runnable() {
		    @Override
		    public void run() {
		    	Entity entity = Bukkit.getEntity(entityUuid);
		    	if (entity == null)
		    		return;
		    	
		    	// if over 3 chunks, remove any pets
	    		if (entity.getLocation().distance(loc) > 48)
	    		{
	    			if (EntityUtils.HasPet(entityUuid))
	    			{
	    				EntityUtils.KillAllPets(entityUuid);
	    				entity.sendMessage(ChatColor.GRAY + "* Your pets have been removed due to teleportation" + ChatColor.RESET);
	    			}
	    		}
		    	
		    	if (entity instanceof Player)
				{
					Essentials ess = (Essentials) Bukkit.getPluginManager().getPlugin("Essentials");
					if (ess != null)
					{
						User user = ess.getUser((Player)entity);
						if (user == null)
							entity.teleport(loc);
						else
							try {
								user.getTeleport().now(loc, false, TeleportCause.PLUGIN);
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
								entity.teleport(loc);
							}
					} else {
						entity.teleport(loc);
					}
				} else {
					entity.teleport(loc);
				}
		    }
		});
	}

	public static boolean HasPet(UUID uuid)
	{
		try {
		LivingEntity pet = StateManager.getInstance().getEntityManager()
				.getPet(uuid);
		
		if (pet != null)
			return true;
		} catch (CoreStateInitException e) {

		}
		
		
		return false;
	}

	public static void KillAllPets(UUID uuid) {
		try {
			LivingEntity pet = StateManager.getInstance().getEntityManager()
					.getPet(uuid);
			if (pet == null)
				return;

			ISoliniaLivingEntity petsolEntity = SoliniaLivingEntityAdapter.Adapt(pet);
			StateManager.getInstance().getEntityManager().removePet(uuid,
					!petsolEntity.isCharmed());
		} catch (CoreStateInitException e) {

		}
	}
}
