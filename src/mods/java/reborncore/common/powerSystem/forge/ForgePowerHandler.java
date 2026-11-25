package reborncore.common.powerSystem.forge;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Predicate;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import reborncore.api.power.ExternalPowerHandler;
import reborncore.api.power.IEnergyInterfaceTile;
import reborncore.common.RebornCoreConfig;
import reborncore.common.powerSystem.ExternalPowerSystems;
import reborncore.common.powerSystem.TilePowerAcceptor;

public class ForgePowerHandler implements ExternalPowerHandler {
   TilePowerAcceptor powerAcceptor;
   ForgeEnergyStorage powerManager;

   public ForgePowerHandler(TilePowerAcceptor powerAcceptor) {
      this.powerAcceptor = powerAcceptor;
      this.powerManager = new ForgeEnergyStorage(powerAcceptor, (EnumFacing)null);
   }

   public void tick() {
      Map<EnumFacing, TileEntity> acceptors = new HashMap();
      if (this.powerAcceptor.getEnergy() > 0.0D) {
         for(EnumFacing side : EnumFacing.values()) {
            if (this.powerAcceptor.canProvideEnergy(side)) {
               TileEntity tile = this.powerAcceptor.getWorld().getTileEntity(this.powerAcceptor.getPos().offset(side));
               if (tile != null && !isOtherPoweredTile(tile, side.getOpposite())) {
                  if (tile instanceof IEnergyInterfaceTile) {
                     IEnergyInterfaceTile eFace = (IEnergyInterfaceTile)tile;
                     if (eFace.canAcceptEnergy(side.getOpposite())) {
                        acceptors.put(side, tile);
                     }
                  } else if (tile.hasCapability(CapabilityEnergy.ENERGY, side.getOpposite())) {
                     acceptors.put(side, tile);
                  }
               }
            }
         }
      }

      if (acceptors.size() > 0) {
         double drain = this.powerAcceptor.useEnergy(Math.min(this.powerAcceptor.getEnergy(), this.powerAcceptor.getMaxOutput()), true);
         double energyShare = drain / (double)acceptors.size();
         double remainingEnergy = drain;
         if (energyShare > 0.0D) {
            for(Entry<EnumFacing, TileEntity> entry : acceptors.entrySet()) {
               EnumFacing side = (EnumFacing)entry.getKey();
               TileEntity tile = (TileEntity)entry.getValue();
               if (tile instanceof IEnergyInterfaceTile) {
                  IEnergyInterfaceTile eFace = (IEnergyInterfaceTile)tile;
                  if (RebornCoreConfig.smokeHighTeir && this.powerAcceptor.handleTierWithPower() && eFace.getTier().ordinal() < this.powerAcceptor.getPushingTier().ordinal()) {
                     World world = this.powerAcceptor.getWorld();
                     BlockPos pos = this.powerAcceptor.getPos();

                     for(int j = 0; j < 2; ++j) {
                        double d3 = (double)pos.getX() + world.rand.nextDouble() + (double)(side.getFrontOffsetX() / 2);
                        double d8 = (double)pos.getY() + world.rand.nextDouble() + 1.0D;
                        double d13 = (double)pos.getZ() + world.rand.nextDouble() + (double)(side.getFrontOffsetZ() / 2);
                        ((WorldServer)world).spawnParticle(EnumParticleTypes.SMOKE_LARGE, false, d3, d8, d13, 2, 0.0D, 0.0D, 0.0D, 0.0D, new int[0]);
                     }
                  } else {
                     double filled = eFace.addEnergy(Math.min(energyShare, remainingEnergy), false);
                     remainingEnergy -= this.powerAcceptor.useEnergy(filled, false);
                  }
               } else if (tile.hasCapability(CapabilityEnergy.ENERGY, side.getOpposite())) {
                  IEnergyStorage energyStorage = (IEnergyStorage)tile.getCapability(CapabilityEnergy.ENERGY, side.getOpposite());
                  if (this.powerManager != null && energyStorage != null && energyStorage.canReceive() && this.powerAcceptor.canProvideEnergy(side)) {
                     int filled = energyStorage.receiveEnergy((int)Math.min(energyShare, remainingEnergy) * RebornCoreConfig.euPerFU, false);
                     remainingEnergy -= this.powerAcceptor.useEnergy((double)(filled / RebornCoreConfig.euPerFU), false);
                  }
               }
            }
         }
      }

   }

   public void unload() {
   }

   public void invalidate() {
   }

   public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
      return capability == CapabilityEnergy.ENERGY && (this.powerAcceptor.canAcceptEnergy(facing) || this.powerAcceptor.canProvideEnergy(facing));
   }

   @Nullable
   public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
      if (capability == CapabilityEnergy.ENERGY && (this.powerAcceptor.canAcceptEnergy(facing) || this.powerAcceptor.canProvideEnergy(facing))) {
         if (this.powerManager == null) {
            this.powerManager = new ForgeEnergyStorage(this.powerAcceptor, facing);
         }

         this.powerManager.setFacing(facing);
         return (T)CapabilityEnergy.ENERGY.cast(this.powerManager);
      } else {
         return (T)null;
      }
   }

   private static boolean isOtherPoweredTile(TileEntity tileEntity, EnumFacing facing) {
      return ExternalPowerSystems.externalPowerHandlerList.stream().filter((externalPowerManager) -> {
         return !(externalPowerManager instanceof ForgePowerManager);
      }).anyMatch((externalPowerManager) -> {
         return externalPowerManager.isPoweredTile(tileEntity, facing);
      });
   }
}
