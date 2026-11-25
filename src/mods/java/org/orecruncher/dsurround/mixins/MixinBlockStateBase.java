package org.orecruncher.dsurround.mixins;

import net.minecraft.block.state.BlockStateBase;
import org.orecruncher.dsurround.registry.IDataAccessor;
import org.orecruncher.dsurround.registry.blockstate.BlockStateData;
import org.spongepowered.asm.mixin.Mixin;

@Mixin({BlockStateBase.class})
public abstract class MixinBlockStateBase implements IDataAccessor<BlockStateData> {
   private BlockStateData dsurround_blockstate_info = null;

   public MixinBlockStateBase() {
   }

   public BlockStateData getData() {
      return this.dsurround_blockstate_info;
   }

   public void setData(BlockStateData data) {
      this.dsurround_blockstate_info = data;
   }
}
