package reborncore.api;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface IToolHandler {
   boolean handleTool(ItemStack var1, BlockPos var2, World var3, EntityPlayer var4, EnumFacing var5, boolean var6);
}
