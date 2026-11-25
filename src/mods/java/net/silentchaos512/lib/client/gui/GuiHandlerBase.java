package net.silentchaos512.lib.client.gui;

import javax.annotation.Nullable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

public abstract class GuiHandlerBase<T extends IGuiType> implements IGuiHandler {
   public GuiHandlerBase() {
   }

   @Nullable
   abstract T typeByID(int var1);

   @Nullable
   public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
      BlockPos pos = new BlockPos(x, y, z);
      TileEntity tile = world.getTileEntity(pos);
      T type = this.typeByID(ID);
      return type != null ? type.getContainer(tile, player, x) : null;
   }

   @Nullable
   public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
      return null;
   }
}
