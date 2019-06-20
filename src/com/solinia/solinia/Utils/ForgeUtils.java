package com.solinia.solinia.Utils;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.nio.charset.StandardCharsets;

import org.bukkit.craftbukkit.v1_14_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

import io.netty.buffer.Unpooled;
import net.minecraft.server.v1_14_R1.MinecraftKey;
import net.minecraft.server.v1_14_R1.PacketDataSerializer;
import net.minecraft.server.v1_14_R1.PacketPlayOutCustomPayload;

public class ForgeUtils {

	public static void sendForgeMessage(Player player, String channelName, int discriminator, String message) throws Exception {
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		DataOutputStream dataOut = new DataOutputStream(stream);

		try
		{
			dataOut.writeInt(discriminator);
			dataOut.write(message.getBytes(StandardCharsets.UTF_8));
			
			PacketPlayOutCustomPayload packet = new PacketPlayOutCustomPayload(new MinecraftKey(channelName), new PacketDataSerializer(Unpooled.wrappedBuffer(stream.toByteArray())));
			((CraftPlayer)player).getHandle().playerConnection.sendPacket(packet);
		
		} finally {
			dataOut.close();
			stream.close();
		}
	}

}
