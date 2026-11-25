package vazkii.quark.misc.feature;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntitySnowman;
import net.minecraft.entity.monster.EntityWitch;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import vazkii.arl.util.ItemNBTHelper;
import vazkii.quark.base.module.Feature;

public class SnowGolemPlayerHeads extends Feature {
   public SnowGolemPlayerHeads() {
   }

   @SubscribeEvent
   public void onDrops(LivingDropsEvent event) {
      Entity e = event.getEntity();
      if (e.hasCustomName() && e instanceof EntitySnowman && event.getSource().getTrueSource() != null && event.getSource().getTrueSource() instanceof EntityWitch) {
         EntitySnowman snowman = (EntitySnowman)e;
         if (snowman.isPumpkinEquipped()) {
            ItemStack stack = new ItemStack(Items.SKULL, 1, 3);
            ItemNBTHelper.setString(stack, "SkullOwner", e.getCustomNameTag());
            event.getDrops().add(new EntityItem(e.getEntityWorld(), e.posX, e.posY, e.posZ, stack));
         }
      }

   }

   public boolean hasSubscriptions() {
      return true;
   }

   public String getFeatureIngameConfigName() {
      return "Snow Golem Player Heads";
   }
}
