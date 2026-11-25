package cursedflames.bountifulbaubles.item;

import cursedflames.bountifulbaubles.BountifulBaubles;
import cursedflames.bountifulbaubles.item.base.GenericItemBB;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumHand;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ItemUmbrella extends GenericItemBB {
   public ItemUmbrella() {
      super("umbrella", BountifulBaubles.TAB);
      MinecraftForge.EVENT_BUS.register(this);
      this.setMaxStackSize(1);
   }

   @SubscribeEvent
   public void livingUpdate(LivingUpdateEvent event) {
      EntityLivingBase entity = event.getEntityLiving();
      if (entity instanceof EntityPlayer) {
         EntityPlayer player = (EntityPlayer)entity;
         if (player.getHeldItem(EnumHand.MAIN_HAND).getItem() == this || player.getHeldItem(EnumHand.OFF_HAND).getItem() == this) {
            if (player.motionY < -0.2D) {
               player.motionY = -0.2D;
            }

            if (player.fallDistance > 0.0F) {
               player.fallDistance = 0.0F;
            }

         }
      }
   }
}
