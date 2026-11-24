package net.minecraft.block;

import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Items;
import net.minecraft.item.Item;

public class BlockEmptyDrops extends Block
{
    public BlockEmptyDrops(Material materialIn)
    {
        super(materialIn);
    }

    public int quantityDropped(EaglercraftRandom random)
    {
        return 0;
    }

    public Item getItemDropped(IBlockState state, EaglercraftRandom rand, int fortune)
    {
        return Items.AIR;
    }
}
