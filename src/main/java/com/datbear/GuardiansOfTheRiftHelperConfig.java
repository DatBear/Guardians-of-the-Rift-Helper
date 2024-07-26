package com.datbear;

import net.runelite.client.config.*;

import java.awt.*;

@ConfigGroup("guardiansOfTheRiftHelper")
public interface GuardiansOfTheRiftHelperConfig extends Config
{
	@ConfigSection(
		name = "Outlines",
		description = "All options relating to colored outlines",
		position =  0,
		closedByDefault = true
	)
	String outlines = "outlines";

	@ConfigSection(
		name = "Overlays",
		description = "All options relating to overlays",
		position =  1,
		closedByDefault = true

	)
	String overlays = "overlays";

    @ConfigSection(
            name = "Guardian Render Style",
            closedByDefault = true,
            position = 0,
            description = "Highlighting style"
    )
    String style = "style";

    @ConfigItem(
            position = 1,
            keyName = "guardianBorderWidth",
            name = "Border Width",
            description = "Width of the highlighted NPC border",
            section = style
    )
    default int guardianBorderWidth()
    {
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
    default int guardianOutlineFeather()
    {
        return 0;
    }

    @ConfigItem(
            position = 0,
            keyName = "guardianOutline",
            name = "Highlight outline",
            description = "Configures whether or not NPC should be highlighted by outline",
            section = style
    )
    default boolean guardianOutline()
    {
        return true;
    }

    @ConfigItem(
            keyName = "portalSpawn",
            name = "Notify on portal spawn",
            description = "Notifies you when a portal spawns."
    )
    default boolean notifyPortalSpawn()
    {
        return true;
    }

    @ConfigItem(
            keyName = "muteApprentices",
            name = "Mute game help messages",
            description = "Mutes the over head messages of the apprentices giving game advice."
    )
    default boolean muteApprentices()
    {
        return true;
    }

    @ConfigItem(
            keyName = "outlineCellTable",
            name = "Outline cell table",
            description = "Outlines the Cell table when you have no cells remaining.",
		section = outlines
    )
    default boolean outlineCellTable()
    {
        return true;
    }

    @ConfigItem(
            keyName = "outlineGreatGuardian",
            name = "Outline Great Guardian",
            description = "Outlines the Great Guardian when you have elemental or catalytic essence in your inventory.",
			section = outlines
    )
    default boolean outlineGreatGuardian()
    {
        return true;
    }


    @ConfigItem(
            keyName = "quickPassCooldown",
            name = "Add cooldown to Quick-Pass",
            description = "Adds a 3 tick delay to the Quick-Pass menu option so you don't enter/leave by spam clicking the gate with Menu Entry Swapper's quick-pass option enabled."
    )
    default boolean quickPassCooldown()
    {
        return true;
    }
    @Alpha
    @ConfigItem(
            keyName = "elementalGuardianColor",
            name = "Elemental outline",
            description = "Color of the outline on the active elemental guardian.",
			section = outlines
    )
    default Color elementalGuardianColor()
    {
        return Color.GREEN;
    }
    @Alpha
    @ConfigItem(
            keyName = "catalyticGuardianColor",
            name = "Catalytic outline",
            description = "Color of the outline on the active catalytic guardian.",
			section = outlines
    )
    default Color catalyticGuardianColor()
    {
        return Color.RED;
    }


    @ConfigItem(
            keyName = "outlineGuardiansByTier",
            name = "Color guardians by cell tier",
            description = "Outlines active portal guardians with colors based on their Cell charge tiers instead of Elemental vs Catalytic.",
            position = 2,
			section = outlines
    )
    default boolean colorGuardiansByTier() { return false; }

    @Alpha
    @ConfigItem(
            keyName = "weakGuardianColor",
            name = "Weak outline",
            description = "Color of the outline on an active weak guardian.",
            position = 3,
			section = outlines
    )
    default Color weakGuardianColor()
    {
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
    default Color mediumGuardianColor()
    {
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
    default Color strongGuardianColor()
    {
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
    default Color overchargedGuardianColor()
    {
        return Color.RED;
    }

    @Alpha
    @ConfigItem(
            keyName = "essencePileColor",
            name = "Essence pile outline",
            description = "Color of the outline on essence piles.",
            position = 7,
            section = outlines
    )
    default Color essencePileColor()
    {
        return Color.GREEN;
    }
    
    @ConfigItem(
            keyName = "startTimerOverlayLocation",
            name = "Start Timer Overlay Location",
            description = "Toggles the start timer overlay location.",
            position =  8,
            section = overlays
    )
    default TimerOverlayLocation startTimerOverlayLocation()
    {
        return TimerOverlayLocation.InfoBox;
    }

    @ConfigItem(
            keyName = "inactivePortalOverlayLocation",
            name = "Inactive Portal Overlay Location",
            description = "Toggles the inactive portal overlay location.",
            position =  9,
            section = overlays
    )
    default TimerOverlayLocation inactivePortalOverlayLocation()
    {
        return TimerOverlayLocation.InfoBox;
    }

    @ConfigItem(
            keyName = "showPointsOverlay",
            name = "Show Points Overlay",
            description = "Toggles the points overlay.",
            position =  10,
            section = overlays
    )
    default boolean showPointsOverlay()
    {
        return true;
    }

	@ConfigItem(
		keyName = "potentialPoints",
		name = "Show potential points",
		description = "Show tallied up points during a game",
		position =  11,
		section = overlays
	)
	default boolean potentialPoints() { return true; }

	@ConfigItem(
		keyName = "highlightPotential",
		name = "Highlight potential points",
		description =  "Highlight potential points depending on balance",
		position =  12,
		section = overlays
	)
	default boolean highlightPotential() { return true; }
	@Alpha
	@ConfigItem(
		keyName = "potentialUnbalanceColor",
		name = "Unbalanced potential color",
		description =  "Color to highlight potential points when unbalanced",
		position = 13,
		section = overlays
	)
	default Color potentialUnbalanceColor() { return Color.RED; }
	@Alpha
	@ConfigItem(
		keyName = "potentialBalanceColor",
		name = "Balanced potential color",
		description =  "Color to highlight potential points when balanced",
		position =  14,
		section = overlays
	)
	default Color potentialBalanceColor() { return Color.GREEN; }
}
