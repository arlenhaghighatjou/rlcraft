package ichttt.mods.firstaid.common.potion;

import ichttt.mods.firstaid.FirstAid;
import ichttt.mods.firstaid.FirstAidConfig;
import ichttt.mods.firstaid.api.CapabilityExtendedHealthSystem;
import ichttt.mods.firstaid.api.damagesystem.AbstractPlayerDamageModel;
import ichttt.mods.firstaid.common.damagesystem.distribution.DamageDistribution;
import ichttt.mods.firstaid.common.damagesystem.distribution.RandomDamageDistribution;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Objects;
import javax.annotation.Nonnull;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

public class PotionPoisonPatched extends Potion {
   public static final PotionPoisonPatched INSTANCE = new PotionPoisonPatched(true, 5149489);
   private static final Method getHurtSound = ObfuscationReflectionHelper.findMethod(EntityLivingBase.class, "getHurtSound", SoundEvent.class, new Class[]{DamageSource.class});
   private static final Method getSoundVolume = ObfuscationReflectionHelper.findMethod(EntityLivingBase.class, "getSoundVolume", Float.TYPE, new Class[0]);
   private static final Method getSoundPitch = ObfuscationReflectionHelper.findMethod(EntityLivingBase.class, "getSoundPitch", Float.TYPE, new Class[0]);

   protected PotionPoisonPatched(boolean isBadEffectIn, int liquidColorIn) {
      super(isBadEffectIn, liquidColorIn);
      this.setPotionName("effect.poison");
      this.setIconIndex(6, 0);
      this.setEffectiveness(0.25D);
      this.setRegistryName(new ResourceLocation("minecraft", "poison"));
      FirstAid.LOGGER.info("Don't worry, the minecraft poison override IS intended.");
   }

   public void performEffect(@Nonnull EntityLivingBase entity, int amplifier) {
      if (entity instanceof EntityPlayer && !(entity instanceof FakePlayer) && (FirstAidConfig.damageSystem.causeDeathBody || FirstAidConfig.damageSystem.causeDeathHead)) {
         if (entity.world.isRemote || entity.isDead || entity.isEntityInvulnerable(DamageSource.MAGIC)) {
            return;
         }

         EntityPlayer player = (EntityPlayer)entity;
         AbstractPlayerDamageModel playerDamageModel = (AbstractPlayerDamageModel)Objects.requireNonNull(player.getCapability(CapabilityExtendedHealthSystem.INSTANCE, (EnumFacing)null));
         if (DamageDistribution.handleDamageTaken(RandomDamageDistribution.ANY_NOKILL, playerDamageModel, 1.0F, player, DamageSource.MAGIC, true, false) != 1.0F) {
            if (player.isPlayerSleeping()) {
               player.wakeUpPlayer(true, true, false);
            }

            try {
               SoundEvent sound = (SoundEvent)getHurtSound.invoke(player, DamageSource.MAGIC);
               player.world.playSound((EntityPlayer)null, player.posX, player.posY, player.posZ, sound, player.getSoundCategory(), ((Float)getSoundVolume.invoke(player)).floatValue(), ((Float)getSoundPitch.invoke(player)).floatValue());
            } catch (InvocationTargetException | IllegalAccessException var6) {
               FirstAid.LOGGER.error("Could not play hurt sound!", var6);
            }
         }
      } else {
         super.performEffect(entity, amplifier);
      }

   }
}
