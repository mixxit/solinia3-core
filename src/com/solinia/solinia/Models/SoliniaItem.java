package com.solinia.solinia.Models;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack;
import org.bukkit.entity.Creature;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wolf;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import com.solinia.solinia.Adapters.ItemStackAdapter;
import com.solinia.solinia.Adapters.SoliniaLivingEntityAdapter;
import com.solinia.solinia.Adapters.SoliniaPlayerAdapter;
import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Exceptions.InvalidItemSettingException;
import com.solinia.solinia.Interfaces.ISoliniaItem;
import com.solinia.solinia.Interfaces.ISoliniaLivingEntity;
import com.solinia.solinia.Interfaces.ISoliniaPlayer;
import com.solinia.solinia.Interfaces.ISoliniaSpell;
import com.solinia.solinia.Managers.StateManager;
import com.solinia.solinia.Utils.*;

import net.md_5.bungee.api.ChatColor;
import net.minecraft.server.v1_12_R1.NBTTagCompound;

public class SoliniaItem implements ISoliniaItem {

	private int id;
	private String displayname;
	private String basename;
	private int abilityid = 0;
	private String lore;
	private int strength = 0;
	private int stamina = 0;
	private int agility = 0;
	private int dexterity = 0;
	private int intelligence = 0;
	private int wisdom = 0;
	private int charisma = 0;
	private List<String> allowedClassNames = new ArrayList<String>();
	private String texturebase64;
	private boolean questitem = false;
	private int damage = 0;
	private int weaponabilityid = 0;
	private int attackspeed = 0;
	private String enchantment1;
	private int enchantment1val;
	private String enchantment2;
	private int enchantment2val;
	private String enchantment3;
	private int enchantment3val;
	private String enchantment4;
	private int enchantment4val;
	private int hpregen = 0;
	private int mpregen = 0;
	private int worth = 1;
	private boolean coreitem = false;
	private int fireResist = 0;
	private int coldResist = 0;
	private int magicResist = 0;
	private int poisonResist = 0;
	private int diseaseResist = 0;
	private boolean spellscroll = false;
	private short color;
	private int dye;
	private boolean isTemporary;
	private boolean isConsumable;
	private int baneUndead = 0;
	private boolean isPetControlRod = false;
	private boolean isAugmentation = false;
	private boolean isQuest = false;
	private AugmentationSlotType augmentationFitsSlotType = AugmentationSlotType.NONE;
	private String discoverer = "";
	private int minLevel = 0;
	private int ac = 0;
	private int hp = 0;
	private int mana = 0;
	private boolean isExperienceBonus = false;
	private SkillType skillModType = SkillType.None;
	private int skillModValue = 0;
	private boolean reagent = false;
	
	private boolean artifact = false;
	private boolean artifactFound = false;
	
	private SkillType skillModType2 = SkillType.None;
	private int skillModValue2 = 0;

	private SkillType skillModType3 = SkillType.None;
	private int skillModValue3 = 0;
	
	private SkillType skillModType4 = SkillType.None;
	private int skillModValue4 = 0;
	
	private boolean operatorCreated = true;
	
	private boolean isFingersItem = false;
	private boolean isNeckItem = false;
	private boolean isShouldersItem = false;
	private boolean isEarsItem = false;
	private boolean territoryFlag = false;
	
	
	@Override
	public ItemStack asItemStack() {
		return ItemStackAdapter.Adapt(this, 1);
	}

	@Override
	public int getId() {
		return id;
	}

	@Override
	public void setId(int id) {
		this.id = id;
	}

	@Override
	public String getDisplayname() {
		return displayname;
	}

	@Override
	public void setDisplayname(String displayname) {
		this.displayname = displayname;
	}

	@Override
	public int getAbilityid() {
		return abilityid;
	}

	@Override
	public void setAbilityid(int abilityid) {
		this.abilityid = abilityid;
	}

	@Override
	public String getBasename() {
		return basename;
	}

	@Override
	public void setBasename(String basename) {
		this.basename = basename;
	}

	@Override
	public String getLore() {
		return lore;
	}

	@Override
	public void setLore(String lore) {
		this.lore = lore;
	}

	@Override
	public int getStrength() {
		return strength;
	}

	@Override
	public void setStrength(int strength) {
		this.strength = strength;
	}

	@Override
	public int getStamina() {
		return stamina;
	}

	@Override
	public void setStamina(int stamina) {
		this.stamina = stamina;
	}

	@Override
	public int getAgility() {
		return agility;
	}

	@Override
	public void setAgility(int agility) {
		this.agility = agility;
	}

	@Override
	public int getDexterity() {
		return dexterity;
	}

	@Override
	public void setDexterity(int dexterity) {
		this.dexterity = dexterity;
	}

	@Override
	public int getIntelligence() {
		return intelligence;
	}

	@Override
	public void setIntelligence(int intelligence) {
		this.intelligence = intelligence;
	}

	@Override
	public int getWisdom() {
		return wisdom;
	}

	@Override
	public void setWisdom(int wisdom) {
		this.wisdom = wisdom;
	}

	@Override
	public int getCharisma() {
		return charisma;
	}

	@Override
	public void setCharisma(int charisma) {
		this.charisma = charisma;
	}

	@Override
	public List<String> getAllowedClassNames() {
		return allowedClassNames;
	}

	@Override
	public void setAllowedClassNames(List<String> allowedClassesNames) {
		this.allowedClassNames = allowedClassesNames;
	}

	@Override
	public String getTexturebase64() {
		return texturebase64;
	}

	@Override
	public void setTexturebase64(String texturebase64) {
		this.texturebase64 = texturebase64;
	}

	@Override
	public boolean getQuestitem() {
		return questitem;
	}

	@Override
	public void setQuestitem(boolean questitem) {
		this.questitem = questitem;
	}

	@Override
	public int getDamage() {
		return damage;
	}

	@Override
	public void setDamage(int damage) {
		this.damage = damage;
	}

	@Override
	public int getWeaponabilityid() {
		return weaponabilityid;
	}

	@Override
	public void setWeaponabilityid(int weaponabilityid) {
		this.weaponabilityid = weaponabilityid;
	}

	@Override
	public int getAttackspeed() {
		return attackspeed;
	}

	@Override
	public void setAttackspeed(int attackspeed) {
		this.attackspeed = attackspeed;
	}

	@Override
	public String getEnchantment1() {
		return enchantment1;
	}

	@Override
	public void setEnchantment1(String enchantment1) {
		this.enchantment1 = enchantment1;
	}

	@Override
	public int getEnchantment1val() {
		return enchantment1val;
	}

	@Override
	public void setEnchantment1val(int enchantment1val) {
		this.enchantment1val = enchantment1val;
	}

	@Override
	public String getEnchantment2() {
		return enchantment2;
	}

	@Override
	public void setEnchantment2(String enchantment2) {
		this.enchantment2 = enchantment2;
	}

	@Override
	public int getEnchantment2val() {
		return enchantment2val;
	}

	@Override
	public void setEnchantment2val(int enchantment2val) {
		this.enchantment2val = enchantment2val;
	}

	@Override
	public String getEnchantment3() {
		return enchantment3;
	}

	@Override
	public void setEnchantment3(String enchantment3) {
		this.enchantment3 = enchantment3;
	}

	@Override
	public int getEnchantment3val() {
		return enchantment3val;
	}

	@Override
	public void setEnchantment3val(int enchantment3val) {
		this.enchantment3val = enchantment3val;
	}

	@Override
	public String getEnchantment4() {
		return enchantment4;
	}

	@Override
	public void setEnchantment4(String enchantment4) {
		this.enchantment4 = enchantment4;
	}

	@Override
	public int getEnchantment4val() {
		return enchantment4val;
	}

	@Override
	public void setEnchantment4val(int enchantment4val) {
		this.enchantment4val = enchantment4val;
	}

	@Override
	public int getHpregen() {
		return hpregen;
	}

	@Override
	public void setHpregen(int hpregen) {
		this.hpregen = hpregen;
	}

	@Override
	public int getMpregen() {
		return mpregen;
	}

	@Override
	public void setMpregen(int mpregen) {
		this.mpregen = mpregen;
	}

	@Override
	public boolean isCoreitem() {
		return coreitem;
	}

	@Override
	public void setCoreitem(boolean coreitem) {
		this.coreitem = coreitem;
	}

	@Override
	public int getFireResist() {
		return fireResist;
	}

	@Override
	public void setFireResist(int fireResist) {
		this.fireResist = fireResist;
	}

	@Override
	public int getColdResist() {
		return coldResist;
	}

	@Override
	public void setColdResist(int coldResist) {
		this.coldResist = coldResist;
	}

	@Override
	public int getMagicResist() {
		return magicResist;
	}

	@Override
	public void setMagicResist(int magicResist) {
		this.magicResist = magicResist;
	}

	@Override
	public int getWorth() {
		return worth;
	}

	@Override
	public void setWorth(int worth) {
		this.worth = worth;
	}

	@Override
	public int getPoisonResist() {
		return poisonResist;
	}

	@Override
	public void setPoisonResist(int poisonResist) {
		this.poisonResist = poisonResist;
	}

	@Override
	public boolean isSpellscroll() {
		return spellscroll;
	}

	@Override
	public void setSpellscroll(boolean spellscroll) {
		this.spellscroll = spellscroll;
	}

	@Override
	public boolean useItemOnEntity(Plugin plugin, Player player, LivingEntity targetentity, boolean isConsumable)
			throws CoreStateInitException {
		
		if (isPetControlRod())
		{
			LivingEntity pet = StateManager.getInstance().getEntityManager().getPet(player);
			if (pet != null)
			{
				if (pet instanceof Wolf)
				{
					// Move pet to player
					pet.teleport(player.getLocation());
					
					// Mez cancel target
					Timestamp mezExpiry = StateManager.getInstance().getEntityManager().getMezzed(targetentity);
	
					if (mezExpiry != null) {
						((Creature) pet).setTarget(null);
						Wolf wolf = (Wolf)pet;
						wolf.setTarget(null);
						player.sendMessage("You cannot send your pet to attack a mezzed player");
						return false;
					}
					
					if (!pet.getUniqueId().equals(targetentity.getUniqueId()))
					{
						Wolf wolf = (Wolf)pet;
						wolf.setTarget(targetentity);
						player.sendMessage("You send your pet to attack!");
						return true;
					} else {
						Wolf wolf = (Wolf)pet;
						wolf.setTarget(null);
						player.sendMessage("You cannot send your pet to attack itself");
						return false;
					}
				}
			}
		}
		
		if (isConsumable == true && isExperienceBonus())
		{
			SoliniaPlayerAdapter.Adapt(player).grantExperienceBonusFromItem();
			System.out.println("Granted " + player.getName() + " experience bonus from item [" + SoliniaPlayerAdapter.Adapt(player).getExperienceBonusExpires().toString() + "]");
			return true;
		}
		
		ISoliniaSpell spell = StateManager.getInstance().getConfigurationManager().getSpell(getAbilityid());
		if (spell == null) {
			return false;
		}

		ISoliniaLivingEntity solentity = SoliniaLivingEntityAdapter.Adapt((LivingEntity)player);
		if (solentity == null)
			return false;

		if (!isConsumable)
		if (spell.getActSpellCost(solentity) > SoliniaPlayerAdapter.Adapt(player).getMana()) {
			player.sendMessage(ChatColor.GRAY + "Insufficient Mana  [E] (Hold crouch or use /trance to meditate)");
			return false;
		}
		
		if (!spell.isBardSong())
		{
			if (spell.getComponents1() > 0)
			{
				ISoliniaPlayer solPlayer = SoliniaPlayerAdapter.Adapt(player);
				ISoliniaItem item = StateManager.getInstance().getConfigurationManager().getItem(spell.getComponents1());
				if (item == null || !item.isReagent())
				{
					player.sendMessage(ChatColor.RED + "ERROR: " + ChatColor.YELLOW + "ERROR-ALERT-ADMIN-SPELL"+ spell.getId() + "-ID" + spell.getComponents1());
					return false;
				}
				if(!solPlayer.hasSufficientReagents(spell.getComponents1(),spell.getComponentCounts1()))
				{
					player.sendMessage(ChatColor.GRAY + "Insufficient Reagents (Check spell and see /reagents)");
					return false;
				}
			}
			
			if (spell.getComponents2() > 0)
			{
				ISoliniaPlayer solPlayer = SoliniaPlayerAdapter.Adapt(player);
				ISoliniaItem item = StateManager.getInstance().getConfigurationManager().getItem(spell.getComponents2());
				if (item == null || !item.isReagent())
				{
					player.sendMessage(ChatColor.RED + "ERROR: " + ChatColor.YELLOW + "ERROR-ALERT-ADMIN-SPELL"+ spell.getId() + "-ID" + spell.getComponents2());
					return false;
				}
				if(!solPlayer.hasSufficientReagents(spell.getComponents2(),spell.getComponentCounts2()))
				{
					player.sendMessage(ChatColor.GRAY + "Insufficient Reagents (Check spell and see /reagents)");
					return false;
				}
			}
			
			if (spell.getComponents3() > 0)
			{
				ISoliniaPlayer solPlayer = SoliniaPlayerAdapter.Adapt(player);
				ISoliniaItem item = StateManager.getInstance().getConfigurationManager().getItem(spell.getComponents3());
				if (item == null || !item.isReagent())
				{
					player.sendMessage(ChatColor.RED + "ERROR: " + ChatColor.YELLOW + "ERROR-ALERT-ADMIN-SPELL"+ spell.getId() + "-ID" + spell.getComponents3());
					return false;
				}
				if(!solPlayer.hasSufficientReagents(spell.getComponents3(),spell.getComponentCounts3()))
				{
					player.sendMessage(ChatColor.GRAY + "Insufficient Reagents (Check spell and see /reagents)");
					return false;
				}
			}
			
			if (spell.getComponents4() > 0)
			{
				ISoliniaPlayer solPlayer = SoliniaPlayerAdapter.Adapt(player);
				ISoliniaItem item = StateManager.getInstance().getConfigurationManager().getItem(spell.getComponents4());
				if (item == null || !item.isReagent())
				{
					player.sendMessage(ChatColor.RED + "ERROR: " + ChatColor.YELLOW + "ERROR-ALERT-ADMIN-SPELL"+ spell.getId() + "-ID" + spell.getComponents4());
					return false;
				}
				if(!solPlayer.hasSufficientReagents(spell.getComponents4(),spell.getComponentCounts4()))
				{
					player.sendMessage(ChatColor.GRAY + "Insufficient Reagents (Check spell and see /reagents)");
					return false;
				}
			}
		}
		
		
		try
		{
			if (StateManager.getInstance().getEntityManager().getEntitySpellCooldown(player, spell.getId()) != null)
			{
				LocalDateTime datetime = LocalDateTime.now();
				Timestamp nowtimestamp = Timestamp.valueOf(datetime);
				Timestamp expiretimestamp = StateManager.getInstance().getEntityManager().getEntitySpellCooldown(player, spell.getId());
	
				if (expiretimestamp != null)
				if (!nowtimestamp.after(expiretimestamp))
				{
					player.sendMessage("You do not have enough willpower to cast " + spell.getName() + " (Wait: " + ((expiretimestamp.getTime() - nowtimestamp.getTime())/1000) + "s");
					return false;
				}
			}
		} catch (CoreStateInitException e)
		{
			// skip
			return false;
		}
		

		boolean itemUseSuccess = spell.tryApplyOnEntity(plugin, player, targetentity);

		if (itemUseSuccess) {
			
			int recastTime = spell.getRecastTime();
			if (spell.isAASpell())
			{
				recastTime = spell.getAARecastTime(SoliniaPlayerAdapter.Adapt(player));
				if (recastTime < spell.getRecastTime())
				{
					recastTime = spell.getRecastTime();
				}
			}
			
			if (spell.getRecastTime() > 0)
			{
				LocalDateTime datetime = LocalDateTime.now();
				Timestamp expiretimestamp = Timestamp.valueOf(datetime.plus(recastTime,ChronoUnit.MILLIS));
				StateManager.getInstance().getEntityManager().addEntitySpellCooldown(player, spell.getId(),expiretimestamp);
			}
			if (!isConsumable)
			{
				SoliniaPlayerAdapter.Adapt(player).reducePlayerMana(spell.getActSpellCost(solentity));
				
				if (!spell.isBardSong())
				{
					if (spell.getComponents1() > 0)
					{
						SoliniaPlayerAdapter.Adapt(player).reduceReagents(spell.getComponents1(),spell.getComponentCounts1());
					}
					if (spell.getComponents2() > 0)
					{
						SoliniaPlayerAdapter.Adapt(player).reduceReagents(spell.getComponents2(),spell.getComponentCounts2());
					}
					if (spell.getComponents3() > 0)
					{
						SoliniaPlayerAdapter.Adapt(player).reduceReagents(spell.getComponents3(),spell.getComponentCounts3());
					}
					if (spell.getComponents4() > 0)
					{
						SoliniaPlayerAdapter.Adapt(player).reduceReagents(spell.getComponents4(),spell.getComponentCounts4());
					}
				}
			}
		}

		return itemUseSuccess;
	}

	@Override
	public boolean useItemOnBlock(Player player, ISoliniaItem solitem, Block clickedBlock, boolean isConsumable) throws CoreStateInitException {
		ISoliniaSpell spell = StateManager.getInstance().getConfigurationManager().getSpell(solitem.getAbilityid());
		if (spell == null) {
			return false;
		}
		
		ISoliniaLivingEntity solentity = SoliniaLivingEntityAdapter.Adapt((LivingEntity)player);
		
		if (solentity == null)
			return false;


		if (!isConsumable)
		if (spell.getActSpellCost(solentity) > SoliniaPlayerAdapter.Adapt(player).getMana()) {
			player.sendMessage(ChatColor.GRAY + "Insufficient Mana [E]  (Hold crouch or use /trance to meditate)");
			return false;
		}
		
		if (!spell.isBardSong())
		{
			if (spell.getComponents1() > 0)
			{
				ISoliniaPlayer solPlayer = SoliniaPlayerAdapter.Adapt(player);
				ISoliniaItem item = StateManager.getInstance().getConfigurationManager().getItem(spell.getComponents1());
				if (item == null || !item.isReagent())
				{
					player.sendMessage(ChatColor.RED + "ERROR: " + ChatColor.YELLOW + "ERROR-ALERT-ADMIN-SPELL"+ spell.getId() + "-ID" + spell.getComponents1());
					return false;
				}
				if(!solPlayer.hasSufficientReagents(spell.getComponents1(),spell.getComponentCounts1()))
				{
					player.sendMessage(ChatColor.GRAY + "Insufficient Reagents (Check spell and see /reagents)");
					return false;
				}
			}
			
			if (spell.getComponents2() > 0)
			{
				ISoliniaPlayer solPlayer = SoliniaPlayerAdapter.Adapt(player);
				ISoliniaItem item = StateManager.getInstance().getConfigurationManager().getItem(spell.getComponents2());
				if (item == null || !item.isReagent())
				{
					player.sendMessage(ChatColor.RED + "ERROR: " + ChatColor.YELLOW + "ERROR-ALERT-ADMIN-SPELL"+ spell.getId() + "-ID" + spell.getComponents2());
					return false;
				}
				if(!solPlayer.hasSufficientReagents(spell.getComponents2(),spell.getComponentCounts2()))
				{
					player.sendMessage(ChatColor.GRAY + "Insufficient Reagents (Check spell and see /reagents)");
					return false;
				}
			}
			
			if (spell.getComponents3() > 0)
			{
				ISoliniaPlayer solPlayer = SoliniaPlayerAdapter.Adapt(player);
				ISoliniaItem item = StateManager.getInstance().getConfigurationManager().getItem(spell.getComponents3());
				if (item == null || !item.isReagent())
				{
					player.sendMessage(ChatColor.RED + "ERROR: " + ChatColor.YELLOW + "ERROR-ALERT-ADMIN-SPELL"+ spell.getId() + "-ID" + spell.getComponents3());
					return false;
				}
				if(!solPlayer.hasSufficientReagents(spell.getComponents3(),spell.getComponentCounts3()))
				{
					player.sendMessage(ChatColor.GRAY + "Insufficient Reagents (Check spell and see /reagents)");
					return false;
				}
			}
			
			if (spell.getComponents4() > 0)
			{
				ISoliniaPlayer solPlayer = SoliniaPlayerAdapter.Adapt(player);
				ISoliniaItem item = StateManager.getInstance().getConfigurationManager().getItem(spell.getComponents4());
				if (item == null || !item.isReagent())
				{
					player.sendMessage(ChatColor.RED + "ERROR: " + ChatColor.YELLOW + "ERROR-ALERT-ADMIN-SPELL"+ spell.getId() + "-ID" + spell.getComponents4());
					return false;
				}
				if(!solPlayer.hasSufficientReagents(spell.getComponents4(),spell.getComponentCounts4()))
				{
					player.sendMessage(ChatColor.GRAY + "Insufficient Reagents (Check spell and see /reagents)");
					return false;
				}
			}
		}

		boolean itemUseSuccess = false;

		itemUseSuccess = spell.tryApplyOnBlock(player, clickedBlock);

		if (itemUseSuccess) {
			SoliniaPlayerAdapter.Adapt(player).reducePlayerMana(spell.getActSpellCost(solentity));
		
			if (!spell.isBardSong())
			{
				if (spell.getComponents1() > 0)
				{
					SoliniaPlayerAdapter.Adapt(player).reduceReagents(spell.getComponents1(),spell.getComponentCounts1());
				}
				if (spell.getComponents2() > 0)
				{
					SoliniaPlayerAdapter.Adapt(player).reduceReagents(spell.getComponents2(),spell.getComponentCounts2());
				}
				if (spell.getComponents3() > 0)
				{
					SoliniaPlayerAdapter.Adapt(player).reduceReagents(spell.getComponents3(),spell.getComponentCounts3());
				}
				if (spell.getComponents4() > 0)
				{
					SoliniaPlayerAdapter.Adapt(player).reduceReagents(spell.getComponents4(),spell.getComponentCounts4());
				}
			}
			
		}

		return itemUseSuccess;
	}

	private String convertItemStackToJsonRegular() {
        // First we convert the item stack into an NMS itemstack
        net.minecraft.server.v1_12_R1.ItemStack nmsItemStack = CraftItemStack.asNMSCopy(asItemStack());
        NBTTagCompound compound = new NBTTagCompound();
        compound = nmsItemStack.save(compound);

        return compound.toString();
    }
	
	@Override
	public String asJsonString() {
		String out = convertItemStackToJsonRegular();
		return out;
	}

	@Override
	public String asJsonStringEscaped() {
		String out = asJsonString();
		return out.replace("\\", "\\\\").replace("\"", "\\\"").replace("\r", "\\r").replace("\n", "\\n");
	}

	@Override
	public void sendItemSettingsToSender(CommandSender sender) throws CoreStateInitException {
		sender.sendMessage(ChatColor.RED + "Item Settings for " + ChatColor.GOLD + getDisplayname() + ChatColor.RESET);
		sender.sendMessage("----------------------------");
		sender.sendMessage("- id: " + ChatColor.GOLD + getId() + ChatColor.RESET);
		sender.sendMessage("- displayname: " + ChatColor.GOLD + getDisplayname() + ChatColor.RESET);
		sender.sendMessage("- basename: " + ChatColor.GOLD + getBasename() + ChatColor.RESET);
		sender.sendMessage("- color (blocktype): " + ChatColor.GOLD + getColor() + ChatColor.RESET);
		sender.sendMessage("- dye (armour color): " + ChatColor.GOLD + getDye() + ChatColor.RESET);
		sender.sendMessage("- minlevel: " + ChatColor.GOLD + getMinLevel() + ChatColor.RESET);
		sender.sendMessage("- temporary: " + ChatColor.GOLD + isTemporary() + ChatColor.RESET);
		sender.sendMessage("- worth: " + ChatColor.GOLD + getWorth() + ChatColor.RESET);
		sender.sendMessage("- abilityid: " + ChatColor.GOLD + getAbilityid() + ChatColor.RESET);		
		sender.sendMessage("- consumable: " + ChatColor.GOLD + isConsumable() + ChatColor.RESET);
		sender.sendMessage("- petcontrolrod: " + ChatColor.GOLD + isPetControlRod() + ChatColor.RESET);
		sender.sendMessage("- crafting: " + ChatColor.GOLD + isCrafting() + ChatColor.RESET);
		sender.sendMessage("- augmentation: " + ChatColor.GOLD + isAugmentation() + ChatColor.RESET);
		sender.sendMessage("- quest: " + ChatColor.GOLD + isQuest() + ChatColor.RESET);
		sender.sendMessage("- artifact: " + ChatColor.GOLD + isArtifact() + ChatColor.RESET + " Found: (" + isArtifactFound() + ")"+ ChatColor.RESET);
		sender.sendMessage("- acceptsaugmentationslottype: " + ChatColor.GOLD + getAcceptsAugmentationSlotType() + ChatColor.RESET);
		sender.sendMessage("- augmentationfitsslottype: " + ChatColor.GOLD + this.getAugmentationFitsSlotType().name() + ChatColor.RESET);
		sender.sendMessage("----------------------------");
		sender.sendMessage("- ac: " + ChatColor.GOLD + getAC() + ChatColor.RESET);
		sender.sendMessage("- hp: " + ChatColor.GOLD + getHp() + ChatColor.RESET);
		sender.sendMessage("- mana: " + ChatColor.GOLD + getMana() + ChatColor.RESET);
		sender.sendMessage("----------------------------");
		sender.sendMessage("- damage: " + ChatColor.GOLD + getDamage() + ChatColor.RESET);
		sender.sendMessage("- baneundead: " + ChatColor.GOLD + getBaneUndead() + ChatColor.RESET);
		sender.sendMessage("- weaponabilityid: " + ChatColor.GOLD + getWeaponabilityid() + ChatColor.RESET);
		sender.sendMessage("----------------------------");
		sender.sendMessage("- strength: " + ChatColor.GOLD + getStrength() + ChatColor.RESET);
		sender.sendMessage("- stamina: " + ChatColor.GOLD + getStamina() + ChatColor.RESET);
		sender.sendMessage("- agility: " + ChatColor.GOLD + getAgility() + ChatColor.RESET);
		sender.sendMessage("- dexterity: " + ChatColor.GOLD + getDexterity() + ChatColor.RESET);
		sender.sendMessage("- intelligence: " + ChatColor.GOLD + getIntelligence() + ChatColor.RESET);
		sender.sendMessage("- wisdom: " + ChatColor.GOLD + getWisdom() + ChatColor.RESET);
		sender.sendMessage("- charisma: " + ChatColor.GOLD + getCharisma() + ChatColor.RESET);
		sender.sendMessage("----------------------------");
		sender.sendMessage("- magicresist: " + ChatColor.GOLD + getMagicResist() + ChatColor.RESET);
		sender.sendMessage("- coldresist: " + ChatColor.GOLD + getColdResist() + ChatColor.RESET);
		sender.sendMessage("- fireresist: " + ChatColor.GOLD + getFireResist() + ChatColor.RESET);
		sender.sendMessage("- diseaseresist: " + ChatColor.GOLD + getDiseaseResist() + ChatColor.RESET);
		sender.sendMessage("- poisonresist: " + ChatColor.GOLD + getPoisonResist() + ChatColor.RESET);
		sender.sendMessage("----------------------------");
		sender.sendMessage("- hpregen: " + ChatColor.GOLD + getHpregen() + ChatColor.RESET);
		sender.sendMessage("- mpregen: " + ChatColor.GOLD + getMpregen() + ChatColor.RESET);
		sender.sendMessage("----------------------------");
		sender.sendMessage("- skillmodtype: " + ChatColor.GOLD + getSkillModType().toString() + ChatColor.RESET);
		sender.sendMessage("- skillmodvalue: " + ChatColor.GOLD + getSkillModValue() + ChatColor.RESET);
		sender.sendMessage("- skillmodtype2: " + ChatColor.GOLD + getSkillModType2().toString() + ChatColor.RESET);
		sender.sendMessage("- skillmodvalue2: " + ChatColor.GOLD + getSkillModValue2() + ChatColor.RESET);
		sender.sendMessage("- skillmodtype3: " + ChatColor.GOLD + getSkillModType3().toString() + ChatColor.RESET);
		sender.sendMessage("- skillmodvalue3: " + ChatColor.GOLD + getSkillModValue3() + ChatColor.RESET);
		sender.sendMessage("- skillmodtype4: " + ChatColor.GOLD + getSkillModType4().toString() + ChatColor.RESET);
		sender.sendMessage("- skillmodvalue4: " + ChatColor.GOLD + getSkillModValue4() + ChatColor.RESET);
		sender.sendMessage("----------------------------");
		sender.sendMessage("- fingersItem: " + ChatColor.GOLD + isFingersItem() + ChatColor.RESET);
		sender.sendMessage("- neckItem: " + ChatColor.GOLD + isNeckItem() + ChatColor.RESET);
		sender.sendMessage("- shouldersItem: " + ChatColor.GOLD + isShouldersItem() + ChatColor.RESET);
		sender.sendMessage("- earsItem: " + ChatColor.GOLD + isShouldersItem() + ChatColor.RESET);
		sender.sendMessage("----------------------------");
		sender.sendMessage("- discoverer: " + ChatColor.GOLD + getDiscoverer() + ChatColor.RESET);
		sender.sendMessage("- territoryflag: " + ChatColor.GOLD + isTerritoryFlag() + ChatColor.RESET);
		sender.sendMessage("- reagent: " + ChatColor.GOLD + isReagent() + ChatColor.RESET);
		sender.sendMessage("- allowedclassnames: ");
		for(String classname : this.getAllowedClassNames())
		{
			sender.sendMessage(" - " + ChatColor.GOLD + classname + ChatColor.RESET);
		}

	}

	@Override
	public void editSetting(String setting, String value)
			throws InvalidItemSettingException, NumberFormatException, CoreStateInitException {

		StateManager.getInstance().getConfigurationManager().setItemsChanged(true);
		
		switch (setting.toLowerCase()) {
		case "displayname":
			if (value.equals(""))
				throw new InvalidItemSettingException("Name is empty");

			if (value.length() > 36)
				throw new InvalidItemSettingException("Name is longer than 36 characters");
			setDisplayname(value);
			break;
		case "worth":
			setWorth(Integer.parseInt(value));
			break;
		case "dye":
			setDye(Integer.parseInt(value));
			break;
		case "allowedclassnames":
			String[] allowedclasses = value.split(",");
			setAllowedClassNames(Arrays.asList(allowedclasses));
			break;
		case "color":
			setColor(Short.parseShort(value));
			break;
		case "damage":
			setDamage(Integer.parseInt(value));
			break;
		case "spellscroll":
			this.setSpellscroll(Boolean.parseBoolean(value));
			break;
		case "artifact":
			setArtifact(Boolean.parseBoolean(value));
			break;
		case "baneundead":
			setBaneUndead(Integer.parseInt(value));
			break;
		case "hpregen":
			setHpregen(Integer.parseInt(value));
			break;
		case "mpregen":
			setMpregen(Integer.parseInt(value));
			break;
		case "strength":
			setStrength(Integer.parseInt(value));
			break;
		case "stamina":
			setStamina(Integer.parseInt(value));
			break;
		case "agility":
			setAgility(Integer.parseInt(value));
			break;
		case "dexterity":
			setDexterity(Integer.parseInt(value));
			break;
		case "intelligence":
			setIntelligence(Integer.parseInt(value));
			break;
		case "wisdom":
			setWisdom(Integer.parseInt(value));
			break;
		case "basename":
			Material material = Material.valueOf(value.toUpperCase());
			setBasename(material.name());
			break;
		case "charisma":
			setCharisma(Integer.parseInt(value));
			break;
		case "magicresist":
			setMagicResist(Integer.parseInt(value));
			break;
		case "coldresist":
			setColdResist(Integer.parseInt(value));
			break;
		case "fireresist":
			setFireResist(Integer.parseInt(value));
			break;
		case "diseaseresist":
			setDiseaseResist(Integer.parseInt(value));
			break;
		case "poisonresist":
			setPoisonResist(Integer.parseInt(value));
			break;
		case "temporary":
			setTemporary(Boolean.parseBoolean(value));
			break;
		case "abilityid":
			setAbilityid(Integer.parseInt(value));
			break;
		case "weaponabilityid":
			setWeaponabilityid(Integer.parseInt(value));
			break;
		case "petcontrolrod":
			setPetControlRod(Boolean.parseBoolean(value));
			break;
		case "consumable":
			setConsumable(Boolean.parseBoolean(value));
			break;
		case "quest":
			setQuest(Boolean.parseBoolean(value));
			break;
		case "augmentation":
			setAugmentation(Boolean.parseBoolean(value));
			break;
		case "clearallowedclasses":
			setAllowedClassNames(new ArrayList<String>());
			break;
		case "cleardiscoverer":
			setDiscoverer("");
			break;
		case "minlevel":
			setMinLevel(Integer.parseInt(value));
			break;
		case "augmentationfitsslottype":
			setAugmentationFitsSlotType(AugmentationSlotType.valueOf(value));
			break;
		case "ac":
			setAC(Integer.parseInt(value));
			break;
		case "hp":
			setHp(Integer.parseInt(value));
			break;
		case "mana":
			setMana(Integer.parseInt(value));
			break;
		case "experiencebonus":
			setExperienceBonus(Boolean.parseBoolean(value));
			break;
		case "skillmodtype":
			setSkillModType(SkillType.valueOf(value));
			break;
		case "skillmodvalue":
			setSkillModValue(Integer.parseInt(value));
			break;
		case "skillmodtype2":
			setSkillModType2(SkillType.valueOf(value));
			break;
		case "skillmodvalue2":
			setSkillModValue2(Integer.parseInt(value));
			break;
		case "skillmodtype3":
			setSkillModType3(SkillType.valueOf(value));
			break;
		case "skillmodvalue3":
			setSkillModValue3(Integer.parseInt(value));
			break;
		case "skillmodtype4":
			setSkillModType4(SkillType.valueOf(value));
			break;
		case "skillmodvalue4":
			setSkillModValue4(Integer.parseInt(value));
			break;
		case "fingersitem":
			setFingersItem(Boolean.parseBoolean(value));
			setNeckItem(false);
			setShouldersItem(false);
			setEarsItem(false);
			break;
		case "neckitem":
			setNeckItem(Boolean.parseBoolean(value));
			setFingersItem(false);
			setShouldersItem(false);
			setEarsItem(false);
			break;
		case "shouldersitem":
			setShouldersItem(Boolean.parseBoolean(value));
			setFingersItem(false);
			setNeckItem(false);
			setEarsItem(false);
			break;
		case "earsitem":
			setEarsItem(Boolean.parseBoolean(value));
			setShouldersItem(false);
			setFingersItem(false);
			setNeckItem(false);
			break;
		case "territoryflag":
			setTerritoryFlag(Boolean.parseBoolean(value));
			break;
		case "reagent":
			setReagent(Boolean.parseBoolean(value));
			break;
		default:
			throw new InvalidItemSettingException("Invalid Item setting. Valid Options are: displayname,worth,color,damage,hpregen,mpregen,strength,stamina,agility,dexterity,intelligence,wisdom,charisma,abilityid,consumable,crafting,quest,augmentation,cleardiscoverer,clearallowedclasses,ac,hp,mana,experiencebonus,skillmodtype,skillmodvalue,skillmodtype2,skillmodvalue2,skillmodtype3,skillmodvalue3,skillmodtype4,skillmodvalue4,artifact,spellscroll,territoryflag,reagent");
		}
	}

	@Override
	public short getColor() {
		return color;
	}

	@Override
	public void setColor(short color) {
		this.color = color;
	}

	@Override
	public void consume(Plugin plugin, Player player) throws CoreStateInitException {
		if (this.getAbilityid() < 1)
			return;
		
		useItemOnEntity(plugin, player,player,true);
	}

	@Override
	public int getDiseaseResist() {
		return diseaseResist;
	}

	@Override
	public void setDiseaseResist(int diseaseResist) {
		this.diseaseResist = diseaseResist;
	}

	@Override
	public boolean isTemporary() {
		return isTemporary;
	}

	@Override
	public void setTemporary(boolean isTemporary) {
		this.isTemporary = isTemporary;
	}

	@Override
	public boolean isConsumable() {
		return isConsumable;
	}

	@Override
	public void setConsumable(boolean isConsumable) {
		this.isConsumable = isConsumable;
	}

	@Override
	public int getBaneUndead() {
		return baneUndead;
	}

	@Override
	public void setBaneUndead(int baneUndead) {
		this.baneUndead = baneUndead;
	}

	@Override
	public boolean isPetControlRod() {
		return isPetControlRod;
	}

	@Override
	public void setPetControlRod(boolean isPetControlRod) {
		this.isPetControlRod = isPetControlRod;
	}

	@Override
	public boolean isAugmentation() {
		return isAugmentation;
	}

	@Override
	public void setAugmentation(boolean isAugmentation) {
		this.isAugmentation = isAugmentation;
	}

	@Override
	public boolean isCrafting() {
		try {
			return StateManager.getInstance().getConfigurationManager().isCraftsHasComponent(this.getId());
		} catch (CoreStateInitException e) {
			return false;
		}
	}

	@Override
	public boolean isQuest() {
		return isQuest;
	}
	
	@Override
	public void setQuest(boolean isQuest) {
		this.isQuest = isQuest;
	}

	@Override
	public AugmentationSlotType getAcceptsAugmentationSlotType() {
		return Utils.getItemStackAugSlotType(getBasename(), isAugmentation);
	}

	@Override
	public AugmentationSlotType getAugmentationFitsSlotType() {
		return this.augmentationFitsSlotType;
	}

	@Override
	public void setAugmentationFitsSlotType(AugmentationSlotType augmentationFitsSlotType) {
		this.augmentationFitsSlotType = augmentationFitsSlotType;
	}

	@Override
	public String getDiscoverer() {
		return discoverer;
	}

	@Override
	public void setDiscoverer(String discoverer) {
		this.discoverer = discoverer;
	}

	@Override
	public int getMinLevel() {
		return minLevel;
	}

	@Override
	public void setMinLevel(int minLevel) {
		this.minLevel = minLevel;
	}

	@Override
	public int getAC() {
		return ac;
	}

	@Override
	public void setAC(int ac) {
		this.ac = ac;
	}

	@Override
	public int getHp() {
		return hp;
	}

	@Override
	public void setHp(int hp) {
		this.hp = hp;
	}

	@Override
	public int getMana() {
		return mana;
	}

	@Override
	public void setMana(int mana) {
		this.mana = mana;
	}

	@Override
	public boolean isExperienceBonus() {
		return isExperienceBonus;
	}

	@Override
	public void setExperienceBonus(boolean isExperienceBonus) {
		this.isExperienceBonus = isExperienceBonus;
	}

	@Override
	public SkillType getSkillModType() {
		return skillModType;
	}

	@Override
	public void setSkillModType(SkillType skillModType) {
		this.skillModType = skillModType;
	}

	@Override
	public int getSkillModValue() {
		return skillModValue;
	}

	@Override
	public void setSkillModValue(int skillModValue) {
		this.skillModValue = skillModValue;
	}

	@Override
	public SkillType getSkillModType2() {
		return skillModType2;
	}

	@Override
	public void setSkillModType2(SkillType skillModType2) {
		this.skillModType2 = skillModType2;
	}

	@Override
	public int getSkillModValue2() {
		return skillModValue2;
	}

	@Override
	public void setSkillModValue2(int skillModValue2) {
		this.skillModValue2 = skillModValue2;
	}

	@Override
	public SkillType getSkillModType3() {
		return skillModType3;
	}

	@Override
	public void setSkillModType3(SkillType skillModType3) {
		this.skillModType3 = skillModType3;
	}

	@Override
	public int getSkillModValue3() {
		return skillModValue3;
	}

	@Override
	public void setSkillModValue3(int skillModValue3) {
		this.skillModValue3 = skillModValue3;
	}

	@Override
	public SkillType getSkillModType4() {
		return skillModType4;
	}

	@Override
	public void setSkillModType4(SkillType skillModType4) {
		this.skillModType4 = skillModType4;
	}

	@Override
	public int getSkillModValue4() {
		return skillModValue4;
	}

	public void setSkillModValue4(int skillModValue4) {
		this.skillModValue4 = skillModValue4;
	}

	@Override
	public boolean isOperatorCreated() {
		return operatorCreated;
	}

	@Override
	public void setOperatorCreated(boolean operatorCreated) {
		this.operatorCreated = operatorCreated;
	}

	@Override
	public boolean isArtifact() {
		return artifact;
	}

	@Override
	public void setArtifact(boolean artifact) {
		this.artifact = artifact;
	}

	@Override
	public boolean isArtifactFound() {
		return artifactFound;
	}

	@Override
	public void setArtifactFound(boolean artifactFound) {
		this.artifactFound = artifactFound;
	}

	@Override
	public int getDye() {
		return dye;
	}

	@Override
	public void setDye(int dye) {
		this.dye = dye;
	}

	@Override
	public boolean isFingersItem() {
		return isFingersItem;
	}

	@Override
	public void setFingersItem(boolean isFingersItem) {
		this.isFingersItem = isFingersItem;
	}

	@Override
	public boolean isNeckItem() {
		return isNeckItem;
	}

	@Override
	public void setNeckItem(boolean isNeckItem) {
		this.isNeckItem = isNeckItem;
	}

	@Override
	public boolean isShouldersItem() {
		return isShouldersItem;
	}

	@Override
	public void setShouldersItem(boolean isShouldersItem) {
		this.isShouldersItem = isShouldersItem;
	}

	@Override
	public boolean isEarsItem() {
		return isEarsItem;
	}

	@Override
	public void setEarsItem(boolean isEarsItem) {
		this.isEarsItem = isEarsItem;
	}
	
	@Override
	public boolean isTerritoryFlag() {
		return territoryFlag;
	}

	@Override
	public void setTerritoryFlag(boolean territoryFlag) {
		this.territoryFlag = territoryFlag;
	}

	@Override
	public ItemStack asItemStackForMerchant(int costmultiplier) {
		return ItemStackAdapter.Adapt(this, costmultiplier);
	}

	@Override
	public boolean isReagent() {
		return reagent;
	}

	@Override
	public void setReagent(boolean reagent) {
		this.reagent = reagent;
	}

	@Override
	public boolean isWeaponOrBowOrShield() {
		try
		{
			return StateManager.getInstance().getConfigurationManager().HandMaterials.contains(this.getBasename().toUpperCase());
		} catch (CoreStateInitException e)
		{
			return false;
		}
	}

	@Override
	public boolean isArmour() {
		try
		{
			return StateManager.getInstance().getConfigurationManager().ArmourMaterials.contains(this.getBasename().toUpperCase());
		} catch (CoreStateInitException e)
		{
			return false;
		}
	}

	@Override
	public boolean isJewelry() {
		return (this.isFingersItem() || this.isNeckItem() || this.isShouldersItem() || this.isEarsItem());
	}
}
