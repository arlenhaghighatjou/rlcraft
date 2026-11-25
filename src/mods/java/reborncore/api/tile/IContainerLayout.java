package reborncore.api.tile;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;

public interface IContainerLayout<T extends TileEntity> {
   void addInventorySlots();

   void addPlayerSlots();

   void setTile(T var1);

   @Nullable
   T getTile();

   void setPlayer(EntityPlayer var1);

   @Nullable
   EntityPlayer getPlayer();

   @Nullable
   List<Integer> getSlotsForSide(EnumFacing var1);
}
