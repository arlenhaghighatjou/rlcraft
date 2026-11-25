package reborncore.common.tile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import reborncore.RebornCore;
import reborncore.common.util.Inventory;
import reborncore.common.util.ItemUtils;

public class SlotConfiguration implements INBTSerializable<NBTTagCompound> {
   List<SlotConfiguration.SlotConfigHolder> slotDetails = new ArrayList();
   @Nullable
   Inventory inventory;

   public SlotConfiguration() {
   }

   public SlotConfiguration(Inventory inventory) {
      this.inventory = inventory;

      for(int i = 0; i < inventory.getSizeInventory(); ++i) {
         this.updateSlotDetails(new SlotConfiguration.SlotConfigHolder(i));
      }

   }

   public void update(RebornMachineTile machineBase) {
      if (this.inventory == null && machineBase.getInventoryForTile().isPresent()) {
         this.inventory = (Inventory)machineBase.getInventoryForTile().get();
      }

      if (this.inventory != null && this.slotDetails.size() != this.inventory.getSizeInventory()) {
         for(int i = 0; i < this.inventory.getSizeInventory(); ++i) {
            SlotConfiguration.SlotConfigHolder holder = this.getSlotDetails(i);
            if (holder == null) {
               RebornCore.logHelper.debug("Fixed slot " + i + " in " + machineBase);
               this.updateSlotDetails(new SlotConfiguration.SlotConfigHolder(i));
            }
         }
      }

      if (!machineBase.getWorld().isRemote && machineBase.getWorld().getTotalWorldTime() % (long)machineBase.slotTransferSpeed() == 0L) {
         this.getSlotDetails().forEach((slotConfigHolder) -> {
            slotConfigHolder.handleItemIO(machineBase);
         });
      }

   }

   public SlotConfiguration(NBTTagCompound tagCompound) {
      this.deserializeNBT(tagCompound);
   }

   public List<SlotConfiguration.SlotConfigHolder> getSlotDetails() {
      return this.slotDetails;
   }

   public SlotConfiguration.SlotConfigHolder updateSlotDetails(SlotConfiguration.SlotConfigHolder slotConfigHolder) {
      SlotConfiguration.SlotConfigHolder lookup = this.getSlotDetails(slotConfigHolder.slotID);
      if (lookup != null) {
         this.slotDetails.remove(lookup);
      }

      this.slotDetails.add(slotConfigHolder);
      return slotConfigHolder;
   }

   @Nullable
   public SlotConfiguration.SlotConfigHolder getSlotDetails(int id) {
      for(SlotConfiguration.SlotConfigHolder detail : this.slotDetails) {
         if (detail.slotID == id) {
            return detail;
         }
      }

      return null;
   }

   public List<SlotConfiguration.SlotConfig> getSlotsForSide(EnumFacing facing) {
      return (List)this.slotDetails.stream().map((slotConfigHolder) -> {
         return slotConfigHolder.getSideDetail(facing);
      }).collect(Collectors.toList());
   }

   public NBTTagCompound serializeNBT() {
      NBTTagCompound tagCompound = new NBTTagCompound();
      tagCompound.setInteger("size", this.slotDetails.size());

      for(int i = 0; i < this.slotDetails.size(); ++i) {
         tagCompound.setTag("slot_" + i, ((SlotConfiguration.SlotConfigHolder)this.slotDetails.get(i)).serializeNBT());
      }

      return tagCompound;
   }

   public void deserializeNBT(NBTTagCompound nbt) {
      int size = nbt.getInteger("size");

      for(int i = 0; i < size; ++i) {
         NBTTagCompound tagCompound = nbt.getCompoundTag("slot_" + i);
         SlotConfiguration.SlotConfigHolder slotConfigHolder = new SlotConfiguration.SlotConfigHolder(tagCompound);
         this.updateSlotDetails(slotConfigHolder);
      }

   }

   public String toJson(String machineIdent) {
      NBTTagCompound tagCompound = new NBTTagCompound();
      tagCompound.setTag("data", this.serializeNBT());
      tagCompound.setString("machine", machineIdent);
      return tagCompound.toString();
   }

   public void readJson(String json, String machineIdent) throws UnsupportedOperationException {
      NBTTagCompound compound;
      try {
         compound = JsonToNBT.getTagFromJson(json);
      } catch (NBTException var5) {
         throw new UnsupportedOperationException("Clipboard conetents isnt a valid slot configuation");
      }

      if (compound.hasKey("machine") && compound.getString("machine").equals(machineIdent)) {
         this.deserializeNBT(compound.getCompoundTag("data"));
      } else {
         throw new UnsupportedOperationException("Machine config is not for this machine.");
      }
   }

   public static enum ExtractConfig {
      NONE(false, false),
      INPUT(false, true),
      OUTPUT(true, false);

      boolean extact;
      boolean insert;

      private ExtractConfig(boolean extact, boolean insert) {
         this.extact = extact;
         this.insert = insert;
      }

      public boolean isExtact() {
         return this.extact;
      }

      public boolean isInsert() {
         return this.insert;
      }

      public SlotConfiguration.ExtractConfig getNext() {
         int i = this.ordinal() + 1;
         if (i >= values().length) {
            i = 0;
         }

         return values()[i];
      }
   }

   public static class SlotConfig implements INBTSerializable<NBTTagCompound> {
      EnumFacing side;
      SlotConfiguration.SlotIO slotIO;
      int slotID;

      public SlotConfig(EnumFacing side, int slotID) {
         this.side = side;
         this.slotID = slotID;
         this.slotIO = new SlotConfiguration.SlotIO(SlotConfiguration.ExtractConfig.NONE);
      }

      public SlotConfig(EnumFacing side, SlotConfiguration.SlotIO slotIO, int slotID) {
         this.side = side;
         this.slotIO = slotIO;
         this.slotID = slotID;
      }

      public SlotConfig(NBTTagCompound tagCompound) {
         this.deserializeNBT(tagCompound);
      }

      public EnumFacing getSide() {
         return this.side;
      }

      public SlotConfiguration.SlotIO getSlotIO() {
         return this.slotIO;
      }

      public int getSlotID() {
         return this.slotID;
      }

      private void handleItemInput(RebornMachineTile machineBase) {
         Inventory inventory = (Inventory)machineBase.getInventoryForTile().get();
         ItemStack targetStack = inventory.getStackInSlot(this.slotID);
         if (targetStack.getMaxStackSize() != targetStack.getCount()) {
            TileEntity tileEntity = machineBase.getWorld().getTileEntity(machineBase.getPos().offset(this.side));
            if (tileEntity != null && tileEntity.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, this.side.getOpposite())) {
               IItemHandler sourceHandler = (IItemHandler)tileEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, this.side.getOpposite());

               for(int i = 0; i < sourceHandler.getSlots(); ++i) {
                  ItemStack sourceStack = sourceHandler.getStackInSlot(i);
                  if (!sourceStack.isEmpty() && (targetStack.isEmpty() || ItemUtils.isItemEqual(sourceStack, targetStack, true, true, false)) && machineBase.isItemValidForSlot(this.slotID, sourceStack)) {
                     int extract = 4;
                     if (!targetStack.isEmpty()) {
                        extract = Math.min(targetStack.getMaxStackSize() - targetStack.getCount(), extract);
                     }

                     ItemStack extractedStack = sourceHandler.extractItem(i, extract, false);
                     if (targetStack.isEmpty()) {
                        inventory.setInventorySlotContents(this.slotID, extractedStack);
                     } else {
                        inventory.getStackInSlot(this.slotID).grow(extractedStack.getCount());
                     }

                     inventory.hasChanged = true;
                     break;
                  }
               }

            }
         }
      }

      private void handleItemOutput(RebornMachineTile machineBase) {
         Inventory inventory = (Inventory)machineBase.getInventoryForTile().get();
         ItemStack sourceStack = inventory.getStackInSlot(this.slotID);
         if (!sourceStack.isEmpty()) {
            TileEntity tileEntity = machineBase.getWorld().getTileEntity(machineBase.getPos().offset(this.side));
            if (tileEntity != null && tileEntity.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, this.side.getOpposite())) {
               IItemHandler destHandler = (IItemHandler)tileEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, this.side.getOpposite());
               ItemStack stack = ItemHandlerHelper.insertItemStacked(destHandler, sourceStack, false);
               inventory.setInventorySlotContents(this.slotID, stack);
            }
         }
      }

      public NBTTagCompound serializeNBT() {
         NBTTagCompound tagCompound = new NBTTagCompound();
         tagCompound.setInteger("side", this.side.ordinal());
         tagCompound.setTag("config", this.slotIO.serializeNBT());
         tagCompound.setInteger("slot", this.slotID);
         return tagCompound;
      }

      public void deserializeNBT(NBTTagCompound nbt) {
         this.side = EnumFacing.VALUES[nbt.getInteger("side")];
         this.slotIO = new SlotConfiguration.SlotIO(nbt.getCompoundTag("config"));
         this.slotID = nbt.getInteger("slot");
      }
   }

   public static class SlotConfigHolder implements INBTSerializable<NBTTagCompound> {
      int slotID;
      HashMap<EnumFacing, SlotConfiguration.SlotConfig> sideMap;
      boolean input;
      boolean output;
      boolean filter;

      public SlotConfigHolder(int slotID) {
         this.slotID = slotID;
         this.sideMap = new HashMap();
         Arrays.stream(EnumFacing.VALUES).forEach((facing) -> {
            SlotConfiguration.SlotConfig var10000 = (SlotConfiguration.SlotConfig)this.sideMap.put(facing, new SlotConfiguration.SlotConfig(facing, slotID));
         });
      }

      public SlotConfigHolder(NBTTagCompound tagCompound) {
         this.sideMap = new HashMap();
         this.deserializeNBT(tagCompound);
      }

      public SlotConfiguration.SlotConfig getSideDetail(EnumFacing side) {
         return (SlotConfiguration.SlotConfig)this.sideMap.get(side);
      }

      public List<SlotConfiguration.SlotConfig> getAllSides() {
         return new ArrayList(this.sideMap.values());
      }

      public void updateSlotConfig(SlotConfiguration.SlotConfig config) {
         SlotConfiguration.SlotConfig toEdit = (SlotConfiguration.SlotConfig)this.sideMap.get(config.side);
         toEdit.slotIO = config.slotIO;
      }

      private void handleItemIO(RebornMachineTile machineBase) {
         if (this.input || this.output) {
            this.getAllSides().stream().filter((config) -> {
               return config.getSlotIO().getIoConfig() != SlotConfiguration.ExtractConfig.NONE;
            }).forEach((config) -> {
               if (this.input && config.getSlotIO().getIoConfig() == SlotConfiguration.ExtractConfig.INPUT) {
                  config.handleItemInput(machineBase);
               }

               if (this.output && config.getSlotIO().getIoConfig() == SlotConfiguration.ExtractConfig.OUTPUT) {
                  config.handleItemOutput(machineBase);
               }

            });
         }
      }

      public boolean autoInput() {
         return this.input;
      }

      public boolean autoOutput() {
         return this.output;
      }

      public boolean filter() {
         return this.filter;
      }

      public void setInput(boolean input) {
         this.input = input;
      }

      public void setOutput(boolean output) {
         this.output = output;
      }

      public void setfilter(boolean filter) {
         this.filter = filter;
      }

      public NBTTagCompound serializeNBT() {
         NBTTagCompound compound = new NBTTagCompound();
         compound.setInteger("slotID", this.slotID);
         Arrays.stream(EnumFacing.VALUES).forEach((facing) -> {
            compound.setTag("side_" + facing.ordinal(), ((SlotConfiguration.SlotConfig)this.sideMap.get(facing)).serializeNBT());
         });
         compound.setBoolean("input", this.input);
         compound.setBoolean("output", this.output);
         compound.setBoolean("filter", this.filter);
         return compound;
      }

      public void deserializeNBT(NBTTagCompound nbt) {
         this.sideMap.clear();
         this.slotID = nbt.getInteger("slotID");
         Arrays.stream(EnumFacing.VALUES).forEach((facing) -> {
            NBTTagCompound compound = nbt.getCompoundTag("side_" + facing.ordinal());
            SlotConfiguration.SlotConfig config = new SlotConfiguration.SlotConfig(compound);
            this.sideMap.put(facing, config);
         });
         this.input = nbt.getBoolean("input");
         this.output = nbt.getBoolean("output");
         if (nbt.hasKey("filter")) {
            this.filter = nbt.getBoolean("filter");
         }

      }
   }

   public static class SlotIO implements INBTSerializable<NBTTagCompound> {
      SlotConfiguration.ExtractConfig ioConfig;

      public SlotIO(NBTTagCompound tagCompound) {
         this.deserializeNBT(tagCompound);
      }

      public SlotIO(SlotConfiguration.ExtractConfig ioConfig) {
         this.ioConfig = ioConfig;
      }

      public SlotConfiguration.ExtractConfig getIoConfig() {
         return this.ioConfig;
      }

      public NBTTagCompound serializeNBT() {
         NBTTagCompound compound = new NBTTagCompound();
         compound.setInteger("config", this.ioConfig.ordinal());
         return compound;
      }

      public void deserializeNBT(NBTTagCompound nbt) {
         this.ioConfig = SlotConfiguration.ExtractConfig.values()[nbt.getInteger("config")];
      }
   }
}
