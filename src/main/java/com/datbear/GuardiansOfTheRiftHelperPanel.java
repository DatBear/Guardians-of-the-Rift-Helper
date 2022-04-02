package com.datbear;

import net.runelite.api.Client;
import net.runelite.client.ui.overlay.OverlayMenuEntry;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.LineComponent;

import javax.inject.Inject;
import java.awt.*;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import static java.lang.Math.max;
import static net.runelite.api.MenuAction.RUNELITE_OVERLAY_CONFIG;
import static net.runelite.client.ui.overlay.OverlayManager.OPTION_CONFIGURE;

public class GuardiansOfTheRiftHelperPanel extends OverlayPanel {
    private Client client;
    private GuardiansOfTheRiftHelperPlugin plugin;
    private GuardiansOfTheRiftHelperConfig config;

    @Inject
    public GuardiansOfTheRiftHelperPanel(Client client, GuardiansOfTheRiftHelperPlugin plugin, GuardiansOfTheRiftHelperConfig config){
        super(plugin);
        this.client = client;
        this.plugin = plugin;
        this.config = config;
        setPosition(OverlayPosition.TOP_CENTER);
        getMenuEntries().add(new OverlayMenuEntry(RUNELITE_OVERLAY_CONFIG, OPTION_CONFIGURE, "Guardians of the Rift Helper Overlay"));
    }

    @Override
    public Dimension render(Graphics2D graphics)
    {
        if((!plugin.isInMainRegion() && !plugin.isInMinigame()) || !config.showOverlay()){
            return null;
        }

        Optional<Instant> gameStart = plugin.getNextGameStart();
        Optional<Instant> despawn = plugin.getLastPortalDespawnTime();


        if(gameStart.isPresent()){
            int timeToStart = ((int)ChronoUnit.SECONDS.between(Instant.now(), gameStart.get()));
            panelComponent.getChildren().add(LineComponent.builder()
                    .left("Time to start:")
                    .right("" + max(timeToStart, 0))
                    .build());
        } else if (despawn.isPresent()) {
            int timeSincePortal = (int)(ChronoUnit.SECONDS.between(despawn.get(), Instant.now()));
            panelComponent.getChildren().add(LineComponent.builder()
                    .left("Time since portal:")
                    .right("" + max(timeSincePortal,0))
                    .build());
            }

        panelComponent.getChildren().add(LineComponent.builder()
                        .left("Reward points:")
                        .right(plugin.getElementalRewardPoints() + "/" + plugin.getCatalyticRewardPoints())
                .build());

        return super.render(graphics);
    }

}
