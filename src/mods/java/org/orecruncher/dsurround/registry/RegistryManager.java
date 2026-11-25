package org.orecruncher.dsurround.registry;

import java.util.function.Consumer;
import javax.annotation.Nonnull;
import net.minecraftforge.fml.client.event.ConfigChangedEvent.OnConfigChangedEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.orecruncher.dsurround.ModBase;
import org.orecruncher.dsurround.registry.acoustics.AcousticRegistry;
import org.orecruncher.dsurround.registry.biome.BiomeRegistry;
import org.orecruncher.dsurround.registry.blockstate.BlockStateRegistry;
import org.orecruncher.dsurround.registry.config.ConfigData;
import org.orecruncher.dsurround.registry.dimension.DimensionRegistry;
import org.orecruncher.dsurround.registry.effect.EffectRegistry;
import org.orecruncher.dsurround.registry.footstep.FootstepsRegistry;
import org.orecruncher.dsurround.registry.item.ItemRegistry;
import org.orecruncher.dsurround.registry.sound.SoundRegistry;
import org.orecruncher.lib.Singleton;
import org.orecruncher.lib.collections.ObjectArray;
import org.orecruncher.lib.task.Scheduler;

@EventBusSubscriber(
   modid = "dsurround"
)
public final class RegistryManager {
   @SideOnly(Side.CLIENT)
   public static SoundRegistry SOUND;
   @SideOnly(Side.CLIENT)
   public static AcousticRegistry ACOUSTICS;
   @SideOnly(Side.CLIENT)
   public static BiomeRegistry BIOME;
   @SideOnly(Side.CLIENT)
   public static BlockStateRegistry BLOCK;
   @SideOnly(Side.CLIENT)
   public static ItemRegistry ITEMS;
   @SideOnly(Side.CLIENT)
   public static FootstepsRegistry FOOTSTEPS;
   @SideOnly(Side.CLIENT)
   public static EffectRegistry EFFECTS;
   public static DimensionRegistry DIMENSION;
   public static final Singleton<ConfigData> DATA = new Singleton<ConfigData>() {
      @Nonnull
      protected ConfigData initialValue() {
         return ConfigData.load();
      }
   };
   static final ObjectArray<Registry> REGISTRIES = new ObjectArray(8);

   public RegistryManager() {
   }

   private static boolean handleReload() {
      return ModBase.proxy().isRunningAsServer() || ModBase.proxy().effectiveSide() == Side.CLIENT;
   }

   public static void initialize() {
      if (!ModBase.proxy().isRunningAsServer()) {
         SOUND = new SoundRegistry();
         ACOUSTICS = new AcousticRegistry();
         BIOME = new BiomeRegistry();
         BLOCK = new BlockStateRegistry();
         FOOTSTEPS = new FootstepsRegistry();
         ITEMS = new ItemRegistry();
         EFFECTS = new EffectRegistry();
      }

      DIMENSION = new DimensionRegistry();
      load();
   }

   public static void doReload() {
      if (ModBase.proxy().isRunningAsServer()) {
         load();
      } else {
         Scheduler.schedule(Side.CLIENT, RegistryManager::load);
      }

   }

   @SubscribeEvent
   public static void onReload(@Nonnull OnConfigChangedEvent event) {
      if (event.getModID().equals("dsurround") && handleReload()) {
         load();
      }

   }

   private static void load() {
      DATA.clear();
      REGISTRIES.forEach((r) -> {
         r.initialize((ConfigData)DATA.get());
      });
      REGISTRIES.forEach(Registry::complete);
   }
}
