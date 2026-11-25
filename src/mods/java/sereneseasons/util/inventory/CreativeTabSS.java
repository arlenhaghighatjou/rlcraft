package sereneseasons.util.inventory;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityList.EntityEggInfo;
import net.minecraft.init.Items;
import net.minecraft.item.ItemMonsterPlacer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import sereneseasons.api.SSItems;

public class CreativeTabSS extends CreativeTabs {
   public static final CreativeTabs instance = new CreativeTabSS(CreativeTabs.getNextID(), "tabSereneSeasons");

   private CreativeTabSS(int index, String label) {
      super(index, label);
   }

   public ItemStack getTabIconItem() {
      return new ItemStack(SSItems.ss_icon);
   }

   @SideOnly(Side.CLIENT)
   public void displayAllRelevantItems(NonNullList<ItemStack> itemList) {
      super.displayAllRelevantItems(itemList);

      for(EntityEggInfo eggInfo : EntityList.ENTITY_EGGS.values()) {
         if (eggInfo.spawnedID.getResourceDomain().equals("sereneseasons")) {
            ItemStack itemstack = new ItemStack(Items.SPAWN_EGG, 1);
            ItemMonsterPlacer.applyEntityIdToItemStack(itemstack, eggInfo.spawnedID);
            itemList.add(itemstack);
         }
      }

   }
}
