package knightminer.inspirations.recipes.block;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Locale;
import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import javax.annotation.Nonnull;
import knightminer.inspirations.common.Config;
import knightminer.inspirations.library.InspirationsRegistry;
import knightminer.inspirations.library.Util;
import knightminer.inspirations.library.util.TextureBlockUtil;
import knightminer.inspirations.recipes.client.BoilingParticle;
import knightminer.inspirations.recipes.tileentity.TileCauldron;
import net.minecraft.block.Block;
import net.minecraft.block.BlockCauldron;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import slimeknights.mantle.property.PropertyString;

public class BlockEnhancedCauldron extends BlockCauldron implements ITileEntityProvider {
   public static final PropertyEnum<BlockEnhancedCauldron.CauldronContents> CONTENTS = PropertyEnum.create("contents", BlockEnhancedCauldron.CauldronContents.class);
   public static final PropertyInteger LEVELS = PropertyInteger.create("levels", 0, 4);
   public static final PropertyBool BOILING = PropertyBool.create("boiling");
   public static final PropertyString TEXTURE = TextureBlockUtil.TEXTURE_PROP;
   public static final AxisAlignedBB[] BOUNDS = new AxisAlignedBB[]{new AxisAlignedBB(0.0D, 0.1875D, 0.0D, 1.0D, 1.0D, 1.0D), new AxisAlignedBB(0.0D, 0.0D, 0.0D, 0.25D, 0.1875D, 0.125D), new AxisAlignedBB(0.0D, 0.0D, 0.125D, 0.125D, 0.1875D, 0.25D), new AxisAlignedBB(0.75D, 0.0D, 0.0D, 1.0D, 0.1875D, 0.125D), new AxisAlignedBB(0.875D, 0.0D, 0.125D, 1.0D, 0.1875D, 0.25D), new AxisAlignedBB(0.0D, 0.0D, 0.875D, 0.25D, 0.1875D, 1.0D), new AxisAlignedBB(0.0D, 0.0D, 0.75D, 0.125D, 0.1875D, 0.875D), new AxisAlignedBB(0.75D, 0.0D, 0.875D, 1.0D, 0.1875D, 1.0D), new AxisAlignedBB(0.875D, 0.0D, 0.75D, 1.0D, 0.1875D, 0.875D)};

   public BlockEnhancedCauldron() {
      IBlockState state = this.blockState.getBaseState().withProperty(LEVEL, Integer.valueOf(0)).withProperty(BOILING, Boolean.valueOf(false)).withProperty(CONTENTS, BlockEnhancedCauldron.CauldronContents.FLUID);
      if (Config.enableBiggerCauldron) {
         state = state.withProperty(LEVELS, Integer.valueOf(0));
      }

      this.setDefaultState(state);
      this.setHardness(2.0F);
      this.setUnlocalizedName("cauldron");
      this.hasTileEntity = true;
   }

   public TileEntity createNewTileEntity(World worldIn, int meta) {
      return new TileCauldron();
   }

   public void fillWithRain(World world, BlockPos pos) {
      TileEntity te = world.getTileEntity(pos);
      if (!(te instanceof TileCauldron) || ((TileCauldron)te).isWater()) {
         if ((Config.fasterCauldronRain || world.rand.nextInt(20) == 0) && world.getBiomeProvider().getTemperatureAtHeight(world.getBiome(pos).getTemperature(pos), pos.getY()) >= 0.15F) {
            IBlockState state = world.getBlockState(pos);
            int level = this.getLevel(state);
            if (level < (Config.enableBiggerCauldron ? 4 : 3)) {
               this.setWaterLevel(world, pos, state, level + 1);
            }
         }

      }
   }

   public void onEntityCollidedWithBlock(World world, BlockPos pos, IBlockState state, Entity entity) {
      TileEntity te = world.getTileEntity(pos);
      if (te instanceof TileCauldron) {
         if (!world.isRemote) {
            int level = this.getLevel(state);
            float f = (float)pos.getY() + ((Config.enableBiggerCauldron ? 2.5F : 5.5F) + (float)(3 * Math.max(level, 1))) / 16.0F;
            if (entity.getEntityBoundingBox().minY <= (double)f) {
               int newLevel = ((TileCauldron)te).onEntityCollide(entity, level, state);
               if (level != newLevel) {
                  this.setWaterLevel(world, pos, state, newLevel);
               }
            }

         }
      }
   }

   public void breakBlock(World world, BlockPos pos, IBlockState state) {
      int level = this.getLevel(state);
      if (Config.dropCauldronContents && level != 0) {
         TileEntity te = world.getTileEntity(pos);
         if (te instanceof TileCauldron) {
            ((TileCauldron)te).onBreak(pos, level);
         }

         super.breakBlock(world, pos, state);
      }
   }

   public boolean removedByPlayer(IBlockState state, World world, BlockPos pos, EntityPlayer player, boolean willHarvest) {
      this.onBlockHarvested(world, pos, state, player);
      world.setBlockState(pos, Blocks.AIR.getDefaultState(), world.isRemote ? 11 : 3);
      return world.getBlockState(pos).getBlock() != Blocks.CAULDRON;
   }

   protected ExtendedBlockState createBlockState() {
      return Config.enableBiggerCauldron ? new ExtendedBlockState(this, new IProperty[]{LEVEL, LEVELS, CONTENTS, BOILING}, new IUnlistedProperty[]{TEXTURE}) : new ExtendedBlockState(this, new IProperty[]{LEVEL, CONTENTS, BOILING}, new IUnlistedProperty[]{TEXTURE});
   }

   /** @deprecated */
   @Deprecated
   public IBlockState getActualState(IBlockState state, IBlockAccess world, BlockPos pos) {
      TileEntity te = world.getTileEntity(pos);
      if (te instanceof TileCauldron) {
         state = state.withProperty(CONTENTS, ((TileCauldron)te).getContentType());
      }

      return state;
   }

   @Nonnull
   public IBlockState getExtendedState(@Nonnull IBlockState state, IBlockAccess world, BlockPos pos) {
      IExtendedBlockState extendedState = (IExtendedBlockState)state;
      TileEntity te = world.getTileEntity(pos);
      return te instanceof TileCauldron ? ((TileCauldron)te).writeExtendedBlockState(extendedState) : super.getExtendedState(state, world, pos);
   }

   public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
      return true;
   }

   /** @deprecated */
   @Deprecated
   public void neighborChanged(IBlockState state, World world, BlockPos pos, Block blockIn, BlockPos fromPos) {
      setBoiling(world, pos, state);
   }

   public void onBlockAdded(World world, BlockPos pos, IBlockState state) {
      setBoiling(world, pos, state);
   }

   private static void setBoiling(World world, BlockPos pos, IBlockState state) {
      world.setBlockState(pos, state.withProperty(BOILING, Boolean.valueOf(InspirationsRegistry.isCauldronFire(world.getBlockState(pos.down())))));
   }

   @SideOnly(Side.CLIENT)
   public void randomDisplayTick(IBlockState state, World world, BlockPos pos, EaglercraftRandom rand) {
      if (((Boolean)state.getValue(BOILING)).booleanValue()) {
         int level = this.getLevel(state);
         if (level != 0) {
            ParticleManager manager = Minecraft.getMinecraft().effectRenderer;

            for(int i = 0; i < 2; ++i) {
               double x = (double)pos.getX() + 0.1875D + (double)rand.nextFloat() * 0.625D;
               double y = (double)pos.getY() + (Config.enableBiggerCauldron ? 0.1875D : 0.375D) + (double)level * 0.1875D;
               double z = (double)pos.getZ() + 0.1875D + (double)rand.nextFloat() * 0.625D;
               manager.addEffect(new BoilingParticle(world, x, y, z, 0.0D, 0.0D, 0.0D));
            }

         }
      }
   }

   public void setWaterLevel(World worldIn, BlockPos pos, IBlockState state, int level) {
      if (Config.enableBiggerCauldron) {
         state = state.withProperty(LEVELS, Integer.valueOf(MathHelper.clamp(level, 0, 4)));
      }

      worldIn.setBlockState(pos, state.withProperty(LEVEL, Integer.valueOf(MathHelper.clamp(level, 0, 3))), 2);
      worldIn.updateComparatorOutputLevel(pos, this);
   }

   public static int getCauldronLevel(IBlockState state) {
      Block block = state.getBlock();
      if (state.getBlock() instanceof BlockEnhancedCauldron) {
         return ((BlockEnhancedCauldron)block).getLevel(state);
      } else {
         return state.getPropertyKeys().contains(LEVEL) ? ((Integer)state.getValue(LEVEL)).intValue() : InspirationsRegistry.getCauldronMax();
      }
   }

   public int getLevel(IBlockState state) {
      return Config.enableBiggerCauldron ? ((Integer)state.getValue(LEVELS)).intValue() : ((Integer)state.getValue(LEVEL)).intValue();
   }

   public int getComparatorInputOverride(IBlockState state, World worldIn, BlockPos pos) {
      return this.getLevel(state);
   }

   public IBlockState getStateFromMeta(int meta) {
      IBlockState state = this.getDefaultState().withProperty(LEVEL, Integer.valueOf(MathHelper.clamp(meta & 7, 0, 3)));
      if (Config.enableBiggerCauldron) {
         state = state.withProperty(LEVELS, Integer.valueOf(MathHelper.clamp(meta & 7, 0, 4)));
      }

      state = state.withProperty(BOILING, Boolean.valueOf((meta & 8) > 0));
      return state;
   }

   public int getMetaFromState(IBlockState state) {
      return this.getLevel(state) | (((Boolean)state.getValue(BOILING)).booleanValue() ? 8 : 0);
   }

   /** @deprecated */
   @Deprecated
   public RayTraceResult collisionRayTrace(IBlockState state, @Nonnull World world, @Nonnull BlockPos pos, @Nonnull Vec3d start, @Nonnull Vec3d end) {
      List<RayTraceResult> list = Lists.newArrayList();

      for(AxisAlignedBB axisalignedbb : BOUNDS) {
         list.add(this.rayTrace(pos, start, end, axisalignedbb));
      }

      return Util.closestResult(list, end);
   }

   public static enum CauldronContents implements IStringSerializable {
      FLUID,
      DYE,
      POTION;

      private int meta = this.ordinal();

      private CauldronContents() {
      }

      public String getName() {
         return this.name().toLowerCase(Locale.US);
      }

      public int getMeta() {
         return this.meta;
      }

      public static BlockEnhancedCauldron.CauldronContents fromMeta(int meta) {
         if (meta > values().length) {
            meta = 0;
         }

         return values()[meta];
      }
   }
}
