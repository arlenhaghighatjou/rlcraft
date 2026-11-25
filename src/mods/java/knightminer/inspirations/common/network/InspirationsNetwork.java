package knightminer.inspirations.common.network;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.Packet;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;
import slimeknights.mantle.network.AbstractPacket;
import slimeknights.mantle.network.NetworkWrapper;

public class InspirationsNetwork extends NetworkWrapper {
   public static InspirationsNetwork instance = new InspirationsNetwork();

   public InspirationsNetwork() {
      super("inspirations");
   }

   public void setup() {
      this.registerPacketClient(InventorySlotSyncPacket.class);
      this.registerPacketClient(MilkablePacket.class);
      this.registerPacketClient(RenderBlockUpdatePacket.class);
   }

   public static void sendToAll(AbstractPacket packet) {
      instance.network.sendToAll(packet);
   }

   public static void sendTo(AbstractPacket packet, EntityPlayerMP player) {
      instance.network.sendTo(packet, player);
   }

   public static void sendPacket(Entity player, Packet<?> packet) {
      if (player instanceof EntityPlayerMP && ((EntityPlayerMP)player).connection != null) {
         ((EntityPlayerMP)player).connection.sendPacket(packet);
      }

   }

   public static void sendToClients(World world, BlockPos pos, AbstractPacket packet) {
      if (world instanceof WorldServer) {
         sendToClients((WorldServer)world, pos, packet);
      }

   }

   public static void sendToClients(WorldServer world, BlockPos pos, AbstractPacket packet) {
      Chunk chunk = world.getChunkFromBlockCoords(pos);

      for(EntityPlayer player : world.playerEntities) {
         if (player instanceof EntityPlayerMP) {
            EntityPlayerMP playerMP = (EntityPlayerMP)player;
            if (world.getPlayerChunkMap().isPlayerWatchingChunk(playerMP, chunk.x, chunk.z)) {
               sendTo(packet, playerMP);
            }
         }
      }

   }
}
