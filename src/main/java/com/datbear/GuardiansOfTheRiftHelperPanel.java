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

	private int potentialPoints(int savedPoints, int currentPoints)
	{
		if (currentPoints == 0){
			return savedPoints;
		}
		return savedPoints += currentPoints / 100;
	}

    @Override
    public Dimension render(Graphics2D graphics)
    {
        if((!plugin.isInMainRegion() && !plugin.isInMinigame()) || !config.showOverlay()){
            return null;
        }

        Optional<Instant> gameStart = plugin.getNextGameStart();

        if(gameStart.isPresent()){
            int timeToStart = ((int)ChronoUnit.SECONDS.between(Instant.now(), gameStart.get()));
            panelComponent.getChildren().add(LineComponent.builder()
                    .left("Time to start:")
                    .right(""+timeToStart)
                    .build());
        } else{
            Optional<Instant> despawn = plugin.getLastPortalDespawnTime();
            int timeSincePortal = despawn.isPresent() ? ((int)(ChronoUnit.SECONDS.between(despawn.get(), Instant.now()))) : 0;
            panelComponent.getChildren().add(LineComponent.builder()
                    .left("Time since portal:")
                    .right(""+timeSincePortal)
                    .rightColor(plugin.getTimeSincePortalColor(timeSincePortal))
                    .build());
        }



        panelComponent.getChildren().add(LineComponent.builder()
                        .left("Reward points:")
                        .right(plugin.getElementalRewardPoints() + "/" + plugin.getCatalyticRewardPoints())
                .build());

		if (config.potentialPoints())
		{
			final int potElementalPoints = potentialPoints(plugin.getElementalRewardPoints(), plugin.getCurrentElementalRewardPoints());
			final int potCatalyticPoints = potentialPoints(plugin.getCatalyticRewardPoints(), plugin.getCurrentCatalyticRewardPoints());
			final int elementalRemain = plugin.getCurrentElementalRewardPoints() % 100;
			final int catalyticRemain = plugin.getCurrentCatalyticRewardPoints() % 100;
			final String potPoints = potElementalPoints + "." + elementalRemain + "/" + potCatalyticPoints + "." + catalyticRemain;
			Color potColor = Color.WHITE;
			if (config.highlightPotential())
			{
				potColor = potElementalPoints == potCatalyticPoints ? config.potentialBalanceColor() : config.potentialUnbalanceColor();
			}
			panelComponent.getChildren().add(LineComponent.builder()
				.left("Potential:")
				.rightColor(potColor)
				.right(potPoints)
				.build());
		}
		return super.render(graphics);
    }

}
