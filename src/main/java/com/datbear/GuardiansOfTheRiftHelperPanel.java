package com.datbear;

import net.runelite.api.Client;
import net.runelite.client.ui.overlay.OverlayMenuEntry;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.LineComponent;

import javax.inject.Inject;
import java.awt.*;

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
        if((!plugin.isInMainRegion() && !plugin.isInMinigame()) || !config.showPointsOverlay()){
            return null;
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
			final String potPoints = String.format("%d.%02d/%d.%02d", potElementalPoints, elementalRemain, potCatalyticPoints, catalyticRemain);
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
