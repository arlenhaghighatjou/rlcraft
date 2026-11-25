package ladysnake.spawnercontrol.controlledspawner;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import ladysnake.spawnercontrol.config.MSCConfig;
import ladysnake.spawnercontrol.config.SpawnerConfig;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;

public class TileEntityControlledSpawner extends TileEntityMobSpawner {
   private IControllableSpawner handler = new TileEntityControlledSpawner.ControlledSpawnerHandler();

   public TileEntityControlledSpawner() {
   }

   @Nullable
   public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
      return (T)(capability == CapabilityControllableSpawner.CAPABILITY_SPAWNER ? CapabilityControllableSpawner.CAPABILITY_SPAWNER.cast(this.handler) : super.getCapability(capability, facing));
   }

   public void readFromNBT(NBTTagCompound compound) {
      super.readFromNBT(compound);
      CapabilityControllableSpawner.CAPABILITY_SPAWNER.readNBT(this.handler, (EnumFacing)null, compound.getCompoundTag("spawnerCap"));
   }

   @Nonnull
   public NBTTagCompound writeToNBT(NBTTagCompound compound) {
      super.writeToNBT(compound);
      compound.setTag("spawnerCap", CapabilityControllableSpawner.CAPABILITY_SPAWNER.writeNBT(this.handler, (EnumFacing)null));
      return compound;
   }

   public class ControlledSpawnerHandler extends CapabilityControllableSpawner.DefaultControllableSpawner {
      public ControlledSpawnerHandler() {
         super(TileEntityControlledSpawner.this);
      }

      @Nonnull
      public SpawnerConfig getConfig() {
         return TileEntityControlledSpawner.this.getBlockType() instanceof BlockControlledSpawner ? ((BlockControlledSpawner)TileEntityControlledSpawner.this.getBlockType()).getConfig() : MSCConfig.vanillaSpawnerConfig;
      }
   }
}
