package rustic.common.blocks;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.SoundType;
import net.minecraft.block.BlockDoor.EnumDoorHalf;
import net.minecraft.block.BlockDoor.EnumHingePosition;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMap.Builder;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemDoor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import rustic.core.Rustic;

public class BlockDoorRustic extends BlockDoor {
   protected Item item;

   protected BlockDoorRustic(Material materialIn, String name) {
      super(materialIn);
      this.setRegistryName(name);
      this.setUnlocalizedName("rustic." + name);
      this.setCreativeTab(Rustic.decorTab);
      Item item = (new ItemDoor(this) {
      }).setCreativeTab(Rustic.decorTab);
      item.setRegistryName(this.getRegistryName());
      item.setUnlocalizedName(this.getUnlocalizedName());
      GameRegistry.findRegistry(Block.class).register(this);
      GameRegistry.findRegistry(Item.class).register(item);
      this.item = item;
      this.setHardness(3.0F);
      this.setSoundType(SoundType.WOOD);
      this.setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH).withProperty(OPEN, Boolean.valueOf(false)).withProperty(HINGE, EnumHingePosition.LEFT).withProperty(POWERED, Boolean.valueOf(false)).withProperty(HALF, EnumDoorHalf.LOWER));
   }

   public MapColor getMapColor(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
      return state.getMaterial().getMaterialMapColor();
   }

   public Item getItemDropped(IBlockState state, Random rand, int fortune) {
      return state.getValue(HALF) == EnumDoorHalf.UPPER ? Items.AIR : this.getItem();
   }

   public ItemStack getItem(World worldIn, BlockPos pos, IBlockState state) {
      return new ItemStack(this.getItem());
   }

   private Item getItem() {
      return this.item;
   }

   @SideOnly(Side.CLIENT)
   public void initModel() {
      ModelLoader.setCustomStateMapper(this, (new Builder()).ignore(new IProperty[]{BlockDoor.POWERED}).build());
      ModelLoader.setCustomModelResourceLocation(this.getItem(), 0, new ModelResourceLocation(this.getRegistryName(), "inventory"));
   }
}
