package com.teamacronymcoders.base.recipesystem.event;

import com.google.common.collect.Maps;
import com.teamacronymcoders.base.json.factory.DefaultObjectFactory;
import com.teamacronymcoders.base.json.factory.IObjectFactory;
import java.util.Map;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.eventhandler.GenericEvent;

@ParametersAreNonnullByDefault
public class RegisterRecipeFactoriesEvent<T> extends GenericEvent<T> {
   private final Map<String, IObjectFactory<? extends T>> factories = Maps.newHashMap();

   public RegisterRecipeFactoriesEvent(Class<T> tClass) {
      super(tClass);
   }

   public void register(ResourceLocation resourceLocation, IObjectFactory<? extends T> factory) {
      this.factories.put(resourceLocation.toString(), factory);
   }

   public void register(ResourceLocation resourceLocation, Class<? extends T> tClass) {
      this.register(resourceLocation, new DefaultObjectFactory(tClass));
   }

   public Map<String, IObjectFactory<? extends T>> getFactories() {
      return this.factories;
   }
}
