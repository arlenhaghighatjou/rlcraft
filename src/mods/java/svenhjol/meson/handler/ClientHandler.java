package svenhjol.meson.handler;

import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ClientHandler {
   private static Map<String, BlockPos> structures = new HashMap();

   public ClientHandler() {
   }

   public static World getWorldClient() {
      return Minecraft.getMinecraft().world;
   }

   public static void setNearestStronghold(@Nullable BlockPos structure) {
      structures.put("Stronghold", structure);
   }

   public static void setNearestEndCity(@Nullable BlockPos structure) {
      structures.put("EndCity", structure);
   }

   public static void setNearestStructure(String name, @Nullable BlockPos structure) {
      structures.put(name, structure);
   }

   @Nullable
   public static BlockPos getNearestStronghold() {
      return (BlockPos)structures.get("Stronghold");
   }

   @Nullable
   public static BlockPos getNearestEndCity() {
      return (BlockPos)structures.get("EndCity");
   }
}
