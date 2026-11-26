package noppes.vc;

import net.minecraftforge.fml.common.network.FMLNetworkEvent.ServerCustomPacketEvent;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;
import noppes.vc.blocks.tiles.TileBasicContainer;
import noppes.vc.constants.GuiType;
import noppes.vc.containers.ContainerCarpentryBench;
import noppes.vc.containers.ContainerCrate;
import noppes.vc.containers.ContainerTradingBlock;

public class CommonProxy implements IGuiHandler {
   public CommonProxy() {
   }

   public void preinit() {
      VariedCommodities.Channel.register(ServerCustomPacketEvent.class, new PacketHandlerServer()::onServerPacket);
   }

   public void init() {
   }

   public void postinit() {
   }

   public ModelBiped getSkirtModel() {
      return null;
   }

   public void spawnParticle(EntityLivingBase player, String string, Object... ob) {
   }

   public void spawnParticle(EnumParticleTypes particle, double x, double y, double z, double motionX, double motionY, double motionZ, float scale) {
   }

   public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
      return null;
   }

   public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
      if (ID > GuiType.values().length) {
         return null;
      } else {
         GuiType gui = GuiType.values()[ID];
         return this.getContainer(gui, player, world, x, y, z);
      }
   }

   public Container getContainer(GuiType gui, EntityPlayer player, World world, int x, int y, int z) {
      if (gui == GuiType.CRATE) {
         return new ContainerCrate(player.inventory, (TileBasicContainer)player.world.getTileEntity(new BlockPos(x, y, z)));
      } else if (gui == GuiType.TRADING_BLOCK) {
         return new ContainerTradingBlock(player, new BlockPos(x, y, z));
      } else {
         return gui == GuiType.CARPENTRY_BENCH ? new ContainerCarpentryBench(player.inventory, world, new BlockPos(x, y, z)) : null;
      }
   }

   public void openGui(GuiType gui, int x, int y, int z) {
   }

   public void openGui(EntityPlayer player, Object guiscreen) {
   }
}
