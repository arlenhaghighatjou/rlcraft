package prospector.traverse.blocks.base;

import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import prospector.traverse.core.TraverseTab;
import prospector.traverse.shadow.shootingstar.ShootingStar;
import prospector.traverse.shadow.shootingstar.model.ModelCompound;

public class BlockTraverse extends Block {
   public ResourceLocation drop;

   public BlockTraverse(String name, Material material, SoundType soundType, ResourceLocation drop) {
      super(material);
      this.drop = null;
      this.drop = drop;
      this.setRegistryName(new ResourceLocation("traverse", name));
      this.setUnlocalizedName(this.getRegistryName().toString());
      this.setCreativeTab(TraverseTab.TAB);
      this.setHardness(1.5F);
      this.setResistance(10.0F);
      this.setHarvestLevel("pickaxe", 0);
      this.setSoundType(soundType);
      ShootingStar.registerModel(new ModelCompound("traverse", this, new IProperty[0]));
   }

   public BlockTraverse(String name, Material material, SoundType soundType) {
      this(name, material, soundType, (ResourceLocation)null);
   }

   public Item getItemDropped(IBlockState state, EaglercraftRandom rand, int fortune) {
      return this.drop != null ? (Item)ForgeRegistries.ITEMS.getValue(this.drop) : super.getItemDropped(state, rand, fortune);
   }
}
