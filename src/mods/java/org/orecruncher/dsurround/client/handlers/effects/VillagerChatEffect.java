package org.orecruncher.dsurround.client.handlers.effects;

import com.google.common.collect.ImmutableList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;
import javax.annotation.Nonnull;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntityEvoker;
import net.minecraft.entity.monster.EntityVex;
import net.minecraft.entity.monster.EntityVindicator;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.util.EntitySelectors;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.orecruncher.dsurround.ModBase;
import org.orecruncher.dsurround.ModOptions;
import org.orecruncher.dsurround.capabilities.CapabilityEntityData;
import org.orecruncher.dsurround.capabilities.entitydata.IEntityData;
import org.orecruncher.dsurround.client.effects.EntityEffect;
import org.orecruncher.dsurround.client.effects.IEntityEffectFactory;
import org.orecruncher.dsurround.client.effects.IEntityEffectFactoryFilter;
import org.orecruncher.dsurround.client.effects.IEntityEffectHandlerState;

@SideOnly(Side.CLIENT)
public class VillagerChatEffect extends EntityEffect {
   protected final Predicate<Entity> pred;
   protected final EntityChatEffect normalChat;
   protected final EntityChatEffect fleeChat;
   protected boolean runningScared = false;
   public static final IEntityEffectFactoryFilter DEFAULT_FILTER = (e, eei) -> {
      return eei.effects.contains("chat") && e instanceof EntityVillager && EntityChatEffect.hasMessages(e);
   };

   public VillagerChatEffect(@Nonnull Entity entity) {
      EntityVillager villager = (EntityVillager)entity;
      this.pred = EntitySelectors.CAN_AI_TARGET.and((input) -> {
         return input.isEntityAlive() && villager.getEntitySenses().canSee(input);
      });
      this.normalChat = new EntityChatEffect(entity);
      this.fleeChat = new EntityChatEffect(entity, "villager.flee");
   }

   @Nonnull
   public String name() {
      return "Villager Chat";
   }

   public void initialize(@Nonnull IEntityEffectHandlerState state) {
      super.initialize(state);
      this.normalChat.initialize(state);
      this.fleeChat.initialize(state);
   }

   public void update(@Nonnull Entity subject) {
      if (ModOptions.speechbubbles.enableEntityChat) {
         EntityVillager entity = (EntityVillager)subject;
         if (!entity.isChild()) {
            if (this.villagerThreatened(entity)) {
               this.runningScared = true;
               this.fleeChat.update(subject);
            } else {
               if (this.runningScared) {
                  this.runningScared = false;
                  this.normalChat.genNextChatTime();
               }

               this.normalChat.update(subject);
            }

         }
      }
   }

   protected boolean villagerThreatened(Entity entity) {
      if (ModBase.isInstalledOnServer()) {
         IEntityData data = CapabilityEntityData.getCapability(entity);
         if (data != null) {
            return data.isFleeing();
         }
      }

      Iterator var4 = entity.getEntityWorld().loadedEntityList.iterator();

      while(true) {
         if (!var4.hasNext()) {
            return false;
         }

         Entity e = (Entity)var4.next();
         if (e.getDistanceSq(entity) <= 64.0D && (e instanceof EntityZombie || e instanceof EntityEvoker || e instanceof EntityVex || e instanceof EntityVindicator) && this.pred.test(e)) {
            break;
         }
      }

      return true;
   }

   static {
      EntityChatEffect.setTimers("villager.flee", 250, 200);
   }

   public static class Factory implements IEntityEffectFactory {
      public Factory() {
      }

      @Nonnull
      public List<EntityEffect> create(@Nonnull Entity entity) {
         return ImmutableList.of(new VillagerChatEffect(entity));
      }
   }
}
