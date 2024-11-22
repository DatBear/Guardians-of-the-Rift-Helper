package com.datbear;

import com.google.common.collect.ImmutableSet;
import net.runelite.api.ItemID;

import java.util.Set;

public class CellTileInfo {

    public static final CellTileInfo WEAK = new CellTileInfo(43740, ItemID.WEAK_CELL, CellType.Weak);
    public static final CellTileInfo MEDIUM = new CellTileInfo(43741, ItemID.MEDIUM_CELL, CellType.Medium);
    public static final CellTileInfo STRONG = new CellTileInfo(43742, ItemID.STRONG_CELL, CellType.Strong);
    public static final CellTileInfo OVERCHARGED = new CellTileInfo(43743, ItemID.OVERCHARGED_CELL, CellType.Overcharged);

    public static final Set<CellTileInfo> ALL = ImmutableSet.of(WEAK, MEDIUM, STRONG, OVERCHARGED);

    public int groundObjectId;
    public int itemId;
    public CellType cellType;

    public CellTileInfo(int groundObjectId, int itemId, CellType cellType) {
        this.groundObjectId = groundObjectId;
        this.itemId = itemId;
        this.cellType = cellType;
    }
}
