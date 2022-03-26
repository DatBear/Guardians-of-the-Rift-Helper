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
            description = "Toggles the status overlay"
    )
    default boolean showOverlay()
    {
        return true;
    }

    @ConfigItem(
            keyName = "portalSpawn",
            name = "Notify on portal spawn",
            description = "Notifies you when a portal spawns"
    )
    default boolean notifyPortalSpawn()
    {
        return true;
    }

    @ConfigItem(
            keyName = "outlineCellTable",
            name = "Outline cell table",
            description = "Outlines the Cell table when you have no cells remaining"
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
            keyName = "elementalGuardianColor",
            name = "Elemental Outline",
            description = "Color of the outline on the active elemental guardian"
    )
    default Color elementalGuardianColor()
    {
        return Color.GREEN;
    }

    @ConfigItem(
            keyName = "catalyticGuardianColor",
            name = "Catalytic Outline",
            description = "Color of the outline on the active catalytic guardian"
    )
    default Color catalyticGuardianColor()
    {
        return Color.RED;
    }
}
