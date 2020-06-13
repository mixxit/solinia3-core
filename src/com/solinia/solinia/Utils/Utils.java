package com.solinia.solinia.Utils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import org.bukkit.Location;
import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Managers.StateManager;
import com.solinia.solinia.Models.DisguisePackage;
import com.solinia.solinia.Models.SoliniaZone;

public class Utils {
	public static final int CALL_FOR_ASSIST_RANGE = 4;

	public static final int HP_REGEN_CAP = 48;
	public static final int MP_REGEN_CAP = 48;
	public static final int MAX_ENTITY_AGGRORANGE = 100;
	public static final int MAX_LIMIT_INCLUDE = 16;
	public static final int DMG_INVULNERABLE = -5;

	public static float clamp(float val, float min, float max) {
		return Math.max(min, Math.min(max, val));
	}
	
	public static final int TICKS_PER_SECOND = 20;
	public static final long MAXDAYTICK = 24000L;
	public static final double MaxRangeForExperience = 100;

	public static final int HIGHESTSKILL = 255;

	public static final int PetAttackMagicLevel = 11;

	public static final int SPELL_UNKNOWN = 0xFFFF;

	public static final double BaseProcChance = 0.0350000001490;

	public static final float ProcDexDivideBy = 11000;

	public static final int MinHastedDelay = 400;

	public static final double MinRangedAttackDist = 2;

	public static final double DefProcPerMinAgiContrib = 0.075;

	public static final float AvgDefProcsPerMinute = 2;

	public static final boolean ClassicMasterWu = false;

	public static final int DMG_RUNE = -6;

	public static final int FRENZY_REUSETIME = 10;

	public static final int HASTE_CAP = 200;

	

	public static String getHttpUrlAsString(String urlLink) {
		try {
			URL url = new URL(urlLink);
			URLConnection con = url.openConnection();
			con.setConnectTimeout(15000);
			con.setReadTimeout(15000);
			con.setRequestProperty("User-Agent",
					"Mozilla/5.0 (Windows; U; Windows NT 6.0; ru; rv:1.9.0.11) Gecko/2009060215 Firefox/3.0.11 (.NET CLR 3.5.30729)");
			con.connect();
			InputStream is = con.getInputStream();
			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));

			StringBuilder response = new StringBuilder();
			String inputLine;

			while ((inputLine = in.readLine()) != null)
				response.append(inputLine);
			is.close();
			in.close();
			return response.toString();
		} catch (Exception e) {
			return "";
		}
	}

	public static boolean isLocationInZone(Location location, SoliniaZone zone) {
		return zone.isLocationInside(location);
	}

	public static boolean isLocationInZone(Location location, int zoneId) {
		if (zoneId < 1)
			return false;
		
		try
		{
			SoliniaZone zone = StateManager.getInstance().getConfigurationManager().getZone(zoneId);
			if (zone == null)
				return false;
			return isLocationInZone(location,zone);
		} catch (CoreStateInitException e)
		{
		}
		
		return false;
		
	}
}
