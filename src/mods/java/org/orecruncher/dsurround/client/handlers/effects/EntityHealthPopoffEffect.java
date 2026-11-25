package org.orecruncher.dsurround.client.handlers.effects;

import com.google.common.collect.ImmutableList;
import java.util.List;
import java.util.function.Consumer;
import javax.annotation.Nonnull;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.orecruncher.dsurround.ModOptions;
import org.orecruncher.dsurround.client.effects.EntityEffect;
import org.orecruncher.dsurround.client.effects.IEntityEffectFactory;
import org.orecruncher.dsurround.client.effects.IEntityEffectFactoryFilter;
import org.orecruncher.dsurround.client.effects.IEntityEffectHandlerState;
import org.orecruncher.dsurround.client.fx.particle.ParticleTextPopOff;
import org.orecruncher.dsurround.client.handlers.EnvironStateHandler;
import org.orecruncher.lib.Color;
import org.orecruncher.lib.Translations;
import org.orecruncher.lib.math.MathStuff;
import org.orecruncher.lib.random.XorShiftRandom;

@SideOnly(Side.CLIENT)
public class EntityHealthPopoffEffect extends EntityEffect {
   private static final Color CRITICAL_TEXT_COLOR = Color.MC_GOLD;
   private static final Color HEAL_TEXT_COLOR = Color.MC_GREEN;
   private static final Color DAMAGE_TEXT_COLOR = Color.MC_RED;
   private static final Translations xlate = new Translations();
   private static final int CRITWORD_COUNT = 85;
   private static final String CRITWORD_PREFIX = "critword.";
   protected float lastHealth;
   public static final IEntityEffectFactoryFilter DEFAULT_FILTER = (e, eei) -> {
      return e instanceof EntityLivingBase;
   };

   public EntityHealthPopoffEffect() {
   }

   private String getPowerWord() {
      int id = XorShiftRandom.current().nextInt(85);
      return xlate.loadString("critword." + id) + "!";
   }

   public void initialize(@Nonnull IEntityEffectHandlerState state) {
      super.initialize(state);
      this.getState().subject().ifPresent((e) -> {
         this.lastHealth = ((EntityLivingBase)e).getHealth();
      });
   }

   @Nonnull
   public String name() {
      return "Health Tracker";
   }

   public boolean receiveLastCall() {
      return true;
   }

   public void update(@Nonnull Entity subject) {
      if (ModOptions.effects.enableDamagePopoffs) {
         EntityLivingBase entity = (EntityLivingBase)subject;
         if (this.lastHealth != entity.getHealth()) {
            int adjustment = MathHelper.ceil(entity.getHealth() - this.lastHealth);
            this.lastHealth = entity.getHealth();
            if (!EnvironStateHandler.EnvironState.isPlayer(subject) || !this.isFirstPersonView()) {
               int delta = Math.max(1, MathStuff.abs(adjustment));
               int criticalAmount = (int)(entity.getMaxHealth() / 2.5F);
               AxisAlignedBB bb = entity.getEntityBoundingBox();
               double posX = entity.posX;
               double posY = bb.maxY + 0.5D;
               double posZ = entity.posZ;
               String text = String.valueOf(delta);
               Color color = adjustment > 0 ? HEAL_TEXT_COLOR : DAMAGE_TEXT_COLOR;
               World world = EnvironStateHandler.EnvironState.getWorld();
               if (ModOptions.effects.showCritWords && adjustment < 0 && delta >= criticalAmount) {
                  ParticleTextPopOff particle = new ParticleTextPopOff(world, this.getPowerWord(), CRITICAL_TEXT_COLOR, posX, posY + 0.5D, posZ);
                  this.getState().addParticle(particle);
               }

               ParticleTextPopOff particle = new ParticleTextPopOff(world, text, color, posX, posY, posZ);
               this.getState().addParticle(particle);
            }
         }

      }
   }

   static {
      xlate.load("/assets/dsurround/dsurround/data/critwords/", new String[0]);
   }

   public static class Factory implements IEntityEffectFactory {
      public Factory() {
      }

      @Nonnull
      public List<EntityEffect> create(@Nonnull Entity entity) {
         return ImmutableList.of(new EntityHealthPopoffEffect());
      }
   }
}
