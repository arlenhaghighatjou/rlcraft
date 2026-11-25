package noppes.vc;

import java.net.InetAddress;
import java.net.UnknownHostException;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import noppes.vc.blocks.tiles.TileBanner;
import noppes.vc.shared.util.AnalyticsTracking;

public class ServerEventsHandler {
   private String serverName = null;

   public ServerEventsHandler() {
   }

   @SubscribeEvent
   public void invoke(RightClickBlock event) {
      if (event.getHand() == EnumHand.MAIN_HAND) {
         EntityPlayer player = event.getEntityPlayer();
         BlockPos pos = event.getPos();
         IBlockState state = player.world.getBlockState(pos);
         Block block = state.getBlock();
         if (!player.world.isRemote && (block == VCBlocks.banner || block == VCBlocks.wall_banner || block == VCBlocks.sign)) {
            ItemStack item = player.inventory.getCurrentItem();
            if (item == null || item.getItem() == null) {
               return;
            }

            int meta = block.getMetaFromState(state);
            if (meta >= 7) {
               pos = pos.down();
            }

            TileBanner tile = (TileBanner)player.world.getTileEntity(pos);
            if (!tile.canEdit()) {
               return;
            }

            if (!player.world.isRemote) {
               tile.icon = item.copy();
               tile.markDirty();
               player.world.notifyBlockUpdate(pos, state, state, 3);
               event.setCanceled(true);
            }
         }

      }
   }

   @SubscribeEvent
   public void playerLogin(PlayerLoggedInEvent event) {
      if (this.serverName == null) {
         String e = "local";
         MinecraftServer server = event.player.getServer();
         if (server.isDedicatedServer()) {
            try {
               e = InetAddress.getByName(server.getServerHostname()).getCanonicalHostName();
            } catch (UnknownHostException var5) {
               e = server.getServerHostname();
            }

            if (server.getServerPort() != 25565) {
               e = e + ":" + server.getServerPort();
            }
         }

         if (e == null || e.startsWith("192.168") || e.contains("127.0.0.1") || e.startsWith("localhost")) {
            e = "local";
         }

         this.serverName = e;
      }

      AnalyticsTracking.sendData(event.player, "join", this.serverName, "variedcommodities", "1.12");
   }
}
