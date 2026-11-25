package ivorius.ivtoolkit.tools;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public interface MCRegistry {
   Item itemFromID(ResourceLocation var1);

   ResourceLocation idFromItem(Item var1);

   void modifyItemStackCompound(NBTTagCompound var1, ResourceLocation var2);

   Block blockFromID(ResourceLocation var1);

   ResourceLocation idFromBlock(Block var1);

   TileEntity loadTileEntity(World var1, NBTTagCompound var2);
}
