package reborncore.common.powerSystem;

import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.capabilities.Capability;
import reborncore.api.IListInfoProvider;
import reborncore.api.power.EnumPowerTier;
import reborncore.api.power.ExternalPowerHandler;
import reborncore.api.power.IEnergyInterfaceTile;
import reborncore.common.RebornCoreConfig;
import reborncore.common.tile.RebornMachineTile;
import reborncore.common.util.StringUtils;

public abstract class TilePowerAcceptor extends RebornMachineTile implements IEnergyInterfaceTile, IListInfoProvider {
   private EnumPowerTier tier;
   private double energy;
   public double extraPowerStoage;
   public double extraPowerInput;
   public int extraTeir;
   public double powerChange;
   public double powerLastTick;
   public boolean checkOverfill;
   public int maxPacketsPerTick;
   private List<ExternalPowerHandler> powerManagers;

   public TilePowerAcceptor() {
      this.checkOverfill = true;
      this.maxPacketsPerTick = 1;
      this.checkTeir();
      this.setupManagers();
   }

   /** @deprecated */
   @Deprecated
   public TilePowerAcceptor(EnumPowerTier tier) {
      this();
   }

   private void setupManagers() {
      this.powerManagers = (List)ExternalPowerSystems.externalPowerHandlerList.stream().map((externalPowerManager) -> {
         return externalPowerManager.createPowerHandler(tile);
      }).filter(Objects::nonNull).collect(Collectors.toList());
   }

   public void checkTeir() {
      if (this.getBaseTier() == null) {
         if (this.getMaxInput() == 0.0D) {
            this.tier = EnumPowerTier.getTeir((int)this.getBaseMaxOutput());
         } else {
            this.tier = EnumPowerTier.getTeir((int)this.getBaseMaxInput());
         }
      }

   }

   public void setExtraPowerStoage(double extraPowerStoage) {
      this.extraPowerStoage = extraPowerStoage;
   }

   public void setMaxPacketsPerTick(int maxPacketsPerTick) {
      this.maxPacketsPerTick = maxPacketsPerTick;
   }

   public double getFreeSpace() {
      return this.getMaxPower() - this.getEnergy();
   }

   public void charge(int slot) {
      if (!this.world.isRemote) {
         double chargeEnergy = Math.min(this.getFreeSpace(), this.getMaxInput());
         if (chargeEnergy > 0.0D) {
            ItemStack batteryStack = this.getStackInSlot(slot);
            if (!batteryStack.isEmpty()) {
               if (ExternalPowerSystems.isPoweredItem(batteryStack)) {
                  ExternalPowerSystems.dischargeItem(this, batteryStack);
               }

            }
         }
      }
   }

   public int getEnergyScaled(int scale) {
      return (int)(this.getEnergy() * (double)scale / this.getMaxPower());
   }

   public void readFromNBTWithoutCoords(NBTTagCompound tag) {
      NBTTagCompound data = tag.getCompoundTag("TilePowerAcceptor");
      if (this.shouldHanldeEnergyNBT()) {
         this.setEnergy(data.getDouble("energy"));
      }

   }

   public NBTTagCompound writeToNBTWithoutCoords(NBTTagCompound tag) {
      NBTTagCompound data = new NBTTagCompound();
      data.setDouble("energy", this.energy);
      tag.setTag("TilePowerAcceptor", data);
      return tag;
   }

   public boolean shouldHanldeEnergyNBT() {
      return true;
   }

   public boolean handleTierWithPower() {
      return true;
   }

   public double getPowerChange() {
      return this.powerChange;
   }

   public void setPowerChange(double powerChange) {
      this.powerChange = powerChange;
   }

   public void update() {
      super.update();
      if (!this.world.isRemote) {
         this.powerManagers.forEach(ExternalPowerHandler::tick);
         this.powerChange = this.getEnergy() - this.powerLastTick;
         this.powerLastTick = this.getEnergy();
      }
   }

   public void readFromNBT(NBTTagCompound tag) {
      super.readFromNBT(tag);
      NBTTagCompound data = tag.getCompoundTag("TilePowerAcceptor");
      if (this.shouldHanldeEnergyNBT()) {
         this.setEnergy(data.getDouble("energy"));
      }

   }

   public NBTTagCompound writeToNBT(NBTTagCompound tag) {
      super.writeToNBT(tag);
      NBTTagCompound data = new NBTTagCompound();
      data.setDouble("energy", this.getEnergy());
      tag.setTag("TilePowerAcceptor", data);
      return tag;
   }

   public void resetUpgrades() {
      super.resetUpgrades();
      this.extraPowerStoage = 0.0D;
      this.extraTeir = 0;
      this.extraPowerInput = 0.0D;
   }

   public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
      return this.powerManagers.stream().filter(Objects::nonNull).anyMatch((externalPowerHandler) -> {
         return externalPowerHandler.hasCapability(capability, facing);
      }) ? true : super.hasCapability(capability, facing);
   }

   public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
      T externalCap = (T)this.powerManagers.stream().filter(Objects::nonNull).map((externalPowerHandler) -> {
         return (T)externalPowerHandler.getCapability(capability, facing);
      }).filter(Objects::nonNull).findFirst().orElse((Object)null);
      return (T)(externalCap != null ? externalCap : super.getCapability(capability, facing));
   }

   public abstract double getBaseMaxPower();

   public abstract double getBaseMaxOutput();

   public abstract double getBaseMaxInput();

   /** @deprecated */
   @Deprecated
   public EnumPowerTier getBaseTier() {
      return null;
   }

   public void invalidate() {
      super.invalidate();
      this.powerManagers.forEach(ExternalPowerHandler::invalidate);
   }

   public void onChunkUnload() {
      super.onChunkUnload();
      this.powerManagers.forEach(ExternalPowerHandler::unload);
   }

   public double getEnergy() {
      return this.energy;
   }

   public void setEnergy(double energy) {
      if (!this.checkOverfill) {
         this.energy = energy;
      } else {
         this.energy = Math.max(Math.min(energy, this.getMaxPower()), 0.0D);
      }
   }

   public double addEnergy(double energy) {
      return this.addEnergy(energy, false);
   }

   public double addEnergy(double energy, boolean simulate) {
      double energyReceived = Math.min(this.getMaxPower(), Math.min(this.getFreeSpace(), energy));
      if (!simulate) {
         this.setEnergy(this.getEnergy() + energyReceived);
      }

      return energyReceived;
   }

   public boolean canUseEnergy(double input) {
      return input <= this.energy;
   }

   public double useEnergy(double energy) {
      return this.useEnergy(energy, false);
   }

   public double useEnergy(double extract, boolean simulate) {
      if (extract > this.energy) {
         extract = this.energy;
      }

      if (!simulate) {
         this.setEnergy(this.energy - extract);
      }

      return extract;
   }

   public boolean canAddEnergy(double energyIn) {
      return this.getEnergy() + energyIn <= this.getMaxPower();
   }

   public double getMaxPower() {
      return this.getBaseMaxPower() + this.extraPowerStoage;
   }

   public double getMaxOutput() {
      double maxOutput = 0.0D;
      if (this.extraTeir > 0) {
         maxOutput = (double)this.getTier().getMaxOutput();
      } else {
         maxOutput = this.getBaseMaxOutput();
      }

      return maxOutput;
   }

   public double getMaxInput() {
      double maxInput = 0.0D;
      if (this.extraTeir > 0) {
         maxInput = (double)this.getTier().getMaxInput();
      } else {
         maxInput = this.getBaseMaxInput();
      }

      return maxInput + this.extraPowerInput;
   }

   public EnumPowerTier getPushingTier() {
      return this.getTier();
   }

   public EnumPowerTier getTier() {
      EnumPowerTier baseTier = this.getBaseTier();
      if (baseTier == null) {
         if (this.tier == null) {
            this.checkTeir();
         }

         baseTier = this.tier;
      }

      if (this.extraTeir > 0) {
         for(EnumPowerTier tier : EnumPowerTier.values()) {
            if (tier.getIC2Tier() == baseTier.getIC2Tier() + this.extraTeir) {
               return tier;
            }
         }

         return EnumPowerTier.INFINITE;
      } else {
         return baseTier;
      }
   }

   public void addInfo(List<String> info, boolean isRealTile) {
      info.add(TextFormatting.GRAY + StringUtils.t("reborncore.tooltip.energy.maxEnergy") + ": " + TextFormatting.GOLD + PowerSystem.getLocaliszedPowerFormatted(this.getMaxPower()));
      if (this.getMaxInput() != 0.0D) {
         info.add(TextFormatting.GRAY + StringUtils.t("reborncore.tooltip.energy.inputRate") + ": " + TextFormatting.GOLD + PowerSystem.getLocaliszedPowerFormatted(this.getMaxInput()));
      }

      if (this.getMaxOutput() != 0.0D) {
         info.add(TextFormatting.GRAY + StringUtils.t("reborncore.tooltip.energy.outputRate") + ": " + TextFormatting.GOLD + PowerSystem.getLocaliszedPowerFormatted(this.getMaxOutput()));
      }

      info.add(TextFormatting.GRAY + StringUtils.t("reborncore.tooltip.energy.tier") + ": " + TextFormatting.GOLD + StringUtils.toFirstCapitalAllLowercase(this.getTier().toString()));
      if (isRealTile) {
         info.add(TextFormatting.GRAY + StringUtils.t("reborncore.tooltip.energy.change") + ": " + TextFormatting.GOLD + PowerSystem.getLocaliszedPowerFormatted(this.getPowerChange()) + "/t");
      }

   }

   /** @deprecated */
   @Deprecated
   public boolean canConnectEnergy(EnumFacing from) {
      return this.canAcceptEnergy(from) || this.canProvideEnergy(from);
   }

   /** @deprecated */
   @Deprecated
   public int receiveEnergy(EnumFacing from, int maxReceive, boolean simulate) {
      if (!this.canAcceptEnergy(from)) {
         return 0;
      } else {
         int feReceived = (int)Math.min((double)(this.getMaxEnergyStored(from) - this.getEnergyStored(from)), Math.min(this.getMaxInput() * (double)RebornCoreConfig.euPerFU, (double)maxReceive));
         int euReceived = feReceived / RebornCoreConfig.euPerFU;
         feReceived = euReceived * RebornCoreConfig.euPerFU;
         if (!simulate) {
            this.setEnergy(this.getEnergy() + (double)euReceived);
         }

         return feReceived;
      }
   }

   /** @deprecated */
   @Deprecated
   public int getEnergyStored(EnumFacing from) {
      return (int)this.getEnergy() * RebornCoreConfig.euPerFU;
   }

   /** @deprecated */
   @Deprecated
   public int getMaxEnergyStored(EnumFacing from) {
      return (int)this.getMaxPower() * RebornCoreConfig.euPerFU;
   }

   /** @deprecated */
   @Deprecated
   public int extractEnergy(EnumFacing from, int maxExtract, boolean simulate) {
      if (!this.canProvideEnergy(from)) {
         return 0;
      } else {
         int euExtracted = Math.min(this.getEnergyStored((EnumFacing)null), maxExtract / RebornCoreConfig.euPerFU);
         int feExtracted = euExtracted * RebornCoreConfig.euPerFU;
         if (!simulate) {
            this.setEnergy(this.getEnergy() - (double)euExtracted);
         }

         return feExtracted;
      }
   }
}
