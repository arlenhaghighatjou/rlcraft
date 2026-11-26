package net.silentchaos512.lib.block;

import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public interface ITileEntityBlock {
   Class<? extends TileEntity> getTileEntityClass();

   Supplier<? extends TileEntity> getTileEntityFactory();

   @SideOnly(Side.CLIENT)
   @Nullable
   default TileEntitySpecialRenderer<?> getTileRenderer() {
      return null;
   }
}
