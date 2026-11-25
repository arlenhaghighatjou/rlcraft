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
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;

public class UseItemTrigger implements ICriterionTrigger<UseItemTrigger.Instance> {
   private static final ResourceLocation ID = new ResourceLocation("silentlib", "use_item");
   private final Map<PlayerAdvancements, UseItemTrigger.Listeners> listeners = new HashMap();

   public UseItemTrigger() {
   }

   public ResourceLocation getId() {
      return ID;
   }

   public void addListener(PlayerAdvancements playerAdvancementsIn, Listener<UseItemTrigger.Instance> listenerIn) {
      UseItemTrigger.Listeners triggerListeners = (UseItemTrigger.Listeners)this.listeners.get(playerAdvancementsIn);
      if (triggerListeners == null) {
         triggerListeners = new UseItemTrigger.Listeners(playerAdvancementsIn);
         this.listeners.put(playerAdvancementsIn, triggerListeners);
      }

      triggerListeners.add(listenerIn);
   }

   public void removeListener(PlayerAdvancements playerAdvancementsIn, Listener<UseItemTrigger.Instance> listenerIn) {
      UseItemTrigger.Listeners triggerListeners = (UseItemTrigger.Listeners)this.listeners.get(playerAdvancementsIn);
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

   public UseItemTrigger.Instance deserializeInstance(JsonObject json, JsonDeserializationContext context) {
      ItemPredicate itempredicate = ItemPredicate.deserialize(json.get("item"));
      UseItemTrigger.Target target = UseItemTrigger.Target.fromString(JsonUtils.getString(json, "target", "any"));
      return new UseItemTrigger.Instance(itempredicate, target);
   }

   public void trigger(EntityPlayerMP player, ItemStack stack, UseItemTrigger.Target target) {
      UseItemTrigger.Listeners triggerListeners = (UseItemTrigger.Listeners)this.listeners.get(player.getAdvancements());
      if (triggerListeners != null) {
         triggerListeners.trigger(stack, target);
      }

   }

   public static class Instance extends AbstractCriterionInstance {
      ItemPredicate itempredicate;
      UseItemTrigger.Target target;

      Instance(ItemPredicate itempredicate, UseItemTrigger.Target target) {
         super(UseItemTrigger.ID);
         this.itempredicate = itempredicate;
         this.target = target;
      }

      public boolean test(ItemStack stack, UseItemTrigger.Target target) {
         return this.itempredicate.test(stack) && (this.target == target || this.target == UseItemTrigger.Target.ANY);
      }
   }

   static class Listeners {
      private final PlayerAdvancements playerAdvancements;
      private final Set<Listener<UseItemTrigger.Instance>> listeners = new HashSet();

      Listeners(PlayerAdvancements playerAdvancementsIn) {
         this.playerAdvancements = playerAdvancementsIn;
      }

      public boolean isEmpty() {
         return this.listeners.isEmpty();
      }

      public void add(Listener<UseItemTrigger.Instance> listener) {
         this.listeners.add(listener);
      }

      public void remove(Listener<UseItemTrigger.Instance> listener) {
         this.listeners.remove(listener);
      }

      public void trigger(ItemStack stack, UseItemTrigger.Target target) {
         List<Listener<UseItemTrigger.Instance>> list = null;

         for(Listener<UseItemTrigger.Instance> listener : this.listeners) {
            if (((UseItemTrigger.Instance)listener.getCriterionInstance()).test(stack, target)) {
               if (list == null) {
                  list = new ArrayList();
               }

               list.add(listener);
            }
         }

         if (list != null) {
            for(Listener<UseItemTrigger.Instance> listener1 : list) {
               listener1.grantCriterion(this.playerAdvancements);
            }
         }

      }
   }

   public static enum Target {
      BLOCK,
      ENTITY,
      ITEM,
      ANY;

      private Target() {
      }

      static UseItemTrigger.Target fromString(String str) {
         for(UseItemTrigger.Target t : values()) {
            if (t.name().equalsIgnoreCase(str)) {
               return t;
            }
         }

         return ANY;
      }
   }
}
