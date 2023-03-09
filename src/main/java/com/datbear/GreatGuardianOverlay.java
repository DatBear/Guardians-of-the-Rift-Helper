package com.datbear;

import net.runelite.api.GameState;
import net.runelite.api.InventoryID;
import net.runelite.api.ItemContainer;
import net.runelite.api.ItemID;
import net.runelite.api.NPC;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.ItemContainerChanged;
import net.runelite.api.events.NpcSpawned;
import net.runelite.client.game.ItemManager;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.outline.ModelOutlineRenderer;

import javax.inject.Inject;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.util.Optional;

public class GreatGuardianOverlay extends Overlay {
    @Inject
    private ItemManager itemManager;

    @Inject
    private ModelOutlineRenderer modelOutlineRenderer;

    @Inject
    private GuardiansOfTheRiftHelperConfig config;

    private final int GREAT_GUARDIAN_ID = 11403;
    private Optional<NPC> greatGuardian = Optional.empty();
    private boolean elementalGuardianStones;
    private boolean catalyticGuardianStones;
    private boolean polyelementalGuardianStones;

    public GreatGuardianOverlay() {
        setPosition(OverlayPosition.DYNAMIC);
        setLayer(OverlayLayer.UNDER_WIDGETS);
    }

    /**
     * Save great guardian npc for rendering.
     */
    public void onNpcSpawned(final NpcSpawned event) {
        if (event.getNpc().getId() == GREAT_GUARDIAN_ID) {
            this.greatGuardian = Optional.of(event.getNpc());
        }
    }

    /**
     * Clear great guardian on loading.
     */
    public void onGameStateChanged(final GameStateChanged event) {
        if (event.getGameState() == GameState.LOADING) {
            this.greatGuardian = Optional.empty();
        }
    }

    /**
     * Check if player has guardian stones in their inventory.
     */
    public void onItemContainerChanged(final ItemContainerChanged event) {
        if (event.getItemContainer().getId() == InventoryID.INVENTORY.getId()) {
            final ItemContainer inventory = event.getItemContainer();
            this.elementalGuardianStones = inventory.contains(ItemID.ELEMENTAL_GUARDIAN_STONE);
            this.catalyticGuardianStones = inventory.contains(ItemID.CATALYTIC_GUARDIAN_STONE);
            this.polyelementalGuardianStones = inventory.contains(ItemID.POLYELEMENTAL_GUARDIAN_STONE);
        }
    }

    @Override
    public Dimension render(final Graphics2D graphics) {
        if (this.greatGuardian.isPresent() && config.outlineGreatGuardian() && (this.elementalGuardianStones || this.catalyticGuardianStones || this.polyelementalGuardianStones)) {
            final Color color =
                this.elementalGuardianStones ? config.elementalGuardianColor() :
                this.catalyticGuardianStones ? config.catalyticGuardianColor() :
                config.polyelementalGuardianColor();

            modelOutlineRenderer.drawOutline(this.greatGuardian.get(), 2, color, 2);
        }

        return null;
    }
}
