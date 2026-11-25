package com.teamacronymcoders.contenttweaker.modules.materials.parts;

import com.teamacronymcoders.base.materialsystem.parts.Part;
import com.teamacronymcoders.contenttweaker.modules.materials.materialpartdata.CTPartDataPiece;
import com.teamacronymcoders.contenttweaker.modules.materials.materialpartdata.IPartDataPiece;
import com.teamacronymcoders.contenttweaker.modules.materials.materialparts.IMaterialPart;
import com.teamacronymcoders.contenttweaker.modules.materials.materials.IMaterial;
import com.teamacronymcoders.contenttweaker.modules.materials.parttypes.CTPartType;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class CTPart implements IPart {
   private Part part;

   public CTPart(Part part) {
      this.part = part;
   }

   public String getName() {
      return this.part.getName();
   }

   public String getUnlocalizedName() {
      return this.part.getUnlocalizedName();
   }

   public String getShortUnlocalizedName() {
      return this.part.getShortUnlocalizedName();
   }

   public CTPartType getPartType() {
      return new CTPartType(this.part.getPartType());
   }

   public String getPartTypeName() {
      return this.part.getPartTypeName();
   }

   public String getOreDictPrefix() {
      return this.part.getOreDictPrefix();
   }

   public boolean hasOverlay() {
      return this.part.hasOverlayTexture();
   }

   public List<IPartDataPiece> getData() {
      return (List)this.part.getPartType().getData().stream().map(CTPartDataPiece::new).collect(Collectors.toList());
   }

   public IMaterialPart registerToMaterial(IMaterial material) {
      return material.registerPart(this);
   }

   public List<IMaterialPart> registerToMaterials(IMaterial[] materials) {
      return (List)Arrays.stream(materials).map((material) -> {
         return material.registerPart(this);
      }).collect(Collectors.toList());
   }
}
