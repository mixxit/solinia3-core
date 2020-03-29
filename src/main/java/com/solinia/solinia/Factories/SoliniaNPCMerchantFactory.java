package com.solinia.solinia.Factories;

import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Interfaces.ISoliniaNPCMerchant;
import com.solinia.solinia.Interfaces.ISoliniaNPCMerchantEntry;
import com.solinia.solinia.Managers.StateManager;
import com.solinia.solinia.Models.SoliniaNPCMerchant;
import com.solinia.solinia.Models.SoliniaNPCMerchantEntry;

public class SoliniaNPCMerchantFactory {
	public static void CreateNPCMerchant(String name) throws CoreStateInitException {
		SoliniaNPCMerchant merchant = new SoliniaNPCMerchant();
		merchant.setId(StateManager.getInstance().getConfigurationManager().getNextNPCMerchantId());
		merchant.setName(name);
		StateManager.getInstance().getConfigurationManager().addNPCMerchant(merchant);
	}
	
	public static void AddNPCMerchantItem(int merchantid, int itemid)
	{
		try
		{
			ISoliniaNPCMerchant merchant = StateManager.getInstance().getConfigurationManager().getNPCMerchant(merchantid);
			SoliniaNPCMerchantEntry merchantentry = new SoliniaNPCMerchantEntry();
			int id = 1;
			for(ISoliniaNPCMerchantEntry entry : merchant.getEntries())
			{
				if (entry.getId() > id)
					id = entry.getId() + 1;
			}
			
			merchantentry.setId(id);
			merchantentry.setMerchantid(merchantid);
			merchantentry.setItemid(itemid);
			StateManager.getInstance().getConfigurationManager().getNPCMerchant(merchantid).getEntries().add(merchantentry);
		} catch (CoreStateInitException e)
		{
			e.printStackTrace();
		}
	}
}
