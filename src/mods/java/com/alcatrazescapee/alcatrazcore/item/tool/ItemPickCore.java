package com.alcatrazescapee.alcatrazcore.item.tool;

import net.minecraft.item.Item.ToolMaterial;

public class ItemPickCore extends ItemToolCore {
   public ItemPickCore(ToolMaterial material) {
      super(material, 1.0F, -2.8F);
      this.addToolClass(ItemToolCore.ToolClass.PICKAXE);
   }
}
