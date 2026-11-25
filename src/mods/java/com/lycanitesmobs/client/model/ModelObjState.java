package com.lycanitesmobs.client.model;

import java.util.HashMap;
import java.util.Map;

public class ModelObjState {
   public float attackAnimationProgress = 0.0F;
   public boolean attackAnimationIncreasing = false;
   public boolean attackAnimationPlaying = false;
   public float attackAnimationSpeed = 0.1F;
   protected Map<String, Boolean> additionalBooleans = new HashMap();
   protected Map<String, Float> additionalFloats = new HashMap();

   public ModelObjState() {
   }

   public void setBoolean(String key, boolean value) {
      this.additionalBooleans.put(key, Boolean.valueOf(value));
   }

   public boolean getBoolean(String key) {
      return this.additionalBooleans.containsKey(key) ? ((Boolean)this.additionalBooleans.get(key)).booleanValue() : false;
   }

   public void setFloat(String key, float value) {
      this.additionalFloats.put(key, Float.valueOf(value));
   }

   public float getFloat(String key) {
      return this.additionalFloats.containsKey(key) ? ((Float)this.additionalFloats.get(key)).floatValue() : 0.0F;
   }
}
