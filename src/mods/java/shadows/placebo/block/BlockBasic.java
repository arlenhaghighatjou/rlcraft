package shadows.placebo.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.event.ModelRegistryEvent;
import shadows.placebo.Placebo;
import shadows.placebo.client.IHasModel;
import shadows.placebo.interfaces.IItemBlock;
import shadows.placebo.itemblock.ItemBlockBase;
import shadows.placebo.registry.RegistryInformation;
import shadows.placebo.util.PlaceboUtil;

public abstract class BlockBasic extends Block implements IHasModel, IItemBlock {
   public BlockBasic(String name, Material material, float hardness, float resist, RegistryInformation info) {
      super(material);
      this.setRegistryName(info.getID(), name);
      this.setUnlocalizedName(info.getID() + "." + name);
      this.setHardness(hardness);
      this.setResistance(resist);
      this.setCreativeTab(info.getDefaultTab());
      info.getBlockList().add(this);
      ItemBlock ib = this.createItemBlock();
      if (ib != null) {
         info.getItemList().add(ib);
      }

   }

   public ItemBlock createItemBlock() {
      return new ItemBlockBase(this);
   }

   public boolean canSilkHarvest(World world, BlockPos pos, IBlockState state, EntityPlayer player) {
      return false;
   }

   public void initModels(ModelRegistryEvent e) {
      PlaceboUtil.sMRL("blocks", this, 0, "type=" + this.getRegistryName().getResourcePath());
      Placebo.PROXY.useRenamedMapper(this, "blocks", "", "type=" + this.getRegistryName().getResourcePath());
   }
}
