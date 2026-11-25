package com.alcatrazescapee.notreepunching.common;

import com.alcatrazescapee.notreepunching.ModConfig;
import net.minecraft.item.Item.ToolMaterial;
import net.minecraftforge.common.util.EnumHelper;

public final class ModMaterials {
   public static final ToolMaterial TOOL_FLINT;
   public static final ToolMaterial TOOL_TIN;
   public static final ToolMaterial TOOL_COPPER;
   public static final ToolMaterial TOOL_BRONZE;
   public static final ToolMaterial TOOL_STEEL;

   public ModMaterials() {
   }

   static {
      TOOL_FLINT = EnumHelper.addToolMaterial("notreepunching:flint", ModConfig.TOOLS.miningLevelFlint, 45, 2.5F, 0.5F, 0);
      TOOL_TIN = EnumHelper.addToolMaterial("notreepunching:tin", ModConfig.TOOLS.miningLevelTin, 120, 3.0F, 1.0F, 2);
      TOOL_COPPER = EnumHelper.addToolMaterial("notreepunching:copper", ModConfig.TOOLS.miningLevelCopper, 220, 4.0F, 1.5F, 4);
      TOOL_BRONZE = EnumHelper.addToolMaterial("notreepunching:bronze", ModConfig.TOOLS.miningLevelBronze, 560, 8.0F, 2.0F, 8);
      TOOL_STEEL = EnumHelper.addToolMaterial("notreepunching:steel", ModConfig.TOOLS.miningLevelSteel, 1800, 11.0F, 2.5F, 10);
   }
}
