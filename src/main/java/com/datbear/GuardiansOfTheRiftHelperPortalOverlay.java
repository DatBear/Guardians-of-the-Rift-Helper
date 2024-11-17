package com.datbear;

import net.runelite.api.Client;
import net.runelite.api.widgets.Widget;
import net.runelite.client.game.SpriteManager;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPriority;
import net.runelite.client.util.ImageUtil;

import javax.inject.Inject;
import java.awt.*;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

public class GuardiansOfTheRiftHelperPortalOverlay extends Overlay {
    private Client client;
    private GuardiansOfTheRiftHelperPlugin plugin;
    private GuardiansOfTheRiftHelperConfig config;
    private SpriteManager spriteManager;

    @Inject
    public GuardiansOfTheRiftHelperPortalOverlay(Client client, GuardiansOfTheRiftHelperPlugin plugin, GuardiansOfTheRiftHelperConfig config, SpriteManager spriteManager) {
        super(plugin);
        this.client = client;
        this.plugin = plugin;
        this.config = config;
        this.spriteManager = spriteManager;
        setLayer(OverlayLayer.ABOVE_SCENE);
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        if (!plugin.isInMainRegion() && !plugin.isInMinigame()) {
            return null;
        }

        if(plugin.getNextGameStart().isPresent()){
            return null;
        }

        if(config.inactivePortalOverlayLocation() != TimerOverlayLocation.GameOverlay && config.inactivePortalOverlayLocation() != TimerOverlayLocation.Both){
            return null;
        }

        Optional<Instant> despawn = plugin.getLastPortalDespawnTime();
        if(!despawn.isPresent()) return null;

        var parentWidget = client.getWidget(plugin.getParentWidgetId());
        var portalWidget = client.getWidget(plugin.getPortalWidgetId());

        if (parentWidget == null || portalWidget == null) {
            return null;
        }

        if (parentWidget.isHidden() || !portalWidget.isHidden()) {
            return null;
        }

        var image = ImageUtil.grayscaleImage(spriteManager.getSprite(plugin.getPortalSpriteId(), 0));

        var x = (int)parentWidget.getRelativeX() + portalWidget.getRelativeX() + 16;
        var y = (int)parentWidget.getRelativeY() + portalWidget.getRelativeY() + 10;
        var width = 32;
        var height = 32;

        graphics.drawImage(image, x, y, width, height, null);

        // simulates the delay that the widget has for the initial text
        if (plugin.isFirstPortal())
        {
            var timeSincePortalMillis = despawn.isPresent() ? ((int)(ChronoUnit.MILLIS.between(despawn.get(), Instant.now()))) : 0;
            if (timeSincePortalMillis < 1200) {
                return null;
            }
        }

        var timeSincePortal = ((int)(ChronoUnit.SECONDS.between(despawn.get(), Instant.now())));
        var color = plugin.getTimeSincePortalColor(timeSincePortal);
        var mins = String.format("%01d", timeSincePortal / 60);
        var secs = String.format("%02d", timeSincePortal % 60);
        var text = mins + ":" + secs;

        var textHeight = 24;
        Rectangle rect = new Rectangle(x, y + height, width, textHeight);

        plugin.drawCenteredString(graphics, text, rect, Optional.of(color));

        return null;
    }
}
