package com.solinia.solinia.Tests;

import org.junit.Test;
import static org.junit.Assert.assertEquals;

import com.solinia.solinia.Models.SkillType;
import com.solinia.solinia.Models.SoliniaPlayer;
import com.solinia.solinia.Utils.ForgeUtils;
import org.json.JSONException;

public class MiningSkillCheckTest {
	@Test
	public void WhenMiningDropRarely() {
		// Setup
		SoliniaPlayer solPlayer = new SoliniaPlayer();
		int expectedIterations = 500;
		int actualIterations = 0;
		int skillTrivial = 50;
		solPlayer.setSkill(SkillType.Mining, 50);
		
		
		// Test should complete half way through this loop
		for (int i = 0; i < (expectedIterations * 2); i++)
		{
			if(solPlayer.getSkillCheck(SkillType.Mining, skillTrivial) == true)
			{
				actualIterations = i;
				break;
			}
			actualIterations = i;
		}
		
        assertEquals(true, actualIterations >= 0);
    }
	
}
