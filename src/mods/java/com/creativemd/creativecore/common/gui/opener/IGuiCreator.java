package com.creativemd.creativecore.common.gui.opener;

import com.creativemd.creativecore.common.gui.container.SubContainer;
import com.creativemd.creativecore.common.gui.container.SubGui;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public interface IGuiCreator {
   @SideOnly(Side.CLIENT)
   SubGui getGui(EntityPlayer var1, ItemStack var2, World var3, BlockPos var4, IBlockState var5);

   SubContainer getContainer(EntityPlayer var1, ItemStack var2, World var3, BlockPos var4, IBlockState var5);
}
