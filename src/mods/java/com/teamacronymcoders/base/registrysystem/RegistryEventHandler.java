package com.teamacronymcoders.base.registrysystem;

import com.teamacronymcoders.base.IBaseMod;
import com.teamacronymcoders.base.registrysystem.entity.SpawnInfo;
import com.teamacronymcoders.base.registrysystem.entity.UpdateInfo;
import java.util.Collection;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityList.EntityEggInfo;
import net.minecraft.item.Item;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.registries.IForgeRegistry;

public class RegistryEventHandler {
   private IBaseMod mod;
   private int nextAvailableID;

   public RegistryEventHandler(IBaseMod mod) {
      this.mod = mod;
   }

   @SubscribeEvent
   public void registerBlocks(Register<Block> blockRegistryEvent) {
      BlockRegistry blockRegistry = (BlockRegistry)this.mod.getRegistryHolder().getRegistry(BlockRegistry.class, "BLOCK");
      blockRegistry.getEntries().forEach((name, block) -> {
         blockRegistryEvent.getRegistry().register(block.setRegistryName(name));
      });
      blockRegistry.registryEvent();
   }

   @SubscribeEvent
   public void registerItems(Register<Item> itemRegistryEvent) {
      ItemRegistry itemRegistry = (ItemRegistry)this.mod.getRegistryHolder().getRegistry(ItemRegistry.class, "ITEM");
      itemRegistry.getEntries().forEach((name, item) -> {
         itemRegistryEvent.getRegistry().register(item.setRegistryName(name));
      });
      itemRegistry.registryEvent();
   }

   @SubscribeEvent
   public void registerEntities(Register<EntityEntry> entityRegistryEvent) {
      EntityRegistry entityRegistry = (EntityRegistry)this.mod.getRegistryHolder().getRegistry(EntityRegistry.class, "ENTITY");
      entityRegistry.getEntries().forEach((name, entityEntry) -> {
         Class<? extends Entity> entityClass = entityEntry.getEntityClass();
         UpdateInfo updateInfo = entityEntry.getUpdateInfo();
         net.minecraftforge.fml.common.registry.EntityRegistry.registerModEntity(name, entityClass, name.getResourcePath(), this.nextAvailableID++, this.mod, updateInfo.getTrackingRange(), updateInfo.getUpdateFrequency(), updateInfo.isSendVelocityUpdates());
         EntityEggInfo spawnEgg = entityEntry.getEgg();
         if (spawnEgg != null) {
            net.minecraftforge.fml.common.registry.EntityRegistry.registerEgg(name, spawnEgg.primaryColor, spawnEgg.secondaryColor);
         }

         SpawnInfo spawnInfo = entityEntry.getSpawnInfo();
         if (spawnInfo != null && EntityLiving.class.isAssignableFrom(entityClass)) {
            net.minecraftforge.fml.common.registry.EntityRegistry.addSpawn(entityClass, spawnInfo.getWeighted(), spawnInfo.getMinimum(), spawnInfo.getMaximum(), spawnInfo.getCreatureType(), spawnInfo.getSpawnBiomes());
         }

      });
      entityRegistry.registryEvent();
   }

   @SubscribeEvent
   public void registerSoundEvents(Register<SoundEvent> soundEventRegistryEvent) {
      Collection var10000 = ((SoundEventRegistry)this.mod.getRegistryHolder().getRegistry(SoundEventRegistry.class, "SOUND_EVENT")).entries.values();
      IForgeRegistry var10001 = soundEventRegistryEvent.getRegistry();
      var10000.forEach(var10001::register);
   }

   @SubscribeEvent
   public void onModelRun(ModelRegistryEvent event) {
      this.mod.getRegistryHolder().getAllRegistries().forEach((s, registry) -> {
         registry.onModelRun();
      });
   }
}
