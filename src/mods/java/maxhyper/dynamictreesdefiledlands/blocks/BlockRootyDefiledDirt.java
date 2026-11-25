package maxhyper.dynamictreesdefiledlands.blocks;

import com.ferreusveritas.dynamictrees.blocks.BlockRooty;
import com.ferreusveritas.dynamictrees.blocks.MimicProperty;
import com.ferreusveritas.dynamictrees.systems.DirtHelper;
import lykrast.defiledlands.common.init.ModBlocks;
import maxhyper.dynamictreesdefiledlands.ModContent;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

public class BlockRootyDefiledDirt extends BlockRooty {
   static String name = "rootydefileddirt";
   public static final Material[] materialOrder = new Material[]{Material.GRASS, Material.GROUND};

   public BlockRootyDefiledDirt(boolean isTileEntity) {
      this(name + (isTileEntity ? "species" : ""), isTileEntity);
   }

   public BlockRootyDefiledDirt(String name, boolean isTileEntity) {
      super(name, Material.GROUND, isTileEntity);
   }

   public IBlockState getMimic(IBlockAccess access, BlockPos pos) {
      return MimicProperty.getGenericMimic(access, pos, materialOrder, DirtHelper.getSoilFlags(new String[]{ModContent.CORRUPTDIRTLIKE}), ModBlocks.dirtDefiled.getDefaultState());
   }

   public boolean canRenderInLayer(IBlockState state, BlockRenderLayer layer) {
      return layer == BlockRenderLayer.CUTOUT_MIPPED || layer == BlockRenderLayer.SOLID;
   }
}
