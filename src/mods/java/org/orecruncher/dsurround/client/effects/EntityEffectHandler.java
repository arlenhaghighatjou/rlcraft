package org.orecruncher.dsurround.client.effects;

import com.google.common.collect.ImmutableList;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.orecruncher.lib.collections.ObjectArray;

@SideOnly(Side.CLIENT)
public class EntityEffectHandler extends EntityEffectStateBase implements IEntityEffectHandlerState {
   protected final ObjectArray<EntityEffect> activeEffects;
   protected boolean isAlive = true;
   protected double rangeToPlayer;

   protected EntityEffectHandler(@Nonnull Entity entity) {
      super(entity);
      this.activeEffects = null;
   }

   public EntityEffectHandler(@Nonnull Entity entity, @Nonnull IParticleHelper ph, @Nonnull ISoundHelper sh) {
      super(entity, ph, sh);
      this.activeEffects = null;
   }

   public EntityEffectHandler(@Nonnull Entity entity, @Nonnull ObjectArray<EntityEffect> effects, @Nonnull IParticleHelper ph, @Nonnull ISoundHelper sh) {
      super(entity, ph, sh);
      this.activeEffects = effects;

      for(EntityEffect ee : this.activeEffects) {
         ee.initialize(this);
      }

   }

   public void update() {
      if (this.isAlive()) {
         this.isAlive = this.isSubjectAlive();
         if (this.activeEffects != null) {
            Entity entity = (Entity)this.subject.get();
            if (entity != null) {
               EntityPlayer player = Minecraft.getMinecraft().player;
               this.rangeToPlayer = entity.getDistanceSq(player);

               for(int i = 0; i < this.activeEffects.size(); ++i) {
                  EntityEffect e = (EntityEffect)this.activeEffects.get(i);
                  if (this.isAlive || e.receiveLastCall()) {
                     e.update(entity);
                  }
               }
            }
         }

      }
   }

   public boolean isDummy() {
      return false;
   }

   @Nonnull
   public List<String> getAttachedEffects() {
      List<String> result = new ArrayList();
      if (this.activeEffects.size() == 0) {
         result.add("No effects");
      } else {
         for(EntityEffect e : this.activeEffects) {
            result.add(e.toString());
         }
      }

      return result;
   }

   public boolean isAlive() {
      return this.isAlive;
   }

   public static class Dummy extends EntityEffectHandler {
      public Dummy(@Nonnull Entity entity) {
         super(entity);
      }

      public void update() {
      }

      public boolean isDummy() {
         return true;
      }

      @Nonnull
      public List<String> getAttachedEffects() {
         return ImmutableList.of("Dummy EffectHandler");
      }
   }
}
