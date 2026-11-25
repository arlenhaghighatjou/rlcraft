package xzeroair.trinkets.api;

import baubles.api.BaublesApi;
import baubles.api.cap.IBaublesItemHandler;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Predicate;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.items.IItemHandler;
import xzeroair.trinkets.Trinkets;
import xzeroair.trinkets.capabilities.Capabilities;
import xzeroair.trinkets.capabilities.InventoryContainerCapability.ITrinketContainerHandler;
import xzeroair.trinkets.util.compat.baubles.BaublesHelper;

public class TrinketHelper {
   public TrinketHelper() {
   }

   public static ITrinketContainerHandler getTrinketHandler(EntityLivingBase entity) {
      return Capabilities.getTrinketContainer(entity);
   }

   public static ITrinketContainerHandler getTrinketHandler(EntityLivingBase entity, Consumer<ITrinketContainerHandler> consumer) {
      return Capabilities.getTrinketContainer(entity, consumer);
   }

   public static <R> R getTrinketHandler(EntityLivingBase entity, R ret, BiFunction<ITrinketContainerHandler, R, R> func) {
      return (R)Capabilities.getTrinketContainer(entity, ret, func);
   }

   @Nullable
   public static TrinketHelper.SlotInformation getSlotInfoForItem(EntityLivingBase entity, Predicate<ItemStack> predicate) {
      TrinketHelper.SlotInformation info = getSlotInfoForItemFromAccessory(entity, predicate);
      if (info == null) {
         TrinketHelper.SlotInformation equipmentInfo = getSlotInfoForItemFromEquipment(entity, predicate);
         if (equipmentInfo == null) {
            TrinketHelper.SlotInformation heldInfo = getSlotInfoForItemFromHeldEquipment(entity, predicate);
            return heldInfo == null && entity instanceof EntityPlayer ? getSlotInfoForItemFromPlayerInventory(entity, predicate, true) : heldInfo;
         } else {
            return equipmentInfo;
         }
      } else {
         return info;
      }
   }

   @Nullable
   public static TrinketHelper.SlotInformation getSlotInfoForItemFromAccessory(EntityLivingBase entity, Predicate<ItemStack> predicate) {
      TrinketHelper.SlotInformation info = getTrinketSlotInformation(entity, predicate);
      if (info == null) {
         TrinketHelper.SlotInformation baubleInfo = getBaubleSlotInformation(entity, predicate);
         return baubleInfo == null ? null : baubleInfo;
      } else {
         return info;
      }
   }

   @Nullable
   public static TrinketHelper.SlotInformation getSlotInfoForItemFromEquipment(EntityLivingBase entity, Predicate<ItemStack> predicate) {
      if (entity == null) {
         return null;
      } else {
         ItemStack head = entity.getItemStackFromSlot(EntityEquipmentSlot.HEAD);
         if (predicate.test(head)) {
            return getSlotInfo(head, TrinketHelper.SlotInformation.ItemHandlerType.HEAD.getName(), EntityEquipmentSlot.HEAD.getSlotIndex());
         } else {
            ItemStack chest = entity.getItemStackFromSlot(EntityEquipmentSlot.CHEST);
            if (predicate.test(chest)) {
               return getSlotInfo(chest, TrinketHelper.SlotInformation.ItemHandlerType.CHEST.getName(), EntityEquipmentSlot.CHEST.getSlotIndex());
            } else {
               ItemStack legs = entity.getItemStackFromSlot(EntityEquipmentSlot.LEGS);
               if (predicate.test(legs)) {
                  return getSlotInfo(legs, TrinketHelper.SlotInformation.ItemHandlerType.LEGS.getName(), EntityEquipmentSlot.LEGS.getSlotIndex());
               } else {
                  ItemStack feet = entity.getItemStackFromSlot(EntityEquipmentSlot.FEET);
                  return predicate.test(feet) ? getSlotInfo(feet, TrinketHelper.SlotInformation.ItemHandlerType.FEET.getName(), EntityEquipmentSlot.FEET.getSlotIndex()) : null;
               }
            }
         }
      }
   }

   @Nullable
   public static TrinketHelper.SlotInformation getSlotInfoForItemFromHeldEquipment(EntityLivingBase entity, Predicate<ItemStack> predicate) {
      if (entity == null) {
         return null;
      } else {
         ItemStack mainHand = entity.getItemStackFromSlot(EntityEquipmentSlot.MAINHAND);
         if (predicate.test(mainHand)) {
            return getSlotInfo(mainHand, TrinketHelper.SlotInformation.ItemHandlerType.MAINHAND.getName(), EntityEquipmentSlot.MAINHAND.getSlotIndex());
         } else {
            ItemStack offHand = entity.getItemStackFromSlot(EntityEquipmentSlot.OFFHAND);
            return predicate.test(offHand) ? getSlotInfo(offHand, TrinketHelper.SlotInformation.ItemHandlerType.OFFHAND.getName(), EntityEquipmentSlot.OFFHAND.getSlotIndex()) : null;
         }
      }
   }

   @Nullable
   public static TrinketHelper.SlotInformation getSlotInfoForItemFromPlayerInventory(EntityLivingBase entity, Predicate<ItemStack> predicate, boolean onHotbar) {
      if (entity instanceof EntityPlayer) {
         InventoryPlayer inventory = ((EntityPlayer)entity).inventory;

         for(int i = 0; i < inventory.getSizeInventory(); ++i) {
            ItemStack stack = inventory.getStackInSlot(i);
            boolean isHotBar = InventoryPlayer.isHotbar(i);
            boolean flag = onHotbar ? isHotBar : !isHotBar;
            if (predicate.test(stack) && flag) {
               String type = isHotBar ? TrinketHelper.SlotInformation.ItemHandlerType.HOTBAR.getName() : TrinketHelper.SlotInformation.ItemHandlerType.INVENTORY.getName();
               return getSlotInfo(stack, type, i);
            }
         }
      }

      return null;
   }

   @Nonnull
   public static TrinketHelper.SlotInformation getSlotInfo(ItemStack stack, String handler, int slot) {
      return new TrinketHelper.SlotInformation(stack, handler, slot);
   }

   public static List<TrinketHelper.SlotInformation> getSlotInfoForAccessories(EntityLivingBase entity, Predicate<ItemStack> predicate) {
      List<TrinketHelper.SlotInformation> list = new ArrayList();
      List<TrinketHelper.SlotInformation> trinkets = getSlotInfoForTrinkets(entity, predicate);
      List<TrinketHelper.SlotInformation> baubles = getSlotInfoForBaubles(entity, predicate);
      list.addAll(trinkets);
      list.addAll(baubles);
      return list;
   }

   public static List<TrinketHelper.SlotInformation> getSlotInfoForEquipment(EntityLivingBase entity, Predicate<ItemStack> predicate) {
      List<TrinketHelper.SlotInformation> list = new ArrayList();
      list.addAll(getSlotInfoForArmor(entity, predicate));
      list.addAll(getSlotInfoForHeldEquipment(entity, predicate));
      return list;
   }

   public static List<TrinketHelper.SlotInformation> getSlotInfoForArmor(EntityLivingBase entity, Predicate<ItemStack> predicate) {
      List<TrinketHelper.SlotInformation> list = new ArrayList();
      if (entity == null) {
         return list;
      } else {
         ItemStack head = entity.getItemStackFromSlot(EntityEquipmentSlot.HEAD);
         if (predicate.test(head)) {
            TrinketHelper.SlotInformation info = getSlotInfo(head, TrinketHelper.SlotInformation.ItemHandlerType.HEAD.getName(), EntityEquipmentSlot.HEAD.getSlotIndex());
            list.add(info);
         }

         ItemStack chest = entity.getItemStackFromSlot(EntityEquipmentSlot.CHEST);
         if (predicate.test(chest)) {
            TrinketHelper.SlotInformation info = getSlotInfo(chest, TrinketHelper.SlotInformation.ItemHandlerType.CHEST.getName(), EntityEquipmentSlot.CHEST.getSlotIndex());
            list.add(info);
         }

         ItemStack legs = entity.getItemStackFromSlot(EntityEquipmentSlot.LEGS);
         if (predicate.test(legs)) {
            TrinketHelper.SlotInformation info = getSlotInfo(legs, TrinketHelper.SlotInformation.ItemHandlerType.LEGS.getName(), EntityEquipmentSlot.LEGS.getSlotIndex());
            list.add(info);
         }

         ItemStack feet = entity.getItemStackFromSlot(EntityEquipmentSlot.FEET);
         if (predicate.test(feet)) {
            TrinketHelper.SlotInformation info = getSlotInfo(feet, TrinketHelper.SlotInformation.ItemHandlerType.FEET.getName(), EntityEquipmentSlot.FEET.getSlotIndex());
            list.add(info);
         }

         return list;
      }
   }

   public static List<TrinketHelper.SlotInformation> getSlotInfoForHeldEquipment(EntityLivingBase entity, Predicate<ItemStack> predicate) {
      List<TrinketHelper.SlotInformation> list = new ArrayList();
      if (entity == null) {
         return list;
      } else {
         ItemStack mainHand = entity.getItemStackFromSlot(EntityEquipmentSlot.MAINHAND);
         if (predicate.test(mainHand)) {
            TrinketHelper.SlotInformation info = getSlotInfo(mainHand, TrinketHelper.SlotInformation.ItemHandlerType.MAINHAND.getName(), EntityEquipmentSlot.MAINHAND.getSlotIndex());
            list.add(info);
         }

         ItemStack offHand = entity.getItemStackFromSlot(EntityEquipmentSlot.OFFHAND);
         if (predicate.test(offHand)) {
            TrinketHelper.SlotInformation info = getSlotInfo(offHand, TrinketHelper.SlotInformation.ItemHandlerType.OFFHAND.getName(), EntityEquipmentSlot.OFFHAND.getSlotIndex());
            list.add(info);
         }

         return list;
      }
   }

   public static List<TrinketHelper.SlotInformation> getSlotInfoForItems(EntityLivingBase entity, Predicate<ItemStack> predicate, boolean onHotbar) {
      List<TrinketHelper.SlotInformation> list = new ArrayList();
      if (entity instanceof EntityPlayer) {
         InventoryPlayer inventory = ((EntityPlayer)entity).inventory;

         for(int i = 0; i < inventory.getSizeInventory(); ++i) {
            ItemStack stack = inventory.getStackInSlot(i);
            boolean isHotBar = InventoryPlayer.isHotbar(i);
            boolean flag = onHotbar ? isHotBar : !isHotBar;
            if (predicate.test(stack) && flag) {
               String type = isHotBar ? TrinketHelper.SlotInformation.ItemHandlerType.HOTBAR.getName() : TrinketHelper.SlotInformation.ItemHandlerType.INVENTORY.getName();
               TrinketHelper.SlotInformation info = getSlotInfo(stack, type, i);
               list.add(info);
            }
         }
      }

      return list;
   }

   public static boolean AccessoryCheck(EntityLivingBase entity, Item item) {
      return item == null ? false : AccessoryCheck(entity, (stack) -> {
         return !stack.isEmpty() && stack.getItem().getRegistryName().toString().contentEquals(item.getRegistryName().toString());
      });
   }

   public static boolean AccessoryCheck(EntityLivingBase entity, Predicate<ItemStack> predicate) {
      if (!getTrinketStack(entity, predicate).isEmpty()) {
         return true;
      } else {
         return !getBaubleStack(entity, predicate).isEmpty();
      }
   }

   public static boolean AccessoryCheck(EntityLivingBase entity, List<Item> items) {
      boolean found = false;

      for(Item item : items) {
         if (AccessoryCheck(entity, item)) {
            found = true;
            break;
         }
      }

      return found;
   }

   public static ItemStack getAccessory(EntityLivingBase entity, Item item) {
      return item == null ? ItemStack.EMPTY : getAccessory(entity, (stack) -> {
         return !stack.isEmpty() && stack.getItem().getRegistryName().toString().contentEquals(item.getRegistryName().toString());
      });
   }

   public static ItemStack getAccessory(EntityLivingBase entity, Predicate<ItemStack> predicate) {
      ItemStack stack1 = getTrinketStack(entity, predicate);
      return stack1.isEmpty() ? getBaubleStack(entity, predicate) : stack1;
   }

   public static void applyToAccessories(EntityLivingBase entity, Consumer<ItemStack> consumer) {
      applyToTrinkets(entity, consumer);
      applyToBaubles(entity, consumer);
   }

   public static int countAccessories(EntityLivingBase entity, Predicate<ItemStack> predicate) {
      int amount = 0;
      amount = amount + countTrinkets(entity, predicate);
      amount = amount + countBaubles(entity, predicate);
      return amount;
   }

   public static boolean TrinketCheck(EntityLivingBase entity, Item item) {
      return !getTrinketStack(entity, item).isEmpty();
   }

   public static ItemStack getTrinketStack(EntityLivingBase entity, Item item) {
      return item == null ? ItemStack.EMPTY : getTrinketStack(entity, (stack) -> {
         return !stack.isEmpty() && stack.getItem().getRegistryName().toString().contentEquals(item.getRegistryName().toString());
      });
   }

   public static ItemStack getTrinketStack(EntityLivingBase entity, Predicate<ItemStack> predicate) {
      ITrinketContainerHandler Trinket = getTrinketHandler(entity);
      if (Trinket != null) {
         for(int i = 0; i < Trinket.getSlots(); ++i) {
            if (!Trinket.getStackInSlot(i).isEmpty()) {
               ItemStack stack = Trinket.getStackInSlot(i);
               if (predicate.test(stack)) {
                  return stack;
               }
            }
         }
      }

      return ItemStack.EMPTY;
   }

   public static void applyToTrinkets(EntityLivingBase entity, Consumer<ItemStack> consumer) {
      getTrinketHandler(entity, (Trinket) -> {
         for(int i = 0; i < Trinket.getSlots(); ++i) {
            if (!Trinket.getStackInSlot(i).isEmpty()) {
               ItemStack stack = Trinket.getStackInSlot(i);
               if (consumer != null) {
                  consumer.accept(stack);
               }
            }
         }

      });
   }

   public static int countTrinkets(EntityLivingBase entity, Predicate<ItemStack> predicate) {
      int ret = 0;
      ITrinketContainerHandler Trinket = getTrinketHandler(entity);
      if (Trinket != null) {
         for(int i = 0; i < Trinket.getSlots(); ++i) {
            if (!Trinket.getStackInSlot(i).isEmpty()) {
               ItemStack stack = Trinket.getStackInSlot(i);
               if (predicate.test(stack)) {
                  ++ret;
               }
            }
         }
      }

      return ret;
   }

   public static TrinketHelper.SlotInformation getTrinketSlotInformation(EntityLivingBase entity, Predicate<ItemStack> predicate) {
      ITrinketContainerHandler Trinket = getTrinketHandler(entity);
      if (Trinket != null) {
         for(int i = 0; i < Trinket.getSlots(); ++i) {
            if (!Trinket.getStackInSlot(i).isEmpty()) {
               ItemStack stack = Trinket.getStackInSlot(i);
               if (predicate.test(stack)) {
                  return getSlotInfo(stack, TrinketHelper.SlotInformation.ItemHandlerType.TRINKETS.getName(), i);
               }
            }
         }
      }

      return null;
   }

   public static List<TrinketHelper.SlotInformation> getSlotInfoForTrinkets(EntityLivingBase entity, Predicate<ItemStack> predicate) {
      List<TrinketHelper.SlotInformation> list = new ArrayList();
      ITrinketContainerHandler Trinket = getTrinketHandler(entity);
      if (Trinket != null) {
         for(int i = 0; i < Trinket.getSlots(); ++i) {
            if (!Trinket.getStackInSlot(i).isEmpty()) {
               ItemStack stack = Trinket.getStackInSlot(i);
               if (predicate.test(stack)) {
                  TrinketHelper.SlotInformation info = getSlotInfo(stack, TrinketHelper.SlotInformation.ItemHandlerType.TRINKETS.getName(), i);
                  list.add(info);
               }
            }
         }
      }

      return list;
   }

   public static boolean baubleCheck(EntityLivingBase entity, Item item) {
      return !getBaubleStack(entity, item).isEmpty();
   }

   public static ItemStack getBaubleStack(EntityLivingBase entity, Item item) {
      return item == null ? ItemStack.EMPTY : getBaubleStack(entity, (stack) -> {
         return !stack.isEmpty() && stack.getItem().getRegistryName().toString().contentEquals(item.getRegistryName().toString());
      });
   }

   public static ItemStack getBaubleStack(EntityLivingBase entity, Predicate<ItemStack> predicate) {
      if (Trinkets.Baubles && entity instanceof EntityPlayer) {
         IBaublesItemHandler baubles = BaublesApi.getBaublesHandler((EntityPlayer)entity);
         if (baubles != null) {
            for(int i = 0; i < baubles.getSlots(); ++i) {
               ItemStack stack = baubles.getStackInSlot(i);
               if (predicate.test(stack)) {
                  return stack;
               }
            }
         }
      }

      return ItemStack.EMPTY;
   }

   public static void applyToBaubles(EntityLivingBase entity, Consumer<ItemStack> consumer) {
      if (Trinkets.Baubles && entity instanceof EntityPlayer) {
         IBaublesItemHandler baubles = BaublesApi.getBaublesHandler((EntityPlayer)entity);
         if (baubles != null) {
            for(int i = 0; i < baubles.getSlots(); ++i) {
               if (!baubles.getStackInSlot(i).isEmpty()) {
                  ItemStack stack = baubles.getStackInSlot(i);
                  if (consumer != null) {
                     consumer.accept(stack);
                  }
               }
            }
         }
      }

   }

   public static int countBaubles(EntityLivingBase entity, Predicate<ItemStack> predicate) {
      int ret = 0;
      if (Trinkets.Baubles && entity instanceof EntityPlayer) {
         IBaublesItemHandler baubles = BaublesApi.getBaublesHandler((EntityPlayer)entity);
         if (baubles != null) {
            for(int i = 0; i < baubles.getSlots(); ++i) {
               if (!baubles.getStackInSlot(i).isEmpty()) {
                  ItemStack stack = baubles.getStackInSlot(i);
                  if (predicate.test(stack)) {
                     ++ret;
                  }
               }
            }
         }
      }

      return ret;
   }

   public static TrinketHelper.SlotInformation getBaubleSlotInformation(EntityLivingBase entity, Predicate<ItemStack> predicate) {
      if (Trinkets.Baubles && entity instanceof EntityPlayer) {
         IBaublesItemHandler baubles = BaublesApi.getBaublesHandler((EntityPlayer)entity);
         if (baubles != null) {
            for(int i = 0; i < baubles.getSlots(); ++i) {
               if (!baubles.getStackInSlot(i).isEmpty()) {
                  ItemStack stack = baubles.getStackInSlot(i);
                  if (predicate.test(stack)) {
                     return getSlotInfo(stack, TrinketHelper.SlotInformation.ItemHandlerType.BAUBLES.getName(), i);
                  }
               }
            }
         }
      }

      return null;
   }

   public static List<TrinketHelper.SlotInformation> getSlotInfoForBaubles(EntityLivingBase entity, Predicate<ItemStack> predicate) {
      List<TrinketHelper.SlotInformation> list = new ArrayList();
      if (Trinkets.Baubles && entity instanceof EntityPlayer) {
         IBaublesItemHandler baubles = BaublesApi.getBaublesHandler((EntityPlayer)entity);
         if (baubles != null) {
            for(int i = 0; i < baubles.getSlots(); ++i) {
               if (!baubles.getStackInSlot(i).isEmpty()) {
                  ItemStack stack = baubles.getStackInSlot(i);
                  if (predicate.test(stack)) {
                     TrinketHelper.SlotInformation info = getSlotInfo(stack, TrinketHelper.SlotInformation.ItemHandlerType.BAUBLES.getName(), i);
                     list.add(info);
                  }
               }
            }
         }
      }

      return list;
   }

   public static ItemStack getItemStackFromSlot(EntityLivingBase player, int slot, int handler) {
      if (player instanceof EntityPlayer && slot >= 0) {
         if (handler == 1) {
            ITrinketContainerHandler Trinket = getTrinketHandler(player);
            if (!Trinket.getStackInSlot(slot).isEmpty()) {
               return Trinket.getStackInSlot(slot);
            }
         } else if (Trinkets.Baubles && handler == 2) {
            IBaublesItemHandler baubles = BaublesApi.getBaublesHandler((EntityPlayer)player);
            if (!baubles.getStackInSlot(slot).isEmpty()) {
               return baubles.getStackInSlot(slot);
            }
         }
      }

      return ItemStack.EMPTY;
   }

   public static NBTTagCompound getTagCompoundSafe(ItemStack stack) {
      NBTTagCompound tagCompound = stack.getTagCompound();
      if (tagCompound == null) {
         tagCompound = new NBTTagCompound();
         stack.setTagCompound(tagCompound);
      }

      return tagCompound;
   }

   public static boolean entityHasAbility(String ability, EntityLivingBase entity) {
      return ((Boolean)Capabilities.getEntityProperties(entity, Boolean.valueOf(false), (prop, rtn) -> {
         try {
            return prop.getAbilityHandler().getAbility(ability) != null;
         } catch (Exception var4) {
            var4.printStackTrace();
            return false;
         }
      })).booleanValue();
   }

   public static class SlotInformation {
      protected int Slot;
      protected String handler;
      protected ItemStack stack;

      public SlotInformation(TrinketHelper.SlotInformation.ItemHandlerType handler) {
         this(handler.getName(), -1);
      }

      public SlotInformation(TrinketHelper.SlotInformation.ItemHandlerType handler, int slot) {
         this(handler.getName(), slot);
      }

      public SlotInformation(String handler, int slot) {
         this(ItemStack.EMPTY, handler, slot);
      }

      public SlotInformation(ItemStack stack, TrinketHelper.SlotInformation.ItemHandlerType handler, int slot) {
         this(stack, handler.getName(), slot);
      }

      public SlotInformation(ItemStack stack, String handler, int slot) {
         this.Slot = -1;
         if (stack == null) {
            stack = ItemStack.EMPTY;
         }

         this.stack = stack;
         this.Slot = slot;
         this.handler = handler;
      }

      public void setSlot(int slot) {
         this.Slot = slot;
      }

      public int getSlot() {
         return this.Slot;
      }

      public void setHandler(TrinketHelper.SlotInformation.ItemHandlerType handler) {
         this.handler = handler.getName();
      }

      public void setHandler(String handler) {
         this.handler = handler;
      }

      public String getItemID() {
         return this.stack.isEmpty() ? "EMPTY" : this.stack.getItem().getRegistryName().toString();
      }

      public String getHandler() {
         return this.handler;
      }

      public TrinketHelper.SlotInformation.ItemHandlerType getHandlerType() {
         return TrinketHelper.SlotInformation.ItemHandlerType.byName(this.handler);
      }

      public ItemStack getSourceStack() {
         return this.stack;
      }

      public ItemStack getStackFromHandler(EntityLivingBase entity) {
         if (entity == null) {
            return ItemStack.EMPTY;
         } else {
            switch(this.getHandlerType()) {
            case TRINKETS:
               ITrinketContainerHandler TrinketHandler = Capabilities.getTrinketContainer(entity);
               return TrinketHandler != null ? Capabilities.getTrinketContainer(entity).getStackInSlot(this.getSlot()) : ItemStack.EMPTY;
            case BAUBLES:
               if (Trinkets.Baubles) {
                  IItemHandler BaublesHandler = BaublesHelper.getBaublesHandler(entity);
                  if (BaublesHandler != null) {
                     return BaublesHandler.getStackInSlot(this.getSlot());
                  }
               }

               return ItemStack.EMPTY;
            case HEAD:
               return entity.getItemStackFromSlot(EntityEquipmentSlot.HEAD);
            case CHEST:
               return entity.getItemStackFromSlot(EntityEquipmentSlot.CHEST);
            case LEGS:
               return entity.getItemStackFromSlot(EntityEquipmentSlot.LEGS);
            case FEET:
               return entity.getItemStackFromSlot(EntityEquipmentSlot.FEET);
            case OFFHAND:
               return entity.getItemStackFromSlot(EntityEquipmentSlot.OFFHAND);
            case MAINHAND:
               return entity.getItemStackFromSlot(EntityEquipmentSlot.MAINHAND);
            case HOTBAR:
               return entity instanceof EntityPlayer && InventoryPlayer.isHotbar(this.getSlot()) ? ((EntityPlayer)entity).inventory.getStackInSlot(this.getSlot()) : ItemStack.EMPTY;
            case INVENTORY:
               return entity instanceof EntityPlayer ? ((EntityPlayer)entity).inventory.getStackInSlot(this.getSlot()) : ItemStack.EMPTY;
            default:
               return ItemStack.EMPTY;
            }
         }
      }

      public String toString() {
         return "ID:" + this.getItemID() + ", Handler:" + this.getHandler() + ", Slot:" + this.getSlot();
      }

      public static enum ItemHandlerType {
         NONE(0, "None"),
         RACE(1, "Race"),
         TRINKETS(2, "Trinkets"),
         BAUBLES(3, "Baubles"),
         INVENTORY(4, "Inventory"),
         HOTBAR(5, "Hotbar"),
         HEAD(6, "Head"),
         CHEST(7, "Chest"),
         LEGS(8, "Legs"),
         FEET(9, "Feet"),
         OFFHAND(10, "OffHand"),
         MAINHAND(11, "MainHand"),
         POTION(12, "Potion"),
         OTHER(13, "Other");

         private static final TrinketHelper.SlotInformation.ItemHandlerType[] ID = new TrinketHelper.SlotInformation.ItemHandlerType[values().length];
         private int id;
         private String name;

         private ItemHandlerType(int id, String name) {
            this.id = id;
            this.name = name;
         }

         public int getId() {
            return this.id;
         }

         public String getName() {
            return this.name;
         }

         public static TrinketHelper.SlotInformation.ItemHandlerType byName(String name) {
            for(int i = 0; i < values().length; ++i) {
               if (byID(i).getName().contentEquals(name)) {
                  return byID(i);
               }
            }

            return NONE;
         }

         public static TrinketHelper.SlotInformation.ItemHandlerType byID(int value) {
            if (value < 0 || value >= values().length) {
               value = 0;
            }

            return values()[value];
         }
      }
   }
}
