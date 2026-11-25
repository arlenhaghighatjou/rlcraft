package vazkii.quark.base.network.message;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vazkii.arl.network.NetworkMessage;
import vazkii.arl.util.ClientTicker;
import vazkii.quark.client.feature.ItemsFlashBeforeExpiring;

public class MessageItemUpdate extends NetworkMessage<MessageItemUpdate> {
   public int id;
   public int age;
   public int lifespan;

   public MessageItemUpdate() {
   }

   public MessageItemUpdate(int id, int age, int lifespan) {
      this.id = id;
      this.age = age;
      this.lifespan = lifespan;
   }

   @SideOnly(Side.CLIENT)
   public IMessage handleMessage(MessageContext context) {
      ClientTicker.addAction(() -> {
         World world = Minecraft.getMinecraft().world;
         if (world != null) {
            Entity entity = world.getEntityByID(this.id);
            if (entity instanceof EntityItem) {
               ((EntityItem)entity).lifespan = this.lifespan;
               ItemsFlashBeforeExpiring.setItemAge((EntityItem)entity, this.age);
            }
         }

      });
      return null;
   }
}
