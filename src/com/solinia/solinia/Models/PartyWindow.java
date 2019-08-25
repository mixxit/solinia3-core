package com.solinia.solinia.Models;

import java.util.ArrayList;
import java.util.List;

import com.solinia.solinia.Interfaces.ISoliniaPlayer;

public class PartyWindow {
	public PartyWindowPlayer Me;
	public List<PartyWindowPlayer> PartyMembers = new ArrayList<PartyWindowPlayer>();
	
	public PartyWindow(PartyWindowPlayer me)
	{
		this.Me = me;
	}

	public PartyWindow(ISoliniaPlayer solPlayer) {
		this.Me = new com.solinia.solinia.Models.PartyWindowPlayer(solPlayer.getFullName(),solPlayer.getSoliniaLivingEntity().getHPRatio()/100D,solPlayer.getSoliniaLivingEntity().getManaRatio()/100D);
	}
	
	public void AddPartyMember(ISoliniaPlayer solPlayer)
	{
		this.PartyMembers.add(new com.solinia.solinia.Models.PartyWindowPlayer(solPlayer.getFullName(),solPlayer.getSoliniaLivingEntity().getHPRatio()/100D,solPlayer.getSoliniaLivingEntity().getManaRatio()/100D));
	}
}
