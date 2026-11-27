package maxhyper.dynamictreesdefiledlands.proxy;

import com.ferreusveritas.dynamictrees.api.TreeHelper;
import com.ferreusveritas.dynamictrees.api.client.ModelHelper;
import com.ferreusveritas.dynamictrees.blocks.BlockDynamicLeaves;
import com.ferreusveritas.dynamictrees.blocks.BlockRooty;
import com.ferreusveritas.dynamictrees.blocks.LeavesPaging;
import maxhyper.dynamictreesdefiledlands.ModContent;
import maxhyper.dynamictreesdefiledlands.event.EventListenerDefiledLands;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.color.BlockColors;
import net.minecraft.client.renderer.color.IBlockColor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ClientProxy extends CommonProxy {
   public ClientProxy() {
   }

   public void preInit() {
      super.preInit();
      (new EventListenerDefiledLands()).register(MinecraftForge.EVENT_BUS);
   }

   public void init() {
      super.init();
      this.registerColorHandlers();
   }

   public void postInit() {
      super.postInit();
   }

   public void registerColorHandlers() {
      for(BlockDynamicLeaves leaves : LeavesPaging.getLeavesMapForModId("dynamictreesdefiledlands").values()) {
         ModelHelper.regColorHandler(leaves, new IBlockColor() {
            public int colorMultiplier(IBlockState state, IBlockAccess worldIn, BlockPos pos, int tintIndex) {
               Block block = state.getBlock();
               return TreeHelper.isLeaves(block) ? ((BlockDynamicLeaves)block).getProperties(state).foliageColorMultiplier(state, worldIn, pos) : 16711935;
            }
         });
      }

      BlockColors blockColors = Minecraft.getMinecraft().getBlockColors();
      blockColors.registerBlockColorHandler((state, world, pos, tintIndex) -> {
         switch(tintIndex) {
         case 0:
            return blockColors.colorMultiplier(ModContent.rootyDefiledDirt.getMimic(world, pos), world, pos, tintIndex);
         case 1:
            return state.getBlock() instanceof BlockRooty ? ((BlockRooty)state.getBlock()).rootColor(state, world, pos) : -1;
         default:
            return -1;
         }
      }, new Block[]{ModContent.rootyDefiledDirt});
   }
}
