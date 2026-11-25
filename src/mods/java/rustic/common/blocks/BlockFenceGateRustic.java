package rustic.common.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockFenceGate;
import net.minecraft.block.BlockPlanks.EnumType;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMap.Builder;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import rustic.core.Rustic;

public class BlockFenceGateRustic extends BlockFenceGate {
   public BlockFenceGateRustic(IBlockState state, String name) {
      super(EnumType.OAK);
      this.setRegistryName(name);
      this.setUnlocalizedName("rustic." + name);
      GameRegistry.findRegistry(Block.class).register(this);
      GameRegistry.findRegistry(Item.class).register((new ItemBlock(this)).setRegistryName(this.getRegistryName()));
      this.setHardness(2.0F);
      this.setSoundType(state.getBlock().getSoundType());
      this.setCreativeTab(Rustic.decorTab);
      Blocks.FIRE.setFireInfo(this, 5, 20);
   }

   @SideOnly(Side.CLIENT)
   public void initModel() {
      ModelLoader.setCustomStateMapper(this, (new Builder()).ignore(new IProperty[]{BlockFenceGate.POWERED}).build());
      ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), 0, new ModelResourceLocation(this.getRegistryName().toString(), "inventory"));
   }
}
