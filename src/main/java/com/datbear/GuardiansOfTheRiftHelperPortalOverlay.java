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

        if(config.inactivePortalOverlayLocation() != TimerOverlayLocation.GameOverlay && config.inactivePortalOverlayLocation() != TimerOverlayLocation.Both){
            return null;
        }

        Widget parentWidget = client.getWidget(plugin.getParentWidgetId());
        Widget portalWidget = client.getWidget(plugin.getPortalWidgetId());

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

        Optional<Instant> lastDespawnTime = plugin.getLastPortalDespawnTime();
        // simulates the delay that the widget has for the initial text
        if (plugin.isFirstPortal())
        {
            var timeSincePortalMillis = lastDespawnTime.isPresent() ? ((int)(ChronoUnit.MILLIS.between(lastDespawnTime.get(), Instant.now()))) : 0;
            if (timeSincePortalMillis < 1200) {
                return null;
            }
        }

        var timeSincePortal = lastDespawnTime.isPresent() ? ((int)(ChronoUnit.SECONDS.between(lastDespawnTime.get(), Instant.now()))) : 0;
        String mins = String.format("%01d", timeSincePortal / 60);
        String secs = String.format("%02d", timeSincePortal % 60);
        String text = mins + ":" + secs;

        var textHeight = 24;
        Rectangle rect = new Rectangle(x, y + height, width, textHeight);

        plugin.drawCenteredString(graphics, text, rect);

        return null;
    }
}
