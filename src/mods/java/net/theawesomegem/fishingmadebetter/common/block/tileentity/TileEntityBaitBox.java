package net.theawesomegem.fishingmadebetter.common.block.tileentity;

import net.lax1dude.eaglercraft.v1_8.HString;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import javax.annotation.Nullable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.theawesomegem.fishingmadebetter.BetterFishUtil;
import net.theawesomegem.fishingmadebetter.common.capability.world.ChunkCapabilityProvider;
import net.theawesomegem.fishingmadebetter.common.capability.world.IChunkFishingData;
import net.theawesomegem.fishingmadebetter.common.capability.world.PopulationData;
import net.theawesomegem.fishingmadebetter.common.configuration.ConfigurationManager;
import net.theawesomegem.fishingmadebetter.common.configuration.CustomConfigurationHandler;
import net.theawesomegem.fishingmadebetter.common.data.FishData;
import net.theawesomegem.fishingmadebetter.util.TimeUtil;

public class TileEntityBaitBox extends TileEntity implements ITickable {
   private long baitUpdateTime;
   private int timer = 0;
   private ItemStackHandler inventory = new ItemStackHandler(8);

   public TileEntityBaitBox() {
   }

   public void onLoad() {
      if (!this.world.isRemote) {
         this.updateBaitTime(this.world.getTotalWorldTime());
      }
   }

   public void update() {
      if (!this.world.isRemote) {
         if (this.timer % 20 == 0) {
            this.timer = 0;
         }

         if (this.timer == 0) {
            this.updateOnSecond(this.world.getTotalWorldTime());
         }

         ++this.timer;
      }
   }

   public void handleRightClick(World worldIn, BlockPos pos, IBlockState state, EntityPlayer player) {
      ItemStack itemStack = player.getHeldItemMainhand();
      if (!itemStack.isEmpty()) {
         int freeSlot = -1;
         if (!BetterFishUtil.isValidBait(itemStack)) {
            player.sendMessage(new TextComponentTranslation("notif.fishingmadebetter.baitbox.bait_not_valid", new Object[0]));
         } else {
            for(int i = 0; i < this.inventory.getSlots() && itemStack.getCount() > 0; ++i) {
               ItemStack stack = this.inventory.getStackInSlot(i);
               if (stack.isEmpty()) {
                  freeSlot = i;
               } else if (this.isItemStackEqual(itemStack, stack) && stack.getMaxStackSize() - stack.getCount() > 0) {
                  itemStack = this.inventory.insertItem(i, itemStack, false);
               }
            }

            if (itemStack.getCount() > 0 && freeSlot != -1) {
               itemStack = this.inventory.insertItem(freeSlot, itemStack, false);
            }

            if (itemStack.getCount() > 0) {
               player.setHeldItem(EnumHand.MAIN_HAND, itemStack);
               player.sendMessage(new TextComponentTranslation("notif.fishingmadebetter.baitbox.full", new Object[0]));
            } else {
               player.setHeldItem(EnumHand.MAIN_HAND, ItemStack.EMPTY);
            }

         }
      } else {
         Map<String, Integer> baitAmountMap = new HashMap();

         for(int i = 0; i < this.inventory.getSlots(); ++i) {
            ItemStack stack = this.inventory.getStackInSlot(i);
            if (!stack.isEmpty()) {
               String name = stack.getDisplayName();
               if (baitAmountMap.containsKey(name)) {
                  baitAmountMap.put(name, Integer.valueOf(((Integer)baitAmountMap.get(name)).intValue() + stack.getCount()));
               } else {
                  baitAmountMap.put(name, Integer.valueOf(stack.getCount()));
               }
            }
         }

         if (baitAmountMap.isEmpty()) {
            player.sendMessage(new TextComponentTranslation("notif.fishingmadebetter.baitbox.empty", new Object[0]));
         } else {
            for(Entry<String, Integer> baitEntry : baitAmountMap.entrySet()) {
               player.sendMessage(new TextComponentString(HString.format("%s: %d", baitEntry.getKey(), baitEntry.getValue())));
            }
         }

      }
   }

   private void updateOnSecond(long worldTime) {
      if (worldTime > this.baitUpdateTime) {
         IChunkFishingData chunkFishingData = (IChunkFishingData)this.world.getChunkFromBlockCoords(this.getPos()).getCapability(ChunkCapabilityProvider.CHUNK_FISHING_DATA_CAP, (EnumFacing)null);
         if (chunkFishingData == null) {
            return;
         }

         this.updateBaitTime(worldTime);

         for(PopulationData populationData : chunkFishingData.getFishes(worldTime).values()) {
            if (populationData.isHungry(worldTime)) {
               FishData fishData = (FishData)CustomConfigurationHandler.fishDataMap.get(populationData.getFishType());
               if (fishData != null) {
                  this.checkBait(fishData.baitItemMap, populationData.getQuantity(), populationData, worldTime, (fishData.maxWeight + fishData.minWeight) / 2);
               }
            }
         }
      }

   }

   private boolean isItemStackEqual(ItemStack itemStack, ItemStack other) {
      if (!ItemStack.areItemsEqual(itemStack, other)) {
         return false;
      } else if (itemStack.getTagCompound() == null && other.getTagCompound() != null) {
         return false;
      } else {
         return (itemStack.getTagCompound() == null || itemStack.getTagCompound().equals(other.getTagCompound())) && itemStack.areCapsCompatible(other);
      }
   }

   private void updateBaitTime(long worldTime) {
      this.baitUpdateTime = worldTime + TimeUtil.minutesToMinecraftTicks(ConfigurationManager.server.baitBoxUpdateInterval);
   }

   private void checkBait(Map<String, Integer[]> baitItemMap, int population, PopulationData populationData, long worldTime, int avgWeight) {
      if (population >= 2) {
         int itemsLeft = Math.min(16, Math.max(1, avgWeight / 100));

         for(int i = 0; i < this.inventory.getSlots() && itemsLeft > 0; ++i) {
            ItemStack itemStack = this.inventory.getStackInSlot(i);
            if (!itemStack.isEmpty()) {
               String id = itemStack.getItem().getRegistryName().toString();
               if (ConfigurationManager.server.simpleBait || baitItemMap.containsKey(id) && Arrays.asList(baitItemMap.get(id)).contains(Integer.valueOf(itemStack.getMetadata()))) {
                  int itemsToTake = itemsLeft > itemStack.getCount() ? itemStack.getCount() : itemsLeft;
                  itemsLeft -= itemsToTake;
                  this.inventory.extractItem(i, itemsToTake, false);
               }
            }
         }

         if (itemsLeft <= 0) {
            populationData.setLastEatenTime(worldTime);
         }

      }
   }

   public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
      return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY || super.hasCapability(capability, facing);
   }

   @Nullable
   public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
      return (T)(capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY ? this.inventory : super.getCapability(capability, facing));
   }

   public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newSate) {
      return oldState.getBlock() != newSate.getBlock();
   }

   public NBTTagCompound writeToNBT(NBTTagCompound compound) {
      compound.setLong("baitUpdateTime", this.baitUpdateTime);
      compound.setTag("inventory", this.inventory.serializeNBT());
      return super.writeToNBT(compound);
   }

   public void readFromNBT(NBTTagCompound compound) {
      this.baitUpdateTime = compound.getLong("baitUpdateTime");
      this.inventory.deserializeNBT(compound.getCompoundTag("inventory"));
      super.readFromNBT(compound);
   }
}
