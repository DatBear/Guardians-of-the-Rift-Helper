package com.datbear;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

import java.awt.*;

@ConfigGroup("guardiansOfTheRiftHelper")
public interface GuardiansOfTheRiftHelperConfig extends Config
{
    @ConfigItem(
            keyName = "showOverlay",
            name = "Show Overlay",
            description = "Toggles the status overlay."
    )
    default boolean showOverlay()
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
            description = "Outlines the Cell table when you have no cells remaining."
    )
    default boolean outlineCellTable()
    {
        return true;
    }

    @ConfigItem(
            keyName = "outlineGreatGuardian",
            name = "Outline Great Guardian",
            description = "Outlines the Great Guardian when you have elemental or catalytic essence in your inventory."
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

    @ConfigItem(
            keyName = "elementalGuardianColor",
            name = "Elemental outline",
            description = "Color of the outline on the active elemental guardian."
    )
    default Color elementalGuardianColor()
    {
        return Color.GREEN;
    }

    @ConfigItem(
            keyName = "catalyticGuardianColor",
            name = "Catalytic outline",
            description = "Color of the outline on the active catalytic guardian."
    )
    default Color catalyticGuardianColor()
    {
        return Color.RED;
    }


    @ConfigItem(
            keyName = "outlineGuardiansByTier",
            name = "Color guardians by cell tier",
            description = "Outlines active portal guardians with colors based on their Cell charge tiers instead of Elemental vs Catalytic.",
            position = 2
    )
    default boolean colorGuardiansByTier() { return false; }


    @ConfigItem(
            keyName = "weakGuardianColor",
            name = "Weak outline",
            description = "Color of the outline on an active weak guardian.",
            position = 3
    )
    default Color weakGuardianColor()
    {
        return Color.WHITE;
    }

    @ConfigItem(
            keyName = "mediumGuardianColor",
            name = "Medium outline",
            description = "Color of the outline on an active medium guardian.",
            position = 4
    )
    default Color mediumGuardianColor()
    {
        return Color.BLUE;
    }

    @ConfigItem(
            keyName = "strongGuardianColor",
            name = "Strong outline",
            description = "Color of the outline on an active strong guardian.",
            position = 5
    )
    default Color strongGuardianColor()
    {
        return Color.GREEN;
    }

    @ConfigItem(
            keyName = "overchargedGuardianColor",
            name = "Overcharged outline",
            description = "Color of the outline on an active overcharged guardian.",
            position = 6
    )
    default Color overchargedGuardianColor()
    {
        return Color.RED;
    }


}
