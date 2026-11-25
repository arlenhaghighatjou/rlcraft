package ivorius.ivtoolkit.blocks;

import ivorius.ivtoolkit.tools.IvSideClient;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;

public class IvTileEntityHelper {
   public IvTileEntityHelper() {
   }

   public static SPacketUpdateTileEntity getStandardDescriptionPacket(TileEntity tileEntity) {
      NBTTagCompound var1 = new NBTTagCompound();
      tileEntity.writeToNBT(var1);
      return new SPacketUpdateTileEntity(tileEntity.getPos(), 1, var1);
   }

   public static World getAnyWorld() {
      return FMLCommonHandler.instance().getSide().isClient() ? IvSideClient.getClientWorld() : FMLCommonHandler.instance().getMinecraftServerInstance().getEntityWorld();
   }
}
