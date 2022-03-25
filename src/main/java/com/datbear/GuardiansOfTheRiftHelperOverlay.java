package com.datbear;

import com.google.common.collect.ImmutableSet;
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
import java.util.Set;

public class GuardiansOfTheRiftHelperOverlay extends Overlay {
    GuardiansOfTheRiftHelperPlugin plugin;

    private static final Color GREEN = new Color(0,255,0, 150);
    private static final Color RED = new Color(255, 0, 0, 150);
    private static final Set<Integer> ELEMENTAL_GUARDIAN_IDS = ImmutableSet.of(43701, 43702, 43703, 43704);

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

    @Inject
    ItemManager itemManager;

    @Inject
    ModelOutlineRenderer modelOutlineRenderer;

    @Inject
    private Client client;

    @Inject
    public GuardiansOfTheRiftHelperOverlay(Client client, GuardiansOfTheRiftHelperPlugin plugin) {
        super();
        setPosition(OverlayPosition.DYNAMIC);
        setLayer(OverlayLayer.ABOVE_SCENE);
        this.client = client;
        this.plugin = plugin;
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        if(plugin.isInMainRegion()){
            renderActiveGuardians(graphics);
            highlightGreatGuardian(graphics);
            highlightUnchargedCellTable(graphics);
            highlightEssencePiles(graphics);
        }
        return null;
    }

    private void highlightEssencePiles(Graphics2D graphics){
        if(plugin.isShouldMakeGuardian()) {
            GameObject elementalEss = plugin.getElementalEssencePile();
            GameObject catalyticEss = plugin.getCatalyticEssencePile();
            if(elementalEss != null) {
                modelOutlineRenderer.drawOutline(elementalEss, 2, GREEN, 2);
            }
            if(catalyticEss != null) {
                modelOutlineRenderer.drawOutline(catalyticEss, 2, GREEN, 2);
            }
        }
    }

    private void renderActiveGuardians(Graphics2D graphics){
        if(!plugin.isInMainRegion()) return;

        Set<GameObject> activeGuardians = plugin.getActiveGuardians();

        for(GameObject guardian : activeGuardians) {
            if(guardian == null) continue;
            Shape hull = guardian.getConvexHull();
            if(hull == null) continue;

            GuardianInfo info = GUARDIAN_INFO.get(guardian.getId());
            Color color = info.isCatalytic ? RED : GREEN;
            graphics.setColor(color);

            modelOutlineRenderer.drawOutline(guardian, 2, color, 2);

            int imageOffset = 505;
            BufferedImage img = info.getRuneImage(itemManager);
            OverlayUtil.renderImageLocation(client, graphics, guardian.getLocalLocation(), img, imageOffset);
            if(info.spawnTime.isPresent()) {
                Point imgLocation = Perspective.getCanvasImageLocation(client, guardian.getLocalLocation(), img, imageOffset);
                long millis = ChronoUnit.MILLIS.between(Instant.now(), info.spawnTime.get().plusMillis((long)Math.floor(33 * .6 * 1000)));
                String timeRemainingText = ""+(Math.round(millis/100)/10d);
                Rectangle2D strBounds = graphics.getFontMetrics().getStringBounds(timeRemainingText, graphics);
                Point textLocation =  Perspective.getCanvasTextLocation(client, graphics, guardian.getLocalLocation(), timeRemainingText, 565);
                textLocation = new Point((int)(imgLocation.getX() + img.getWidth()/2d - strBounds.getWidth()/2d), textLocation.getY());
                OverlayUtil.renderTextLocation(graphics, textLocation, timeRemainingText, Color.WHITE);
            }
        }
    }

    private void highlightGreatGuardian(Graphics2D graphics){
        NPC greatGuardian = plugin.getGreatGuardian();
        if(plugin.isOutlineGreatGuardian() && greatGuardian != null){
            modelOutlineRenderer.drawOutline(greatGuardian, 2, Color.GREEN, 2);
        }
    }

    private void highlightUnchargedCellTable(Graphics2D graphics){
        GameObject table = plugin.getUnchargedCellTable();
        if(plugin.isOutlineUnchargedCellTable() && table != null){
            modelOutlineRenderer.drawOutline(table, 2, GREEN, 2);
        }
    }
}
