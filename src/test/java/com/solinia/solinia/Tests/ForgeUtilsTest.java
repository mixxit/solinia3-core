package com.solinia.solinia.Tests;

import org.junit.Test;
import static org.junit.Assert.assertEquals;

import com.solinia.solinia.Utils.ForgeUtils;
import org.json.JSONException;

public class ForgeUtilsTest {
	@Test
	public void WhenGettingURLReturnExpectedString() {
		String expectedVersion = "1.14.4.20190902195348";
		String actualVersion = "";
		
		String versionJsonString = "{\r" + System.lineSeparator() + 
				"  \"homepage\": \"https://github.com/mixxit/solinia3-ui/releases\",\r" + System.lineSeparator() + 
				"  \"promos\": {\r" + System.lineSeparator() + 
				"    \"1.14.4-recommended\": \"1.14.4.20190902195348\"\r" + System.lineSeparator() + 
				"  }\r" + System.lineSeparator() + 
				"}";
		
		try {
			actualVersion = ForgeUtils.getVersionFromJsonString(versionJsonString);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        assertEquals(expectedVersion, actualVersion);
    }
	
}
