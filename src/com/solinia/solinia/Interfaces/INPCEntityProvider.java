package com.solinia.solinia.Interfaces;

public interface INPCEntityProvider {

	void updateNpc(ISoliniaNPC npc);

	void updateSpawnGroup(ISoliniaSpawnGroup spawnGroup);

	void reloadProvider();

	void removeSpawnGroup(ISoliniaSpawnGroup group);

	void spawnNPC(ISoliniaNPC npc, int amount, String world, int x, int y, int z);

}
