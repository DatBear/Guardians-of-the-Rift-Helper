package com.datbear;

import net.runelite.api.ItemID;
import net.runelite.client.game.ItemManager;

import java.awt.image.BufferedImage;

public class GuardianInfo {
    public static final GuardianInfo AIR = new GuardianInfo(1, ItemID.AIR_RUNE, 0,false);
    public static final GuardianInfo MIND = new GuardianInfo(2, ItemID.MIND_RUNE, 0,true);
    public static final GuardianInfo WATER = new GuardianInfo(5, ItemID.WATER_RUNE, 0,false);
    public static final GuardianInfo EARTH = new GuardianInfo(9, ItemID.EARTH_RUNE, 0,false);
    public static final GuardianInfo FIRE = new GuardianInfo(14, ItemID.FIRE_RUNE, 26890,false);
    public static final GuardianInfo BODY = new GuardianInfo(20, ItemID.BODY_RUNE, 0,true);
    public static final GuardianInfo COSMIC = new GuardianInfo(27, ItemID.COSMIC_RUNE, 0,true);
    public static final GuardianInfo CHAOS = new GuardianInfo(35, ItemID.CHAOS_RUNE, 0,true);
    public static final GuardianInfo NATURE = new GuardianInfo(44, ItemID.NATURE_RUNE, 0,true);
    public static final GuardianInfo LAW = new GuardianInfo(54, ItemID.LAW_RUNE, 0,true);
    public static final GuardianInfo DEATH = new GuardianInfo(65, ItemID.DEATH_RUNE, 0,true);
    public static final GuardianInfo BLOOD = new GuardianInfo(77, ItemID.BLOOD_RUNE, 0,true);

    public int levelRequired;
    public int runeId;
    public int talismanId;
    public boolean isCatalytic;

    public GuardianInfo(int levelRequired, int runeId, int talismanId, boolean isCatalytic) {
        this.levelRequired = levelRequired;
        this.runeId = runeId;
        this.talismanId = talismanId;
        this.isCatalytic = isCatalytic;
    }

    public BufferedImage getRuneImage(ItemManager itemManager) {
        return itemManager.getImage(runeId);
    }
}