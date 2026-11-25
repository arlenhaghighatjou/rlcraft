package dev.satyrn.wolfarmor.advancements;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nonnull;
import net.minecraft.advancements.ICriterionTrigger;
import net.minecraft.advancements.PlayerAdvancements;
import net.minecraft.advancements.ICriterionTrigger.Listener;
import net.minecraft.advancements.critereon.AbstractCriterionInstance;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ResourceLocation;

public class WolfArmorTrigger implements ICriterionTrigger<WolfArmorTrigger.Instance> {
   private final Map<PlayerAdvancements, WolfArmorTrigger.Listeners> listeners = Maps.newHashMap();
   private final ResourceLocation id;

   public WolfArmorTrigger(ResourceLocation id) {
      this.id = id;
   }

   @Nonnull
   public ResourceLocation getId() {
      return this.id;
   }

   public void addListener(@Nonnull PlayerAdvancements playerAdvancementsIn, @Nonnull Listener<WolfArmorTrigger.Instance> listener) {
      WolfArmorTrigger.Listeners listeners = (WolfArmorTrigger.Listeners)this.listeners.get(playerAdvancementsIn);
      if (listeners == null) {
         listeners = new WolfArmorTrigger.Listeners(playerAdvancementsIn);
         this.listeners.put(playerAdvancementsIn, listeners);
      }

      listeners.add(listener);
   }

   public void removeListener(@Nonnull PlayerAdvancements playerAdvancementsIn, @Nonnull Listener<WolfArmorTrigger.Instance> listener) {
      WolfArmorTrigger.Listeners listeners = (WolfArmorTrigger.Listeners)this.listeners.get(playerAdvancementsIn);
      if (listeners != null) {
         listeners.remove(listener);
         if (listeners.isEmpty()) {
            this.listeners.remove(playerAdvancementsIn);
         }
      }

   }

   public void removeAllListeners(@Nonnull PlayerAdvancements playerAdvancementsIn) {
      this.listeners.remove(playerAdvancementsIn);
   }

   @Nonnull
   public WolfArmorTrigger.Instance deserializeInstance(@Nonnull JsonObject json, @Nonnull JsonDeserializationContext context) {
      return new WolfArmorTrigger.Instance(this.id, EntityPredicate.deserialize(json.get("entity")));
   }

   public void trigger(EntityPlayerMP player, Entity entity) {
      WolfArmorTrigger.Listeners listeners = (WolfArmorTrigger.Listeners)this.listeners.get(player.getAdvancements());
      if (listeners != null) {
         listeners.trigger(player, entity);
      }

   }

   static final class Instance extends AbstractCriterionInstance {
      private EntityPredicate target;

      Instance(ResourceLocation criterion, EntityPredicate target) {
         super(criterion);
         this.target = target;
      }

      boolean test(EntityPlayerMP player, Entity entity) {
         return this.target.test(player, entity);
      }
   }

   static final class Listeners {
      private final PlayerAdvancements playerAdvancements;
      private final Set<Listener<WolfArmorTrigger.Instance>> listeners = Sets.newHashSet();

      Listeners(@Nonnull PlayerAdvancements playerAdvancements) {
         this.playerAdvancements = playerAdvancements;
      }

      boolean isEmpty() {
         return this.listeners.isEmpty();
      }

      void add(Listener<WolfArmorTrigger.Instance> listener) {
         this.listeners.add(listener);
      }

      void remove(Listener<WolfArmorTrigger.Instance> listener) {
         this.listeners.remove(listener);
      }

      public void trigger(EntityPlayerMP player, Entity entity) {
         List<Listener<WolfArmorTrigger.Instance>> list = null;

         for(Listener<WolfArmorTrigger.Instance> listener : this.listeners) {
            if (((WolfArmorTrigger.Instance)listener.getCriterionInstance()).test(player, entity)) {
               if (list == null) {
                  list = Lists.newArrayList();
               }

               list.add(listener);
            }
         }

         if (list != null) {
            for(Listener<WolfArmorTrigger.Instance> listener : list) {
               listener.grantCriterion(this.playerAdvancements);
            }
         }

      }
   }
}
