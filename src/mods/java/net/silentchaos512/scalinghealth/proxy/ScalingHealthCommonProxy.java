package net.silentchaos512.scalinghealth.proxy;

import java.util.function.Consumer;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.potion.Potion;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.silentchaos512.lib.proxy.IProxy;
import net.silentchaos512.lib.registry.SRegistry;
import net.silentchaos512.lib.util.Color;
import net.silentchaos512.scalinghealth.config.Config;
import net.silentchaos512.scalinghealth.event.BlightHandler;
import net.silentchaos512.scalinghealth.event.DamageScaling;
import net.silentchaos512.scalinghealth.event.DifficultyHandler;
import net.silentchaos512.scalinghealth.event.PetEventHandler;
import net.silentchaos512.scalinghealth.event.ScalingHealthCommonEvents;
import net.silentchaos512.scalinghealth.init.ModBlocks;
import net.silentchaos512.scalinghealth.init.ModEntities;
import net.silentchaos512.scalinghealth.init.ModItems;
import net.silentchaos512.scalinghealth.init.ModPotions;
import net.silentchaos512.scalinghealth.init.ModSounds;
import net.silentchaos512.scalinghealth.lib.EnumModParticles;
import net.silentchaos512.scalinghealth.loot.ModLootStuff;
import net.silentchaos512.scalinghealth.network.NetworkHandler;
import net.silentchaos512.scalinghealth.network.message.MessagePlaySound;
import net.silentchaos512.scalinghealth.utils.SHPlayerDataHandler;
import net.silentchaos512.scalinghealth.world.SHWorldGenerator;

public class ScalingHealthCommonProxy implements IProxy {
   public ScalingHealthCommonProxy() {
   }

   public void preInit(SRegistry registry, FMLPreInitializationEvent event) {
      Config.INSTANCE.init(event.getSuggestedConfigurationFile());
      registry.addRegistrationHandler(ModPotions::registerAll, Potion.class);
      registry.addRegistrationHandler(ModBlocks::registerAll, Block.class);
      registry.addRegistrationHandler(ModItems::registerAll, Item.class);
      registry.addRegistrationHandler(ModSounds::registerAll, SoundEvent.class);
      ModLootStuff.registerAll(registry);
      GameRegistry.registerWorldGenerator(new SHWorldGenerator(true), 0);
      ModEntities.init(registry);
      DifficultyHandler.INSTANCE.initDefaultEquipment();
      NetworkHandler.init();
      (new ScalingHealthCommonEvents()).register(MinecraftForge.EVENT_BUS);
      (new SHPlayerDataHandler.EventHandler()).register(MinecraftForge.EVENT_BUS);
      DifficultyHandler.INSTANCE.register(MinecraftForge.EVENT_BUS);
      BlightHandler.INSTANCE.register(MinecraftForge.EVENT_BUS);
      PetEventHandler.INSTANCE.register(MinecraftForge.EVENT_BUS);
      DamageScaling.INSTANCE.register(MinecraftForge.EVENT_BUS);

   }

   public void init(SRegistry registry, FMLInitializationEvent event) {
      DifficultyHandler.INSTANCE.initPotionMap();
      Config.INSTANCE.save();
   }

   public void postInit(SRegistry registry, FMLPostInitializationEvent event) {
   }

   public void spawnParticles(EnumModParticles type, Color color, World world, double x, double y, double z, double motionX, double motionY, double motionZ) {
   }

   public void playSoundOnClient(EntityPlayer player, SoundEvent sound, float volume, float pitch) {
      if (player instanceof EntityPlayerMP) {
         NetworkHandler.INSTANCE.sendTo(new MessagePlaySound(sound, volume, pitch), (EntityPlayerMP)player);
      }

   }

   public EntityPlayer getClientPlayer() {
      return null;
   }

   public int getParticleSettings() {
      return 0;
   }
}
