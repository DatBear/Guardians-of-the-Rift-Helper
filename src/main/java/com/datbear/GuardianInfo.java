package com.datbear;

import com.google.common.collect.ImmutableSet;
import net.runelite.api.ItemID;
import net.runelite.client.config.Notification;
import net.runelite.client.game.ItemManager;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.time.Instant;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

public class GuardianInfo {
    public static final GuardianInfo AIR = new GuardianInfo("Air", 43701, 1, ItemID.AIR_RUNE, 26887, 4353, false, CellType.Weak, x -> x.notifyAirGuardian());
    public static final GuardianInfo MIND = new GuardianInfo("Mind", 43705, 2, ItemID.MIND_RUNE, 26891, 4354, true, CellType.Weak, x -> x.notifyMindGuardian());
    public static final GuardianInfo WATER = new GuardianInfo("Water", 43702, 5, ItemID.WATER_RUNE, 26888, 4355, false, CellType.Medium, x -> x.notifyWaterGuardian());
    public static final GuardianInfo EARTH = new GuardianInfo("Earth", 43703, 9, ItemID.EARTH_RUNE, 26889, 4356, false, CellType.Strong, x -> x.notifyEarthGuardian());
    public static final GuardianInfo FIRE = new GuardianInfo("Fire", 43704, 14, ItemID.FIRE_RUNE, 26890, 4357, false, CellType.Overcharged, x -> x.notifyFireGuardian());
    public static final GuardianInfo BODY = new GuardianInfo("Body", 43709, 20, ItemID.BODY_RUNE, 26895, 4358, true, CellType.Weak, x -> x.notifyBodyGuardian());
    public static final GuardianInfo COSMIC = new GuardianInfo("Cosmic", 43710, 27, ItemID.COSMIC_RUNE, 26896, 4359, true, CellType.Medium, x -> x.notifyCosmicGuardian());
    public static final GuardianInfo CHAOS = new GuardianInfo("Chaos", 43706, 35, ItemID.CHAOS_RUNE, 26892, 4360, true, CellType.Medium, x -> x.notifyChaosGuardian());
    public static final GuardianInfo NATURE = new GuardianInfo("Nature", 43711, 44, ItemID.NATURE_RUNE, 26897, 4361, true, CellType.Strong, x -> x.notifyNatureGuardian());
    public static final GuardianInfo LAW = new GuardianInfo("Law", 43712, 54, ItemID.LAW_RUNE, 26898, 4362, true, CellType.Strong, x -> x.notifyLawGuardian());
    public static final GuardianInfo DEATH = new GuardianInfo("Death", 43707, 65, ItemID.DEATH_RUNE, 26893, 4363, true, CellType.Overcharged, x -> x.notifyDeathGuardian());
    public static final GuardianInfo BLOOD = new GuardianInfo("Blood", 43708, 77, ItemID.BLOOD_RUNE, 26894, 4364, true, CellType.Overcharged, x -> x.notifyBloodGuardian());

    public static final Set<GuardianInfo> ALL = ImmutableSet.of(AIR, MIND, WATER, EARTH, FIRE, BODY, COSMIC, CHAOS, NATURE, LAW, DEATH, BLOOD);

    public String name;
    public int gameObjectId;
    public int levelRequired;
    public int runeId;
    public int talismanId;
    public int spriteId;
    public boolean isCatalytic;
    public CellType cellType;
    public Function<GuardiansOfTheRiftHelperConfig, Notification> notifyFunc;

    public Optional<Instant> spawnTime = Optional.empty();

    public GuardianInfo(String name, int gameObjectId, int levelRequired, int runeId, int talismanId, int spriteId, boolean isCatalytic, CellType cellType, Function<GuardiansOfTheRiftHelperConfig, Notification> notifyFunc) {
        this.name = name;
        this.gameObjectId = gameObjectId;
        this.levelRequired = levelRequired;
        this.runeId = runeId;
        this.talismanId = talismanId;
        this.spriteId = spriteId;
        this.isCatalytic = isCatalytic;
        this.cellType = cellType;
        this.notifyFunc = notifyFunc;
    }

    public BufferedImage getRuneImage(ItemManager itemManager) {
        return itemManager.getImage(runeId);
    }

    public BufferedImage getTalismanImage(ItemManager itemManager) {
        return itemManager.getImage(talismanId);
    }

    public void spawn() {
        spawnTime = Optional.of(Instant.now());
    }

    public void despawn() {
        spawnTime = Optional.empty();
    }

    public Color getColor(GuardiansOfTheRiftHelperConfig config, int level) {
        if (config.colorGuardiansWithInsufficientRunecraftingLevel() && levelRequired > level) {
            return config.colorGuardiansWithInsufficientRunecraftingLevelColor();
        } else if (config.colorGuardiansByTier()) {
            switch (cellType) {
                case Weak:
                    return config.weakGuardianColor();
                case Medium:
                    return config.mediumGuardianColor();
                case Strong:
                    return config.strongGuardianColor();
                case Overcharged:
                    return config.overchargedGuardianColor();
            }
        } else {
            return isCatalytic ? config.catalyticGuardianColor() : config.elementalGuardianColor();
        }

        return Color.WHITE;
    }
}