package com.endertech.minecraft.forge.units;

import com.endertech.minecraft.forge.ForgeMod;
import com.endertech.minecraft.forge.api.IEmitter;
import com.endertech.minecraft.forge.blocks.BlockState;
import com.endertech.minecraft.forge.configs.IForgeEnum;
import com.endertech.minecraft.forge.world.ForgeWorld;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.annotation.Nullable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;

public abstract class EmittersInit<T extends IEmitter, E extends Enum<?> & IForgeEnum> extends RelatedUnitsInit<T, E> {
   private final Map<ResourceLocation, T> entities = new ConcurrentHashMap();

   public EmittersInit(ForgeMod mod, Class<E> builtIn) {
      super(mod, builtIn);
   }

   protected void clearAttachedStates() {
      this.entities.clear();
      super.clearAttachedStates();
   }

   protected void attachToStates() {
      this.clearAttachedStates();

      for(T emitter : this.getAll()) {
         if (emitter.isEntity()) {
            UnitId id = emitter.getRelatedId();
            if (Loader.isModLoaded(id.getModId())) {
               this.entities.put(id.toResLoc(), emitter);
            }
         } else {
            this.attachToBlockStates(emitter);
            this.attachToItemStates(emitter);
         }
      }

   }

   @Nullable
   public T findBy(Entity entity) {
      ResourceLocation key = EntityList.getKey(entity);
      return (T)(key != null ? (IEmitter)this.entities.get(key) : null);
   }

   @Nullable
   public T findBy(World world, BlockPos pos) {
      IBlockState state = world.getBlockState(pos);
      T emitter = this.findBy(state);
      if (emitter == null) {
         return (T)null;
      } else {
         if (emitter.useDroppedItemMeta()) {
            BlockState droppedState = ForgeWorld.getBlockDroppedState(world, pos);
            T actualEmitter = this.findBy(droppedState);
            if (actualEmitter == null || actualEmitter == emitter) {
               return (T)null;
            }

            emitter = actualEmitter;
         }

         TileEntity tile = world.getTileEntity(pos);
         if (emitter.isIdentified(tile)) {
            return emitter;
         } else {
            return (T)null;
         }
      }
   }

   public void onPostInit(FMLPostInitializationEvent event) {
      super.onPostInit(event);

      for(T unit : this.getAll()) {
         unit.onPostInit(event);

         for(BlockState state : unit.getRelatedId().getAllBlockStates()) {
            if (state.exists() && !state.isAir()) {
               state.getBlock().setTickRandomly(true);
            }
         }
      }

   }
}
