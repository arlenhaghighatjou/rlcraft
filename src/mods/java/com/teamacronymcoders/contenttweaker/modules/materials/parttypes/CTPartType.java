package com.teamacronymcoders.contenttweaker.modules.materials.parttypes;

import com.teamacronymcoders.base.materialsystem.parttype.PartType;
import com.teamacronymcoders.contenttweaker.modules.materials.materialpartdata.IPartDataPiece;

public class CTPartType implements IPartType {
   private final PartType partType;

   public CTPartType(PartType partType) {
      this.partType = partType;
   }

   public String getName() {
      return this.partType.getName();
   }

   public void setData(IPartDataPiece[] data) {
      throw new UnsupportedOperationException("Can't setData for Existing PartTypes");
   }

   public PartType getInternal() {
      return this.partType;
   }
}
