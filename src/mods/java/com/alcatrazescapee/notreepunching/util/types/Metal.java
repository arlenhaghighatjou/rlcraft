package com.alcatrazescapee.notreepunching.util.types;

import com.alcatrazescapee.notreepunching.ModConfig;
import com.alcatrazescapee.notreepunching.common.ModMaterials;
import javax.annotation.Nullable;
import net.minecraft.item.Item.ToolMaterial;

public enum Metal {
   IRON(true, ToolMaterial.IRON),
   GOLD(true, ToolMaterial.GOLD),
   DIAMOND(true, ToolMaterial.DIAMOND),
   COPPER(ModConfig.TOOLS.enableCopperTools, ModMaterials.TOOL_COPPER),
   TIN(ModConfig.TOOLS.enableTinTools, ModMaterials.TOOL_TIN),
   BRONZE(ModConfig.TOOLS.enableBronzeTools, ModMaterials.TOOL_BRONZE),
   STEEL(ModConfig.TOOLS.enableSteelTools, ModMaterials.TOOL_STEEL);

   public final boolean isEnabled;
   public final ToolMaterial toolMaterial;

   private Metal(boolean isEnabled, @Nullable ToolMaterial toolMaterial) {
      this.isEnabled = isEnabled;
      this.toolMaterial = toolMaterial;
   }
}
