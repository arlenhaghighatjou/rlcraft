package com.endertech.minecraft.forge.units;

import com.endertech.minecraft.forge.ForgeMod;
import com.endertech.minecraft.forge.blocks.BlockState;
import com.endertech.minecraft.forge.configs.IForgeEnum;
import com.endertech.minecraft.forge.regs.IPostInit;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import org.apache.logging.log4j.Logger;

public abstract class RelatedUnitsInit<T extends IRelatedUnit, E extends Enum<?> & IForgeEnum> extends UnitsInitWithEnum<T, E> implements IPostInit {
   protected final Map<BlockState, T> blockStates = new StateHashMap<BlockState, T>();
   protected final Map<ItemState, T> itemStates = new StateHashMap<ItemState, T>();

   public RelatedUnitsInit(ForgeMod mod, Class<E> builtIn) {
      super(mod, builtIn);
      this.getMod().getRegistrator().addPostInit(this);
   }

   public void onPostInit(FMLPostInitializationEvent event) {
      this.attachToStates();
   }

   protected void clearAttachedStates() {
      this.blockStates.clear();
      this.itemStates.clear();
   }

   protected void attachToStates() {
      this.clearAttachedStates();

      for(T unit : this.getAll()) {
         this.attachToBlockStates(unit);
         this.attachToItemStates(unit);
      }

   }

   protected void attachToBlockStates(T unit) {
      Logger logger = this.getMod().getLogger();
      UnitId id = unit.getRelatedId();
      List<BlockState> statesList = id.getAllBlockStates();
      if (!statesList.isEmpty()) {
         for(BlockState state : statesList) {
            if (this.findBy(state) != null) {
               logger.warn("ALREADY REGISTERED: <{}> as {}", id, state);
            } else {
               this.blockStates.put(state, unit);
            }
         }
      }

   }

   protected void attachToItemStates(T unit) {
      Logger logger = this.getMod().getLogger();
      UnitId id = unit.getRelatedId();
      List<ItemState> statesList = id.getAllItemStates();
      if (!statesList.isEmpty()) {
         for(ItemState state : statesList) {
            if (this.findBy(state) != null) {
               logger.warn("ALREADY REGISTERED: <{}> as {}", id, state);
            } else {
               this.itemStates.put(state, unit);
            }
         }
      }

   }

   @Nullable
   public T findBy(ItemStack stack) {
      return (T)this.findBy(ItemState.from(stack));
   }

   @Nullable
   public T findBy(ItemState state) {
      return (T)(this.itemStates.get(state));
   }

   @Nullable
   public T findBy(BlockState state) {
      return (T)(this.blockStates.get(state));
   }

   @Nullable
   public T findBy(IBlockState state) {
      return (T)this.findBy(BlockState.from(state));
   }
}
