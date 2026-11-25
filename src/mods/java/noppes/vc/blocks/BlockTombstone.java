package noppes.vc.blocks;

import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import noppes.vc.CommonUtils;
import noppes.vc.blocks.tiles.TileBigSign;
import noppes.vc.blocks.tiles.TileTombstone;
import noppes.vc.constants.GuiType;

public class BlockTombstone extends BlockBasicContainer {
   public static final PropertyInteger DAMAGE = PropertyInteger.create("damage", 0, 6);
   public static final AxisAlignedBB AABB1 = new AxisAlignedBB(0.0D, 0.0D, 0.30000001192092896D, 1.0D, 1.0D, 0.699999988079071D);
   public static final AxisAlignedBB AABB2 = new AxisAlignedBB(0.30000001192092896D, 0.0D, 0.0D, 0.699999988079071D, 1.0D, 1.0D);

   public BlockTombstone() {
      super(Material.ROCK);
      this.setSoundType(SoundType.STONE);
   }

   public int getMetaFromState(IBlockState state) {
      return this.damageDropped(state);
   }

   public IBlockState getStateFromMeta(int meta) {
      return this.getDefaultState().withProperty(DAMAGE, Integer.valueOf(meta));
   }

   protected BlockStateContainer createBlockState() {
      return new BlockStateContainer(this, new IProperty[]{DAMAGE});
   }

   public int damageDropped(IBlockState state) {
      return ((Integer)state.getValue(DAMAGE)).intValue();
   }

   public boolean onBlockActivated(World par1World, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
      if (par1World.isRemote) {
         return false;
      } else {
         ItemStack currentItem = player.inventory.getCurrentItem();
         if (currentItem.getItem().getUnlocalizedName().equals("item.npcwand")) {
            TileBigSign tile = (TileBigSign)par1World.getTileEntity(pos);
            if (tile.getBlockMetadata() >= 2) {
               return false;
            } else {
               tile.canEdit = true;
               CommonUtils.OpenGui(player, GuiType.BIG_SIGN, pos.getX(), pos.getY(), pos.getZ());
               return true;
            }
         } else {
            return false;
         }
      }
   }

   public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase entity, ItemStack stack) {
      world.setBlockState(pos, state.withProperty(DAMAGE, Integer.valueOf(stack.getItemDamage())), 2);
      int l = MathHelper.floor((double)(entity.rotationYaw * (float)this.maxRotation() / 360.0F) + 0.5D) & this.maxRotation() - 1;
      l = l % this.maxRotation();
      TileTombstone tile = (TileTombstone)world.getTileEntity(pos);
      tile.rotation = l;
      if (entity instanceof EntityPlayer && !world.isRemote && stack.getItemDamage() < 2) {
         CommonUtils.OpenGui((EntityPlayer)entity, GuiType.BIG_SIGN, pos.getX(), pos.getY(), pos.getZ());
      }

   }

   public int maxRotation() {
      return 4;
   }

   public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess world, BlockPos pos) {
      TileEntity tileentity = world.getTileEntity(pos);
      if (!(tileentity instanceof TileTombstone)) {
         return super.getBoundingBox(state, world, pos);
      } else {
         TileTombstone tile = (TileTombstone)tileentity;
         return tile.rotation % 2 == 0 ? AABB1 : AABB2;
      }
   }

   public boolean isOpaqueCube(IBlockState state) {
      return false;
   }

   public boolean isFullCube(IBlockState state) {
      return false;
   }

   public void getSubBlocks(CreativeTabs par2CreativeTabs, NonNullList<ItemStack> par3List) {
      par3List.add(new ItemStack(this, 1, 0));
      par3List.add(new ItemStack(this, 1, 1));
      par3List.add(new ItemStack(this, 1, 2));
   }

   public TileEntity createNewTileEntity(World var1, int var2) {
      return new TileTombstone();
   }
}
