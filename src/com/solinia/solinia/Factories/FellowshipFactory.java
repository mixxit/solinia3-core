package com.solinia.solinia.Factories;

import java.util.UUID;

import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Managers.StateManager;
import com.solinia.solinia.Models.Fellowship;

public class FellowshipFactory {
	public static Fellowship CreateFellowship(int ownerCharacterId) throws CoreStateInitException {
		Fellowship fellowship = new Fellowship();
		fellowship.setId(StateManager.getInstance().getConfigurationManager().getNextFellowshipId());
		fellowship.setOwnerCharacterId(ownerCharacterId);
		fellowship.getMemberCharacterIds().add(ownerCharacterId);
		return StateManager.getInstance().getConfigurationManager().addFellowship(fellowship);
	}
}
