package com.datbear;

import net.runelite.client.config.*;

import java.awt.*;

@ConfigGroup("guardiansOfTheRiftHelper")
public interface GuardiansOfTheRiftHelperConfig extends Config {
    @ConfigSection(
            name = "General Notifications",
            closedByDefault = false,
            position = 0,
            description = "Choose when you are notified."
    )
    String generalNotifications = "generalNotifications";

    @ConfigSection(
            name = "Outlines",
            description = "All options relating to colored outlines",
            position = 1,
            closedByDefault = true
    )
    String outlines = "outlines";

    @ConfigSection(
            name = "Overlays",
            description = "All options relating to overlays",
            position = 2,
            closedByDefault = true

    )
    String overlays = "overlays";

    @ConfigSection(
            name = "Guardian Render Style",
            closedByDefault = true,
            position = 3,
            description = "Highlighting style"
    )
    String style = "style";

    @ConfigSection(
            name = "Guardian Active Notifications",
            closedByDefault = true,
            position = 4,
            description = "Choose when you are notified about guardians becoming active."
    )
    String guardianNotifications = "guardianNotifications";

    @ConfigItem(
            keyName = "portalSpawn",
            name = "Notify on portal spawn",
            description = "Notifies you when a portal spawns.",
            position = 0,
            section = generalNotifications
    )
    default boolean notifyPortalSpawn() {
        return true;
    }

    @ConfigItem(
            keyName = "notifyBeforeGame",
            name = "Notify before game start",
            description = "Notifies you the specified number of seconds before the next game starts.",
            position = 1,
            section = generalNotifications
    )
    default boolean notifyBeforeGameStart() {
        return false;
    }

    @ConfigItem(
            keyName = "beforeGameSeconds",
            name = "Before game seconds",
            description = "Notifies you this many seconds before the next game starts.",
            position = 2,
            section = generalNotifications
    )
    @Range(min = 0, max = 59)
    default int beforeGameStartSeconds() {
        return 0;
    }

    @ConfigItem(
            keyName = "notifyBeforeFirstAltar",
            name = "Notify before first altar",
            description = "Notifies you the specified number of seconds before the game's first altar opens.",
            position = 3,
            section = generalNotifications
    )
    @Range(min = 0, max = 119)
    default boolean notifyBeforeFirstAltar() {
        return false;
    }

    @ConfigItem(
            keyName = "beforeFirstAltarSeconds",
            name = "First altar seconds",
            description = "Notifies you this many seconds before the game's first altar opens.",
            position = 4,
            section = generalNotifications
    )
    @Range(min = 0, max = 59)
    default int beforeFirstAltarSeconds() {
        return 0;
    }



    @ConfigItem(
            keyName = "notifyGuardianFragments",
            name = "Notify on Guardian Fragments",
            description = "Notifies you after you mine the specified amount of guardian fragments.",
            position = 4,
            section = generalNotifications
    )
    default boolean notifyGuardianFragments() {
        return true;
    }

    @ConfigItem(
            keyName = "guardianFragmentsAmount",
            name = "Guardian Fragments",
            description = "Notifies you when you hit the specified amount of guardian fragments in your inventory.",
            position = 5,
            section = generalNotifications
    )
    default int guardianFragmentsAmount() {
        return 0;
    }


    @ConfigItem(
            position = 1,
            keyName = "guardianBorderWidth",
            name = "Border Width",
            description = "Width of the highlighted NPC border",
            section = style
    )
    default int guardianBorderWidth() {
        return 2;
    }

    @ConfigItem(
            position = 2,
            keyName = "guardianOutlineFeather",
            name = "Outline feather",
            description = "Specify between 0-4 how much of the model outline should be faded",
            section = style
    )
    @Range(
            min = 0,
            max = 4
    )
    default int guardianOutlineFeather() {
        return 0;
    }

    @ConfigItem(
            position = 0,
            keyName = "guardianOutline",
            name = "Highlight outline",
            description = "Configures whether or not NPC should be highlighted by outline",
            section = style
    )
    default boolean guardianOutline() {
        return true;
    }

    @ConfigItem(
            keyName = "muteApprentices",
            name = "Mute game help messages",
            description = "Mutes the over head messages of the apprentices giving game advice."
    )
    default boolean muteApprentices() {
        return true;
    }

    @ConfigItem(
            keyName = "outlineCellTable",
            name = "Outline cell table",
            description = "Outlines the Cell table when you have no cells remaining.",
            section = outlines
    )
    default boolean outlineCellTable() {
        return true;
    }

    @ConfigItem(
            keyName = "outlineDepositPool",
            name = "Outline deposit pool",
            description = "Outlines the Deposit Pool when you have runes in your inventory.",
            section = outlines
    )
    default boolean outlineDepositPool() {
        return true;
    }

    @ConfigItem(
            keyName = "outlineGreatGuardian",
            name = "Outline Great Guardian",
            description = "Outlines the Great Guardian when you have elemental or catalytic essence in your inventory.",
            section = outlines
    )
    default boolean outlineGreatGuardian() {
        return true;
    }

    @ConfigItem(
            keyName = "pointBalanceHelper",
            name = "Balance Helper",
            description = "Highlights the guardian needed to keep points balanced or highest tier",
            section = outlines
    )
    default boolean pointBalanceHelper() {
        return false;
    }


    @ConfigItem(
            keyName = "quickPassCooldown",
            name = "Add cooldown to Quick-Pass",
            description = "Adds a 3 tick delay to the Quick-Pass menu option so you don't enter/leave by spam clicking the gate with Menu Entry Swapper's quick-pass option enabled."
    )
    default boolean quickPassCooldown() {
        return true;
    }

    @Alpha
    @ConfigItem(
            keyName = "elementalGuardianColor",
            name = "Elemental outline",
            description = "Color of the outline on the active elemental guardian.",
            section = outlines
    )
    default Color elementalGuardianColor() {
        return Color.GREEN;
    }

    @Alpha
    @ConfigItem(
            keyName = "catalyticGuardianColor",
            name = "Catalytic outline",
            description = "Color of the outline on the active catalytic guardian.",
            section = outlines
    )
    default Color catalyticGuardianColor() {
        return Color.RED;
    }


    @ConfigItem(
            keyName = "outlineGuardiansByTier",
            name = "Color guardians by cell tier",
            description = "Outlines active portal guardians with colors based on their Cell charge tiers instead of Elemental vs Catalytic.",
            position = 2,
            section = outlines
    )
    default boolean colorGuardiansByTier() {
        return false;
    }

    @Alpha
    @ConfigItem(
            keyName = "weakGuardianColor",
            name = "Weak outline",
            description = "Color of the outline on an active weak guardian.",
            position = 3,
            section = outlines
    )
    default Color weakGuardianColor() {
        return Color.WHITE;
    }

    @Alpha
    @ConfigItem(
            keyName = "mediumGuardianColor",
            name = "Medium outline",
            description = "Color of the outline on an active medium guardian.",
            position = 4,
            section = outlines
    )
    default Color mediumGuardianColor() {
        return Color.BLUE;
    }

    @Alpha
    @ConfigItem(
            keyName = "strongGuardianColor",
            name = "Strong outline",
            description = "Color of the outline on an active strong guardian.",
            position = 5,
            section = outlines
    )
    default Color strongGuardianColor() {
        return Color.GREEN;
    }

    @Alpha
    @ConfigItem(
            keyName = "overchargedGuardianColor",
            name = "Overcharged outline",
            description = "Color of the outline on an active overcharged guardian.",
            position = 6,
            section = outlines
    )
    default Color overchargedGuardianColor() {
        return Color.RED;
    }

    @ConfigItem(
            keyName = "colorGuardiansWithInsufficientRunecraftingLevel",
            name = "Recolor Unusable Guardians",
            description = "Outlines active portal guardians with this color if the player is not a high enough Runecrafting level to use them.",
            position = 7,
            section = outlines
    )
    default boolean colorGuardiansWithInsufficientRunecraftingLevel() { return false; }

    @Alpha
    @ConfigItem(
            keyName = "colorGuardiansWithInsufficientRunecraftingLevelColor",
            name = "Unusable Guardian Colors",
            description = "Color of the outline on the active guardian if it is too high level.",
            position = 8,
            section = outlines
    )
    default Color colorGuardiansWithInsufficientRunecraftingLevelColor()
    {
        return Color.PINK;
    }
  
    @Alpha
    @ConfigItem(
            keyName = "essencePileColor",
            name = "Essence pile outline",
            description = "Color of the outline on essence piles.",
            position = 9,
            section = outlines
    )
    default Color essencePileColor() {
        return Color.GREEN;
    }

    @ConfigItem(
            keyName = "startTimerOverlayLocation",
            name = "Start Timer Overlay Location",
            description = "Toggles the start timer overlay location.",
            position = 10,
            section = overlays
    )
    default TimerOverlayLocation startTimerOverlayLocation() {
        return TimerOverlayLocation.InfoBox;
    }

    @ConfigItem(
            keyName = "inactivePortalOverlayLocation",
            name = "Inactive Portal Overlay Location",
            description = "Toggles the inactive portal overlay location.",
            position = 11,
            section = overlays
    )
    default TimerOverlayLocation inactivePortalOverlayLocation() {
        return TimerOverlayLocation.InfoBox;
    }

    @ConfigItem(
            keyName = "showPointsOverlay",
            name = "Show Points Overlay",
            description = "Toggles the points overlay.",
            position = 12,
            section = overlays
    )
    default boolean showPointsOverlay() {
        return true;
    }

    @ConfigItem(
            keyName = "potentialPoints",
            name = "Show potential points",
            description = "Show tallied up points during a game",
            position = 13,
            section = overlays
    )
    default boolean potentialPoints() {
        return true;
    }

    @ConfigItem(
            keyName = "highlightPotential",
            name = "Highlight potential points",
            description = "Highlight potential points depending on balance",
            position = 14,
            section = overlays
    )
    default boolean highlightPotential() {
        return true;
    }

    @Alpha
    @ConfigItem(
            keyName = "potentialUnbalanceColor",
            name = "Unbalanced potential color",
            description = "Color to highlight potential points when unbalanced",
            position = 15,
            section = overlays
    )
    default Color potentialUnbalanceColor() {
        return Color.RED;
    }

    @Alpha
    @ConfigItem(
            keyName = "potentialBalanceColor",
            name = "Balanced potential color",
            description = "Color to highlight potential points when balanced",
            position = 16,
            section = overlays
    )
    default Color potentialBalanceColor() {
        return Color.GREEN;
    }

    @ConfigItem(
            keyName = "airSpawn",
            name = "Notify on Air Guardian",
            description = "Notifies you when an air guardian opens.",
            position = 1,
            section = guardianNotifications
    )
    default boolean notifyAirGuardian() {
        return false;
    }

    @ConfigItem(
            keyName = "mindSpawn",
            name = "Notify on Mind Guardian",
            description = "Notifies you when an mind guardian opens.",
            position = 2,
            section = guardianNotifications
    )
    default boolean notifyMindGuardian() {
        return false;
    }

    @ConfigItem(
            keyName = "waterSpawn",
            name = "Notify on Water Guardian",
            description = "Notifies you when a water guardian opens.",
            position = 3,
            section = guardianNotifications
    )
    default boolean notifyWaterGuardian() {
        return false;
    }

    @ConfigItem(
            keyName = "earthSpawn",
            name = "Notify on Earth Guardian",
            description = "Notifies you when an earth guardian opens.",
            position = 4,
            section = guardianNotifications
    )
    default boolean notifyEarthGuardian() {
        return false;
    }

    @ConfigItem(
            keyName = "fireSpawn",
            name = "Notify on Fire Guardian",
            description = "Notifies you when a fire guardian opens.",
            position = 5,
            section = guardianNotifications
    )
    default boolean notifyFireGuardian() {
        return false;
    }

    @ConfigItem(
            keyName = "bodySpawn",
            name = "Notify on Body Guardian",
            description = "Notifies you when a body guardian opens.",
            position = 6,
            section = guardianNotifications
    )
    default boolean notifyBodyGuardian() {
        return false;
    }

    @ConfigItem(
            keyName = "cosmicSpawn",
            name = "Notify on Cosmic Guardian",
            description = "Notifies you when a cosmic guardian opens.",
            position = 7,
            section = guardianNotifications
    )
    default boolean notifyCosmicGuardian() {
        return false;
    }

    @ConfigItem(
            keyName = "chaosSpawn",
            name = "Notify on Chaos Guardian",
            description = "Notifies you when a chaos guardian opens.",
            position = 8,
            section = guardianNotifications
    )
    default boolean notifyChaosGuardian() {
        return false;
    }

    @ConfigItem(
            keyName = "natureSpawn",
            name = "Notify on Nature Guardian",
            description = "Notifies you when a nature guardian opens.",
            position = 9,
            section = guardianNotifications
    )
    default boolean notifyNatureGuardian() {
        return false;
    }

    @ConfigItem(
            keyName = "lawSpawn",
            name = "Notify on Law Guardian",
            description = "Notifies you when a law guardian opens.",
            position = 10,
            section = guardianNotifications
    )
    default boolean notifyLawGuardian() {
        return false;
    }

    @ConfigItem(
            keyName = "deathSpawn",
            name = "Notify on Death Guardian",
            description = "Notifies you when a death guardian opens.",
            position = 11,
            section = guardianNotifications
    )
    default boolean notifyDeathGuardian() {
        return false;
    }

    @ConfigItem(
            keyName = "bloodSpawn",
            name = "Notify on Blood Guardian",
            description = "Notifies you when a blood guardian opens.",
            position = 12,
            section = guardianNotifications
    )
    default boolean notifyBloodGuardian() {
        return false;
    }
}
