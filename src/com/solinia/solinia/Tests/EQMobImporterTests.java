package com.solinia.solinia.Tests;

import org.junit.Test;

import com.solinia.solinia.Adapters.SoliniaNPCAdapter;
import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Interfaces.ISoliniaNPC;
import com.solinia.solinia.Models.EQMob;
import com.solinia.solinia.Utils.EQUtils;
import com.solinia.solinia.Utils.JsonUtils;
import com.solinia.solinia.Utils.TextUtils;
import static org.junit.Assert.assertEquals;

public class EQMobImporterTests {
	@Test
	public void EQMobDeserializeJson() {
		String sampleMobB64 = "eyJpZCI6NTAwLCJuYW1lIjoiQW5pbWF0aW9uMSIsImNsYXNzIjoxLCJsZXZlbCI6MSwiaHAiOjExLCJocF9yZWdlbl9yYXRlIjoyLCJtYW5hIjowLCJtYW5hX3JlZ2VuX3JhdGUiOjAsIm5wY19zcGVsbHNfaWQiOjAsIm1pbmRtZyI6MSwibWF4ZG1nIjo4LCJNUiI6MCwiQ1IiOjQwMCwiRFIiOjAsIkZSIjowLCJQUiI6MCwiQ29ycnVwIjowLCJQaFIiOjAsInNlZV9pbnZpcyI6MCwiQUMiOjAsImF0dGFja19zcGVlZCI6MCwiYXR0YWNrX2RlbGF5IjozMCwiU1RSIjo3NSwiU1RBIjo3NSwiREVYIjo3NSwiQUdJIjo5MCwiX0lOVCI6MTAsIldJUyI6MTAsIkNIQSI6NzUsIkFUSyI6MCwiQWNjdXJhY3kiOjAsIkF2b2lkYW5jZSI6MCwibnBjc3BlY2lhbGF0dGtzIjoiYmYiLCJzcGVjaWFsX2FiaWxpdGllcyI6IjksMV4yMSwxIn0K";
		EQMob mob = JsonUtils.getEQMobFromJson(TextUtils.FromBase64UTF8(sampleMobB64));
        assertEquals("Animation1", mob.getName());
    }

	@Test
	public void CreateSoliniaNPCFromJson() {
		String sampleMobB64 = "eyJpZCI6NTAwLCJuYW1lIjoiQW5pbWF0aW9uMSIsImNsYXNzIjoxLCJsZXZlbCI6MSwiaHAiOjExLCJocF9yZWdlbl9yYXRlIjoyLCJtYW5hIjowLCJtYW5hX3JlZ2VuX3JhdGUiOjAsIm5wY19zcGVsbHNfaWQiOjAsIm1pbmRtZyI6MSwibWF4ZG1nIjo4LCJNUiI6MCwiQ1IiOjQwMCwiRFIiOjAsIkZSIjowLCJQUiI6MCwiQ29ycnVwIjowLCJQaFIiOjAsInNlZV9pbnZpcyI6MCwiQUMiOjAsImF0dGFja19zcGVlZCI6MCwiYXR0YWNrX2RlbGF5IjozMCwiU1RSIjo3NSwiU1RBIjo3NSwiREVYIjo3NSwiQUdJIjo5MCwiX0lOVCI6MTAsIldJUyI6MTAsIkNIQSI6NzUsIkFUSyI6MCwiQWNjdXJhY3kiOjAsIkF2b2lkYW5jZSI6MCwibnBjc3BlY2lhbGF0dGtzIjoiYmYiLCJzcGVjaWFsX2FiaWxpdGllcyI6IjksMV4yMSwxIn0K";
		EQMob mob = JsonUtils.getEQMobFromJson(TextUtils.FromBase64UTF8(sampleMobB64));
		
		ISoliniaNPC npc = null;
		try {
			npc = SoliniaNPCAdapter.Adapt(mob, false);
	        
		} catch (CoreStateInitException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		assertEquals("Animation1", npc.getName());
        assertEquals(1, EQUtils.getSolClassIdFromEQClassId(npc.getClassid()));
        assertEquals(1, npc.getLevel());
        assertEquals(11, npc.getForcedMaxHp());
        assertEquals(2, npc.getNPCHPRegen());
        assertEquals(0, npc.getNPCMana());
        assertEquals(0, npc.getNPCMPRegen());
        assertEquals(0, npc.getNpcSpellList());
        assertEquals(1, npc.getMinInternalDmg());
        assertEquals(8, npc.getMaxInternalDmg());
        assertEquals(0, npc.getInternalMagicresist());
        assertEquals(400, npc.getInternalColdresist());
        assertEquals(0, npc.getInternalDiseaseresist());
        assertEquals(0, npc.getInternalFireresist());
        assertEquals(0, npc.getInternalPoisonresist());
        assertEquals(0, npc.getAC());
        assertEquals(0, npc.getNPCDefaultAtk());
        assertEquals(0, npc.getAccuracyRating());
        assertEquals(0, npc.getAvoidanceRating());
    }
}
