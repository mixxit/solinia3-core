package com.solinia.solinia.Races;

import net.minecraft.server.v1_14_R1.VillagerProfession;
import org.bukkit.entity.Villager;

/**
 * Created by Redned on 4/27/2019.
 */
public class VillagerUtils {

    public static VillagerProfession fromBukkitProfession(Villager.Profession x) {
        VillagerProfession profession = null;

        switch (x) {
            case NONE:
                profession = VillagerProfession.NONE;
                break;
            case ARMORER:
                profession = VillagerProfession.ARMORER;
                break;
            case BUTCHER:
                profession = VillagerProfession.BUTCHER;
                break;
            case CARTOGRAPHER:
                profession = VillagerProfession.CARTOGRAPHER;
                break;
            case CLERIC:
                profession = VillagerProfession.CLERIC;
                break;
            case FARMER:
                profession = VillagerProfession.FARMER;
                break;
            case FISHERMAN:
                profession = VillagerProfession.FISHERMAN;
                break;
            case FLETCHER:
                profession = VillagerProfession.FLETCHER;
                break;
            case LEATHERWORKER:
                profession = VillagerProfession.LEATHERWORKER;
                break;
            case LIBRARIAN:
                profession = VillagerProfession.LIBRARIAN;
                break;
            case MASON:
                profession = VillagerProfession.NITWIT;
                break;
            case SHEPHERD:
                profession = VillagerProfession.SHEPHERD;
                break;
            case TOOLSMITH:
                profession = VillagerProfession.TOOLSMITH;
                break;
            case WEAPONSMITH:
                profession = VillagerProfession.WEAPONSMITH;
                break;
            default:
                profession = VillagerProfession.NONE;
                break;
        }

        return profession;
    }
}