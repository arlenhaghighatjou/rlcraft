package vazkii.quark.base.network.message;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vazkii.arl.network.NetworkMessage;
import vazkii.arl.util.ClientTicker;
import vazkii.quark.vanity.feature.BoatSails;

public class MessageSyncBoatBanner extends NetworkMessage<MessageSyncBoatBanner> {
   public int boat;
   public ItemStack banner;

   public MessageSyncBoatBanner() {
   }

   public MessageSyncBoatBanner(int boat, ItemStack banner) {
      this.boat = boat;
      this.banner = banner;
   }

   @SideOnly(Side.CLIENT)
   public IMessage handleMessage(MessageContext context) {
      ClientTicker.addAction(() -> {
         World world = Minecraft.getMinecraft().world;
         if (world != null) {
            Entity boatEntity = world.getEntityByID(this.boat);
            if (boatEntity == null) {
               BoatSails.queueBannerUpdate(this.boat, this.banner);
            } else {
               BoatSails.setBanner(boatEntity, this.banner, false);
            }
         }

      });
      return null;
   }
}
