package cursedflames.bountifulbaubles.network.wormhole;

import cursedflames.bountifulbaubles.capability.CapabilityWormholePins;
import cursedflames.bountifulbaubles.network.NBTPacket;
import cursedflames.bountifulbaubles.wormhole.ContainerWormhole;
import cursedflames.bountifulbaubles.wormhole.IWormholeTarget;
import cursedflames.bountifulbaubles.wormhole.PlayerTarget;
import cursedflames.bountifulbaubles.wormhole.TeleportRequest;
import cursedflames.bountifulbaubles.wormhole.WormholeUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class HandlerWormhole {
   public HandlerWormhole() {
   }

   public static void handleWormhole(NBTPacket message, MessageContext ctx) {
      NBTTagCompound tag = message.getTag();
      if (tag.hasKey("target")) {
         EntityPlayer player = ctx.getServerHandler().player;
         IWormholeTarget target = CapabilityWormholePins.targetFromNBT(tag.getCompoundTag("target"));
         if (target != null) {
            if (target instanceof PlayerTarget) {
               EntityPlayer playerTarget = ((PlayerTarget)target).getPlayer(player.world);
               if (playerTarget != null) {
                  TeleportRequest.makeReq(player.world, player, playerTarget);
               }
            } else if (target.teleportPlayerTo(player)) {
               WormholeUtil.consumeItem(player);
            }
         }
      }
   }

   public static void handlePin(NBTPacket message, MessageContext ctx) {
      NBTTagCompound tag = message.getTag();
      int index = tag.getInteger("index");
      Container container = ctx.getServerHandler().player.openContainer;
      if (container instanceof ContainerWormhole) {
         ((ContainerWormhole)container).pin(index);
      }

   }
}
