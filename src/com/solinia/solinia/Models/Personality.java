package com.solinia.solinia.Models;

import java.util.ArrayList;
import java.util.List;

import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Managers.StateManager;

public class Personality {
	private int idealId = 0;
	private int bondId = 0;
	private int flawId = 0;
	private int firstTraitId = 0;
	private int secondTraitId = 0;
	private List<String> customPersonalityTraits = new ArrayList<String>();

	public int getIdealId() {
		return idealId;
	}
	public void setIdealId(int idealId) {
		this.idealId = idealId;
	}
	public int getBondId() {
		return bondId;
	}
	public void setBondId(int bondId) {
		this.bondId = bondId;
	}
	public int getFlawId() {
		return flawId;
	}
	public void setFlawId(int flawId) {
		this.flawId = flawId;
	}
	public int getFirstTraitId() {
		return firstTraitId;
	}
	public void setFirstTraitId(int firstTraitId) {
		this.firstTraitId = firstTraitId;
	}
	public int getSecondTraitId() {
		return secondTraitId;
	}
	public void setSecondTraitId(int secondTraitId) {
		this.secondTraitId = secondTraitId;
	}
	
	public Bond getBond()
	{
		try
		{
			return StateManager.getInstance().getConfigurationManager().getBond(this.getBondId());
		} catch (CoreStateInitException e)
		{
			return null;
		}
	}
	
	public Flaw getFlaw()
	{
		try
		{
			return StateManager.getInstance().getConfigurationManager().getFlaw(this.getFlawId());
		} catch (CoreStateInitException e)
		{
			return null;
		}
	}
	
	public Ideal getIdeal()
	{
		try
		{
			return StateManager.getInstance().getConfigurationManager().getIdeal(this.getIdealId());
		} catch (CoreStateInitException e)
		{
			return null;
		}
	}
	
	public Trait getFirstTrait()
	{
		try
		{
			return StateManager.getInstance().getConfigurationManager().getTrait(this.getFirstTraitId());
		} catch (CoreStateInitException e)
		{
			return null;
		}
	}
	
	public Trait getSecondTrait()
	{
		try
		{
			return StateManager.getInstance().getConfigurationManager().getTrait(this.getSecondTraitId());
		} catch (CoreStateInitException e)
		{
			return null;
		}
	}
	
	public List<String> getCustomPersonalityTraits() {
		return customPersonalityTraits;
	}
	public void setCustomPersonalityTraits(List<String> customPersonalityTraits) {
		this.customPersonalityTraits = customPersonalityTraits;
	}
}
