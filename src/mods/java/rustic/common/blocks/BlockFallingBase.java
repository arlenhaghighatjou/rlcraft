package rustic.common.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockFalling;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.registry.GameRegistry;
import rustic.core.Rustic;

public class BlockFallingBase extends BlockFalling {
   public BlockFallingBase(Material mat, String name) {
      this(mat, name, true);
   }

   public BlockFallingBase(Material mat, String name, boolean register) {
      super(mat);
      this.setRegistryName(name);
      this.setUnlocalizedName("rustic." + name);
      this.setHardness(1.0F);
      this.setCreativeTab(Rustic.decorTab);
      if (register) {
         GameRegistry.findRegistry(Block.class).register(this);
         GameRegistry.findRegistry(Item.class).register((new ItemBlock(this)).setRegistryName(this.getRegistryName()));
      }

   }

   public void initModel() {
      ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), 0, new ModelResourceLocation(this.getRegistryName().toString(), "inventory"));
   }
}
