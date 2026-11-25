package com.teamacronymcoders.contenttweaker.modules.vanilla.blocks;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.teamacronymcoders.base.blocks.BlockBase;
import com.teamacronymcoders.base.blocks.IHasBlockColor;
import com.teamacronymcoders.base.client.models.generator.generatedmodel.GeneratedModel;
import com.teamacronymcoders.base.client.models.generator.generatedmodel.IGeneratedModel;
import com.teamacronymcoders.base.client.models.generator.generatedmodel.ModelType;
import com.teamacronymcoders.base.items.IHasItemColor;
import com.teamacronymcoders.base.util.files.templates.TemplateFile;
import com.teamacronymcoders.base.util.files.templates.TemplateManager;
import com.teamacronymcoders.contenttweaker.api.ctobjects.blockpos.IBlockPos;
import com.teamacronymcoders.contenttweaker.api.ctobjects.blockpos.MCBlockPos;
import com.teamacronymcoders.contenttweaker.api.ctobjects.blockstate.MCBlockState;
import com.teamacronymcoders.contenttweaker.api.ctobjects.color.CTColor;
import com.teamacronymcoders.contenttweaker.api.ctobjects.enums.FaceShape;
import com.teamacronymcoders.contenttweaker.api.ctobjects.enums.Facing;
import com.teamacronymcoders.contenttweaker.api.ctobjects.itemlist.CTItemList;
import com.teamacronymcoders.contenttweaker.api.ctobjects.resourcelocation.CTResourceLocation;
import com.teamacronymcoders.contenttweaker.api.ctobjects.world.MCWorld;
import com.teamacronymcoders.contenttweaker.api.utils.CTUtils;
import com.teamacronymcoders.contenttweaker.modules.vanilla.functions.IBlockAction;
import com.teamacronymcoders.contenttweaker.modules.vanilla.functions.IBlockColorSupplier;
import com.teamacronymcoders.contenttweaker.modules.vanilla.functions.IBlockFaceSupplier;
import com.teamacronymcoders.contenttweaker.modules.vanilla.tileentity.TileEntityContent;
import crafttweaker.mc1120.item.MCItemStack;
import crafttweaker.mc1120.world.MCBlockAccess;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFalling;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.EnumPushReaction;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityWitherSkull;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockContent extends BlockBase implements IHasBlockColor, IHasItemColor {
   private BlockRepresentation blockRepresentation;
   private EnumBlockRenderType enumBlockRenderType;
   private CreativeTabs creativeTab;
   private BlockRenderLayer blockRenderLayer;
   private AxisAlignedBB blockShape;

   public BlockContent(BlockRepresentation blockRepresentation) {
      super(blockRepresentation.getBlockMaterial().getInternal(), blockRepresentation.getUnlocalizedName());
      this.blockRepresentation = blockRepresentation;
      this.setFields();
   }

   public void setFields() {
      this.setUnlocalizedName(this.blockRepresentation.getUnlocalizedName());
      if (this.blockRepresentation.getCreativeTab() != null) {
         Object creativeTab = this.blockRepresentation.getCreativeTab().getInternal();
         if (creativeTab instanceof CreativeTabs) {
            this.setCreativeTab((CreativeTabs)this.blockRepresentation.getCreativeTab().getInternal());
         }
      }

      this.setLightOpacity(this.blockRepresentation.getLightOpacity());
      this.setLightLevel(this.blockRepresentation.getLightValue());
      this.setHardness(this.blockRepresentation.getBlockHardness());
      this.setResistance(this.blockRepresentation.getBlockResistance());
      this.setHarvestLevel(this.blockRepresentation.getToolClass(), this.blockRepresentation.getToolLevel());
      this.setSoundType((SoundType)this.blockRepresentation.getBlockSoundType().getInternal());
      this.enumBlockRenderType = (EnumBlockRenderType)CTUtils.getEnum(this.blockRepresentation.getEnumBlockRenderType(), EnumBlockRenderType.class);
      this.blockRenderLayer = (BlockRenderLayer)CTUtils.getEnum(this.blockRepresentation.getBlockLayer(), BlockRenderLayer.class);
      this.blockShape = this.blockRepresentation.getAxisAlignedBB().getInternal();
      if (this.blockRepresentation.getOnRandomTick() != null) {
         this.setTickRandomly(true);
      }

      this.fullBlock = this.blockRepresentation.isFullBlock();
      this.translucent = this.blockRepresentation.isTranslucent();
   }

   @ParametersAreNonnullByDefault
   public boolean isSideSolid(IBlockState baseState, IBlockAccess world, BlockPos pos, EnumFacing side) {
      return this.blockRepresentation.isFullBlock();
   }

   @Nonnull
   public CreativeTabs getCreativeTabToDisplayOn() {
      return this.creativeTab;
   }

   @Nonnull
   public Block setCreativeTab(@Nonnull CreativeTabs creativeTab) {
      this.creativeTab = creativeTab;
      return this;
   }

   public float getEnchantPowerBonus(@Nonnull World world, @Nonnull BlockPos pos) {
      return this.blockRepresentation.getEnchantPowerBonus();
   }

   @Nonnull
   public EnumBlockRenderType getRenderType(@Nonnull IBlockState state) {
      return this.enumBlockRenderType;
   }

   @SideOnly(Side.CLIENT)
   @Nonnull
   public BlockRenderLayer getBlockLayer() {
      return this.blockRenderLayer;
   }

   public void onBlockAdded(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull IBlockState state) {
      super.onBlockAdded(world, pos, state);
      this.activateBlockAction(this.blockRepresentation.getOnBlockPlace(), world, pos, state);
      if (this.blockRepresentation.hasGravity()) {
         world.scheduleUpdate(pos, this, this.tickRate(world));
      }

   }

   public void breakBlock(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull IBlockState state) {
      this.activateBlockAction(this.blockRepresentation.getOnBlockBreak(), world, pos, state);
      super.breakBlock(world, pos, state);
   }

   public boolean canEntityDestroy(IBlockState state, IBlockAccess world, BlockPos pos, Entity entity) {
      return !this.blockRepresentation.isWitherProof() || !(entity instanceof EntityWither) && !(entity instanceof EntityWitherSkull) ? super.canEntityDestroy(state, world, pos, entity) : false;
   }

   public float getExplosionResistance(World world, BlockPos pos, @Nullable Entity entity, Explosion explosion) {
      return !this.blockRepresentation.isWitherProof() || !(entity instanceof EntityWither) && !(entity instanceof EntityWitherSkull) ? super.getExplosionResistance(world, pos, entity, explosion) : 2000.0F;
   }

   public float getSlipperiness(IBlockState state, IBlockAccess world, BlockPos pos, @Nullable Entity entity) {
      return this.blockRepresentation.getSlipperiness();
   }

   @Nonnull
   public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
      return this.blockShape;
   }

   @ParametersAreNonnullByDefault
   public void randomTick(World world, BlockPos pos, IBlockState state, Random random) {
      this.activateBlockAction(this.blockRepresentation.getOnRandomTick(), world, pos, state);
   }

   public void updateTick(World world, BlockPos pos, IBlockState state, Random rand) {
      this.activateBlockAction(this.blockRepresentation.getOnUpdateTick(), world, pos, state);
      if (this.blockRepresentation.hasGravity() && !world.isRemote && (world.isAirBlock(pos.down()) || BlockFalling.canFallThrough(world.getBlockState(pos.down()))) && pos.getY() >= 0 && world.isAreaLoaded(pos.add(-32, -32, -32), pos.add(32, 32, 32))) {
         world.spawnEntity(new EntityFallingBlock(world, (double)pos.getX() + 0.5D, (double)pos.getY(), (double)pos.getZ() + 0.5D, world.getBlockState(pos)));
      }

   }

   public void neighborChanged(IBlockState state, World world, BlockPos pos, Block blockIn, BlockPos fromPos) {
      if (this.blockRepresentation.hasGravity()) {
         world.scheduleUpdate(pos, this, this.tickRate(world));
      } else {
         super.neighborChanged(state, world, pos, blockIn, fromPos);
      }

   }

   public int tickRate(World world) {
      return this.blockRepresentation.hasGravity() ? 2 : super.tickRate(world);
   }

   public void activateBlockAction(IBlockAction blockAction, World world, BlockPos blockPos, IBlockState blockState) {
      if (blockAction != null) {
         blockAction.onBlockAction(new MCWorld(world), new MCBlockPos(blockPos), new MCBlockState(blockState));
      }

   }

   @Nonnull
   public EnumPushReaction getMobilityFlag(@Nonnull IBlockState state) {
      return this.blockRepresentation.getMobilityFlag().getInternal();
   }

   public boolean isPassable(IBlockAccess worldIn, BlockPos pos) {
      return this.blockRepresentation.isPassable();
   }

   public boolean isReplaceable(IBlockAccess world, @Nonnull BlockPos pos) {
      return this.blockRepresentation.isReplaceable();
   }

   @ParametersAreNonnullByDefault
   public void addCollisionBoxToList(IBlockState state, World world, BlockPos pos, AxisAlignedBB entityBox, List<AxisAlignedBB> collidingBoxes, @Nullable Entity entity, boolean isActualState) {
      if (this.blockRepresentation.isPassable()) {
         addCollisionBoxToList(pos, entityBox, collidingBoxes, NULL_AABB);
      } else {
         addCollisionBoxToList(pos, entityBox, collidingBoxes, state.getCollisionBoundingBox(world, pos));
      }

   }

   public boolean canEntitySpawn(IBlockState state, Entity entity) {
      return this.blockRepresentation.canEntitySpawn();
   }

   public boolean isOpaqueCube(IBlockState state) {
      return this.fullBlock;
   }

   public boolean isFullCube(IBlockState state) {
      return this.fullBlock;
   }

   public boolean isNormalCube(IBlockState state, IBlockAccess world, BlockPos pos) {
      return this.fullBlock;
   }

   public void getDrops(@Nonnull NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, @Nonnull IBlockState state, int fortune) {
      super.getDrops(drops, world, pos, state, fortune);
      if (this.blockRepresentation.getDropHandler() != null) {
         crafttweaker.api.world.IBlockAccess blockAccess = (crafttweaker.api.world.IBlockAccess)(world instanceof World ? new MCWorld((World)world) : new MCBlockAccess(world));
         this.blockRepresentation.getDropHandler().handleDrops(new CTItemList(drops), blockAccess, new MCBlockPos(pos), new MCBlockState(state), fortune);
      }

   }

   public boolean isBeaconBase(IBlockAccess worldObj, BlockPos pos, BlockPos beacon) {
      return this.blockRepresentation.beaconBase;
   }

   public boolean hasTileEntity(IBlockState state) {
      return this.blockRepresentation.tileEntityRepresentation != null;
   }

   @Nullable
   @ParametersAreNonnullByDefault
   public TileEntity createTileEntity(World world, IBlockState state) {
      return this.blockRepresentation.tileEntityRepresentation != null ? new TileEntityContent(this.blockRepresentation.tileEntityRepresentation) : null;
   }

   public List<IGeneratedModel> getGeneratedModels() {
      List<IGeneratedModel> models = Lists.newArrayList();
      this.getResourceLocations(Lists.newArrayList()).forEach((resourceLocation) -> {
         TemplateFile templateFile = TemplateManager.getTemplateFile("colored_block");
         Map<String, String> replacements = Maps.newHashMap();
         replacements.put("texture", Optional.ofNullable(this.blockRepresentation.getTextureLocation()).map(CTResourceLocation::getInternal).map(ResourceLocation::toString).orElseGet(() -> {
            return (new ResourceLocation(resourceLocation.getResourceDomain(), "blocks/" + resourceLocation.getResourcePath())).toString();
         }));
         templateFile.replaceContents(replacements);
         models.add(new GeneratedModel(resourceLocation.getResourcePath(), ModelType.BLOCKSTATE, templateFile.getFileContents()));
      });
      return models;
   }

   public int colorMultiplier(IBlockState state, @Nullable IBlockAccess world, @Nullable BlockPos pos, int tintIndex) {
      IBlockColorSupplier blockColorSupplier = this.blockRepresentation.getBlockColorSupplier();
      if (blockColorSupplier == null) {
         return -1;
      } else {
         crafttweaker.api.world.IBlockAccess blockAccess = null;
         if (world instanceof World) {
            blockAccess = new MCWorld((World)world);
         } else if (world != null) {
            blockAccess = new MCBlockAccess(world);
         }

         IBlockPos blockPos = pos == null ? null : new MCBlockPos(pos);
         CTColor color = blockColorSupplier.getColor(new MCBlockState(state), blockAccess, blockPos, tintIndex);
         return color == null ? -1 : color.getIntColor();
      }
   }

   public int getColorFromItemstack(@Nonnull ItemStack stack, int tintIndex) {
      return ((Integer)Optional.ofNullable(this.blockRepresentation.getItemColorSupplier()).map((supplier) -> {
         return supplier.getColor(new MCItemStack(stack), tintIndex);
      }).map(CTColor::getIntColor).orElse(Integer.valueOf(-1))).intValue();
   }

   public boolean canSilkHarvest(World world, BlockPos pos, @Nonnull IBlockState state, EntityPlayer player) {
      return this.blockRepresentation.canSilkHarvest;
   }

   /** @deprecated */
   @Nonnull
   @Deprecated
   public BlockFaceShape getBlockFaceShape(IBlockAccess world, IBlockState state, BlockPos pos, EnumFacing face) {
      IBlockFaceSupplier blockFaceSupplier = this.blockRepresentation.getBlockFaceSupplier();
      if (blockFaceSupplier == null) {
         return super.getBlockFaceShape(world, state, pos, face);
      } else {
         crafttweaker.api.world.IBlockAccess blockAccess = null;
         if (world instanceof World) {
            blockAccess = new MCWorld((World)world);
         } else if (world != null) {
            blockAccess = new MCBlockAccess(world);
         }

         IBlockPos blockPos = pos == null ? null : new MCBlockPos(pos);
         Facing facing = face == null ? null : Facing.of(face);
         FaceShape faceShape = blockFaceSupplier.getBlockFaceShape(blockAccess, new MCBlockState(state), blockPos, facing);
         return faceShape != null ? faceShape.getInternal() : super.getBlockFaceShape(world, state, pos, face);
      }
   }
}
