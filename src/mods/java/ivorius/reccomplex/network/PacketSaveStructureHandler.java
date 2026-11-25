package ivorius.reccomplex.network;

import ivorius.ivtoolkit.network.SchedulingMessageHandler;
import ivorius.reccomplex.RecurrentComplex;
import ivorius.reccomplex.capability.RCEntityInfo;
import ivorius.reccomplex.commands.RCCommands;
import ivorius.reccomplex.files.loading.ResourceDirectory;
import ivorius.reccomplex.utils.SaveDirectoryData;
import ivorius.reccomplex.world.gen.feature.structure.Structure;
import ivorius.reccomplex.world.gen.feature.structure.StructureRegistry;
import ivorius.reccomplex.world.gen.feature.structure.generic.GenericStructure;
import java.nio.file.Path;
import java.util.Set;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import org.apache.commons.lang3.tuple.Pair;

public class PacketSaveStructureHandler extends SchedulingMessageHandler<PacketSaveStructure, IMessage> {
   public PacketSaveStructureHandler() {
   }

   public static void saveStructure(GenericStructure structureInfo, String structureID, SaveDirectoryData.Result saveDirectoryDataResult) {
      RecurrentComplex.network.sendToServer(new PacketSaveStructure(structureInfo, structureID, saveDirectoryDataResult));
   }

   public void processServer(PacketSaveStructure message, MessageContext ctx, WorldServer server) {
      NetHandlerPlayServer netHandlerPlayServer = ctx.getServerHandler();
      EntityPlayerMP player = netHandlerPlayServer.player;
      if (!RecurrentComplex.checkPerms(player)) {
         RCEntityInfo entityInfo = RCEntityInfo.get(player, (EnumFacing)null);
         GenericStructure genericStructureInfo = message.getStructureInfo();
         if (entityInfo != null) {
            genericStructureInfo.worldDataCompound = entityInfo.getCachedExportStructureBlockDataNBT();
         }

         SaveDirectoryData.Result saveDirectoryDataResult = message.getSaveDirectoryDataResult();
         String id = message.getStructureID();
         ResourceDirectory saveDir = saveDirectoryDataResult.directory;
         write(player, genericStructureInfo, id, saveDir, saveDirectoryDataResult.deleteOther, true);
      }
   }

   public static boolean write(ICommandSender sender, GenericStructure structure, String id, ResourceDirectory saveDir, boolean deleteOther, boolean inform) {
      StructureRegistry.INSTANCE.register(id, "", (Structure)structure, saveDir.isActive(), saveDir.getLevel());
      ResourceDirectory delDir = saveDir.opposite();
      boolean saveResult = RecurrentComplex.saver.trySave(saveDir.toPath(), "structure", id);
      if ((!inform || RCCommands.informSaveResult(saveResult, sender, saveDir, "structure", id)) && deleteOther) {
         Pair<Set<Path>, Set<Path>> deleteResult = RecurrentComplex.saver.tryDeleteWithID(delDir.toPath(), "structure", id);
         if (inform) {
            RCCommands.informDeleteResult(deleteResult, sender, "structure", id, delDir);
         }
      }

      return saveResult;
   }
}
