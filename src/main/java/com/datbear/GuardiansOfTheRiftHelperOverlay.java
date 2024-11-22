package com.datbear;

import net.runelite.api.Point;
import net.runelite.api.*;
import net.runelite.client.game.ItemManager;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayUtil;
import net.runelite.client.ui.overlay.outline.ModelOutlineRenderer;

import javax.inject.Inject;
import java.awt.*;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.Set;

public class GuardiansOfTheRiftHelperOverlay extends Overlay {
    private static final Color GREEN = new Color(0, 255, 0, 150);
    private static final Color RED = new Color(255, 0, 0, 150);

    private static final int GUARDIAN_TICK_COUNT = 33;
    private static final int PORTAL_TICK_COUNT = 43;

    private static final int RUNE_IMAGE_OFFSET = 505;

    @Inject
    private ItemManager itemManager;

    @Inject
    private ModelOutlineRenderer modelOutlineRenderer;

    private Client client;
    private GuardiansOfTheRiftHelperPlugin plugin;
    private GuardiansOfTheRiftHelperConfig config;

    @Inject
    public GuardiansOfTheRiftHelperOverlay(Client client, GuardiansOfTheRiftHelperPlugin plugin, GuardiansOfTheRiftHelperConfig config) {
        super();
        setPosition(OverlayPosition.DYNAMIC);
        setLayer(OverlayLayer.ABOVE_SCENE);
        this.client = client;
        this.plugin = plugin;
        this.config = config;
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        if (plugin.isInMainRegion()) {
            renderActiveGuardians(graphics);
            highlightGreatGuardian(graphics);
            highlightUnchargedCellTable(graphics);
            highlightDepositPool(graphics);
            highlightEssencePiles(graphics);
            highlightCellTiles(graphics);
            renderPortal(graphics);
        }
        return null;
    }

    private void renderPortal(Graphics2D graphics) {
        if (plugin.getPortalSpawnTime().isPresent() && plugin.getPortal() != null) {
            var spawnTime = plugin.getPortalSpawnTime().get();
            var portal = plugin.getPortal();
            var millis = ChronoUnit.MILLIS.between(Instant.now(), spawnTime.plusMillis((long) Math.floor(PORTAL_TICK_COUNT * 600)));
            var timeRemainingText = "" + (Math.round(millis / 100) / 10d);
            var textLocation = Perspective.getCanvasTextLocation(client, graphics, portal.getLocalLocation(), timeRemainingText, 100);
            OverlayUtil.renderTextLocation(graphics, textLocation, timeRemainingText, Color.WHITE);
        }
    }

    private void highlightEssencePiles(Graphics2D graphics) {
        if (plugin.isShouldMakeGuardian()) {
            var elementalEss = plugin.getElementalEssencePile();
            var catalyticEss = plugin.getCatalyticEssencePile();
            if (elementalEss != null) {
                modelOutlineRenderer.drawOutline(elementalEss, 2, config.essencePileColor(), 2);
            }
            if (catalyticEss != null) {
                modelOutlineRenderer.drawOutline(catalyticEss, 2, config.essencePileColor(), 2);
            }
        }
    }

    private CellType bestCell(final Set<GameObject> activeGuardians) {
        var best = CellType.Weak;
        for (final GameObject guardian : activeGuardians) {
            if (guardian == null) continue;
            var hull = guardian.getConvexHull();
            if (hull == null) continue;
            var info = getGuardianInfo(guardian.getId());

            if (info.cellType.compareTo(best) > 0 && info.levelRequired < client.getBoostedSkillLevel(Skill.RUNECRAFT)) {
                if (info.cellType == CellType.Overcharged) {
                    return CellType.Overcharged;
                }
                best = info.cellType;
            }
        }
        return best;
    }

    private PointBalance currentBalance() {
        final var potElementalPoints = plugin.potentialPointsElemental();
        final var potCatalyticPoints = plugin.potentialPointsCatalytic();
        if (potElementalPoints > potCatalyticPoints) {
            return PointBalance.NEED_CATALYTIC;
        } else if (potCatalyticPoints > potElementalPoints) {
            return PointBalance.NEED_ELEMENTAL;
        }
        return PointBalance.BALANCED;
    }

    private void renderActiveGuardians(Graphics2D graphics) {
        if (!plugin.isInMainRegion()) return;

        var activeGuardians = plugin.getActiveGuardians();
        var guardians = plugin.getGuardians();
        var inventoryTalismans = plugin.getInventoryTalismans();

        var balance = PointBalance.BALANCED;
        CellType bestCell = null;

        if (config.pointBalanceHelper()) {
            balance = currentBalance();
        }

        for (GameObject guardian : activeGuardians) {
            if (guardian == null) continue;
            var hull = guardian.getConvexHull();
            if (hull == null) continue;

            var info = getGuardianInfo(guardian.getId());

            if (config.pointBalanceHelper()) {
                if (!info.isCatalytic && balance == PointBalance.NEED_CATALYTIC) {
                    continue;
                } else if (info.isCatalytic && balance == PointBalance.NEED_ELEMENTAL) {
                    continue;
                } else if (balance == PointBalance.BALANCED) {
                    if (bestCell == null) {
                        bestCell = bestCell(activeGuardians);
                    }
                    if (info.cellType != bestCell) {
                        continue;
                    }
                }
            }

            var color = info.getColor(config, client.getBoostedSkillLevel(Skill.RUNECRAFT));
            graphics.setColor(color);

            if (config.guardianOutline()) {
                modelOutlineRenderer.drawOutline(guardian, config.guardianBorderWidth(), color, config.guardianOutlineFeather());
            }

            var img = info.getRuneImage(itemManager);
            if (config.guardianShowRuneIcons()) {
                OverlayUtil.renderImageLocation(client, graphics, guardian.getLocalLocation(), img, RUNE_IMAGE_OFFSET);
            }

            if (!info.spawnTime.isPresent()) continue;

            var imgLocation = Perspective.getCanvasImageLocation(client, guardian.getLocalLocation(), img, RUNE_IMAGE_OFFSET);
            var millis = ChronoUnit.MILLIS.between(Instant.now(), info.spawnTime.get().plusMillis((long) Math.floor(GUARDIAN_TICK_COUNT * 600)));
            var timeRemainingText = "" + (Math.round(millis / 100) / 10d);
            var strBounds = graphics.getFontMetrics().getStringBounds(timeRemainingText, graphics);
            var textLocation = Perspective.getCanvasTextLocation(client, graphics, guardian.getLocalLocation(), timeRemainingText, RUNE_IMAGE_OFFSET + 60);
            if (textLocation == null) continue;

            textLocation = new Point((int) (imgLocation.getX() + img.getWidth() / 2d - strBounds.getWidth() / 2d), textLocation.getY());
            OverlayUtil.renderTextLocation(graphics, textLocation, timeRemainingText, Color.WHITE);
        }

        for (int talisman : inventoryTalismans) {
            var talismanGuardian = guardians.stream().filter(x -> getGuardianInfo(x.getId()).talismanId == talisman).findFirst();

            if (talismanGuardian.isPresent() && activeGuardians.stream().noneMatch(x -> x.getId() == talismanGuardian.get().getId())) {
                var talismanGuardianInfo = getGuardianInfo(talismanGuardian.get().getId());
                if (config.guardianOutline()) {
                    modelOutlineRenderer.drawOutline(talismanGuardian.get(), config.guardianBorderWidth(), talismanGuardianInfo.getColor(config, client.getBoostedSkillLevel(Skill.RUNECRAFT)), config.guardianOutlineFeather());
                }
                OverlayUtil.renderImageLocation(client, graphics, talismanGuardian.get().getLocalLocation(), talismanGuardianInfo.getTalismanImage(itemManager), RUNE_IMAGE_OFFSET);
            }
        }
    }

    private GuardianInfo getGuardianInfo(int gameObjectId) {
        return GuardianInfo.ALL.stream().filter(x -> x.gameObjectId == gameObjectId).findFirst().get();
    }

    private void highlightGreatGuardian(Graphics2D graphics) {
        if (!config.outlineGreatGuardian()) {
            return;
        }

        NPC greatGuardian = plugin.getGreatGuardian();
        if (plugin.isHasAnyStones() && greatGuardian != null) {
            modelOutlineRenderer.drawOutline(greatGuardian, 2, Color.GREEN, 2);
        }
    }

    private void highlightUnchargedCellTable(Graphics2D graphics) {
        if (!config.outlineCellTable()) {
            return;
        }

        GameObject table = plugin.getUnchargedCellTable();
        if (plugin.isOutlineUnchargedCellTable() && table != null) {
            modelOutlineRenderer.drawOutline(table, 2, GREEN, 2);
        }
    }

    private void highlightDepositPool(Graphics2D graphics) {
        if (!config.outlineDepositPool()) {
            return;
        }

        var depositPool = plugin.getDepositPool();
        if (plugin.isHasAnyRunes() && depositPool != null) {
            modelOutlineRenderer.drawOutline(depositPool, 2, GREEN, 2);
        }
    }

    private void highlightCellTiles(Graphics2D graphics) {
        if (config.outlineCellTileLocation() == TileLocation.None) {
            return;
        }

        if (!plugin.getChargedCellType().isPresent()) {
            return;
        }

        if (config.outlineCellType().ordinal() > plugin.getChargedCellType().get().ordinal()) {
            return;
        }

        if (config.outlineCellTileLocation() == TileLocation.Closest) {
            var closest = plugin.getCellTiles().stream().sorted(Comparator.comparingInt(a -> a.getWorldLocation().distanceTo(client.getLocalPlayer().getWorldLocation()))).findFirst();
            closest.ifPresent(x -> modelOutlineRenderer.drawOutline(x, 2, GREEN, 2));
            return;
        }

        for (var tile : plugin.getCellTiles()) {
            modelOutlineRenderer.drawOutline(tile, 2, GREEN, 2);
        }
    }
}
