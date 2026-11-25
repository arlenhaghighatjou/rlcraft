package net.silentchaos512.scalinghealth.network.message;

import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.silentchaos512.lib.event.ClientTicks;
import net.silentchaos512.scalinghealth.event.BlightHandler;
import net.silentchaos512.scalinghealth.network.Message;

public class MessageMarkBlight extends Message {
   public int entityId;
   public boolean isBlight;

   public MessageMarkBlight() {
   }

   /** @deprecated */
   @Deprecated
   public MessageMarkBlight(EntityLivingBase entity) {
      this(entity, true);
   }

   public MessageMarkBlight(EntityLivingBase entity, boolean isBlight) {
      this.entityId = entity.getEntityId();
      this.isBlight = isBlight;
   }

   @Nullable
   @SideOnly(Side.CLIENT)
   public IMessage handleMessage(MessageContext context) {
      ClientTicks.scheduleAction(() -> {
         Minecraft mc = Minecraft.getMinecraft();
         if (mc != null && mc.world != null) {
            Entity entity = mc.world.getEntityByID(this.entityId);
            if (entity instanceof EntityLivingBase) {
               BlightHandler.markBlight((EntityLivingBase)entity, this.isBlight);
            }

         }
      });
      return null;
   }
}
