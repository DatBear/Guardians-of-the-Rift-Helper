package com.datbear;

import net.runelite.api.Client;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;

import javax.inject.Inject;
import java.awt.*;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

public class GuardiansOfTheRiftHelperStartTimerOverlay extends Overlay {
    private Client client;
    private GuardiansOfTheRiftHelperPlugin plugin;
    private GuardiansOfTheRiftHelperConfig config;

    @Inject
    public GuardiansOfTheRiftHelperStartTimerOverlay(Client client, GuardiansOfTheRiftHelperPlugin plugin, GuardiansOfTheRiftHelperConfig config) {
        super(plugin);
        this.client = client;
        this.plugin = plugin;
        this.config = config;
        setLayer(OverlayLayer.ABOVE_SCENE);
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        if ((!plugin.isInMainRegion() && !plugin.isInMinigame())) {
            return null;
        }

        if(config.startTimerOverlayLocation() != TimerOverlayLocation.Game_Overlay && config.startTimerOverlayLocation() != TimerOverlayLocation.Both){
            return null;
        }

        Optional<Instant> gameStart = plugin.getNextGameStart();

        if (gameStart.isPresent()) {
            int timeToStart = ((int) ChronoUnit.SECONDS.between(Instant.now(), gameStart.get()));

            if (timeToStart < 0) {
                return null;
            }

            String startString = String.format("%02d", timeToStart % 60) + " seconds to game start";

            var parentWidget = client.getWidget(plugin.getParentWidgetId());
            var portalWidget = client.getWidget(plugin.getPortalWidgetId());
            var x = parentWidget.getRelativeX() + 16;
            var y = parentWidget.getRelativeY() + portalWidget.getRelativeY() + 60;

            int width = 180;
            int height = 15;

            Rectangle rect = new Rectangle(x, y, width, height);
            plugin.drawCenteredString(graphics, startString, rect, Optional.empty());
        }

        return null;
    }
}
