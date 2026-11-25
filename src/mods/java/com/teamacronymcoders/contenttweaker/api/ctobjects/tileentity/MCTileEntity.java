package com.teamacronymcoders.contenttweaker.api.ctobjects.tileentity;

import com.teamacronymcoders.contenttweaker.api.ctobjects.blockpos.IBlockPos;
import com.teamacronymcoders.contenttweaker.api.ctobjects.blockpos.MCBlockPos;
import com.teamacronymcoders.contenttweaker.api.ctobjects.world.IWorld;
import com.teamacronymcoders.contenttweaker.api.ctobjects.world.MCWorld;
import crafttweaker.api.data.IData;
import net.minecraft.tileentity.TileEntity;

public class MCTileEntity implements IMCTileEntity {
   private final TileEntity tileEntity;

   public MCTileEntity(TileEntity tileEntity) {
      this.tileEntity = tileEntity;
   }

   public IWorld getWorld() {
      return new MCWorld(this.tileEntity.getWorld());
   }

   public IBlockPos getBlockPos() {
      return new MCBlockPos(this.tileEntity.getPos());
   }

   public IData getTileData() {
      return this.tileEntity instanceof IAccessibleData ? ((IAccessibleData)this.tileEntity).getAccessibleData() : null;
   }

   public TileEntity getInternal() {
      return this.tileEntity;
   }
}
