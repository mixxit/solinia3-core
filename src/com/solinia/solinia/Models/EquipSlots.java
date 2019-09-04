package com.solinia.solinia.Models;

public class EquipSlots {
	public String FingersItemBase64;
	public String ShouldersItemBase64;
	public String NeckItemBase64;
	public String EarsItemBase64;
	public String ForearmsItemBase64;
	public String ArmsItemBase64;
	public String HandsItemBase64;
	public String WaistItemBase64;
	
	public String setSlotByIndex(int i, String base64)
	{
		switch(i)
		{
			case 0:
				this.FingersItemBase64 = base64;
			case 1:
				this.ShouldersItemBase64 = base64;
			case 2:
				this.NeckItemBase64 = base64;
			case 3:
				this.EarsItemBase64 = base64;
			case 4:
				this.ForearmsItemBase64 = base64;
			case 5:
				this.ArmsItemBase64 = base64;
			case 6:
				this.HandsItemBase64 = base64;
			case 7:
				this.WaistItemBase64 = base64;
			default:
				return null;
		}
	}
	
	public String getSlotByIndex(int i)
	{
		switch(i)
		{
			case 0:
				return this.FingersItemBase64;
			case 1:
				return this.ShouldersItemBase64;
			case 2:
				return this.NeckItemBase64;
			case 3:
				return this.EarsItemBase64;
			case 4:
				return this.ForearmsItemBase64;
			case 5:
				return this.ArmsItemBase64;
			case 6:
				return this.HandsItemBase64;
			case 7:
				return this.WaistItemBase64;
			default:
				return null;
		}
	}
}
