package com.teamacronymcoders.contenttweaker.modules.materials.materialpartdata;

import com.teamacronymcoders.base.materialsystem.partdata.DataPartParsers;
import com.teamacronymcoders.base.materialsystem.partdata.MaterialPartData;
import java.util.function.Function;

public class CTMaterialPartData implements IMaterialPartData {
   private MaterialPartData materialPartData;

   public CTMaterialPartData(MaterialPartData materialPartData) {
      this.materialPartData = materialPartData;
   }

   public void addDataValue(String name, String value) {
      this.materialPartData.addDataValue(name, value);
   }

   public int getIntValue(String name, int defaultValue) {
      return ((Integer)this.materialPartData.getValue(name, Integer.valueOf(defaultValue), DataPartParsers::getInt)).intValue();
   }

   public String getStringValue(String name, String defaultValue) {
      return (String)this.materialPartData.getValue(name, defaultValue, DataPartParsers::getString);
   }
}
