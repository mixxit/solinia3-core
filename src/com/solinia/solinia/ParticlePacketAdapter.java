package com.solinia.solinia;
import org.bukkit.Bukkit;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;


public class ParticlePacketAdapter extends PacketAdapter {

	public ParticlePacketAdapter(AdapterParameteters params) {
        super(params);
    }
   
    @Override
    public void onPacketSending(PacketEvent e) {
    	try
    	{
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
//        Bukkit.broadcastMessage(String.valueOf(packet.getIntegers().read(0))); //2002;
//      
//        Bukkit.broadcastMessage(String.valueOf(packet.getIntegers().read(1))); //16421
//      
//        Bukkit.broadcastMessage(String.valueOf(packet.getIntegers().read(2))); //-5
//        Bukkit.broadcastMessage(String.valueOf(packet.getIntegers().read(3))); //51
//        Bukkit.broadcastMessage(String.valueOf(packet.getIntegers().read(4))); //11
//      
//        Bukkit.broadcastMessage(String.valueOf(packet.getBooleans().read(0))); //false
    }

}
