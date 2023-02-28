package com.datbear;

import net.runelite.api.Client;
import net.runelite.client.ui.overlay.Overlay;

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
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        if ((!plugin.isInMainRegion() && !plugin.isInMinigame())) {
            return null;
        }

        if(config.startTimerOverlayLocation() != TimerOverlayLocation.GameOverlay && config.startTimerOverlayLocation() != TimerOverlayLocation.Both){
            return null;
        }

        Optional<Instant> gameStart = plugin.getNextGameStart();

        if (gameStart.isPresent()) {
            int timeToStart = ((int) ChronoUnit.SECONDS.between(Instant.now(), gameStart.get()));

            // fix for showing negative time
            if (timeToStart < 0) {
                return null;
            }

            String mins = String.format("%01d", timeToStart / 60);
            String secs = String.format("%02d", timeToStart % 60);
            String text = mins + ":" + secs;

            int x = 68;
            int y = 23;
            int width = 32;
            int height = 24;
            Rectangle rect = new Rectangle(x, y + height, width, height);

            plugin.drawCenteredString(graphics, text, rect);
        }

        return null;
    }
}
