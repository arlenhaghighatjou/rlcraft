package ladysnake.spawnercontrol;

import javax.annotation.Nullable;
import ladysnake.spawnercontrol.config.MSCConfig;
import ladysnake.spawnercontrol.config.SpawnerConfig;
import ladysnake.spawnercontrol.controlledspawner.CapabilityControllableSpawner;
import ladysnake.spawnercontrol.controlledspawner.IControllableSpawner;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagLong;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;

public class SpawnerUtil {
   public SpawnerUtil() {
   }

   @Nullable
   public static SpawnerConfig getConfig(@Nullable NBTBase nbt) {
      long spawnerPos;
      World world;
      if (nbt instanceof NBTTagCompound && ((NBTTagCompound)nbt).hasKey("pos")) {
         spawnerPos = ((NBTTagCompound)nbt).getLong("pos");
         world = FMLCommonHandler.instance().getMinecraftServerInstance().getWorld(((NBTTagCompound)nbt).getInteger("dimension"));
      } else {
         if (!(nbt instanceof NBTTagLong)) {
            return null;
         }

         spawnerPos = ((NBTTagLong)nbt).getLong();
         world = FMLCommonHandler.instance().getMinecraftServerInstance().getWorld(0);
      }

      return getConfig(world, BlockPos.fromLong(spawnerPos));
   }

   @Nullable
   public static SpawnerConfig getConfig(World world, BlockPos spawnerPos) {
      TileEntity spawnerTE = world.getTileEntity(spawnerPos);
      if (!(spawnerTE instanceof TileEntityMobSpawner)) {
         return null;
      } else if (MSCConfig.customSpawners.length == 0) {
         return MSCConfig.alterVanillaSpawner ? MSCConfig.vanillaSpawnerConfig : null;
      } else {
         SpawnerConfig ret = CapabilityControllableSpawner.getHandler((TileEntityMobSpawner)spawnerTE).getConfig();
         return ret == MSCConfig.vanillaSpawnerConfig && !MSCConfig.alterVanillaSpawner ? null : ret;
      }
   }

   @Nullable
   public static IControllableSpawner getHandlerIfAffected(World world, BlockPos spawnerPos) {
      TileEntity spawnerTE = world.getTileEntity(spawnerPos);
      if (!(spawnerTE instanceof TileEntityMobSpawner)) {
         return null;
      } else {
         IControllableSpawner ret = CapabilityControllableSpawner.getHandler((TileEntityMobSpawner)spawnerTE);
         return ret.getConfig() == MSCConfig.vanillaSpawnerConfig && !MSCConfig.alterVanillaSpawner ? null : ret;
      }
   }
}
