package com.solinia.solinia.Tests;

import org.junit.Test;
import static org.junit.Assert.assertEquals;

import java.util.Arrays;

import com.solinia.solinia.Models.SkillType;
import com.solinia.solinia.Models.SoliniaPlayer;

public class MiningSkillCheckTest {
	@Test
	public void WhenMiningDropRarely() {
		// Setup
		SoliniaPlayer solPlayer = new SoliniaPlayer();
		int expectedIterations = 500;
		double expectedAverage = 500;
		int actualIterations = 0;
		int skillTrivial = 50;
		solPlayer.setSkill(SkillType.Mining, 50);
		
		int[] resultsPerSet = new int[expectedIterations];
		// Perform many sets of iterations and check average
		for (int x = 0; x < (expectedIterations); x++)
		{
			// Test should complete half way through this loop
			for (int i = 0; i < (expectedIterations * 2); i++)
			{
				if(solPlayer.getTradeskillSkillCheck(SkillType.Mining, skillTrivial) == true)
				{
					actualIterations = i;
					break;
				}
				actualIterations = i;
			}
			
			resultsPerSet[x] = actualIterations;
		}
		
		double average = Arrays.stream(resultsPerSet).average().orElse(Double.NaN);
		
        assertEquals(expectedAverage, average, 0);
    }
	
}
