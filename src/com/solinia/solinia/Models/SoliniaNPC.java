package com.solinia.solinia.Models;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.solinia.solinia.Adapters.SoliniaPlayerAdapter;
import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Exceptions.InvalidNPCEventSettingException;
import com.solinia.solinia.Exceptions.InvalidNpcSettingException;
import com.solinia.solinia.Interfaces.ISoliniaAARank;
import com.solinia.solinia.Interfaces.ISoliniaClass;
import com.solinia.solinia.Interfaces.ISoliniaFaction;
import com.solinia.solinia.Interfaces.ISoliniaItem;
import com.solinia.solinia.Interfaces.ISoliniaLootDrop;
import com.solinia.solinia.Interfaces.ISoliniaLootDropEntry;
import com.solinia.solinia.Interfaces.ISoliniaLootTable;
import com.solinia.solinia.Interfaces.ISoliniaLootTableEntry;
import com.solinia.solinia.Interfaces.ISoliniaNPC;
import com.solinia.solinia.Interfaces.ISoliniaNPCEventHandler;
import com.solinia.solinia.Interfaces.ISoliniaNPCMerchant;
import com.solinia.solinia.Interfaces.ISoliniaNPCMerchantEntry;
import com.solinia.solinia.Interfaces.ISoliniaPlayer;
import com.solinia.solinia.Interfaces.ISoliniaSpawnGroup;
import com.solinia.solinia.Interfaces.ISoliniaSpell;
import com.solinia.solinia.Managers.StateManager;
import com.solinia.solinia.Utils.Utils;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.chat.ComponentSerializer;

public class SoliniaNPC implements ISoliniaNPC {
	private int id;
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
	private boolean isUndead = false;
	private boolean isAnimal = false;
	private List<ISoliniaNPCEventHandler> eventHandlers = new ArrayList<ISoliniaNPCEventHandler>();
	private String deathGrantsTitle = "";
	private boolean isSummoner = false;
	private int avoidanceRating = 0;
	private int accuracyRating = 0;
	private int ac = 0;
	private boolean speaksAllLanguages = false;
	private boolean operatorCreated = true;
	
	@Override
	public int getId() {
		return id;
	}

	@Override
	public void setId(int id) {
		this.id = id;
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
	public void sendMerchantItemListToPlayer(Player player, int pageno) 
	{
		try {
			Inventory merchantInventory = StateManager.getInstance().getEntityManager().getMerchantInventory(player.getUniqueId(),this, pageno);
			if (merchantInventory != null)
				player.openInventory(merchantInventory);
		} catch (CoreStateInitException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	//@Override
	/*
	public void sendOldMerchantItemListToPlayer(Player player, int pageno) 
	{
		String debugLastJson = "";
		try
		{
			pageno = pageno - 1;
			
			int sizePerPage = 10;
			
			List<ISoliniaNPCMerchantEntry> fullmerchantentries = StateManager.getInstance().getEntityManager()
					.getNPCMerchantCombinedEntries(this);
			
			List<ISoliniaNPCMerchantEntry> merchantentries = fullmerchantentries.stream()
					  .skip(pageno * sizePerPage)
					  .limit(sizePerPage)
					  .collect(Collectors.toCollection(ArrayList::new));
			
			for (ISoliniaNPCMerchantEntry merchantitem : merchantentries) {
				ISoliniaItem item = StateManager.getInstance().getConfigurationManager().getItem(merchantitem.getItemid());
				int price = item.getWorth();
	
				// Buy 1
				if (merchantitem.getId() != 0 && merchantitem.getTemporaryquantitylimit() == 0)
				{
					final String jsonbuy = "{\"text\":\"~ (CLICK-BUY) " + item.getDisplayname() + " x1: $" + price
							+ "\",\"color\":\"aqua\",\"underlined\":false,\"clickEvent\":{\"action\":\"suggest_command\",\"value\":\"/npcbuy "
							+ merchantitem.getItemid() + " 1\"},\"hoverEvent\":{\"action\":\"show_item\",\"value\":\""
							+ item.asJsonStringEscaped() + "\"}}";
					player.spigot().sendMessage(ComponentSerializer.parse(jsonbuy));
				}
				
				// Buy 1 (temporary items)
				if (merchantitem.getId() == 0 && merchantitem.getTemporaryquantitylimit() > 0)
				{
					final String jsonbuy = "{\"text\":\"~ (CLICK-BUYBACK) " + item.getDisplayname() + " (Reset) x1: $" + price
							+ "\",\"color\":\"green\",\"underlined\":false,\"clickEvent\":{\"action\":\"suggest_command\",\"value\":\"/npcbuy "
							+ merchantitem.getItemid() + " 1\"},\"hoverEvent\":{\"action\":\"show_item\",\"value\":\""
							+ item.asJsonStringEscaped() + "\"}}";
					player.spigot().sendMessage(ComponentSerializer.parse(jsonbuy));
				}
	
				// Buy 64
				if (merchantitem.getId() != 0 && merchantitem.getTemporaryquantitylimit() == 0)
				{
					final String jsonbuy64 = "{\"text\":\"~ (CLICK-BUY) " + item.getDisplayname() + " x64: $" + (price * 64)
							+ "\",\"color\":\"aqua\",\"underlined\":false,\"clickEvent\":{\"action\":\"suggest_command\",\"value\":\"/npcbuy "
							+ merchantitem.getItemid() + " 64\"},\"hoverEvent\":{\"action\":\"show_item\",\"value\":\""
							+ item.asJsonStringEscaped() + "\"}}";
					player.spigot().sendMessage(ComponentSerializer.parse(jsonbuy64));
				}
			}
	
			for (int i = 0; i < 36; i++) {
				ItemStack itemstack = player.getInventory().getItem(i);
				if (itemstack == null)
					continue;
	
				if (itemstack.getType().equals(Material.AIR))
					continue;
	
				int ench = itemstack.getEnchantmentLevel(Enchantment.DURABILITY);
				if (ench < 1000)
					continue;
	
				ISoliniaItem item = StateManager.getInstance().getConfigurationManager().getItem(itemstack);
				if (item == null)
					continue;
				
				if (item.isTemporary())
					continue;
	
				int sellprice = item.getWorth();
	
				// List all items sellable by player
				final String jsonsell = "{\"text\":\"~ (CLICK-SELL) " + item.getDisplayname() + " x1: $" + sellprice
						+ "\",\"color\":\"yellow\",\"underlined\":false,\"clickEvent\":{\"action\":\"suggest_command\",\"value\":\"/npcsell "
						+ item.getId() + " 1\"},\"hoverEvent\":{\"action\":\"show_item\",\"value\":\""
						+ item.asJsonStringEscaped() + "\"}}";

				player.spigot().sendMessage(ComponentSerializer.parse(jsonsell));

				if (itemstack.getAmount() > 1) {
					final String jsonsellmany = "{\"text\":\"~ (CLICK-SELL) " + item.getDisplayname() + " x"
							+ itemstack.getAmount() + ": $" + (sellprice * itemstack.getAmount())
							+ "\",\"color\":\"yellow\",\"underlined\":false,\"clickEvent\":{\"action\":\"suggest_command\",\"value\":\"/npcsell "
							+ item.getId() + " " + itemstack.getAmount()
							+ "\"},\"hoverEvent\":{\"action\":\"show_item\",\"value\":\""
							+ item.asJsonStringEscaped() + "\"}}";
					player.spigot().sendMessage(ComponentSerializer.parse(jsonsellmany));
				}
			}
			
			player.sendMessage("Displayed Page " + ChatColor.GOLD + (pageno + 1) + ChatColor.RESET + "/" + ChatColor.GOLD + Math.ceil((float)fullmerchantentries.size() / (float)sizePerPage) + ChatColor.RESET + " (See SHOP <pageno>");

		} catch (CoreStateInitException e)
		{
			e.printStackTrace();
		} catch (Exception e)
		{
			player.sendMessage("* This npc is currently not trading");
			e.printStackTrace();
			System.out.println("Last Json Was:");
			System.out.println(debugLastJson);
		}
	}
    */
	
	@Override
	public void sendNpcSettingsToSender(CommandSender sender) throws CoreStateInitException {
		sender.sendMessage(ChatColor.RED + "NPC Settings for " + ChatColor.GOLD + getName() + ChatColor.RESET);
		sender.sendMessage("----------------------------");
		sender.sendMessage("- id: " + ChatColor.GOLD + getId() + ChatColor.RESET);
		sender.sendMessage("- name: " + ChatColor.GOLD + getName() + ChatColor.RESET);
		sender.sendMessage("- raceid: " + ChatColor.GOLD + getRaceid() + ChatColor.RESET);
		sender.sendMessage("- professionid: " + ChatColor.GOLD + getClassid() + ChatColor.RESET);
		sender.sendMessage(ChatColor.RED + "STATS" + ChatColor.RESET);
		sender.sendMessage("- level: " + ChatColor.GOLD + getLevel() + ChatColor.RESET);
		sender.sendMessage("- avoidancerating: " + ChatColor.GOLD + getAvoidanceRating() + ChatColor.RESET);
		sender.sendMessage("- accuracyrating: " + ChatColor.GOLD + getAccuracyRating() + ChatColor.RESET);
		sender.sendMessage("- ac: " + ChatColor.GOLD + getAC() + ChatColor.RESET);
		sender.sendMessage("- deathgrantstitle: " + ChatColor.GOLD + getDeathGrantsTitle() + ChatColor.RESET);
		sender.sendMessage("----------------------------");
		sender.sendMessage(ChatColor.RED + "SPAWNING" + ChatColor.RESET);
		sender.sendMessage("- randomspawn: " + ChatColor.GOLD + isRandomSpawn() + ChatColor.RESET);
		sender.sendMessage("----------------------------");
		sender.sendMessage(ChatColor.RED + "AI" + ChatColor.RESET);
		sender.sendMessage("- pet: " + ChatColor.GOLD + isPet() + ChatColor.RESET);
		sender.sendMessage("- guard: " + ChatColor.GOLD + isGuard() + ChatColor.RESET);
		sender.sendMessage("- roamer: " + ChatColor.GOLD + isRoamer() + ChatColor.RESET);
		sender.sendMessage("- killtriggertext: " + ChatColor.GOLD + getKillTriggerText());
		sender.sendMessage("- summoner: " + ChatColor.GOLD + isSummoner() + ChatColor.RESET);
		sender.sendMessage("- randomchattriggertext: " + ChatColor.GOLD + getRandomchatTriggerText());
		sender.sendMessage("----------------------------");
		sender.sendMessage(ChatColor.RED + "APPEARANCE" + ChatColor.RESET);
		sender.sendMessage("- mctype: " + ChatColor.GOLD + getMctype() + ChatColor.RESET);
		sender.sendMessage("- usedisguise: " + ChatColor.GOLD + isUsedisguise() + ChatColor.RESET);
		sender.sendMessage("- disguisetype: " + ChatColor.GOLD + getDisguisetype() + ChatColor.RESET);
		sender.sendMessage("- customhead: " + ChatColor.GOLD + isCustomhead() + ChatColor.RESET);
		sender.sendMessage("- customheaddata: " + ChatColor.GOLD + getCustomheaddata() + ChatColor.RESET);
		sender.sendMessage("- upsidedown: " + ChatColor.GOLD + isUpsidedown() + ChatColor.RESET);
		sender.sendMessage("- burning: " + ChatColor.GOLD + isBurning() + ChatColor.RESET);
		sender.sendMessage("- invisible: " + ChatColor.GOLD + isInvisible() + ChatColor.RESET);
		sender.sendMessage("- isundead: " + ChatColor.GOLD + isUndead() + ChatColor.RESET);
		sender.sendMessage("- isanimal: " + ChatColor.GOLD + isAnimal() + ChatColor.RESET);
		sender.sendMessage(ChatColor.RED + "EQUIPMENT" + ChatColor.RESET);
		if (getLoottableid() != 0) {
			sender.sendMessage("- loottableid: " + ChatColor.GOLD + getLoottableid() + " ("
					+ StateManager.getInstance().getConfigurationManager().getLootTable(getLoottableid()).getName()
					+ ")" + ChatColor.RESET);
		} else {
			sender.sendMessage(
					"- loottableid: " + ChatColor.GOLD + getLoottableid() + " (No Loot Table)" + ChatColor.RESET);
		}
		sender.sendMessage("- handitem: " + ChatColor.GOLD + getHanditem() + ChatColor.RESET);
		sender.sendMessage("- offhanditem: " + ChatColor.GOLD + getOffhanditem() + ChatColor.RESET);
		sender.sendMessage("- headitem: " + ChatColor.GOLD + getHeaditem() + ChatColor.RESET);
		sender.sendMessage("- chestitem: " + ChatColor.GOLD + getChestitem() + ChatColor.RESET);
		sender.sendMessage("- legsitem: " + ChatColor.GOLD + getLegsitem() + ChatColor.RESET);
		sender.sendMessage("- feetitem: " + ChatColor.GOLD + getFeetitem() + ChatColor.RESET);
		sender.sendMessage(ChatColor.RED + "FACTION & MERCHANT" + ChatColor.RESET);
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
		sender.sendMessage(ChatColor.RED + "MISC" + ChatColor.RESET);
		sender.sendMessage("- heroic: " + ChatColor.GOLD + isHeroic());
		sender.sendMessage("- boss: " + ChatColor.GOLD + isBoss());
		sender.sendMessage("- raidheroic: " + ChatColor.GOLD + isRaidheroic());
		sender.sendMessage("- raidboss: " + ChatColor.GOLD + isRaidboss());
		sender.sendMessage("- speaksalllangauges: " + ChatColor.GOLD + isSpeaksAllLanguages());
		sender.sendMessage("----------------------------");
		if (getLoottableid() != 0) {
			sender.sendMessage(ChatColor.RED + "LOOT" + ChatColor.RESET + "[" + getLoottableid() + "] - " + "("
					+ StateManager.getInstance().getConfigurationManager().getLootTable(getLoottableid()).getName()
					+ ")");
			ISoliniaLootTable loottable = StateManager.getInstance().getConfigurationManager()
					.getLootTable(getLoottableid());
			for (ISoliniaLootTableEntry le : StateManager.getInstance().getConfigurationManager()
					.getLootTable(loottable.getId()).getEntries()) {
				ISoliniaLootDrop ld = StateManager.getInstance().getConfigurationManager()
						.getLootDrop(le.getLootdropid());
				sender.sendMessage(
						"- " + ChatColor.GOLD + ld.getName().toUpperCase() + ChatColor.RESET + "[" + ld.getId() + "]:");
				}
		}

	}

	@Override
	public void editSetting(String setting, String value)
			throws InvalidNpcSettingException, NumberFormatException, CoreStateInitException, java.io.IOException {

		switch (setting.toLowerCase()) {
		case "name":
			if (value.equals(""))
				throw new InvalidNpcSettingException("Name is empty");

			if (value.length() > 25)
				throw new InvalidNpcSettingException("Name is longer than 25 characters");
			setName(value);
			break;
		case "mctype":
			setMctype(value);
			break;
		case "level":
			setLevel(Integer.parseInt(value));
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
		case "usedisguise":
			setUsedisguise(Boolean.parseBoolean(value));
			break;
		case "disguisetype":
			setDisguisetype(value);
			break;
		case "headitem":
			setHeaditem(value);
			break;
		case "chestitem":
			setChestitem(value);
			break;
		case "legsitem":
			setLegsitem(value);
			break;
		case "feetitem":
			setFeetitem(value);
			break;
		case "handitem":
			setHanditem(value);
			break;
		case "offhanditem":
			setOffhanditem(value);
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
			break;
		case "upsidedown":
			setUpsidedown(Boolean.parseBoolean(value));
			break;
		case "loottableid":
			if (Integer.parseInt(value) == 0)
			{
				setLoottableid(0);
				break;
			}
			
			ISoliniaLootTable loottable = StateManager.getInstance().getConfigurationManager()
			.getLootTable(Integer.parseInt(value));
			if (loottable == null)
				throw new InvalidNpcSettingException("Loottable ID does not exist");
			
			setLoottableid(Integer.parseInt(value));
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
			break;
		case "ac":
			setAC(Integer.parseInt(value));
		case "randomchattriggertext":
			setRandomchatTriggerText(value);
			break;
		case "guard":
			setGuard(Boolean.parseBoolean(value));
			break;
		case "pet":
			setPet(Boolean.parseBoolean(value));
			break;
		case "roamer":
			setRoamer(Boolean.parseBoolean(value));
			break;
		case "undead":
			setUndead(Boolean.parseBoolean(value));
			break;
		case "animal":
			setAnimal(Boolean.parseBoolean(value));
			break;
		case "summoner":
			setSummoner(Boolean.parseBoolean(value));
			break;
		case "disablespawners":
			disableAllSpawners(Boolean.parseBoolean(value));
			break;
		case "accuracyrating":
			setAccuracyRating(Integer.parseInt(value));
			break;
		case "avoidancerating":
			setAvoidanceRating(Integer.parseInt(value));
			break;
		case "speaksalllanguages":
			setSpeaksAllLanguages(Boolean.parseBoolean(value));
			break;
		default:
			throw new InvalidNpcSettingException(
					"Invalid NPC setting. Valid Options are: name,mctype,health,damage,factionid,usedisguise,disguisetype,headitem,chestitem,legsitem,feetitem,handitem,offhanditem,boss,burning,invisible,customhead,customheaddata,merchantid,upsidedown,loottableid,randomspawn,killtriggertext,randomchattriggertext,guard,roamer,undead,customheaddatafromnpc,summoner,disablespawners,animal,speaksalllanguages");
		}
	}

	@Override
	public void disableAllSpawners(boolean parseBoolean) {
		try {
			for(ISoliniaSpawnGroup group : StateManager.getInstance().getConfigurationManager().getSpawnGroups())
			{
				if (group.getNpcid() == this.getId())
				{
					System.out.println("Set Spawner Disabled Status: " + group.getId() + ":" + group.getName() + " - " + parseBoolean);
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
	public ISoliniaClass getClassObj()
	{
		if (getClassid() < 1)
			return null;
		
		try
		{
			return StateManager.getInstance().getConfigurationManager().getClassObj(getClassid());
		} catch (CoreStateInitException e)
		{
			return null;
		}
	}
	
	@Override
	public boolean isPet() {
		return isPet;
	}

	@Override
	public void setPet(boolean isPet) {
		this.isPet = isPet;
	}

	@Override
	public void processInteractionEvent(SoliniaLivingEntity solentity, LivingEntity triggerentity, InteractionType type, String data) {
		switch(type)
		{
			case CHAT:
				processChatInteractionEvent(solentity, triggerentity, data);
			default:
				return;
		}
	}
	
	@Override
	public void processChatInteractionEvent(SoliniaLivingEntity solentity, LivingEntity triggerentity, String data)
	{
		String words[] = data.split(" ");
		
		// Merchant special commands
		if (words.length > 0)
		{
			// Check player has sufficient faction
			if (triggerentity instanceof Player)
			if (solentity.getNpcid() > 0)
			{
				try
				{
					ISoliniaNPC npc = StateManager.getInstance().getConfigurationManager().getNPC(solentity.getNpcid());
					if (npc.getFactionid() > 0)
					{
						ISoliniaPlayer solPlayer = SoliniaPlayerAdapter.Adapt((Player)triggerentity);
						PlayerFactionEntry factionEntry = solPlayer.getFactionEntry(npc.getFactionid());
						if (factionEntry != null)
						{
							switch (Utils.getFactionStandingType(factionEntry.getFactionId(), factionEntry.getValue()))
							{
								case FACTION_THREATENLY:
								case FACTION_SCOWLS:
									solentity.emote("* " + npc.getName() + " scowls angrily at " + solPlayer.getFullName());
									return;
								default:
									break;
							}
						}
					}
				} catch (CoreStateInitException e)
				{
					
				}
			}
			
			switch(words[0].toUpperCase())
			{
				case "SHOP":
					if (triggerentity instanceof Player)
					if (getMerchantid() > 0)
					{
						if (words.length == 1)
						{
							sendMerchantItemListToPlayer((Player)triggerentity, 1);
							return;
						}
						
						int page = 1;
						try
						{
							page = Integer.parseInt(words[1]);
						} catch (Exception e)
						{
							
						}
						
						if (page < 1)
							page = 1;
						
						sendMerchantItemListToPlayer((Player)triggerentity, page);
					}
					return;
				case "LISTEFFECTS":
					if (triggerentity instanceof Player)
					{
						if (((Player)triggerentity).isOp())
						{
							Player player = (Player)triggerentity;
							try
							{
								for (SoliniaActiveSpell spell : StateManager.getInstance().getEntityManager().getActiveEntitySpells(solentity.getBukkitLivingEntity()).getActiveSpells())
								{
									player.sendMessage(spell.getSpell().getName());
									for (ActiveSpellEffect effect : spell.getActiveSpellEffects())
									{
										player.sendMessage(" - " + effect.getSpellEffectType().name() + " " + effect.getBase());
									}
								}
							} catch (CoreStateInitException e)
							{
								// 
							}
						}
					}
					return;
				default:
					break;
			}
		}
		
		// Normal text matching
		for(ISoliniaNPCEventHandler handler : getEventHandlers())
		{
			if (!handler.getInteractiontype().equals(InteractionType.CHAT))
				continue;
			
			if (!data.toUpperCase().contains(handler.getTriggerdata().toUpperCase()))
				continue;
			
			if (handler.getChatresponse() != null && !handler.getChatresponse().equals(""))
			{
				if ((triggerentity instanceof Player))
				{
					if (!handler.playerMeetsRequirements((Player)triggerentity))
						return;
				}
				
				String response = handler.getChatresponse();
				solentity.say(replaceChatWordsWithHints(response),triggerentity);
				if (triggerentity instanceof Player)
					handler.awardPlayer((Player)triggerentity);
				
				if (handler.getTeleportResponse() != null && !handler.getTeleportResponse().equals(""))
				{
					if (triggerentity instanceof Player)
					{
						String[] zonedata = handler.getTeleportResponse().split(",");
						// Dissasemble the value to ensure it is correct
						String world = zonedata[0];
						double x = Double.parseDouble(zonedata[1]);
						double y = Double.parseDouble(zonedata[2]);
						double z = Double.parseDouble(zonedata[3]);
						Location loc = new Location(Bukkit.getWorld(world),x,y,z);
						((Player)triggerentity).teleport(loc);
					}
					
				}
			}
		}
		return;
	}
	
	@Override
	public String replaceChatWordsWithHints(String message)
	{
		List<String> messages = Arrays.asList(message.toUpperCase().split(" "));
		
		if (getEventHandlers().size() > 0)
		{
			String searchlist = "";
			for(String messageword : messages)
			{
				searchlist += messageword + ",";
			}
		}
		
		for(ISoliniaNPCEventHandler handler : getEventHandlers())
		{
			if (!handler.getInteractiontype().equals(InteractionType.CHAT))
				continue;

			if (!messages.contains(handler.getTriggerdata().toUpperCase()))
				continue;


			message = message.toLowerCase().replace(handler.getTriggerdata().toLowerCase(), "["+handler.getTriggerdata().toLowerCase()+"]");
		}
		message = message.replace("[", "[" + ChatColor.LIGHT_PURPLE);
		message = message.replace("]", ChatColor.AQUA + "]");
		return message.toLowerCase();
	}

	@Override
	public List<ISoliniaNPCEventHandler> getEventHandlers() {
		return eventHandlers;
	}
	
	@Override
	public List<String> getEventHandlerTriggerDatas() {
		List<String> eventHandlerTriggerDatas = new ArrayList<String>();
		for(ISoliniaNPCEventHandler handler : getEventHandlers())
		{
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
	public boolean getDodgeCheck()
	{
		if (canDodge() == false)
			return false;
		
		int chance = getLevel();
		chance += 100;
		chance /= 45;

		return Utils.RandomBetween(1, 500) <= chance;
	}
	
	@Override 
	public boolean getRiposteCheck()
	{
		if (canRiposte() == false)
			return false;
		
		int chance = getLevel();
		chance += 100;
		chance /= 50;

		return Utils.RandomBetween(1, 500) <= chance;
	}
	
	@Override 
	public boolean getDoubleAttackCheck()
	{
		if (canDoubleAttack() == false)
			return false;
		
		int chance = getLevel();
		chance += getLevel();
		if (getLevel() > 35)
		{
			chance += getLevel();
		}
		chance /= 5;

		return Utils.RandomBetween(1, 500) <= chance;
	}

	@Override
	public boolean isUndead() {
		return isUndead;
	}

	@Override
	public void setUndead(boolean isUndead) {
		this.isUndead = isUndead;
	}
	
	@Override
	public boolean isAnimal() {
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
		for (ISoliniaNPCEventHandler eventHandler : eventHandlers)
		{
			if (!eventHandler.getTriggerdata().toUpperCase().equals(triggertext.toUpperCase()))
				continue;
			
			eventHandler.sendNPCEvent(sender);
		}
	}

	@Override
	public void sendNPCEvents(CommandSender sender) {
		for(ISoliniaNPCEventHandler eventHandler : eventHandlers)
		{
			eventHandler.sendNPCEvent(sender);
		}
	}

	@Override
	public void editTriggerEventSetting(String triggertext, String setting, String value) throws InvalidNPCEventSettingException {
		for(ISoliniaNPCEventHandler eventHandler : eventHandlers)
		{
			if (!eventHandler.getTriggerdata().toUpperCase().equals(triggertext.toUpperCase()))
				continue;
			
			eventHandler.editTriggerEventSetting(setting,value);
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
	public int getSkillCap(String skillName) {
		return Utils.getSkillCap(skillName, getClassObj(), getLevel(), "");
	}

	@Override
	public int getSkill(String skillName) {
		
		int skillLevel = getLevel() * 5;
		
		if (skillLevel > getSkillCap(skillName))
			skillLevel = getSkillCap(skillName);
			
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
	public void setOperatorCreated(boolean operatorCreated) {
		// TODO Auto-generated method stub
		this.operatorCreated  = operatorCreated;
	}

	@Override
	public boolean isOperatorCreated() {
		// TODO Auto-generated method stub
		return this.operatorCreated;
	}

}

	
