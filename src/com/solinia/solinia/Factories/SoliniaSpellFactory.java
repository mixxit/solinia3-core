package com.solinia.solinia.Factories;

import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Interfaces.ISoliniaNPC;
import com.solinia.solinia.Interfaces.ISoliniaSpell;
import com.solinia.solinia.Managers.StateManager;
import com.solinia.solinia.Models.SoliniaNPC;
import com.solinia.solinia.Models.SoliniaSpell;

public class SoliniaSpellFactory {

	public static void CreateSpellCopy(int spellid, String name) throws Exception {
		try
		{
			ISoliniaSpell source = StateManager.getInstance().getConfigurationManager().getSpell(spellid);
			
			if (source == null)
				throw new Exception("Source Spell could not be found!");
			
			ISoliniaSpell obj = new SoliniaSpell();
			obj.setId(StateManager.getInstance().getConfigurationManager().getNextSpellId());
			obj.setName(name);
			
			obj.setActivated(source.getActivated());
			obj.setAEDuration(source.getAEDuration());
			obj.setAemaxtargets(source.getAemaxtargets());
			obj.setAllowedClasses(source.getAllowedClasses());
			obj.setAllowrest(source.getAllowrest());
			obj.setAoerange(source.getAoerange());
			obj.setBasediff(source.getBasediff());
			obj.setBonushate(source.getBonushate());
			obj.setBuffduration(source.getBuffduration());
			obj.setBuffdurationformula(source.getBuffdurationformula());
			obj.setCanMgb(source.getCanMgb());
			obj.setCastingAnim(source.getCastingAnim());
			obj.setCastOnOther(source.getCastOnOther());
			obj.setCastOnYou(source.getCastOnYou());
			obj.setCastRestriction(source.getCastRestriction());
			obj.setCastTime(source.getCastTime());
			obj.setClasses1(source.getClasses1());
			obj.setClasses2(source.getClasses2());
			obj.setClasses3(source.getClasses3());
			obj.setClasses4(source.getClasses4());
			obj.setClasses5(source.getClasses5());
			obj.setClasses6(source.getClasses6());
			obj.setClasses7(source.getClasses7());
			obj.setClasses8(source.getClasses8());
			obj.setClasses9(source.getClasses9());
			obj.setClasses10(source.getClasses10());
			obj.setClasses11(source.getClasses11());
			obj.setClasses12(source.getClasses12());
			obj.setClasses13(source.getClasses13());
			obj.setClasses14(source.getClasses14());
			obj.setClasses15(source.getClasses15());
			obj.setClasses16(source.getClasses16());
			obj.setComponentCounts1(source.getComponentCounts1());
			obj.setComponentCounts2(source.getComponentCounts2());
			obj.setComponentCounts3(source.getComponentCounts3());
			obj.setComponentCounts4(source.getComponentCounts4());
			obj.setComponents1(source.getComponents1());
			obj.setComponents2(source.getComponents2());
			obj.setComponents3(source.getComponents3());
			obj.setComponents4(source.getComponents4());
			obj.setConeStartAngle(source.getConeStartAngle());
			obj.setConeStopAngle(source.getConeStopAngle());
			obj.setDeities0(source.getDeities0());
			obj.setDeities1(source.getDeities1());
			obj.setDeities2(source.getDeities2());
			obj.setDeities3(source.getDeities3());
			obj.setDeities4(source.getDeities4());
			obj.setDeities5(source.getDeities5());
			obj.setDeities6(source.getDeities6());
			obj.setDeities7(source.getDeities7());
			obj.setDeities8(source.getDeities8());
			obj.setDeities9(source.getDeities9());
			obj.setDeities10(source.getDeities10());
			obj.setDeities11(source.getDeities11());
			obj.setDeities12(source.getDeities12());
			obj.setDeities13(source.getDeities13());
			obj.setDeities14(source.getDeities14());
			obj.setDeities15(source.getDeities15());
			obj.setDeities16(source.getDeities16());
			obj.setDeleteable(source.getDeleteable());
			obj.setDescnum(source.getDescnum());
			StateManager.getInstance().getConfigurationManager().addSpell(obj);
			
			System.out.println("New Spell Added: " + obj.getId() + " - " + obj.getName());
			StateManager.getInstance().getConfigurationManager().setSpellsChanged(true);

		} catch (CoreStateInitException e)
		{
			
		}
	}

}
