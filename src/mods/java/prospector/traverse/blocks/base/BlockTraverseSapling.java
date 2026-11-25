package prospector.traverse.blocks.base;

import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import net.minecraft.block.BlockSapling;
import net.minecraft.block.SoundType;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.terraingen.TerrainGen;
import prospector.traverse.core.TraverseTab;
import prospector.traverse.shadow.shootingstar.ShootingStar;
import prospector.traverse.shadow.shootingstar.model.ModelCompound;
import prospector.traverse.world.features.TraverseTreeGenerator;

public class BlockTraverseSapling extends BlockSapling {
   public TraverseTreeGenerator generator;

   public BlockTraverseSapling(String name, TraverseTreeGenerator generator) {
      this.generator = generator;
      this.setRegistryName(new ResourceLocation("traverse", name + "_sapling"));
      this.setUnlocalizedName(this.getRegistryName().toString());
      this.setCreativeTab(TraverseTab.TAB);
      this.setSoundType(SoundType.PLANT);
      ShootingStar.registerModel(new ModelCompound("traverse", this, "sapling", new IProperty[]{STAGE, TYPE}));
   }

   public void generateTree(World worldIn, BlockPos pos, IBlockState state, EaglercraftRandom rand) {
      if (TerrainGen.saplingGrowTree(worldIn, rand, pos)) {
         if (this.generator.generate(worldIn, rand, pos)) {
            worldIn.setBlockToAir(pos);
         }

      }
   }

   public void getSubBlocks(CreativeTabs tab, NonNullList<ItemStack> list) {
      list.add(new ItemStack(this, 1, 0));
   }
}
