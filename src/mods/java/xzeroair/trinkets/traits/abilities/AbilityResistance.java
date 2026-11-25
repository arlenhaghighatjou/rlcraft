package xzeroair.trinkets.traits.abilities;

import java.util.function.Consumer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import xzeroair.trinkets.capabilities.Capabilities;
import xzeroair.trinkets.capabilities.Vip.VipStatus;
import xzeroair.trinkets.traits.abilities.interfaces.IAttackAbility;
import xzeroair.trinkets.traits.abilities.interfaces.IPotionAbility;
import xzeroair.trinkets.traits.abilities.interfaces.ITickableAbility;
import xzeroair.trinkets.util.TrinketsConfig;
import xzeroair.trinkets.util.config.ClientConfig;
import xzeroair.trinkets.util.config.trinkets.ConfigDamageShield;

public class AbilityResistance extends Ability implements ITickableAbility, IAttackAbility, IPotionAbility {
   protected static final ClientConfig.ClientConfigItems.ClientConfigDamageShield clientConfig;
   protected static final ConfigDamageShield serverConfig;
   protected int hitCount = 0;

   public AbilityResistance() {
      super("safe_guard");
   }

   public int getHitCount() {
      return this.hitCount;
   }

   public AbilityResistance setHitCount(int hitCount) {
      this.hitCount = hitCount;
      return this;
   }

   public void tickAbility(EntityLivingBase entity) {
      World world = entity.getEntityWorld();
      if (!world.isRemote) {
         int duration = 200;
         Potion resistance = Potion.getPotionFromResourceLocation(serverConfig.potionEffect);
         if (resistance == null) {
            resistance = MobEffects.RESISTANCE;
         }

         boolean has = entity.isPotionActive(resistance);
         if (!has) {
            entity.addPotionEffect(new PotionEffect(resistance, 200, serverConfig.resistance_level, false, false));
         }

      }
   }

   public boolean attacked(EntityLivingBase attacked, DamageSource source, float dmg, boolean cancel) {
      if (serverConfig.damage_ignore && !attacked.world.isRemote && dmg >= 1.0F) {
         boolean indirect = this.isIndirectDamage(source);
         if (indirect) {
            this.countHit(attacked, indirect);
            if (this.trigger(attacked, indirect)) {
               return true;
            }
         }
      }

      return cancel;
   }

   public float hurt(EntityLivingBase attacked, DamageSource source, float dmg) {
      if (serverConfig.damage_ignore && dmg >= 1.0F) {
         boolean indirect = this.isIndirectDamage(source);
         if (!indirect) {
            this.countHit(attacked, indirect);
            if (this.trigger(attacked, indirect)) {
               dmg = 0.0F;
            }
         }
      }

      return serverConfig.explosion_resist && source.isExplosion() && dmg >= 1.0F ? dmg * serverConfig.explosion_amount : dmg;
   }

   protected void countHit(EntityLivingBase attacked, boolean indirect) {
      ++this.hitCount;
      Capabilities.getEntityProperties(attacked, (prop) -> {
         prop.sendInformationToPlayer(attacked);
      });
   }

   protected boolean trigger(EntityLivingBase attacked, boolean indirect) {
      if (this.hitCount > serverConfig.hits) {
         this.hitCount = 0;
         if (attacked instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer)attacked;
            if (serverConfig.special && TrinketsConfig.SERVER.misc.retrieveVIP) {
               String string = "Ow!";
               VipStatus vip = Capabilities.getVipStatus(attacked);
               if (vip != null) {
                  String quote = vip.getRandomQuote();
                  if (!quote.isEmpty()) {
                     string = quote;
                  }
               }

               TextComponentString message = new TextComponentString(TextFormatting.BOLD + "" + TextFormatting.GOLD + string);
               player.sendStatusMessage(message, true);
               if (clientConfig.effectVolume > 0.0D) {
                  player.world.playSound((EntityPlayer)null, player.posX, player.posY, player.posZ, SoundEvents.BLOCK_ANVIL_PLACE, SoundCategory.PLAYERS, (float)clientConfig.effectVolume, 1.0F);
               }
            }
         }

         Capabilities.getEntityProperties(attacked, (prop) -> {
            prop.sendInformationToPlayer(attacked);
         });
         return true;
      } else {
         return false;
      }
   }

   public boolean potionApplied(EntityLivingBase entity, PotionEffect effect, boolean cancel) {
      String AppliedEffect = effect.getPotion().getRegistryName().toString();
      String targetEffect = serverConfig.potionEffect;
      if (!AppliedEffect.contentEquals(targetEffect)) {
         return cancel;
      } else if (!entity.world.isRemote && serverConfig.resistance_stacks) {
         Potion resistance = Potion.getPotionFromResourceLocation(targetEffect);
         if (resistance == null) {
            resistance = MobEffects.RESISTANCE;
         }

         boolean has = entity.isPotionActive(resistance);
         int newAmp = effect.getAmplifier();
         int newDur = effect.getDuration();
         if (!has) {
            return cancel;
         } else {
            PotionEffect oldEffect = entity.getActivePotionEffect(resistance);
            int oldAmp = oldEffect.getAmplifier();
            int oldDur = oldEffect.getDuration();
            int amp = newAmp + 1;
            int duration = newDur;
            if (oldAmp > amp) {
               return true;
            } else {
               if (amp <= serverConfig.resistance_stacks_max) {
                  try {
                     PotionEffect e = new PotionEffect(resistance, duration, amp, false, false);
                     effect.combine(e);
                  } catch (Exception var17) {
                     ;
                  }
               }

               return false;
            }
         }
      } else {
         return cancel;
      }
   }

   public void onAbilityRemoved(EntityLivingBase entity) {
      Potion resistance = Potion.getPotionFromResourceLocation(serverConfig.potionEffect);
      if (resistance == null) {
         resistance = MobEffects.RESISTANCE;
      }

      if (entity.isPotionActive(resistance)) {
         entity.removePotionEffect(resistance);
         if (entity.world.isRemote) {
            entity.addPotionEffect(new PotionEffect(resistance, 30, serverConfig.resistance_level, false, false));
         }
      }

   }

   public void loadStorage(NBTTagCompound compound) {
      if (compound.hasKey("hitCount")) {
         this.hitCount = compound.getInteger("hitCount");
      }

   }

   public NBTTagCompound saveStorage(NBTTagCompound compound) {
      compound.setInteger("hitCount", this.hitCount);
      return compound;
   }

   static {
      clientConfig = TrinketsConfig.CLIENT.items.DAMAGE_SHIELD;
      serverConfig = TrinketsConfig.SERVER.Items.DAMAGE_SHIELD;
   }
}
