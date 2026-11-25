package reborncore.common.util;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.oredict.OreDictionary;
import reborncore.api.power.IEnergyItemInfo;
import reborncore.common.powerSystem.forge.ForgePowerItemManager;
import reborncore.common.recipes.IRecipeInput;

public class ItemUtils {
   public ItemUtils() {
   }

   public static boolean isItemEqual(ItemStack a, ItemStack b, boolean matchDamage, boolean matchNBT) {
      if (!a.isEmpty() && !b.isEmpty()) {
         if (a.getItem() != b.getItem()) {
            return false;
         } else if (matchNBT && !ItemStack.areItemStackTagsEqual(a, b)) {
            return false;
         } else {
            if (matchDamage && a.getHasSubtypes()) {
               if (isWildcard(a) || isWildcard(b)) {
                  return true;
               }

               if (a.getItemDamage() != b.getItemDamage()) {
                  return false;
               }
            }

            return true;
         }
      } else {
         return false;
      }
   }

   public static boolean isItemEqual(ItemStack a, ItemStack b, boolean matchDamage, boolean matchNBT, boolean useOreDic) {
      if (isItemEqual(a, b, matchDamage, matchNBT)) {
         return true;
      } else if (!a.isEmpty() && !b.isEmpty()) {
         if (useOreDic) {
            for(int inta : OreDictionary.getOreIDs(a)) {
               for(int intb : OreDictionary.getOreIDs(b)) {
                  if (inta == intb) {
                     return true;
                  }
               }
            }
         }

         return false;
      } else {
         return false;
      }
   }

   public static boolean isInputEqual(Object input, ItemStack other, boolean matchDamage, boolean matchNBT, boolean useOreDic) {
      if (input instanceof ItemStack) {
         return isItemEqual((ItemStack)input, other, matchDamage, matchNBT, useOreDic);
      } else {
         if (input instanceof String) {
            for(ItemStack stack : OreDictionary.getOres((String)input)) {
               if (isItemEqual(stack, other, matchDamage, matchNBT, false)) {
                  return true;
               }
            }
         } else if (input instanceof IRecipeInput) {
            for(ItemStack stack : ((IRecipeInput)input).getAllStacks()) {
               if (isItemEqual(stack, other, matchDamage, matchNBT, false)) {
                  return true;
               }
            }
         }

         return false;
      }
   }

   public static boolean isWildcard(ItemStack stack) {
      return isWildcard(stack.getItemDamage());
   }

   public static boolean isWildcard(int damage) {
      return damage == -1 || damage == 32767;
   }

   public static void writeInvToNBT(IInventory inv, String tag, NBTTagCompound data) {
      NBTTagList list = new NBTTagList();

      for(byte slot = 0; slot < inv.getSizeInventory(); ++slot) {
         ItemStack stack = inv.getStackInSlot(slot);
         if (!stack.isEmpty()) {
            NBTTagCompound itemTag = new NBTTagCompound();
            itemTag.setByte("Slot", slot);
            writeItemToNBT(stack, itemTag);
            list.appendTag(itemTag);
         }
      }

      data.setTag(tag, list);
   }

   public static void readInvFromNBT(IInventory inv, String tag, NBTTagCompound data) {
      NBTTagList list = data.getTagList(tag, 10);

      for(byte entry = 0; entry < list.tagCount(); ++entry) {
         NBTTagCompound itemTag = list.getCompoundTagAt(entry);
         int slot = itemTag.getByte("Slot");
         if (slot >= 0 && slot < inv.getSizeInventory()) {
            ItemStack stack = readItemFromNBT(itemTag);
            inv.setInventorySlotContents(slot, stack);
         }
      }

   }

   public static List<ItemStack> getStackWithAllOre(ItemStack stack) {
      if (stack.isEmpty()) {
         return new ArrayList();
      } else {
         ArrayList<ItemStack> list = new ArrayList();

         for(int oreID : OreDictionary.getOreIDs(stack)) {
            for(ItemStack ore : OreDictionary.getOres(OreDictionary.getOreName(oreID))) {
               ItemStack newOre = ore.copy();
               newOre.setCount(stack.getCount());
               list.add(newOre);
            }
         }

         if (list.isEmpty()) {
            list.add(stack);
         }

         return list;
      }
   }

   public static ItemStack readItemFromNBT(NBTTagCompound data) {
      return new ItemStack(data);
   }

   public static void writeItemToNBT(ItemStack stack, NBTTagCompound data) {
      if (!isEmpty(stack)) {
         if (stack.getCount() > 127) {
            stack.setCount(127);
         }

         stack.writeToNBT(data);
      }
   }

   public static double getPowerForDurabilityBar(ItemStack stack) {
      if (isEmpty(stack)) {
         return 0.0D;
      } else if (!(stack.getItem() instanceof IEnergyItemInfo)) {
         return 0.0D;
      } else {
         IEnergyStorage capEnergy = new ForgePowerItemManager(stack);
         return (double)capEnergy.getEnergyStored() / (double)capEnergy.getMaxEnergyStored();
      }
   }

   public static boolean isActive(ItemStack stack) {
      if (!isEmpty(stack)) {
         NBTTagCompound nbt = getStackNbtData(stack);
         if (nbt.hasKey("isActive")) {
            return nbt.getBoolean("isActive");
         }
      }

      return false;
   }

   public static NBTTagCompound getStackNbtData(ItemStack stack) {
      NBTTagCompound ret = stack.getTagCompound();
      if (ret == null) {
         ret = new NBTTagCompound();
         stack.setTagCompound(ret);
      }

      return ret;
   }

   public static boolean isEmpty(ItemStack stack) {
      return stack == ItemStack.EMPTY || stack == null || stack.getCount() <= 0;
   }

   public static int getSize(ItemStack stack) {
      return isEmpty(stack) ? 0 : stack.getCount();
   }

   public static ItemStack setSize(ItemStack stack, int size) {
      if (size <= 0) {
         return ItemStack.EMPTY;
      } else {
         stack.setCount(size);
         return stack;
      }
   }

   public static ItemStack increaseSize(ItemStack stack, int amount) {
      return setSize(stack, getSize(stack) + amount);
   }

   public static ItemStack increaseSize(ItemStack stack) {
      return increaseSize(stack, 1);
   }

   public static ItemStack decreaseSize(ItemStack stack, int amount) {
      return setSize(stack, getSize(stack) - amount);
   }

   public static ItemStack decreaseSize(ItemStack stack) {
      return decreaseSize(stack, 1);
   }

   public static ItemStack copyWithSize(ItemStack stack, int size) {
      return isEmpty(stack) ? ItemStack.EMPTY : setSize(stack.copy(), size);
   }

   public static void dropToWorld(World world, BlockPos pos, ItemStack stack) {
      if (!isEmpty(stack)) {
         double f = 0.7D;
         double dx = (double)world.rand.nextFloat() * f + (1.0D - f) * 0.5D;
         double dy = (double)world.rand.nextFloat() * f + (1.0D - f) * 0.5D;
         double dz = (double)world.rand.nextFloat() * f + (1.0D - f) * 0.5D;
         EntityItem entityItem = new EntityItem(world, (double)pos.getX() + dx, (double)pos.getY() + dy, (double)pos.getZ() + dz, stack.copy());
         entityItem.setDefaultPickupDelay();
         world.spawnEntity(entityItem);
      }
   }

   public static String toFormattedString(ItemStack itemStack) {
      return itemStack == null ? "(null)" : (itemStack.getItem() == null ? getSize(itemStack) + "x(null)@(unknown)" : itemStack.toString());
   }
}
