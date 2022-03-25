package com.datbear;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Provides;
import javax.inject.Inject;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.events.GameObjectSpawned;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GameTick;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;

import java.util.Arrays;
import java.util.HashSet;
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

	private static final Set<Integer> RIFT_MAP_REGIONS = ImmutableSet.of(14227,14228,14229,14484,14485,14739,14740,14741);
	private static final Set<Integer> GUARDIAN_IDS = ImmutableSet.of(43705, 43701, 43710, 43702, 43703, 43711, 43704, 43708, 43712, 43707, 43706, 43709, 43702);

	private static final int GUARDIAN_ACTIVE_ANIM = 9363;

	@Getter(AccessLevel.PACKAGE)
	private final Set<GameObject> guardians = new HashSet<>();
	@Getter(AccessLevel.PACKAGE)
	private final Set<GameObject> activeGuardians = new HashSet<>();

	@Getter(AccessLevel.PACKAGE)
	private boolean isInMainRegion;

	private boolean checkInMinigame(){
		GameState gameState = client.getGameState();
		if (gameState != GameState.LOGGED_IN
				&& gameState != GameState.LOADING)
		{
			return false;
		}

		return true;
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

		for(var guardian : guardians){
			Animation animation = ((DynamicObject) guardian.getRenderable()).getAnimation();
			if(animation != null && animation.getId() == GUARDIAN_ACTIVE_ANIM) {
				activeGuardians.add(guardian);
			}
		}
	}

	@Subscribe
	public void onGameObjectSpawned(GameObjectSpawned event)
	{
		if (!isInMinigame)
		{
			return;
		}

		var gameObject = event.getGameObject();
		if(GUARDIAN_IDS.contains(event.getGameObject().getId())) {
			guardians.removeIf(g -> g.getId() == gameObject.getId());
			activeGuardians.removeIf(g -> g.getId() == gameObject.getId());
			guardians.add(gameObject);
		}
	}


//	@Provides
//	GuardiansOfTheRiftHelperConfig provideConfig(ConfigManager configManager)
//	{
//		return configManager.getConfig(GuardiansOfTheRiftHelperConfig.class);
//	}
}
