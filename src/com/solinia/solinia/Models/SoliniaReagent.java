package com.solinia.solinia.Models;

import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Interfaces.ISoliniaItem;
import com.solinia.solinia.Managers.StateManager;

public class SoliniaReagent  {
	private Integer itemId = 0;
	private Integer qty = 0;
	private String creationstamp = StateManager.getInstance().getInstanceGuid();
	
	public SoliniaReagent(Integer itemId, Integer qty, String creationstamp)
	{
		this.itemId = itemId;
		this.qty = qty;
		this.creationstamp = creationstamp;
	}

	public Integer getItemId() {
		return itemId;
	}

	public void setItemId(Integer itemId) {
		this.itemId = itemId;
	}

	public Integer getQty() {
		removeOldSummonedItems();
		return qty;
	}

	public void setQty(Integer qty) {
		removeOldSummonedItems();
		this.qty = qty;
	}

	public String getCreationstamp() {
		return creationstamp;
	}

	public void setCreationstamp(String creationstamp) {
		this.creationstamp = creationstamp;
	}

	public void addQty(int amount) {
		removeOldSummonedItems();
		this.qty += amount;
		if (this.qty > Integer.MAX_VALUE)
			this.qty = Integer.MAX_VALUE;
	}
	
	public void reduceQty(int amount) {
		removeOldSummonedItems();
		this.qty -= amount;
		if (this.qty < 0)
			this.qty = 0;
	}

	private void removeOldSummonedItems() {
		try
		{
			ISoliniaItem item = StateManager.getInstance().getConfigurationManager().getItem(this.getItemId());
			if (item == null)
			{				
				this.qty = 0;
				return;
			}
			
			if (item.isTemporary())
			if (!this.creationstamp.equals(StateManager.getInstance().getInstanceGuid()))
			{
				this.qty = 0;
			}
		} catch (CoreStateInitException e)
		{
			
		}
	}
}
