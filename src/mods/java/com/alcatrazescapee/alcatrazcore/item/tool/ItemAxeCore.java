package com.alcatrazescapee.alcatrazcore.item.tool;

import net.minecraft.block.material.Material;
import net.minecraft.item.Item.ToolMaterial;

public class ItemAxeCore extends ItemToolCore {
   public ItemAxeCore(ToolMaterial material) {
      this(material, material.getAttackDamage(), -3.0F);
   }

   public ItemAxeCore(ToolMaterial material, float damage, float speed) {
      super(material, damage, speed);
      this.addToolClass(ItemToolCore.ToolClass.AXE);
      this.effectiveMaterials.add(Material.WOOD);
      this.effectiveMaterials.add(Material.PLANTS);
      this.effectiveMaterials.add(Material.VINE);
   }
}
