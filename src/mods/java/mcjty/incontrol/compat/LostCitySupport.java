package mcjty.incontrol.compat;

import java.util.function.Function;
import javax.annotation.Nullable;
import mcjty.lostcities.api.ILostChunkGenerator;
import mcjty.lostcities.api.ILostChunkInfo;
import mcjty.lostcities.api.ILostCities;
import mcjty.tools.rules.IEventQuery;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.event.FMLInterModComms;

public class LostCitySupport {
   private static ILostCities lostCities;

   public LostCitySupport() {
   }

   public static void register() {
      FMLInterModComms.sendFunctionMessage("lostcities", "getLostCities", "mcjty.incontrol.compat.LostCitySupport$GetLostCities");
   }

   public static <T> boolean isCity(IEventQuery<T> query, T event) {
      World world = query.getWorld(event);
      if (world.isRemote) {
         return false;
      } else {
         ILostChunkGenerator generator = lostCities.getLostGenerator(world.provider.getDimension());
         if (generator != null) {
            BlockPos pos = query.getPos(event);
            ILostChunkInfo chunkInfo = generator.getChunkInfo(pos.getX() >> 4, pos.getZ() >> 4);
            return chunkInfo.isCity();
         } else {
            return false;
         }
      }
   }

   public static <T> boolean isStreet(IEventQuery<T> query, T event) {
      World world = query.getWorld(event);
      if (world.isRemote) {
         return false;
      } else {
         ILostChunkGenerator generator = lostCities.getLostGenerator(world.provider.getDimension());
         if (generator == null) {
            return false;
         } else {
            BlockPos pos = query.getPos(event);
            ILostChunkInfo chunkInfo = generator.getChunkInfo(pos.getX() >> 4, pos.getZ() >> 4);
            return chunkInfo.isCity() && chunkInfo.getBuildingType() == null;
         }
      }
   }

   public static <T> boolean inSphere(IEventQuery<T> query, T event) {
      World world = query.getWorld(event);
      if (world.isRemote) {
         return false;
      } else {
         ILostChunkGenerator generator = lostCities.getLostGenerator(world.provider.getDimension());
         if (generator != null) {
            BlockPos pos = query.getPos(event);
            ILostChunkInfo chunkInfo = generator.getChunkInfo(pos.getX() >> 4, pos.getZ() >> 4);
            return chunkInfo.getSphere() != null;
         } else {
            return false;
         }
      }
   }

   public static <T> boolean isBuilding(IEventQuery<T> query, T event) {
      World world = query.getWorld(event);
      if (world.isRemote) {
         return false;
      } else {
         ILostChunkGenerator generator = lostCities.getLostGenerator(world.provider.getDimension());
         if (generator == null) {
            return false;
         } else {
            BlockPos pos = query.getPos(event);
            ILostChunkInfo chunkInfo = generator.getChunkInfo(pos.getX() >> 4, pos.getZ() >> 4);
            return chunkInfo.isCity() && chunkInfo.getBuildingType() != null;
         }
      }
   }

   public static class GetLostCities implements Function<ILostCities, Void> {
      public GetLostCities() {
      }

      @Nullable
      public Void apply(ILostCities lc) {
         LostCitySupport.lostCities = lc;
         return null;
      }
   }
}
