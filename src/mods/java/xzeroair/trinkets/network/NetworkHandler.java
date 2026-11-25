package xzeroair.trinkets.network;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.Packet;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;
import xzeroair.trinkets.network.configsync.PacketConfigSync;
import xzeroair.trinkets.network.keybinds.KeybindPacket;
import xzeroair.trinkets.network.keybinds.MovementKeyPacket;
import xzeroair.trinkets.network.mana.SyncManaCostToHudPacket;
import xzeroair.trinkets.network.mana.SyncManaStatsPacket;
import xzeroair.trinkets.network.particles.EffectsRenderPacket;
import xzeroair.trinkets.network.status.StatusEffectPacket;
import xzeroair.trinkets.network.trinketcontainer.OpenTrinketGui;
import xzeroair.trinkets.network.vip.VipStatusPacket;

public class NetworkHandler extends BasicNetworkWrapper {
   public static final NetworkHandler INSTANCE = new NetworkHandler();

   public NetworkHandler() {
      super("xat".toLowerCase());
   }

   public void init() {
      this.registerPacket(SyncRaceDataPacket.class);
      this.registerPacket(SyncItemDataPacket.class);
      this.registerPacket(StatusEffectPacket.class);
      this.registerPacketClient(SyncManaStatsPacket.class);
      this.registerPacketClient(SyncManaCostToHudPacket.class);
      this.registerPacketServer(KeybindPacket.class);
      this.registerPacketServer(MovementKeyPacket.class);
      this.registerPacketServer(IncreasedReachPacket.class);
      this.registerPacket(EffectsRenderPacket.class);
      this.registerPacket(VipStatusPacket.class);
      this.registerPacket(OpenTrinketGui.class);
      this.registerPacketClient(PacketConfigSync.class);
   }

   public static void sendPacket(Entity player, Packet<?> packet) {
      if (player instanceof EntityPlayerMP && ((EntityPlayerMP)player).connection != null) {
         ((EntityPlayerMP)player).connection.sendPacket(packet);
      }

   }

   public static void sendToAll(BasicPacket packet) {
      INSTANCE.network.sendToAll(packet);
   }

   public static void sendTo(BasicPacket packet, EntityPlayerMP player) {
      INSTANCE.network.sendTo(packet, player);
   }

   public static void sendToAllAround(BasicPacket packet, TargetPoint point) {
      INSTANCE.network.sendToAllAround(packet, point);
   }

   public static void sendToDimension(BasicPacket packet, int dimensionId) {
      INSTANCE.network.sendToDimension(packet, dimensionId);
   }

   public static void sendToServer(BasicPacket packet) {
      INSTANCE.network.sendToServer(packet);
   }

   public static void sendToTracking(BasicPacket packet, Entity entity) {
      INSTANCE.network.sendToAllTracking(packet, entity);
   }

   public static void sendToClients(WorldServer world, BlockPos pos, BasicPacket packet) {
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
