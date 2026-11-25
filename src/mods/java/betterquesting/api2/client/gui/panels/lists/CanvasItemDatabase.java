package betterquesting.api2.client.gui.panels.lists;

import betterquesting.api.utils.BigItemStack;
import betterquesting.api2.client.gui.misc.GuiRectangle;
import betterquesting.api2.client.gui.misc.IGuiRect;
import betterquesting.api2.client.gui.panels.content.PanelItemSlot;
import betterquesting.api2.utils.QuestTranslation;
import betterquesting.core.BetterQuesting;
import java.util.ArrayDeque;
import java.util.Iterator;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraftforge.oredict.OreDictionary;

public class CanvasItemDatabase extends CanvasSearch<ItemStack, Item> {
   private final int btnId;

   public CanvasItemDatabase(IGuiRect rect, int buttonId) {
      super(rect);
      this.btnId = buttonId;
   }

   protected Iterator<Item> getIterator() {
      return Item.REGISTRY.iterator();
   }

   protected void queryMatches(Item item, String query, ArrayDeque<ItemStack> results) {
      if (item != null && item.getRegistryName() != null) {
         if (item == Items.AIR) {
            results.add(ItemStack.EMPTY);
         } else {
            try {
               NonNullList<ItemStack> subList = NonNullList.create();
               item.getSubItems(CreativeTabs.SEARCH, subList);
               if (subList.isEmpty()) {
                  subList.add(item.getDefaultInstance());
               }

               if (!item.getUnlocalizedName().toLowerCase().contains(query) && !QuestTranslation.translate(item.getUnlocalizedName()).toLowerCase().contains(query) && !item.getRegistryName().toString().toLowerCase().contains(query)) {
                  for(ItemStack subItem : subList) {
                     try {
                        if (!subItem.getUnlocalizedName().toLowerCase().contains(query) && !subItem.getDisplayName().toLowerCase().contains(query)) {
                           int[] oids = OreDictionary.getOreIDs(subItem);

                           for(int oid : oids) {
                              if (OreDictionary.getOreName(oid).toLowerCase().contains(query)) {
                                 results.add(subItem);
                                 break;
                              }
                           }
                        } else {
                           results.add(subItem);
                        }
                     } catch (Exception var12) {
                        BetterQuesting.logger.error("An error occured while searching itemstack " + subItem.toString() + " from item \"" + item.getRegistryName() + "\" (" + item.getClass().getName() + ").\nNBT: " + subItem.writeToNBT(new NBTTagCompound()), var12);
                     }
                  }
               } else {
                  results.addAll(subList);
               }
            } catch (Exception var13) {
               BetterQuesting.logger.error("An error occured while searching item \"" + item.getRegistryName() + "\" (" + item.getClass().getName() + ")", var13);
            }

         }
      }
   }

   public boolean addResult(ItemStack stack, int index, int cachedWidth) {
      if (stack == null) {
         return false;
      } else {
         int x = index % (cachedWidth / 18) * 18;
         int y = index / (cachedWidth / 18) * 18;
         this.addPanel(new PanelItemSlot(new GuiRectangle(x, y, 18, 18, 0), this.btnId, new BigItemStack(stack)));
         return true;
      }
   }
}
