package ichttt.mods.firstaid.common.damagesystem.debuff;

import ichttt.mods.firstaid.api.CapabilityExtendedHealthSystem;
import ichttt.mods.firstaid.api.damagesystem.AbstractDamageablePart;
import ichttt.mods.firstaid.api.damagesystem.AbstractPlayerDamageModel;
import ichttt.mods.firstaid.api.debuff.IDebuff;
import ichttt.mods.firstaid.api.enums.EnumDebuffSlot;
import ichttt.mods.firstaid.api.enums.EnumPlayerPart;
import java.util.Objects;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.EnumFacing;

public class SharedDebuff implements IDebuff {
   private final IDebuff debuff;
   private final EnumPlayerPart[] parts;
   private int damage;
   private int healingDone;
   private int damageCount;
   private int healingCount;

   public SharedDebuff(IDebuff debuff, EnumDebuffSlot slot) {
      if (slot.playerParts.length <= 1) {
         throw new IllegalArgumentException("Only slots with more then more parts can be wrapped by SharedDebuff!");
      } else {
         this.debuff = debuff;
         this.parts = slot.playerParts;
      }
   }

   public void handleDamageTaken(float damage, float healthPerMax, EntityPlayerMP player) {
      if (this.debuff.isEnabled()) {
         this.damage = (int)((float)this.damage + damage);
         ++this.damageCount;
      }

   }

   public void handleHealing(float healingDone, float healthPerMax, EntityPlayerMP player) {
      if (this.debuff.isEnabled()) {
         this.healingDone = (int)((float)this.healingDone + healingDone);
         ++this.healingCount;
      }

   }

   public void tick(EntityPlayer player) {
      if (this.debuff.isEnabled() && !player.world.isRemote && player instanceof EntityPlayerMP) {
         AbstractPlayerDamageModel damageModel = (AbstractPlayerDamageModel)Objects.requireNonNull(player.getCapability(CapabilityExtendedHealthSystem.INSTANCE, (EnumFacing)null));
         float healthPerMax = 0.0F;

         for(EnumPlayerPart part : this.parts) {
            AbstractDamageablePart damageablePart = damageModel.getFromEnum(part);
            healthPerMax += damageablePart.currentHealth / (float)damageablePart.getMaxHealth();
         }

         healthPerMax = healthPerMax / (float)this.parts.length;
         if (this.healingCount > 0) {
            this.healingDone /= this.healingCount;
            this.debuff.handleHealing((float)this.healingDone, healthPerMax, (EntityPlayerMP)player);
         }

         if (this.damageCount > 0) {
            this.damage /= this.damageCount;
            this.debuff.handleDamageTaken((float)this.damage, healthPerMax, (EntityPlayerMP)player);
         }

         this.healingDone = 0;
         this.damage = 0;
         this.damageCount = 0;
         this.healingCount = 0;
         this.debuff.update(player, healthPerMax);
      }
   }
}
