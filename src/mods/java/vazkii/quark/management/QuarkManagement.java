package vazkii.quark.management;

import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import vazkii.quark.base.module.Module;
import vazkii.quark.management.feature.AutomaticToolRestock;
import vazkii.quark.management.feature.BetterCraftShifting;
import vazkii.quark.management.feature.ChangeHotbarKeybind;
import vazkii.quark.management.feature.ChestButtons;
import vazkii.quark.management.feature.ChestsInBoats;
import vazkii.quark.management.feature.DeleteItems;
import vazkii.quark.management.feature.FToSwitchItems;
import vazkii.quark.management.feature.FavoriteItems;
import vazkii.quark.management.feature.InventorySorting;
import vazkii.quark.management.feature.LinkItems;
import vazkii.quark.management.feature.RightClickAddToShulkerBox;
import vazkii.quark.management.feature.RotateArrowTypes;
import vazkii.quark.management.feature.StoreToChests;

public class QuarkManagement extends Module {
   public QuarkManagement() {
   }

   public void addFeatures() {
      this.registerFeature(new StoreToChests());
      this.registerFeature(new FToSwitchItems(), "Press F in the inventory to switch item to main hand");
      this.registerFeature(new ChestButtons());
      this.registerFeature(new FavoriteItems(), "Ctrl-click an item to favorite it. Favorited items aren't stored by the other features here");
      this.registerFeature(new LinkItems(), "Press T in the inventory to link items to chat");
      this.registerFeature(new DeleteItems(), "Press Ctrl-DELETE in the inventory to delete an item");
      this.registerFeature(new InventorySorting());
      this.registerFeature(new BetterCraftShifting());
      this.registerFeature(new ChangeHotbarKeybind());
      this.registerFeature(new AutomaticToolRestock());
      this.registerFeature(new RightClickAddToShulkerBox());
      this.registerFeature(new ChestsInBoats());
      this.registerFeature(new RotateArrowTypes());
   }

   public ItemStack getIconStack() {
      return new ItemStack(Blocks.CHEST);
   }
}
