package rustic.common.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockStairs;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.registry.GameRegistry;
import rustic.core.Rustic;

public class BlockStairsBase extends BlockStairs {
   public BlockStairsBase(IBlockState state, String name) {
      super(state);
      this.setLightOpacity(0);
      this.setRegistryName(name);
      this.setUnlocalizedName("rustic." + name);
      GameRegistry.findRegistry(Block.class).register(this);
      GameRegistry.findRegistry(Item.class).register((new ItemBlock(this)).setRegistryName(this.getRegistryName()));
      this.setCreativeTab(Rustic.decorTab);
   }

   public void initModel() {
      ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), 0, new ModelResourceLocation(this.getRegistryName().toString(), "inventory"));
   }
}
