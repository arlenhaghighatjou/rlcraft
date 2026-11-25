package cursedflames.bountifulbaubles.item;

import cursedflames.bountifulbaubles.potion.ModPotions;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class ItemAmuletSinPride extends ItemAmuletSin {
   public ItemAmuletSinPride() {
      super("amuletSinPride", "amulet_sin_pride");
   }

   public void onWornTick(ItemStack stack, EntityLivingBase entity) {
      if (entity instanceof EntityPlayer) {
         EntityPlayer player = (EntityPlayer)entity;
         boolean hasEffect = player.isPotionActive(ModPotions.sin);
         if ((double)player.getHealth() + 0.5D > (double)player.getMaxHealth()) {
            if (!hasEffect) {
               addEffect(player, 0, Integer.MAX_VALUE, false);
            }
         } else if (hasEffect) {
            player.removePotionEffect(ModPotions.sin);
         }
      }

   }

   public void onUnequipped(ItemStack stack, EntityLivingBase entity) {
      entity.removePotionEffect(ModPotions.sin);
   }
}
