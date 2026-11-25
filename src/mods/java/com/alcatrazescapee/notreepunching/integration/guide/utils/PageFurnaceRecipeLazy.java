package com.alcatrazescapee.notreepunching.integration.guide.utils;

import amerifrance.guideapi.page.PageFurnaceRecipe;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.FurnaceRecipes;

public class PageFurnaceRecipeLazy extends PageFurnaceRecipe implements ILazyLoader {
   public PageFurnaceRecipeLazy(Block block) {
      super(block);
   }

   public PageFurnaceRecipeLazy(Item item) {
      super(item);
   }

   public void loadPost() {
      this.output = FurnaceRecipes.instance().getSmeltingResult(this.input);
   }
}
