package ivorius.reccomplex.network;

import ivorius.ivtoolkit.network.SchedulingMessageHandler;
import ivorius.reccomplex.RecurrentComplex;
import ivorius.reccomplex.commands.RCCommands;
import ivorius.reccomplex.files.loading.ResourceDirectory;
import ivorius.reccomplex.item.ItemLootTableComponentTag;
import ivorius.reccomplex.utils.SaveDirectoryData;
import ivorius.reccomplex.world.storage.loot.GenericItemCollectionRegistry;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.util.EnumHand;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketSaveLootTableHandler extends SchedulingMessageHandler<PacketSaveLootTable, IMessage> {
   public PacketSaveLootTableHandler() {
   }

   public void processServer(PacketSaveLootTable message, MessageContext ctx, WorldServer server) {
      NetHandlerPlayServer playServer = ctx.getServerHandler();
      EntityPlayerMP player = playServer.player;
      if (!RecurrentComplex.checkPerms(player)) {
         SaveDirectoryData.Result saveDirectoryDataResult = message.getSaveDirectoryDataResult();
         String id = message.getKey();
         ResourceDirectory saveDir = saveDirectoryDataResult.directory;
         ResourceDirectory delDir = saveDir.opposite();
         GenericItemCollectionRegistry.INSTANCE.register(id, "", message.getComponent(), saveDir.isActive(), saveDir.getLevel());
         if (RCCommands.informSaveResult(message.getComponent() != null && id != null && RecurrentComplex.saver.trySave(saveDir.toPath(), "inventory_generation_component", id), player, saveDir, "inventory_generation_component", id)) {
            if (saveDirectoryDataResult.deleteOther) {
               RCCommands.informDeleteResult(RecurrentComplex.saver.tryDeleteWithID(delDir.toPath(), "inventory_generation_component", id), player, "inventory_generation_component", id, delDir);
            }

            ItemStack heldItem = playServer.player.getHeldItem(EnumHand.MAIN_HAND);
            if (heldItem != null && heldItem.getItem() instanceof ItemLootTableComponentTag) {
               ItemLootTableComponentTag.setComponentKey(heldItem, id);
            }

            player.openContainer.detectAndSendChanges();
         }

      }
   }
}
