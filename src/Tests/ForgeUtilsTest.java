package Tests;

import org.junit.Test;
import static org.junit.Assert.assertEquals;

import com.solinia.solinia.Utils.ForgeUtils;
import org.json.JSONException;

public class ForgeUtilsTest {
	@Test
	public void WhenGettingURLReturnExpectedString() {
		String expectedVersion = "1.14.4.20190902195348";
		String actualVersion = "";
		
		String versionJsonString = "{\r\n" + 
				"  \"homepage\": \"https://github.com/mixxit/solinia3-ui/releases\",\r\n" + 
				"  \"promos\": {\r\n" + 
				"    \"1.14.4-recommended\": \"1.14.4.20190902195348\"\r\n" + 
				"  }\r\n" + 
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
