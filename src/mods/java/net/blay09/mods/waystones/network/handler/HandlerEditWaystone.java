package net.blay09.mods.waystones.network.handler;

import javax.annotation.Nullable;
import net.blay09.mods.waystones.GlobalWaystones;
import net.blay09.mods.waystones.WarpMode;
import net.blay09.mods.waystones.WaystoneConfig;
import net.blay09.mods.waystones.WaystoneManager;
import net.blay09.mods.waystones.block.TileWaystone;
import net.blay09.mods.waystones.network.NetworkHandler;
import net.blay09.mods.waystones.network.message.MessageEditWaystone;
import net.blay09.mods.waystones.network.message.MessageOpenWaystoneSelection;
import net.blay09.mods.waystones.util.WaystoneEntry;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class HandlerEditWaystone implements IMessageHandler<MessageEditWaystone, IMessage> {
   public HandlerEditWaystone() {
   }

   @Nullable
   public IMessage onMessage(MessageEditWaystone message, MessageContext ctx) {
      NetworkHandler.getThreadListener(ctx).addScheduledTask(() -> {
         EntityPlayerMP entityPlayer = ctx.getServerHandler().player;
         if (!WaystoneConfig.general.creativeModeOnly || entityPlayer.capabilities.isCreativeMode) {
            World world = entityPlayer.getEntityWorld();
            BlockPos pos = message.getPos();
            if (entityPlayer.getDistance((double)pos.getX(), (double)pos.getY(), (double)pos.getZ()) <= 10.0D) {
               GlobalWaystones globalWaystones = GlobalWaystones.get(entityPlayer.world);
               TileEntity tileEntity = world.getTileEntity(pos);
               if (tileEntity instanceof TileWaystone) {
                  TileWaystone tileWaystone = ((TileWaystone)tileEntity).getParent();
                  if (globalWaystones.getGlobalWaystone(tileWaystone.getWaystoneName()) != null && !entityPlayer.capabilities.isCreativeMode && !WaystoneConfig.general.allowEveryoneGlobal) {
                     return;
                  }

                  if (WaystoneConfig.general.restrictRenameToOwner && !tileWaystone.isOwner(entityPlayer)) {
                     ctx.getServerHandler().player.sendMessage(new TextComponentTranslation("waystones:notTheOwner", new Object[0]));
                     return;
                  }

                  String newName = message.getName();
                  if (newName.equals("%RANDOM%") && !entityPlayer.capabilities.isCreativeMode) {
                     newName = "RANDOM";
                  }

                  if (globalWaystones.getGlobalWaystone(newName) != null && !entityPlayer.capabilities.isCreativeMode) {
                     ctx.getServerHandler().player.sendMessage(new TextComponentTranslation("waystones:nameOccupied", new Object[]{newName}));
                     return;
                  }

                  WaystoneEntry oldWaystone = new WaystoneEntry(tileWaystone);
                  if (oldWaystone.isGlobal()) {
                     globalWaystones.removeGlobalWaystone(oldWaystone);
                  }

                  tileWaystone.setWaystoneName(newName);
                  WaystoneEntry newWaystone = new WaystoneEntry(tileWaystone);
                  if (message.isGlobal() && (entityPlayer.capabilities.isCreativeMode || WaystoneConfig.general.allowEveryoneGlobal)) {
                     tileWaystone.setGlobal(true);
                     newWaystone.setGlobal(true);
                     globalWaystones.addGlobalWaystone(newWaystone);

                     for(Object obj : FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayers()) {
                        WaystoneManager.sendPlayerWaystones((EntityPlayer)obj);
                     }
                  }

                  if (!newWaystone.isGlobal()) {
                     WaystoneManager.removePlayerWaystone(entityPlayer, oldWaystone);
                     WaystoneManager.addPlayerWaystone(entityPlayer, newWaystone);
                     WaystoneManager.sendPlayerWaystones(entityPlayer);
                  }

                  if (message.isFromSelectionGui()) {
                     NetworkHandler.channel.sendTo(new MessageOpenWaystoneSelection(WarpMode.WAYSTONE, EnumHand.MAIN_HAND, newWaystone), entityPlayer);
                  }
               }

            }
         }
      });
      return null;
   }
}
