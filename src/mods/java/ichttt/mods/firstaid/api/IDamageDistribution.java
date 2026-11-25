package ichttt.mods.firstaid.api;

import javax.annotation.Nonnull;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;

public interface IDamageDistribution {
   float distributeDamage(float var1, @Nonnull EntityPlayer var2, @Nonnull DamageSource var3, boolean var4);

   default boolean skipGlobalPotionModifiers() {
      return false;
   }
}
