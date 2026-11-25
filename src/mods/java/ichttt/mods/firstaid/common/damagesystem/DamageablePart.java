package ichttt.mods.firstaid.common.damagesystem;

import ichttt.mods.firstaid.FirstAid;
import ichttt.mods.firstaid.FirstAidConfig;
import ichttt.mods.firstaid.api.damagesystem.AbstractDamageablePart;
import ichttt.mods.firstaid.api.damagesystem.AbstractPartHealer;
import ichttt.mods.firstaid.api.debuff.IDebuff;
import ichttt.mods.firstaid.api.enums.EnumPlayerPart;
import ichttt.mods.firstaid.common.apiimpl.FirstAidRegistryImpl;
import ichttt.mods.firstaid.common.items.FirstAidItems;
import java.util.Arrays;
import java.util.Objects;
import java.util.function.Consumer;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class DamageablePart extends AbstractDamageablePart {
   private int maxHealth;
   @Nonnull
   private final IDebuff[] debuffs;
   private float absorption;

   public DamageablePart(int maxHealth, boolean canCauseDeath, @Nonnull EnumPlayerPart playerPart, @Nonnull IDebuff... debuffs) {
      super(maxHealth, canCauseDeath, playerPart);
      this.maxHealth = maxHealth;
      this.currentHealth = (float)maxHealth;
      this.debuffs = debuffs;
   }

   public float heal(float amount, @Nullable EntityPlayer player, boolean applyDebuff) {
      if (amount <= 0.0F) {
         return 0.0F;
      } else {
         float notFitting = Math.abs(Math.min(0.0F, (float)this.maxHealth - (this.currentHealth + amount)));
         this.currentHealth = Math.min((float)this.maxHealth, this.currentHealth + amount);
         if (notFitting > 0.0F) {
            float oldHealth = this.currentHealth;
            this.currentHealth = Math.min(this.currentHealth + notFitting, this.currentHealth);
            notFitting -= this.currentHealth - oldHealth;
         }

         if (applyDebuff) {
            Objects.requireNonNull(player, "Got null player with applyDebuff = true");
            Arrays.stream(this.debuffs).forEach((debuff) -> {
               debuff.handleHealing(amount - finalNotFitting, this.currentHealth / (float)this.maxHealth, (EntityPlayerMP)player);
            });
         }

         return notFitting;
      }
   }

   public float damage(float amount, @Nullable EntityPlayer player, boolean applyDebuff) {
      return this.damage(amount, player, applyDebuff, 0.0F);
   }

   public float damage(float amount, @Nullable EntityPlayer player, boolean applyDebuff, float minHealth) {
      if (amount <= 0.0F) {
         return 0.0F;
      } else if (minHealth > (float)this.maxHealth) {
         throw new IllegalArgumentException("Cannot damage part with minHealth " + minHealth + " while he has more max health (" + this.maxHealth + ")");
      } else {
         float origAmount = amount;
         if (this.absorption > 0.0F) {
            amount = Math.abs(Math.min(0.0F, this.absorption - amount));
            this.absorption = Math.max(0.0F, this.absorption - origAmount);
         }

         float notFitting = Math.abs(Math.min(minHealth, this.currentHealth - amount) - minHealth);
         this.currentHealth = Math.max(minHealth, this.currentHealth - amount);
         if (applyDebuff) {
            Objects.requireNonNull(player, "Got null player with applyDebuff = true");
            Arrays.stream(this.debuffs).forEach((debuff) -> {
               debuff.handleDamageTaken(origAmount - notFitting, this.currentHealth / (float)this.maxHealth, (EntityPlayerMP)player);
            });
         }

         return notFitting;
      }
   }

   public void tick(World world, EntityPlayer player, boolean tickDebuffs) {
      if (this.activeHealer != null) {
         if (this.activeHealer.tick()) {
            this.heal(1.0F, player, !world.isRemote);
         }

         if (this.activeHealer.hasFinished()) {
            this.activeHealer = null;
         }
      }

      if (!world.isRemote && tickDebuffs) {
         Arrays.stream(this.debuffs).forEach((debuff) -> {
            debuff.update(player, this.currentHealth / (float)this.maxHealth);
         });
      }

   }

   public NBTTagCompound serializeNBT() {
      NBTTagCompound compound = new NBTTagCompound();
      compound.setFloat("health", this.currentHealth);
      if (FirstAidConfig.scaleMaxHealth) {
         compound.setInteger("maxHealth", this.maxHealth);
      }

      if (this.absorption > 0.0F) {
         compound.setFloat("absorption", this.absorption);
      }

      if (this.activeHealer != null) {
         compound.setTag("healer", this.activeHealer.stack.serializeNBT());
         compound.setInteger("itemTicks", this.activeHealer.getTicksPassed());
         compound.setInteger("itemHeals", this.activeHealer.getHealsDone());
      }

      return compound;
   }

   public void deserializeNBT(@Nullable NBTTagCompound nbt) {
      if (nbt != null) {
         if (nbt.hasKey("maxHealth") && FirstAidConfig.scaleMaxHealth) {
            this.maxHealth = nbt.getInteger("maxHealth");
         }

         this.currentHealth = Math.min((float)this.maxHealth, nbt.getFloat("health"));
         ItemStack stack = null;
         if (nbt.hasKey("healingItem")) {
            stack = new ItemStack(nbt.getByte("healingItem") == 1 ? FirstAidItems.PLASTER : FirstAidItems.BANDAGE);
         } else if (nbt.hasKey("healer")) {
            stack = new ItemStack((NBTTagCompound)nbt.getTag("healer"));
         }

         if (stack != null) {
            AbstractPartHealer healer = FirstAidRegistryImpl.INSTANCE.getPartHealer(stack);
            if (healer == null) {
               FirstAid.LOGGER.warn("Failed to lookup healer for item {}", stack.getItem());
            } else {
               this.activeHealer = healer.loadNBT(nbt.getInteger("itemTicks"), nbt.getInteger("itemHeals"));
            }
         }

         if (nbt.hasKey("absorption")) {
            this.absorption = nbt.getFloat("absorption");
         }

         Arrays.stream(this.debuffs).forEach((debuff) -> {
            debuff.handleHealing(0.0F, this.currentHealth / (float)this.maxHealth, (EntityPlayerMP)null);
         });
      }
   }

   public void setAbsorption(float absorption) {
      if (absorption > 4.0F && FirstAidConfig.capMaxHealth) {
         absorption = 4.0F;
      }

      if (absorption > 32.0F) {
         absorption = 32.0F;
      }

      this.absorption = absorption;
      this.currentHealth = Math.min((float)this.maxHealth + absorption, this.currentHealth);
   }

   public float getAbsorption() {
      return this.absorption;
   }

   public void setMaxHealth(int maxHealth) {
      if (maxHealth > 12 && FirstAidConfig.capMaxHealth) {
         maxHealth = 12;
      }

      if (maxHealth > 128) {
         maxHealth = 128;
      }

      this.maxHealth = Math.max(2, maxHealth);
      this.currentHealth = Math.min(this.currentHealth, (float)this.maxHealth);
   }

   public int getMaxHealth() {
      return this.maxHealth;
   }
}
