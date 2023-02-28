package com.datbear;

import net.runelite.api.Client;
import net.runelite.api.widgets.Widget;
import net.runelite.client.game.SpriteManager;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.util.ImageUtil;

import javax.inject.Inject;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

public class GuardiansOfTheRiftHelperInactivePortalOverlay extends Overlay {
    private Client client;
    private GuardiansOfTheRiftHelperPlugin plugin;
    private GuardiansOfTheRiftHelperConfig config;
    private SpriteManager spriteManager;

    @Inject
    public GuardiansOfTheRiftHelperInactivePortalOverlay(Client client, GuardiansOfTheRiftHelperPlugin plugin, GuardiansOfTheRiftHelperConfig config, SpriteManager spriteManager) {
        super(plugin);
        this.client = client;
        this.plugin = plugin;
        this.config = config;
        this.spriteManager = spriteManager;
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

        if (parentWidget.isHidden()) {
            return null;
        }

        if (!portalWidget.isHidden()) {
            return null;
        }

        BufferedImage image = spriteManager.getSprite(plugin.getPortalSpriteId(), 0);
        image = ImageUtil.grayscaleImage(image);

        int x = 189;
        int y = 70;
        int width = 32;
        int height = 32;

        graphics.drawImage(image, x, y, width, height, null);

        Optional<Instant> despawn = plugin.getLastPortalDespawnTime();

        // simulates the delay that the widget has for the initial text
        if (plugin.isFirstPortal())
        {
            int timeSincePortalMillis = despawn.isPresent() ? ((int)(ChronoUnit.MILLIS.between(despawn.get(), Instant.now()))) : 0;
            if (timeSincePortalMillis < 1200) {
                return null;
            }
        }

        int timeSincePortal = despawn.isPresent() ? ((int)(ChronoUnit.SECONDS.between(despawn.get(), Instant.now()))) : 0;
        String mins = String.format("%01d", timeSincePortal / 60);
        String secs = String.format("%02d", timeSincePortal % 60);
        String text = mins + ":" + secs;

        int textHeight = 24;
        Rectangle rect = new Rectangle(x, y + height, width, textHeight);

        plugin.drawCenteredString(graphics, text, rect);

        return null;
    }
}
