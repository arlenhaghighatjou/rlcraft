package org.orecruncher.dshuds;

import java.util.function.Function;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.MutableBlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import org.orecruncher.dshuds.compat.SereneSeasonHandler;
import org.orecruncher.lib.MinecraftClock;

@EventBusSubscriber(
   modid = "dshuds"
)
public final class Environment {
   private static final MinecraftClock clock = new MinecraftClock();
   private static final MutableBlockPos playerPosition = new MutableBlockPos();
   private static final Function<World, String> seasonStringHandler;
   private static int tickCount = 0;
   private static String biomeName = "";
   private static String seasonString = "";

   public Environment() {
   }

   public static int getTickCounter() {
      return tickCount;
   }

   @Nullable
   public static World getWorld() {
      return Minecraft.getMinecraft().world;
   }

   @Nullable
   public static EntityPlayer getPlayer() {
      return Minecraft.getMinecraft().player;
   }

   @Nonnull
   public static MinecraftClock getClock() {
      return clock;
   }

   @Nonnull
   public static BlockPos getPlayerPosition() {
      return playerPosition;
   }

   @Nonnull
   public static String getBiomeName() {
      return biomeName;
   }

   @Nonnull
   public static String getSeasonString() {
      return seasonString;
   }

   @SubscribeEvent
   public static void clientTick(@Nonnull ClientTickEvent event) {
      if (event.phase == Phase.START && getPlayer() != null && getWorld() != null) {
         ++tickCount;
         clock.update(getWorld());
         playerPosition.setPos(getPlayer());
         biomeName = getWorld().getBiome(playerPosition).getBiomeName();
         seasonString = (String)seasonStringHandler.apply(getWorld());
      }

   }

   static {
      if (Loader.isModLoaded("sereneseasons")) {
         seasonStringHandler = new SereneSeasonHandler();
      } else {
         seasonStringHandler = (world) -> {
            return "";
         };
      }

   }
}
