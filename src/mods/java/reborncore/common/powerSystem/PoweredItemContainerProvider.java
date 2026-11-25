package reborncore.common.powerSystem;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.energy.CapabilityEnergy;
import reborncore.api.power.IEnergyItemInfo;
import reborncore.common.RebornCoreConfig;
import reborncore.common.powerSystem.forge.ForgePowerItemManager;

public class PoweredItemContainerProvider implements ICapabilityProvider {
   ItemStack stack;
   ForgePowerItemManager capEnergy;
   final boolean isEnergyItem;

   public PoweredItemContainerProvider(ItemStack stack) {
      this.stack = stack;
      this.isEnergyItem = stack.getItem() instanceof IEnergyItemInfo;
      if (this.isEnergyItem) {
         this.capEnergy = new ForgePowerItemManager(stack);
      }

   }

   public PoweredItemContainerProvider(ItemStack stack, IEnergyItemInfo itemPowerInfo) {
      this.isEnergyItem = true;
      this.capEnergy = new ForgePowerItemManager(stack, itemPowerInfo);
      this.stack = stack;
   }

   public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
      return this.isEnergyItem && capability == CapabilityEnergy.ENERGY && RebornCoreConfig.enableFE;
   }

   @Nullable
   public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
      return (T)(this.isEnergyItem && capability == CapabilityEnergy.ENERGY && RebornCoreConfig.enableFE ? CapabilityEnergy.ENERGY.cast(this.capEnergy) : null);
   }

   public ForgePowerItemManager getCapEnergy() {
      return this.capEnergy;
   }
}
