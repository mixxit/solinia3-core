package com.solinia.solinia.Models;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Managers.StateManager;

public class UniversalMerchant {
	public UUID universalMerchant = UUID.randomUUID();
	public List<UniversalMerchantEntry> fullmerchantentries = new ArrayList<UniversalMerchantEntry>();
	public String merchantName = "";
	
	public void sendMerchantItemListToPlayer(Player player, int pageno) 
	{
		try {
			Inventory merchantInventory = StateManager.getInstance().getEntityManager().getMerchantInventory(player.getUniqueId(), pageno, this);
			if (merchantInventory != null)
				player.openInventory(merchantInventory);
		} catch (CoreStateInitException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
