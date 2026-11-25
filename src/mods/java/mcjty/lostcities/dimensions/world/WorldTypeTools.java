package mcjty.lostcities.dimensions.world;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nullable;
import mcjty.lostcities.config.LostCityConfiguration;
import mcjty.lostcities.config.LostCityProfile;
import mcjty.lostcities.dimensions.ModDimensions;
import mcjty.lostcities.network.PacketHandler;
import mcjty.lostcities.network.PacketRequestProfile;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

public class WorldTypeTools {
   private static Map<Integer, LostCityProfile> profileMap = new HashMap();
   private static Map<Integer, WeakReference<LostCityChunkGenerator>> chunkGeneratorMap = new HashMap();
   private static long clientTimeout = -1L;

   public WorldTypeTools() {
   }

   public static void cleanCache() {
      profileMap.clear();
      clientTimeout = -1L;
   }

   public static void cleanChunkGeneratorMap() {
      chunkGeneratorMap.clear();
   }

   public static void registerChunkGenerator(Integer dimension, LostCityChunkGenerator chunkGenerator) {
      chunkGeneratorMap.put(dimension, new WeakReference(chunkGenerator));
   }

   @Nullable
   public static LostCityChunkGenerator getChunkGenerator(int dimension) {
      if (chunkGeneratorMap.containsKey(Integer.valueOf(dimension))) {
         WeakReference<LostCityChunkGenerator> reference = (WeakReference)chunkGeneratorMap.get(Integer.valueOf(dimension));
         return (LostCityChunkGenerator)reference.get();
      } else {
         return null;
      }
   }

   public static LostCityProfile getProfile(World world) {
      if (profileMap.containsKey(Integer.valueOf(world.provider.getDimension()))) {
         return (LostCityProfile)profileMap.get(Integer.valueOf(world.provider.getDimension()));
      } else if (world instanceof WorldServer) {
         LostCityProfile profile = getProfileOnServer(world);
         profileMap.put(Integer.valueOf(world.provider.getDimension()), profile);
         return profile;
      } else {
         long time = System.currentTimeMillis();
         if (clientTimeout == -1L || clientTimeout + 2000L > time) {
            PacketHandler.INSTANCE.sendToServer(new PacketRequestProfile(world.provider.getDimension()));
            clientTimeout = time;
         }

         return ModDimensions.dimensionProfileMap.keySet().contains(Integer.valueOf(world.provider.getDimension())) ? (LostCityProfile)LostCityConfiguration.profiles.get(ModDimensions.dimensionProfileMap.get(Integer.valueOf(world.provider.getDimension()))) : (LostCityProfile)LostCityConfiguration.profiles.get(LostCityConfiguration.DEFAULT_PROFILE);
      }
   }

   public static void setProfileFromServer(int dimension, String profileName) {
      LostCityProfile profile = (LostCityProfile)LostCityConfiguration.profiles.get(profileName);
      if (profile == null) {
         throw new RuntimeException("Cannot find profile '" + profileName + "' that the server is using! Please make client configs for Lost Cities compatible");
      } else {
         profileMap.put(Integer.valueOf(dimension), profile);
      }
   }

   @Nullable
   public static LostCityChunkGenerator getLostCityChunkGenerator(World world) {
      return getChunkGenerator(world.provider.getDimension());
   }

   public static boolean isLostCities(World world) {
      if (ModDimensions.dimensionProfileMap.containsKey(Integer.valueOf(world.provider.getDimension()))) {
         return true;
      } else if (world.provider.getDimension() != 0) {
         return false;
      } else {
         return world.getWorldType() instanceof LostWorldType || world.getWorldType() instanceof LostWorldTypeBOP;
      }
   }

   private static LostCityProfile getProfileOnServer(World world) {
      if (ModDimensions.dimensionProfileMap.containsKey(Integer.valueOf(world.provider.getDimension()))) {
         LostCityProfile profile = (LostCityProfile)LostCityConfiguration.profiles.get(ModDimensions.dimensionProfileMap.get(Integer.valueOf(world.provider.getDimension())));
         if (profile != null) {
            return profile;
         }
      }

      String generatorOptions = world.getWorldInfo().getGeneratorOptions();
      LostCityProfile p;
      if (generatorOptions != null && !generatorOptions.trim().isEmpty()) {
         JsonParser parser = new JsonParser();
         JsonElement parsed = parser.parse(generatorOptions);
         String profileName;
         if (parsed.getAsJsonObject().has("profile")) {
            profileName = parsed.getAsJsonObject().get("profile").getAsString();
         } else {
            profileName = LostCityConfiguration.DEFAULT_PROFILE;
         }

         p = (LostCityProfile)LostCityConfiguration.profiles.get(profileName);
         if (p == null) {
            throw new RuntimeException("Something went wrong! Profile '" + profileName + "' is missing!");
         }
      } else {
         p = (LostCityProfile)LostCityConfiguration.profiles.get(LostCityConfiguration.DEFAULT_PROFILE);
         if (p == null) {
            throw new RuntimeException("Something went wrong! Profile '" + LostCityConfiguration.DEFAULT_PROFILE + "' is missing!");
         }
      }

      return p;
   }
}
