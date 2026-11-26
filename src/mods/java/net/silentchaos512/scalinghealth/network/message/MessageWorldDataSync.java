package net.silentchaos512.scalinghealth.network.message;

import net.lax1dude.eaglercraft.v1_8.netty.ByteBuf;
import javax.annotation.Nullable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.silentchaos512.lib.event.ClientTicks;
import net.silentchaos512.scalinghealth.ScalingHealth;
import net.silentchaos512.scalinghealth.network.Message;
import net.silentchaos512.scalinghealth.world.ScalingHealthSavedData;

public class MessageWorldDataSync extends Message<MessageWorldDataSync> {
   public NBTTagCompound tags;

   public MessageWorldDataSync() {
   }

   public MessageWorldDataSync(ScalingHealthSavedData data) {
      this.tags = new NBTTagCompound();
      data.writeToNBT(this.tags);
   }

   @Nullable
   @SideOnly(Side.CLIENT)
   public void fromBytes(ByteBuf buf) {
      this.tags = readNBT(buf);
   }

   public void toBytes(ByteBuf buf) {
      writeNBT(this.tags, buf);
   }

   public IMessage handleMessage(MessageContext context) {
      ClientTicks.scheduleAction(() -> {
         EntityPlayer clientPlayer = ScalingHealth.proxy.getClientPlayer();
         if (clientPlayer != null) {
            ScalingHealthSavedData data = ScalingHealthSavedData.get(clientPlayer.world);
            if (data != null) {
               data.readFromNBT(this.tags);
            }
         }

      });
      return null;
   }
}
