package com.datbear;

import com.google.common.collect.ImmutableSet;
import net.runelite.api.Client;
import net.runelite.client.game.ItemManager;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayUtil;
import net.runelite.client.ui.overlay.outline.ModelOutlineRenderer;

import javax.inject.Inject;
import java.awt.*;
import java.util.HashMap;
import java.util.Set;

public class GuardiansOfTheRiftHelperOverlay extends Overlay {
    GuardiansOfTheRiftHelperPlugin plugin;

    private static final Color GREEN = new Color(0,255,0, 150);
    private static final Color RED = new Color(255, 0, 0, 150);
    private static final Set<Integer> ELEMENTAL_GUARDIAN_IDS = ImmutableSet.of(43701, 43702, 43703, 43704);

    private static final Set<Integer> GUARDIAN_IDS = ImmutableSet.of(43705, 43701, 43710, 43702, 43703, 43711, 43704, 43708, 43712, 43707, 43706, 43709, 43702);
    public static final HashMap<Integer, GuardianInfo> GUARDIAN_INFO = new HashMap<>(){{
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
        renderActiveGuardians(graphics);
        return null;
    }

    private void renderActiveGuardians(Graphics2D graphics){
        if(!plugin.isInMainRegion()) return;

        var activeGuardians = plugin.getActiveGuardians();

        for(var guardian : activeGuardians) {
            if(guardian == null) continue;
            var hull = guardian.getConvexHull();
            if(hull == null) continue;

            Color color;
            if(ELEMENTAL_GUARDIAN_IDS.contains(guardian.getId())){
                color = GREEN;
            } else {
                color = RED;
            }

            graphics.setColor(color);

            modelOutlineRenderer.drawOutline(guardian, 2, color, 2);
            var info = GUARDIAN_INFO.get(guardian.getId());
            OverlayUtil.renderImageLocation(client, graphics, guardian.getLocalLocation(), info.getRuneImage(itemManager), 505);
        }
    }
}
