package com.teamacronymcoders.base.materialsystem.parttype;

import com.google.common.collect.Lists;
import com.teamacronymcoders.base.materialsystem.MaterialSystem;
import com.teamacronymcoders.base.materialsystem.MaterialUser;
import com.teamacronymcoders.base.materialsystem.blocks.SubBlockOrePart;
import com.teamacronymcoders.base.materialsystem.materialparts.MaterialPart;
import com.teamacronymcoders.base.materialsystem.partdata.MaterialPartData;
import java.util.List;
import javax.annotation.Nonnull;
import net.minecraft.util.ResourceLocation;

public class OrePartType extends BlockPartType {
   public static final String DROP_DATA_NAME = "drops";
   public static final String VARIANT_DATA_NAME = "variants";

   public OrePartType() {
      super("Ore", setupOreData());
   }

   private static List<PartDataPiece> setupOreData() {
      List<PartDataPiece> oreDataPieces = Lists.newArrayList();
      oreDataPieces.add(new PartDataPiece("variants", false));
      oreDataPieces.add(new PartDataPiece("drops", false));
      return oreDataPieces;
   }

   public void setup(@Nonnull MaterialPart materialPart, @Nonnull MaterialUser materialUser) {
      this.createOreSubBlocks(materialPart);
   }

   private void createOreSubBlocks(MaterialPart materialPart) {
      MaterialPartData data = materialPart.getData();
      MaterialUser materialUser = materialPart.getMaterialUser();
      if (data.containsDataPiece("variants")) {
         String[] variantNames = data.getDataPiece("variants").split(",");
         int[] hardness = this.getArrayForField(data, "hardness");
         int[] resistance = this.getArrayForField(data, "resistance");
         int[] harvestLevel = this.getArrayForField(data, "harvestLevel");
         String[] harvestTool = null;
         String[] drops = null;
         if (data.containsDataPiece("harvestTool")) {
            harvestTool = data.getDataPiece("harvestTool").split(",");
         }

         if (data.containsDataPiece("drops")) {
            drops = data.getDataPiece("drops").split(",");
         }

         for(int i = 0; i < variantNames.length; ++i) {
            String variantName = variantNames[i];
            MaterialPart variantMaterialPart = new MaterialPart(materialPart.getMaterialUser(), materialPart.getMaterial(), materialPart.getPart(), variantName);
            MaterialPartData variantData = variantMaterialPart.getData();
            variantData.addDataValue("variants", variantName);
            this.trySetData(hardness, i, "hardness", variantData);
            this.trySetData(resistance, i, "resistance", variantData);
            this.trySetData(harvestLevel, i, "harvestLevel", variantData);
            if (harvestTool != null && harvestTool.length > i) {
               variantData.addDataValue("harvestTool", harvestTool[i]);
            }

            if (drops != null && drops.length > i) {
               variantData.addDataValue("drops", drops[i]);
            }

            this.registerSubBlock(materialPart, new SubBlockOrePart(variantMaterialPart, new ResourceLocation(variantName), materialUser));
            variantMaterialPart.setColorized(materialPart.isColorized());
            materialUser.registerMaterialPart(variantMaterialPart);
         }

         MaterialSystem.removeMaterialPart(materialPart);
      } else {
         this.registerSubBlock(materialPart, new SubBlockOrePart(materialPart, new ResourceLocation("stone"), materialUser));
      }

   }

   private void trySetData(int[] numbers, int place, String fieldName, MaterialPartData data) {
      if (numbers != null && numbers.length > place) {
         data.addDataValue(fieldName, Integer.toString(numbers[place]));
      }

   }

   private int[] getArrayForField(MaterialPartData data, String fieldName) {
      int[] returned = null;
      if (data.containsDataPiece(fieldName)) {
         String[] stringPieces = data.getDataPiece(fieldName).split(",");
         returned = new int[stringPieces.length];

         for(int i = 0; i < stringPieces.length; ++i) {
            returned[i] = Integer.parseInt(stringPieces[i]);
         }
      }

      return returned;
   }
}
