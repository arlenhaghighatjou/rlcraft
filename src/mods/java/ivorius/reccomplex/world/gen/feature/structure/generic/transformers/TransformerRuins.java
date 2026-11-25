package ivorius.reccomplex.world.gen.feature.structure.generic.transformers;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;
import com.google.common.collect.ImmutableList.Builder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import gnu.trove.map.TObjectDoubleMap;
import gnu.trove.map.hash.TObjectDoubleHashMap;
import ivorius.ivtoolkit.blocks.BlockArea;
import ivorius.ivtoolkit.blocks.BlockAreas;
import ivorius.ivtoolkit.blocks.BlockPositions;
import ivorius.ivtoolkit.blocks.Directions;
import ivorius.ivtoolkit.blocks.IvBlockCollection;
import ivorius.ivtoolkit.blocks.IvMutableBlockPos;
import ivorius.ivtoolkit.random.BlurredValueField;
import ivorius.ivtoolkit.tools.IvTranslations;
import ivorius.ivtoolkit.tools.IvWorldData;
import ivorius.ivtoolkit.tools.MCRegistry;
import ivorius.ivtoolkit.tools.NBTCompoundObjects;
import ivorius.ivtoolkit.tools.NBTTagLists;
import ivorius.ivtoolkit.transform.PosTransformer;
import ivorius.ivtoolkit.world.chunk.gen.StructureBoundingBoxes;
import ivorius.reccomplex.RecurrentComplex;
import ivorius.reccomplex.block.BlockGenericSolid;
import ivorius.reccomplex.block.RCBlocks;
import ivorius.reccomplex.gui.editstructure.transformers.TableDataSourceBTRuins;
import ivorius.reccomplex.gui.table.TableDelegate;
import ivorius.reccomplex.gui.table.TableNavigator;
import ivorius.reccomplex.gui.table.datasource.TableDataSource;
import ivorius.reccomplex.json.JsonUtils;
import ivorius.reccomplex.nbt.NBTStorable;
import ivorius.reccomplex.world.gen.feature.structure.Structures;
import ivorius.reccomplex.world.gen.feature.structure.context.StructureLiveContext;
import ivorius.reccomplex.world.gen.feature.structure.context.StructureLoadContext;
import ivorius.reccomplex.world.gen.feature.structure.context.StructurePrepareContext;
import ivorius.reccomplex.world.gen.feature.structure.context.StructureSpawnContext;
import ivorius.reccomplex.world.gen.feature.structure.generic.GenericStructure;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import java.util.Set;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntBinaryOperator;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockPlanks;
import net.minecraft.block.BlockSandStone;
import net.minecraft.block.BlockStairs;
import net.minecraft.block.BlockStoneBrick;
import net.minecraft.block.BlockVine;
import net.minecraft.block.BlockWall;
import net.minecraft.block.BlockStairs.EnumHalf;
import net.minecraft.block.BlockStoneBrick.EnumType;
import net.minecraft.block.material.EnumPushReaction;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.BlockPos.MutableBlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class TransformerRuins extends Transformer<TransformerRuins.InstanceData> {
   private static final List<BlockPos> neighbors;
   public static final TObjectDoubleMap<Material> stability = new TObjectDoubleHashMap(10, 0.5F, 1.0D);
   public EnumFacing decayDirection;
   public float minDecay;
   public float maxDecay;
   public float decayChaos;
   public float decayValueDensity;
   public boolean gravity;
   public float blockErosion;
   public float vineGrowth;
   public float cobwebGrowth;

   public TransformerRuins() {
      this((String)null, EnumFacing.DOWN, 0.0F, 0.8F, 0.6F, 0.005F, true, 0.3F, 0.08F, 0.03F);
   }

   public TransformerRuins(@Nullable String id, EnumFacing decayDirection, float minDecay, float maxDecay, float decayChaos, float decayValueDensity, boolean gravity, float blockErosion, float vineGrowth, float cobwebGrowth) {
      super(id != null ? id : randomID(TransformerRuins.class));
      this.decayDirection = decayDirection;
      this.minDecay = minDecay;
      this.maxDecay = maxDecay;
      this.decayChaos = decayChaos;
      this.decayValueDensity = decayValueDensity;
      this.gravity = gravity;
      this.blockErosion = blockErosion;
      this.vineGrowth = vineGrowth;
      this.cobwebGrowth = cobwebGrowth;
   }

   private static int getPass(IBlockState state) {
      return !state.isNormalCube() && state.getMaterial() != Material.AIR ? 1 : 0;
   }

   public static void shuffleArray(Object[] ar, EaglercraftRandom rand) {
      for(int i = ar.length - 1; i > 0; --i) {
         int index = rand.nextInt(i + 1);
         Object a = ar[index];
         ar[index] = ar[i];
         ar[i] = a;
      }

   }

   public static int product(int[] surfaceSize) {
      return Arrays.stream(surfaceSize).reduce(1, (left, right) -> {
         return left * right;
      });
   }

   @Nonnull
   protected static IBlockState randomPlacement(EaglercraftRandom random, IBlockState stairs) {
      return stairs.withProperty(BlockStairs.FACING, EnumFacing.getHorizontal(random.nextInt(4))).withProperty(BlockStairs.HALF, random.nextBoolean() ? EnumHalf.BOTTOM : EnumHalf.TOP);
   }

   public boolean skipGeneration(TransformerRuins.InstanceData instanceData, StructureLiveContext context, BlockPos pos, IBlockState state, IvWorldData worldData, BlockPos sourcePos) {
      if (instanceData.fallingBlocks.contains(sourcePos)) {
         return true;
      } else {
         double decay = this.getDecay(instanceData, sourcePos, state);
         if (decay < 1.0E-6D) {
            return false;
         } else if (decay > 1.0D) {
            return true;
         } else {
            return this.getStability(worldData, sourcePos) < decay;
         }
      }
   }

   public double getDecay(TransformerRuins.InstanceData instanceData, BlockPos sourcePos, IBlockState state) {
      return Math.pow(instanceData.getDecay(sourcePos), stability.get(state.getMaterial()));
   }

   public double getStability(IvWorldData worldData, BlockPos sourcePos) {
      double stability = (double)this.decayDirection.getFrontOffsetX() * ((double)(sourcePos.getX() + 1) / (double)(worldData.blockCollection.getWidth() + 1)) + (double)this.decayDirection.getFrontOffsetY() * ((double)(sourcePos.getY() + 1) / (double)(worldData.blockCollection.getHeight() + 1)) + (double)this.decayDirection.getFrontOffsetZ() * ((double)(sourcePos.getZ() + 1) / (double)(worldData.blockCollection.getLength() + 1));
      if (stability < 0.0D) {
         ++stability;
      }

      return stability;
   }

   public void transform(TransformerRuins.InstanceData instanceData, Transformer.Phase phase, StructureSpawnContext context, IvWorldData worldData, RunTransformer transformer) {
      if (phase == Transformer.Phase.AFTER) {
         WorldServer world = context.environment.world;
         IvBlockCollection blockCollection = worldData.blockCollection;
         int[] areaSize = new int[]{blockCollection.width, blockCollection.height, blockCollection.length};
         BlockPos lowerCoord = StructureBoundingBoxes.min(context.boundingBox);
         Map<BlockPos, NBTTagCompound> tileEntityCompounds = new HashMap();

         for(NBTTagCompound tileEntityCompound : worldData.tileEntities) {
            BlockPos key = new BlockPos(tileEntityCompound.getInteger("x"), tileEntityCompound.getInteger("y"), tileEntityCompound.getInteger("z"));
            tileEntityCompounds.put(key, tileEntityCompound);
         }

         MutableBlockPos dest = new MutableBlockPos(lowerCoord);

         for(BlockPos sourcePos : instanceData.fallingBlocks) {
            IBlockState source = blockCollection.getBlockState(sourcePos);
            if (this.canLand(source)) {
               IvMutableBlockPos.add(context.transform.applyOn(sourcePos, dest, areaSize), lowerCoord);

               while(dest.getY() > 0 && world.getBlockState(dest).getBlock().isReplaceable(world, dest)) {
                  IvMutableBlockPos.offset(dest, dest, EnumFacing.DOWN);
               }

               IvMutableBlockPos.offset(dest, dest, EnumFacing.UP);
               IBlockState state = PosTransformer.transformBlockState(source, context.transform);
               GenericStructure.setBlock(context, areaSize, dest, state, () -> {
                  return (NBTTagCompound)tileEntityCompounds.get(sourcePos);
               });
            }
         }

         StructureBoundingBox dropAreaBB = context.boundingBox;
         RecurrentComplex.forgeEventHandler.disabledTileDropAreas.add(dropAreaBB);
         if (this.blockErosion > 0.0F || this.vineGrowth > 0.0F) {
            StructureBoundingBox relevantBB = context.generationBB != null ? Structures.intersection(context.boundingBox, context.generationBB) : context.boundingBox;

            for(BlockPos sourceCoord : BlockAreas.mutablePositions(blockCollection.area())) {
               BlockPos worldCoord = context.transform.apply(sourceCoord, areaSize).add(StructureBoundingBoxes.min(context.boundingBox));
               if (context.includes(worldCoord)) {
                  IBlockState state = world.getBlockState(worldCoord);
                  if (!transformer.transformer.skipGeneration(transformer.instanceData, context, worldCoord, state, worldData, sourceCoord)) {
                     this.decayBlock(world, context.random, state, worldCoord, relevantBB);
                  }
               }
            }
         }

         RecurrentComplex.forgeEventHandler.disabledTileDropAreas.remove(dropAreaBB);
      }

   }

   public void decayBlock(World world, EaglercraftRandom random, IBlockState state, BlockPos pos, StructureBoundingBox boundingBox) {
      IBlockState newState = state;
      if (random.nextFloat() < this.blockErosion) {
         if (state.getBlock() == Blocks.STONEBRICK && state.getProperties().get(BlockStoneBrick.VARIANT) != EnumType.MOSSY) {
            newState = Blocks.STONEBRICK.getDefaultState().withProperty(BlockStoneBrick.VARIANT, EnumType.CRACKED);
         } else if (state.getBlock() == Blocks.SANDSTONE) {
            newState = Blocks.SANDSTONE.getDefaultState().withProperty(BlockSandStone.TYPE, net.minecraft.block.BlockSandStone.EnumType.DEFAULT);
         }
      }

      newState = this.maybeErodeShape(random, newState, Blocks.STONEBRICK, BlockStoneBrick.VARIANT, EnumType.DEFAULT, Blocks.STONE_BRICK_STAIRS);
      newState = this.maybeErodeShape(random, newState, Blocks.PLANKS, BlockPlanks.VARIANT, net.minecraft.block.BlockPlanks.EnumType.OAK, Blocks.OAK_STAIRS);
      newState = this.maybeErodeShape(random, newState, Blocks.PLANKS, BlockPlanks.VARIANT, net.minecraft.block.BlockPlanks.EnumType.SPRUCE, Blocks.SPRUCE_STAIRS);
      newState = this.maybeErodeShape(random, newState, Blocks.PLANKS, BlockPlanks.VARIANT, net.minecraft.block.BlockPlanks.EnumType.BIRCH, Blocks.BIRCH_STAIRS);
      newState = this.maybeErodeShape(random, newState, Blocks.PLANKS, BlockPlanks.VARIANT, net.minecraft.block.BlockPlanks.EnumType.JUNGLE, Blocks.JUNGLE_STAIRS);
      newState = this.maybeErodeShape(random, newState, Blocks.PLANKS, BlockPlanks.VARIANT, net.minecraft.block.BlockPlanks.EnumType.ACACIA, Blocks.ACACIA_STAIRS);
      newState = this.maybeErodeShape(random, newState, Blocks.PLANKS, BlockPlanks.VARIANT, net.minecraft.block.BlockPlanks.EnumType.DARK_OAK, Blocks.DARK_OAK_STAIRS);
      newState = this.maybeErodeShape(random, newState, Blocks.SANDSTONE, (PropertyEnum)null, (Object)null, Blocks.SANDSTONE_STAIRS);
      newState = this.maybeErodeShape(random, newState, Blocks.COBBLESTONE, (PropertyEnum)null, (Object)null, Blocks.STONE_STAIRS);
      newState = this.maybeErodeShape(random, newState, Blocks.QUARTZ_BLOCK, (PropertyEnum)null, (Object)null, Blocks.QUARTZ_STAIRS);
      newState = this.maybeErodeShape(random, newState, Blocks.RED_SANDSTONE, (PropertyEnum)null, (Object)null, Blocks.RED_SANDSTONE_STAIRS);
      newState = this.maybeErodeShape(random, newState, Blocks.NETHER_BRICK, (PropertyEnum)null, (Object)null, Blocks.NETHER_BRICK_STAIRS);
      if (random.nextFloat() < this.vineGrowth) {
         if (newState.getBlock() == Blocks.STONEBRICK) {
            newState = Blocks.STONEBRICK.getDefaultState().withProperty(BlockStoneBrick.VARIANT, EnumType.MOSSY);
         } else if (newState.getBlock() == Blocks.COBBLESTONE) {
            newState = Blocks.MOSSY_COBBLESTONE.getDefaultState();
         } else if (newState.getBlock() == Blocks.COBBLESTONE_WALL) {
            newState = Blocks.COBBLESTONE_WALL.getDefaultState().withProperty(BlockWall.VARIANT, net.minecraft.block.BlockWall.EnumType.MOSSY);
         }
      }

      if (newState.getBlock() == Blocks.AIR) {
         newState = null;

         label66:
         for(EnumFacing direction : EnumFacing.HORIZONTALS) {
            if (random.nextFloat() < this.vineGrowth && boundingBox.isVecInside(pos.offset(direction.getOpposite())) && Blocks.VINE.canPlaceBlockOnSide(world, pos, direction)) {
               IBlockState downState = world.getBlockState(pos.offset(EnumFacing.DOWN));
               downState = downState.getBlock() == Blocks.VINE ? downState : Blocks.VINE.getDefaultState();
               downState = downState.withProperty(BlockVine.getPropertyFor(direction.getOpposite()), Boolean.valueOf(true));
               int length = 1 + random.nextInt(MathHelper.floor(this.vineGrowth * 10.0F + 3.0F));
               int y = 0;

               while(true) {
                  if (y >= length) {
                     break label66;
                  }

                  BlockPos downPos = pos.offset(EnumFacing.DOWN, y);
                  if (world.getBlockState(downPos).getMaterial() != Material.AIR) {
                     break label66;
                  }

                  world.setBlockState(downPos, downState, 3);
                  ++y;
               }
            }

            if (random.nextFloat() < this.cobwebGrowth && this.hasAirNeighbors(world, pos, 3)) {
               newState = null;
               world.setBlockState(pos, Blocks.WEB.getDefaultState(), 3);
            }
         }
      }

      if (newState != null && state != newState) {
         world.setBlockState(pos, newState, 3);
      }

   }

   @Nonnull
   protected IBlockState maybeErodeShape(EaglercraftRandom random, IBlockState newState, Block block, PropertyEnum<?> variant, Object value, Block oakStairs) {
      if (newState.getBlock() == block && (variant == null || newState.getProperties().get(variant) == value)) {
         newState = this.erodeShape(random, newState, oakStairs);
      }

      return newState;
   }

   @Nonnull
   protected IBlockState erodeShape(EaglercraftRandom random, IBlockState newState, Block stairs) {
      if (random.nextFloat() < this.blockErosion * 0.125F) {
         newState = randomPlacement(random, stairs.getDefaultState());
      }

      return newState;
   }

   public boolean hasAirNeighbors(World world, BlockPos pos, int sides) {
      int num = 0;
      int neg = 0;

      for(EnumFacing facing : EnumFacing.VALUES) {
         if (world.getBlockState(pos.offset(facing)).isNormalCube()) {
            ++num;
         } else {
            ++neg;
         }

         if (num >= sides) {
            return true;
         }

         if (neg > 6 - sides) {
            return false;
         }
      }

      throw new InternalError();
   }

   @SideOnly(Side.CLIENT)
   public String getDisplayString() {
      return IvTranslations.get("reccomplex.transformer.ruins");
   }

   @SideOnly(Side.CLIENT)
   public TableDataSource tableDataSource(TableNavigator navigator, TableDelegate delegate) {
      return new TableDataSourceBTRuins(this, navigator, delegate);
   }

   public TransformerRuins.InstanceData prepareInstanceData(StructurePrepareContext context, IvWorldData worldData) {
      TransformerRuins.InstanceData instanceData = new TransformerRuins.InstanceData();
      if (this.minDecay > 0.0F || this.maxDecay > 0.0F) {
         BlockArea sourceArea = BlockArea.areaFromSize(BlockPos.ORIGIN, StructureBoundingBoxes.size(context.boundingBox));
         double decayChaos = (context.random.nextDouble() * 0.5D + 0.5D) * (double)this.decayChaos;
         instanceData.baseDecay = context.random.nextDouble() * (double)(this.maxDecay - this.minDecay) + (double)this.minDecay;
         int[] surfaceSize = BlockAreas.side(sourceArea, this.decayDirection).areaSize();
         instanceData.surfaceField = new BlurredValueField(surfaceSize);
         int surfaceValues = MathHelper.floor((double)((float)product(surfaceSize) * this.decayValueDensity) + 0.5D);

         for(int i = 0; i < surfaceValues; ++i) {
            instanceData.surfaceField.addValue((context.random.nextDouble() - context.random.nextDouble()) * decayChaos * 1.5D, context.random);
         }

         int[] volumeSize = sourceArea.areaSize();
         instanceData.volumeField = new BlurredValueField(volumeSize);
         int volumeValues = MathHelper.floor((double)((float)product(volumeSize) * this.decayValueDensity) * 0.25D + 0.5D);

         for(int i = 0; i < volumeValues; ++i) {
            instanceData.volumeField.addValue((context.random.nextDouble() - context.random.nextDouble()) * decayChaos * 0.5D, context.random);
         }

         instanceData.clearDecayCache();
      }

      return instanceData;
   }

   public void configureInstanceData(TransformerRuins.InstanceData instanceData, StructurePrepareContext context, IvWorldData worldData, RunTransformer transformer) {
      if (this.gravity) {
         IvBlockCollection blockCollection = worldData.blockCollection;
         int[] areaSize = new int[]{blockCollection.width, blockCollection.height, blockCollection.length};
         BlockPos lowerCoord = StructureBoundingBoxes.min(context.boundingBox);
         MutableBlockPos dest = new MutableBlockPos(lowerCoord);

         for(BlockPos sourcePos : BlockAreas.mutablePositions(blockCollection.area())) {
            IBlockState state = blockCollection.getBlockState(sourcePos);
            IvMutableBlockPos.add(context.transform.applyOn(sourcePos, dest, areaSize), lowerCoord);
            if (!transformer.transformer.skipGeneration(transformer.instanceData, context, dest, state, worldData, sourcePos) && this.canFall(context, worldData, transformer, dest, sourcePos, state)) {
               double stability = this.getStability(worldData, sourcePos);
               double decay = this.getDecay(instanceData, sourcePos, state);
               double stabilitySQ = stability * stability;
               if (stability >= decay && stabilitySQ * stabilitySQ < decay) {
                  instanceData.fallingBlocks.add(sourcePos.toImmutable());
               }
            }
         }

         Set<BlockPos> complete = new HashSet(product(areaSize));
         HashSet<BlockPos> connected = new HashSet();
         boolean[] hasFloor = new boolean[1];

         for(BlockPos startPos : BlockAreas.positions(blockCollection.area())) {
            if (!complete.contains(startPos)) {
               hasFloor[0] = false;
               connected.clear();
               TransformerAbstractCloud.visitRecursively(Sets.newHashSet(new BlockPos[]{startPos}), (changed, sourcePos) -> {
                  if (complete.add(sourcePos) && !instanceData.fallingBlocks.contains(sourcePos)) {
                     IvMutableBlockPos.add(context.transform.applyOn(sourcePos, dest, areaSize), lowerCoord);
                     IBlockState state = blockCollection.getBlockState(sourcePos);
                     if (this.canFall(context, worldData, transformer, dest, sourcePos, state)) {
                        if (state.getBlock() == RCBlocks.genericSolid && ((Integer)state.getValue(BlockGenericSolid.TYPE)).intValue() == 0) {
                           hasFloor[0] = true;
                        }

                        connected.add(sourcePos);
                        Stream var10000 = neighbors.stream();
                        sourcePos.getClass();
                        var10000.map(sourcePos::add).forEach(changed::add);
                     }

                     return true;
                  } else {
                     return true;
                  }
               });
               if (connected.size() > 0 && connected.size() < 200 && !hasFloor[0]) {
                  instanceData.fallingBlocks.addAll(connected);
               }
            }
         }
      }

   }

   public boolean canLand(IBlockState state) {
      return state.getMaterial().getMobilityFlag() == EnumPushReaction.NORMAL && state.isNormalCube();
   }

   public boolean canFall(StructurePrepareContext context, IvWorldData worldData, RunTransformer transformer, MutableBlockPos dest, BlockPos worldPos, IBlockState state) {
      return !transformer.transformer.skipGeneration(transformer.instanceData, context, dest, state, worldData, worldPos) && state.getMaterial() != Material.AIR;
   }

   public TransformerRuins.InstanceData loadInstanceData(StructureLoadContext context, NBTBase nbt) {
      return new TransformerRuins.InstanceData(nbt instanceof NBTTagCompound ? (NBTTagCompound)nbt : new NBTTagCompound());
   }

   static {
      Builder<BlockPos> builder = ImmutableList.builder();

      for(int x = -1; x <= 1; ++x) {
         for(int y = -1; y <= 1; ++y) {
            for(int z = -1; z <= 1; ++z) {
               if (x != 0 || y != 0 || z != 0) {
                  builder.add(new BlockPos(x, y, z));
               }
            }
         }
      }

      neighbors = builder.build();
      stability.put(Material.GLASS, 0.1D);
      stability.put(Material.LAVA, 2.0D);
      stability.put(Material.CIRCUITS, 0.2D);
      stability.put(Material.IRON, 2.0D);
      stability.put(Material.WOOD, 0.3D);
      stability.put(Material.CLOTH, 0.3D);
   }

   public static class InstanceData implements NBTStorable {
      public Double baseDecay;
      public BlurredValueField surfaceField;
      public BlurredValueField volumeField;
      public final Set<BlockPos> fallingBlocks = new HashSet();
      public Double[] decayCache;
      public int[] decayCacheSize;

      public InstanceData() {
      }

      public InstanceData(NBTTagCompound compound) {
         this.baseDecay = compound.hasKey("baseDecay") ? compound.getDouble("baseDecay") : null;
         this.surfaceField = compound.hasKey("field", 10) ? (BlurredValueField)NBTCompoundObjects.read(compound.getCompoundTag("field"), BlurredValueField::new) : null;
         this.volumeField = compound.hasKey("volumeField", 10) ? (BlurredValueField)NBTCompoundObjects.read(compound.getCompoundTag("volumeField"), BlurredValueField::new) : null;
         this.fallingBlocks.addAll((Collection)NBTTagLists.intArraysFrom(compound, "fallingBlocks").stream().map(BlockPositions::fromIntArray).collect(Collectors.toList()));
         this.clearDecayCache();
      }

      public NBTBase writeToNBT() {
         NBTTagCompound compound = new NBTTagCompound();
         if (this.baseDecay != null) {
            compound.setDouble("baseDecay", this.baseDecay.doubleValue());
         }

         if (this.surfaceField != null) {
            compound.setTag("field", NBTCompoundObjects.write(this.surfaceField));
         }

         if (this.volumeField != null) {
            compound.setTag("volumeField", NBTCompoundObjects.write(this.volumeField));
         }

         NBTTagLists.writeIntArraysTo(compound, "fallingBlocks", (List)this.fallingBlocks.stream().map(BlockPositions::toIntArray).collect(Collectors.toList()));
         return compound;
      }

      private Integer getIndex(BlockPos pos) {
         if (this.decayCacheSize == null) {
            return null;
         } else {
            return pos.getX() >= 0 && pos.getY() >= 0 && pos.getZ() >= 0 && pos.getX() < this.decayCacheSize[0] && pos.getY() < this.decayCacheSize[1] && pos.getZ() < this.decayCacheSize[2] ? (pos.getX() * this.decayCacheSize[1] + pos.getY()) * this.decayCacheSize[2] + pos.getZ() : null;
         }
      }

      public double getDecay(BlockPos pos) {
         if (!this.hasDecay()) {
            return 0.0D;
         } else {
            Integer index = this.getIndex(pos);
            if (index != null) {
               Double decay = this.decayCache[index.intValue()];
               return (decay != null ? decay : (this.decayCache[index.intValue()] = this.calculateDecay(pos))).doubleValue();
            } else {
               return this.calculateDecay(pos);
            }
         }
      }

      public void clearDecayCache() {
         if (this.volumeField != null) {
            this.decayCacheSize = this.volumeField.getSize();
            this.decayCache = new Double[TransformerRuins.product(this.decayCacheSize)];
         } else {
            this.decayCacheSize = null;
            this.decayCache = null;
         }

      }

      protected boolean hasDecay() {
         return this.baseDecay != null || this.surfaceField != null || this.volumeField != null;
      }

      protected double calculateDecay(BlockPos pos) {
         return (this.baseDecay != null ? this.baseDecay.doubleValue() : 0.0D) + (this.surfaceField != null ? this.surfaceField.getValue(new int[]{Math.min(pos.getX(), this.surfaceField.getSize()[0]), Math.min(pos.getY(), this.surfaceField.getSize()[1]), Math.min(pos.getZ(), this.surfaceField.getSize()[2])}) : 0.0D) + (this.volumeField != null ? this.volumeField.getValue(new int[]{pos.getX(), pos.getY(), pos.getZ()}) : 0.0D);
      }
   }

   public static class Serializer implements JsonDeserializer<TransformerRuins>, JsonSerializer<TransformerRuins> {
      private MCRegistry registry;

      public Serializer(MCRegistry registry) {
         this.registry = registry;
      }

      public TransformerRuins deserialize(JsonElement jsonElement, Type par2Type, JsonDeserializationContext context) {
         JsonObject jsonObject = JsonUtils.asJsonObject(jsonElement, "transformerRuins");
         String id = Transformer.readID(jsonObject);
         EnumFacing decayDirection = Directions.deserialize(JsonUtils.getString(jsonObject, "decayDirection", "DOWN"));
         float minDecay = JsonUtils.getFloat(jsonObject, "minDecay", 0.0F);
         float maxDecay = JsonUtils.getFloat(jsonObject, "maxDecay", 0.9F);
         float decayChaos = JsonUtils.getFloat(jsonObject, "decayChaos", 0.3F);
         float decayValueDensity = JsonUtils.getFloat(jsonObject, "decayValueDensity", 0.04F);
         boolean gravity = JsonUtils.getBoolean(jsonObject, "gravity", true);
         float blockErosion = JsonUtils.getFloat(jsonObject, "blockErosion", 0.0F);
         float vineGrowth = JsonUtils.getFloat(jsonObject, "vineGrowth", 0.0F);
         float cobwebGrowth = JsonUtils.getFloat(jsonObject, "cobwebGrowth", 0.0F);
         return new TransformerRuins(id, decayDirection, minDecay, maxDecay, decayChaos, decayValueDensity, gravity, blockErosion, vineGrowth, cobwebGrowth);
      }

      public JsonElement serialize(TransformerRuins transformer, Type par2Type, JsonSerializationContext context) {
         JsonObject jsonObject = new JsonObject();
         jsonObject.addProperty("id", transformer.id());
         jsonObject.addProperty("decayDirection", Directions.serialize(transformer.decayDirection));
         jsonObject.addProperty("minDecay", Float.valueOf(transformer.minDecay));
         jsonObject.addProperty("maxDecay", Float.valueOf(transformer.maxDecay));
         jsonObject.addProperty("decayChaos", Float.valueOf(transformer.decayChaos));
         jsonObject.addProperty("decayValueDensity", Float.valueOf(transformer.decayValueDensity));
         jsonObject.addProperty("gravity", Boolean.valueOf(transformer.gravity));
         jsonObject.addProperty("blockErosion", Float.valueOf(transformer.blockErosion));
         jsonObject.addProperty("vineGrowth", Float.valueOf(transformer.vineGrowth));
         jsonObject.addProperty("cobwebGrowth", Float.valueOf(transformer.cobwebGrowth));
         return jsonObject;
      }
   }
}
