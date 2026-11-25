package svenhjol.charm.crafting.block;

import java.util.ArrayList;
import java.util.List;
import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.block.BlockDirectional;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.Mirror;
import net.minecraft.util.NonNullList;
import net.minecraft.util.Rotation;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Optional.Interface;
import svenhjol.charm.Charm;
import svenhjol.charm.base.CharmSounds;
import svenhjol.charm.crafting.feature.Barrel;
import svenhjol.charm.crafting.tile.TileBarrel;
import svenhjol.meson.MesonBlock;
import svenhjol.meson.MesonBlockTE;
import svenhjol.meson.iface.IMesonBlock;
import vazkii.quark.api.IRotationLockHandler;

@Interface(
   iface = "vazkii.quark.api.IRotationLockHandler",
   modid = "quark"
)
public class BlockBarrel extends MesonBlockTE<TileBarrel> implements IMesonBlock, IRotationLockHandler {
   public static final PropertyDirection FACING = BlockDirectional.FACING;
   public static PropertyEnum<MesonBlock.WoodVariant> VARIANT = PropertyEnum.create("variant", MesonBlock.WoodVariant.class);

   public BlockBarrel() {
      super(Material.WOOD, "barrel");
      this.setHardness(Barrel.hardness);
      this.setSoundType(SoundType.WOOD);
      this.setCreativeTab(CreativeTabs.DECORATIONS);
      this.setDefaultState(this.getBlockState().getBaseState().withProperty(FACING, EnumFacing.UP));
   }

   public String getModId() {
      return "charm";
   }

   public String[] getVariants() {
      List<String> variants = new ArrayList();

      for(MesonBlock.WoodVariant variant : MesonBlock.WoodVariant.values()) {
         variants.add(variant.toString().toLowerCase());
      }

      return (String[])variants.toArray(new String[0]);
   }

   public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
      TileBarrel barrel = (TileBarrel)this.getTileEntity(worldIn, pos);
      if (this.validTileEntity(barrel)) {
         EnumFacing facing = (EnumFacing)state.getValue(FACING);
         barrel.setFacing(facing);
         barrel.setName(stack.getDisplayName());
      }

      super.onBlockPlacedBy(worldIn, pos, state, placer, stack);
   }

   public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
      if (!worldIn.isRemote) {
         TileBarrel barrel = (TileBarrel)this.getTileEntity(worldIn, pos);
         if (!this.validTileEntity(barrel)) {
            return false;
         }

         if (playerIn.isSneaking()) {
            return false;
         }

         if (barrel.hasLootTable()) {
            barrel.generateLoot(playerIn);
         }

         playerIn.openGui(Charm.instance, 2, worldIn, pos.getX(), pos.getY(), pos.getZ());
      }

      if (worldIn.isRemote && !playerIn.isSneaking()) {
         playerIn.playSound(CharmSounds.WOOD_OPEN, 1.0F, 1.0F);
      }

      return true;
   }

   public IBlockState setRotation(World world, BlockPos pos, IBlockState state, EnumFacing facing, boolean hasHalf, boolean topHalf) {
      TileBarrel barrel = (TileBarrel)this.getTileEntity(world, pos);
      if (this.validTileEntity(barrel)) {
         barrel.setFacing(facing);
      }

      state = state.withProperty(FACING, facing);
      world.setBlockState(pos, state, 2);
      return state;
   }

   public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
      ItemStack thisBarrel = new ItemStack(this, 1, this.getMetaFromState(state));
      TileBarrel barrel = (TileBarrel)this.getTileEntity(world, pos);
      if (this.validTileEntity(barrel)) {
         this.dropsInventory(barrel, 27, (World)world, pos);
         thisBarrel.setStackDisplayName(barrel.getName());
      }

      drops.add(thisBarrel);
   }

   @Nonnull
   public Item getItemDropped(IBlockState state, EaglercraftRandom rand, int fortune) {
      return Items.AIR;
   }

   public boolean isFlammable(IBlockAccess world, BlockPos pos, EnumFacing face) {
      return true;
   }

   public int getFlammability(IBlockAccess world, BlockPos pos, EnumFacing face) {
      return 50;
   }

   public Class<TileBarrel> getTileEntityClass() {
      return TileBarrel.class;
   }

   @Nullable
   public TileBarrel createTileEntity(World world, IBlockState state) {
      return new TileBarrel();
   }

   public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
      return this.getDefaultState().withProperty(VARIANT, MesonBlock.WoodVariant.byMetadata(meta)).withProperty(FACING, facing);
   }

   public IBlockState withRotation(IBlockState state, Rotation rot) {
      return state.withProperty(FACING, rot.rotate((EnumFacing)state.getValue(FACING)));
   }

   public IBlockState withMirror(IBlockState state, Mirror mirrorIn) {
      return state.withProperty(FACING, mirrorIn.mirror((EnumFacing)state.getValue(FACING)));
   }

   public int getMetaFromState(IBlockState state) {
      return ((MesonBlock.WoodVariant)state.getValue(VARIANT)).ordinal();
   }

   public IBlockState getStateFromMeta(int meta) {
      return this.getDefaultState().withProperty(VARIANT, MesonBlock.WoodVariant.byMetadata(meta));
   }

   public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
      TileBarrel barrel = (TileBarrel)worldIn.getTileEntity(pos);
      if (barrel != null && this.validTileEntity(barrel)) {
         EnumFacing facing = barrel.getFacing();
         if (facing.getAxis() == Axis.Y) {
            facing = EnumFacing.UP;
         }

         state = state.withProperty(FACING, facing);
      }

      return state;
   }

   @Nonnull
   protected BlockStateContainer createBlockState() {
      return new BlockStateContainer(this, new IProperty[]{FACING, VARIANT});
   }

   public int damageDropped(IBlockState state) {
      return this.getMetaFromState(state);
   }

   public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player) {
      return new ItemStack(this, 1, this.getMetaFromState(world.getBlockState(pos)));
   }
}
