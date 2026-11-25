package xzeroair.trinkets.capabilities.magic;

import net.lax1dude.eaglercraft.v1_8.EaglercraftUUID;
import java.util.function.BiFunction;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextComponentString;
import xzeroair.trinkets.attributes.MagicAttributes;
import xzeroair.trinkets.attributes.UpdatingAttribute;
import xzeroair.trinkets.capabilities.Capabilities;
import xzeroair.trinkets.capabilities.CapabilityBase;
import xzeroair.trinkets.init.EntityRaces;
import xzeroair.trinkets.network.NetworkHandler;
import xzeroair.trinkets.network.mana.SyncManaCostToHudPacket;
import xzeroair.trinkets.network.mana.SyncManaStatsPacket;
import xzeroair.trinkets.races.EntityRace;
import xzeroair.trinkets.util.TrinketsConfig;
import xzeroair.trinkets.util.config.mana.EntityManaConfig;

public class MagicStats extends CapabilityBase<MagicStats, EntityLivingBase> {
   private static final EntityManaConfig manaConfig;
   private float mana = 100.0F;
   private float maxMana = 100.0F;
   private double bonusMana = 0.0D;
   private boolean sync = false;
   private double manaUpdateTickRate = 0.0D;
   private double manaRegenTimeout = 0.0D;
   private UpdatingAttribute MANA_BONUS = (new UpdatingAttribute("BonusMax", EaglercraftUUID.fromString("a3b8802c-e521-45c0-b126-eb45692f68eb"), MagicAttributes.MAX_MANA)).setSavedInNBT(true);

   public MagicStats(EntityLivingBase e) {
      super(e);
   }

   public NBTTagCompound getTag() {
      NBTTagCompound tag = ((EntityLivingBase)this.object).getEntityData();
      if (tag != null) {
         NBTTagCompound persistentData;
         if (this.object instanceof EntityPlayer) {
            if (!tag.hasKey("PlayerPersisted")) {
               tag.setTag("PlayerPersisted", new NBTTagCompound());
            }

            persistentData = tag.getCompoundTag("PlayerPersisted");
         } else {
            persistentData = tag;
         }

         String capTag = "xat.magicstats";
         if (!persistentData.hasKey("xat.magicstats")) {
            persistentData.setTag("xat.magicstats", new NBTTagCompound());
         }

         return persistentData.getCompoundTag("xat.magicstats");
      } else {
         return super.getTag();
      }
   }

   public boolean onRegenCooldown() {
      if (this.manaRegenTimeout > 0.0D) {
         --this.manaRegenTimeout;
         return true;
      } else {
         this.manaRegenTimeout = 0.0D;
         return false;
      }
   }

   public void onUpdate() {
      if (this.MANA_BONUS != null) {
         float bonusPerPoint = TrinketsConfig.SERVER.mana.bonus;
         if (bonusPerPoint > 0.0F) {
            double bonus = this.getBonusMana();
            float maxBonus = TrinketsConfig.SERVER.mana.cap_bonus;
            double amount = maxBonus > 0.0F ? Math.min((double)bonusPerPoint * bonus, (double)maxBonus) : (double)bonusPerPoint * bonus;
            this.MANA_BONUS.addModifier((EntityLivingBase)this.object, amount, 0);
         } else {
            this.MANA_BONUS.removeModifier((EntityLivingBase)this.object);
         }
      }

      if (((EntityLivingBase)this.object).getEntityWorld().isRemote) {
         this.sync = false;
      } else {
         boolean isCreative = this.object instanceof EntityPlayer && ((EntityPlayer)this.object).isCreative();
         if (TrinketsConfig.SERVER.mana.mana_enabled && !isCreative) {
            if (!this.onRegenCooldown()) {
               if (this.getMana() > this.getMaxMana()) {
                  this.setMana(this.getMaxMana());
               }

               if (this.getMana() < this.getMaxMana()) {
                  ++this.manaUpdateTickRate;
                  IAttributeInstance cooldown = ((EntityLivingBase)this.object).getAttributeMap().getAttributeInstance(MagicAttributes.regenCooldown);
                  double cooldownMulti = cooldown != null ? cooldown.getAttributeValue() : 1.0D;
                  if (this.manaUpdateTickRate > (double)manaConfig.mana_update_ticks * cooldownMulti) {
                     IAttributeInstance attribute = ((EntityLivingBase)this.object).getAttributeMap().getAttributeInstance(MagicAttributes.regen);
                     this.addMana(attribute != null ? (float)attribute.getAttributeValue() : 1.0F);
                     this.manaUpdateTickRate = 0.0D;
                  }
               }

               if (this.sync) {
                  this.sync = false;
                  this.refillMana();
               }

            }
         } else {
            this.refillMana();
         }
      }
   }

   public void refillMana() {
      if (this.getMana() != this.getMaxMana()) {
         this.setMana(this.getMaxMana());
      }

   }

   public float getMana() {
      return this.mana;
   }

   public void setMana(float mana) {
      if (!((EntityLivingBase)this.object).world.isRemote) {
         if (mana > this.getMaxMana()) {
            this.mana = this.getMaxMana();
         } else if (mana < 0.0F) {
            this.mana = 0.0F;
         } else {
            this.mana = mana;
         }

         this.sendManaToPlayer((EntityLivingBase)this.object);
      }

   }

   public void addMana(float mana) {
      this.setMana(this.mana + mana);
   }

   public boolean spendMana(float cost) {
      boolean isCreative = this.object instanceof EntityPlayer && ((EntityPlayer)this.object).isCreative();
      boolean spend = true;
      if (!isCreative && TrinketsConfig.SERVER.mana.mana_enabled) {
         if (cost <= 0.0F) {
            return true;
         } else {
            if (cost <= this.getMana()) {
               spend = true;
            } else {
               spend = false;
               if (this.object instanceof EntityPlayer && ((EntityLivingBase)this.object).world.isRemote) {
                  String Message = "No MP";
                  ((EntityPlayer)this.object).sendStatusMessage(new TextComponentString("No MP"), true);
               }
            }

            if (spend) {
               this.setMana(this.mana - cost);
               this.setManaRegenTimeout();
            }

            return spend;
         }
      } else {
         return true;
      }
   }

   public float getMaxMana() {
      IAttributeInstance maxMana = ((EntityLivingBase)this.object).getEntityAttribute(MagicAttributes.MAX_MANA);
      if (maxMana != null) {
         float max = (float)maxMana.getAttributeValue();
         float maxAffinityBonus = (float)(maxMana.getBaseValue() * (double)((float)this.getRacialAffinity() * 0.01F) - maxMana.getBaseValue());
         if (manaConfig.mana_cap) {
            return manaConfig.cap_affinity ? MathHelper.clamp(max, 0.0F, manaConfig.mana_max) : MathHelper.clamp(max, 0.0F, manaConfig.mana_max) + maxAffinityBonus;
         } else {
            return Math.max(max + maxAffinityBonus, 0.0F);
         }
      } else {
         return 100.0F;
      }
   }

   public double getBonusMana() {
      return this.bonusMana;
   }

   public boolean needMana() {
      return this.mana < this.getMaxMana();
   }

   public void setBonusMana(double bonus) {
      if (bonus < 0.0D) {
         this.bonusMana = 0.0D;
      } else {
         this.bonusMana = bonus;
      }

      this.sendManaToPlayer((EntityLivingBase)this.object);
   }

   public void setManaRegenTimeout() {
      IAttributeInstance attribute = ((EntityLivingBase)this.object).getAttributeMap().getAttributeInstance(MagicAttributes.regenCooldown);
      double cooldownMulti = attribute != null ? attribute.getAttributeValue() : 1.0D;
      this.setManaRegenTimeout((int)((double)manaConfig.mana_regen_timeout * cooldownMulti));
   }

   public void setManaRegenTimeout(int timeout) {
      this.manaRegenTimeout = (double)timeout;
   }

   public int getMagicAffinity() {
      IAttributeInstance affinity = ((EntityLivingBase)this.object).getEntityAttribute(MagicAttributes.affinity);
      if (affinity != null) {
         int amount = (int)affinity.getAttributeValue();
         amount = amount + (this.getRacialAffinity() - 100);
         return amount;
      } else {
         return 100;
      }
   }

   public int getRacialAffinity() {
      return ((EntityRace)Capabilities.getEntityProperties((Entity)this.object, EntityRaces.none, (prop, r) -> {
         return prop.getCurrentRace();
      })).getMagicAffinity();
   }

   public void sendManaToPlayer(EntityLivingBase e) {
      if (e instanceof EntityPlayerMP) {
         NetworkHandler.sendTo(new SyncManaStatsPacket((EntityLivingBase)this.object, this), (EntityPlayerMP)e);
      }

   }

   public void syncToManaCostToHud(float cost) {
      if (this.object instanceof EntityPlayer && !((EntityLivingBase)this.object).world.isRemote) {
         NetworkHandler.sendTo(new SyncManaCostToHudPacket(cost), (EntityPlayerMP)this.object);
      }

   }

   public void copyFrom(MagicStats stats, boolean wasDeath, boolean keepInv) {
      this.bonusMana = stats.bonusMana;
      if (wasDeath) {
         if (keepInv) {
            this.mana = stats.mana;
         } else {
            this.mana = this.getMaxMana();
         }
      } else {
         this.mana = stats.mana;
      }

      this.sync = true;
   }

   public NBTTagCompound saveToNBT(NBTTagCompound tag) {
      tag.setFloat("mana", this.getMana());
      tag.setFloat("max_mana", this.getMaxMana());
      tag.setDouble("bonus_mana", this.getBonusMana());
      return tag;
   }

   public void loadFromNBT(NBTTagCompound tag) {
      if (tag.hasKey("mana")) {
         this.mana = tag.getFloat("mana");
      }

      if (tag.hasKey("max_mana")) {
         this.maxMana = tag.getFloat("max_mana");
      }

      if (tag.hasKey("bonus_mana")) {
         this.bonusMana = tag.getDouble("bonus_mana");
      }

   }

   static {
      manaConfig = TrinketsConfig.SERVER.mana;
   }
}
