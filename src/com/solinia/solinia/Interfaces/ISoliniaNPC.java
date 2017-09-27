package com.solinia.solinia.Interfaces;

import java.io.IOException;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Exceptions.InvalidNpcSettingException;
import com.solinia.solinia.Models.InteractionType;
import com.solinia.solinia.Models.SoliniaLivingEntity;
import com.solinia.solinia.Models.SoliniaNPCEventHandler;

public interface ISoliniaNPC {

	int getId();

	void setId(int id);

	String getName();

	void setName(String name);

	void sendNpcSettingsToSender(CommandSender sender) throws CoreStateInitException;

	void editSetting(String setting, String value) throws InvalidNpcSettingException, NumberFormatException, CoreStateInitException, IOException;

	String getMctype();

	void setMctype(String mctype);

	int getLevel();

	void setLevel(int level);

	int getFactionid();

	void setFactionid(int factionid);

	boolean isUsedisguise();

	void setUsedisguise(boolean usedisguise);

	String getDisguisetype();

	void setDisguisetype(String disguisetype);

	String getHeaditem();

	void setHeaditem(String headitem);

	String getChestitem();

	void setChestitem(String chestitem);

	String getLegsitem();

	void setLegsitem(String legsitem);

	String getFeetitem();

	void setFeetitem(String feetitem);

	String getHanditem();

	void setHanditem(String handitem);

	String getOffhanditem();

	void setOffhanditem(String offhanditem);

	boolean isBoss();

	void setBoss(boolean boss);

	boolean isBurning();

	void setBurning(boolean burning);

	boolean isInvisible();

	void setInvisible(boolean invisible);

	boolean isCustomhead();

	void setCustomhead(boolean customhead);

	String getCustomheaddata();

	void setCustomheaddata(String customheaddata);

	int getMerchantid();

	void setMerchantid(int merchantid);

	boolean isUpsidedown();

	void setUpsidedown(boolean upsidedown);

	int getLoottableid();

	void setLoottableid(int loottableid);

	int getRaceid();

	void setRaceid(int raceid);

	int getClassid();

	void setClassid(int classid);

	int getMaxDamage();

	int getMaxHP();

	boolean isRandomSpawn();

	void setRandomSpawn(boolean isRandomSpawn);

	String getKillTriggerText();

	void setKillTriggerText(String killTriggerText);

	String getRandomchatTriggerText();

	void setRandomchatTriggerText(String randomchatTriggerText);

	boolean isGuard();

	void setGuard(boolean isGuard);

	boolean isRoamer();

	void setRoamer(boolean isRoamer);

	Integer getMaxMP();

	ISoliniaClass getClassObj();
	
	public int getStrength();

	public int getStamina();
	
	public int getAgility();
	
	public int getDexterity();
	
	public int getIntelligence();
	
	public int getWisdom();
	
	public int getCharisma();

	boolean isPet();

	void setPet(boolean isPet);

	List<ISoliniaNPCEventHandler> getEventHandlers();

	void setEventHandlers(List<ISoliniaNPCEventHandler> eventHandlers);

	void processInteractionEvent(SoliniaLivingEntity solentity, LivingEntity triggerentity, InteractionType type,
			String data);

	void processChatInteractionEvent(SoliniaLivingEntity solentity, LivingEntity triggerentity, String data);

	void addEventHandler(SoliniaNPCEventHandler eventhandler);

	void sendMerchantItemListToPlayer(Player player, int pageno);

	boolean canDodge();

	boolean canRiposte();

	boolean canDoubleAttack();

	boolean getDodgeCheck();

	boolean getRiposteCheck();

	boolean getDoubleAttackCheck();
}
