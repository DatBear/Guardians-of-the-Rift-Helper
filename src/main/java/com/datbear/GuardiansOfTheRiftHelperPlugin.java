package com.datbear;

import com.google.inject.Provides;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Menu;
import net.runelite.api.*;
import net.runelite.api.events.*;
import net.runelite.api.widgets.Widget;
import net.runelite.client.Notifier;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;
import javax.inject.Inject;
import java.awt.*;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Slf4j
@PluginDescriptor(
        name = "Guardians of the Rift Helper",
        description = "Show info about the Guardians of the Rift minigame",
        tags = {"minigame", "overlay", "guardians of the rift", "gotr"}
)
public class GuardiansOfTheRiftHelperPlugin extends Plugin {
    @Inject
    private Client client;

    @Inject
    private GuardiansOfTheRiftHelperConfig config;

    @Inject
    private OverlayManager overlayManager;

    @Inject
    private GuardiansOfTheRiftHelperOverlay overlay;

    @Inject
    private GuardiansOfTheRiftHelperPanel panel;

    @Inject
    private GuardiansOfTheRiftHelperStartTimerOverlay startTimerOverlay;

    @Inject
    private GuardiansOfTheRiftHelperPortalOverlay portalOverlay;

    @Inject
    private Notifier notifier;

    private static final int MINIGAME_MAIN_REGION = 14484;

    private static final Set<Integer> GUARDIAN_IDS = GuardianInfo.ALL.stream().mapToInt(x -> x.gameObjectId).boxed().collect(Collectors.toSet());
    private static final Set<Integer> RUNE_IDS = GuardianInfo.ALL.stream().mapToInt(x -> x.runeId).boxed().collect(Collectors.toSet());
    private static final Set<Integer> TALISMAN_IDS = GuardianInfo.ALL.stream().mapToInt(x -> x.talismanId).boxed().collect(Collectors.toSet());

    private static final Set<Integer> CHARGED_CELL_ITEM_IDS = CellTileInfo.ALL.stream().mapToInt(x -> x.itemId).boxed().collect(Collectors.toSet());
    private static final Set<Integer> CELL_TILE_IDS = CellTileInfo.ALL.stream().mapToInt(x -> x.groundObjectId).boxed().collect(Collectors.toSet());

    private static final int GREAT_GUARDIAN_ID = 11403;

    private static final int CATALYTIC_GUARDIAN_STONE_ID = 26880;
    private static final int ELEMENTAL_GUARDIAN_STONE_ID = 26881;
    private static final int POLYELEMENTAL_GUARDIAN_STONE_ID = 26941;

    private static final int ELEMENTAL_ESSENCE_PILE_ID = 43722;
    private static final int CATALYTIC_ESSENCE_PILE_ID = 43723;

    private static final int UNCHARGED_CELL_ITEM_ID = 26882;
    private static final int UNCHARGED_CELL_GAMEOBJECT_ID = 43732;
    private static final int CHISEL_ID = 1755;
    private static final int OVERCHARGED_CELL_ID = 26886;


    private static final int DEPOSIT_POOL_ID = 43696;

    private static final int GUARDIAN_ACTIVE_ANIM = 9363;

    private static final int PARENT_WIDGET_ID = 48889858;
    private static final int CATALYTIC_RUNE_WIDGET_ID = 48889876;
    private static final int ELEMENTAL_RUNE_WIDGET_ID = 48889879;
    private static final int GUARDIAN_COUNT_WIDGET_ID = 48889886;
    private static final int PORTAL_WIDGET_ID = 48889882;
    private static final int PORTAL_TEXT_WIDGET_ID = 48889884;

    private final static int PORTAL_SPRITE_ID = 4368;

    private static final int PORTAL_ID = 43729;

    private static final String REWARD_POINT_REGEX = "Total elemental energy:[^>]+>([\\d,]+).*Total catalytic energy:[^>]+>([\\d,]+).";
    private static final Pattern REWARD_POINT_PATTERN = Pattern.compile(REWARD_POINT_REGEX);
    private static final String CHECK_POINT_REGEX = "You have (\\d+) catalytic energy and (\\d+) elemental energy";
    private static final Pattern CHECK_POINT_PATTERN = Pattern.compile(CHECK_POINT_REGEX);

    private static final int DIALOG_WIDGET_GROUP = 229;
    private static final int DIALOG_WIDGET_MESSAGE = 1;
    private static final String BARRIER_DIALOG_FINISHING_UP = "It looks like the adventurers within are just finishing up. You must<br>wait until they are done to join.";

    @Getter(AccessLevel.PACKAGE)
    private final Set<GameObject> guardians = new HashSet<>();
    @Getter(AccessLevel.PACKAGE)
    private final Set<GameObject> activeGuardians = new HashSet<>();
    @Getter(AccessLevel.PACKAGE)
    private final Set<Integer> inventoryTalismans = new HashSet<>();
    @Getter(AccessLevel.PACKAGE)
    private final Set<GroundObject> cellTiles = new HashSet<>();
    @Getter(AccessLevel.PACKAGE)
    private NPC greatGuardian;
    @Getter(AccessLevel.PACKAGE)
    private GameObject unchargedCellTable;
    @Getter(AccessLevel.PACKAGE)
    private GameObject depositPool;
    @Getter(AccessLevel.PACKAGE)
    private GameObject catalyticEssencePile;
    @Getter(AccessLevel.PACKAGE)
    private GameObject elementalEssencePile;
    @Getter(AccessLevel.PACKAGE)
    private GameObject portal;

    @Getter(AccessLevel.PACKAGE)
    private boolean isInMinigame;
    @Getter(AccessLevel.PACKAGE)
    private boolean isInMainRegion;
    @Getter(AccessLevel.PACKAGE)
    private boolean hasAnyStones = false;
    @Getter(AccessLevel.PACKAGE)
    private boolean outlineUnchargedCellTable = false;
    @Getter(AccessLevel.PACKAGE)
    private boolean hasAnyRunes = false;
    @Getter(AccessLevel.PACKAGE)
    private boolean hasAnyChargedCells = false;
    @Getter(AccessLevel.PACKAGE)
    private Optional<CellType> chargedCellType;
    @Getter(AccessLevel.PACKAGE)
    private boolean hasAnyGuardianEssence = false;
    @Getter(AccessLevel.PACKAGE)
    private boolean hasFullInventory = false;
    @Getter(AccessLevel.PACKAGE)
    private boolean shouldMakeGuardian = false;
    @Getter(AccessLevel.PACKAGE)
    private boolean isFirstPortal = false;

    @Getter(AccessLevel.PACKAGE)
    private int elementalRewardPoints;
    @Getter(AccessLevel.PACKAGE)
    private int catalyticRewardPoints;
    @Getter(AccessLevel.PACKAGE)
    private int currentElementalRewardPoints;
    @Getter(AccessLevel.PACKAGE)
    private int currentCatalyticRewardPoints;

    @Getter(AccessLevel.PACKAGE)
    private Optional<Instant> portalSpawnTime = Optional.empty();
    @Getter(AccessLevel.PACKAGE)
    private Optional<Instant> lastPortalDespawnTime = Optional.empty();
    @Getter(AccessLevel.PACKAGE)
    private Optional<Instant> nextGameStart = Optional.empty();
    private Optional<Instant> gameStarted = Optional.empty();

    @Getter(AccessLevel.PACKAGE)
    private int lastRewardUsage;

    private boolean hasNotifiedGameStart = true;
    private boolean hasNotifiedFirstRift = true;
    private int previousGuardianFragments = 0;


    private String portalLocation;
    private int lastElementalRuneSprite;
    private int lastCatalyticRuneSprite;
    private boolean areGuardiansNeeded = false;
    private int entryBarrierClickCooldown = 0;

    private final Map<String, String> expandCardinal = new HashMap<>() {
        {
            put("S", "south");
            put("SW", "south west");
            put("W", "west");
            put("NW", "north west");
            put("N", "north");
            put("NE", "north east");
            put("E", "east");
            put("SE", "south east");
        }
    };

    private boolean checkInMinigame() {
        GameState gameState = client.getGameState();
        if (gameState != GameState.LOGGED_IN && gameState != GameState.LOADING) {
            return false;
        }

        Widget elementalRuneWidget = client.getWidget(PARENT_WIDGET_ID);
        return elementalRuneWidget != null;
    }

    private boolean checkInMainRegion() {
        int[] currentMapRegions = client.getMapRegions();
        return Arrays.stream(currentMapRegions).anyMatch(x -> x == MINIGAME_MAIN_REGION);
    }

    @Override
    protected void startUp() {
        CELL_TILE_IDS.add(CellTileInfo.WEAK.groundObjectId);
        overlayManager.add(overlay);
        overlayManager.add(panel);
        overlayManager.add(startTimerOverlay);
        overlayManager.add(portalOverlay);
        isInMinigame = true;

    }

    @Override
    protected void shutDown() {
        overlayManager.remove(overlay);
        overlayManager.remove(panel);
        overlayManager.remove(startTimerOverlay);
        overlayManager.remove(portalOverlay);
        reset();
    }

    @Subscribe
    public void onItemContainerChanged(ItemContainerChanged event) {
        if ((!isInMainRegion && !isInMinigame) || event.getItemContainer() != client.getItemContainer(InventoryID.INVENTORY)) {
            return;
        }

        Item[] items = event.getItemContainer().getItems();
        hasAnyStones = Arrays.stream(items).anyMatch(x -> x.getId() == ELEMENTAL_GUARDIAN_STONE_ID || x.getId() == CATALYTIC_GUARDIAN_STONE_ID || x.getId() == POLYELEMENTAL_GUARDIAN_STONE_ID);
        outlineUnchargedCellTable = Arrays.stream(items).noneMatch(x -> x.getId() == UNCHARGED_CELL_ITEM_ID);
        shouldMakeGuardian = Arrays.stream(items).anyMatch(x -> x.getId() == CHISEL_ID) && Arrays.stream(items).anyMatch(x -> x.getId() == OVERCHARGED_CELL_ID) && areGuardiansNeeded;
        hasAnyRunes = Arrays.stream(items).anyMatch(x -> RUNE_IDS.contains(x.getId()));
        hasAnyChargedCells = Arrays.stream(items).anyMatch(x -> CHARGED_CELL_ITEM_IDS.contains(x.getId()));
        chargedCellType = Arrays.stream(items)
                .filter(x -> CHARGED_CELL_ITEM_IDS.contains(x.getId()))
                .map(x -> CellTileInfo.ALL.stream().filter(c -> c.itemId == x.getId()).findFirst().get().cellType).findFirst();

        hasAnyGuardianEssence = Arrays.stream(items).anyMatch(x -> x.getId() == ItemID.GUARDIAN_ESSENCE);
        hasFullInventory = Arrays.stream(items).allMatch(x -> x.getId() != -1);

        List<Integer> invTalismans = Arrays.stream(items).mapToInt(x -> x.getId()).filter(x -> TALISMAN_IDS.contains(x)).boxed().collect(Collectors.toList());
        if (invTalismans.stream().count() != inventoryTalismans.stream().count()) {
            inventoryTalismans.clear();
            inventoryTalismans.addAll(invTalismans);
        }

        if (config.notifyGuardianFragments().isEnabled() && config.guardianFragmentsAmount() > 0) {
            var optNewFragments = Arrays.stream(items).filter(x -> x.getId() == ItemID.GUARDIAN_FRAGMENTS).findFirst();
            if (optNewFragments.isPresent()) {
                var quantity = optNewFragments.get().getQuantity();
                if (quantity >= config.guardianFragmentsAmount() && previousGuardianFragments < config.guardianFragmentsAmount()) {
                    notifier.notify(config.notifyGuardianFragments(), "You have mined " + config.guardianFragmentsAmount() + "+ guardian fragments.");
                }
                previousGuardianFragments = quantity;
            }
        }
    }

    @Subscribe
    public void onGameTick(GameTick tick) {
        isInMinigame = checkInMinigame();
        isInMainRegion = checkInMainRegion();
        NotifyBeforeGameStart();
        NotifyBeforeFirstAltar();
        if (entryBarrierClickCooldown > 0) {
            entryBarrierClickCooldown--;
        }

        activeGuardians.removeIf(ag -> {
            Animation anim = ((DynamicObject) ag.getRenderable()).getAnimation();
            return anim == null || anim.getId() != GUARDIAN_ACTIVE_ANIM;
        });

        for (GameObject guardian : guardians) {
            Animation animation = ((DynamicObject) guardian.getRenderable()).getAnimation();
            if (animation != null && animation.getId() == GUARDIAN_ACTIVE_ANIM) {
                activeGuardians.add(guardian);
            }
        }

        Widget elementalRuneWidget = client.getWidget(ELEMENTAL_RUNE_WIDGET_ID);
        Widget catalyticRuneWidget = client.getWidget(CATALYTIC_RUNE_WIDGET_ID);
        Widget guardianCountWidget = client.getWidget(GUARDIAN_COUNT_WIDGET_ID);
        Widget portalTextWidget = client.getWidget(PORTAL_TEXT_WIDGET_ID);

        lastElementalRuneSprite = parseRuneWidget(elementalRuneWidget, lastElementalRuneSprite);
        lastCatalyticRuneSprite = parseRuneWidget(catalyticRuneWidget, lastCatalyticRuneSprite);

        if (guardianCountWidget != null) {
            String text = guardianCountWidget.getText();
            areGuardiansNeeded = text != null && !text.contains("10/10");
        }

        if (portalTextWidget != null && !portalTextWidget.isHidden()) {
            if (!portalSpawnTime.isPresent()) {
                lastPortalDespawnTime = Optional.empty();
                if (isFirstPortal) {
                    isFirstPortal = false;
                }
                if (config.notifyPortalSpawn().isEnabled()) {
                    String compass = portalTextWidget.getText().split(" ")[0];
                    String full = expandCardinal.getOrDefault(compass, "unknown");
                    notifier.notify(config.notifyPortalSpawn(), "A portal has spawned in the " + full + ".");
                }
            }
            portalLocation = portalTextWidget.getText();
            portalSpawnTime = portalSpawnTime.isPresent() ? portalSpawnTime : Optional.of(Instant.now());
        } else if (elementalRuneWidget != null && !elementalRuneWidget.isHidden()) {
            if (portalSpawnTime.isPresent()) {
                lastPortalDespawnTime = Optional.of(Instant.now());
            }
            portalLocation = null;
            portalSpawnTime = Optional.empty();
        }

        Widget dialog = client.getWidget(DIALOG_WIDGET_GROUP, DIALOG_WIDGET_MESSAGE);
        if (dialog != null) {
            String dialogText = dialog.getText();
            if (dialogText.equals(BARRIER_DIALOG_FINISHING_UP)) {
                // Allow one click per tick while the portal is closed
                entryBarrierClickCooldown = 0;
            } else {
                final Matcher checkMatcher = CHECK_POINT_PATTERN.matcher(dialogText);
                if (checkMatcher.find(0)) {
                    //For some reason these are reversed compared to everything else
                    catalyticRewardPoints = Integer.parseInt(checkMatcher.group(1));
                    elementalRewardPoints = Integer.parseInt(checkMatcher.group(2));
                }
            }
        }
    }

    int parseRuneWidget(Widget runeWidget, int lastSpriteId) {
        if (runeWidget != null) {
            int spriteId = runeWidget.getSpriteId();
            if (spriteId != lastSpriteId) {
                if (lastSpriteId > 0) {
                    Optional<GuardianInfo> lastGuardian = GuardianInfo.ALL.stream().filter(g -> g.spriteId == lastSpriteId).findFirst();
                    if (lastGuardian.isPresent()) {
                        lastGuardian.get().despawn();
                    }
                }

                Optional<GuardianInfo> optionalGuardian = GuardianInfo.ALL.stream().filter(g -> g.spriteId == spriteId).findFirst();
                if (optionalGuardian.isPresent()) {
                    var currentGuardian = optionalGuardian.get();
                    currentGuardian.spawn();
                    if (currentGuardian.notifyFunc.apply(config).isEnabled()) {
                        var condition = config.notifyGuardianCondition();
                        if (condition == NotifyGuardianCondition.Always || (condition == NotifyGuardianCondition.Have_Guardian_Essence && hasAnyGuardianEssence) || (condition == NotifyGuardianCondition.Full_Inventory && hasFullInventory)) {
                            notifier.notify(currentGuardian.notifyFunc.apply(config), "A portal to the " + currentGuardian.name + " altar has opened.");
                        }
                    }
                }
            }

            return spriteId;
        }
        return lastSpriteId;
    }

    @Subscribe
    public void onGameObjectSpawned(GameObjectSpawned event) {
        GameObject gameObject = event.getGameObject();
        if (GUARDIAN_IDS.contains(event.getGameObject().getId())) {
            guardians.removeIf(g -> g.getId() == gameObject.getId());
            activeGuardians.removeIf(g -> g.getId() == gameObject.getId());
            guardians.add(gameObject);
        }

        if (gameObject.getId() == UNCHARGED_CELL_GAMEOBJECT_ID) {
            unchargedCellTable = gameObject;
        }

        if (gameObject.getId() == DEPOSIT_POOL_ID) {
            depositPool = gameObject;
        }

        if (gameObject.getId() == ELEMENTAL_ESSENCE_PILE_ID) {
            elementalEssencePile = gameObject;
        }

        if (gameObject.getId() == CATALYTIC_ESSENCE_PILE_ID) {
            catalyticEssencePile = gameObject;
        }

        if (gameObject.getId() == PORTAL_ID) {
            portal = gameObject;
            if (config.notifyPortalSpawn().isEnabled()) {
                // The hint arrow is cleared under the following circumstances:
                // 1. Player enters the portal
                // 2. Plugin is "reset()"
                // 3. The portal despawns
                client.setHintArrow(portal.getWorldLocation());
            }
        }
    }

    @Subscribe
    public void onGameObjectDespawned(GameObjectDespawned event) {
        if (event.getGameObject().getId() == PORTAL_ID) {
            client.clearHintArrow();
        }
    }

    @Subscribe
    public void onGroundObjectSpawned(GroundObjectSpawned event) {
        var groundObject = event.getGroundObject();

        if (CELL_TILE_IDS.contains(groundObject.getId())) {
            cellTiles.removeIf(x -> x.getWorldLocation().distanceTo(groundObject.getWorldLocation()) < 1);
            cellTiles.add(groundObject);
        }
    }

    @Subscribe
    public void onNpcSpawned(NpcSpawned npcSpawned) {
        NPC npc = npcSpawned.getNpc();
        if (npc.getId() == GREAT_GUARDIAN_ID) {
            greatGuardian = npc;
        }
    }


    @Subscribe
    public void onGameStateChanged(GameStateChanged event) {
        if (event.getGameState() == GameState.LOADING) {
            // on region changes the tiles get set to null
            reset();
        } else if (event.getGameState() == GameState.LOGIN_SCREEN) {
            isInMinigame = false;
        }
    }

    @Subscribe
    public void onVarbitChanged(VarbitChanged event) {
        if (!isInMainRegion && !isInMinigame) return;
        currentElementalRewardPoints = Math.max(0, client.getVarbitValue(13686));
        currentCatalyticRewardPoints = Math.max(0, client.getVarbitValue(13685));
    }

    @Subscribe
    public void onChatMessage(ChatMessage chatMessage) {
        if ((!isInMainRegion && !isInMinigame)) return;
        if (chatMessage.getType() != ChatMessageType.SPAM && chatMessage.getType() != ChatMessageType.GAMEMESSAGE)
            return;

        String msg = chatMessage.getMessage();
        if (msg.contains("You step through the portal")) {
            client.clearHintArrow();
        }
        if (msg.contains("The rift becomes active!")) {
            lastPortalDespawnTime = Optional.of(Instant.now());
            nextGameStart = Optional.empty();
            gameStarted = Optional.of(Instant.now());
            isFirstPortal = true;
            hasNotifiedFirstRift = false;
        } else if (msg.contains("The rift will become active in 30 seconds.")) {
            hasNotifiedGameStart = config.beforeGameStartSeconds() > 30;
            nextGameStart = Optional.of(Instant.now().plusSeconds(30));
        } else if (msg.contains("The rift will become active in 10 seconds.")) {
            nextGameStart = Optional.of(Instant.now().plusSeconds(10));
            hasNotifiedGameStart = config.beforeGameStartSeconds() > 10;
        } else if (msg.contains("The rift will become active in 5 seconds.")) {
            nextGameStart = Optional.of(Instant.now().plusSeconds(5));
            hasNotifiedGameStart = config.beforeGameStartSeconds() > 5;
        } else if (msg.contains("The Portal Guardians will keep their rifts open for another 30 seconds.")) {
            hasNotifiedGameStart = false;
            nextGameStart = Optional.of(Instant.now().plusSeconds(60));
        } else if (msg.contains("You found some loot:")) {
            elementalRewardPoints = Math.max(0, elementalRewardPoints - 1);
            catalyticRewardPoints = Math.max(0, catalyticRewardPoints - 1);
        }

        Matcher rewardPointMatcher = REWARD_POINT_PATTERN.matcher(msg);
        if (rewardPointMatcher.find()) {
            // Use replaceAll to remove thousands separators from the text
            elementalRewardPoints = Integer.parseInt(rewardPointMatcher.group(1).replaceAll(",", ""));
            catalyticRewardPoints = Integer.parseInt(rewardPointMatcher.group(2).replaceAll(",", ""));
        }
        //log.info(msg);
    }

    @Provides
    GuardiansOfTheRiftHelperConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(GuardiansOfTheRiftHelperConfig.class);
    }

    @Subscribe
    public void onMenuOptionClicked(MenuOptionClicked event) {
        if (!config.quickPassCooldown()) return;

        // Only allow one click on the entry barrier's quick-pass option for every 3 game ticks
        if (event.getId() == 43700 && event.getMenuAction().getId() == 5) {
            if (entryBarrierClickCooldown > 0) {
                event.consume();
            } else {
                entryBarrierClickCooldown = 3;
            }
        }
    }

    @Subscribe
    public void onOverheadTextChanged(OverheadTextChanged event) {
        if (!("Apprentice Tamara".equals(event.getActor().getName()) || "Apprentice Cordelia".equals(event.getActor().getName()))) {
            return;
        }
        if (config.muteApprentices()) {
            event.getActor().setOverheadText(" ");
        }
    }

    @Subscribe(priority = -1)
    public void onPostMenuSort(PostMenuSort e) {
        if (!isInMinigame && !isInMainRegion) {
            return;
        }
        if (client.isMenuOpen()) {
            return;
        }

        var menu = client.getMenu();

        SwapMenu(menu);

    }


    private void SwapMenu(Menu menu) {
        var entries = menu.getMenuEntries();
        if (config.hideGreatGuardianPowerUp() && !hasAnyStones) {
            entries = Arrays.stream(entries).filter(x -> !x.getOption().contains("Power-up") || !x.getTarget().contains("Great Guardian")).toArray(MenuEntry[]::new);
        }
        if (config.hideCellTilePlaceCell() && !hasAnyChargedCells) {
            entries = Arrays.stream(entries).filter(x -> !x.getOption().contains("Place-cell")).toArray(MenuEntry[]::new);
        }

        if (config.hideRuneUse() == MinigameLocation.Everywhere || (config.hideRuneUse() == MinigameLocation.Main_Room && isInMainRegion)) {
            entries = Arrays.stream(entries).filter(x -> !x.getOption().contains("Use") || !x.getTarget().toLowerCase(Locale.ROOT).contains(" rune") || x.getType() != MenuAction.WIDGET_TARGET).toArray(MenuEntry[]::new);
            var dropEntry = Arrays.stream(entries).filter(x -> x.getOption().contains("Drop") && x.getTarget().toLowerCase(Locale.ROOT).contains("rune")).findFirst();
            dropEntry.ifPresent(x -> x.setType(MenuAction.CC_OP));
        }

        menu.setMenuEntries(entries);
    }

    private void reset() {
        guardians.clear();
        activeGuardians.clear();
        cellTiles.clear();
        unchargedCellTable = null;
        depositPool = null;
        greatGuardian = null;
        catalyticEssencePile = null;
        elementalEssencePile = null;
        if (isInMinigame || isInMainRegion) {
            client.clearHintArrow();
        }
    }

    private void NotifyBeforeGameStart() {
        if (hasNotifiedGameStart || !nextGameStart.isPresent() || !config.notifyBeforeGameStart().isEnabled()) return;

        var start = nextGameStart.get();
        var secondsToStart = ChronoUnit.SECONDS.between(Instant.now(), start) - .5d;
        if (secondsToStart < config.beforeGameStartSeconds()) {
            if (config.beforeGameStartSeconds() > 0) {
                notifier.notify(config.notifyBeforeGameStart(), "The next game is starting in " + config.beforeGameStartSeconds() + " seconds!");
            } else {
                notifier.notify(config.notifyBeforeGameStart(), "The next game is starting now!");
            }
            hasNotifiedGameStart = true;
        }
    }

    private void NotifyBeforeFirstAltar() {
        if (hasNotifiedFirstRift || !gameStarted.isPresent() || !config.notifyBeforeFirstAltar().isEnabled()) return;

        var start = gameStarted.get();
        var secondsToRift = ChronoUnit.SECONDS.between(Instant.now(), start.plusSeconds(120)) - .5d;

        if (secondsToRift < config.beforeFirstAltarSeconds()) {
            if (config.beforeFirstAltarSeconds() > 0) {
                notifier.notify(config.notifyBeforeFirstAltar(), "The first altar is in " + config.beforeFirstAltarSeconds() + " seconds!");
            } else {
                notifier.notify(config.notifyBeforeFirstAltar(), "The first altar is opening now!");
            }
            hasNotifiedFirstRift = true;
        }
    }

    public Color getTimeSincePortalColor(int timeSincePortal) {
        if (isFirstPortal) {
            // first portal takes about 40 more seconds to spawn
            timeSincePortal -= 40;
        }
        if (timeSincePortal >= 108) {
            return Color.RED;
        } else if (timeSincePortal >= 85) {
            return Color.YELLOW;
        }
        return Color.GREEN;
    }

    public int getParentWidgetId() {
        return PARENT_WIDGET_ID;
    }

    public int getPortalWidgetId() {
        return PORTAL_WIDGET_ID;
    }

    public int getPortalSpriteId() {
        return PORTAL_SPRITE_ID;
    }

    public void drawCenteredString(Graphics g, String text, Rectangle rect, Optional<Color> textColor) {
        FontMetrics metrics = g.getFontMetrics();
        int x = rect.x + (rect.width - metrics.stringWidth(text)) / 2;
        int y = rect.y + ((rect.height - metrics.getHeight()) / 2) + metrics.getAscent();
        g.setColor(Color.BLACK);
        g.drawString(text, x + 1, y + 1);
        g.setColor(textColor.isPresent() ? textColor.get() : Color.WHITE);
        g.drawString(text, x, y);
    }

    public int potentialPointsElemental() {
        return potentialPoints(getElementalRewardPoints(), getCurrentElementalRewardPoints());
    }

    public int potentialPointsCatalytic() {
        return potentialPoints(getCatalyticRewardPoints(), getCurrentCatalyticRewardPoints());
    }

    private int potentialPoints(int savedPoints, int currentPoints) {
        if (currentPoints == 0) {
            return savedPoints;
        }
        return savedPoints += currentPoints / 100;
    }
}
