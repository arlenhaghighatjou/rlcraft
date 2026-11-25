package com.lothrazar.playerbosses;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.event.FMLFingerprintViolationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.registries.IForgeRegistryEntry;
import org.apache.logging.log4j.Logger;

@Mod(
   modid = "playerbosses",
   certificateFingerprint = "1bc8f8dbe770187a854cef35dad0ff40ba441bbe",
   acceptedMinecraftVersions = "[1.12.2]"
)
public class ModBosses {
   public static final String MODID = "playerbosses";
   private static Logger logger;
   @Instance("playerbosses")
   public static ModBosses instance;
   private List<SoundEvent> sounds = new ArrayList();

   public ModBosses() {
   }

   @EventHandler
   public void preInit(FMLPreInitializationEvent event) {
      logger = event.getModLog();
      ConfigManager.init(event.getSuggestedConfigurationFile());
      MinecraftForge.EVENT_BUS.register(this);
      MinecraftForge.EVENT_BUS.register(new ConfigManager());
      int id = 0;
      String name = "player_boss";
      EntityRegistry.registerModEntity(new ResourceLocation("playerbosses", name), EntityPlayerBoss.class, name, id++, instance, 64, 1, true);
      EntityPlayerBoss.SOUND_HURT = this.registerSound("boss.hurt");
      EntityPlayerBoss.SOUND_AMB = this.registerSound("boss.ambient");
      EntityPlayerBoss.SOUND_DEATH = this.registerSound("boss.death");
   }

   public SoundEvent registerSound(String name) {
      ResourceLocation res = new ResourceLocation("playerbosses", name);
      SoundEvent sound = new SoundEvent(res);
      sound.setRegistryName(res);
      this.sounds.add(sound);
      return sound;
   }

   @SubscribeEvent
   public void onRegisterSoundEvent(Register<SoundEvent> event) {
      event.getRegistry().registerAll((IForgeRegistryEntry[])this.sounds.toArray(new SoundEvent[0]));
   }

   @SideOnly(Side.CLIENT)
   @SubscribeEvent
   public void registerRendering(ModelRegistryEvent event) {
      RenderingRegistry.registerEntityRenderingHandler(EntityPlayerBoss.class, new EntityPlayerBoss.Factory());
   }

   @EventHandler
   public void onFingerprintViolation(FMLFingerprintViolationEvent event) {
      String source = event.getSource() == null ? "" : event.getSource().getName() + " ";
      String msg = "playerbosses: Invalid fingerprint detected! The file " + source + "may have been tampered with. This version will NOT be supported by the author!";
      if (logger == null) {
         System.out.println(msg);
      } else {
         logger.error(msg);
      }

   }
}
