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
	public String HeadItemBase64;
	public String ChestItemBase64;
	public String LegsItemBase64;
	public String FeetItemBase64;
		
	public String setSlotByIndex(int i, String base64)
	{

		switch(i)
		{
			case 0:
				this.FingersItemBase64 = base64;
				break;
			case 1:
				this.ShouldersItemBase64 = base64;
				break;
			case 2:
				this.NeckItemBase64 = base64;
				break;
			case 3:
				this.EarsItemBase64 = base64;
				break;
			case 4:
				this.ForearmsItemBase64 = base64;
				break;
			case 5:
				this.ArmsItemBase64 = base64;
				break;
			case 6:
				this.HandsItemBase64 = base64;
				break;
			case 7:
				this.WaistItemBase64 = base64;
				break;
			case 8:
				this.HeadItemBase64 = base64;
				break;
			case 9:
				this.ChestItemBase64 = base64;
				break;
			case 10:
				this.LegsItemBase64 = base64;
				break;
			case 11:
				this.FeetItemBase64 = base64;
				break;
			default:
				return null;
		}
		
		return null;
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
			case 8:
				return this.HeadItemBase64;
			case 9:
				return this.ChestItemBase64;
			case 10:
				return this.LegsItemBase64;
			case 11:
				return this.FeetItemBase64;
			default:
				return null;
		}
	}
}
