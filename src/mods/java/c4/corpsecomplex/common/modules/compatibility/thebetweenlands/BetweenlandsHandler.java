package c4.corpsecomplex.common.modules.compatibility.thebetweenlands;

import c4.corpsecomplex.common.modules.inventory.InventoryModule;
import c4.corpsecomplex.common.modules.inventory.capability.IDeathInventory;
import c4.corpsecomplex.common.modules.inventory.enchantment.EnchantmentModule;
import c4.corpsecomplex.common.modules.inventory.helpers.DeathInventoryHandler;
import c4.corpsecomplex.common.modules.inventory.helpers.DeathStackHelper;
import java.util.EnumMap;
import java.util.Map;
import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import thebetweenlands.api.capability.IEquipmentCapability;
import thebetweenlands.common.capability.equipment.EnumEquipmentInventory;
import thebetweenlands.common.registries.CapabilityRegistry;

public class BetweenlandsHandler extends DeathInventoryHandler {
   private static final String MOD_ID = "thebetweenlands";
   private IEquipmentCapability betweenlandsEquipment;

   public BetweenlandsHandler(EntityPlayer player) {
      super(player, "thebetweenlands");
      this.betweenlandsEquipment = (IEquipmentCapability)player.getCapability(CapabilityRegistry.CAPABILITY_EQUIPMENT, (EnumFacing)null);
   }

   public boolean checkToStore(int slot) {
      return BetweenlandsModule.keepBetweenlands;
   }

   public void storeInventory() {
      Map<EnumEquipmentInventory, IInventory> inventories = new EnumMap(EnumEquipmentInventory.class);
      if (this.betweenlandsEquipment != null) {
         EnumEquipmentInventory.VALUES.forEach((value) -> {
            IInventory var10000 = (IInventory)inventories.put(value, this.betweenlandsEquipment.getInventory(value));
         });
         EaglercraftRandom generator = new EaglercraftRandom();
         NBTTagCompound compound = new NBTTagCompound();
         inventories.forEach((equipment, inventory) -> {
            String name = equipment.toString();
            NonNullList<ItemStack> storedStacks = NonNullList.withSize(inventory.getSizeInventory(), ItemStack.EMPTY);

            for(int i = 0; i < inventory.getSizeInventory(); ++i) {
               ItemStack stack = inventory.getStackInSlot(i);
               if (!stack.isEmpty()) {
                  boolean essential = DeathStackHelper.isEssential(stack);
                  boolean cursed = !essential && DeathStackHelper.isCursed(stack);
                  boolean store = this.checkToStore(0) && !cursed || essential;
                  if (!store && EnchantmentModule.registerEnchant) {
                     int level = EnchantmentHelper.getEnchantmentLevel(EnchantmentModule.soulbound, stack);
                     if (level != 0) {
                        store = essential = DeathStackHelper.handleSoulbound(stack, level);
                        cursed = !essential && cursed;
                     }
                  }

                  if (cursed && InventoryModule.destroyCursed) {
                     inventory.setInventorySlotContents(i, ItemStack.EMPTY);
                  } else {
                     if (InventoryModule.dropLoss > 0.0D || InventoryModule.keptLoss > 0.0D) {
                        DeathStackHelper.loseDurability(this.player, stack, store);
                     }

                     if (stack.isEmpty()) {
                        return;
                     }

                     if (store) {
                        if (!essential && generator.nextDouble() < InventoryModule.randomDrop) {
                           if (generator.nextDouble() < InventoryModule.randomDestroy) {
                              inventory.setInventorySlotContents(i, ItemStack.EMPTY);
                           }
                        } else {
                           storedStacks.set(i, stack.copy());
                           inventory.setInventorySlotContents(i, ItemStack.EMPTY);
                        }
                     }
                  }
               }
            }

            NBTTagCompound tag = new NBTTagCompound();
            ItemStackHelper.saveAllItems(tag, storedStacks);
            compound.setTag(name, tag);
         });
         this.deathInventory.addStorage("thebetweenlands", compound);
      }
   }

   public void retrieveInventory(IDeathInventory oldDeathInventory) {
      NBTTagCompound storage = oldDeathInventory.getStorage("thebetweenlands");
      if (storage != null && this.betweenlandsEquipment != null) {
         EnumEquipmentInventory.VALUES.forEach((value) -> {
            NBTTagCompound tag = storage.getCompoundTag(value.toString());
            if (!tag.hasNoTags()) {
               IInventory inventory = this.betweenlandsEquipment.getInventory(value);
               NonNullList<ItemStack> stored = NonNullList.withSize(inventory.getSizeInventory(), ItemStack.EMPTY);
               ItemStackHelper.loadAllItems(tag, stored);

               for(int i = 0; i < stored.size(); ++i) {
                  ItemStack stack = (ItemStack)stored.get(i);
                  if (!stack.isEmpty()) {
                     inventory.setInventorySlotContents(i, stack);
                  }
               }
            }

         });
      }

   }
}
