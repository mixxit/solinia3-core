package com.solinia.solinia.Interfaces;

import java.util.List;
import java.util.UUID;

import org.bukkit.command.CommandSender;

import com.solinia.solinia.Models.SoliniaAlignmentChunk;
import com.solinia.solinia.Models.SoliniaChunk;

public interface ISoliniaAlignment {

	int getId();

	void setId(int id);

	String getName();

	void setName(String name);

	UUID getEmperor();

	void setEmperor(UUID emperor);

	int getCoffers();

	void setCoffers(int coffers);

	int getUpkeepCost();

	void sendAlignmentStats(CommandSender sender);

	int getTotalMaterialChunks();

	SoliniaAlignmentChunk getChunk(SoliniaChunk chunk);

	int getTotalTradePosts();

	void setChunk(String worldChunkPositionCode, SoliniaAlignmentChunk alignmentChunk);

	void removeChunk(SoliniaChunk chunk);

	int getTotalChunks();

	List<SoliniaAlignmentChunk> getMaterialChunks();

}
