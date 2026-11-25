package com.github.alexthe666.iceandfire.block;

import com.github.alexthe666.iceandfire.IceAndFire;
import com.github.alexthe666.iceandfire.core.ModBlocks;
import com.github.alexthe666.iceandfire.core.ModItems;
import com.github.alexthe666.iceandfire.core.ModSounds;
import com.github.alexthe666.iceandfire.entity.tile.TileEntityJar;
import com.github.alexthe666.iceandfire.item.ICustomRendered;
import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockJar extends BlockContainer implements ICustomRendered {
   private static AxisAlignedBB AABB = new AxisAlignedBB(0.1875D, 0.0D, 0.1875D, 0.8125D, 1.0D, 0.8125D);
   public Item itemBlock;
   private boolean empty;

   public BlockJar(boolean empty) {
      super(Material.GLASS);
      this.setHardness(1.0F);
      this.setResistance(2.0F);
      this.setSoundType(SoundType.GLASS);
      this.setCreativeTab(IceAndFire.TAB);
      this.setUnlocalizedName("iceandfire.jar" + (empty ? "_empty" : "_pixie"));
      this.setRegistryName("iceandfire", "jar" + (empty ? "_empty" : "_pixie"));
      if (!empty) {
         this.setLightLevel(0.75F);
         GameRegistry.registerTileEntity(TileEntityJar.class, "jar");
      }

      this.empty = empty;
   }

   public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
      return AABB;
   }

   public boolean isOpaqueCube(IBlockState blockstate) {
      return false;
   }

   public boolean isFullCube(IBlockState blockstate) {
      return false;
   }

   public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
      IBlockState iblockstate = worldIn.getBlockState(pos.down());
      return iblockstate.isSideSolid(worldIn, pos, EnumFacing.UP) || iblockstate.getBlock().canPlaceTorchOnTop(iblockstate, worldIn, pos);
   }

   public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
      this.dropPixie(worldIn, pos);
      super.breakBlock(worldIn, pos, state);
   }

   public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
      worldIn.scheduleUpdate(pos, this, this.tickRate(worldIn));
   }

   public void updateTick(World world, BlockPos pos, IBlockState state, EaglercraftRandom rand) {
      this.checkFall(world, pos);
   }

   private boolean checkFall(World worldIn, BlockPos pos) {
      if (!this.canPlaceBlockAt(worldIn, pos)) {
         worldIn.destroyBlock(pos, true);
         this.dropPixie(worldIn, pos);
         return false;
      } else {
         return true;
      }
   }

   public void dropPixie(World world, BlockPos pos) {
      if (world.getTileEntity(pos) != null && world.getTileEntity(pos) instanceof TileEntityJar && ((TileEntityJar)world.getTileEntity(pos)).hasPixie) {
         ((TileEntityJar)world.getTileEntity(pos)).releasePixie();
      }

   }

   public Item getItemDropped(IBlockState state, EaglercraftRandom rand, int fortune) {
      return Item.getItemFromBlock(ModBlocks.jar_empty);
   }

   protected ItemStack getSilkTouchDrop(IBlockState state) {
      return new ItemStack(ModBlocks.jar_empty);
   }

   public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
      if (!this.empty && world.getTileEntity(pos) != null && world.getTileEntity(pos) instanceof TileEntityJar && ((TileEntityJar)world.getTileEntity(pos)).hasPixie && ((TileEntityJar)world.getTileEntity(pos)).hasProduced) {
         ((TileEntityJar)world.getTileEntity(pos)).hasProduced = false;
         EntityItem item = new EntityItem(world, (double)pos.getX() + 0.5D, (double)pos.getY() + 0.5D, (double)pos.getZ() + 0.5D, new ItemStack(ModItems.pixie_dust));
         if (!world.isRemote) {
            world.spawnEntity(item);
         }

         world.playSound((double)pos.getX() + 0.5D, (double)pos.getY() + 0.5D, (double)pos.getZ() + 0.5D, ModSounds.PIXIE_HURT, SoundCategory.NEUTRAL, 1.0F, 1.0F, false);
      }

      return false;
   }

   @SideOnly(Side.CLIENT)
   public BlockRenderLayer getBlockLayer() {
      return BlockRenderLayer.TRANSLUCENT;
   }

   public EnumBlockRenderType getRenderType(IBlockState state) {
      return EnumBlockRenderType.MODEL;
   }

   public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
      if (world.getTileEntity(pos) != null && world.getTileEntity(pos) instanceof TileEntityJar) {
         if (!this.empty) {
            ((TileEntityJar)world.getTileEntity(pos)).hasPixie = true;
            ((TileEntityJar)world.getTileEntity(pos)).pixieType = stack.getMetadata();
         } else {
            ((TileEntityJar)world.getTileEntity(pos)).hasPixie = false;
         }
      }

   }

   public TileEntity createNewTileEntity(World world, int meta) {
      return new TileEntityJar(this.empty);
   }

   public class ItemBlockJar extends ItemBlock {
      public ItemBlockJar(Block block) {
         super(block);
         this.maxStackSize = 1;
         this.setHasSubtypes(true);
      }

      public String getUnlocalizedName(ItemStack stack) {
         int i = stack.getMetadata();
         return "tile.iceandfire.jar_" + i;
      }

      @SideOnly(Side.CLIENT)
      public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> subItems) {
         if (tab == IceAndFire.TAB) {
            for(int i = 0; i < 5; ++i) {
               subItems.add(new ItemStack(this, 1, i));
            }
         }

      }
   }
}
