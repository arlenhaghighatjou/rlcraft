package com.github.fnar.minecraft.item;

import java.util.List;
import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;

public enum ToolType {
   AXE,
   FISHING_ROD,
   FLINT_AND_STEEL,
   HOE,
   PICKAXE,
   SHEARS,
   SHOVEL;

   private ToolType() {
   }

   public static ToolType randomAmong(EaglercraftRandom random, List<ToolType> values) {
      return (ToolType)values.get(random.nextInt(values.size()));
   }

   public static ToolType random(EaglercraftRandom random) {
      int choice = random.nextInt(values().length);
      return values()[choice];
   }

   public Tool asItem() {
      return new Tool(this);
   }

   public RldItemStack asItemStack() {
      return this.asItem().asStack();
   }
}
