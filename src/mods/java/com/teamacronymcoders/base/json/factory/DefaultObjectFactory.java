package com.teamacronymcoders.base.json.factory;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import net.minecraftforge.common.crafting.JsonContext;

public class DefaultObjectFactory<T> implements IObjectFactory<T> {
   private static final Gson GSON = new Gson();
   private final Class<T> tClass;

   public DefaultObjectFactory(Class<T> tClass) {
      this.tClass = tClass;
   }

   public T parse(JsonContext jsonContext, JsonElement jsonElement) {
      return (T)GSON.fromJson(jsonElement, this.tClass);
   }
}
