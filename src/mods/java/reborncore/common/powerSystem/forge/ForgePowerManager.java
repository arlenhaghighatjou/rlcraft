package reborncore.common.powerSystem.forge;

import java.util.function.Predicate;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import reborncore.api.power.ExternalPowerHandler;
import reborncore.api.power.ExternalPowerManager;
import reborncore.common.RebornCoreConfig;
import reborncore.common.powerSystem.ExternalPowerSystems;
import reborncore.common.powerSystem.TilePowerAcceptor;
import reborncore.common.registration.RebornRegistry;

@RebornRegistry
public class ForgePowerManager implements ExternalPowerManager {
   public ForgePowerManager() {
   }

   public ExternalPowerHandler createPowerHandler(TilePowerAcceptor acceptor) {
      return !RebornCoreConfig.enableFE ? null : new ForgePowerHandler(acceptor);
   }

   public boolean isPoweredItem(ItemStack stack) {
      return !RebornCoreConfig.enableFE ? false : stack.hasCapability(CapabilityEnergy.ENERGY, (EnumFacing)null);
   }

   public boolean isPoweredTile(TileEntity tileEntity, EnumFacing side) {
      return !RebornCoreConfig.enableFE ? false : tileEntity.hasCapability(CapabilityEnergy.ENERGY, side);
   }

   public void dischargeItem(TilePowerAcceptor powerAcceptor, ItemStack stack) {
      if (RebornCoreConfig.enableFE && !isOtherPoweredItem(stack)) {
         IEnergyStorage powerItem = (IEnergyStorage)stack.getCapability(CapabilityEnergy.ENERGY, (EnumFacing)null);
         if (powerItem != null) {
            double chargeEnergy = Math.min(powerAcceptor.getFreeSpace(), powerAcceptor.getMaxInput()) * (double)RebornCoreConfig.euPerFU;
            if (chargeEnergy > 0.0D) {
               if (powerItem.getEnergyStored() > 0) {
                  int extracted = powerItem.extractEnergy((int)chargeEnergy, false);
                  powerAcceptor.addEnergy((double)(extracted / RebornCoreConfig.euPerFU));
               }

            }
         }
      }
   }

   public void chargeItem(TilePowerAcceptor powerAcceptor, ItemStack stack) {
      if (RebornCoreConfig.enableFE && !isOtherPoweredItem(stack)) {
         IEnergyStorage powerItem = (IEnergyStorage)stack.getCapability(CapabilityEnergy.ENERGY, (EnumFacing)null);
         if (powerItem != null) {
            int maxReceive = powerItem.receiveEnergy((int)powerAcceptor.getMaxOutput() * RebornCoreConfig.euPerFU, true);
            double maxUse = Math.min((double)(maxReceive / RebornCoreConfig.euPerFU), powerAcceptor.getMaxOutput());
            if (powerAcceptor.getEnergy() >= 0.0D && maxReceive > 0) {
               powerItem.receiveEnergy((int)powerAcceptor.useEnergy(maxUse) * RebornCoreConfig.euPerFU, false);
            }

         }
      }
   }

   public void chargeItem(ForgePowerItemManager powerAcceptor, ItemStack stack) {
      if (RebornCoreConfig.enableFE && !isOtherPoweredItem(stack)) {
         IEnergyStorage powerItem = (IEnergyStorage)stack.getCapability(CapabilityEnergy.ENERGY, (EnumFacing)null);
         if (powerItem != null) {
            int maxReceive = powerItem.receiveEnergy(powerAcceptor.extractEnergy(powerAcceptor.getEnergyStored(), true), true);
            if (maxReceive > 0) {
               powerItem.receiveEnergy(powerAcceptor.extractEnergy(maxReceive, false), false);
            }

         }
      }
   }

   private static boolean isOtherPoweredItem(ItemStack stack) {
      return ExternalPowerSystems.externalPowerHandlerList.stream().filter((externalPowerManager) -> {
         return !(externalPowerManager instanceof ForgePowerManager);
      }).anyMatch((externalPowerManager) -> {
         return externalPowerManager.isPoweredItem(stack);
      });
   }
}
