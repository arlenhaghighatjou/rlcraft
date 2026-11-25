package com.charles445.simpledifficulty.api.config.json;

import com.google.gson.annotations.SerializedName;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;

public class JsonPropertyTemperature {
   @SerializedName("properties")
   public Map<String, String> properties;
   @SerializedName("temperature")
   public float temperature;

   public JsonPropertyTemperature(float temperature, JsonPropertyValue... props) {
      this.temperature = temperature;
      this.properties = new HashMap();

      for(JsonPropertyValue prop : props) {
         this.properties.put(prop.property, prop.value);
      }

   }

   public JsonPropertyValue[] getAsPropertyArray() {
      List<JsonPropertyValue> jpvList = new ArrayList();

      for(Entry<String, String> entry : this.properties.entrySet()) {
         jpvList.add(new JsonPropertyValue((String)entry.getKey(), (String)entry.getValue()));
      }

      return (JsonPropertyValue[])jpvList.toArray(new JsonPropertyValue[0]);
   }

   public boolean matchesState(IBlockState blockstate) {
      for(IProperty<?> property : blockstate.getPropertyKeys()) {
         String propname = property.getName();
         if (this.properties.containsKey(propname)) {
            String stateValue = blockstate.getValue(property).toString();
            if (!((String)this.properties.get(propname)).equals(stateValue)) {
               return false;
            }
         }
      }

      return true;
   }

   public boolean matchesDescribedProperties(JsonPropertyValue... props) {
      if (props.length != this.properties.keySet().size()) {
         return false;
      } else {
         for(JsonPropertyValue prop : props) {
            if (!this.properties.containsKey(prop.property)) {
               return false;
            }

            if (!prop.value.equals(this.properties.get(prop.property))) {
               return false;
            }
         }

         return true;
      }
   }
}
