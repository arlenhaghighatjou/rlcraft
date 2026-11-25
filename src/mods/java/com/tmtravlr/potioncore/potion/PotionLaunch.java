package com.tmtravlr.potioncore.potion;

import com.tmtravlr.potioncore.PotionCore;
import com.tmtravlr.potioncore.network.SToCMessage;
import io.netty.buffer.Unpooled;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.PacketBuffer;

public class PotionLaunch extends PotionCorePotion {
   public static final String NAME = "launch";
   public static final PotionLaunch INSTANCE = new PotionLaunch();
   public static float launchSpeed = 1.0F;

   public PotionLaunch() {
      super("launch", true, 65280);
   }

   public boolean isInstant() {
      return true;
   }

   public void performEffect(EntityLivingBase entity, int amplifier) {
      entity.motionY = (double)((float)(amplifier + 1) * launchSpeed);
      if (entity instanceof EntityPlayerMP) {
         PacketBuffer out = new PacketBuffer(Unpooled.buffer());
         out.writeInt(2);
         out.writeInt(amplifier + 1);
         SToCMessage packet = new SToCMessage(out);
         PotionCore.networkWrapper.sendTo(packet, (EntityPlayerMP)entity);
      }

   }
}
