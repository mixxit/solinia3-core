package com.solinia.solinia.Interfaces;

import java.util.List;

import org.bukkit.command.CommandSender;

import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Exceptions.InvalidNPCMerchantListSettingException;

public interface ISoliniaNPCMerchant {

	int getId();

	void setId(int id);

	String getName();

	void setName(String name);

	List<ISoliniaNPCMerchantEntry> getEntries();

	void setEntries(List<ISoliniaNPCMerchantEntry> entries);

	void editSetting(String setting, String value) throws InvalidNPCMerchantListSettingException, NumberFormatException, CoreStateInitException;

	void sendMerchantSettingsToSender(CommandSender sender);

	boolean isOperatorCreated();

	void setOperatorCreated(boolean operatorCreated);

	boolean isPublishedBookStore();

	void setPublishedBookStore(boolean publishedBookStore);

	String getRequiresPermissionNode();

	void setRequiresPermissionNode(String requiresPermissionNode);
}
