package net.mcft.copy.backpacks.block;

import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import javax.annotation.Nullable;
import net.mcft.copy.backpacks.api.BackpackHelper;
import net.mcft.copy.backpacks.api.IBackpack;
import net.mcft.copy.backpacks.block.entity.TileEntityBackpack;
import net.mcft.copy.backpacks.misc.util.LangUtils;
import net.mcft.copy.backpacks.misc.util.MiscUtils;
import net.mcft.copy.backpacks.misc.util.WorldUtils;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockBackpack extends BlockContainer {
   public static final int EXPLOSION_RESIST_TICKS = 10;
   private final AxisAlignedBB[] _boundsFromFacing = new AxisAlignedBB[4];
   private long _lastHelpMessage = System.currentTimeMillis();

   public BlockBackpack() {
      super(Material.CLOTH);
      this.setSoundType(SoundType.SNOW);
      this.setHardness(1.5F);
      this.initBlockBounds();
   }

   public String getUnlocalizedName() {
      return MiscUtils.getItemFromBlock(this).getUnlocalizedName();
   }

   public TileEntity createNewTileEntity(World worldIn, int meta) {
      return new TileEntityBackpack();
   }

   public int quantityDropped(EaglercraftRandom random) {
      return 0;
   }

   public boolean isOpaqueCube(IBlockState state) {
      return false;
   }

   public boolean isFullCube(IBlockState state) {
      return false;
   }

   public EnumBlockRenderType getRenderType(IBlockState state) {
      return EnumBlockRenderType.INVISIBLE;
   }

   protected float getBoundsWidth() {
      return 0.75F;
   }

   protected float getBoundsHeight() {
      return 0.8125F;
   }

   protected float getBoundsDepth() {
      return 0.625F;
   }

   public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
      TileEntity entity = source.getTileEntity(pos);
      EnumFacing facing = entity instanceof TileEntityBackpack ? ((TileEntityBackpack)entity).facing : EnumFacing.NORTH;
      return this._boundsFromFacing[facing.ordinal() - 2];
   }

   private void initBlockBounds() {
      float w = this.getBoundsWidth();
      float h = this.getBoundsHeight();
      float d = this.getBoundsDepth();

      for(int i = 0; i < this._boundsFromFacing.length; ++i) {
         EnumFacing facing = EnumFacing.getFront(i + 2);
         this._boundsFromFacing[i] = facing.getAxis() == Axis.Z ? new AxisAlignedBB((double)(0.5F - w / 2.0F), 0.0D, (double)(0.5F - d / 2.0F), (double)(0.5F + w / 2.0F), (double)h, (double)(0.5F + d / 2.0F)) : new AxisAlignedBB((double)(0.5F - d / 2.0F), 0.0D, (double)(0.5F - w / 2.0F), (double)(0.5F + d / 2.0F), (double)h, (double)(0.5F + w / 2.0F));
      }

   }

   public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player) {
      IBackpack backpack = BackpackHelper.getBackpack(world.getTileEntity(pos));
      return backpack != null ? backpack.getStack().copy() : super.getPickBlock(state, target, world, pos, player);
   }

   public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
      TileEntity tileEntity = worldIn.getTileEntity(pos);
      if (tileEntity instanceof TileEntityBackpack) {
         ((TileEntityBackpack)tileEntity).facing = placer.getHorizontalFacing();
      }

   }

   public float getPlayerRelativeBlockHardness(IBlockState state, EntityPlayer player, World worldIn, BlockPos pos) {
      float hardness = super.getPlayerRelativeBlockHardness(state, player, worldIn, pos);
      return hardness * 2.0F;
   }

   public void onBlockClicked(World world, BlockPos pos, EntityPlayer player) {
      if (world.isRemote && player.isSneaking() && !BackpackHelper.canEquipBackpack(player) && System.currentTimeMillis() > this._lastHelpMessage + 10000L) {
         boolean backpack = BackpackHelper.getBackpack(player) != null;
         LangUtils.displayChatMessage("cantEquip" + (!backpack ? ".bauble" : ""));
         this._lastHelpMessage = System.currentTimeMillis();
      }

   }

   public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
      if (worldIn.isRemote) {
         return true;
      } else {
         TileEntity entity = worldIn.getTileEntity(pos);
         IBackpack backpack = BackpackHelper.getBackpack(entity);
         if (backpack != null) {
            backpack.getType().onPlacedInteract(player, entity, backpack);
         }

         return true;
      }
   }

   public void onBlockHarvested(World worldIn, BlockPos pos, IBlockState state, EntityPlayer player) {
      if (!worldIn.isRemote && player.isSneaking() && BackpackHelper.canEquipBackpack(player)) {
         BackpackHelper.equipBackpack(player, worldIn.getTileEntity(pos));
      }

   }

   protected void dropBackpack(World worldIn, BlockPos pos) {
      this.dropBackpack(worldIn, pos, worldIn.getTileEntity(pos));
   }

   protected void dropBackpack(World worldIn, BlockPos pos, TileEntity entity) {
      if (!worldIn.isRemote) {
         IBackpack backpack = BackpackHelper.getBackpack(entity);
         if (backpack != null && backpack.getType() != null && !backpack.getStack().isEmpty()) {
            backpack.getType().onBlockBreak(entity, backpack);
            WorldUtils.dropStackFromBlock(worldIn, pos, backpack.getStack());
         }
      }

   }

   protected boolean preventExplosionDestroy(World world, BlockPos pos) {
      TileEntity entity = world.getTileEntity(pos);
      return entity instanceof TileEntityBackpack && ((TileEntityBackpack)entity).getAge() < 0;
   }

   public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
   }

   public void dropBlockAsItemWithChance(World world, BlockPos pos, IBlockState state, float chance, int fortune) {
      if (!this.preventExplosionDestroy(world, pos) && chance > 0.0F) {
         this.dropBackpack(world, pos);
      }

   }

   public void harvestBlock(World worldIn, EntityPlayer player, BlockPos pos, IBlockState state, @Nullable TileEntity te, ItemStack stack) {
      player.addStat(StatList.getBlockStats(this));
      player.addExhaustion(0.005F);
      this.harvesters.set(player);
      this.dropBackpack(worldIn, pos, te);
      this.harvesters.set((Object)null);
   }

   public void onBlockExploded(World world, BlockPos pos, Explosion explosion) {
      if (!this.preventExplosionDestroy(world, pos)) {
         super.onBlockExploded(world, pos, explosion);
      }
   }
}
