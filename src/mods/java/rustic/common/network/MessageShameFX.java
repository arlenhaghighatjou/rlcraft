package rustic.common.network;

import net.lax1dude.eaglercraft.v1_8.netty.ByteBuf;
import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import net.minecraft.client.Minecraft;
import net.minecraft.item.Item;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import rustic.common.items.ModItems;

public class MessageShameFX implements IMessage {
   public static EaglercraftRandom rand = new EaglercraftRandom();
   int num = 0;
   double x = 0.0D;
   double y = 0.0D;
   double z = 0.0D;
   double xVel = 0.0D;
   double yVel = 0.0D;
   double zVel = 0.0D;
   double width = 0.0D;
   double height = 0.0D;

   public MessageShameFX() {
   }

   public MessageShameFX(int num, double x, double y, double z, double xVel, double yVel, double zVel, double width, double height) {
      this.num = num;
      this.x = x;
      this.y = y;
      this.z = z;
      this.xVel = xVel;
      this.yVel = yVel;
      this.zVel = zVel;
      this.width = width;
      this.height = height;
   }

   public void fromBytes(ByteBuf buf) {
      this.num = buf.readInt();
      this.x = buf.readDouble();
      this.y = buf.readDouble();
      this.z = buf.readDouble();
      this.xVel = buf.readDouble();
      this.yVel = buf.readDouble();
      this.zVel = buf.readDouble();
      this.width = buf.readDouble();
      this.height = buf.readDouble();
   }

   public void toBytes(ByteBuf buf) {
      buf.writeInt(this.num);
      buf.writeDouble(this.x);
      buf.writeDouble(this.y);
      buf.writeDouble(this.z);
      buf.writeDouble(this.xVel);
      buf.writeDouble(this.yVel);
      buf.writeDouble(this.zVel);
      buf.writeDouble(this.width);
      buf.writeDouble(this.height);
   }

   public static class MessageHolder implements IMessageHandler<MessageShameFX, IMessage> {
      public MessageHolder() {
      }

      @SideOnly(Side.CLIENT)
      public IMessage onMessage(MessageShameFX message, MessageContext ctx) {
         World world = Minecraft.getMinecraft().world;
         if (world.isRemote) {
            for(int i = 0; i < message.num; ++i) {
               double x = message.x + message.xVel;
               double y = message.y + message.height * MessageShameFX.rand.nextDouble() + message.yVel;
               double z = message.z + message.zVel;
               switch(MessageShameFX.rand.nextInt(4)) {
               case 1:
                  z = z + message.width * (MessageShameFX.rand.nextDouble() - 0.5D);
                  x = x - message.width * 0.5D;
                  break;
               case 2:
                  x = x + message.width * (MessageShameFX.rand.nextDouble() - 0.5D);
                  z = z + message.width * 0.5D;
                  break;
               case 3:
                  x = x + message.width * (MessageShameFX.rand.nextDouble() - 0.5D);
                  z = z - message.width * 0.5D;
                  break;
               default:
                  z = z + message.width * (MessageShameFX.rand.nextDouble() - 0.5D);
                  x = x + message.width * 0.5D;
               }

               world.spawnParticle(EnumParticleTypes.ITEM_CRACK, true, x, y, z, message.xVel, message.yVel, message.zVel, new int[]{Item.getIdFromItem(ModItems.TOMATO)});
            }
         }

         return null;
      }
   }
}
