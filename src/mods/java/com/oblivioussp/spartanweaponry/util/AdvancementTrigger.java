package com.oblivioussp.spartanweaponry.util;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.ICriterionInstance;
import net.minecraft.advancements.ICriterionTrigger;
import net.minecraft.advancements.PlayerAdvancements;
import net.minecraft.advancements.ICriterionTrigger.Listener;
import net.minecraft.advancements.critereon.AbstractCriterionInstance;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

public class AdvancementTrigger implements ICriterionTrigger {
   public static final AdvancementTrigger CRAFT_ITEM = new AdvancementTrigger("craft_item");
   public static final AdvancementTrigger[] TRIGGER_ARRAY = new AdvancementTrigger[]{CRAFT_ITEM};
   private final ResourceLocation id;
   private final Map<PlayerAdvancements, AdvancementTrigger.Listeners> listeners = Maps.newHashMap();

   public AdvancementTrigger(String idString) {
      this.id = new ResourceLocation(idString);
   }

   public AdvancementTrigger(ResourceLocation idRes) {
      this.id = idRes;
   }

   public ResourceLocation getId() {
      return this.id;
   }

   public void addListener(PlayerAdvancements playerAdvancementsIn, Listener listener) {
      AdvancementTrigger.Listeners triggerListeners = (AdvancementTrigger.Listeners)this.listeners.get(playerAdvancementsIn);
      if (triggerListeners == null) {
         triggerListeners = new AdvancementTrigger.Listeners(playerAdvancementsIn);
         this.listeners.put(playerAdvancementsIn, triggerListeners);
      }

      triggerListeners.add(listener);
   }

   public void removeListener(PlayerAdvancements playerAdvancementsIn, Listener listener) {
      AdvancementTrigger.Listeners triggerListeners = (AdvancementTrigger.Listeners)this.listeners.get(playerAdvancementsIn);
      if (triggerListeners != null) {
         triggerListeners.remove(listener);
         if (triggerListeners.isEmpty()) {
            this.listeners.remove(playerAdvancementsIn);
         }
      }

   }

   public void removeAllListeners(PlayerAdvancements playerAdvancementsIn) {
      this.listeners.remove(playerAdvancementsIn);
   }

   public ICriterionInstance deserializeInstance(JsonObject json, JsonDeserializationContext context) {
      String type = json.get("className").getAsString();
      return new AdvancementTrigger.Instance(this.getId(), type);
   }

   public void trigger(EntityPlayerMP player, Item item) {
      AdvancementTrigger.Listeners triggerListeners = (AdvancementTrigger.Listeners)this.listeners.get(player.getAdvancements());
      if (triggerListeners != null) {
         triggerListeners.trigger(player, item);
      }

   }

   public static void registerTriggers() {
      try {
         Log.info("Attempting to add new advancement triggers...");
         Method method = ReflectionHelper.findMethod(CriteriaTriggers.class, "register", "register", new Class[]{ICriterionTrigger.class});
         method.setAccessible(true);

         for(int i = 0; i < TRIGGER_ARRAY.length; ++i) {
            method.invoke((Object)null, TRIGGER_ARRAY[i]);
         }

         Log.info("New advancement triggers added successfully!");
      } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | SecurityException var2) {
         Log.error("Couldn't register advancement triggers due to an exception occuring. " + var2.getMessage());
      }

   }

   public static class Instance extends AbstractCriterionInstance {
      private final String itemClass;

      public Instance(ResourceLocation criterionId, String className) {
         super(criterionId);
         this.itemClass = className;
      }

      public boolean test(Item item) {
         String canonicalName = item != null ? item.getClass().getCanonicalName() : null;
         return canonicalName != null && canonicalName.equals(this.itemClass);
      }
   }

   protected static class Listeners {
      private final PlayerAdvancements playerAdvancements;
      private final Set<Listener> listeners = Sets.newHashSet();

      public Listeners(PlayerAdvancements advancements) {
         this.playerAdvancements = advancements;
      }

      public boolean isEmpty() {
         return this.listeners.isEmpty();
      }

      public void add(Listener listener) {
         this.listeners.add(listener);
      }

      public void remove(Listener listener) {
         this.listeners.remove(listener);
      }

      public void trigger(EntityPlayerMP player, Item item) {
         List<Listener> list = null;

         for(Listener listener : this.listeners) {
            if (((AdvancementTrigger.Instance)listener.getCriterionInstance()).test(item)) {
               if (list == null) {
                  list = new ArrayList();
               }

               list.add(listener);
            }
         }

         if (list != null) {
            for(Listener listener : list) {
               listener.grantCriterion(this.playerAdvancements);
            }
         }

      }
   }
}
