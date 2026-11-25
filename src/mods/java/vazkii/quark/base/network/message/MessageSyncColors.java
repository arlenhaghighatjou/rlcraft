package vazkii.quark.base.network.message;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vazkii.arl.network.NetworkMessage;
import vazkii.arl.util.ClientTicker;
import vazkii.quark.world.effects.PotionColorizer;

public class MessageSyncColors extends NetworkMessage<MessageSyncColors> {
   public int entityID;
   public double r;
   public double g;
   public double b;

   public MessageSyncColors() {
   }

   public MessageSyncColors(int entity, double r, double g, double b) {
      this.entityID = entity;
      this.r = r;
      this.g = g;
      this.b = b;
   }

   @SideOnly(Side.CLIENT)
   public IMessage handleMessage(MessageContext context) {
      ClientTicker.addAction(() -> {
         World world = Minecraft.getMinecraft().world;
         if (world != null) {
            Entity byId = world.getEntityByID(this.entityID);
            if (byId instanceof EntityLivingBase) {
               PotionColorizer.receiveColors((EntityLivingBase)byId, this.r, this.g, this.b);
            }
         }

      });
      return null;
   }
}
