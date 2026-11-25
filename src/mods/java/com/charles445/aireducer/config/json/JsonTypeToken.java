package com.charles445.aireducer.config.json;

import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.Map;

public class JsonTypeToken {
   public JsonTypeToken() {
   }

   public static Type get(JsonFileName jcfn) {
      switch(jcfn) {
      case taskDelay:
         return (new TypeToken<Map<String, Integer>>() {
         }).getType();
      default:
         return null;
      }
   }
}
