package ivorius.reccomplex.network;

import ivorius.reccomplex.block.TileEntityWithGUI;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

public class PacketEditTileEntity extends PacketFullTileEntityData {
   public PacketEditTileEntity() {
   }

   public <TE extends TileEntity & TileEntityWithGUI> PacketEditTileEntity(TE tileEntity) {
      super(tileEntity.getPos(), getSyncedNBT((TileEntityWithGUI)tileEntity));
   }

   private static NBTTagCompound getSyncedNBT(TileEntityWithGUI structureGenerator) {
      NBTTagCompound compound = new NBTTagCompound();
      structureGenerator.writeSyncedNBT(compound);
      return compound;
   }
}
