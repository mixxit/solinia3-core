package com.solinia.solinia.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Interfaces.ISoliniaPlayer;
import com.solinia.solinia.Managers.StateManager;
import com.solinia.solinia.Models.Fellowship;
import com.solinia.solinia.Models.PlayerState;

public class PatchUtils {
	// Used for one off patching, added in /solinia patch command for console sender
	public static void Patcher() {
		try
		{
			for (Fellowship entry : StateManager.getInstance().getConfigurationManager().getFellowships())
			{
				if (entry.getOwnerUuid() == null)
					continue;
				
				ISoliniaPlayer player = StateManager.getInstance().getConfigurationManager().getCharacterByCharacterUUID(entry.getOwnerUuid());
				if (player == null)
					continue;
				
				entry.setOwnerCharacterId(player.getId());
				
				List<Integer> members = new ArrayList<Integer>();
				
				for(UUID memberUuid : entry.getMembers())
				{
					ISoliniaPlayer member = StateManager.getInstance().getConfigurationManager().getCharacterByCharacterUUID(memberUuid);
					if (player == null)
						continue;
					
					if (!members.contains(member.getId()))
						members.add(member.getId());
				}
					
				entry.setMemberCharacterIds(members);
				
				System.out.println("Updated");
			}
			
			
		} catch (CoreStateInitException e)
		{
			
		}
		
	}
}
