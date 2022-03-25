package com.datbear;

import com.google.common.collect.ImmutableSet;

import javax.inject.Inject;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.events.*;
import net.runelite.api.widgets.Widget;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;
import org.pushingpixels.substance.internal.utils.WidgetUtilities;

import java.time.Instant;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Slf4j
@PluginDescriptor(
		name = "Guardians of the Rift Helper",
		description = "Show info about the Guardians of the Rift minigame",
		tags = {"minigame", "overlay", "guardians of the rift"}
)
public class GuardiansOfTheRiftHelperPlugin extends Plugin
{
	@Inject
	private Client client;

//	@Inject
//	private GuardiansOfTheRiftHelperConfig config;

	@Inject
	private OverlayManager overlayManager;

	@Inject
	private GuardiansOfTheRiftHelperOverlay overlay;

	private boolean isInMinigame;

	private static final int MINIGAME_MAIN_REGION = 14484;

	private static final Set<Integer> GUARDIAN_IDS = ImmutableSet.of(43705, 43701, 43710, 43702, 43703, 43711, 43704, 43708, 43712, 43707, 43706, 43709, 43702);
	private static final int GREAT_GUARDIAN_ID = 11403;

	private static final int CATALYTIC_GUARDIAN_STONE_ID = 26880;
	private static final int ELEMENTAL_GUARDIAN_STONE_ID = 26881;

	private static final int ELEMENTAL_ESSENCE_PILE_ID = 43722;
	private static final int CATALYTIC_ESSENCE_PILE_ID = 43723;

	private static final int UNCHARGED_CELL_ITEM_ID = 26882;
	private static final int UNCHARGED_CELL_GAMEOBJECT_ID = 43732;
	private static final int CHISEL_ID = 1755;
	private static final int OVERCHARGED_CELL_ID = 26886;

	private static final int GUARDIAN_ACTIVE_ANIM = 9363;

	private static final int CATALYTIC_RUNE_WIDGET_ID = 48889878;
	private static final int ELEMENTAL_RUNE_WIDGET_ID = 48889875;
	private static final int GUARDIAN_COUNT_WIDGET_ID = 48889885;

	private static final int ELEMENTAL_RUNE_INACTIVE_SPRITE = 4370;
	private static final int CATALYTIC_RUNE_INACTIVE_SPRITE = 4369;

	@Getter(AccessLevel.PACKAGE)
	private final Set<GameObject> guardians = new HashSet<>();
	@Getter(AccessLevel.PACKAGE)
	private final Set<GameObject> activeGuardians = new HashSet<>();
	@Getter(AccessLevel.PACKAGE)
	private NPC greatGuardian;
	@Getter(AccessLevel.PACKAGE)
	private GameObject unchargedCellTable;
	@Getter(AccessLevel.PACKAGE)
	private GameObject catalyticEssencePile;
	@Getter(AccessLevel.PACKAGE)
	private GameObject elementalEssencePile;

	@Getter(AccessLevel.PACKAGE)
	private boolean isInMainRegion;
	@Getter(AccessLevel.PACKAGE)
	private boolean outlineGreatGuardian = false;
	@Getter(AccessLevel.PACKAGE)
	private boolean outlineUnchargedCellTable = false;
	@Getter(AccessLevel.PACKAGE)
	private boolean shouldMakeGuardian = false;

	private int lastElementalRuneSprite;
	private int lastCatalyticRuneSprite;
	private boolean areGuardiansNeeded = false;

	private boolean checkInMinigame() {
		GameState gameState = client.getGameState();
		if (gameState != GameState.LOGGED_IN
				&& gameState != GameState.LOADING)
		{
			return false;
		}

		Widget elementalRuneWidget = client.getWidget(ELEMENTAL_RUNE_WIDGET_ID);
		return elementalRuneWidget != null && !elementalRuneWidget.isHidden();
	}

	private boolean checkInMainRegion(){
		int[] currentMapRegions = client.getMapRegions();
		return Arrays.stream(currentMapRegions).anyMatch(x -> x == MINIGAME_MAIN_REGION);
	}

	@Override
	protected void startUp()
	{
		overlayManager.add(overlay);
		isInMinigame = true;
	}

	@Override
	protected void shutDown() {
		overlayManager.remove(overlay);
		reset();
	}

	@Subscribe
	public void onItemContainerChanged(ItemContainerChanged event)
	{
		if (event.getItemContainer() != client.getItemContainer(InventoryID.INVENTORY))
		{
			return;
		}

		Item[] items = event.getItemContainer().getItems();
		if(Arrays.stream(items).anyMatch(x -> x.getId() == ELEMENTAL_GUARDIAN_STONE_ID || x.getId() == CATALYTIC_GUARDIAN_STONE_ID)){
			outlineGreatGuardian = true;
		} else {
			outlineGreatGuardian = false;
		}

		if(Arrays.stream(items).noneMatch(x -> x.getId() == UNCHARGED_CELL_ITEM_ID)) {
			outlineUnchargedCellTable = true;
		} else {
			outlineUnchargedCellTable = false;
		}

		if(Arrays.stream(items).anyMatch(x -> x.getId() == CHISEL_ID) && Arrays.stream(items).anyMatch(x -> x.getId() == OVERCHARGED_CELL_ID) && areGuardiansNeeded) {
			shouldMakeGuardian = true;
		} else {
			shouldMakeGuardian = false;
		}

	}

	@Subscribe
	public void onGameTick(GameTick tick)
	{
		isInMinigame = checkInMinigame();
		isInMainRegion = checkInMainRegion();

		activeGuardians.removeIf(ag -> {
			Animation anim = ((DynamicObject)ag.getRenderable()).getAnimation();
			return anim == null || anim.getId() != GUARDIAN_ACTIVE_ANIM;
		});

		for(GameObject guardian : guardians){
			Animation animation = ((DynamicObject) guardian.getRenderable()).getAnimation();
			if(animation != null && animation.getId() == GUARDIAN_ACTIVE_ANIM) {
				activeGuardians.add(guardian);
			}
		}

		Widget elementalRuneWidget = client.getWidget(ELEMENTAL_RUNE_WIDGET_ID);
		Widget catalyticRuneWidget = client.getWidget(CATALYTIC_RUNE_WIDGET_ID);
		Widget guardianCountWidget = client.getWidget(GUARDIAN_COUNT_WIDGET_ID);

		if(elementalRuneWidget != null && !elementalRuneWidget.isHidden()) {
			int spriteId = elementalRuneWidget.getSpriteId();
			if(spriteId != lastElementalRuneSprite) {
				if(lastElementalRuneSprite > 0) {
					Optional<GuardianInfo> lastGuardian = GuardianInfo.ALL.stream().filter(g -> g.spriteId == lastElementalRuneSprite).findFirst();
					if(lastGuardian.isPresent()) {
						lastGuardian.get().despawn();
					}
				}

				Optional<GuardianInfo> currentGuardian = GuardianInfo.ALL.stream().filter(g -> g.spriteId == spriteId).findFirst();
				if(currentGuardian.isPresent()) {
					log.info("updating elemental rune to: " + currentGuardian.get().spriteId + " " + Instant.now().toString());
					currentGuardian.get().spawn();
				}
			}

			lastElementalRuneSprite = spriteId;
		}

		if(catalyticRuneWidget != null && !catalyticRuneWidget.isHidden()){
			int spriteId = catalyticRuneWidget.getSpriteId();
			if(spriteId != lastCatalyticRuneSprite){
				if(lastCatalyticRuneSprite > 0){
					Optional<GuardianInfo> lastGuardian = GuardianInfo.ALL.stream().filter(g -> g.spriteId == lastCatalyticRuneSprite).findFirst();
					if(lastGuardian.isPresent()) {
						lastGuardian.get().despawn();
					}
				}

				Optional<GuardianInfo> currentGuardian = GuardianInfo.ALL.stream().filter(g -> g.spriteId == spriteId).findFirst();
				if(currentGuardian.isPresent()) {
					log.info("updating catalytic rune to: " + currentGuardian.get().spriteId + " " + Instant.now().toString());
					currentGuardian.get().spawn();
				}
			}

			lastCatalyticRuneSprite = spriteId;
		}

		if(guardianCountWidget != null && !guardianCountWidget.isHidden()) {
			areGuardiansNeeded = !guardianCountWidget.getText().equals("10/10");
		}
	}

	@Subscribe
	public void onGameObjectSpawned(GameObjectSpawned event)
	{
		GameObject gameObject = event.getGameObject();
		if(GUARDIAN_IDS.contains(event.getGameObject().getId())) {
			guardians.removeIf(g -> g.getId() == gameObject.getId());
			activeGuardians.removeIf(g -> g.getId() == gameObject.getId());
			guardians.add(gameObject);
		}

		if(gameObject.getId() == UNCHARGED_CELL_GAMEOBJECT_ID){
			unchargedCellTable = gameObject;
		}

		if(gameObject.getId() == ELEMENTAL_ESSENCE_PILE_ID){
			elementalEssencePile = gameObject;
		}

		if(gameObject.getId() == CATALYTIC_ESSENCE_PILE_ID){
			catalyticEssencePile = gameObject;
		}
	}

	@Subscribe
	public void onNpcSpawned(NpcSpawned npcSpawned) {
		NPC npc = npcSpawned.getNpc();
		if(npc.getId() == GREAT_GUARDIAN_ID){
			greatGuardian = npc;
		}
	}


	@Subscribe
	public void onGameStateChanged(GameStateChanged event)
	{
		if (event.getGameState() == GameState.LOADING)
		{
			// on region changes the tiles get set to null
			reset();
		}
		else if (event.getGameState() == GameState.LOGIN_SCREEN)
		{
			// Prevent code from running while logged out.
			isInMinigame = false;
		}
	}

	private void reset(){
		guardians.clear();
		activeGuardians.clear();
		unchargedCellTable = null;
		greatGuardian = null;
		catalyticEssencePile = null;
		elementalEssencePile = null;
	}

//	@Provides
//	GuardiansOfTheRiftHelperConfig provideConfig(ConfigManager configManager)
//	{
//		return configManager.getConfig(GuardiansOfTheRiftHelperConfig.class);
//	}
}
