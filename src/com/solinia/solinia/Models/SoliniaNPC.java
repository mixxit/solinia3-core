package com.solinia.solinia.Models;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import com.solinia.solinia.Adapters.SoliniaPlayerAdapter;
import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Exceptions.InvalidNPCEventSettingException;
import com.solinia.solinia.Exceptions.InvalidNpcSettingException;
import com.solinia.solinia.Interfaces.IPersistable;
import com.solinia.solinia.Interfaces.ISoliniaClass;
import com.solinia.solinia.Interfaces.ISoliniaFaction;
import com.solinia.solinia.Interfaces.ISoliniaItem;
import com.solinia.solinia.Interfaces.ISoliniaLootDrop;
import com.solinia.solinia.Interfaces.ISoliniaLootTable;
import com.solinia.solinia.Interfaces.ISoliniaLootTableEntry;
import com.solinia.solinia.Interfaces.ISoliniaNPC;
import com.solinia.solinia.Interfaces.ISoliniaNPCEventHandler;
import com.solinia.solinia.Interfaces.ISoliniaNPCMerchant;
import com.solinia.solinia.Interfaces.ISoliniaPlayer;
import com.solinia.solinia.Interfaces.ISoliniaRace;
import com.solinia.solinia.Interfaces.ISoliniaSpawnGroup;
import com.solinia.solinia.Managers.StateManager;
import com.solinia.solinia.Utils.EntityUtils;
import com.solinia.solinia.Utils.ItemStackUtils;
import com.solinia.solinia.Utils.PlayerUtils;
import com.solinia.solinia.Utils.QuestUtils;
import com.solinia.solinia.Utils.Utils;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.minecraft.server.v1_14_R1.Tuple;

public class SoliniaNPC implements ISoliniaNPC,IPersistable {
	private int id;
	private UUID primaryUUID = UUID.randomUUID();
	private UUID secondaryUUID = UUID.randomUUID();
	private String name;
	private String mctype = "SKELETON"; // do not use zombie, it ignores assist rules
	private int level = 1;
	private int factionid;
	private boolean usedisguise = false;
	private String disguisetype;
	private String headitem;
	private String chestitem;
	private String legsitem;
	private String feetitem;
	private String handitem;
	private String offhanditem;
	private boolean boss = false;
	private boolean heroic = false;
	private boolean raidboss = false;
	private boolean raidheroic = false;
	private boolean burning = false;
	private boolean invisible = false;
	private boolean customhead = false;
	private String customheaddata;
	private int merchantid;
	private boolean upsidedown = false;
	private int loottableid;
	private int raceid;
	private int classid;
	private boolean isRandomSpawn = false;
	private String killTriggerText;
	private String randomchatTriggerText;
	private boolean isGuard = false;
	private boolean isRoamer = false;
	private boolean isPet = false;
	private boolean isMounted = false;
	private boolean isUndead = false;
	private boolean isAnimal = false;
	private boolean isPlant = false;
	private List<ISoliniaNPCEventHandler> eventHandlers = new ArrayList<ISoliniaNPCEventHandler>();
	private String deathGrantsTitle = "";
	private boolean isSummoner = false;
	private int avoidanceRating = 0;
	private int accuracyRating = 0;
	private int ac = 0;
	private boolean speaksAllLanguages = false;
	private boolean isPetControllable = true;
	private int forcedMaxHp = 0;
	private int npcSpellList = 0;
	private int chanceToRespawnOnDeath = 0;
	private boolean teleportAttack = false;
	private String teleportAttackLocation = "";
	private long timefrom = 0L;
	private long timeto = Utils.MAXDAYTICK;
	private boolean isSocial = true;
	private boolean isBanker = false;

	@Override
	public int getId() {
		return id;
	}

	@Override
	public void setId(int id) {
		this.id = id;
	}
	
	@Override
	public UUID getPrimaryUUID() {
		// TODO Auto-generated method stub
		return this.primaryUUID;
	}
	@Override
	public void setPrimaryUUID(UUID uuid) {
		// TODO Auto-generated method stub
		this.primaryUUID = uuid;
	}
	@Override
	public UUID getSecondaryUUID() {
		// TODO Auto-generated method stub
		return this.secondaryUUID;
	}
	@Override
	public void setSecondaryUUID(UUID uuid) {
		// TODO Auto-generated method stub
		this.secondaryUUID = uuid;
	}
	

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String getMctype() {
		return mctype;
	}

	@Override
	public void setMctype(String mctype) {
		this.mctype = mctype;
	}

	@Override
	public int getLevel() {
		return level;
	}

	@Override
	public void setLevel(int level) {
		this.level = level;
	}

	@Override
	public int getFactionid() {
		return factionid;
	}

	@Override
	public void setFactionid(int factionid) {
		this.factionid = factionid;
	}

	@Override
	public boolean isUsedisguise() {
		return usedisguise;
	}

	@Override
	public void setUsedisguise(boolean usedisguise) {
		this.usedisguise = usedisguise;
	}

	@Override
	public String getDisguisetype() {
		return disguisetype;
	}

	@Override
	public void setDisguisetype(String disguisetype) {
		this.disguisetype = disguisetype;
	}

	@Override
	public String getHeaditem() {
		return headitem;
	}

	@Override
	public void setHeaditem(String headitem) {
		this.headitem = headitem;
	}

	@Override
	public String getChestitem() {
		return chestitem;
	}

	@Override
	public void setChestitem(String chestitem) {
		this.chestitem = chestitem;
	}

	@Override
	public String getLegsitem() {
		return legsitem;
	}

	@Override
	public void setLegsitem(String legsitem) {
		this.legsitem = legsitem;
	}

	@Override
	public String getFeetitem() {
		return feetitem;
	}

	@Override
	public void setFeetitem(String feetitem) {
		this.feetitem = feetitem;
	}

	@Override
	public String getHanditem() {
		return handitem;
	}

	@Override
	public void setHanditem(String handitem) {
		this.handitem = handitem;
	}

	@Override
	public String getOffhanditem() {
		return offhanditem;
	}

	@Override
	public void setOffhanditem(String offhanditem) {
		this.offhanditem = offhanditem;
	}

	@Override
	public boolean isBoss() {
		return boss;
	}

	@Override
	public void setBoss(boolean boss) {
		this.boss = boss;
	}

	@Override
	public boolean isBurning() {
		return burning;
	}

	@Override
	public void setBurning(boolean burning) {
		this.burning = burning;
	}

	@Override
	public boolean isInvisible() {
		return invisible;
	}

	@Override
	public void setInvisible(boolean invisible) {
		this.invisible = invisible;
	}

	@Override
	public boolean isCustomhead() {
		return customhead;
	}

	@Override
	public void setCustomhead(boolean customhead) {
		this.customhead = customhead;
	}

	@Override
	public String getCustomheaddata() {
		return customheaddata;
	}

	@Override
	public void setCustomheaddata(String customheaddata) {
		this.customheaddata = customheaddata;
	}

	@Override
	public int getMerchantid() {
		return merchantid;
	}

	@Override
	public void setMerchantid(int merchantid) {
		this.merchantid = merchantid;
	}

	@Override
	public boolean isUpsidedown() {
		return upsidedown;
	}

	@Override
	public void setUpsidedown(boolean upsidedown) {
		this.upsidedown = upsidedown;
	}

	@Override
	public int getLoottableid() {
		return loottableid;
	}

	@Override
	public void setLoottableid(int loottableid) {
		this.loottableid = loottableid;
	}

	@Override
	public int getRaceid() {
		return raceid;
	}

	@Override
	public void setRaceid(int raceid) {
		this.raceid = raceid;
	}

	@Override
	public int getClassid() {
		return classid;
	}

	@Override
	public void setClassid(int classid) {
		this.classid = classid;
	}

	@Override
	public void sendMerchantItemListToPlayer(Player player, int pageno) {
		if (this.getMerchantid() < 1)
			return;
		
		try {
			ISoliniaNPCMerchant soliniaNpcMerchant = StateManager.getInstance().getConfigurationManager().getNPCMerchant(getMerchantid());
			
			
			Inventory merchantInventory = StateManager.getInstance().getEntityManager()
					.getNPCMerchantInventory(player.getUniqueId(), soliniaNpcMerchant, pageno);
			if (merchantInventory != null)
				player.openInventory(merchantInventory);
		} catch (CoreStateInitException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void sendNpcSettingsToSender(CommandSender sender) throws CoreStateInitException {
		sender.sendMessage(ChatColor.RED + "NPC Settings for " + ChatColor.GOLD + getName() + ChatColor.RESET);
		sender.sendMessage("----------------------------");
		sender.sendMessage("- id: " + ChatColor.GOLD + getId() + ChatColor.RESET + " " + "name: " + ChatColor.GOLD
				+ getName() + ChatColor.RESET);
		if (getRaceid() != 0) {
			sender.sendMessage("- raceid: " + ChatColor.GOLD + getRaceid() + " ("
					+ StateManager.getInstance().getConfigurationManager().getRace(getRaceid()).getName() + ")"
					+ ChatColor.RESET);
		} else {
			sender.sendMessage("- raceid: " + ChatColor.GOLD + getRaceid() + " (No Race)" + ChatColor.RESET);
		}
		if (getClassid() != 0) {
			sender.sendMessage("- classid: " + ChatColor.GOLD + getClassid() + " ("
					+ StateManager.getInstance().getConfigurationManager().getClassObj(getClassid()).getName() + ")"
					+ ChatColor.RESET);
		} else {
			sender.sendMessage("- classid: " + ChatColor.GOLD + getClassid() + " (No Class)" + ChatColor.RESET);
		}
		sender.sendMessage(ChatColor.RED + "STATS" + ChatColor.RESET);
		sender.sendMessage("- level: " + ChatColor.GOLD + getLevel() + ChatColor.RESET + " " + "ac: " + ChatColor.GOLD
				+ getAC() + ChatColor.RESET + " " + "forcedmaxhp: " + ChatColor.GOLD + getForcedMaxHp());
		sender.sendMessage("- avoidancerating: " + ChatColor.GOLD + getAvoidanceRating() + ChatColor.RESET + " "
				+ "accuracyrating: " + ChatColor.GOLD + getAccuracyRating() + ChatColor.RESET);
		sender.sendMessage("----------------------------");
		sender.sendMessage(ChatColor.RED + "SPAWNING" + ChatColor.RESET);
		sender.sendMessage("- timefrom: " + ChatColor.GOLD + getTimefrom() + ChatColor.RESET +  "- timeto: " + ChatColor.GOLD + getTimeto() + ChatColor.RESET + " - randomspawn: "
				+ ChatColor.GOLD + isRandomSpawn() + ChatColor.RESET + " chancetorespawnondeath: " + ChatColor.GOLD
				+ getChanceToRespawnOnDeath() + ChatColor.RESET);
		sender.sendMessage("----------------------------");
		sender.sendMessage(ChatColor.RED + "AI" + ChatColor.RESET);
		sender.sendMessage("- undead: " + ChatColor.GOLD + isUndead() + ChatColor.RESET + " " + "plant: "
				+ ChatColor.GOLD + isPlant() + ChatColor.RESET + " " + "animal: " + ChatColor.GOLD + isAnimal()
				+ ChatColor.RESET);
		sender.sendMessage("- pet: " + ChatColor.GOLD + isCorePet() + ChatColor.RESET + " " + "petcontrollable: "
				+ ChatColor.GOLD + isPetControllable() + ChatColor.RESET + " " + "social: "
						+ ChatColor.GOLD + isSocial());
		sender.sendMessage("- summoner: " + ChatColor.GOLD + isSummoner() + ChatColor.RESET + " - guard: "
				+ ChatColor.GOLD + isGuard() + ChatColor.RESET + " " + "roamer: " + ChatColor.GOLD + isRoamer()
				+ ChatColor.RESET);
		sender.sendMessage("- heroic: " + ChatColor.GOLD + isHeroic() + " " + ChatColor.RESET + "- boss: " + ChatColor.GOLD + isBoss() + ChatColor.RESET + " - raidheroic: " + ChatColor.GOLD + isRaidheroic() + " " + ChatColor.RESET +  "raidboss: " + ChatColor.GOLD
				+ isRaidboss());
		sender.sendMessage("- speaksalllanguages: " + ChatColor.GOLD + isSpeaksAllLanguages() + ChatColor.RESET + " banker: " + ChatColor.GOLD + isBanker() + ChatColor.RESET);
		sender.sendMessage("- randomchattriggertext: " + ChatColor.GOLD + getRandomchatTriggerText());
		sender.sendMessage("- deathgrantstitle: " + ChatColor.GOLD + getDeathGrantsTitle() + ChatColor.RESET);
		sender.sendMessage("- killtriggertext: " + ChatColor.GOLD + getKillTriggerText());
		sender.sendMessage("- teleportattack: " + ChatColor.GOLD + isTeleportAttack() + " " + ChatColor.RESET
				+ " teleportattacklocation: " + ChatColor.GOLD + getTeleportAttackLocation() + ChatColor.RESET);
		if (getFactionid() != 0) {
			sender.sendMessage("- factionid: " + ChatColor.GOLD + getFactionid() + " ("
					+ StateManager.getInstance().getConfigurationManager().getFaction(getFactionid()).getName() + ")"
					+ ChatColor.RESET);
		} else {
			sender.sendMessage("- factionid: " + ChatColor.GOLD + getFactionid() + " (No Faction)" + ChatColor.RESET);
		}
		if (getMerchantid() != 0) {
			sender.sendMessage("- merchantid: " + ChatColor.GOLD + getMerchantid() + " ("
					+ StateManager.getInstance().getConfigurationManager().getNPCMerchant(getMerchantid()).getName()
					+ ")" + ChatColor.RESET);
		} else {
			sender.sendMessage(
					"- merchantid: " + ChatColor.GOLD + getMerchantid() + " (No Merchant Table)" + ChatColor.RESET);
		}
		sender.sendMessage("----------------------------");
		sender.sendMessage(ChatColor.RED + "APPEARANCE" + ChatColor.RESET);
		sender.sendMessage("- mctype: " + ChatColor.GOLD + getMctype() + ChatColor.RESET + " - usedisguise: "
				+ ChatColor.GOLD + isUsedisguise() + ChatColor.RESET + " " + "disguisetype: " + ChatColor.GOLD
				+ getDisguisetype() + ChatColor.RESET);
		TextComponent tc = new TextComponent("- customhead: " + ChatColor.GOLD + isCustomhead() + ChatColor.RESET + " - customheaddata: " + ChatColor.GOLD + "<hover to see>" + ChatColor.RESET);
		if (this.customheaddata != null && this.customheaddata.length() > 0)
			tc.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(this.getCustomheaddata()).create()));
		sender.spigot().sendMessage(tc);
		sender.sendMessage("- upsidedown: " + ChatColor.GOLD + isUpsidedown() + ChatColor.RESET + " " + "burning: "
				+ ChatColor.GOLD + isBurning() + ChatColor.RESET + " " + "invisible: " + ChatColor.GOLD + isInvisible()
				+ ChatColor.RESET);
		sender.sendMessage(ChatColor.RED + "EQUIPMENT" + ChatColor.RESET);
		if (getLoottableid() != 0) {
			sender.sendMessage("- loottableid: " + ChatColor.GOLD + getLoottableid() + " ("
					+ StateManager.getInstance().getConfigurationManager().getLootTable(getLoottableid()).getName()
					+ ")" + ChatColor.RESET);
		} else {
			sender.sendMessage(
					"- loottableid: " + ChatColor.GOLD + getLoottableid() + " (No Loot Table)" + ChatColor.RESET);
		}
		sender.sendMessage("- handitem: " + ChatColor.GOLD + getHanditem() + ChatColor.RESET + " " + "offhanditem: "
				+ ChatColor.GOLD + getOffhanditem() + ChatColor.RESET);
		sender.sendMessage("- headitem: " + ChatColor.GOLD + getHeaditem() + ChatColor.RESET + " " + "chestitem: "
				+ ChatColor.GOLD + getChestitem() + ChatColor.RESET);
		sender.sendMessage("- legsitem: " + ChatColor.GOLD + getLegsitem() + ChatColor.RESET + " " + "feetitem: "
				+ ChatColor.GOLD + getFeetitem() + ChatColor.RESET);
		sender.sendMessage(ChatColor.RED + "MISC" + ChatColor.RESET);

		if (getNpcSpellList() > 0) {
			NPCSpellList npcSpellList = StateManager.getInstance().getConfigurationManager()
					.getNPCSpellList(getNpcSpellList());
			sender.sendMessage("- npcspelllist: " + ChatColor.GOLD + npcSpellList.getName());
		} else {
			sender.sendMessage(
					"- npcspelllist: " + ChatColor.GOLD + getNpcSpellList() + " (Defaults to class spell list)");
		}
		sender.sendMessage("----------------------------");
		if (getLoottableid() != 0) {
			sender.sendMessage(ChatColor.RED + "LOOTTABLE: " + ChatColor.RESET + "[" + getLoottableid() + "] - " + "("
					+ StateManager.getInstance().getConfigurationManager().getLootTable(getLoottableid()).getName()
					+ ")");
			ISoliniaLootTable loottable = StateManager.getInstance().getConfigurationManager()
					.getLootTable(getLoottableid());
			for (ISoliniaLootTableEntry le : StateManager.getInstance().getConfigurationManager()
					.getLootTable(loottable.getId()).getEntries()) {
				ISoliniaLootDrop ld = StateManager.getInstance().getConfigurationManager()
						.getLootDrop(le.getLootdropid());
				sender.sendMessage("- LOOTDROP: " + ChatColor.GOLD + ld.getName().toUpperCase() + ChatColor.RESET + "["
						+ ld.getId() + "]:");
			}
		}

	}

	@Override
	public boolean editSetting(String setting, String value)
			throws InvalidNpcSettingException, NumberFormatException, CoreStateInitException, java.io.IOException {

		boolean requiresreload = true;
		
		switch (setting.toLowerCase()) {
		case "name":
			if (value.equals(""))
				throw new InvalidNpcSettingException("Name is empty");

			if (value.length() > 25)
				throw new InvalidNpcSettingException("Name is longer than 25 characters");
			setName(value);
			break;
		case "mctype":
			if (!value.toLowerCase().equals("skeleton"))
				throw new InvalidNpcSettingException("mctype can only be SKELETON");

			setMctype(value.toUpperCase());
			break;
		case "teleportattack":
			setTeleportAttack(Boolean.parseBoolean(value));
			break;
		case "teleportattacklocation":
			try {
				String[] zonedata = value.split(",");
				// Dissasemble the value to ensure it is correct
				String world = zonedata[0];
				double x = Double.parseDouble(zonedata[1]);
				double y = Double.parseDouble(zonedata[2]);
				double z = Double.parseDouble(zonedata[3]);

				setTeleportAttackLocation(world + "," + x + "," + y + "," + z);
				break;
			} catch (Exception e) {
				throw new InvalidNpcSettingException("Teleport attack location value must be in format: world,x,y,z");
			}
		case "level":
			setLevel(Integer.parseInt(value));
			break;
		case "chancetorespawnondeath":
			setChanceToRespawnOnDeath(Integer.parseInt(value));
			requiresreload = false;
			break;
		case "factionid":
			if (Integer.parseInt(value) == 0) {
				setFactionid(Integer.parseInt(value));
				break;
			}

			ISoliniaFaction faction = StateManager.getInstance().getConfigurationManager()
					.getFaction(Integer.parseInt(value));
			if (faction == null)
				throw new InvalidNpcSettingException("Faction ID does not exist");
			setFactionid(Integer.parseInt(value));
			break;
		case "mounted":
			setMounted(Boolean.parseBoolean(value));
			break;
		case "social":
			setSocial(Boolean.parseBoolean(value));
			requiresreload = false;
			break;
		case "usedisguise":
			setUsedisguise(Boolean.parseBoolean(value));
			break;
		case "timefrom":
			long time = Long.parseLong(value);
			if (time < 0L || time > Utils.MAXDAYTICK)
				throw new InvalidNpcSettingException("This is not a valid time range, it shoudl be between 0 and " + Utils.MAXDAYTICK);
			setTimefrom(time);
			requiresreload = false;
			break;
		case "timeto":
			long time2 = Long.parseLong(value);
			if (time2 < 0L || time2 > Utils.MAXDAYTICK)
				throw new InvalidNpcSettingException("This is not a valid time range, it shoudl be between 0 and " + Utils.MAXDAYTICK);
			setTimeto(time2);
			requiresreload = false;
			break;
		case "disguisetype":
			setDisguisetype(value);
			break;
		case "headitem":
			try
			{
				Material.valueOf(value.toUpperCase());
			} catch (IllegalArgumentException e)
			{
				throw new InvalidNpcSettingException("This is not a valid minecraft item type: " + value.toUpperCase() + " See: https://hub.spigotmc.org/javadocs/spigot/org/bukkit/Material.html");
			}
			setHeaditem(value.toUpperCase());
			break;
		case "chestitem":
			try
			{
				Material.valueOf(value.toUpperCase());
			} catch (IllegalArgumentException e)
			{
				throw new InvalidNpcSettingException("This is not a valid minecraft item type: " + value.toUpperCase() + " See: https://hub.spigotmc.org/javadocs/spigot/org/bukkit/Material.html");
			}
			setChestitem(value.toUpperCase());
			break;
		case "legsitem":
			try
			{
				Material.valueOf(value.toUpperCase());
			} catch (IllegalArgumentException e)
			{
				throw new InvalidNpcSettingException("This is not a valid minecraft item type: " + value.toUpperCase() + " See: https://hub.spigotmc.org/javadocs/spigot/org/bukkit/Material.html");
			}
			setLegsitem(value.toUpperCase());
			break;
		case "feetitem":
			try
			{
				Material.valueOf(value.toUpperCase());
			} catch (IllegalArgumentException e)
			{
				throw new InvalidNpcSettingException("This is not a valid minecraft item type: " + value.toUpperCase() + " See: https://hub.spigotmc.org/javadocs/spigot/org/bukkit/Material.html");
			}
			setFeetitem(value.toUpperCase());
			break;
		case "handitem":
			try
			{
				Material.valueOf(value.toUpperCase());
			} catch (IllegalArgumentException e)
			{
				throw new InvalidNpcSettingException("This is not a valid minecraft item type: " + value.toUpperCase() + " See: https://hub.spigotmc.org/javadocs/spigot/org/bukkit/Material.html");
			}
			setHanditem(value.toUpperCase());
			break;
		case "offhanditem":
			try
			{
				Material.valueOf(value.toUpperCase());
			} catch (IllegalArgumentException e)
			{
				throw new InvalidNpcSettingException("This is not a valid minecraft item type: " + value.toUpperCase() + " See: https://hub.spigotmc.org/javadocs/spigot/org/bukkit/Material.html");
			}
			setOffhanditem(value.toUpperCase());
			break;
		case "clearitems":
			setHeaditem(null);
			setChestitem(null);
			setLegsitem(null);
			setFeetitem(null);
			setHanditem(null);
			setOffhanditem(null);
		case "boss":
			setBoss(Boolean.parseBoolean(value));
			break;
		case "heroic":
			setHeroic(Boolean.parseBoolean(value));
			break;
		case "raidboss":
			setRaidboss(Boolean.parseBoolean(value));
			break;
		case "raidheroic":
			setRaidheroic(Boolean.parseBoolean(value));
			break;
		case "burning":
			setBurning(Boolean.parseBoolean(value));
			break;
		case "invisible":
			setInvisible(Boolean.parseBoolean(value));
			break;
		case "customhead":
			setCustomhead(Boolean.parseBoolean(value));
			break;
		case "customheaddata":
			// fetches custom head texture by a player name
			setCustomheaddata(Utils.getTextureFromName(value));
			break;
		case "customheaddatafromnpc":
			int npcid = Integer.parseInt(value);
			ISoliniaNPC npc = StateManager.getInstance().getConfigurationManager().getNPC(npcid);
			if (npc == null)
				throw new InvalidNpcSettingException("NPCID does not exist");
			// fetches custom head texture by existing npc
			setCustomheaddata(npc.getCustomheaddata());
			break;
		case "deathgrantstitle":
			// fetches custom head texture by a player name
			setDeathGrantsTitle(value);
			break;
		case "merchantid":
			if (Integer.parseInt(value) == 0) {
				setMerchantid(Integer.parseInt(value));
				break;
			}

			if (StateManager.getInstance().getConfigurationManager().getNPCMerchant(Integer.parseInt(value)) == null)
				throw new InvalidNpcSettingException("MerchantID does not exist");
			setMerchantid(Integer.parseInt(value));
			requiresreload = false;
			break;
		case "upsidedown":
			setUpsidedown(Boolean.parseBoolean(value));
			break;
		case "npcspelllist":
			setNpcSpellList(Integer.parseInt(value));
			requiresreload = false;
			break;
		case "loottableid":
			if (Integer.parseInt(value) == 0) {
				setLoottableid(0);
				break;
			}

			ISoliniaLootTable loottable = StateManager.getInstance().getConfigurationManager()
					.getLootTable(Integer.parseInt(value));
			if (loottable == null)
				throw new InvalidNpcSettingException("Loottable ID does not exist");

			setLoottableid(Integer.parseInt(value));
			requiresreload = false;
			break;
		case "raceid":
			setRaceid(Integer.parseInt(value));
			break;
		case "classid":
			setClassid(Integer.parseInt(value));
			break;
		case "randomspawn":
			setRandomSpawn(Boolean.parseBoolean(value));
			break;
		case "killtriggertext":
			setKillTriggerText(value);
			requiresreload = false;
			break;
		case "ac":
			setAC(Integer.parseInt(value));
			break;
		case "clearrandomchattriggertext":
			setRandomchatTriggerText("");
			requiresreload = false;
			break;
		case "randomchattriggertext":
			setRandomchatTriggerText(value);
			requiresreload = false;
			break;
		case "guard":
			setGuard(Boolean.parseBoolean(value));
			break;
		case "pet":
			setCorePet(Boolean.parseBoolean(value));
			break;
		case "banker":
			setBanker(Boolean.parseBoolean(value));
			break;
		case "roamer":
			setRoamer(Boolean.parseBoolean(value));
			break;
		case "undead":
			setUndead(Boolean.parseBoolean(value));
			requiresreload = false;
			break;
		case "plant":
			setPlant(Boolean.parseBoolean(value));
			requiresreload = false;
			break;
		case "animal":
			setAnimal(Boolean.parseBoolean(value));
			requiresreload = false;
			break;
		case "summoner":
			setSummoner(Boolean.parseBoolean(value));
			requiresreload = false;
			break;
		case "disablespawners":
			disableAllSpawners(Boolean.parseBoolean(value));
			break;
		case "accuracyrating":
			setAccuracyRating(Integer.parseInt(value));
			requiresreload = false;
			break;
		case "avoidancerating":
			setAvoidanceRating(Integer.parseInt(value));
			requiresreload = false;
			break;
		case "speaksalllanguages":
			setSpeaksAllLanguages(Boolean.parseBoolean(value));
			requiresreload = false;
			break;
		case "forcedmaxhp":
			setForcedMaxHp(Integer.parseInt(value));
			break;
		case "petcontrollable":
			setPetControllable(Boolean.parseBoolean(value));
			break;
		default:
			throw new InvalidNpcSettingException(
					"Invalid NPC setting. Valid Options are: name,mctype,health,damage,factionid,usedisguise,disguisetype,headitem,chestitem,legsitem,feetitem,handitem,offhanditem,boss,burning,invisible,customhead,customheaddata,merchantid,upsidedown,loottableid,randomspawn,killtriggertext,randomchattriggertext,guard,roamer,undead,customheaddatafromnpc,summoner,disablespawners,animal,speaksalllanguages,mounted,clearrandomchattriggertext");
		}
		
		return requiresreload;
	}

	@Override
	public void disableAllSpawners(boolean parseBoolean) {
		try {
			for (ISoliniaSpawnGroup group : StateManager.getInstance().getConfigurationManager().getSpawnGroups()) {
				if (group.getNpcid() == this.getId()) {
					System.out.println("Set Spawner Disabled Status: " + group.getId() + ":" + group.getName() + " - "
							+ parseBoolean);
					group.setDisabled(parseBoolean);
					StateManager.getInstance().getEntityManager().getNPCEntityProvider().removeSpawnGroup(group);
				}
			}
		} catch (CoreStateInitException e) {

		}
	}

	@Override
	public boolean isRandomSpawn() {
		return isRandomSpawn;
	}

	@Override
	public void setRandomSpawn(boolean isRandomSpawn) {
		this.isRandomSpawn = isRandomSpawn;
	}

	@Override
	public String getKillTriggerText() {
		return killTriggerText;
	}

	@Override
	public void setKillTriggerText(String killTriggerText) {
		this.killTriggerText = killTriggerText;
	}

	@Override
	public String getRandomchatTriggerText() {
		return randomchatTriggerText;
	}

	@Override
	public void setRandomchatTriggerText(String randomchatTriggerText) {
		this.randomchatTriggerText = randomchatTriggerText;
	}

	@Override
	public boolean isGuard() {
		return isGuard;
	}

	@Override
	public void setGuard(boolean isGuard) {
		this.isGuard = isGuard;
	}

	@Override
	public boolean isRoamer() {
		return isRoamer;
	}

	@Override
	public void setRoamer(boolean isRoamer) {
		this.isRoamer = isRoamer;
	}

	@Override
	public ISoliniaClass getClassObj() {
		if (getClassid() < 1)
			return null;

		try {
			return StateManager.getInstance().getConfigurationManager().getClassObj(getClassid());
		} catch (CoreStateInitException e) {
			return null;
		}
	}

	@Override
	public ISoliniaRace getRace() {
		if (getRaceid() < 1)
			return null;

		try {
			return StateManager.getInstance().getConfigurationManager().getRace(getRaceid());
		} catch (CoreStateInitException e) {
			return null;
		}
	}
	
	@Override
	public boolean isCorePet() {
		return isPet;
	}

	@Override
	public void setCorePet(boolean isCorePet) {
		this.isPet = isCorePet;
	}

	@Override
	public void processInteractionEvent(SoliniaLivingEntity solentity, LivingEntity triggerentity, InteractionType type,
			String data) {
		switch (type) {
		case CHAT:
			processChatInteractionEvent(solentity, triggerentity, data);
		default:
			return;
		}
	}

	@Override
	public void processChatInteractionEvent(SoliniaLivingEntity solentity, LivingEntity triggerentity, String data) {
		String words[] = data.split(" ");

		// Merchant special commands
		if (words.length > 0) {
			// Check player has sufficient faction
			if (triggerentity instanceof Player)
				if (solentity.getNpcid() > 0) {
					try {
						ISoliniaNPC npc = StateManager.getInstance().getConfigurationManager()
								.getNPC(solentity.getNpcid());
						if (npc.getFactionid() > 0) {
							ISoliniaPlayer solPlayer = SoliniaPlayerAdapter.Adapt((Player) triggerentity);
							PlayerFactionEntry factionEntry = solPlayer.getFactionEntry(npc.getFactionid());
							if (factionEntry != null) {
								switch (Utils.getFactionStandingType(factionEntry.getFactionId(),
										factionEntry.getValueWithEffectsOnEntity(solentity.getBukkitLivingEntity(),
												solPlayer.getBukkitPlayer()))) {
								case FACTION_THREATENLY:
								case FACTION_SCOWLS:
									solentity.emote(npc.getName() + " scowls angrily at " + solPlayer.getFullName());
									return;
								default:
									break;
								}
							}
						}
					} catch (CoreStateInitException e) {

					}
				}

			switch (words[0].toUpperCase()) {
			// we will move this to the bottom
			//case "HAIL":
			//	listenToHail(solentity,triggerentity);
			//	break;
			case "BANK":
				if (triggerentity instanceof Player)
					if (isBanker()) {
						ISoliniaPlayer solPlayer;
						try {
							solPlayer = SoliniaPlayerAdapter.Adapt((Player) triggerentity);
							solPlayer.openBank();
						} catch (CoreStateInitException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
					}
				return;
			case "SHOP":
				if (triggerentity instanceof Player)
					if (getMerchantid() > 0) {
						if (words.length == 1) {
							sendMerchantItemListToPlayer((Player) triggerentity, 1);
							return;
						}

						int page = 1;
						try {
							page = Integer.parseInt(words[1]);
						} catch (Exception e) {

						}

						if (page < 1)
							page = 1;

						sendMerchantItemListToPlayer((Player) triggerentity, page);
					}
				return;
			/*
			 * case "SHOWSTATUS": if (triggerentity instanceof Player) { if
			 * (((Player)triggerentity).isOp()) { Player player = (Player)triggerentity;
			 * 
			 * if (Utils.isSoliniaMob(solentity.getBukkitLivingEntity())) {
			 * player.sendMessage("UUID:" +
			 * solentity.getBukkitLivingEntity().getUniqueId());
			 * 
			 * MythicEntitySoliniaMob mob =
			 * Utils.GetSoliniaMob(solentity.getBukkitLivingEntity()); if (mob != null) {
			 * player.sendMessage("MeleeAttackPercent: " + mob.getMeleeAttackPercent()); } }
			 * } } return;
			 */
			default:
				break;
			}
		}

		// Normal text matching
		for (ISoliniaNPCEventHandler handler : getEventHandlers()) {
			if (!handler.getInteractiontype().equals(InteractionType.CHAT))
				continue;

			if (!data.toUpperCase().contains(handler.getTriggerdata().toUpperCase()))
				continue;

			if (handler.getChatresponse() != null && !handler.getChatresponse().equals("")) {
				if ((triggerentity instanceof Player)) {
					if (!handler.playerMeetsRequirements((Player) triggerentity)) {
						((Player) triggerentity).sendMessage(ChatColor.GRAY
								+ "[Hint] You do not meet the requirements to for a response. Either you are missing a quest step, have already completed this step or missing something like the mod");
						return;
					}
				}

				// stop players spamming hail to learn a language
				String response = handler.getChatresponse();
				if (words.length > 0) {
					if (words[0].toLowerCase().equals("hail")) {
						if ((triggerentity instanceof Player)) {
							if (handler.getResponseType().equals("SAY"))
								solentity.sayto((Player) triggerentity, replaceChatWordsWithHints(response), true);
							if (handler.getResponseType().equals("EMOTE"))
								solentity.emote(replaceChatWordsWithHints(response));
						} else {
							if (handler.getResponseType().equals("SAY"))
								solentity.say(replaceChatWordsWithHints(response), triggerentity, true);
							if (handler.getResponseType().equals("EMOTE"))
								solentity.emote(replaceChatWordsWithHints(response));
						}
					} else {
						if ((triggerentity instanceof Player)) {
							if (handler.getResponseType().equals("SAY"))
								solentity.sayto((Player) triggerentity, replaceChatWordsWithHints(response), true);
							if (handler.getResponseType().equals("EMOTE"))
								solentity.emote(replaceChatWordsWithHints(response));
						} else {
							solentity.say(replaceChatWordsWithHints(response), triggerentity, true);
						}
					}
				} else {
					if ((triggerentity instanceof Player)) {
						if (handler.getResponseType().equals("SAY"))
							solentity.sayto((Player) triggerentity, replaceChatWordsWithHints(response), true);
						if (handler.getResponseType().equals("EMOTE"))
							solentity.emote(replaceChatWordsWithHints(response));
					} else {
						if (handler.getResponseType().equals("SAY"))
							solentity.say(replaceChatWordsWithHints(response), triggerentity, true);
						if (handler.getResponseType().equals("EMOTE"))
							solentity.emote(replaceChatWordsWithHints(response));
					}
				}

				if (triggerentity instanceof Player)
					handler.awardPlayer((Player) triggerentity, solentity.getBukkitLivingEntity());

				if (handler.getNpcId() > 0 && handler.getInteractiontype().equals(InteractionType.ITEM)) {
					try {
						ISoliniaNPC npc = StateManager.getInstance().getConfigurationManager()
								.getNPC(handler.getNpcId());
						if (npc != null) {
							// Spawn Npc
							npc.Spawn(triggerentity.getLocation(), 1);
						}

					} catch (CoreStateInitException e) {
					}
				}

				if (handler.getTeleportResponse() != null && !handler.getTeleportResponse().equals("")) {
					if (triggerentity instanceof Player) {
						String[] zonedata = handler.getTeleportResponse().split(",");
						// Dissasemble the value to ensure it is correct
						String world = zonedata[0];
						double x = Double.parseDouble(zonedata[1]);
						double y = Double.parseDouble(zonedata[2]);
						double z = Double.parseDouble(zonedata[3]);
						Location loc = new Location(Bukkit.getWorld(world), x, y, z);
						EntityUtils.teleportSafely(((Player) triggerentity),loc);
					}

				}

				if (handler.isAwardsBind() == true) {
					if (triggerentity instanceof Player) {
						Player player = (Player) triggerentity;
						try {
							ISoliniaPlayer solPlayer = SoliniaPlayerAdapter.Adapt(player);
							solPlayer.setBindPoint(
									player.getLocation().getWorld().getName() + "," + player.getLocation().getX() + ","
											+ player.getLocation().getY() + "," + player.getLocation().getZ());
							player.sendMessage("You feel yourself bind to the area");
						} catch (CoreStateInitException e) {
							// skip
						}
					}

				}
				
				
			
			}
		}
		
		if (words.length > 0) {
			// Check player has sufficient faction
			if (triggerentity instanceof Player)
			{
				if ((words[0].toUpperCase().equals("HAIL")))
					listenToHail(solentity,triggerentity);
			}
		}
		
		return;
	}

	private void listenToHail(SoliniaLivingEntity myEntity, LivingEntity triggerentity) {
		if (!(triggerentity instanceof Player))
			return;
		
		try {
			if (getMerchantid() > 0) {
				myEntity.sayto((Player) triggerentity,
						"i have a [" + ChatColor.LIGHT_PURPLE + "SHOP" + ChatColor.AQUA
								+ "] available if you are interested in buying or selling something",
						true);
			}
			
			if (isBanker()) {
				myEntity.sayto((Player) triggerentity,
						"i have a [" + ChatColor.LIGHT_PURPLE + "BANK" + ChatColor.AQUA
								+ "] available if you are interested in storing goods",
						true);
			}

			ISoliniaPlayer solPlayer = SoliniaPlayerAdapter.Adapt((Player) triggerentity);
			for (ISoliniaNPCEventHandler eventHandler : getEventHandlers()) {
				if (!eventHandler.getInteractiontype().equals(InteractionType.ITEM))
					continue;

				// See if player has any items that are wanted
				int itemId = Integer.parseInt(eventHandler.getTriggerdata());
				if (itemId == 0)
					continue;

				if (PlayerUtils.getPlayerTotalCountOfItemId(solPlayer.getBukkitPlayer(), itemId) < 1)
					continue;

				ISoliniaItem item = StateManager.getInstance().getConfigurationManager().getItem(itemId);

				TextComponent tc = new TextComponent(
						TextComponent.fromLegacyText(ChatColor.YELLOW + "[QUEST] "));
				TextComponent tc2 = new TextComponent(TextComponent.fromLegacyText(ChatColor.YELLOW
						+ "[HAND IN] >> Click here to give " + item.getDisplayname() + " <<"+ ChatColor.RESET));
				tc2.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/npcgive " + itemId));
				tc.addExtra(tc2);
				solPlayer.getBukkitPlayer().spigot().sendMessage(tc);

			}
		} catch (CoreStateInitException e) {

		}
	}

	@Override
	public String replaceChatWordsWithHints(String message) {
		return QuestUtils.replaceChatWordsWithHints(message, getEventHandlers());
	}

	@Override
	public List<ISoliniaNPCEventHandler> getEventHandlers() {
		return eventHandlers;
	}

	@Override
	public List<String> getEventHandlerTriggerDatas() {
		List<String> eventHandlerTriggerDatas = new ArrayList<String>();
		for (ISoliniaNPCEventHandler handler : getEventHandlers()) {
			eventHandlerTriggerDatas.add(handler.getTriggerdata());
		}

		return eventHandlerTriggerDatas;
	}

	@Override
	public void setEventHandlers(List<ISoliniaNPCEventHandler> eventHandlers) {
		this.eventHandlers = eventHandlers;
	}

	@Override
	public void addEventHandler(SoliniaNPCEventHandler eventhandler) {
		this.getEventHandlers().add(eventhandler);

	}

	@Override
	public boolean canDodge() {
		if (getClassObj() == null)
			return false;

		if (getClassObj().canDodge() == false)
			return false;

		if (getClassObj().getDodgelevel() > getLevel())
			return false;

		return true;
	}

	@Override
	public boolean canRiposte() {
		if (getClassObj() == null)
			return false;

		if (getClassObj().canRiposte() == false)
			return false;

		if (getClassObj().getRipostelevel() > getLevel())
			return false;

		return true;
	}

	@Override
	public boolean canDoubleAttack() {
		if (getClassObj() == null)
			return false;

		if (getClassObj().canDoubleAttack() == false)
			return false;

		if (getClassObj().getDoubleattacklevel() > getLevel())
			return false;

		return true;
	}
	
	@Override
	public boolean canDisarm() {
		if (getClassObj() == null)
			return false;

		if (getClassObj().canDisarm() == false)
			return false;

		if (getClassObj().getDisarmLevel() > getLevel())
			return false;

		return true;
	}

	@Override
	public boolean getDodgeCheck() {
		if (canDodge() == false)
			return false;

		int chance = getLevel();
		chance += 100;
		chance /= 45;

		return Utils.RandomBetween(1, 500) <= chance;
	}

	@Override
	public boolean getRiposteCheck() {
		if (canRiposte() == false)
			return false;

		int chance = getLevel();
		chance += 100;
		chance /= 50;

		return Utils.RandomBetween(1, 500) <= chance;
	}

	@Override
	public boolean getDoubleAttackCheck() {
		if (canDoubleAttack() == false)
			return false;

		int chance = getLevel();
		chance += getLevel();
		if (getLevel() > 35) {
			chance += getLevel();
		}
		chance /= 5;

		return Utils.RandomBetween(1, 500) <= chance;
	}

	@Override
	public boolean canDualWield() {
		if (getClassObj() == null)
			return false;

		if (getClassObj().canDualWield() == false)
			return false;

		if (getClassObj().getDualwieldlevel() > getLevel())
			return false;

		return true;
	}

	@Override
	public boolean isUndead() {
		if (this.getRace() != null)
			if (this.getRace().isUndead())
				return true;

		return isUndead;
	}

	@Override
	public void setUndead(boolean isUndead) {
		this.isUndead = isUndead;
	}

	@Override
	public boolean isAnimal() {
		if (this.getRace() != null)
			if (this.getRace().isAnimal())
				return true;

		return isAnimal;
	}

	@Override
	public void setAnimal(boolean isAnimal) {
		this.isAnimal = isAnimal;
	}

	@Override
	public String getDeathGrantsTitle() {
		return deathGrantsTitle;
	}

	@Override
	public void setDeathGrantsTitle(String deathGrantsTitle) {
		this.deathGrantsTitle = deathGrantsTitle;
	}

	@Override
	public void sendNPCEvent(CommandSender sender, String triggertext) {
		for (ISoliniaNPCEventHandler eventHandler : eventHandlers) {
			if (!eventHandler.getTriggerdata().toUpperCase().equals(triggertext.toUpperCase()))
				continue;

			eventHandler.sendNPCEvent(sender);
		}
	}

	@Override
	public void sendNPCEvents(CommandSender sender) {
		for (ISoliniaNPCEventHandler eventHandler : eventHandlers) {
			eventHandler.sendNPCEvent(sender);
		}
	}

	@Override
	public void editTriggerEventSetting(String triggertext, String setting, String value)
			throws InvalidNPCEventSettingException {
		for (ISoliniaNPCEventHandler eventHandler : eventHandlers) {
			if (!eventHandler.getTriggerdata().toUpperCase().equals(triggertext.toUpperCase()))
				continue;

			eventHandler.editTriggerEventSetting(setting, value);
		}
	}

	@Override
	public boolean isSummoner() {
		return isSummoner;
	}

	@Override
	public void setSummoner(boolean isSummoner) {
		this.isSummoner = isSummoner;
	}

	@Override
	public int getSkillCap(SkillType skillType) {
		return EntityUtils.getSkillCap(skillType, getClassObj(), getLevel(), "", 0, null);
	}

	@Override
	public int getSkill(SkillType skillType) {

		int skillLevel = getLevel() * 5;

		if (skillLevel > getSkillCap(skillType))
			skillLevel = getSkillCap(skillType);

		return skillLevel;
	}

	public int getAvoidanceRating() {
		return avoidanceRating;
	}

	public void setAvoidanceRating(int avoidanceRating) {
		this.avoidanceRating = avoidanceRating;
	}

	public int getAccuracyRating() {
		return accuracyRating;
	}

	public void setAccuracyRating(int accuracyRating) {
		this.accuracyRating = accuracyRating;
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
	public boolean isHeroic() {
		return heroic;
	}

	@Override
	public void setHeroic(boolean heroic) {
		this.heroic = heroic;
	}

	@Override
	public boolean isRaidboss() {
		return raidboss;
	}

	@Override
	public void setRaidboss(boolean raidboss) {
		this.raidboss = raidboss;
	}

	@Override
	public boolean isRaidheroic() {
		return raidheroic;
	}

	@Override
	public void setRaidheroic(boolean raidheroic) {
		this.raidheroic = raidheroic;
	}

	@Override
	public boolean isSpeaksAllLanguages() {
		return speaksAllLanguages;
	}

	@Override
	public void setSpeaksAllLanguages(boolean speaksAllLanguages) {
		this.speaksAllLanguages = speaksAllLanguages;
	}

	@Override
	public List<ISoliniaItem> getEquippedSoliniaItems(LivingEntity livingEntity) {
		return getEquippedSoliniaItems(livingEntity, false);
	}

	@Override
	public List<ISoliniaItem> getEquippedSoliniaItems(LivingEntity livingEntity, boolean excludeMainHand) {
		List<ISoliniaItem> items = new ArrayList<ISoliniaItem>();

		try {
			List<ItemStack> itemStacks = new ArrayList<ItemStack>();
			if (excludeMainHand == false) {

				itemStacks.add(livingEntity.getEquipment().getItemInMainHand());
			}
			itemStacks.add(livingEntity.getEquipment().getItemInOffHand());
			itemStacks.addAll(Arrays.asList(livingEntity.getEquipment().getArmorContents()));
			
			for (ItemStack itemstack : itemStacks) {
				if (itemstack == null)
					continue;

				ISoliniaItem item = StateManager.getInstance().getConfigurationManager().getItem(itemstack);
				if (item == null)
					continue;
				
				if (item.isSpellscroll())
					continue;

				items.add(item);

				Integer augmentationId = ItemStackUtils.getAugmentationItemId(itemstack);
				ISoliniaItem augItem = null;
				if (augmentationId != null && augmentationId != 0) {
					augItem = StateManager.getInstance().getConfigurationManager().getItem(augmentationId);
					items.add(augItem);
				}
			}
		} catch (CoreStateInitException e) {

		}
		return items;
	}

	@Override
	public boolean isPetControllable() {
		return isPetControllable;
	}

	@Override
	public void setPetControllable(boolean isPetControllable) {
		this.isPetControllable = isPetControllable;
	}

	@Override
	public int getForcedMaxHp() {
		return forcedMaxHp;
	}

	@Override
	public void setForcedMaxHp(int forcedMaxHp) {
		this.forcedMaxHp = forcedMaxHp;
	}

	@Override
	public void Spawn(Location location, int amount) {
		try {
			StateManager.getInstance().getEntityManager().getNPCEntityProvider().spawnNPC(this, amount,
					location.getWorld().getName(), (int) location.getX(), (int) location.getY(), (int) location.getZ());
		} catch (CoreStateInitException e) {

		}
	}

	@Override
	public int getNpcSpellList() {
		return npcSpellList;
	}

	@Override
	public void setNpcSpellList(int npcSpellList) {
		this.npcSpellList = npcSpellList;
	}

	@Override
	public boolean isPlant() {
		if (this.getRace() != null)
			if (this.getRace().isPlant())
				return true;

		return isPlant;
	}

	@Override
	public void setPlant(boolean isPlant) {
		this.isPlant = isPlant;
	}

	@Override
	public int getChanceToRespawnOnDeath() {
		return chanceToRespawnOnDeath;
	}

	@Override
	public void setChanceToRespawnOnDeath(int chanceToRespawnOnDeath) {
		this.chanceToRespawnOnDeath = chanceToRespawnOnDeath;
	}

	@Override
	public boolean isTeleportAttack() {
		return teleportAttack;
	}

	@Override
	public void setTeleportAttack(boolean teleportAttack) {
		this.teleportAttack = teleportAttack;
	}

	@Override
	public String getTeleportAttackLocation() {
		return teleportAttackLocation;
	}

	@Override
	public void setTeleportAttackLocation(String teleportAttackLocation) {
		this.teleportAttackLocation = teleportAttackLocation;
	}

	@Override
	public long getTimefrom() {
		return timefrom;
	}

	@Override
	public void setTimefrom(long timefrom) {
		this.timefrom = timefrom;
	}

	@Override
	public long getTimeto() {
		return timeto;
	}

	@Override
	public void setTimeto(long timeto) {
		this.timeto = timeto;
	}

	@Override
	public boolean isMounted() {
		return isMounted;
	}

	@Override
	public void setMounted(boolean isMounted) {
		this.isMounted = isMounted;
	}

	@Override
	public boolean isSocial() {
		return isSocial;
	}

	@Override
	public void setSocial(boolean isSocial) {
		this.isSocial = isSocial;
	}

	@Override
	public FactionStandingType checkNPCFactionAlly(int other_faction) {
		try
		{
			if (this.getFactionid() < 1)
				return FactionStandingType.FACTION_INDIFFERENT;
			
			ISoliniaFaction faction = StateManager.getInstance().getConfigurationManager().getFaction(this.getFactionid());
			
			FactionStandingEntry fac = faction.getFactionEntry(other_faction);
			if (fac == null)
				return FactionStandingType.FACTION_INDIFFERENT;
	
			if (fac.getValue() > 0)
				return FactionStandingType.FACTION_ALLY;
			else if (fac.getValue() < 0)
				return FactionStandingType.FACTION_SCOWLS;
			else
				return FactionStandingType.FACTION_INDIFFERENT;
		} catch (CoreStateInitException e)
		{
			return FactionStandingType.FACTION_INDIFFERENT;
		}
	}

	@Override
	public int getNPCDefaultAtk() {
		// TODO Auto-generated method stub
		return (int) Math.ceil((getLevel()/7)*10);
	}

	private Tuple<Integer, Integer> calcNPCDamage() {
		int AC_adjust=12;
		int min_dmg = 0;
		int max_dmg = 0;

		if (getLevel() >= 66) {
			if (min_dmg==0)
				min_dmg = 220;
			if (max_dmg==0)
				max_dmg = ((((99000)*(getLevel()-64))/400)*AC_adjust/10);
		}
		else if (getLevel() >= 60 && getLevel() <= 65){
			if(min_dmg==0)
				min_dmg = (getLevel()+(getLevel()/3));
			if(max_dmg==0)
				max_dmg = (getLevel()*3)*AC_adjust/10;
		}
		else if (getLevel() >= 51 && getLevel() <= 59){
			if(min_dmg==0)
				min_dmg = (getLevel()+(getLevel()/3));
			if(max_dmg==0)
				max_dmg = (getLevel()*3)*AC_adjust/10;
		}
		else if (getLevel() >= 40 && getLevel() <= 50) {
			if (min_dmg==0)
				min_dmg = getLevel();
			if(max_dmg==0)
				max_dmg = (getLevel()*3)*AC_adjust/10;
		}
		else if (getLevel() >= 28 && getLevel() <= 39) {
			if (min_dmg==0)
				min_dmg = getLevel() / 2;
			if (max_dmg==0)
				max_dmg = ((getLevel()*2)+2)*AC_adjust/10;
		}
		else if (getLevel() <= 27) {
			if (min_dmg==0)
				min_dmg=1;
			if (max_dmg==0)
				max_dmg = (getLevel()*2)*AC_adjust/10;
		}

		int clfact = getClassLevelFactor();
		min_dmg = (min_dmg * clfact) / 220;
		max_dmg = (max_dmg * clfact) / 220;

		return new Tuple<Integer,Integer>(min_dmg,max_dmg);
	}
	
	private int getClassLevelFactor() {
		int multiplier = 0;
		int mlevel = getLevel();
		
		String className = "";
		if (getClassObj() != null)
			className = getClassObj().getName();
		
		switch (className) {
			case "WARRIOR": {
					if (mlevel < 20) {
						multiplier = 220;
					}
					else if (mlevel < 30) {
						multiplier = 230;
					}
					else if (mlevel < 40) {
						multiplier = 250;
					}
					else if (mlevel < 53) {
						multiplier = 270;
					}
					else if (mlevel < 57) {
						multiplier = 280;
					}
					else if (mlevel < 60) {
						multiplier = 290;
					}
					else if (mlevel < 70) {
						multiplier = 300;
					}
					else {
						multiplier = 311;
					}
					break;
				}
			case "DRUID":
			case "CLERIC":
			case "SHAMAN": {
					if (mlevel < 70) {
						multiplier = 150;
					}
					else {
						multiplier = 157;
					}
					break;
				}
			case "BERSERKER":
			case "PALADIN":
			case "SHADOWKNIGHT": {
					if (mlevel < 35) {
						multiplier = 210;
					}
					else if (mlevel < 45) {
						multiplier = 220;
					}
					else if (mlevel < 51) {
						multiplier = 230;
					}
					else if (mlevel < 56) {
						multiplier = 240;
					}
					else if (mlevel < 60) {
						multiplier = 250;
					}
					else if (mlevel < 68) {
						multiplier = 260;
					}
					else {
						multiplier = 270;
					}
					break;
				}
			case "MONK":
			case "BARD":
			case "ROGUE":
			case "BEASTLORD": {
					if (mlevel < 51) {
						multiplier = 180;
					}
					else if (mlevel < 58) {
						multiplier = 190;
					}
					else if (mlevel < 70) {
						multiplier = 200;
					}
					else {
						multiplier = 210;
					}
					break;
				}
			case "RANGER": {
					if (mlevel < 58) {
						multiplier = 200;
					}
					else if (mlevel < 70) {
						multiplier = 210;
					}
					else {
						multiplier = 220;
					}
					break;
				}
			case "MAGICIAN":
			case "WIZARD":
			case "NECROMANCER":
			case "ENCHANTER": {
					if (mlevel < 70) {
						multiplier = 120;
					}
					else {
						multiplier = 127;
					}
					break;
				}
			default: {
					if (mlevel < 35) {
						multiplier = 210;
					}
					else if (mlevel < 45) {
						multiplier = 220;
					}
					else if (mlevel < 51) {
						multiplier = 230;
					}
					else if (mlevel < 56) {
						multiplier = 240;
					}
					else if (mlevel < 60) {
						multiplier = 250;
					}
					else {
						multiplier = 260;
					}
					break;
				}
		}
		return multiplier;
	}

	@Override
	public int getBaseDamage() {
		Tuple<Integer,Integer> minmaxdmg = calcNPCDamage();
		// This is also in getMinDamage()
		return (int) Math.round((minmaxdmg.b() - minmaxdmg.a()) / 1.9);
	}

	@Override
	public int getMinDamage() {
		Tuple<Integer,Integer> minmaxdmg = calcNPCDamage();
		
		// this should be the same as getBaseDamage()
		int base_damage = (int) Math.round((minmaxdmg.b() - minmaxdmg.a()) / 1.9);
		return (int)(minmaxdmg.a() - Math.round(base_damage / 10.0));
	}

	@Override
	public boolean isBanker() {
		return isBanker;
	}

	@Override
	public void setBanker(boolean isBanker) {
		this.isBanker = isBanker;
	}

	@Override
	public SpellResistType getPetElementalTypeId() {
		if (!this.isCorePet())
			return SpellResistType.RESIST_NONE;
		
		if (this.name.startsWith("SumAir"))
			return SpellResistType.RESIST_MAGIC;
		if (this.name.startsWith("SumFire"))
			return SpellResistType.RESIST_FIRE;
		if (this.name.startsWith("SumWater"))
			return SpellResistType.RESIST_COLD;
		if (this.name.startsWith("SumEarth"))
			return SpellResistType.RESIST_DISEASE;
		return SpellResistType.RESIST_NONE;
	}
}
