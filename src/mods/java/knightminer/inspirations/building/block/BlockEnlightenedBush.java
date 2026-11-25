package knightminer.inspirations.building.block;

import java.util.Locale;
import javax.annotation.Nonnull;
import knightminer.inspirations.building.tileentity.TileEnlightenedBush;
import knightminer.inspirations.library.client.ClientUtil;
import knightminer.inspirations.library.util.TextureBlockUtil;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import slimeknights.mantle.block.EnumBlock;
import slimeknights.mantle.block.EnumBlock.IEnumMeta;
import slimeknights.mantle.property.PropertyString;

public class BlockEnlightenedBush extends EnumBlock<BlockEnlightenedBush.LightsType> implements ITileEntityProvider {
   public static final PropertyEnum<BlockEnlightenedBush.LightsType> LIGHTS = PropertyEnum.create("lights", BlockEnlightenedBush.LightsType.class);
   public static final PropertyString TEXTURE = TextureBlockUtil.TEXTURE_PROP;

   public BlockEnlightenedBush() {
      super(Material.LEAVES, LIGHTS, BlockEnlightenedBush.LightsType.class);
      this.setCreativeTab(CreativeTabs.DECORATIONS);
      this.setHardness(0.2F);
      this.setLightOpacity(1);
      this.setLightLevel(1.0F);
      this.setSoundType(SoundType.PLANT);
      this.hasTileEntity = true;
   }

   @Nonnull
   protected ExtendedBlockState createBlockState() {
      return new ExtendedBlockState(this, new IProperty[]{LIGHTS}, new IUnlistedProperty[]{TEXTURE});
   }

   public TileEntity createNewTileEntity(World worldIn, int meta) {
      return new TileEnlightenedBush();
   }

   public void getSubBlocks(CreativeTabs tab, NonNullList<ItemStack> list) {
      for(BlockEnlightenedBush.LightsType type : BlockEnlightenedBush.LightsType.values()) {
         TextureBlockUtil.addBlocksFromOredict("treeLeaves", this, type.getMeta(), list);
      }

   }

   /** @deprecated */
   @Deprecated
   public boolean isOpaqueCube(IBlockState state) {
      return false;
   }

   /** @deprecated */
   @Deprecated
   public boolean causesSuffocation(IBlockState state) {
      return false;
   }

   @SideOnly(Side.CLIENT)
   public BlockRenderLayer getBlockLayer() {
      return BlockRenderLayer.CUTOUT;
   }

   @Nonnull
   public IBlockState getExtendedState(@Nonnull IBlockState state, IBlockAccess world, BlockPos pos) {
      return ClientUtil.writeTextureBlockState(world, pos, state);
   }

   public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
      super.onBlockPlacedBy(world, pos, state, placer, stack);
      TextureBlockUtil.placeTextureBlock(world, pos, stack);
   }

   public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
      drops.add(TextureBlockUtil.getBlockItemStack(world, pos, state));
   }

   @Nonnull
   public ItemStack getPickBlock(@Nonnull IBlockState state, RayTraceResult target, @Nonnull World world, @Nonnull BlockPos pos, EntityPlayer player) {
      return TextureBlockUtil.getBlockItemStack(world, pos, state);
   }

   public boolean removedByPlayer(@Nonnull IBlockState state, @Nonnull World world, @Nonnull BlockPos pos, @Nonnull EntityPlayer player, boolean willHarvest) {
      this.onBlockDestroyedByPlayer(world, pos, state);
      if (willHarvest) {
         this.harvestBlock(world, player, pos, state, world.getTileEntity(pos), player.getHeldItemMainhand());
      }

      world.setBlockToAir(pos);
      return false;
   }

   public static enum LightsType implements IStringSerializable, IEnumMeta {
      WHITE,
      RED(12517376),
      GREEN(2522880),
      BLUE(7359),
      RAINBOW,
      CHRISTMAS;

      private int meta;
      private int color;

      private LightsType() {
         this(-1);
      }

      private LightsType(int color) {
         this.meta = this.ordinal();
         this.color = color;
      }

      public int getMeta() {
         return this.meta;
      }

      public int getColor() {
         return this.color;
      }

      public String getName() {
         return this.name().toLowerCase(Locale.US);
      }

      public static BlockEnlightenedBush.LightsType fromMeta(int meta) {
         if (meta < 0 || meta >= values().length) {
            meta = 0;
         }

         return values()[meta];
      }
   }
}
