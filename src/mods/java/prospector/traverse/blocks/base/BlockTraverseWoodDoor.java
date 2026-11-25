package prospector.traverse.blocks.base;

import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import javax.annotation.Nullable;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.SoundType;
import net.minecraft.block.BlockDoor.EnumDoorHalf;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import prospector.traverse.core.TraverseTab;
import prospector.traverse.shadow.shootingstar.ShootingStar;
import prospector.traverse.shadow.shootingstar.model.ModelCompound;

public class BlockTraverseWoodDoor extends BlockDoor {
   public BlockTraverseWoodDoor(String name) {
      super(Material.WOOD);
      this.setRegistryName(new ResourceLocation("traverse", name + "_door"));
      this.setUnlocalizedName(this.getRegistryName().toString());
      this.setCreativeTab(TraverseTab.TAB);
      this.setSoundType(SoundType.WOOD);
      this.setHardness(3.0F);
      this.setHarvestLevel("axe", 0);
      ShootingStar.registerModel(new ModelCompound("traverse", this, "door", new IProperty[]{POWERED}));
   }

   public SoundType getSoundType(IBlockState state, World world, BlockPos pos, @Nullable Entity entity) {
      return SoundType.WOOD;
   }

   public SoundType getSoundType() {
      return SoundType.WOOD;
   }

   public Item getItemDropped(IBlockState state, EaglercraftRandom rand, int fortune) {
      return state.getValue(HALF) == EnumDoorHalf.UPPER ? Items.AIR : this.getDoorItem().getItem();
   }

   public ItemStack getItem(World worldIn, BlockPos pos, IBlockState state) {
      return this.getDoorItem();
   }

   private ItemStack getDoorItem() {
      return new ItemStack(Item.getItemFromBlock(this));
   }
}
