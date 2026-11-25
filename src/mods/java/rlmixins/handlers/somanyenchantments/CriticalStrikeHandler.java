package rlmixins.handlers.somanyenchantments;

import bettercombat.mod.event.RLCombatCriticalHitEvent;
import com.Shultrea.Rin.Enchantments_Sector.Smc_030;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.SoundCategory;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.eventhandler.Event.Result;
import rlmixins.handlers.ModRegistry;

public class CriticalStrikeHandler {
   public CriticalStrikeHandler() {
   }

   @SubscribeEvent(
      priority = EventPriority.LOW
   )
   public static void onCritical(RLCombatCriticalHitEvent event) {
      EntityPlayer player = event.getEntityPlayer();
      if (player != null) {
         ItemStack stack = event.getOffhand() ? player.getHeldItemOffhand() : player.getHeldItemMainhand();
         if (!stack.isEmpty() && (event.getResult() == Result.ALLOW || event.isVanillaCritical() && event.getResult() == Result.DEFAULT)) {
            int csLevel = EnchantmentHelper.getEnchantmentLevel(Smc_030.CriticalStrike, stack);
            if (csLevel > 0) {
               if (!stack.hasTagCompound()) {
                  stack.setTagCompound(new NBTTagCompound());
               }

               int counter = stack.getTagCompound().getInteger("failedCritCount");
               int maxReduction = csLevel * 50;
               if (player.world.rand.nextInt(1000 - maxReduction) >= 32 * (counter + 1)) {
                  stack.getTagCompound().setInteger("failedCritCount", counter + 1);
               } else {
                  stack.getTagCompound().setInteger("failedCritCount", 0);
                  float crit = 0.4F + (float)csLevel * 0.4F + player.world.rand.nextFloat() * 0.5F;
                  player.world.playSound((EntityPlayer)null, player.posX, player.posY, player.posZ, ModRegistry.CRITICAL_STRIKE, SoundCategory.PLAYERS, 0.8F, 1.0F / (player.world.rand.nextFloat() * 0.4F + 1.2F) * 1.6F);
                  event.setDamageModifier(event.getDamageModifier() + crit);
               }
            }
         }

      }
   }
}
