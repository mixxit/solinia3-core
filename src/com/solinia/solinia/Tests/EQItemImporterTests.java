package com.solinia.solinia.Tests;

import org.junit.Test;

import com.solinia.solinia.Adapters.SoliniaItemAdapter;
import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Exceptions.SoliniaItemException;
import com.solinia.solinia.Interfaces.ISoliniaItem;
import com.solinia.solinia.Models.EQItem;
import com.solinia.solinia.Models.EquipmentSlot;
import com.solinia.solinia.Utils.JsonUtils;
import com.solinia.solinia.Utils.TextUtils;
import static org.junit.Assert.assertEquals;

public class EQItemImporterTests {
	@Test
	public void EQItemDeserializeJson() {
		String sampleItemB64 = "eyJpZCI6MTAwMDAsIk5hbWUiOiJMYW1iZW50IFN0b25lIiwiYWFnaSI6MCwiYWMiOjAsImFjY3VyYWN5IjowLCJhY2hhIjowLCJhZGV4IjowLCJhaW50IjowLCJhc3RhIjowLCJhc3RyIjowLCJhdHRhY2siOjAsImF2b2lkYW5jZSI6MCwiYXdpcyI6MCwiYmFuZWRtZ2FtdCI6MCwiYmFuZWRtZ3JhY2VhbXQiOjAsImJhbmVkbWdib2R5IjowLCJiYW5lZG1ncmFjZSI6MCwiY2xhc3NlcyI6NjU1MzUsImNvbWJhdGVmZmVjdHMiOiIwIiwiZXh0cmFkbWdza2lsbCI6MCwiZXh0cmFkbWdhbXQiOjAsInByaWNlIjo0NTAwLCJjciI6MCwiZGFtYWdlIjowLCJkYW1hZ2VzaGllbGQiOjAsImRlbGF5IjowLCJkciI6MCwiZWxlbWRtZ3R5cGUiOjAsImVsZW1kbWdhbXQiOjAsImZvY3VzZWZmZWN0IjotMSwiZnIiOjAsImhhc3RlIjowLCJocCI6MCwicmVnZW4iOjAsIml0ZW10eXBlIjoxNywibG9yZSI6IkxhbWJlbnQgU3RvbmUiLCJtYWdpYyI6MSwibWFuYSI6MCwibWFuYXJlZ2VuIjowLCJtciI6MCwicHIiOjAsInJhY2VzIjo2NTUzNSwicmVjbGV2ZWwiOjAsInJlcWxldmVsIjowLCJzbG90cyI6MCwiY2xpY2tlZmZlY3QiOi0xLCJwcm9jZWZmZWN0IjotMSwicHJvY3R5cGUiOjAsInByb2NsZXZlbDIiOjAsInByb2NsZXZlbCI6MCwid29ybmVmZmVjdCI6LTEsIndvcm50eXBlIjowLCJ3b3JubGV2ZWwiOjAsImZvY3VzdHlwZSI6MCwiZm9jdXNsZXZlbDIiOjAsImZvY3VzbGV2ZWwiOjAsInNjcm9sbGVmZmVjdCI6LTEsInNjcm9sbHR5cGUiOjAsInNjcm9sbGxldmVsMiI6MCwic2Nyb2xsbGV2ZWwiOjAsImxvcmVmaWxlIjoiIiwic3Zjb3JydXB0aW9uIjowfQo=";
		EQItem item = JsonUtils.getEQItemFromJson(TextUtils.FromBase64UTF8(sampleItemB64));
        assertEquals(10000, (int)item.getId());
        assertEquals("Lambent Stone", item.getName());
        assertEquals(0, (int)item.getAagi());
		assertEquals(0, (int)item.getAc());
		assertEquals(0, (int)item.getAccuracy());
		assertEquals(0, (int)item.getAcha());
		assertEquals(0, (int)item.getAdex());
		assertEquals(0, (int)item.getAint());
		assertEquals(0, (int)item.getAsta());
		assertEquals(0, (int)item.getAstr());
		assertEquals(0, (int)item.getAttack());
		assertEquals(0, (int)item.getAvoidance());
		assertEquals(0, (int)item.getAwis());
		assertEquals(0, (int)item.getBanedmgamt());
		assertEquals(0, (int)item.getBanedmgraceamt());
		assertEquals(0, (int)item.getBanedmgbody());
		assertEquals(0, (int)item.getBanedmgrace());
		assertEquals(65535, (int)item.getClasses());
		assertEquals("0", item.getCombateffects());
		assertEquals(0, (int)item.getExtradmgskill());
		assertEquals(0, (int)item.getExtradmgamt());
		assertEquals(4500, (int)item.getPrice());
		assertEquals(0, (int)item.getCr());
		assertEquals(0, (int)item.getDamage());
		assertEquals(0, (int)item.getDamageshield());
		assertEquals(0, (int)item.getDelay());
		assertEquals(0, (int)item.getDr());
		assertEquals(0, (int)item.getElemdmgtype());
		assertEquals(0, (int)item.getElemdmgamt());
		assertEquals(-1, (int)item.getFocuseffect());
		assertEquals(0, (int)item.getFr());
		assertEquals(0, (int)item.getHaste());
		assertEquals(0, (int)item.getHp());
		assertEquals(0, (int)item.getRegen());
		assertEquals(17, (int)item.getItemtype());
		assertEquals("Lambent Stone", item.getLore());
		assertEquals(1, (int)item.getMagic());
		assertEquals(0, (int)item.getMana());
		assertEquals(0, (int)item.getManaregen());
		assertEquals(0, (int)item.getMr());
		assertEquals(0, (int)item.getPr());
		assertEquals(65535, (int)item.getRaces());
		assertEquals(0, (int)item.getReclevel());
		assertEquals(0, (int)item.getReqlevel());
		assertEquals(EquipmentSlot.None, item.getSlots());
		assertEquals(-1, (int)item.getClickeffect());
		assertEquals(-1, (int)item.getProceffect());
		assertEquals(0, (int)item.getProctype());
		assertEquals(0, (int)item.getProclevel2());
		assertEquals(0, (int)item.getProclevel());
		assertEquals(-1, (int)item.getWorneffect());
		assertEquals(0, (int)item.getWorntype());
		assertEquals(0, (int)item.getWornlevel());
		assertEquals(0, (int)item.getFocustype());
		assertEquals(0, (int)item.getFocuslevel2());
		assertEquals(0, (int)item.getFocuslevel());
		assertEquals(-1, (int)item.getScrolleffect());
		assertEquals(0, (int)item.getScrolltype());
		assertEquals(0, (int)item.getScrolllevel2());
		assertEquals(0, (int)item.getScrolllevel());
		assertEquals("", item.getLorefile());
		assertEquals(0, (int)item.getSvcorruption());
    }
	
	@Test
	public void CreateSoliniaItemFromJson() {
		String sampleItemB64 = "eyJpZCI6MjI5ODYsIk5hbWUiOiJFZGdlIG9mIEV0ZXJuaXR5IiwiYWFnaSI6MTgsImFjIjoyMCwiYWNjdXJhY3kiOjAsImFjaGEiOjAsImFkZXgiOjI1LCJhaW50IjoxMCwiYXN0YSI6MCwiYXN0ciI6MjAsImF0dGFjayI6MCwiYXZvaWRhbmNlIjowLCJhd2lzIjoxMCwiYmFuZWRtZ2FtdCI6MCwiYmFuZWRtZ3JhY2VhbXQiOjAsImJhbmVkbWdib2R5IjowLCJiYW5lZG1ncmFjZSI6MCwiY2xhc3NlcyI6MzkzLCJjb21iYXRlZmZlY3RzIjoiMCIsImV4dHJhZG1nc2tpbGwiOjAsImV4dHJhZG1nYW10IjowLCJwcmljZSI6MCwiY3IiOjE4LCJkYW1hZ2UiOjIwLCJkYW1hZ2VzaGllbGQiOjAsImRlbGF5IjoyMCwiZHIiOjAsImVsZW1kbWd0eXBlIjowLCJlbGVtZG1nYW10IjowLCJmb2N1c2VmZmVjdCI6LTEsImZyIjoxOCwiaGFzdGUiOjAsImhwIjoxODAsInJlZ2VuIjowLCJpdGVtdHlwZSI6MCwibG9yZSI6IkVkZ2Ugb2YgRXRlcm5pdHkiLCJtYWdpYyI6MSwibWFuYSI6MTY1LCJtYW5hcmVnZW4iOjAsIm1yIjowLCJwciI6MTgsInJhY2VzIjo2NTUzNSwicmVjbGV2ZWwiOjAsInJlcWxldmVsIjo2NSwic2xvdHMiOjI0NTc2LCJjbGlja2VmZmVjdCI6LTEsInByb2NlZmZlY3QiOjM2NDgsInByb2N0eXBlIjowLCJwcm9jbGV2ZWwyIjowLCJwcm9jbGV2ZWwiOjAsIndvcm5lZmZlY3QiOi0xLCJ3b3JudHlwZSI6MCwid29ybmxldmVsIjowLCJmb2N1c3R5cGUiOjAsImZvY3VzbGV2ZWwyIjowLCJmb2N1c2xldmVsIjowLCJzY3JvbGxlZmZlY3QiOi0xLCJzY3JvbGx0eXBlIjowLCJzY3JvbGxsZXZlbDIiOjAsInNjcm9sbGxldmVsIjowLCJsb3JlZmlsZSI6IiIsInN2Y29ycnVwdGlvbiI6MH0K";
		EQItem eqitem = JsonUtils.getEQItemFromJson(TextUtils.FromBase64UTF8(sampleItemB64));
		ISoliniaItem item = null;
		try {
			item = SoliniaItemAdapter.Adapt(eqitem, false);
	        
		} catch (CoreStateInitException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SoliniaItemException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//assertEquals(10000, (int)item.getId());
        assertEquals("Edge of Eternity", item.getDisplayname());
        assertEquals(18, (int)item.getAgility());
		assertEquals(20, (int)item.getAC());
		//assertEquals(0, (int)item.getAccuracyRating());
		assertEquals(0, (int)item.getCharisma());
		assertEquals(25, (int)item.getDexterity());
		assertEquals(10, (int)item.getIntelligence());
		assertEquals(0, (int)item.getStamina());
		assertEquals(20, (int)item.getStrength());
		assertEquals(0, (int)item.getAttack());
		//assertEquals(0, (int)item.getAvoidanceRating);
		assertEquals(10, (int)item.getWisdom());
		assertEquals(0, (int)item.getBaneDmgBody());
		assertEquals(0, (int)item.getBaneDmgBodyAmount());
		assertEquals(0, (int)item.getBaneDmgRace());
		assertEquals(0, (int)item.getBaneDmgRaceAmount());
		assertEquals(0, item.getAllowedRaceNames().size());
		//assertEquals("0", item.getCombateffects());
		//assertEquals(0, (int)item.getExtradmgskill());
		//assertEquals(0, (int)item.getExtradmgamt());
		assertEquals(0, (int)item.getWorth());
		assertEquals(18, (int)item.getColdResist());
		assertEquals(20, (int)item.getDefinedItemDamage());
		//assertEquals(0, (int)item.getDamageshield());
		assertEquals(20, (int)item.getWeaponDelay());
		assertEquals(0, (int)item.getDiseaseResist());
		assertEquals(0, (int)item.getElementalDamageType());
		assertEquals(0, (int)item.getElementalDamageAmount());
		assertEquals(-1, (int)item.getFocusEffectId());
		assertEquals(18, (int)item.getFireResist());
		assertEquals(0, (int)item.getAttackspeed());
		assertEquals(180, (int)item.getHp());
		assertEquals(0, (int)item.getHpregen());
		assertEquals("IRON_SWORD", item.getBasename());
		assertEquals("Edge of Eternity", item.getLore());
		assertEquals(true,item.isMagic());
		assertEquals(165, (int)item.getMana());
		assertEquals(0, (int)item.getMpregen());
		assertEquals(0, (int)item.getMagicResist());
		assertEquals(18, (int)item.getPoisonResist());
		assertEquals(4, item.getAllowedClassNames().size());
		//assertEquals(0, (int)item.getMinLevel());
		assertEquals(65, (int)item.getMinLevel());
		assertEquals(EquipmentSlot.None, item.getEquipmentSlot());
		assertEquals(-1, (int)item.getAbilityid());
		assertEquals(3648, (int)item.getWeaponabilityid());
		//assertEquals(0, (int)item.getProctype());
		//assertEquals(0, (int)item.getProclevel2());
		//assertEquals(0, (int)item.getProclevel());
		//assertEquals(-1, (int)item.getWorneffect());
		//assertEquals(0, (int)item.getWorntype());
		//assertEquals(0, (int)item.getWornlevel());
		//assertEquals(0, (int)item.getFocusType());
		//assertEquals(0, (int)item.getFocuslevel2());
		//assertEquals(0, (int)item.getFocuslevel());
		//assertEquals(-1, (int)item.getScrolleffect());
		//assertEquals(0, (int)item.getScrolltype());
		//assertEquals(0, (int)item.getScrolllevel2());
		//assertEquals(0, (int)item.getScrolllevel());
		assertEquals("", item.getIdentifyMessage());
		//assertEquals(0, (int)item.getSvcorruption());
		
    }
	
	@Test
	public void CreateSoliniaArmourFromJson() {
		String sampleItemB64 = "eyJpZCI6Nzg2OSwiTmFtZSI6IkhlYXZ5IFl0dHJpdW0gQ2hlc3RwbGF0ZSIsImFhZ2kiOjAsImFjIjo3NSwiYWNjdXJhY3kiOjAsImFjaGEiOjAsImFkZXgiOjAsImFpbnQiOjIwLCJhc3RhIjoyMCwiYXN0ciI6MjAsImF0dGFjayI6MTUsImF2b2lkYW5jZSI6MCwiYXdpcyI6MjAsImJhbmVkbWdhbXQiOjAsImJhbmVkbWdyYWNlYW10IjowLCJiYW5lZG1nYm9keSI6MCwiYmFuZWRtZ3JhY2UiOjAsImNsYXNzZXMiOjE1MSwiY29tYmF0ZWZmZWN0cyI6IjAiLCJleHRyYWRtZ3NraWxsIjowLCJleHRyYWRtZ2FtdCI6MCwicHJpY2UiOjAsImNyIjoxNSwiZGFtYWdlIjowLCJkYW1hZ2VzaGllbGQiOjAsImRlbGF5IjowLCJkciI6MTUsImVsZW1kbWd0eXBlIjowLCJlbGVtZG1nYW10IjowLCJmb2N1c2VmZmVjdCI6LTEsImZyIjozMCwiaGFzdGUiOjAsImhwIjoxMjUsInJlZ2VuIjo0LCJpdGVtdHlwZSI6MTAsImxvcmUiOiJIZWF2eSBZdHRyaXVtIENoZXN0cGxhdGUiLCJtYWdpYyI6MSwibWFuYSI6MTI1LCJtYW5hcmVnZW4iOjAsIm1yIjoxNSwicHIiOjMwLCJyYWNlcyI6NjU1MzUsInJlY2xldmVsIjowLCJyZXFsZXZlbCI6MCwic2xvdHMiOjEzMTA3MiwiY2xpY2tlZmZlY3QiOi0xLCJwcm9jZWZmZWN0IjotMSwicHJvY3R5cGUiOjAsInByb2NsZXZlbDIiOjAsInByb2NsZXZlbCI6MCwid29ybmVmZmVjdCI6LTEsIndvcm50eXBlIjowLCJ3b3JubGV2ZWwiOjAsImZvY3VzdHlwZSI6MCwiZm9jdXNsZXZlbDIiOjAsImZvY3VzbGV2ZWwiOjAsInNjcm9sbGVmZmVjdCI6LTEsInNjcm9sbHR5cGUiOjAsInNjcm9sbGxldmVsMiI6MCwic2Nyb2xsbGV2ZWwiOjAsImxvcmVmaWxlIjoiIiwic3Zjb3JydXB0aW9uIjowfQo=";
		EQItem eqitem = JsonUtils.getEQItemFromJson(TextUtils.FromBase64UTF8(sampleItemB64));
		ISoliniaItem item = null;
		try {
			item = SoliniaItemAdapter.Adapt(eqitem, false);
	        
		} catch (CoreStateInitException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SoliniaItemException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
        assertEquals("Heavy Yttrium Chestplate", item.getDisplayname());
		assertEquals(5, item.getAllowedClassNames().size());
		assertEquals(0, (int)item.getMinLevel());
		assertEquals(EquipmentSlot.None, item.getEquipmentSlot());
		assertEquals("IRON_CHESTPLATE", item.getBasename());
		
    }
	
}
