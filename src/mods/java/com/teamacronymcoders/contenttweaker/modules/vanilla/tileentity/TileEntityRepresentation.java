package com.teamacronymcoders.contenttweaker.modules.vanilla.tileentity;

import com.teamacronymcoders.contenttweaker.api.IRepresentation;
import stanhebben.zenscript.annotations.ZenProperty;

public class TileEntityRepresentation implements IRepresentation<TileEntityRepresentation> {
   @ZenProperty
   public String name;
   @ZenProperty
   public ITileEntityUpdate clientUpdate = (world, blockPos, tileEntityData) -> {
   };
   @ZenProperty
   public ITileEntityUpdate serverUpdate = (world, blockPos, tileEntityData) -> {
   };

   public TileEntityRepresentation(String name) {
      this.name = name;
   }

   public String getName() {
      return this.name;
   }

   public String getTypeName() {
      return "tile_entity";
   }

   public void register() {
      TileEntityContent.REPRESENTATION_MAP.put(this.name, this);
   }

   public TileEntityRepresentation getInternal() {
      return this;
   }
}
