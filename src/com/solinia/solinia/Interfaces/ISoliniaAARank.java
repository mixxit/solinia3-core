package com.solinia.solinia.Interfaces;

import java.util.List;

import com.solinia.solinia.Models.SoliniaAAPrereq;
import com.solinia.solinia.Models.SoliniaAARankEffect;

public interface ISoliniaAARank {

	int getLevel_req();

	void setLevel_req(int level_req);

	int getId();

	void setId(int id);

	int getCost();

	void setCost(int cost);

	int getSpell();

	void setSpell(int spell);

	int getSpell_type();

	void setSpell_type(int spell_type);

	int getRecast_time();

	void setRecast_time(int recast_time);

	int getExpansion();

	void setExpansion(int expansion);

	int getPosition();

	void setPosition(int position);

	int getAbilityid();

	void setAbilityid(int abilityid);

	List<SoliniaAAPrereq> getPrereqs();

	void setPrereqs(List<SoliniaAAPrereq> prereqs);

	List<SoliniaAARankEffect> getEffects();

	void setEffects(List<SoliniaAARankEffect> effects);

}
