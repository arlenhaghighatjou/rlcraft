package org.orecruncher.dsurround.client.handlers.effects;

import com.google.common.collect.ImmutableList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import javax.annotation.Nonnull;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.particle.Particle;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.orecruncher.dsurround.ModOptions;
import org.orecruncher.dsurround.capabilities.CapabilitySeasonInfo;
import org.orecruncher.dsurround.client.effects.EntityEffect;
import org.orecruncher.dsurround.client.effects.IEntityEffectFactory;
import org.orecruncher.dsurround.client.effects.IEntityEffectFactoryFilter;
import org.orecruncher.dsurround.client.effects.IEntityEffectHandlerState;
import org.orecruncher.dsurround.client.fx.particle.ParticleBubbleBreath;
import org.orecruncher.dsurround.client.fx.particle.ParticleFrostBreath;

@SideOnly(Side.CLIENT)
public class BreathEffect extends EntityEffect {
   private static final int PRIME = 311;
   private int seed;
   public static final IEntityEffectFactoryFilter DEFAULT_FILTER = (e, eei) -> {
      return eei.effects.contains("breath");
   };

   public BreathEffect() {
   }

   @Nonnull
   public String name() {
      return "Breath";
   }

   public void initialize(@Nonnull IEntityEffectHandlerState state) {
      super.initialize(state);
      Optional<Entity> entity = state.subject();
      entity.ifPresent((value) -> {
         this.seed = value.getEntityId() * 311;
      });
   }

   public void update(@Nonnull Entity subject) {
      if (ModOptions.effects.showBreath) {
         if (this.isBreathVisible(subject)) {
            int c = (int)(this.getState().getWorldTime() + (long)this.seed);
            IBlockState state = this.getHeadBlock(subject);
            if (this.showWaterBubbles(state)) {
               int air = subject.getAir();
               if (air > 0) {
                  int interval = c % 3;
                  if (interval == 0) {
                     Particle particle = new ParticleBubbleBreath(subject);
                     this.getState().addParticle(particle);
                  }
               } else if (air == 0) {
                  for(int i = 0; i < 8; ++i) {
                     Particle particle = new ParticleBubbleBreath(subject, true);
                     this.getState().addParticle(particle);
                  }
               }
            } else {
               int interval = c / 10 % 8;
               if (interval < 3 && this.showFrostBreath(subject, state)) {
                  this.getState().addParticle(new ParticleFrostBreath(subject));
               }
            }
         }

      }
   }

   protected boolean isBreathVisible(@Nonnull Entity entity) {
      EntityPlayer player = this.getState().thePlayer();
      if (entity == player) {
         return !player.isSpectator();
      } else {
         return !entity.isInvisibleToPlayer(player) && player.canEntityBeSeen(entity);
      }
   }

   protected IBlockState getHeadBlock(Entity entity) {
      double d0 = entity.posY + (double)entity.getEyeHeight();
      BlockPos blockpos = new BlockPos(entity.posX, d0, entity.posZ);
      return entity.getEntityWorld().getBlockState(blockpos);
   }

   protected boolean showWaterBubbles(@Nonnull IBlockState headBlock) {
      return headBlock.getMaterial().isLiquid();
   }

   protected boolean showFrostBreath(Entity entity, @Nonnull IBlockState headBlock) {
      if (headBlock.getMaterial() == Material.AIR) {
         World world = entity.getEntityWorld();
         BlockPos entityPos = entity.getPosition();
         return CapabilitySeasonInfo.getCapability(world).showFrostBreath(entityPos);
      } else {
         return false;
      }
   }

   public static class Factory implements IEntityEffectFactory {
      public Factory() {
      }

      @Nonnull
      public List<EntityEffect> create(@Nonnull Entity entity) {
         return ImmutableList.of(new BreathEffect());
      }
   }
}
