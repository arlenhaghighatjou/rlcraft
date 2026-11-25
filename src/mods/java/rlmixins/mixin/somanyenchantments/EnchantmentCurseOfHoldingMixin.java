package rlmixins.mixin.somanyenchantments;

import com.Shultrea.Rin.Ench0_4_0.EnchantmentCurseofHolding;
import com.Shultrea.Rin.Enchantments_Sector.Smc_040;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin({EnchantmentCurseofHolding.class})
public abstract class EnchantmentCurseOfHoldingMixin {
   public EnchantmentCurseOfHoldingMixin() {
   }

   @SubscribeEvent
   @Overwrite(
      remap = false
   )
   public void onExist(PlayerTickEvent e) {
      if (e.phase == Phase.START && e.player != null && !e.player.world.isRemote && e.player.ticksExisted % 9 == 0) {
         int level = EnchantmentHelper.getMaxEnchantmentLevel(Smc_040.CurseofHolding, e.player);
         if (level > 0) {
            e.player.addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, 10, level - 1, false, false));
            e.player.addPotionEffect(new PotionEffect(MobEffects.WEAKNESS, 10, level > 1 ? 1 : 0, false, false));
            e.player.addPotionEffect(new PotionEffect(MobEffects.UNLUCK, 10, level - 1, false, false));
            if (level > 1) {
               e.player.addPotionEffect(new PotionEffect(MobEffects.HUNGER, 10, level - 1, false, false));
               e.player.addPotionEffect(new PotionEffect(MobEffects.MINING_FATIGUE, 10, level - 1, false, false));
            }
         }
      }

   }
}
