package com.lycanitesmobs.core;

import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.ObjectManager;
import com.lycanitesmobs.core.info.CreatureManager;
import com.lycanitesmobs.core.info.ModInfo;
import com.lycanitesmobs.core.item.equipment.EquipmentPartManager;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.EntityEntry;

public abstract class Submod {
   public ModInfo group;

   public Submod() {
   }

   public void preInit(FMLPreInitializationEvent event) {
      this.initialSetup();
      ObjectManager.setCurrentModInfo(this.group);
      this.createBlocks();
      this.createItems();
      this.createEntities();

      try {
         CreatureManager.getInstance().loadCreaturesFromJSON(this.group);
      } catch (Exception var4) {
         LycanitesMobs.logWarning("", "No Creatures loaded for: " + this.group.name);
      }

      try {
         EquipmentPartManager.getInstance().loadAllFromJSON(this.group);
      } catch (Exception var3) {
         LycanitesMobs.logWarning("", "No Equipment Parts loaded for: " + this.group.name);
      }

      this.registerModels();
   }

   public void init(FMLInitializationEvent event) {
      ObjectManager.setCurrentModInfo(this.group);
      this.registerOres();
      this.addRecipes();
   }

   public void postInit(FMLPostInitializationEvent event) {
      ObjectManager.setCurrentModInfo(this.group);
      if (CreatureManager.getInstance().spawnConfig.controlVanillaSpawns) {
         this.editVanillaSpawns();
      }

   }

   public abstract void initialSetup();

   public abstract void createItems();

   public abstract void createBlocks();

   public abstract void createEntities();

   public void registerEntities(Register<EntityEntry> event) {
   }

   public abstract void registerModels();

   public abstract void registerOres();

   public abstract void addRecipes();

   public abstract void editVanillaSpawns();
}
