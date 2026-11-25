package reborncore.common.tile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;

public class FluidConfiguration implements INBTSerializable<NBTTagCompound> {
   HashMap<EnumFacing, FluidConfiguration.FluidConfig> sideMap = new HashMap();
   boolean input;
   boolean output;

   public FluidConfiguration() {
      Arrays.stream(EnumFacing.VALUES).forEach((facing) -> {
         FluidConfiguration.FluidConfig var10000 = (FluidConfiguration.FluidConfig)this.sideMap.put(facing, new FluidConfiguration.FluidConfig(facing));
      });
   }

   public FluidConfiguration(NBTTagCompound tagCompound) {
      this.deserializeNBT(tagCompound);
   }

   public FluidConfiguration.FluidConfig getSideDetail(EnumFacing side) {
      return side == null ? (FluidConfiguration.FluidConfig)this.sideMap.get(EnumFacing.NORTH) : (FluidConfiguration.FluidConfig)this.sideMap.get(side);
   }

   public List<FluidConfiguration.FluidConfig> getAllSides() {
      return new ArrayList(this.sideMap.values());
   }

   public void updateFluidConfig(FluidConfiguration.FluidConfig config) {
      FluidConfiguration.FluidConfig toEdit = (FluidConfiguration.FluidConfig)this.sideMap.get(config.side);
      toEdit.ioConfig = config.ioConfig;
   }

   public void update(RebornMachineTile machineBase) {
      if (this.input || this.output) {
         if (machineBase.getTank() != null && machineBase.getWorld().getTotalWorldTime() % (long)machineBase.slotTransferSpeed() == 0L) {
            for(EnumFacing facing : EnumFacing.VALUES) {
               FluidConfiguration.FluidConfig fluidConfig = this.getSideDetail(facing);
               if (fluidConfig != null && fluidConfig.getIoConfig().isEnabled()) {
                  IFluidHandler fluidHandler = this.getFluidHandler(machineBase, facing);
                  if (fluidHandler != null) {
                     if (this.autoInput() && fluidConfig.getIoConfig().isInsert()) {
                        FluidUtil.tryFluidTransfer(machineBase.getTank(), fluidHandler, machineBase.fluidTransferAmount(), true);
                     }

                     if (this.autoOutput() && fluidConfig.getIoConfig().isExtact()) {
                        FluidUtil.tryFluidTransfer(fluidHandler, machineBase.getTank(), machineBase.fluidTransferAmount(), true);
                     }
                  }
               }
            }

         }
      }
   }

   private IFluidHandler getFluidHandler(RebornMachineTile machine, EnumFacing facing) {
      BlockPos pos = machine.getPos().offset(facing);
      TileEntity tileEntity = machine.getWorld().getTileEntity(pos);
      return tileEntity == null ? null : (IFluidHandler)tileEntity.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, facing.getOpposite());
   }

   public boolean autoInput() {
      return this.input;
   }

   public boolean autoOutput() {
      return this.output;
   }

   public void setInput(boolean input) {
      this.input = input;
   }

   public void setOutput(boolean output) {
      this.output = output;
   }

   public NBTTagCompound serializeNBT() {
      NBTTagCompound compound = new NBTTagCompound();
      Arrays.stream(EnumFacing.VALUES).forEach((facing) -> {
         compound.setTag("side_" + facing.ordinal(), ((FluidConfiguration.FluidConfig)this.sideMap.get(facing)).serializeNBT());
      });
      compound.setBoolean("input", this.input);
      compound.setBoolean("output", this.output);
      return compound;
   }

   public void deserializeNBT(NBTTagCompound nbt) {
      this.sideMap.clear();
      Arrays.stream(EnumFacing.VALUES).forEach((facing) -> {
         NBTTagCompound compound = nbt.getCompoundTag("side_" + facing.ordinal());
         FluidConfiguration.FluidConfig config = new FluidConfiguration.FluidConfig(compound);
         this.sideMap.put(facing, config);
      });
      this.input = nbt.getBoolean("input");
      this.output = nbt.getBoolean("output");
   }

   public static enum ExtractConfig {
      NONE(false, false),
      INPUT(false, true),
      OUTPUT(true, false),
      ALL(true, true);

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

      public boolean isEnabled() {
         return this.extact || this.insert;
      }

      public FluidConfiguration.ExtractConfig getNext() {
         int i = this.ordinal() + 1;
         if (i >= values().length) {
            i = 0;
         }

         return values()[i];
      }
   }

   public static class FluidConfig implements INBTSerializable<NBTTagCompound> {
      EnumFacing side;
      FluidConfiguration.ExtractConfig ioConfig;

      public FluidConfig(EnumFacing side) {
         this.side = side;
         this.ioConfig = FluidConfiguration.ExtractConfig.ALL;
      }

      public FluidConfig(EnumFacing side, FluidConfiguration.ExtractConfig ioConfig) {
         this.side = side;
         this.ioConfig = ioConfig;
      }

      public FluidConfig(NBTTagCompound tagCompound) {
         this.deserializeNBT(tagCompound);
      }

      public EnumFacing getSide() {
         return this.side;
      }

      public FluidConfiguration.ExtractConfig getIoConfig() {
         return this.ioConfig;
      }

      public NBTTagCompound serializeNBT() {
         NBTTagCompound tagCompound = new NBTTagCompound();
         tagCompound.setInteger("side", this.side.ordinal());
         tagCompound.setInteger("config", this.ioConfig.ordinal());
         return tagCompound;
      }

      public void deserializeNBT(NBTTagCompound nbt) {
         this.side = EnumFacing.VALUES[nbt.getInteger("side")];
         this.ioConfig = FluidConfiguration.ExtractConfig.values()[nbt.getInteger("config")];
      }
   }
}
