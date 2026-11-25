package ichttt.mods.firstaid.common.damagesystem.distribution;

import ichttt.mods.firstaid.api.CapabilityExtendedHealthSystem;
import ichttt.mods.firstaid.api.IDamageDistribution;
import ichttt.mods.firstaid.api.damagesystem.AbstractPlayerDamageModel;
import ichttt.mods.firstaid.api.enums.EnumPlayerPart;
import javax.annotation.Nonnull;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;

public class DirectDamageDistribution implements IDamageDistribution {
   private final EnumPlayerPart part;
   private final boolean debuff;

   public DirectDamageDistribution(EnumPlayerPart part, boolean debuff) {
      this.part = part;
      this.debuff = debuff;
   }

   public float distributeDamage(float damage, @Nonnull EntityPlayer player, @Nonnull DamageSource source, boolean addStat) {
      return ((AbstractPlayerDamageModel)player.getCapability(CapabilityExtendedHealthSystem.INSTANCE, (EnumFacing)null)).getFromEnum(this.part).damage(damage, player, this.debuff);
   }
}
