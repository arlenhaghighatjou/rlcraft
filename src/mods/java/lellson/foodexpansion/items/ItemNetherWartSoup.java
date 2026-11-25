package lellson.foodexpansion.items;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;

public class ItemNetherWartSoup extends ItemFoodBasic {
   public ItemNetherWartSoup() {
      super("itemNetherWartSoup", 4, 0.4F, false, new ItemStack(Items.BOWL));
      this.setAlwaysEdible();
   }

   public ItemStack onItemUseFinish(ItemStack item, World world, EntityLivingBase player) {
      player.setFire(5);
      if (!world.isRemote) {
         List<PotionEffect> effects = new ArrayList<>(player.getActivePotionEffects());
         for (int i = 0; i < effects.size(); ++i) {
            PotionEffect effect = effects.get(i);
            if (effect.getPotion().isBadEffect()) {
               player.removePotionEffect(effect.getPotion());
            }
         }
      }

      return super.onItemUseFinish(item, world, player);
   }
}
