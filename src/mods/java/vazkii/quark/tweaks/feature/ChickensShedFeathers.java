package vazkii.quark.tweaks.feature;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import vazkii.quark.base.module.Feature;

public class ChickensShedFeathers extends Feature {
   public static boolean chicksDropFeathers;
   public static boolean dropAtLeastOne;
   public static int dropFreq;

   public ChickensShedFeathers() {
   }

   public void setupConfig() {
      chicksDropFeathers = this.loadPropBool("Chicks drop feathers", "", true);
      dropAtLeastOne = this.loadPropBool("Force at least one feather on kill", "", true);
      dropFreq = this.loadPropInt("Drop frequency (lower means more)", "", 28000);
   }

   @SubscribeEvent
   public void onLivingUpdate(LivingUpdateEvent event) {
      if (!event.getEntity().getEntityWorld().isRemote && event.getEntity() instanceof EntityChicken) {
         EntityChicken chicken = (EntityChicken)event.getEntity();
         if ((chicksDropFeathers || !chicken.isChild()) && chicken.getEntityWorld().rand.nextInt(dropFreq) == 0) {
            chicken.dropItem(Items.FEATHER, 1);
         }

      }
   }

   @SubscribeEvent
   public void onLivingDrops(LivingDropsEvent event) {
      if (dropAtLeastOne && !event.getEntity().getEntityWorld().isRemote && event.getEntity() instanceof EntityChicken && (((EntityChicken)event.getEntity()).isChild() || chicksDropFeathers)) {
         EntityChicken chicken = (EntityChicken)event.getEntity();
         boolean hasFeather = false;

         for(EntityItem item : event.getDrops()) {
            if (!item.getItem().isEmpty() && item.getItem().getItem().equals(Items.FEATHER)) {
               hasFeather = true;
               break;
            }
         }

         if (!hasFeather) {
            event.getDrops().add(new EntityItem(event.getEntity().getEntityWorld(), chicken.posX, chicken.posY, chicken.posZ, new ItemStack(Items.FEATHER, 1)));
         }

      }
   }

   public boolean hasSubscriptions() {
      return true;
   }
}
