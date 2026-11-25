package com.teamacronymcoders.base.materialsystem.partdata;

import com.google.common.collect.Lists;
import com.teamacronymcoders.base.Base;
import com.teamacronymcoders.base.materialsystem.MaterialException;
import com.teamacronymcoders.base.materialsystem.materialparts.MaterialPart;
import com.teamacronymcoders.base.materialsystem.parttype.PartDataPiece;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;

public class MaterialPartData {
   private MaterialPart materialPart;
   private Map<String, String> data = new HashMap();
   private List<PartDataPiece> dataPieces;

   public MaterialPartData(MaterialPart materialPart, List<PartDataPiece> dataPieces) {
      this.dataPieces = dataPieces;
      this.materialPart = materialPart;
   }

   public String getDataPiece(String name) {
      return (String)this.data.get(name.toLowerCase(Locale.US));
   }

   public <T> T getValue(String name, T defaultValue, Function<String, T> parser) {
      T value = defaultValue;

      try {
         String dataPiece = this.getDataPiece(name);
         if (dataPiece != null) {
            value = (T)parser.apply(dataPiece);
         }
      } catch (NumberFormatException var6) {
         Base.instance.getLogger().warning(var6.getMessage());
      }

      return value;
   }

   public <T> List<T> getValues(String name, T[] defaultValue, Function<String, T> parser) {
      String value = this.getDataPiece(name);
      List<T> returnList = Lists.newArrayList(defaultValue);
      if (value != null) {
         String[] valueArray = value.split(",");
         List<T> actualValues = Lists.newArrayList();

         for(String string : valueArray) {
            actualValues.add(parser.apply(string));
         }

         returnList = actualValues;
      }

      return returnList;
   }

   public <T> List<T> getValues(String name, T[] defaultValue, Function<String, T> parser, int expectedValues) {
      List<T> values = this.<T>getValues(name, defaultValue, parser);
      if (values.size() != expectedValues) {
         this.materialPart.getMaterialUser().logError("Didn't find expected number of values for field " + name + ". Expected " + expectedValues + ". Found " + values.size() + ".");
      }

      return values;
   }

   public boolean containsDataPiece(String name) {
      return this.data.containsKey(name.toLowerCase(Locale.US));
   }

   public void addDataValue(String name, String value) {
      this.data.put(name.toLowerCase(Locale.US), value);
   }

   public void validate() throws MaterialException {
      // $FF: Couldn't be decompiled
   }
}
