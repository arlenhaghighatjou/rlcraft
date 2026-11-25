package cursedflames.bountifulbaubles.wormhole;

import cursedflames.bountifulbaubles.network.NBTPacket;
import cursedflames.bountifulbaubles.network.PacketHandler;
import java.util.ArrayList;
import java.util.List;
import net.lax1dude.eaglercraft.v1_8.EaglercraftUUID;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;

public class TeleportRequest {
   public static List<TeleportRequest> requests = new ArrayList();
   public static int REQ_EXPIRY_TIME = 400;
   public final long reqTickTime;
   public final World world;
   public final EaglercraftUUID origin;
   public final EaglercraftUUID target;
   public TeleportRequest.Status status = TeleportRequest.Status.PENDING;

   private TeleportRequest(World world, EaglercraftUUID origin, EaglercraftUUID target) {
      this.reqTickTime = world.getTotalWorldTime();
      this.world = world;
      this.origin = origin;
      this.target = target;
   }

   public static TeleportRequest makeReq(World world, EntityPlayer origin, EntityPlayer target) {
      if (!(target instanceof EntityPlayerMP)) {
         return null;
      } else {
         origin.sendMessage(new TextComponentString("Teleport request sent to " + target.getName()));
         TeleportRequest req = new TeleportRequest(world, origin.getUniqueID(), target.getUniqueID());
         requests.add(req);
         NBTTagCompound tag = new NBTTagCompound();
         tag.setUniqueId("sender", origin.getUniqueID());
         tag.setString("name", origin.getName());
         NBTPacket clientReq = new NBTPacket(tag, PacketHandler.HandlerIds.WORMHOLE_REQUEST.id);
         PacketHandler.INSTANCE.sendTo(clientReq, (EntityPlayerMP)target);
         return req;
      }
   }

   public static void acceptReject(EntityPlayer target, boolean accept, String name) {
      long timeoutTime = target.world.getTotalWorldTime() - (long)REQ_EXPIRY_TIME;
      EntityPlayer originPlayer = null;
      EaglercraftUUID origin;
      if (name != null && name.length() != 0) {
         originPlayer = target.world.getPlayerEntityByName(name);
         if (originPlayer == null) {
            return;
         }

         origin = originPlayer.getUniqueID();
      } else {
         origin = null;
      }

      for(int i = requests.size() - 1; i >= 0; --i) {
         TeleportRequest req = (TeleportRequest)requests.get(i);
         if (req.reqTickTime < timeoutTime) {
            req.status = TeleportRequest.Status.TIMEOUT;
            EntityPlayer from = target.world.getPlayerEntityByUUID(req.origin);
            EntityPlayer to = target.world.getPlayerEntityByUUID(req.target);
            if (from != null && to != null) {
               from.sendMessage(new TextComponentString("Teleport request to " + to.getName() + " has expired."));
               to.sendMessage(new TextComponentString("Teleport request from " + from.getName() + " has expired."));
            }

            requests.remove(i);
         } else if (req.target.equals(target.getUniqueID()) && (origin == null || req.origin.equals(origin))) {
            EntityPlayer player = originPlayer;
            if (originPlayer == null) {
               player = target.world.getPlayerEntityByUUID(req.origin);
            }

            if (!accept) {
               req.status = TeleportRequest.Status.REJECT;
               if (player != null) {
                  target.sendMessage(new TextComponentString("Rejected teleport request from " + player.getName() + "."));
                  player.sendMessage(new TextComponentString("Teleport request to " + target.getName() + " was rejected."));
               }
            } else {
               req.status = TeleportRequest.Status.ACCEPT;
               if (player != null) {
                  target.sendMessage(new TextComponentString("Accepted teleport request from " + player.getName() + "."));
                  player.sendMessage(new TextComponentString("Teleport request to " + target.getName() + " was accepted."));
                  TextComponentString message1 = null;
                  TextComponentString message2 = null;
                  if (player.isPlayerSleeping()) {
                     message1 = new TextComponentString("Teleport failed as " + player.getName() + " is asleep.");
                     message2 = new TextComponentString("Teleport failed as you are asleep.");
                  } else if (player.isRiding()) {
                     message1 = new TextComponentString("Teleport failed as " + player.getName() + " is mounted.");
                     message2 = new TextComponentString("Teleport failed as you are mounted.");
                  } else if (WormholeUtil.consumeItem(player)) {
                     WormholeUtil.doTeleport(player, target);
                  } else {
                     message1 = new TextComponentString("Teleport failed as " + player.getName() + " has no wormhole potions or mirror.");
                     message2 = new TextComponentString("Teleport failed as you have no wormhole potions or mirror.");
                  }

                  if (message1 != null) {
                     target.sendMessage(message1);
                  }

                  if (message2 != null) {
                     player.sendMessage(message2);
                  }
               }
            }

            requests.remove(i);
            if (origin != null) {
               return;
            }
         }
      }

   }

   public static enum Status {
      PENDING,
      ACCEPT,
      REJECT,
      TIMEOUT;

      private Status() {
      }
   }
}
