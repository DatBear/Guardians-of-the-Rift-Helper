package com.datbear;

import com.google.common.collect.ImmutableSet;
import net.runelite.api.ItemID;
import net.runelite.client.game.ItemManager;

import java.awt.image.BufferedImage;
import java.time.Instant;
import java.util.Optional;
import java.util.Set;

public class GuardianInfo {
    public static final GuardianInfo AIR = new GuardianInfo(1, ItemID.AIR_RUNE, 0,4353, false);
    public static final GuardianInfo MIND = new GuardianInfo(2, ItemID.MIND_RUNE, 0,4354, true);
    public static final GuardianInfo WATER = new GuardianInfo(5, ItemID.WATER_RUNE, 0,4355, false);
    public static final GuardianInfo EARTH = new GuardianInfo(9, ItemID.EARTH_RUNE, 0,4356, false);
    public static final GuardianInfo FIRE = new GuardianInfo(14, ItemID.FIRE_RUNE, 26890,4357, false);
    public static final GuardianInfo BODY = new GuardianInfo(20, ItemID.BODY_RUNE, 0,4358, true);
    public static final GuardianInfo COSMIC = new GuardianInfo(27, ItemID.COSMIC_RUNE, 0, 4359, true);
    public static final GuardianInfo CHAOS = new GuardianInfo(35, ItemID.CHAOS_RUNE, 0,4360, true);
    public static final GuardianInfo NATURE = new GuardianInfo(44, ItemID.NATURE_RUNE, 0, 4361, true);
    public static final GuardianInfo LAW = new GuardianInfo(54, ItemID.LAW_RUNE, 0,4362, true);
    public static final GuardianInfo DEATH = new GuardianInfo(65, ItemID.DEATH_RUNE, 0,4363, true);
    public static final GuardianInfo BLOOD = new GuardianInfo(77, ItemID.BLOOD_RUNE, 0,4364, true);

    public static final Set<GuardianInfo> ALL = ImmutableSet.of(AIR, MIND, WATER, EARTH, FIRE, BODY, COSMIC, CHAOS, NATURE, LAW, DEATH, BLOOD);

    public int levelRequired;
    public int runeId;
    public int talismanId;
    public int spriteId;
    public boolean isCatalytic;

    public Optional<Instant> spawnTime = Optional.empty();

    public GuardianInfo(int levelRequired, int runeId, int talismanId, int spriteId, boolean isCatalytic) {
        this.levelRequired = levelRequired;
        this.runeId = runeId;
        this.talismanId = talismanId;
        this.spriteId = spriteId;
        this.isCatalytic = isCatalytic;
    }

    public BufferedImage getRuneImage(ItemManager itemManager) {
        return itemManager.getImage(runeId);
    }

    public void spawn(){
        spawnTime = Optional.of(Instant.now());
    }

    public void despawn(){
        spawnTime = Optional.empty();
    }
}