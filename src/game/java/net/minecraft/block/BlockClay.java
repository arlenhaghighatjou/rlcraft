package net.minecraft.block;

import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.Item;

public class BlockClay extends Block
{
    public BlockClay()
    {
        super(Material.CLAY);
        this.setCreativeTab(CreativeTabs.BUILDING_BLOCKS);
    }

    public Item getItemDropped(IBlockState state, EaglercraftRandom rand, int fortune)
    {
        return Items.CLAY_BALL;
    }

    public int quantityDropped(EaglercraftRandom random)
    {
        return 4;
    }
}
