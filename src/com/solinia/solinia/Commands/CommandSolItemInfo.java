package com.solinia.solinia.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import com.solinia.solinia.Adapters.SoliniaItemAdapter;
import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Exceptions.SoliniaItemException;
import com.solinia.solinia.Interfaces.ISoliniaItem;
import com.solinia.solinia.Utils.ItemStackUtils;

public class CommandSolItemInfo implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player))
		{
			sender.sendMessage("This is a player only command");
			return true;
		}
		
		sender.sendMessage("Fetching information about item in main hand");
		Player player = (Player)sender;
		ItemStack itemStack = player.getEquipment().getItemInMainHand();
		player.sendMessage("Material: " + itemStack.getType().toString());
		player.sendMessage("Durability: " + itemStack.getDurability());
		player.sendMessage("Amount: " + itemStack.getAmount());
		
		try
		{
			ISoliniaItem solItem = SoliniaItemAdapter.Adapt(itemStack);
			player.sendMessage("SoliniaItemID: " + solItem.getId());
		} catch (CoreStateInitException e)
		{
			player.sendMessage("SoliniaItemID: " + "Could not fetch information");
		} catch (SoliniaItemException e) {
			// TODO Auto-generated catch block
			player.sendMessage("SoliniaItemID: " + "Not a solinia item");
		}
		
		if (ItemStackUtils.getSoliniaItemId(itemStack) != null)
			player.sendMessage("SoliniaItemId (Tag):" + ItemStackUtils.getSoliniaItemId(itemStack));

		if (ItemStackUtils.getSoliniaLastUpdated(itemStack) != null)
			player.sendMessage("SoliniaLastUpdated (Tag):" + ItemStackUtils.getSoliniaLastUpdated(itemStack));

		if (ItemStackUtils.getAugmentationItemId(itemStack) != null)
			player.sendMessage("SoliniaAugmentationItemId (Tag):" + ItemStackUtils.getAugmentationItemId(itemStack));

		
		if (args.length > 0 && args[0].equals("write"))
		{
			String fileData = ItemStackUtils.itemStackToYamlString(itemStack);
			player.sendMessage(fileData);
		}

		return true;
	}
}
