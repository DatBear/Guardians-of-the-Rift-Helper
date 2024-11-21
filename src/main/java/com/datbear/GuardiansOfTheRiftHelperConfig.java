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
            name = "Menu Swaps",
            description = "All options relating to menu entry swaps",
            position = 1,
            closedByDefault = false
    )
    String menuSwaps = "menuSwaps";

    @ConfigSection(
            name = "Outlines",
            description = "All options relating to colored outlines",
            position = 2,
            closedByDefault = true
    )
    String outlines = "outlines";

    @ConfigSection(
            name = "Overlays",
            description = "All options relating to overlays",
            position = 3,
            closedByDefault = true

    )
    String overlays = "overlays";

    @ConfigSection(
            name = "Guardian Render Style",
            closedByDefault = true,
            position = 4,
            description = "Highlighting style"
    )
    String style = "style";

    @ConfigSection(
            name = "Guardian Active Notifications",
            closedByDefault = true,
            position = 5,
            description = "Choose when you are notified about guardians becoming active."
    )
    String guardianNotifications = "guardianNotifications";


    @ConfigItem(
            keyName = "quickPassCooldown",
            name = "Add cooldown to Quick-Pass",
            description = "Adds a 3 tick delay to the Quick-Pass menu option so you don't enter/leave by spam clicking the gate with Menu Entry Swapper's quick-pass option enabled."
    )
    default boolean quickPassCooldown() {
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
            keyName = "portalSpawn",
            name = "Notify on portal spawn",
            description = "Notifies you when a portal spawns.",
            position = 0,
            section = generalNotifications
    )
    default Notification notifyPortalSpawn() {
        return Notification.ON;
    }

    @ConfigItem(
            keyName = "notifyBeforeGame",
            name = "Notify before game start",
            description = "Notifies you the specified number of seconds before the next game starts.",
            position = 1,
            section = generalNotifications
    )
    default Notification notifyBeforeGameStart() {
        return Notification.OFF;
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
    default Notification notifyBeforeFirstAltar() {
        return Notification.OFF;
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
    default Notification notifyGuardianFragments() {
        return Notification.ON;
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
            keyName = "hideGreatGuardianPowerUp",
            name = "Hide Power-Up",
            description = "Hides the left click Power-Up option on the Great Guardian when you can't power him up (no stones).",
            position = 1,
            section = menuSwaps
    )
    default boolean hideGreatGuardianPowerUp() {
        return false;
    }

    @ConfigItem(
            keyName = "hidePlaceCell",
            name = "Hide Place-cell",
            description = "Hides the left click Place-cell option on the cell tiles when you can't place a cell (no charged cells).",
            position = 2,
            section = menuSwaps
    )
    default boolean hideCellTilePlaceCell() {
        return false;
    }

    @ConfigItem(
            keyName = "hideRuneUse",
            name = "Drop Runes",
            description = "Hides the left click Use option on runes in the main game area.",
            position = 3,
            section = menuSwaps
    )
    default MinigameLocation hideRuneUse() {
        return MinigameLocation.Nowhere;
    }


    @ConfigItem(
            keyName = "guardianOutline",
            name = "Highlight outline",
            description = "Configures whether or not Guardian NPCs should be highlighted by outline.",
            section = style,
            position = 0
    )
    default boolean guardianOutline() {
        return true;
    }

    @ConfigItem(
            keyName = "guardianBorderWidth",
            name = "Border Width",
            description = "Width of the highlighted NPC border.",
            section = style,
            position = 1
    )
    default int guardianBorderWidth() {
        return 2;
    }

    @ConfigItem(

            keyName = "guardianOutlineFeather",
            name = "Outline feather",
            description = "Specify between 0-4 how much of the outline should be faded.",
            section = style,
            position = 2
    )
    @Range(min = 0, max = 4)
    default int guardianOutlineFeather() {
        return 0;
    }

    @ConfigItem(

            keyName = "guardianShowRuneIcons",
            name = "Show Rune Icons",
            description = "Toggles whether or not to show rune icons above guardians.",
            section = style,
            position = 3
    )
    default boolean guardianShowRuneIcons() {
        return true;
    }


    @ConfigItem(
            keyName = "outlineCellTable",
            name = "Outline cell table",
            description = "Outlines the Cell table when you have no cells remaining.",
            section = outlines,
            position = 1
    )
    default boolean outlineCellTable() {
        return true;
    }

    @ConfigItem(
            keyName = "outlineDepositPool",
            name = "Outline deposit pool",
            description = "Outlines the Deposit Pool when you have runes in your inventory.",
            section = outlines,
            position = 2
    )
    default boolean outlineDepositPool() {
        return true;
    }

    @ConfigItem(
            keyName = "outlineGreatGuardian",
            name = "Outline Great Guardian",
            description = "Outlines the Great Guardian when you have elemental or catalytic essence in your inventory.",
            section = outlines,
            position = 2
    )
    default boolean outlineGreatGuardian() {
        return true;
    }

    @Alpha
    @ConfigItem(
            keyName = "elementalGuardianColor",
            name = "Elemental outline",
            description = "Color of the outline on the active elemental guardian.",
            section = outlines,
            position = 3
    )
    default Color elementalGuardianColor() {
        return Color.GREEN;
    }

    @Alpha
    @ConfigItem(
            keyName = "catalyticGuardianColor",
            name = "Catalytic outline",
            description = "Color of the outline on the active catalytic guardian.",
            section = outlines,
            position = 4
    )
    default Color catalyticGuardianColor() {
        return Color.RED;
    }


    @ConfigItem(
            keyName = "outlineGuardiansByTier",
            name = "Color guardians by cell tier",
            description = "Outlines active portal guardians with colors based on their Cell charge tiers instead of Elemental vs Catalytic.",
            position = 5,
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
            position = 6,
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
            position = 7,
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
            position = 8,
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
            position = 9,
            section = outlines
    )
    default Color overchargedGuardianColor() {
        return Color.RED;
    }

    @ConfigItem(
            keyName = "colorGuardiansWithInsufficientRunecraftingLevel",
            name = "Recolor Unusable Guardians",
            description = "Outlines active portal guardians with this color if the player is not a high enough Runecrafting level to use them.",
            position = 10,
            section = outlines
    )
    default boolean colorGuardiansWithInsufficientRunecraftingLevel() {
        return false;
    }

    @Alpha
    @ConfigItem(
            keyName = "colorGuardiansWithInsufficientRunecraftingLevelColor",
            name = "Unusable Guardian Colors",
            description = "Color of the outline on the active guardian if it is too high level.",
            position = 11,
            section = outlines
    )
    default Color colorGuardiansWithInsufficientRunecraftingLevelColor() {
        return Color.PINK;
    }

    @ConfigItem(
            keyName = "pointBalanceHelper",
            name = "Balance Helper",
            description = "Highlights the guardian needed to keep points balanced or highest tier",
            position = 12,
            section = outlines
    )
    default boolean pointBalanceHelper() {
        return false;
    }

    @Alpha
    @ConfigItem(
            keyName = "essencePileColor",
            name = "Essence pile outline",
            description = "Color of the outline on essence piles.",
            position = 13,
            section = outlines
    )
    default Color essencePileColor() {
        return Color.GREEN;
    }


    @ConfigItem(
            keyName = "startTimerOverlayLocation",
            name = "Start Timer Overlay Location",
            description = "Toggles the start timer overlay location.",
            position = 1,
            section = overlays
    )
    default TimerOverlayLocation startTimerOverlayLocation() {
        return TimerOverlayLocation.Info_Box;
    }

    @ConfigItem(
            keyName = "inactivePortalOverlayLocation",
            name = "Inactive Portal Overlay Location",
            description = "Toggles the inactive portal overlay location.",
            position = 2,
            section = overlays
    )
    default TimerOverlayLocation inactivePortalOverlayLocation() {
        return TimerOverlayLocation.Info_Box;
    }

    @ConfigItem(
            keyName = "showPointsOverlay",
            name = "Show Points Overlay",
            description = "Toggles the points overlay.",
            position = 3,
            section = overlays
    )
    default boolean showPointsOverlay() {
        return true;
    }

    @ConfigItem(
            keyName = "potentialPoints",
            name = "Show potential points",
            description = "Show tallied up points during a game",
            position = 4,
            section = overlays
    )
    default boolean potentialPoints() {
        return true;
    }

    @ConfigItem(
            keyName = "highlightPotential",
            name = "Highlight potential points",
            description = "Highlight potential points depending on balance",
            position = 5,
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
            position = 6,
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
            position = 7,
            section = overlays
    )
    default Color potentialBalanceColor() {
        return Color.GREEN;
    }





    @ConfigItem(
            keyName = "notifyGuardianCondition",
            name = "Notify on",
            description = "Determines when guardian notifications will trigger.",
            position = 0,
            section = guardianNotifications
    )
    default NotifyGuardianCondition notifyGuardianCondition() {
        return NotifyGuardianCondition.Full_Inventory;
    }


    @ConfigItem(
            keyName = "airSpawn",
            name = "Notify on Air Guardian",
            description = "Notifies you when an air guardian portal opens.",
            position = 1,
            section = guardianNotifications
    )
    default Notification notifyAirGuardian() {
        return Notification.OFF;
    }

    @ConfigItem(
            keyName = "mindSpawn",
            name = "Notify on Mind Guardian",
            description = "Notifies you when an mind guardian portal opens.",
            position = 2,
            section = guardianNotifications
    )
    default Notification notifyMindGuardian() {
        return Notification.OFF;
    }

    @ConfigItem(
            keyName = "waterSpawn",
            name = "Notify on Water Guardian",
            description = "Notifies you when a water guardian portal opens.",
            position = 3,
            section = guardianNotifications
    )
    default Notification notifyWaterGuardian() {
        return Notification.OFF;
    }

    @ConfigItem(
            keyName = "earthSpawn",
            name = "Notify on Earth Guardian",
            description = "Notifies you when an earth guardian portal opens.",
            position = 4,
            section = guardianNotifications
    )
    default Notification notifyEarthGuardian() {
        return Notification.OFF;
    }

    @ConfigItem(
            keyName = "fireSpawn",
            name = "Notify on Fire Guardian",
            description = "Notifies you when a fire guardian portal opens.",
            position = 5,
            section = guardianNotifications
    )
    default Notification notifyFireGuardian() {
        return Notification.OFF;
    }

    @ConfigItem(
            keyName = "bodySpawn",
            name = "Notify on Body Guardian",
            description = "Notifies you when a body guardian portal opens.",
            position = 6,
            section = guardianNotifications
    )
    default Notification notifyBodyGuardian() {
        return Notification.OFF;
    }

    @ConfigItem(
            keyName = "cosmicSpawn",
            name = "Notify on Cosmic Guardian",
            description = "Notifies you when a cosmic guardian portal opens.",
            position = 7,
            section = guardianNotifications
    )
    default Notification notifyCosmicGuardian() {
        return Notification.OFF;
    }

    @ConfigItem(
            keyName = "chaosSpawn",
            name = "Notify on Chaos Guardian",
            description = "Notifies you when a chaos guardian portal opens.",
            position = 8,
            section = guardianNotifications
    )
    default Notification notifyChaosGuardian() {
        return Notification.OFF;
    }

    @ConfigItem(
            keyName = "natureSpawn",
            name = "Notify on Nature Guardian",
            description = "Notifies you when a nature guardian portal opens.",
            position = 9,
            section = guardianNotifications
    )
    default Notification notifyNatureGuardian() {
        return Notification.OFF;
    }

    @ConfigItem(
            keyName = "lawSpawn",
            name = "Notify on Law Guardian",
            description = "Notifies you when a law guardian portal opens.",
            position = 10,
            section = guardianNotifications
    )
    default Notification notifyLawGuardian() {
        return Notification.OFF;
    }

    @ConfigItem(
            keyName = "deathSpawn",
            name = "Notify on Death Guardian",
            description = "Notifies you when a death guardian portal opens.",
            position = 11,
            section = guardianNotifications
    )
    default Notification notifyDeathGuardian() {
        return Notification.OFF;
    }

    @ConfigItem(
            keyName = "bloodSpawn",
            name = "Notify on Blood Guardian",
            description = "Notifies you when a blood guardian portal opens.",
            position = 12,
            section = guardianNotifications
    )
    default Notification notifyBloodGuardian() {
        return Notification.OFF;
    }
}
