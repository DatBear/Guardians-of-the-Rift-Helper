package com.datbear;

import net.runelite.api.*;
import net.runelite.api.Point;
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
import java.util.HashMap;
import java.util.Optional;
import java.util.Set;

public class GuardiansOfTheRiftHelperOverlay extends Overlay {
    private static final Color GREEN = new Color(0,255,0, 150);
    private static final Color RED = new Color(255, 0, 0, 150);

    public static final HashMap<Integer, GuardianInfo> GUARDIAN_INFO = new HashMap<Integer, GuardianInfo>(){{
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

    private static final int GUARDIAN_TICK_COUNT = 33;
    private static final int PORTAL_TICK_COUNT = 43;

    private static final int RUNE_IMAGE_OFFSET = 505;

    @Inject
    private ItemManager itemManager;

    @Inject
    private ModelOutlineRenderer modelOutlineRenderer;

    private Client client;
    private GuardiansOfTheRiftHelperPlugin plugin;
    private GuardiansOfTheRiftHelperConfig config;

    @Inject
    public GuardiansOfTheRiftHelperOverlay(Client client, GuardiansOfTheRiftHelperPlugin plugin, GuardiansOfTheRiftHelperConfig config) {
        super();
        setPosition(OverlayPosition.DYNAMIC);
        setLayer(OverlayLayer.ABOVE_SCENE);
        this.client = client;
        this.plugin = plugin;
        this.config = config;
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        if(plugin.isInMainRegion()){
            renderActiveGuardians(graphics);
            highlightGreatGuardian(graphics);
            highlightUnchargedCellTable(graphics);
            highlightEssencePiles(graphics);
            renderPortal(graphics);
        }
        return null;
    }

    private void renderPortal(Graphics2D graphics){
        if(plugin.getPortalSpawnTime().isPresent() && plugin.getPortal() != null){
            Instant spawnTime = plugin.getPortalSpawnTime().get();
            GameObject portal = plugin.getPortal();
            long millis = ChronoUnit.MILLIS.between(Instant.now(), spawnTime.plusMillis((long)Math.floor(PORTAL_TICK_COUNT * 600)));
            String timeRemainingText = ""+(Math.round(millis/100)/10d);
            Point textLocation =  Perspective.getCanvasTextLocation(client, graphics, portal.getLocalLocation(), timeRemainingText, 100);
            OverlayUtil.renderTextLocation(graphics, textLocation, timeRemainingText, Color.WHITE);
        }
    }

    private void highlightEssencePiles(Graphics2D graphics){
        if(plugin.isShouldMakeGuardian()) {
            GameObject elementalEss = plugin.getElementalEssencePile();
            GameObject catalyticEss = plugin.getCatalyticEssencePile();
            if(elementalEss != null) {
                modelOutlineRenderer.drawOutline(elementalEss, 2, config.essencePileColor(), 2);
            }
            if(catalyticEss != null) {
                modelOutlineRenderer.drawOutline(catalyticEss, 2, config.essencePileColor(), 2);
            }
        }
    }

    private CellType bestCell(final Set<GameObject> activeGuardians) {
        CellType best = CellType.Weak;
        for (final GameObject guardian : activeGuardians) {
            if(guardian == null) continue;
            Shape hull = guardian.getConvexHull();
            if(hull == null) continue;
            GuardianInfo info = GUARDIAN_INFO.get(guardian.getId());

            if (info.cellType.compareTo(best) > 0 && info.levelRequired < client.getBoostedSkillLevel(Skill.RUNECRAFT)) {
                if (info.cellType == CellType.Overcharged) {
                    return CellType.Overcharged;
                }
                best = info.cellType;
            }
        }
        return best;
    }

    private PointBalance currentBalance() {
        PointBalance val = PointBalance.BALANCED;
        final int potElementalPoints = plugin.potentialPointsElemental();
        final int potCatalyticPoints = plugin.potentialPointsCatalytic();
        if (potElementalPoints > potCatalyticPoints) {
            val = PointBalance.NEED_CATALYTIC;
        } else if (potCatalyticPoints > potElementalPoints) {
            val = PointBalance.NEED_ELEMENTAL;
        }
        return val;
    }

    private void renderActiveGuardians(Graphics2D graphics){
        if(!plugin.isInMainRegion()) return;

        Set<GameObject> activeGuardians = plugin.getActiveGuardians();
        Set<GameObject> guardians = plugin.getGuardians();
        Set<Integer> inventoryTalismans = plugin.getInventoryTalismans();

        PointBalance balance = PointBalance.BALANCED;
        CellType bestCell = null;

        if (config.pointBalanceHelper()) {
            balance = currentBalance();
        }


        for(GameObject guardian : activeGuardians) {
            if(guardian == null) continue;
            Shape hull = guardian.getConvexHull();
            if(hull == null) continue;

            GuardianInfo info = GUARDIAN_INFO.get(guardian.getId());

            if (config.pointBalanceHelper()) {
                if (!info.isCatalytic && balance == PointBalance.NEED_CATALYTIC) {
                    continue;
                } else if (info.isCatalytic && balance == PointBalance.NEED_ELEMENTAL) {
                    continue;
                } else if (balance == PointBalance.BALANCED) {
                    if (bestCell == null) {
                        bestCell = bestCell(activeGuardians);
                    }
                    if (info.cellType != bestCell) {
                        continue;
                    }
                }
            }

            Color color = info.getColor(config);
            graphics.setColor(color);

            modelOutlineRenderer.drawOutline(guardian, 2, color, 2);

            BufferedImage img = info.getRuneImage(itemManager);
            OverlayUtil.renderImageLocation(client, graphics, guardian.getLocalLocation(), img, RUNE_IMAGE_OFFSET);
            if(info.spawnTime.isPresent()) {
                Point imgLocation = Perspective.getCanvasImageLocation(client, guardian.getLocalLocation(), img, RUNE_IMAGE_OFFSET);
                long millis = ChronoUnit.MILLIS.between(Instant.now(), info.spawnTime.get().plusMillis((long)Math.floor(GUARDIAN_TICK_COUNT * 600)));
                String timeRemainingText = ""+(Math.round(millis/100)/10d);
                Rectangle2D strBounds = graphics.getFontMetrics().getStringBounds(timeRemainingText, graphics);
                Point textLocation =  Perspective.getCanvasTextLocation(client, graphics, guardian.getLocalLocation(), timeRemainingText, RUNE_IMAGE_OFFSET+60);
                textLocation = new Point((int)(imgLocation.getX() + img.getWidth()/2d - strBounds.getWidth()/2d), textLocation.getY());
                OverlayUtil.renderTextLocation(graphics, textLocation, timeRemainingText, Color.WHITE);
            }
        }

        for(int talisman : inventoryTalismans){
            Optional<GameObject> talismanGuardian = guardians.stream().filter(x -> GUARDIAN_INFO.get(x.getId()).talismanId == talisman).findFirst();

            if(talismanGuardian.isPresent() && activeGuardians.stream().noneMatch(x -> x.getId() == talismanGuardian.get().getId())) {
                GuardianInfo talismanGuardianInfo = GUARDIAN_INFO.get(talismanGuardian.get().getId());
                modelOutlineRenderer.drawOutline(talismanGuardian.get(), 2, talismanGuardianInfo.getColor(config), 2);
                OverlayUtil.renderImageLocation(client, graphics, talismanGuardian.get().getLocalLocation(), talismanGuardianInfo.getTalismanImage(itemManager), RUNE_IMAGE_OFFSET);
            }
        }
    }

    private void highlightGreatGuardian(Graphics2D graphics) {
        if(!config.outlineGreatGuardian()){
            return;
        }

        NPC greatGuardian = plugin.getGreatGuardian();
        if(plugin.isOutlineGreatGuardian() && greatGuardian != null){
            modelOutlineRenderer.drawOutline(greatGuardian, 2, Color.GREEN, 2);
        }
    }

    private void highlightUnchargedCellTable(Graphics2D graphics) {
        if(!config.outlineCellTable()){
            return;
        }

        GameObject table = plugin.getUnchargedCellTable();
        if(plugin.isOutlineUnchargedCellTable() && table != null){
            modelOutlineRenderer.drawOutline(table, 2, GREEN, 2);
        }
    }
}
