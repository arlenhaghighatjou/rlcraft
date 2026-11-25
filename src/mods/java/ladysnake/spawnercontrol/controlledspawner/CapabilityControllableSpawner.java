package ladysnake.spawnercontrol.controlledspawner;

import net.minecraftforge.common.capabilities.CapabilityManager;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import ladysnake.spawnercontrol.config.MSCConfig;
import ladysnake.spawnercontrol.config.SpawnerConfig;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.MobSpawnerBaseLogic;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.capabilities.Capability.IStorage;

public class CapabilityControllableSpawner {
   public static Capability<IControllableSpawner> CAPABILITY_SPAWNER;
   public static final ResourceLocation CAPABILITY_KEY = new ResourceLocation("spawnercontrol", "controllable_spawner_cap");

   public CapabilityControllableSpawner() {
   }

   public static IControllableSpawner getHandler(TileEntityMobSpawner entity) {
      return (IControllableSpawner)Objects.requireNonNull(entity.getCapability(CAPABILITY_SPAWNER, (EnumFacing)null));
   }

   public static class DefaultControllableSpawner implements IControllableSpawner {
      private final TileEntityMobSpawner spawner;
      private int spawnedMobsCount;

      public DefaultControllableSpawner() {
         this((TileEntityMobSpawner)null);
      }

      DefaultControllableSpawner(TileEntityMobSpawner spawner) {
         this.spawner = spawner;
      }

      public void setSpawnedMobsCount(int mobCount) {
         this.spawnedMobsCount = mobCount;
      }

      public boolean incrementSpawnedMobsCount() {
         SpawnerConfig cfg = this.getConfig();
         if (++this.spawnedMobsCount >= cfg.mobThreshold) {
            if (cfg.breakSpawner) {
               this.spawner.getWorld().setBlockToAir(this.spawner.getPos());
            }

            return true;
         } else {
            this.adjustDelayAfterSpawn(this.spawner.getSpawnerBaseLogic(), (double)cfg.spawnRateModifier);
            return false;
         }
      }

      protected void adjustDelayAfterSpawn(MobSpawnerBaseLogic spawnerBaseLogic, double spawnRateModifier) {
         spawnerBaseLogic.minSpawnDelay = (int)((double)spawnerBaseLogic.minSpawnDelay * spawnRateModifier);
         spawnerBaseLogic.maxSpawnDelay = (int)((double)spawnerBaseLogic.maxSpawnDelay * spawnRateModifier);
      }

      public int getSpawnedMobsCount() {
         return this.spawnedMobsCount;
      }

      public boolean canSpawn() {
         return this.spawnedMobsCount < this.getConfig().mobThreshold;
      }

      @Nonnull
      public SpawnerConfig getConfig() {
         return MSCConfig.vanillaSpawnerConfig;
      }
   }

   public static class Provider implements ICapabilitySerializable<NBTTagCompound> {
      final IControllableSpawner instance;

      public Provider(TileEntityMobSpawner spawner) {
         this.instance = new CapabilityControllableSpawner.DefaultControllableSpawner(spawner);
      }

      public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
         return capability == CapabilityControllableSpawner.CAPABILITY_SPAWNER;
      }

      @Nullable
      public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
         return (T)(capability == CapabilityControllableSpawner.CAPABILITY_SPAWNER ? CapabilityControllableSpawner.CAPABILITY_SPAWNER.cast(this.instance) : null);
      }

      public NBTTagCompound serializeNBT() {
         return (NBTTagCompound)CapabilityControllableSpawner.CAPABILITY_SPAWNER.getStorage().writeNBT(CapabilityControllableSpawner.CAPABILITY_SPAWNER, this.instance, (EnumFacing)null);
      }

      public void deserializeNBT(NBTTagCompound nbt) {
         CapabilityControllableSpawner.CAPABILITY_SPAWNER.getStorage().readNBT(CapabilityControllableSpawner.CAPABILITY_SPAWNER, this.instance, (EnumFacing)null, nbt);
      }
   }

   public static class Storage implements IStorage<IControllableSpawner> {
      public Storage() {
      }

      @Nullable
      public NBTBase writeNBT(Capability<IControllableSpawner> capability, IControllableSpawner instance, EnumFacing side) {
         NBTTagCompound nbt = new NBTTagCompound();
         nbt.setInteger("SpawnedMobsCount", instance.getSpawnedMobsCount());
         return nbt;
      }

      public void readNBT(Capability<IControllableSpawner> capability, IControllableSpawner instance, EnumFacing side, NBTBase nbt) {
         if (nbt instanceof NBTTagCompound) {
            instance.setSpawnedMobsCount(((NBTTagCompound)nbt).getInteger("SpawnedMobsCount"));
         }

      }
   }

   public static void registerCapabilities() {
      CapabilityManager.INSTANCE.onRegistered(IControllableSpawner.class, capability -> CAPABILITY_SPAWNER = capability);
   }
}
