package net.minecraft.block;

import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockAccess;

public class BlockSeaLantern extends Block
{
    public BlockSeaLantern(Material materialIn)
    {
        super(materialIn);
        this.setCreativeTab(CreativeTabs.BUILDING_BLOCKS);
    }

    public int quantityDropped(EaglercraftRandom random)
    {
        return 2 + random.nextInt(2);
    }

    public int quantityDroppedWithBonus(int fortune, EaglercraftRandom random)
    {
        return MathHelper.clamp(this.quantityDropped(random) + random.nextInt(fortune + 1), 1, 5);
    }

    public Item getItemDropped(IBlockState state, EaglercraftRandom rand, int fortune)
    {
        return Items.PRISMARINE_CRYSTALS;
    }

    public MapColor getMapColor(IBlockState state, IBlockAccess worldIn, BlockPos pos)
    {
        return MapColor.QUARTZ;
    }

    protected boolean canSilkHarvest()
    {
        return true;
    }
}
