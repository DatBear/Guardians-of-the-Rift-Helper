package com.datbear;

import net.runelite.api.Point;
import net.runelite.api.*;
import net.runelite.client.game.ItemManager;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayUtil;
import net.runelite.client.ui.overlay.outline.ModelOutlineRenderer;

import javax.inject.Inject;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.*;

public class GuardiansOfTheRiftHelperOverlay extends Overlay {
    public static final HashMap<Integer, GuardianInfo> GUARDIAN_INFO = new HashMap<Integer, GuardianInfo>() {{
        put(43701, GuardianInfo.AIR);
        put(43705, GuardianInfo.MIND);
        put(43702, GuardianInfo.WATER);
        put(43703, GuardianInfo.EARTH);
        put(43704, GuardianInfo.FIRE);
        put(43709, GuardianInfo.BODY);
        put(43710, GuardianInfo.COSMIC);
        put(43706, GuardianInfo.CHAOS);
        put(43711, GuardianInfo.NATURE);
        put(43712, GuardianInfo.LAW);
        put(43707, GuardianInfo.DEATH);
        put(43708, GuardianInfo.BLOOD);
    }};
    private static final List<Integer> RAIMENTS_OF_THE_EYE_SET_IDS = new ArrayList<Integer>() {{
        add(ItemID.HAT_OF_THE_EYE);
        add(ItemID.HAT_OF_THE_EYE_BLUE);
        add(ItemID.HAT_OF_THE_EYE_GREEN);
        add(ItemID.HAT_OF_THE_EYE_RED);
        add(ItemID.ROBE_BOTTOMS_OF_THE_EYE);
        add(ItemID.ROBE_BOTTOMS_OF_THE_EYE_BLUE);
        add(ItemID.ROBE_BOTTOMS_OF_THE_EYE_GREEN);
        add(ItemID.ROBE_BOTTOMS_OF_THE_EYE_RED);
        add(ItemID.ROBE_TOP_OF_THE_EYE);
        add(ItemID.ROBE_TOP_OF_THE_EYE_BLUE);
        add(ItemID.ROBE_TOP_OF_THE_EYE_GREEN);
        add(ItemID.ROBE_TOP_OF_THE_EYE_RED);
        add(ItemID.BOOTS_OF_THE_EYE);
    }};

    private static final Color GREEN = new Color(0, 255, 0, 150);
    private static final Color RED = new Color(255, 0, 0, 150);

    private static final int GUARDIAN_TICK_COUNT = 33;
    private static final int PORTAL_TICK_COUNT = 43;
    private static final int RUNE_IMAGE_OFFSET = 505;

    private final Client client;
    private final GuardiansOfTheRiftHelperPlugin plugin;
    private final GuardiansOfTheRiftHelperConfig config;
    private final RuneLookupTable runeLookupTable;

    @Inject
    private ItemManager itemManager;
    @Inject
    private ModelOutlineRenderer modelOutlineRenderer;

    @Inject
    public GuardiansOfTheRiftHelperOverlay(Client client, GuardiansOfTheRiftHelperPlugin plugin, GuardiansOfTheRiftHelperConfig config) {
        super();
        setPosition(OverlayPosition.DYNAMIC);
        setLayer(OverlayLayer.ABOVE_SCENE);
        this.runeLookupTable = new RuneLookupTable();
        this.client = client;
        this.plugin = plugin;
        this.config = config;
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        if (plugin.isInMainRegion()) {
            renderActiveGuardians(graphics);
            highlightGreatGuardian(graphics);
            highlightUnchargedCellTable(graphics);
            highlightEssencePiles(graphics);
            renderPortal(graphics);
        }
        return null;
    }

    private void renderPortal(Graphics2D graphics) {
        if (plugin.getPortalSpawnTime().isPresent() && plugin.getPortal() != null) {
            Instant spawnTime = plugin.getPortalSpawnTime().get();
            GameObject portal = plugin.getPortal();
            long millis = ChronoUnit.MILLIS.between(Instant.now(), spawnTime.plusMillis((long) Math.floor(PORTAL_TICK_COUNT * 600)));
            String timeRemainingText = "" + (Math.round(millis / 100) / 10d);
            Point textLocation = Perspective.getCanvasTextLocation(client, graphics, portal.getLocalLocation(), timeRemainingText, 100);
            OverlayUtil.renderTextLocation(graphics, textLocation, timeRemainingText, Color.WHITE);
        }
    }

    private void highlightEssencePiles(Graphics2D graphics) {
        if (plugin.isShouldMakeGuardian()) {
            GameObject elementalEss = plugin.getElementalEssencePile();
            GameObject catalyticEss = plugin.getCatalyticEssencePile();
            if (elementalEss != null) {
                modelOutlineRenderer.drawOutline(elementalEss, 2, GREEN, 2);
            }
            if (catalyticEss != null) {
                modelOutlineRenderer.drawOutline(catalyticEss, 2, GREEN, 2);
            }
        }
    }

    private Comparator<GameObject> getLimitOutlineComparator() {
        Comparator<GameObject> comparator;
        switch (this.config.limitOutlineTo()) {
            case CATALYTIC:
                comparator = Comparator.comparing((GameObject guardian) -> !GUARDIAN_INFO.get(guardian.getId()).isCatalytic);
                break;
            case ELEMENTAL:
                comparator = Comparator.comparing((GameObject guardian) -> GUARDIAN_INFO.get(guardian.getId()).isCatalytic);
                break;
            case HIGHEST_LEVEL:
                comparator = Comparator.comparing((GameObject guardian) -> GUARDIAN_INFO.get(guardian.getId()).levelRequired).reversed();
                break;
            case HIGHEST_TIER:
                comparator = Comparator.comparing((GameObject guardian) -> GUARDIAN_INFO.get(guardian.getId()).cellType).thenComparing((GameObject guardian) -> calculateProfitPerFragment(GUARDIAN_INFO.get(guardian.getId()).runeId, this.client.getBoostedSkillLevel(Skill.RUNECRAFT), this.client.getItemContainer(InventoryID.EQUIPMENT))).reversed();
                break;
            case HIGHEST_PROFIT:
                comparator = Comparator.comparing((GameObject guardian) -> calculateProfitPerFragment(GUARDIAN_INFO.get(guardian.getId()).runeId, this.client.getBoostedSkillLevel(Skill.RUNECRAFT), this.client.getItemContainer(InventoryID.EQUIPMENT))).reversed();
                break;
            case CUSTOM:
                List<String> customList = Arrays.asList(config.onlyOutlineGuardianByCustom().toUpperCase().split(","));
                comparator = Comparator.comparing(guardian -> customList.indexOf(itemManager.getItemComposition(GUARDIAN_INFO.get(guardian.getId()).runeId).getMembersName().toUpperCase()));
                break;
            default:
                comparator = Comparator.comparing(GameObject::getId).reversed();
                break;
        }
        return comparator;
    }

    private double calculateProfitPerFragment(int itemId, int playerLevel, ItemContainer itemContainer) {
        double amountOfSetWearing = itemContainer == null ? 0 : Arrays.stream(itemContainer.getItems()).map(Item::getId).filter(id -> RAIMENTS_OF_THE_EYE_SET_IDS.stream().anyMatch(bonusId -> bonusId.equals(id))).count();
        double setBonus = 1 + (amountOfSetWearing * .1 + (amountOfSetWearing == 4 ? .2 : 0));

        return this.itemManager.getItemPrice(itemId) * (runeLookupTable.getHighestMultiplier(itemId, playerLevel) * setBonus);
    }

    private void renderActiveGuardians(Graphics2D graphics) {
        if (!plugin.isInMainRegion()) return;

        Set<GameObject> activeGuardians = plugin.getActiveGuardians();
        Set<GameObject> guardians = plugin.getGuardians();
        Set<Integer> inventoryTalismans = plugin.getInventoryTalismans();
        int talismanGuardianId = -1;

        for (int talisman : inventoryTalismans) {
            Optional<GameObject> talismanGuardian = guardians.stream().filter(x -> GUARDIAN_INFO.get(x.getId()).talismanId == talisman).findFirst();

            if (talismanGuardian.isPresent() && activeGuardians.stream().noneMatch(x -> x.getId() == talismanGuardian.get().getId())) {
                activeGuardians.add(talismanGuardian.get());
                talismanGuardianId = talismanGuardian.get().getId();
            }
        }

        int finalTalismanGuardianId = talismanGuardianId;
        activeGuardians.stream()
                .filter(guardian -> guardian != null && guardian.getConvexHull() != null && (!this.config.levelOverride() || GUARDIAN_INFO.get(guardian.getId()).levelRequired <= this.client.getBoostedSkillLevel(Skill.RUNECRAFT)))
                .sorted(getLimitOutlineComparator())
                .limit(this.config.limitOutlineTo() == LimitOutlineToConfigOptions.NO_LIMIT ? 12 : 1)
                .forEach(guardian -> {
                    GuardianInfo guardianInfo = GUARDIAN_INFO.get(guardian.getId());

                    Color color = guardianInfo.getColor(config);
                    graphics.setColor(color);
                    modelOutlineRenderer.drawOutline(guardian, 2, color, 2);

                    if (finalTalismanGuardianId != -1 && finalTalismanGuardianId == guardian.getId()) {
                        OverlayUtil.renderImageLocation(client, graphics, guardian.getLocalLocation(), guardianInfo.getTalismanImage(itemManager), RUNE_IMAGE_OFFSET);
                    } else {
                        BufferedImage img = guardianInfo.getRuneImage(itemManager);
                        OverlayUtil.renderImageLocation(client, graphics, guardian.getLocalLocation(), img, RUNE_IMAGE_OFFSET);
                        if (guardianInfo.spawnTime.isPresent()) {
                            Point imgLocation = Perspective.getCanvasImageLocation(client, guardian.getLocalLocation(), img, RUNE_IMAGE_OFFSET);
                            long millis = ChronoUnit.MILLIS.between(Instant.now(), guardianInfo.spawnTime.get().plusMillis((long) Math.floor(GUARDIAN_TICK_COUNT * 600)));
                            String timeRemainingText = "" + (Math.round(millis / 100) / 10d);
                            Rectangle2D strBounds = graphics.getFontMetrics().getStringBounds(timeRemainingText, graphics);
                            Point textLocation = Perspective.getCanvasTextLocation(client, graphics, guardian.getLocalLocation(), timeRemainingText, RUNE_IMAGE_OFFSET + 60);
                            textLocation = new Point((int) (imgLocation.getX() + img.getWidth() / 2d - strBounds.getWidth() / 2d), textLocation.getY());
                            OverlayUtil.renderTextLocation(graphics, textLocation, timeRemainingText, Color.WHITE);
                        }
                    }
                });
    }

    private void highlightGreatGuardian(Graphics2D graphics) {
        if (!config.outlineGreatGuardian()) {
            return;
        }

        NPC greatGuardian = plugin.getGreatGuardian();
        if (plugin.isOutlineGreatGuardian() && greatGuardian != null) {
            modelOutlineRenderer.drawOutline(greatGuardian, 2, Color.GREEN, 2);
        }
    }

    private void highlightUnchargedCellTable(Graphics2D graphics) {
        if (!config.outlineCellTable()) {
            return;
        }

        GameObject table = plugin.getUnchargedCellTable();
        if (plugin.isOutlineUnchargedCellTable() && table != null) {
            modelOutlineRenderer.drawOutline(table, 2, GREEN, 2);
        }
    }
}
