package com.solinia.solinia.Utils;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import org.bukkit.craftbukkit.v1_15_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

import org.json.JSONException;
import org.json.JSONObject;

import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Managers.StateManager;
import com.solinia.solinia.Models.Solinia3UIPacketDiscriminators;

import io.netty.buffer.Unpooled;
import net.minecraft.server.v1_15_R1.MinecraftKey;
import net.minecraft.server.v1_15_R1.PacketDataSerializer;
import net.minecraft.server.v1_15_R1.PacketPlayOutCustomPayload;

public class ForgeUtils {

	public static void sendForgeMessage(Player player, String channelName, byte discriminator, String message) throws Exception {
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		DataOutputStream dataOut = new DataOutputStream(stream);

		try
		{
			// diesieben07 - Forge uses an unsigned byte for the discriminator, for a start
			//dataOut.writeInt(discriminator);
			dataOut.writeByte(discriminator);
			// diesieben07 - But you should really send some kind of length prefix
			//and then only read that much of the string
			//You're already using DataOuput, it has writeUTFString
			//dataOut.write(message.getBytes(StandardCharsets.UTF_8));
			dataOut.writeUTF(message);

			PacketPlayOutCustomPayload packet = new PacketPlayOutCustomPayload(new MinecraftKey(channelName), new PacketDataSerializer(Unpooled.wrappedBuffer(stream.toByteArray())));
			((CraftPlayer)player).getHandle().playerConnection.sendPacket(packet);
		
		} finally {
			dataOut.close();
			stream.close();
		}
	}
	
	public static String fetchExpectedForgeClientModVersion() throws JSONException, IOException
	{
		String urljson = readFromUrl("https://raw.githubusercontent.com/mixxit/solinia3-ui/gh-pages/versions.json");
		return getVersionFromJsonString(urljson);
	}
	
	public static String getVersionFromJsonString(String jsonText)
	{
		JSONObject json = new JSONObject(jsonText);
		JSONObject promos = (JSONObject)json.get("promos");
		ArrayList<String> list = new ArrayList<String>(promos.keySet());
	    return promos.getString(list.get(0));
	}
	
	private static String readAll(Reader rd) throws IOException {
	    StringBuilder sb = new StringBuilder();
	    int cp;
	    while ((cp = rd.read()) != -1) {
	      sb.append((char) cp);
	    }
	    return sb.toString();
	  }

	  public static String readFromUrl(String url) throws IOException, JSONException {
	    InputStream is = new URL(url).openStream();
	    try {
	      BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
	      String string = readAll(rd);
	      
	      return string;
	    } finally {
	      is.close();
	    }
	  }

	public static void QueueSendForgeMessage(Player player, String channelName, byte packetDiscriminator, String packetData,int specialQueueNumber) {
		try
		{
			switch(packetDiscriminator)
			{
				case Solinia3UIPacketDiscriminators.CASTINGPERCENT:
					StateManager.getInstance().getConfigurationManager().getQueuedCastingPercentPackets().put(player.getUniqueId(),packetData);
					break;
				case Solinia3UIPacketDiscriminators.CHARCREATION:
					StateManager.getInstance().getConfigurationManager().getQueuedCharCreationPackets().put(player.getUniqueId(),packetData);
					break;
				case Solinia3UIPacketDiscriminators.EFFECTS:
					StateManager.getInstance().getConfigurationManager().getQueuedEffectsPackets().put(player.getUniqueId(),packetData);
					break;
				case Solinia3UIPacketDiscriminators.EQUIPSLOTS:
					StateManager.getInstance().getConfigurationManager().getQueuedEquipSlotsPackets().put(player.getUniqueId(),packetData);
					break;
				case Solinia3UIPacketDiscriminators.MEMORISEDSPELLS:
					StateManager.getInstance().getConfigurationManager().getQueuedMemorisedSpellsPackets().put(player.getUniqueId(),packetData);
					break;
				case Solinia3UIPacketDiscriminators.SPELLBOOKPAGE:
					StateManager.getInstance().getConfigurationManager().getQueueSpellbookPagePackets().put(player.getUniqueId(),packetData);
					break;
				case Solinia3UIPacketDiscriminators.VITALS:
					try
					{
					StateManager.getInstance().getConfigurationManager().getQueueMobVitalsPackets(specialQueueNumber).put(player.getUniqueId(),packetData);
					} catch (Exception e)
					{
						e.printStackTrace();
					}
					break;
				default:
				break;
			}
		} catch (CoreStateInitException e)
		{
			
		}
	}

}
