package vazkii.quark.tweaks.feature;

import java.util.function.Predicate;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.init.Items;
import net.minecraft.item.ItemShears;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.EntityInteract;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import vazkii.quark.base.module.Feature;

public class ShearableChickens extends Feature {
   private static final String TAG_SHEARED = "quark:sheared";

   public ShearableChickens() {
   }

   public boolean hasSubscriptions() {
      return true;
   }

   @SubscribeEvent
   public void onEntityInteract(EntityInteract event) {
      Entity target = event.getTarget();
      if (target instanceof EntityChicken && !target.isDead && ((EntityChicken)target).hurtTime == 0) {
         ItemStack stack = event.getEntityPlayer().getHeldItemMainhand();
         if (stack.isEmpty() || !(stack.getItem() instanceof ItemShears)) {
            stack = event.getEntityPlayer().getHeldItemOffhand();
         }

         if (!stack.isEmpty() && stack.getItem() instanceof ItemShears) {
            if (!event.getWorld().isRemote) {
               EntityItem item = new EntityItem(event.getEntity().getEntityWorld(), target.posX, target.posY, target.posZ, new ItemStack(Items.FEATHER, 1));
               event.getWorld().spawnEntity(item);
            }

            target.attackEntityFrom(DamageSource.GENERIC, 1.0F);
            target.getEntityData().setBoolean("quark:sheared", true);
            stack.damageItem(1, event.getEntityPlayer());
            event.getEntityPlayer().swingArm(event.getHand());
         }
      }

   }

   @SubscribeEvent
   public void onEntityDrops(LivingDropsEvent event) {
      if (event.getEntity().getEntityData().getBoolean("quark:sheared")) {
         event.getDrops().removeIf((e) -> {
            return e.getItem().getItem() == Items.FEATHER;
         });
      }

   }
}
