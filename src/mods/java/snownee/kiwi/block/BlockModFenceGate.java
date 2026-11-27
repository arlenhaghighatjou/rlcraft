package snownee.kiwi.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockFenceGate;
import net.minecraft.block.BlockPlanks.EnumType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.statemap.StateMap.Builder;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockModFenceGate extends BlockFenceGate implements IModBlock {
   private final String name;
   private final IBlockState baseState;

   public BlockModFenceGate(String name, IBlockState baseState) {
      super(EnumType.OAK);
      this.name = name;
      this.baseState = baseState;
      this.setSoundType(baseState.getBlock().getSoundType());
   }

   @SideOnly(Side.CLIENT)
   public void mapModel() {
      ModelLoader.setCustomStateMapper(this, (new Builder()).ignore(new IProperty[]{BlockFenceGate.POWERED}).build());
      IModBlock.super.mapModel();
   }

   public Block cast() {
      return this;
   }

   public String getName() {
      return this.name;
   }

   public void register(String modid) {
      this.setRegistryName(modid, this.getName());
      this.setUnlocalizedName(modid + "." + this.getName());
   }

   public Material getMaterial(IBlockState state) {
      return this.baseState.getMaterial();
   }

   public MapColor getMapColor(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
      return this.baseState.getMapColor(worldIn, pos);
   }
}
