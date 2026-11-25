package net.silentchaos512.lib.advancements;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.minecraft.advancements.ICriterionTrigger;
import net.minecraft.advancements.PlayerAdvancements;
import net.minecraft.advancements.ICriterionTrigger.Listener;
import net.minecraft.advancements.critereon.AbstractCriterionInstance;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;

public class GenericIntTrigger implements ICriterionTrigger<GenericIntTrigger.Instance> {
   private static final ResourceLocation ID = new ResourceLocation("silentlib", "generic_int");
   private final Map<PlayerAdvancements, GenericIntTrigger.Listeners> listeners = new HashMap();

   public GenericIntTrigger() {
   }

   public ResourceLocation getId() {
      return ID;
   }

   public void addListener(PlayerAdvancements playerAdvancementsIn, Listener<GenericIntTrigger.Instance> listenerIn) {
      GenericIntTrigger.Listeners triggerListeners = (GenericIntTrigger.Listeners)this.listeners.get(playerAdvancementsIn);
      if (triggerListeners == null) {
         triggerListeners = new GenericIntTrigger.Listeners(playerAdvancementsIn);
         this.listeners.put(playerAdvancementsIn, triggerListeners);
      }

      triggerListeners.add(listenerIn);
   }

   public void removeListener(PlayerAdvancements playerAdvancementsIn, Listener<GenericIntTrigger.Instance> listenerIn) {
      GenericIntTrigger.Listeners triggerListeners = (GenericIntTrigger.Listeners)this.listeners.get(playerAdvancementsIn);
      if (triggerListeners != null) {
         triggerListeners.remove(listenerIn);
         if (triggerListeners.isEmpty()) {
            this.listeners.remove(playerAdvancementsIn);
         }
      }

   }

   public void removeAllListeners(PlayerAdvancements playerAdvancementsIn) {
      this.listeners.remove(playerAdvancementsIn);
   }

   public GenericIntTrigger.Instance deserializeInstance(JsonObject json, JsonDeserializationContext context) {
      String type = JsonUtils.getString(json, "type", "unknown");
      int value = JsonUtils.getInt(json, "value", 0);
      return new GenericIntTrigger.Instance(type, value);
   }

   public void trigger(EntityPlayerMP player, ResourceLocation type, int value) {
      GenericIntTrigger.Listeners triggerListeners = (GenericIntTrigger.Listeners)this.listeners.get(player.getAdvancements());
      if (triggerListeners != null) {
         triggerListeners.trigger(type.toString(), value);
      }

   }

   public static class Instance extends AbstractCriterionInstance {
      String type;
      int value;

      Instance(String type, int value) {
         super(GenericIntTrigger.ID);
         this.type = type;
         this.value = value;
      }

      public boolean test(String typeIn, int valueIn) {
         return this.type.equals(typeIn) && this.value <= valueIn;
      }
   }

   static class Listeners {
      private final PlayerAdvancements playerAdvancements;
      private final Set<Listener<GenericIntTrigger.Instance>> listeners = new HashSet();

      Listeners(PlayerAdvancements playerAdvancementsIn) {
         this.playerAdvancements = playerAdvancementsIn;
      }

      public boolean isEmpty() {
         return this.listeners.isEmpty();
      }

      public void add(Listener<GenericIntTrigger.Instance> listener) {
         this.listeners.add(listener);
      }

      public void remove(Listener<GenericIntTrigger.Instance> listener) {
         this.listeners.remove(listener);
      }

      public void trigger(String typeIn, int valueIn) {
         List<Listener<GenericIntTrigger.Instance>> list = new ArrayList();

         for(Listener<GenericIntTrigger.Instance> listener : this.listeners) {
            if (((GenericIntTrigger.Instance)listener.getCriterionInstance()).test(typeIn, valueIn)) {
               list.add(listener);
            }
         }

         for(Listener<GenericIntTrigger.Instance> listener : list) {
            listener.grantCriterion(this.playerAdvancements);
         }

      }
   }
}
