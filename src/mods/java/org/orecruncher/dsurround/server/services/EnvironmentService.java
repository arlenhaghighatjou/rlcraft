package org.orecruncher.dsurround.server.services;

import java.util.List;
import javax.annotation.Nonnull;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.BlockPos;
import net.minecraft.village.Village;
import net.minecraft.village.VillageCollection;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;
import net.minecraftforge.fml.relauncher.Side;
import org.orecruncher.dsurround.network.Network;
import org.orecruncher.dsurround.network.PacketEnvironment;

public final class EnvironmentService extends Service {
   EnvironmentService() {
      super("EnvironmentService");
   }

   @SubscribeEvent
   public void tickEvent(@Nonnull PlayerTickEvent event) {
      if (event.phase == Phase.END && event.side == Side.SERVER) {
         EntityPlayer player = event.player;
         VillageCollection villageCollection = player.getEntityWorld().getVillageCollection();
         boolean inVillage = false;
         List<Village> villages = villageCollection.getVillageList();
         if (villages.size() > 0) {
            BlockPos pos = player.getPosition();

            for(Village v : villages) {
               if (v.isBlockPosWithinSqVillageRadius(pos)) {
                  inVillage = true;
                  break;
               }
            }
         }

         PacketEnvironment packet = new PacketEnvironment(inVillage);
         Network.sendToPlayer((EntityPlayerMP)player, packet);
      }

   }
}
