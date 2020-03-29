package com.solinia.solinia.Tests;

import org.junit.Test;
import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.solinia.solinia.Exceptions.InvalidPacketException;
import com.solinia.solinia.Models.PacketOpenCharacterCreation;
import com.solinia.solinia.Models.RaceChoice;

public class PacketOpenCharacterCreationTest {
	@Test
	public void WhenGivenPacketDataReturnExpectedFormat() {
		String testForPacketData = "1|1|Meroei|Druid|MER|DRU|raceDesc|classDesc|"+"1|2|3|4|5|6|7";
		String foundPacketData = "";
		try {
			PacketOpenCharacterCreation vitals = new PacketOpenCharacterCreation();
			vitals.fromPacketData(testForPacketData);
			foundPacketData = vitals.toPacketData();
		} catch (InvalidPacketException e) {
			e.printStackTrace();
		}
        assertEquals(testForPacketData, foundPacketData);
    }
	
	@Test
	public void WhenGivenPacketDataReturnsAllValues() {
		String testPacketData = "";
		boolean first = true;
		
		List<String> races = new ArrayList<String>();
		races.add("Meroei");
		races.add("Caerthain");
		List<String> classes = new ArrayList<String>();
		classes.add("Druid");
		classes.add("Paladin");
		
		for (int i = 1; i <= races.size(); i++)
		{
			for(int i2 =1; i2 <= classes.size(); i2++ )
			{
				if (first)
					first = false;
				else
					testPacketData += "^";
				
				String raceName = races.get(i-1);
				String className = classes.get(i2-1);
				testPacketData += i+"|"+i2+"|"+raceName+"|"+className+"|"+raceName.substring(0,3)+"|"+className.substring(0,3)+"|raceDesc|classDesc|"+"1|2|3|4|5|6|7";
			}
		}
		
		try {
			PacketOpenCharacterCreation vitals = new PacketOpenCharacterCreation();
			vitals.fromPacketData(testPacketData);
			
			for(Map.Entry<String, RaceChoice> entry : vitals.getCharacterCreation().raceChoices.entrySet())
			{
				assertEquals(true,entry.getValue().RaceId>0);
				assertEquals(true,entry.getValue().ClassId>0);

				assertEquals(true,entry.getValue().RaceName.length()>0);
				assertEquals(true,entry.getValue().ClassName.length()>0);
				assertEquals(true,entry.getValue().RaceShort.length()>0);
				assertEquals(true,entry.getValue().ClassShort.length()>0);
				assertEquals(true,entry.getValue().RaceDescription.length()>0);
				assertEquals(true,entry.getValue().ClassDescription.length()>0);
				assertEquals(true,entry.getValue().STR>0);
				assertEquals(true,entry.getValue().STA>0);
				assertEquals(true,entry.getValue().AGI>0);
				assertEquals(true,entry.getValue().DEX>0);
				assertEquals(true,entry.getValue().INT>0);
				assertEquals(true,entry.getValue().WIS>0);
				assertEquals(true,entry.getValue().CHA>0);
				
			}
		} catch (InvalidPacketException e) {
			e.printStackTrace();
		}
    }
}
