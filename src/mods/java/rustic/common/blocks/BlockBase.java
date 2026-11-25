package rustic.common.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.registry.GameRegistry;
import rustic.core.Rustic;

public class BlockBase extends Block {
   public BlockBase(Material mat, String name) {
      super(mat);
      this.setRegistryName(name);
      this.setUnlocalizedName("rustic." + name);
      this.register();
      this.setHardness(1.0F);
      this.setCreativeTab(Rustic.decorTab);
   }

   public BlockBase(Material mat, String name, boolean register) {
      super(mat);
      this.setRegistryName(name);
      this.setUnlocalizedName("rustic." + name);
      this.setHardness(1.0F);
      this.setCreativeTab(Rustic.decorTab);
      if (register) {
         this.register();
      }

   }

   public void register(Item item) {
      GameRegistry.findRegistry(Block.class).register(this);
      GameRegistry.findRegistry(Item.class).register(item);
   }

   public void register() {
      this.register((Item)(new ItemBlock(this)).setRegistryName(this.getRegistryName()));
   }

   public void initModel() {
      ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), 0, new ModelResourceLocation(this.getRegistryName().toString(), "inventory"));
   }

   public BlockBase setBlockSoundType(SoundType soundType) {
      this.setSoundType(soundType);
      return this;
   }
}
