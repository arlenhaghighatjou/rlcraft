package net.minecraft.block;

import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.Item;

public class BlockBookshelf extends Block
{
    public BlockBookshelf()
    {
        super(Material.WOOD);
        this.setCreativeTab(CreativeTabs.BUILDING_BLOCKS);
    }

    public int quantityDropped(EaglercraftRandom random)
    {
        return 3;
    }

    public Item getItemDropped(IBlockState state, EaglercraftRandom rand, int fortune)
    {
        return Items.BOOK;
    }
}
