package com.solinia.solinia;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.WrappedWatchableObject;
import com.solinia.solinia.Adapters.SoliniaPlayerAdapter;
import com.solinia.solinia.Interfaces.ISoliniaPlayer;
import com.solinia.solinia.Managers.StateManager;
import com.solinia.solinia.Models.SpellEffectType;


public class ParticlePacketAdapter extends PacketAdapter {

	public ParticlePacketAdapter(AdapterParameteters params) {
        super(params);
    }
   
    @Override
    public void onPacketSending(PacketEvent e) {
    	try
    	{
			if (e.getPacket().getType().equals(PacketType.Play.Server.ENTITY_METADATA))
			{
				if (e.getPlayer() != null)
				{
					ISoliniaPlayer solPlayer = SoliniaPlayerAdapter.Adapt(e.getPlayer());
					if (solPlayer != null && solPlayer.getSoliniaLivingEntity() != null && solPlayer.getSoliniaLivingEntity().hasSpellEffectType(SpellEffectType.SeeInvis))
					{
						if (e.getPacket().getWatchableCollectionModifier() != null && e.getPacket().getWatchableCollectionModifier().size() > 0)
			    		{
							// We only want packet index 0 for this type
							List<WrappedWatchableObject> packet = e.getPacket().getWatchableCollectionModifier().read(0);
							if (packet.size() > 0 && packet.get(0) != null && packet.get(0).getIndex() == 0)
							{
								Byte entityindex0bitmask = (Byte) packet.get(0).getValue();
								// invis status is in bit position 6 (counting from 0 is 5)
								// see : https://wiki.vg/Entity_metadata#Entity_Metadata_Format
								boolean invis = ((entityindex0bitmask & (1<<5)) != 0);
								if (invis)
								{
							    	e.setCancelled(true);
							    	return;
								}
							}
			    		}
					}
				}
			}
			
	    	if (e.getPacketType().equals(PacketType.Play.Server.ANIMATION))
	    	{
	    		if (e.getPacket().getIntegers().size() < 2)
	    			return;
	    		
	    		// Original arm swing, we use our own
	    		if (e.getPacket().getIntegers().read(1) == 0)
	    			if (!e.getPacket().getMeta("sol").isPresent())
	    				e.setCancelled(true);
		    	return;
	    	}
    	} catch (Exception eMain)
    	{
    		eMain.printStackTrace();
    	}
    	
        /*PacketContainer packet = Solinia3CorePlugin.getProtocolManager().createPacket(PacketType.Play.Server.WORLD_EVENT);
        packet.getIntegers().write(0, 2002);
        packet.getIntegers().write(1, 16421);
        packet.getIntegers().write(2, e.getPacket().getIntegers().read(2));
        packet.getIntegers().write(3, e.getPacket().getIntegers().read(3));
        packet.getIntegers().write(4, e.getPacket().getIntegers().read(4));
        packet.getBooleans().write(0, false);
        e.setCancelled(true);
        try {
        	Solinia3CorePlugin.getProtocolManager().sendServerPacket(e.getPlayer(), packet);
        } catch (InvocationTargetException ex) {
            ex.printStackTrace();
        }*/
    }

}
