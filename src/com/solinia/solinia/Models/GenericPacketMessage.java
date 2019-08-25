package com.solinia.solinia.Models;

import java.util.UUID;

import org.bukkit.entity.LivingEntity;

import com.solinia.solinia.Adapters.SoliniaLivingEntityAdapter;
import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Interfaces.ISoliniaLivingEntity;
import com.solinia.solinia.Interfaces.ISoliniaPlayer;
import com.solinia.solinia.Managers.StateManager;

import net.md_5.bungee.api.ChatColor;

public class GenericPacketMessage {
	public MemorisedSpells MemorisedSpellSlots = null;
	public SpellbookPage SpellbookPage = null;
	public PartyWindow PartyWindow = null;
	public double CastingProgress = 0.0D;
	public UUID TargetUUID = null;
	public String TargetName = null;
	public double TargetHealthPercent = 0.0D;
	
	public GenericPacketMessage(ISoliniaPlayer targetPacketPlayer)
	{
		try
		{
			// Has a target set
			LivingEntity entityTarget = StateManager.getInstance().getEntityManager().getEntityTarget(targetPacketPlayer.getBukkitPlayer());
			
			if (entityTarget != null) {
				ISoliniaLivingEntity solLivingEntity = SoliniaLivingEntityAdapter.Adapt(entityTarget);
				if (solLivingEntity != null) {
					setTargetUUID(entityTarget.getUniqueId());
					setTargetName(solLivingEntity.getLevelCon(targetPacketPlayer.getSoliniaLivingEntity()) + entityTarget.getCustomName() + ChatColor.RESET);
					setTargetHealthPercent(solLivingEntity.getHPRatio()/100D);
				}
			}
			
			this.setCastingProgress(targetPacketPlayer.getCastingProgress());
		} catch (CoreStateInitException e)
		{
			
		}
	}
	
	public void setTargetHealthPercent(double healthPercent)
	{
		this.TargetHealthPercent = healthPercent;
	}

	public void setMemorisedSpellSlots(MemorisedSpells memorisedSpellSlots) {
		this.MemorisedSpellSlots = memorisedSpellSlots;
	}
	
	public void setSpellbookPage(SpellbookPage spellbookPage) {
		this.SpellbookPage = spellbookPage;
	}
	
	public void setPartyWindow(PartyWindow partyWindow) {
		this.PartyWindow = partyWindow;
	}
	
	public void setCastingProgress(double castingProgress) {
		this.CastingProgress = castingProgress;
	}
	public void setTargetUUID(UUID targetUUID) {
		this.TargetUUID = targetUUID;
	}
	public void setTargetName(String targetName)
	{
		this.TargetName = targetName;
	}

}
