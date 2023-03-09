package com.datbear;

import net.runelite.api.Client;
import net.runelite.api.GameObject;
import net.runelite.api.GameState;
import net.runelite.api.InventoryID;
import net.runelite.api.ItemContainer;
import net.runelite.api.ItemID;
import net.runelite.api.events.GameObjectSpawned;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.ItemContainerChanged;
import net.runelite.api.widgets.Widget;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.outline.ModelOutlineRenderer;

import javax.inject.Inject;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CraftableGuardiansOverlay extends Overlay {
    @Inject
    private Client client;

    @Inject
    private ModelOutlineRenderer modelOutlineRenderer;

    @Inject
    private GuardiansOfTheRiftHelperConfig config;

    private final int GUARDIAN_COUNT_WIDGET_ID = 48889886;
    private final int ELEMENTAL_ESSENCE_PILE_ID = 43722;
    private final int CATALYTIC_ESSENCE_PILE_ID = 43723;

    private final Pattern guardianCountRegex = Pattern.compile("(?<current>.+)/(?<total>.+)");

    private Optional<GameObject> elementalEssencePile = Optional.empty();
    private Optional<GameObject> catalyticEssencePile = Optional.empty();

    private int countCurrent = 0;
    private int countTotal = 0;

    private boolean chiselInInventory = false;
    private boolean cellInInventory = false;

    public CraftableGuardiansOverlay() {
        setPosition(OverlayPosition.DYNAMIC);
        setLayer(OverlayLayer.UNDER_WIDGETS);
    }

    /**
     * Save essence piles objects for rendering.
     */
    public void onGameObjectSpawned(final GameObjectSpawned event) {
        final GameObject gameObject = event.getGameObject();

        if (gameObject.getId() == ELEMENTAL_ESSENCE_PILE_ID) {
            this.elementalEssencePile = Optional.of(gameObject);
        } else if (gameObject.getId() == CATALYTIC_ESSENCE_PILE_ID) {
            this.catalyticEssencePile = Optional.of(gameObject);
        }
    }

    /**
     * Reset essence piles when loading.
     */
    public void onGameStateChanged(final GameStateChanged event) {
        if (event.getGameState() == GameState.LOADING) {
            this.elementalEssencePile = Optional.empty();
            this.catalyticEssencePile = Optional.empty();
        }
    }

    /**
     * Check if guardians can be crafted.
     */
    public void onGameTick() {
        final Widget widget = client.getWidget(GUARDIAN_COUNT_WIDGET_ID);
        if (widget == null) return;

        final Matcher matcher = guardianCountRegex.matcher(widget.getText());
        if (!matcher.find()) return;

        this.countCurrent = Integer.parseInt(matcher.group("current"));
        this.countTotal = Integer.parseInt(matcher.group("total"));
    }

    /**
     * Check if the player has chisel and cell in their inventory.
     */
    public void onItemContainerChanged(final ItemContainerChanged event) {
        if (event.getItemContainer().getId() == InventoryID.INVENTORY.getId()) {
            final ItemContainer inventory = event.getItemContainer();
            this.chiselInInventory = inventory.contains(ItemID.CHISEL);
            this.cellInInventory =
                inventory.contains(ItemID.WEAK_CELL) ||
                inventory.contains(ItemID.MEDIUM_CELL) ||
                inventory.contains(ItemID.STRONG_CELL) ||
                inventory.contains(ItemID.OVERCHARGED_CELL);
        }
    }

    @Override
    public Dimension render(final Graphics2D graphics) {
        if (this.chiselInInventory && this.cellInInventory && this.countCurrent < this.countTotal) {
            this.elementalEssencePile.ifPresent(elementalEssencePile -> modelOutlineRenderer.drawOutline(elementalEssencePile, 2, config.elementalGuardianColor(), 2));
            this.catalyticEssencePile.ifPresent(catalyticEssencePile -> modelOutlineRenderer.drawOutline(catalyticEssencePile, 2, config.catalyticGuardianColor(), 2));
        }

        return null;
    }
}
