package net.silentchaos512.scalinghealth.proxy;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.silentchaos512.lib.registry.SRegistry;
import net.silentchaos512.lib.util.Color;
import net.silentchaos512.scalinghealth.client.DifficultyDisplayHandler;
import net.silentchaos512.scalinghealth.client.HeartDisplayHandler;
import net.silentchaos512.scalinghealth.client.key.KeyTrackerSH;
import net.silentchaos512.scalinghealth.client.render.particle.ParticleSH;
import net.silentchaos512.scalinghealth.init.ModEntities;
import net.silentchaos512.scalinghealth.lib.EnumModParticles;
import org.apache.commons.lang3.NotImplementedException;

public class ScalingHealthClientProxy extends ScalingHealthCommonProxy {
   public ScalingHealthClientProxy() {
   }

   public void preInit(SRegistry registry, FMLPreInitializationEvent event) {
      super.preInit(registry, event);
      HeartDisplayHandler.INSTANCE.register(MinecraftForge.EVENT_BUS);
      DifficultyDisplayHandler.INSTANCE.register(MinecraftForge.EVENT_BUS);
      KeyTrackerSH.INSTANCE.register(MinecraftForge.EVENT_BUS);
      registry.clientPreInit(event);
      ModEntities.registerRenderers(registry);
   }

   public void init(SRegistry registry, FMLInitializationEvent event) {
      super.init(registry, event);
      registry.clientInit(event);
   }

   public void postInit(SRegistry registry, FMLPostInitializationEvent event) {
      super.postInit(registry, event);
      registry.clientPostInit(event);
   }

   public void spawnParticles(EnumModParticles type, Color color, World world, double x, double y, double z, double motionX, double motionY, double motionZ) {
      Particle fx = null;
      float r = color.getRed();
      float g = color.getGreen();
      float b = color.getBlue();
      switch(type) {
      case CURSED_HEART:
      case ENCHANTED_HEART:
      case HEART_CONTAINER:
         Particle var20 = new ParticleSH(world, x, y, z, motionX, motionY, motionZ, 1.0F, 10, r, g, b);
         Minecraft.getMinecraft().effectRenderer.addEffect(var20);
         return;
      default:
         throw new NotImplementedException("Unknown particle type: " + type);
      }
   }

   public EntityPlayer getClientPlayer() {
      return Minecraft.getMinecraft().player;
   }

   public int getParticleSettings() {
      return Minecraft.getMinecraft().gameSettings.particleSetting;
   }
}
