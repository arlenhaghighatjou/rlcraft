package com.teamacronymcoders.contenttweaker.modules.materials.materialpartdata;

import com.teamacronymcoders.base.materialsystem.parttype.PartDataPiece;

public class CTPartDataPiece implements IPartDataPiece {
   private PartDataPiece partDataPiece;

   public CTPartDataPiece(PartDataPiece partDataPiece) {
      this.partDataPiece = partDataPiece;
   }

   public String getName() {
      return this.partDataPiece.getName();
   }

   public boolean isRequired() {
      return this.partDataPiece.isRequired();
   }

   public PartDataPiece getInternal() {
      return this.partDataPiece;
   }
}
