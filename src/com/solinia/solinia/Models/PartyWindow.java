package com.solinia.solinia.Models;

import java.util.ArrayList;
import java.util.List;

import com.solinia.solinia.Interfaces.ISoliniaPlayer;

public class PartyWindow {
	public PartyWindowPlayer Me;
	public List<PartyWindowPlayer> PartyMembers;
	
	public PartyWindow(PartyWindowPlayer me)
	{
		this.Me = me;
	}

	public PartyWindow(ISoliniaPlayer solPlayer) {
		this.Me = new com.solinia.solinia.Models.PartyWindowPlayer(solPlayer.getFullName(),solPlayer.getSoliniaLivingEntity().getHPRatio()/100D,solPlayer.getSoliniaLivingEntity().getManaRatio()/100D);
		System.out.println("Debug: mana ratio found to be: " + solPlayer.getSoliniaLivingEntity().getManaRatio());
	}
	
	public void AddPartyMember(ISoliniaPlayer solPlayer)
	{
		if (this.PartyMembers == null)
			this.PartyMembers = new ArrayList<PartyWindowPlayer>();
		this.PartyMembers.add(new com.solinia.solinia.Models.PartyWindowPlayer(solPlayer.getFullName(),solPlayer.getSoliniaLivingEntity().getHPRatio()/100D,solPlayer.getSoliniaLivingEntity().getManaRatio()/100D));
	}
}
