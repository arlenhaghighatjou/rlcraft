package vazkii.quark.world.feature;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Random;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.DungeonHooks.DungeonMob;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.eventhandler.Event;

public class DungeonTweaksCompat {
   public static boolean isLegacy = false;
   public static boolean isLoaded = false;
   public static final ResourceLocation quark = new ResourceLocation("quark:dungeon");

   public DungeonTweaksCompat() {
   }

   public static void legacyCheck() {
      isLoaded = Loader.isModLoaded("dungeontweaks");
      if (isLoaded) {
         try {
            Class.forName("com.EvilNotch.dungeontweeks.main.Events.EventDungeon$Post");
            isLegacy = true;
         } catch (Throwable var1) {
            ;
         }

      }
   }

   public static void registerDungeons() {
      if (isLoaded && !isLegacy) {
         try {
            Class<?> dungeonMobs = Class.forName("com.evilnotch.dungeontweeks.main.world.worldgen.mobs.DungeonMobs");
            Method cacheForge = dungeonMobs.getMethod("cacheForge");
            cacheForge.invoke((Object)null);
            Method addDungeonMob = dungeonMobs.getMethod("addDungeonMob", ResourceLocation.class, DungeonMob.class);
            Method getDungeonList = dungeonMobs.getMethod("getDungeonList", ResourceLocation.class, Boolean.TYPE);

            for(DungeonMob mob : (List)getDungeonList.invoke((Object)null, new ResourceLocation("dungeon"), true)) {
               addDungeonMob.invoke((Object)null, quark, mob);
            }
         } catch (Exception var7) {
            var7.printStackTrace();
         }

      }
   }

   public static void fireDungeonSpawn(TileEntity tile, World world, Random random) {
      if (isLegacy) {
         try {
            Constructor<? extends Event> constructor = Class.forName("com.EvilNotch.dungeontweeks.main.Events.EventDungeon$Post").getConstructor(TileEntity.class, BlockPos.class, Random.class, ResourceLocation.class, World.class);
            Event event = (Event)constructor.newInstance(tile, tile.getPos(), random, quark, world);
            MinecraftForge.EVENT_BUS.post(event);
         } catch (Throwable var6) {
            var6.printStackTrace();
         }
      } else {
         try {
            Method fireDungeonTweaks = Class.forName("com.evilnotch.dungeontweeks.main.world.worldgen.mobs.DungeonMobs").getMethod("fireDungeonTweaks", ResourceLocation.class, TileEntity.class, Random.class, World.class);
            fireDungeonTweaks.invoke((Object)null, quark, tile, random, world);
         } catch (Throwable var5) {
            var5.printStackTrace();
         }
      }

   }
}
