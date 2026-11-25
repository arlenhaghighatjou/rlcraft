package org.orecruncher.dsurround.client.handlers.effects;

import com.google.common.collect.ImmutableList;
import java.util.List;
import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import java.util.function.Consumer;
import javax.annotation.Nonnull;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.orecruncher.dsurround.ModOptions;
import org.orecruncher.dsurround.client.effects.EntityEffect;
import org.orecruncher.dsurround.client.effects.IEntityEffectFactory;
import org.orecruncher.dsurround.client.effects.IEntityEffectFactoryFilter;
import org.orecruncher.dsurround.client.effects.IEntityEffectHandlerState;
import org.orecruncher.dsurround.client.footsteps.Generator;
import org.orecruncher.dsurround.registry.RegistryManager;
import org.orecruncher.lib.random.XorShiftRandom;

@SideOnly(Side.CLIENT)
public class EntityFootprintEffect extends EntityEffect {
   protected static final EaglercraftRandom RANDOM = XorShiftRandom.current();
   protected Generator generator;
   public static final IEntityEffectFactoryFilter DEFAULT_FILTER = (e, eei) -> {
      return eei.effects.contains("footprint");
   };

   public EntityFootprintEffect() {
   }

   @Nonnull
   public String name() {
      return "Footstep/Prints";
   }

   public void initialize(@Nonnull IEntityEffectHandlerState state) {
      super.initialize(state);
      this.getState().subject().ifPresent((e) -> {
         this.generator = RegistryManager.FOOTSTEPS.createGenerator((EntityLivingBase)e);
      });
   }

   public void update(@Nonnull Entity subject) {
      if (this.generator != null) {
         this.generator.generateFootsteps((EntityLivingBase)subject);
      }

   }

   public String toString() {
      return this.generator == null ? "NULL GENERATOR" : super.toString() + ": " + this.generator.getPedometer();
   }

   public static class Factory implements IEntityEffectFactory {
      public Factory() {
      }

      @Nonnull
      public List<EntityEffect> create(@Nonnull Entity entity) {
         return ImmutableList.of(entity instanceof EntityPlayer ? new EntityFootprintEffect.PlayerFootprintEffect() : new EntityFootprintEffect());
      }
   }

   @SideOnly(Side.CLIENT)
   private static class PlayerFootprintEffect extends EntityFootprintEffect {
      protected int lastStyle;

      private PlayerFootprintEffect() {
      }

      @Nonnull
      public String name() {
         return "Player Footstep/Prints";
      }

      public void initialize(@Nonnull IEntityEffectHandlerState state) {
         super.initialize(state);
         this.lastStyle = ModOptions.effects.footprintStyle;
      }

      public void update(@Nonnull Entity subject) {
         if (this.lastStyle != ModOptions.effects.footprintStyle) {
            this.generator = RegistryManager.FOOTSTEPS.createGenerator((EntityLivingBase)subject);
            this.lastStyle = ModOptions.effects.footprintStyle;
         }

         super.update(subject);
      }
   }
}
