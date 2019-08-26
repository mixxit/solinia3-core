package com.solinia.solinia.Utils;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import org.bukkit.craftbukkit.v1_14_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

import io.netty.buffer.Unpooled;
import net.minecraft.server.v1_14_R1.MinecraftKey;
import net.minecraft.server.v1_14_R1.PacketDataSerializer;
import net.minecraft.server.v1_14_R1.PacketPlayOutCustomPayload;

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

}
