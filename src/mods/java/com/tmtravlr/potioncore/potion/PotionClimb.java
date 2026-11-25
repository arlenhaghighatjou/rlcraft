package com.tmtravlr.potioncore.potion;

import com.tmtravlr.potioncore.PotionCore;
import com.tmtravlr.potioncore.network.CToSMessage;
import net.lax1dude.eaglercraft.v1_8.netty.Unpooled;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.PacketBuffer;

public class PotionClimb extends PotionCorePotion {
   public static final String NAME = "climb";
   public static final PotionClimb INSTANCE = new PotionClimb();

   public PotionClimb() {
      super("climb", false, 13391104);
   }

   public void performEffect(EntityLivingBase entity, int amplifier) {
      if (entity.collidedHorizontally) {
         if (!entity.isSneaking()) {
            if (entity.moveForward > 0.0F && entity.motionY < 0.2D) {
               entity.motionY = 0.2D;
            }
         } else {
            entity.motionY = 0.0D;
         }

         entity.fallDistance = 0.0F;
         if (entity.world.isRemote && entity instanceof EntityPlayer && entity.ticksExisted % 21 == 0) {
            PacketBuffer out = new PacketBuffer(Unpooled.buffer());
            out.writeInt(1);
            out.writeLong(((EntityPlayer)entity).getUniqueID().getMostSignificantBits());
            out.writeLong(((EntityPlayer)entity).getUniqueID().getLeastSignificantBits());
            CToSMessage packet = new CToSMessage(out);
            PotionCore.networkWrapper.sendToServer(packet);
         }
      }

   }
}
