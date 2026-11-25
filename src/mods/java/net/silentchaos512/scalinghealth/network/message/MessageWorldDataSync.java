package net.silentchaos512.scalinghealth.network.message;

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

public class MessageWorldDataSync extends Message {
   public NBTTagCompound tags;

   public MessageWorldDataSync() {
   }

   public MessageWorldDataSync(ScalingHealthSavedData data) {
      this.tags = new NBTTagCompound();
      data.writeToNBT(this.tags);
   }

   @Nullable
   @SideOnly(Side.CLIENT)
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
