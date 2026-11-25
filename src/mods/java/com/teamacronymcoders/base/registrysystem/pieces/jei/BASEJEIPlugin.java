package com.teamacronymcoders.base.registrysystem.pieces.jei;

import com.teamacronymcoders.base.items.IHasSubItems;
import java.util.ArrayList;
import java.util.function.Consumer;
import mezz.jei.api.IJeiHelpers;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.JEIPlugin;
import net.minecraft.item.ItemStack;

@JEIPlugin
public class BASEJEIPlugin implements IModPlugin {
   private static IJeiHelpers jeiHelpers;

   public BASEJEIPlugin() {
   }

   public void register(IModRegistry registry) {
      jeiHelpers = registry.getJeiHelpers();
   }

   public static void blackListItemStack(ItemStack itemStack) {
      jeiHelpers.getIngredientBlacklist().addIngredientToBlacklist(itemStack);
   }

   public static void blackListItem(IHasSubItems item) {
      item.getAllSubItems(new ArrayList()).forEach(BASEJEIPlugin::blackListItemStack);
   }
}
