package de.Whitedraco.switchbow.helper;

import de.Whitedraco.switchbow.item.ItemMagicQuiver;
import de.Whitedraco.switchbow.item.ItemQuiver;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemArrow;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

public class QuiverArrowHelper {
   public QuiverArrowHelper() {
   }

   private static void removeArrowFromQuiver(ItemStack quiver, ItemStack removeArrow) {
      if (quiver.getItem() instanceof ItemMagicQuiver) {
         removeArrowFromQuiver(18, quiver, removeArrow);
      } else if (quiver.getItem() instanceof ItemQuiver) {
         removeArrowFromQuiver(9, quiver, removeArrow);
      }

   }

   private static List<ItemStack> getArrowFromQuiver(ItemStack quiver) {
      if (quiver.getItem() instanceof ItemMagicQuiver) {
         return getArrowFromQuiver(18, quiver);
      } else {
         return (List<ItemStack>)(quiver.getItem() instanceof ItemQuiver ? getArrowFromQuiver(9, quiver) : new ArrayList());
      }
   }

   public static Vector<ItemStack> getArrowsInInvAndQuiver(EntityPlayer player, InventoryPlayer playerinventory) {
      Vector<ItemStack> arrows = new Vector();

      for(ItemStack stack : playerinventory.mainInventory) {
         if (stack != ItemStack.EMPTY && stack.getItem() instanceof ItemQuiver) {
            List<ItemStack> quiverArrow = getArrowFromQuiver(stack);
            if (!quiverArrow.isEmpty()) {
               for(int a = 0; a < quiverArrow.size(); ++a) {
                  ItemStack arrow = (ItemStack)quiverArrow.get(a);
                  if (!ArrowItemStackEqual.containsArrow(arrows, arrow)) {
                     arrows.add(arrow);
                  }
               }
            }
         } else if (stack != ItemStack.EMPTY && stack.getItem() instanceof ItemArrow && !ArrowItemStackEqual.containsArrow(arrows, stack)) {
            arrows.addElement(stack);
         }
      }

      if (player.getHeldItemOffhand() != ItemStack.EMPTY) {
         ItemStack stack = player.getHeldItemOffhand();
         if (stack != ItemStack.EMPTY && stack.getItem() instanceof ItemQuiver) {
            List<ItemStack> quiverArrow = getArrowFromQuiver(stack);
            if (!quiverArrow.isEmpty()) {
               for(int a = 0; a < quiverArrow.size(); ++a) {
                  ItemStack arrow = (ItemStack)quiverArrow.get(a);
                  if (!ArrowItemStackEqual.containsArrow(arrows, arrow)) {
                     arrows.add(arrow);
                  }
               }
            }
         } else if (player.getHeldItemOffhand() != ItemStack.EMPTY && stack.getItem() instanceof ItemArrow && !ArrowItemStackEqual.containsArrow(arrows, stack)) {
            arrows.addElement(stack);
         }
      }

      return arrows;
   }

   private static List<ItemStack> getArrowFromQuiver(int size, ItemStack quiver) {
      List<ItemStack> arrows = new ArrayList();
      if (quiver.hasTagCompound()) {
         NBTTagList quiverArrows = quiver.getTagCompound().getTagList("ItemInventory", 10);

         for(int i = 0; i < quiverArrows.tagCount(); ++i) {
            NBTTagCompound item = quiverArrows.getCompoundTagAt(i);
            byte slot = item.getByte("Slot");
            if (slot >= 0 && slot < size) {
               ItemStack arrow = new ItemStack(item);
               if (arrow != null && arrow != ItemStack.EMPTY && !ArrowItemStackEqual.containsArrow(arrows, arrow)) {
                  arrows.add(arrow);
               }
            }
         }
      }

      return arrows;
   }

   private static void removeArrowFromQuiver(int size, ItemStack quiver, ItemStack removeArrow) {
      boolean flag = true;
      if (quiver.hasTagCompound()) {
         NBTTagList quiverArrows = quiver.getTagCompound().getTagList("ItemInventory", 10);
         List<ItemStack> arrows = new ArrayList();

         for(int i = 0; i < quiverArrows.tagCount(); ++i) {
            NBTTagCompound item = quiverArrows.getCompoundTagAt(i);
            byte slot = item.getByte("Slot");
            if (slot >= 0 && slot < size) {
               ItemStack arrow = new ItemStack(item);
               if (arrow != null && arrow != ItemStack.EMPTY) {
                  if (ArrowItemStackEqual.containsArrow(arrow, removeArrow) && flag) {
                     arrow.shrink(1);
                     if (arrow.getCount() == 0) {
                        arrows.add(ItemStack.EMPTY);
                     } else {
                        arrows.add(arrow);
                     }

                     flag = false;
                  } else {
                     arrows.add(arrow);
                  }
               }
            }
         }

         NBTTagList nbttaglist = new NBTTagList();

         for(int i = 0; i < arrows.size(); ++i) {
            ItemStack stack = (ItemStack)arrows.get(i);
            if (stack != ItemStack.EMPTY && stack != null) {
               NBTTagCompound nbttagcompound1 = new NBTTagCompound();
               nbttagcompound1.setByte("Slot", (byte)i);
               stack.writeToNBT(nbttagcompound1);
               nbttaglist.appendTag(nbttagcompound1);
            }
         }

         quiver.getTagCompound().setTag("ItemInventory", nbttaglist);
      }

   }

   public static void removeArrow(EntityPlayer player, ItemStack removeArrow) {
      InventoryPlayer inventory = player.inventory;
      List<ItemStack> quiverList = new ArrayList();
      if (player.getHeldItemOffhand() != ItemStack.EMPTY) {
         ItemStack stack = player.getHeldItemOffhand();
         if (stack.getItem() instanceof ItemArrow) {
            if (ArrowItemStackEqual.containsArrow(stack, removeArrow)) {
               stack.shrink(1);
               if (stack.getCount() == 0) {
                  player.inventory.deleteStack(stack);
               }

               return;
            }
         } else if (stack.getItem() instanceof ItemQuiver) {
            quiverList.add(stack);
         }
      }

      for(ItemStack stack : inventory.mainInventory) {
         if (stack.getItem() instanceof ItemArrow) {
            if (ArrowItemStackEqual.containsArrow(stack, removeArrow)) {
               stack.shrink(1);
               if (stack.getCount() == 0) {
                  player.inventory.deleteStack(stack);
               }

               return;
            }
         } else if (stack.getItem() instanceof ItemQuiver) {
            quiverList.add(stack);
         }
      }

      if (!quiverList.isEmpty()) {
         for(ItemStack quiver : quiverList) {
            if (ArrowItemStackEqual.containsArrow(getArrowFromQuiver(quiver), removeArrow)) {
               removeArrowFromQuiver(quiver, removeArrow);
               return;
            }
         }
      }

   }
}
