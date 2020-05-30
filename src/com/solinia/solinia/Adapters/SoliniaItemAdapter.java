package com.solinia.solinia.Adapters;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.inventory.ItemStack;
import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Exceptions.SoliniaItemException;
import com.solinia.solinia.Factories.SoliniaItemFactory;
import com.solinia.solinia.Interfaces.ISoliniaItem;
import com.solinia.solinia.Managers.StateManager;
import com.solinia.solinia.Models.EQItem;
import com.solinia.solinia.Utils.EQUtils;
import com.solinia.solinia.Utils.ItemStackUtils;

public class SoliniaItemAdapter {
	public static ISoliniaItem Adapt(ItemStack itemStack) throws SoliniaItemException, CoreStateInitException
	{
		if (!ItemStackUtils.IsSoliniaItem(itemStack))
			throw new SoliniaItemException("Not a valid solinia item");
		
		return StateManager.getInstance().getConfigurationManager().getItem(itemStack);
		
	}

	public static ISoliniaItem Adapt(EQItem eqitem, boolean commit) throws SoliniaItemException, CoreStateInitException {
		ItemStack itemStack = EQUtils.getItemStackFromEQItem(eqitem);
		
		ISoliniaItem item = SoliniaItemFactory.CreateItem(itemStack,commit);
		item.setDisplayname(eqitem.getName());
		item.setAgility((int)eqitem.getAagi());
		item.setAC((int)eqitem.getAc());
		//item.setAccuracyRating((int)eqitem.getAccuracy());
		item.setCharisma((int)eqitem.getAcha());
		item.setDexterity((int)eqitem.getAdex());
		item.setIntelligence((int)eqitem.getAint());
		item.setStamina((int)eqitem.getAsta());
		item.setStrength((int)eqitem.getAstr());
		item.setAttack((int)eqitem.getAttack());
		//item.setAvoidanceRating((int)eqitem.getAvoidance());
		item.setWisdom((int)eqitem.getAwis());
		//item.setBaneDmgBodyAmount(((int)eqitem.getBanedmgamt());
		//item.setBanedmgraceamt((int)eqitem.getBanedmgraceamt());
		//item.setBanedmgbody((int)eqitem.getBanedmgbody());
		//item.setBanedmgrace((int)eqitem.getBanedmgrace());
		
		List<String> allowedClasses = new ArrayList<String>();
		for(Integer classId : EQUtils.getClassIdsFromClasses(eqitem))
		{
			allowedClasses.add(EQUtils.getClassNameFromEQClassId(classId));
		}
		item.setAllowedClassNames(allowedClasses);
		
		//item.setClasses(eqitem.getClasses());
		//item.setCombateffects((int)eqitem.getCombateffects());
		//item.setExtradmgskill((int)eqitem.getExtradmgskill());
		//item.setExtradmgamt((int)eqitem.getExtradmgamt());
		item.setWorth((int)eqitem.getPrice()/100);
		item.setColdResist((int)eqitem.getCr());
		item.setDefinedItemDamage((int)eqitem.getDamage());
		//item.setDamageshield((int)eqitem.getDamageshield());
		item.setWeaponDelay((int)eqitem.getDelay());
		item.setDiseaseResist((int)eqitem.getDr());
		item.setElementalDamageType((int)eqitem.getElemdmgtype());
		item.setElementalDamageAmount((int)eqitem.getElemdmgamt());
		item.setFocusEffectId((int)eqitem.getFocuseffect());
		item.setFireResist((int)eqitem.getFr());
		item.setAttackspeed((int)eqitem.getHaste());
		item.setHp((int)eqitem.getHp());
		item.setHpregen((int)eqitem.getRegen());
		item.setBasename(itemStack.getType().name().toUpperCase());
		
		item.setEquipmentSlot(EQUtils.getEquipmentSlotFromEQEmuSlot((int)eqitem.getSlots()));
		
		item.setItemType(EQUtils.getItemTypeFromEQItem(eqitem));
		item.setLore(eqitem.getLore());
		if (eqitem.getMagic() > 0)
			item.setMagic(true);
		item.setMana((int)eqitem.getMana());
		item.setMpregen((int)eqitem.getManaregen());
		item.setMagicResist((int)eqitem.getMr());
		item.setPoisonResist((int)eqitem.getPr());
		//item.setRaces((int)eqitem.getRaces());
		//item.setReclevel((int)eqitem.getReclevel());
		item.setMinLevel((int)eqitem.getReqlevel());
		//item.setSlots((int)eqitem.getSlots());
		item.setAbilityid((int)eqitem.getClickeffect());
		item.setWeaponabilityid((int)eqitem.getProceffect());
		
		//item.setProctype((int)eqitem.getProctype());
		//item.setProclevel2((int)eqitem.getProclevel2());
		//item.setProclevel((int)eqitem.getProclevel());
		//item.setWorneffect((int)eqitem.getWorneffect());
		//item.setWorntype((int)eqitem.getWorntype());
		//item.setWornlevel((int)eqitem.getWornlevel());
		//item.setFocustype((int)eqitem.getFocustype());
		//item.setFocuslevel2((int)eqitem.getFocuslevel2());
		//item.setFocuslevel((int)eqitem.getFocuslevel());
		//item.setScrolleffect((int)eqitem.getScrolleffect());
		//item.setScrolltype((int)eqitem.getScrolltype());
		//item.setScrolllevel2((int)eqitem.getScrolllevel2());
		//item.setScrolllevel((int)eqitem.getScrolllevel());
		//item.setLorefile((int)eqitem.getLorefile());
		//item.setSvcorruption((int)eqitem.getSvcorruption());

		item.setLastUpdatedTimeNow();
	
		if (commit)
			StateManager.getInstance().getConfigurationManager().updateItem(item);
		
		return item;
	}
	
	

	
	
	
}
