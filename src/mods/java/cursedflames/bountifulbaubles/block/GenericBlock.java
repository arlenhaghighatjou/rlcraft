package cursedflames.bountifulbaubles.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;

public abstract class GenericBlock extends Block {
   public GenericBlock(String modId, String unlocalizedName, CreativeTabs tab, Material mat, float hardness, float resistance) {
      super(mat);
      this.setUnlocalizedName(modId + "." + unlocalizedName);
      this.setRegistryName(unlocalizedName);
      if (tab != null) {
         this.setCreativeTab(tab);
      }

      this.setHardness(hardness);
      this.setResistance(resistance);
   }

   public GenericBlock(String modId, String unlocalizedName, CreativeTabs tab, Material mat) {
      this(modId, unlocalizedName, tab, mat, 2.0F, 40.0F);
   }

   public GenericBlock(String modId, String unlocalizedName, CreativeTabs tab) {
      this(modId, unlocalizedName, tab, Material.ROCK);
   }
}
