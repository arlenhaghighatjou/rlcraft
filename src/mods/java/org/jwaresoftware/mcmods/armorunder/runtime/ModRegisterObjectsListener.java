package org.jwaresoftware.mcmods.armorunder.runtime;

import net.minecraftforge.fml.common.eventhandler.EventBus;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.event.RegistryEvent.Register;
import org.jwaresoftware.mcmods.lib.IniRegistries;

final class ModRegisterObjectsListener {
   private ModRuntimeSkeleton _owner;

   ModRegisterObjectsListener(ModRuntimeSkeleton owner) {
      this._owner = owner;
   }

   public void register(EventBus bus) {
      bus.registerGeneric(Register.class, Block.class, this::doBlocksRegister);
      bus.registerGeneric(Register.class, Item.class, this::doItemsRegister);
      bus.register(ModelRegistryEvent.class, this::doInventoryRenderModelsRegister);
      bus.registerGeneric(Register.class, IRecipe.class, this::doRecipesRegister);
   }

   public void doBlocksRegister(Register<Block> event) {
      if (this._owner != null) {
         IniRegistries.autoregisterBlocks("armorunder", event.getRegistry());
      }

   }

   public void doItemsRegister(Register<Item> event) {
      if (this._owner != null) {
         IniRegistries.autoregisterItems("armorunder", event.getRegistry());
         this._owner.afterItemsRegistered();
      }

   }

   public void doInventoryRenderModelsRegister(ModelRegistryEvent event) {
      if (this._owner != null) {
         this._owner.registerRenderModels();
      }

   }

   public void doRecipesRegister(Register<IRecipe> event) {
      if (this._owner != null) {
         this._owner.registerModRecipes();
         this._owner = null;
      }

   }
}
