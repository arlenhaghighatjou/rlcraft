package vazkii.quark.tweaks.feature;

import net.minecraft.entity.item.EntityArmorStand;
import net.minecraftforge.event.entity.EntityEvent.EntityConstructing;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import vazkii.quark.base.module.Feature;

public class ArmedArmorStands extends Feature {
   public ArmedArmorStands() {
   }

   @SubscribeEvent
   public void entityConstruct(EntityConstructing event) {
      if (event.getEntity() instanceof EntityArmorStand) {
         EntityArmorStand stand = (EntityArmorStand)event.getEntity();
         if (!stand.getShowArms()) {
            this.setShowArms(stand, true);
         }
      }

   }

   private void setShowArms(EntityArmorStand e, boolean showArms) {
      e.getDataManager().set(EntityArmorStand.STATUS, Byte.valueOf(this.setBit(((Byte)e.getDataManager().get(EntityArmorStand.STATUS)).byteValue(), 4, showArms)));
   }

   private byte setBit(byte status, int bitFlag, boolean value) {
      if (value) {
         status = (byte)(status | bitFlag);
      } else {
         status = (byte)(status & ~bitFlag);
      }

      return status;
   }

   public boolean hasSubscriptions() {
      return true;
   }
}
