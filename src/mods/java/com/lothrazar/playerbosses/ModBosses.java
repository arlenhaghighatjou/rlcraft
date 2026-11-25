package com.lothrazar.playerbosses;

import net.minecraftforge.fml.common.eventhandler.EventBus;
import net.minecraftforge.fml.common.event.FMLConstructionEvent;
import net.minecraftforge.fml.common.event.FMLEvent;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.lax1dude.eaglercraft.v1_8.log4j.Logger;

@Mod(
   modid = "playerbosses",
   acceptedMinecraftVersions = "[1.12.2]"
)
public class ModBosses {
   public static final String MODID = "playerbosses";
   private static Logger logger;
   public static ModBosses instance;
   private List<SoundEvent> sounds = new ArrayList();

   public ModBosses() {
   }

   public void handleModStateEvent(FMLEvent event) {
      if (event instanceof FMLConstructionEvent) {
         instance = this;
      } else if (event instanceof FMLPreInitializationEvent) {
         this.preInit((FMLPreInitializationEvent)event);
      }

   }

   public void preInit(FMLPreInitializationEvent event) {
      logger = event.getModLog();
      ConfigManager.init(event.getSuggestedConfigurationFile());
      this.register(MinecraftForge.EVENT_BUS);
      (new ConfigManager()).register(MinecraftForge.EVENT_BUS);
      int id = 0;
      String name = "player_boss";
      EntityRegistry.registerModEntity(new ResourceLocation("playerbosses", name), EntityPlayerBoss.class, name, id++, instance, 64, 1, true, EntityPlayerBoss::new);
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

   public void register(EventBus bus) {
      bus.registerGeneric(Register.class, SoundEvent.class, this::onRegisterSoundEvent);
      bus.register(ModelRegistryEvent.class, this::registerRendering);
   }

   public void onRegisterSoundEvent(Register<SoundEvent> event) {
      event.getRegistry().registerAll(this.sounds.toArray(new SoundEvent[0]));
   }

   @SideOnly(Side.CLIENT)
   public void registerRendering(ModelRegistryEvent event) {
      RenderingRegistry.registerEntityRenderingHandler(EntityPlayerBoss.class, new EntityPlayerBoss.Factory());
   }
}
