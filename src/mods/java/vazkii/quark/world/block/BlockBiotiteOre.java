package vazkii.quark.world.block;

import java.util.Random;
import javax.annotation.Nonnull;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.item.Item;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import vazkii.arl.block.BlockMod;
import vazkii.quark.base.block.IQuarkBlock;
import vazkii.quark.world.feature.Biotite;

public class BlockBiotiteOre extends BlockMod implements IQuarkBlock {
   public BlockBiotiteOre() {
      super("biotite_ore", Material.ROCK, new String[0]);
      this.setHardness(3.2F);
      this.setResistance(15.0F);
      this.setSoundType(SoundType.STONE);
      this.setCreativeTab(CreativeTabs.BUILDING_BLOCKS);
   }

   @Nonnull
   public Item getItemDropped(IBlockState state, Random rand, int fortune) {
      return Biotite.biotite;
   }

   public boolean canEntityDestroy(IBlockState state, IBlockAccess world, BlockPos pos, Entity entity) {
      return !(entity instanceof EntityDragon);
   }

   public int quantityDroppedWithBonus(int fortune, @Nonnull Random random) {
      if (fortune > 0) {
         int i = random.nextInt(fortune + 2) - 1;
         if (i < 0) {
            i = 0;
         }

         return this.quantityDropped(random) * (i + 1);
      } else {
         return this.quantityDropped(random);
      }
   }

   public int getExpDrop(IBlockState state, IBlockAccess world, BlockPos pos, int fortune) {
      Random rand = world instanceof World ? ((World)world).rand : new Random();
      return MathHelper.getInt(rand, 2, 5);
   }
}
