package ivorius.reccomplex.network;

import gnu.trove.map.TObjectFloatMap;
import ivorius.ivtoolkit.network.SchedulingMessageHandler;
import ivorius.reccomplex.RCConfig;
import ivorius.reccomplex.RecurrentComplex;
import ivorius.reccomplex.gui.tweak.GuiTweakStructures;
import javax.annotation.Nonnull;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class PacketSpawnTweaksHandler extends SchedulingMessageHandler<PacketSpawnTweaks, IMessage> {
   public PacketSpawnTweaksHandler() {
   }

   public static void sendToServer(TObjectFloatMap<String> spawnTweaks) {
      RecurrentComplex.network.sendToServer(new PacketSpawnTweaks(spawnTweaks));
   }

   public static void openGUI(@Nonnull EntityPlayerMP player) {
      RecurrentComplex.network.sendTo(new PacketSpawnTweaks(RCConfig.spawnTweaks), player);
   }

   public void processServer(PacketSpawnTweaks message, MessageContext ctx, WorldServer server) {
      RCConfig.spawnTweaks.clear();
      RCConfig.spawnTweaks.putAll(message.getData());
      RCConfig.writeSpawnTweaks();
      RecurrentComplex.config.save();
   }

   @SideOnly(Side.CLIENT)
   public void processClient(PacketSpawnTweaks message, MessageContext ctx) {
      Minecraft.getMinecraft().displayGuiScreen(new GuiTweakStructures(message.getData()));
   }
}
