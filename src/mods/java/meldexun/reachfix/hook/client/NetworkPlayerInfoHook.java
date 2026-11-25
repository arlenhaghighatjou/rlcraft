package meldexun.reachfix.hook.client;

import meldexun.reachfix.util.ReachFixUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.GameType;

public class NetworkPlayerInfoHook {
   public NetworkPlayerInfoHook() {
   }

   public static void onUpdateGameMode(NetworkPlayerInfo playerInfo, GameType newGameMode) {
      Minecraft mc = Minecraft.getMinecraft();
      if (mc.world != null) {
         EntityPlayer player = mc.world.getPlayerEntityByUUID(playerInfo.getGameProfile().getId());
         if (player != null) {
            ReachFixUtil.updateBaseReachModifier(player, newGameMode == GameType.CREATIVE);
         }
      }
   }
}
