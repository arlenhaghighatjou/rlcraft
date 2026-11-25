package org.jwaresoftware.mcmods.armorunder.runtime;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.jwaresoftware.mcmods.lib.IniRegistries;

final class ModRegisterObjectsListener {
   private ModRuntimeSkeleton _owner;

   ModRegisterObjectsListener(ModRuntimeSkeleton owner) {
      this._owner = owner;
   }

   @SubscribeEvent
   public void doBlocksRegister(Register<Block> event) {
      if (this._owner != null) {
         IniRegistries.autoregisterBlocks("armorunder", event.getRegistry());
      }

   }

   @SubscribeEvent
   public void doItemsRegister(Register<Item> event) {
      if (this._owner != null) {
         IniRegistries.autoregisterItems("armorunder", event.getRegistry());
         this._owner.afterItemsRegistered();
      }

   }

   @SubscribeEvent
   public void doInventoryRenderModelsRegister(ModelRegistryEvent event) {
      if (this._owner != null) {
         this._owner.registerRenderModels();
      }

   }

   @SubscribeEvent
   public void doRecipesRegister(Register<IRecipe> event) {
      if (this._owner != null) {
         this._owner.registerModRecipes();
         this._owner = null;
      }

   }
}
