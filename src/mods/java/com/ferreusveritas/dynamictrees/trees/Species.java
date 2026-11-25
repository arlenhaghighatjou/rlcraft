package com.ferreusveritas.dynamictrees.trees;

import com.ferreusveritas.dynamictrees.ModBlocks;
import com.ferreusveritas.dynamictrees.ModConfigs;
import com.ferreusveritas.dynamictrees.api.BoneMealHelper;
import com.ferreusveritas.dynamictrees.api.IFullGenFeature;
import com.ferreusveritas.dynamictrees.api.IGenFeature;
import com.ferreusveritas.dynamictrees.api.IPostGenFeature;
import com.ferreusveritas.dynamictrees.api.IPostGrowFeature;
import com.ferreusveritas.dynamictrees.api.IPreGenFeature;
import com.ferreusveritas.dynamictrees.api.TreeHelper;
import com.ferreusveritas.dynamictrees.api.TreeRegistry;
import com.ferreusveritas.dynamictrees.api.network.INodeInspector;
import com.ferreusveritas.dynamictrees.api.network.MapSignal;
import com.ferreusveritas.dynamictrees.api.substances.IEmptiable;
import com.ferreusveritas.dynamictrees.api.substances.ISubstanceEffect;
import com.ferreusveritas.dynamictrees.api.substances.ISubstanceEffectProvider;
import com.ferreusveritas.dynamictrees.api.treedata.IDropCreator;
import com.ferreusveritas.dynamictrees.api.treedata.IDropCreatorStorage;
import com.ferreusveritas.dynamictrees.api.treedata.ILeavesProperties;
import com.ferreusveritas.dynamictrees.api.treedata.ITreePart;
import com.ferreusveritas.dynamictrees.blocks.BlockBonsaiPot;
import com.ferreusveritas.dynamictrees.blocks.BlockBranch;
import com.ferreusveritas.dynamictrees.blocks.BlockDynamicLeaves;
import com.ferreusveritas.dynamictrees.blocks.BlockDynamicSapling;
import com.ferreusveritas.dynamictrees.blocks.BlockFruit;
import com.ferreusveritas.dynamictrees.blocks.BlockRooty;
import com.ferreusveritas.dynamictrees.blocks.LeavesProperties;
import com.ferreusveritas.dynamictrees.entities.EntityFallingTree;
import com.ferreusveritas.dynamictrees.entities.EntityLingeringEffector;
import com.ferreusveritas.dynamictrees.entities.animation.IAnimationHandler;
import com.ferreusveritas.dynamictrees.event.BiomeSuitabilityEvent;
import com.ferreusveritas.dynamictrees.growthlogic.GrowthLogicKits;
import com.ferreusveritas.dynamictrees.growthlogic.IGrowthLogicKit;
import com.ferreusveritas.dynamictrees.items.Seed;
import com.ferreusveritas.dynamictrees.models.ModelEntityFallingTree;
import com.ferreusveritas.dynamictrees.seasons.SeasonHelper;
import com.ferreusveritas.dynamictrees.systems.DirtHelper;
import com.ferreusveritas.dynamictrees.systems.GrowSignal;
import com.ferreusveritas.dynamictrees.systems.dropcreators.DropCreatorLogs;
import com.ferreusveritas.dynamictrees.systems.dropcreators.DropCreatorSeed;
import com.ferreusveritas.dynamictrees.systems.dropcreators.DropCreatorStorage;
import com.ferreusveritas.dynamictrees.systems.nodemappers.NodeDisease;
import com.ferreusveritas.dynamictrees.systems.nodemappers.NodeFindEnds;
import com.ferreusveritas.dynamictrees.systems.nodemappers.NodeInflator;
import com.ferreusveritas.dynamictrees.systems.nodemappers.NodeShrinker;
import com.ferreusveritas.dynamictrees.systems.substances.SubstanceFertilize;
import com.ferreusveritas.dynamictrees.tileentity.TileEntitySpecies;
import com.ferreusveritas.dynamictrees.util.CoordUtils;
import com.ferreusveritas.dynamictrees.util.Deprecatron;
import com.ferreusveritas.dynamictrees.util.SafeChunkBounds;
import com.ferreusveritas.dynamictrees.util.SimpleVoxmap;
import com.ferreusveritas.dynamictrees.util.Styles;
import com.ferreusveritas.dynamictrees.worldgen.JoCode;
import com.ferreusveritas.dynamictrees.worldgen.JoCodeStore;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.BiomeDictionary.Type;
import net.minecraftforge.event.RegistryEvent.NewRegistry;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryBuilder;
import net.minecraftforge.registries.IForgeRegistryEntry.Impl;
import org.apache.logging.log4j.LogManager;

public class Species extends Impl<Species> {
   public static final Species NULLSPECIES = new Species() {
      public Seed getSeed() {
         return Seed.NULLSEED;
      }

      public TreeFamily getFamily() {
         return TreeFamily.NULLFAMILY;
      }

      public boolean isTransformable() {
         return false;
      }

      public void addJoCodes() {
      }

      public boolean plantSapling(World world, BlockPos pos) {
         return false;
      }

      public boolean generate(World world, BlockPos pos, Biome biome, Random random, int radius, SafeChunkBounds safeBounds) {
         return false;
      }

      public float biomeSuitability(World world, BlockPos pos) {
         return 0.0F;
      }

      public boolean addDropCreator(IDropCreator dropCreator) {
         return false;
      }

      public ItemStack setSeedStack(ItemStack newSeedStack) {
         return new ItemStack(this.getSeed());
      }

      public ItemStack getSeedStack(int qty) {
         return new ItemStack(this.getSeed());
      }

      public Species setupStandardSeedDropping() {
         return this;
      }

      public boolean update(World world, BlockRooty rootyDirt, BlockPos rootPos, int soilLife, ITreePart treeBase, BlockPos treePos, Random random, boolean rapid) {
         return false;
      }

      public boolean testFlowerSeasonHold(World world, BlockPos pos, float seasonValue) {
         return false;
      }
   };
   public static IForgeRegistry<Species> REGISTRY;
   protected final TreeFamily treeFamily;
   protected IGrowthLogicKit logicKit;
   protected float tapering;
   protected int upProbability;
   protected int lowestBranchHeight;
   protected float signalEnergy;
   protected float growthRate;
   protected int soilLongevity;
   protected int soilTypeFlags;
   private boolean requiresTileEntity;
   protected ILeavesProperties leavesProperties;
   private final List<ILeavesProperties> validLeaves;
   protected ItemStack seedStack;
   protected IBlockState saplingBlock;
   protected IDropCreatorStorage dropCreatorStorage;
   protected Map<Type, Float> envFactors;
   protected JoCodeStore joCodeStore;
   private Species megaSpecies;
   protected IFullGenFeature genFeatureOverride;
   protected List<IPreGenFeature> preGenFeatures;
   protected List<IPostGenFeature> postGenFeatures;
   protected List<IPostGrowFeature> postGrowFeatures;
   public int saplingModelId;
   private String unlocalizedName;
   private static final EnumFacing[] upFirst = new EnumFacing[]{EnumFacing.UP, EnumFacing.NORTH, EnumFacing.SOUTH, EnumFacing.EAST, EnumFacing.WEST};
   protected float flowerSeasonHoldMin;
   protected float flowerSeasonHoldMax;

   public static void newRegistry(NewRegistry event) {
      REGISTRY = (new RegistryBuilder()).setName(new ResourceLocation("dynamictrees", "species")).setDefaultKey(new ResourceLocation("dynamictrees", "null")).disableSaving().setType(Species.class).setIDRange(0, 2147483646).create();
   }

   public Species() {
      this.logicKit = GrowthLogicKits.nullLogic;
      this.tapering = 0.3F;
      this.upProbability = 2;
      this.lowestBranchHeight = 3;
      this.signalEnergy = 16.0F;
      this.growthRate = 1.0F;
      this.soilLongevity = 8;
      this.soilTypeFlags = 0;
      this.requiresTileEntity = false;
      this.validLeaves = new LinkedList();
      this.dropCreatorStorage = new DropCreatorStorage();
      this.envFactors = new HashMap();
      this.joCodeStore = new JoCodeStore(this);
      this.unlocalizedName = "";
      this.flowerSeasonHoldMin = 0.0F;
      this.flowerSeasonHoldMax = 0.5F;
      this.treeFamily = TreeFamily.NULLFAMILY;
      this.leavesProperties = LeavesProperties.NULLPROPERTIES;
   }

   public Species(ResourceLocation name, TreeFamily treeFamily) {
      this(name, treeFamily, treeFamily.getCommonLeaves());
   }

   public Species(ResourceLocation name, TreeFamily treeFamily, ILeavesProperties leavesProperties) {
      this.logicKit = GrowthLogicKits.nullLogic;
      this.tapering = 0.3F;
      this.upProbability = 2;
      this.lowestBranchHeight = 3;
      this.signalEnergy = 16.0F;
      this.growthRate = 1.0F;
      this.soilLongevity = 8;
      this.soilTypeFlags = 0;
      this.requiresTileEntity = false;
      this.validLeaves = new LinkedList();
      this.dropCreatorStorage = new DropCreatorStorage();
      this.envFactors = new HashMap();
      this.joCodeStore = new JoCodeStore(this);
      this.unlocalizedName = "";
      this.flowerSeasonHoldMin = 0.0F;
      this.flowerSeasonHoldMax = 0.5F;
      this.setRegistryName(name);
      this.setUnlocalizedName(name.toString());
      this.treeFamily = treeFamily;
      this.setLeavesProperties(leavesProperties);
      this.setStandardSoils();
      this.seedStack = new ItemStack(Seed.NULLSEED);
      this.saplingBlock = Blocks.AIR.getDefaultState();
      this.megaSpecies = NULLSPECIES;
      this.addJoCodes();
      this.addDropCreator(new DropCreatorLogs());
   }

   public boolean isValid() {
      return this != NULLSPECIES;
   }

   public void ifValid(Consumer<Species> c) {
      if (this.isValid()) {
         c.accept(this);
      }

   }

   public TreeFamily getFamily() {
      return this.treeFamily;
   }

   public Species setUnlocalizedName(String name) {
      this.unlocalizedName = name;
      return this;
   }

   public String getLocalizedName() {
      return I18n.translateToLocal(this.getUnlocalizedName() + ".name");
   }

   public String getUnlocalizedName() {
      return "species." + this.unlocalizedName;
   }

   public Species setBasicGrowingParameters(float tapering, float energy, int upProbability, int lowestBranchHeight, float growthRate) {
      this.tapering = tapering;
      this.signalEnergy = energy;
      this.upProbability = upProbability;
      this.lowestBranchHeight = lowestBranchHeight;
      this.growthRate = growthRate;
      return this;
   }

   public float getEnergy(World world, BlockPos rootPos) {
      return this.getGrowthLogicKit().getEnergy(world, rootPos, this, this.signalEnergy);
   }

   public float getGrowthRate(World world, BlockPos rootPos) {
      return this.growthRate * this.seasonalGrowthFactor(world, rootPos);
   }

   public int getUpProbability() {
      return this.upProbability;
   }

   public int getReinfTravel() {
      return 1;
   }

   public int getLowestBranchHeight() {
      return this.lowestBranchHeight;
   }

   public int getLowestBranchHeight(World world, BlockPos pos) {
      return this.getLowestBranchHeight();
   }

   public float getTapering() {
      return this.tapering;
   }

   public boolean getRequiresTileEntity(World world, BlockPos pos) {
      return this.requiresTileEntity;
   }

   public void setRequiresTileEntity(boolean truth) {
      this.requiresTileEntity = truth;
   }

   public boolean isTransformable() {
      return true;
   }

   public Species setLeavesProperties(ILeavesProperties leavesProperties) {
      this.leavesProperties = leavesProperties;
      this.addValidLeavesBlocks(leavesProperties);
      return this;
   }

   public ILeavesProperties getLeavesProperties() {
      return this.leavesProperties;
   }

   public void addValidLeavesBlocks(ILeavesProperties... leaves) {
      this.validLeaves.addAll(Arrays.asList(leaves));
   }

   public boolean isCompatibleLeaves(World world, BlockPos pos, IBlockState state) {
      if (!this.treeFamily.isCompatibleGenericLeaves(state, world, pos)) {
         return false;
      } else {
         BlockDynamicLeaves leaves = TreeHelper.getLeaves(state);
         if (leaves == null) {
            return false;
         } else {
            ILeavesProperties properties = leaves.getProperties(state);
            return this.leavesProperties == properties || this.isValidLeaves(properties);
         }
      }
   }

   public boolean isValidLeaves(ILeavesProperties leaves) {
      return this.validLeaves.contains(leaves);
   }

   public int getLeavesBlockIndex(IBlockState state) {
      if (!(state.getBlock() instanceof BlockDynamicLeaves)) {
         return 0;
      } else {
         BlockDynamicLeaves dynamicLeaves = (BlockDynamicLeaves)state.getBlock();
         return this.getLeavesBlockIndex(dynamicLeaves.getProperties(state));
      }
   }

   public int getLeavesBlockIndex(ILeavesProperties leavesProperties) {
      int index = this.validLeaves.indexOf(leavesProperties);
      if (index < 0) {
         LogManager.getLogger().warn("{} not valid leaves for {}.", leavesProperties, this);
         return 0;
      } else {
         return index;
      }
   }

   public ILeavesProperties getValidLeavesProperties(int index) {
      return (ILeavesProperties)this.validLeaves.get(index);
   }

   public IBlockState getValidLeavesBlock(int index) {
      return this.getValidLeavesProperties(index).getDynamicLeavesState();
   }

   public int colorTreeQuads(int defaultColor, ModelEntityFallingTree.TreeQuadData treeQuad, @Nullable EntityFallingTree entity) {
      return defaultColor;
   }

   public ItemStack getSeedStack(int qty) {
      if (this.seedStack == null) {
         return null;
      } else {
         ItemStack stack = this.seedStack.copy();
         stack.setCount(MathHelper.clamp(qty, 0, 64));
         return stack;
      }
   }

   public Seed getSeed() {
      return (Seed)this.seedStack.getItem();
   }

   public Species generateSeed() {
      Seed seed = new Seed(this.getRegistryName().getResourcePath() + "seed");
      this.setSeedStack(new ItemStack(seed));
      return this;
   }

   public ItemStack setSeedStack(ItemStack newSeedStack) {
      if (newSeedStack.getItem() instanceof Seed) {
         this.seedStack = newSeedStack;
         Seed seed = (Seed)this.seedStack.getItem();
         seed.setSpecies(this, this.seedStack);
         return this.seedStack;
      } else {
         System.err.println("setSeedStack must have an ItemStack with an Item that is an instance of a Seed");
         return ItemStack.EMPTY;
      }
   }

   public Species setupStandardSeedDropping() {
      this.addDropCreator(new DropCreatorSeed());
      return this;
   }

   public boolean addDropCreator(IDropCreator dropCreator) {
      return this.dropCreatorStorage.addDropCreator(dropCreator);
   }

   public boolean remDropCreator(ResourceLocation dropCreatorName) {
      return this.dropCreatorStorage.remDropCreator(dropCreatorName);
   }

   public Map<ResourceLocation, IDropCreator> getDropCreators() {
      return this.dropCreatorStorage.getDropCreators();
   }

   public List<ItemStack> getTreeHarvestDrops(World world, BlockPos leafPos, List<ItemStack> dropList, Random random) {
      dropList = TreeRegistry.globalDropCreatorStorage.getHarvestDrop(world, this, leafPos, random, dropList, 0, 0);
      return this.dropCreatorStorage.getHarvestDrop(world, this, leafPos, random, dropList, 0, 0);
   }

   public List<ItemStack> getVoluntaryDrops(World world, BlockPos rootPos, BlockPos treePos, int soilLife) {
      List<ItemStack> dropList = TreeRegistry.globalDropCreatorStorage.getVoluntaryDrop(world, this, rootPos, world.rand, (List)null, soilLife);
      return this.dropCreatorStorage.getVoluntaryDrop(world, this, rootPos, world.rand, dropList, soilLife);
   }

   public List<ItemStack> getLeavesDrops(IBlockAccess access, BlockPos breakPos, List<ItemStack> dropList, int fortune) {
      Random random = access instanceof World ? ((World)access).rand : new Random();
      dropList = TreeRegistry.globalDropCreatorStorage.getLeavesDrop(access, this, breakPos, random, dropList, fortune);
      return this.dropCreatorStorage.getLeavesDrop(access, this, breakPos, random, dropList, fortune);
   }

   public List<ItemStack> getLogsDrops(World world, BlockPos breakPos, List<ItemStack> dropList, float volume) {
      dropList = TreeRegistry.globalDropCreatorStorage.getLogsDrop(world, this, breakPos, world.rand, dropList, volume);
      return this.dropCreatorStorage.getLogsDrop(world, this, breakPos, world.rand, dropList, volume);
   }

   public Species.LogsAndSticks getLogsAndSticks(float volume) {
      int logs = (int)volume;
      int sticks = (int)((volume - (float)logs) * 8.0F);
      return new Species.LogsAndSticks(logs, sticks);
   }

   public boolean handleVoluntaryDrops(World world, List<BlockPos> endPoints, BlockPos rootPos, BlockPos treePos, int soilLife) {
      int tickSpeed = world.getGameRules().getInt("randomTickSpeed");
      if (tickSpeed > 0) {
         double slowFactor = 3.0D / (double)tickSpeed;
         if (world.rand.nextDouble() < slowFactor) {
            List<ItemStack> drops = this.getVoluntaryDrops(world, rootPos, treePos, soilLife);
            if (!drops.isEmpty() && !endPoints.isEmpty()) {
               for(ItemStack drop : drops) {
                  BlockPos branchPos = (BlockPos)endPoints.get(world.rand.nextInt(endPoints.size()));
                  branchPos = branchPos.up();
                  BlockPos itemPos = CoordUtils.getRayTraceFruitPos(world, this, treePos, branchPos, SafeChunkBounds.ANY);
                  if (itemPos != BlockPos.ORIGIN) {
                     EntityItem itemEntity = new EntityItem(world, (double)itemPos.getX() + 0.5D, (double)itemPos.getY() + 0.5D, (double)itemPos.getZ() + 0.5D, drop);
                     Vec3d motion = (new Vec3d(itemPos)).subtract(new Vec3d(treePos));
                     float distAngle = 15.0F;
                     float launchSpeed = 4.0F;
                     motion = (new Vec3d(motion.x, 0.0D, motion.y)).normalize().rotateYaw(world.rand.nextFloat() * distAngle * 2.0F - distAngle).scale((double)(launchSpeed / 20.0F));
                     itemEntity.motionX = motion.x;
                     itemEntity.motionY = motion.y;
                     itemEntity.motionZ = motion.z;
                     return world.spawnEntity(itemEntity);
                  }
               }
            }
         }
      }

      return true;
   }

   public boolean plantSapling(World world, BlockPos pos) {
      if (world.getBlockState(pos).getBlock().isReplaceable(world, pos) && BlockDynamicSapling.canSaplingStay(world, this, pos)) {
         ModBlocks.blockDynamicSapling.setSpecies(world, pos, this);
         return true;
      } else {
         return false;
      }
   }

   public boolean canGrowWithBoneMeal(World world, BlockPos pos) {
      return this.canBoneMeal();
   }

   public boolean canUseBoneMealNow(World world, Random rand, BlockPos pos) {
      return this.canBoneMeal();
   }

   public boolean canBoneMeal() {
      return true;
   }

   public boolean transitionToTree(World world, BlockPos pos) {
      TreeFamily family = this.getFamily();
      if (world.isAirBlock(pos.up()) && this.isAcceptableSoil(world, pos.down(), world.getBlockState(pos.down()))) {
         family.getDynamicBranch().setRadius(world, pos, (int)family.getPrimaryThickness(), (EnumFacing)null);
         world.setBlockState(pos.up(), this.getLeavesProperties().getDynamicLeavesState());
         this.placeRootyDirtBlock(world, pos.down(), 15);
         return true;
      } else {
         return false;
      }
   }

   public AxisAlignedBB getSaplingBoundingBox() {
      return new AxisAlignedBB(0.25D, 0.0D, 0.25D, 0.75D, 0.75D, 0.75D);
   }

   public ResourceLocation getSaplingName() {
      return this.getRegistryName();
   }

   public int saplingColorMultiplier(IBlockState state, IBlockAccess access, BlockPos pos, int tintIndex) {
      return this.getLeavesProperties().foliageColorMultiplier(state, access, pos);
   }

   public SoundType getSaplingSound() {
      return SoundType.PLANT;
   }

   public BlockRooty getRootyBlock(World world, BlockPos rootPos) {
      return this.getRequiresTileEntity(world, rootPos) ? ModBlocks.blockRootyDirtSpecies : ModBlocks.blockRootyDirt;
   }

   public boolean placeRootyDirtBlock(World world, BlockPos rootPos, int life) {
      world.setBlockState(rootPos, this.getRootyBlock(world, rootPos).getDefaultState().withProperty(BlockRooty.LIFE, Integer.valueOf(life)));
      TileEntity tileEntity = world.getTileEntity(rootPos);
      if (tileEntity instanceof TileEntitySpecies) {
         TileEntitySpecies speciesTE = (TileEntitySpecies)tileEntity;
         speciesTE.setSpecies(this);
      }

      return true;
   }

   public Species setSoilLongevity(int longevity) {
      this.soilLongevity = longevity;
      return this;
   }

   public int getSoilLongevity(World world, BlockPos rootPos) {
      return (int)(this.biomeSuitability(world, rootPos) * (float)this.soilLongevity);
   }

   public boolean isThick() {
      return false;
   }

   public int maxBranchRadius() {
      return this.isThick() ? 24 : 8;
   }

   /** @deprecated */
   @Deprecated
   public Species addAcceptableSoil(Block... soilBlocks) {
      Deprecatron.Complain("addAcceptableSoil", "The Block version of addAcceptableSoil is DEPRECATED. Species: " + this.getRegistryName());
      return this;
   }

   public Species addAcceptableSoils(String... soilTypes) {
      this.soilTypeFlags |= DirtHelper.getSoilFlags(soilTypes);
      return this;
   }

   public Species clearAcceptableSoils() {
      this.soilTypeFlags = 0;
      return this;
   }

   protected void setStandardSoils() {
      this.addAcceptableSoils("dirtlike");
   }

   public boolean isAcceptableSoil(IBlockState soilBlockState) {
      return DirtHelper.isSoilAcceptable(soilBlockState.getBlock(), this.soilTypeFlags);
   }

   public boolean isAcceptableSoil(World world, BlockPos pos, IBlockState soilBlockState) {
      return this.isAcceptableSoil(soilBlockState);
   }

   public boolean isAcceptableSoilForWorldgen(World world, BlockPos pos, IBlockState soilBlockState) {
      return this.isAcceptableSoil(world, pos, soilBlockState);
   }

   public boolean update(World world, BlockRooty rootyDirt, BlockPos rootPos, int soilLife, ITreePart treeBase, BlockPos treePos, Random random, boolean natural) {
      List<BlockPos> ends = this.getEnds(world, treePos, treeBase);
      if (this.handleRot(world, ends, rootPos, treePos, soilLife, SafeChunkBounds.ANY)) {
         return false;
      } else {
         if (natural) {
            this.handleVoluntaryDrops(world, ends, rootPos, treePos, soilLife);
            if (this.handleDisease(world, treeBase, treePos, random, soilLife)) {
               return true;
            }
         }

         return this.grow(world, rootyDirt, rootPos, soilLife, treeBase, treePos, random, natural);
      }
   }

   protected final List<BlockPos> getEnds(World world, BlockPos treePos, ITreePart treeBase) {
      NodeFindEnds endFinder = new NodeFindEnds();
      treeBase.analyse(world.getBlockState(treePos), world, treePos, (EnumFacing)null, new MapSignal(new INodeInspector[]{endFinder}));
      return endFinder.getEnds();
   }

   public boolean handleRot(World world, List<BlockPos> ends, BlockPos rootPos, BlockPos treePos, int soilLife, SafeChunkBounds safeBounds) {
      Iterator<BlockPos> iter = ends.iterator();
      SimpleVoxmap leafMap = this.getLeavesProperties().getCellKit().getLeafCluster();

      while(iter.hasNext()) {
         BlockPos endPos = (BlockPos)iter.next();
         IBlockState branchState = world.getBlockState(endPos);
         BlockBranch branch = TreeHelper.getBranch(branchState);
         if (branch != null) {
            int radius = branch.getRadius(branchState);
            float rotChance = this.rotChance(world, endPos, world.rand, radius);
            if (branch.checkForRot(world, endPos, this, radius, world.rand, rotChance, safeBounds != SafeChunkBounds.ANY) || radius != 1) {
               if (safeBounds != SafeChunkBounds.ANY) {
                  TreeHelper.ageVolume(world, endPos.down((leafMap.getLenZ() - 1) / 2), (leafMap.getLenX() - 1) / 2, leafMap.getLenY(), 2, safeBounds);
               }

               iter.remove();
            }
         }
      }

      return ends.isEmpty() && !TreeHelper.isBranch(world.getBlockState(treePos));
   }

   public boolean rot(World world, BlockPos pos, int neighborCount, int radius, Random random, boolean rapid) {
      if (radius <= 1) {
         BlockDynamicLeaves leaves = (BlockDynamicLeaves)this.getLeavesProperties().getDynamicLeavesState().getBlock();

         for(EnumFacing dir : upFirst) {
            if (leaves.growLeavesIfLocationIsSuitable(world, this.getLeavesProperties(), pos.offset(dir), 0)) {
               return false;
            }
         }
      }

      if (rapid || ModConfigs.maxBranchRotRadius != 0 && radius <= ModConfigs.maxBranchRotRadius) {
         BlockBranch branch = TreeHelper.getBranch(world.getBlockState(pos));
         if (branch != null) {
            branch.rot(world, pos);
         }

         return true;
      } else {
         return false;
      }
   }

   public float rotChance(World world, BlockPos pos, Random rand, int radius) {
      return 0.3F + (float)(8 - radius) * 0.1F;
   }

   public boolean grow(World world, BlockRooty rootyDirt, BlockPos rootPos, int soilLife, ITreePart treeBase, BlockPos treePos, Random random, boolean natural) {
      float growthRate = this.getGrowthRate(world, rootPos) * ModConfigs.treeGrowthMultiplier * (float)ModConfigs.treeGrowthFolding;

      while(true) {
         if (soilLife > 0 && growthRate > random.nextFloat()) {
            GrowSignal signal = new GrowSignal(this, rootPos, this.getEnergy(world, rootPos));
            boolean success = treeBase.growSignal(world, treePos, signal).success;
            int soilLongevity = this.getSoilLongevity(world, rootPos) * (success ? 1 : 16);
            if (soilLongevity <= 0 || random.nextInt(soilLongevity) == 0) {
               rootyDirt.setSoilLife(world, rootPos, soilLife - 1);
            }

            if (signal.choked) {
               soilLife = 0;
               rootyDirt.setSoilLife(world, rootPos, soilLife);
               TreeHelper.startAnalysisFromRoot(world, rootPos, new MapSignal(new INodeInspector[]{new NodeShrinker(signal.getSpecies())}));
            }
         }

         if (--growthRate <= 0.0F) {
            break;
         }
      }

      return this.postGrow(world, rootPos, treePos, soilLife, natural);
   }

   public Species setGrowthLogicKit(IGrowthLogicKit logicKit) {
      this.logicKit = logicKit;
      return this;
   }

   public IGrowthLogicKit getGrowthLogicKit() {
      return this.logicKit;
   }

   public EnumFacing selectNewDirection(World world, BlockPos pos, BlockBranch branch, GrowSignal signal) {
      EnumFacing originDir = signal.dir.getOpposite();
      if (signal.numSteps + 1 <= this.getLowestBranchHeight(world, signal.rootPos)) {
         return EnumFacing.UP;
      } else {
         int[] probMap = new int[6];
         probMap[EnumFacing.UP.ordinal()] = signal.dir != EnumFacing.DOWN ? this.getUpProbability() : 0;
         int var10001 = signal.dir.ordinal();
         probMap[var10001] += this.getReinfTravel();

         for(EnumFacing dir : EnumFacing.VALUES) {
            if (!dir.equals(originDir)) {
               BlockPos deltaPos = pos.offset(dir);
               IBlockState deltaBlockState = world.getBlockState(deltaPos);
               var10001 = dir.getIndex();
               probMap[var10001] += TreeHelper.getTreePart(deltaBlockState).probabilityForBlock(deltaBlockState, world, deltaPos, branch);
            }
         }

         probMap = this.customDirectionManipulation(world, pos, branch.getRadius(world.getBlockState(pos)), signal, probMap);
         int choice = com.ferreusveritas.dynamictrees.util.MathHelper.selectRandomFromDistribution(signal.rand, probMap);
         return this.newDirectionSelected(EnumFacing.getFront(choice != -1 ? choice : 1), signal);
      }
   }

   protected int[] customDirectionManipulation(World world, BlockPos pos, int radius, GrowSignal signal, int[] probMap) {
      return this.getGrowthLogicKit().directionManipulation(world, pos, this, radius, signal, probMap);
   }

   protected EnumFacing newDirectionSelected(EnumFacing newDir, GrowSignal signal) {
      return this.getGrowthLogicKit().newDirectionSelected(this, newDir, signal);
   }

   public boolean postGrow(World world, BlockPos rootPos, BlockPos treePos, int soilLife, boolean natural) {
      if (this.postGrowFeatures != null) {
         for(IPostGrowFeature feature : this.postGrowFeatures) {
            feature.postGrow(world, rootPos, treePos, this, soilLife, natural);
         }
      }

      return true;
   }

   public boolean handleDisease(World world, ITreePart baseTreePart, BlockPos treePos, Random random, int soilLife) {
      if (soilLife == 0 && ModConfigs.diseaseChance > random.nextFloat()) {
         baseTreePart.analyse(world.getBlockState(treePos), world, treePos, EnumFacing.DOWN, new MapSignal(new INodeInspector[]{new NodeDisease(this)}));
         return true;
      } else {
         return false;
      }
   }

   public Species envFactor(Type type, float factor) {
      this.envFactors.put(type, Float.valueOf(factor));
      return this;
   }

   public float biomeSuitability(World world, BlockPos pos) {
      Biome biome = world.getBiome(pos);
      BiomeSuitabilityEvent suitabilityEvent = new BiomeSuitabilityEvent(world, biome, this, pos);
      MinecraftForge.EVENT_BUS.post(suitabilityEvent);
      if (suitabilityEvent.isHandled()) {
         return suitabilityEvent.getSuitability();
      } else {
         float ugs = ModConfigs.scaleBiomeGrowthRate;
         if (ugs != 1.0F && !this.isBiomePerfect(biome)) {
            float suit = defaultSuitability();

            for(Type t : BiomeDictionary.getTypes(biome)) {
               suit *= this.envFactors.containsKey(t) ? ((Float)this.envFactors.get(t)).floatValue() : 1.0F;
            }

            suit = ugs <= 0.5F ? ugs * 2.0F * suit : ((1.0F - ugs) * suit + (ugs - 0.5F)) * 2.0F;
            return MathHelper.clamp(suit, 0.0F, 1.0F);
         } else {
            return 1.0F;
         }
      }
   }

   public boolean isBiomePerfect(Biome biome) {
      return false;
   }

   public static final float defaultSuitability() {
      return 0.85F;
   }

   public static boolean isOneOfBiomes(Biome biomeToCheck, Biome... biomes) {
      for(Biome biome : biomes) {
         if (biomeToCheck == biome) {
            return true;
         }
      }

      return false;
   }

   public float seasonalGrowthFactor(World world, BlockPos rootPos) {
      return ModConfigs.enableSeasonalGrowthFactor ? SeasonHelper.globalSeasonalGrowthFactor(world, rootPos) : 1.0F;
   }

   public float seasonalSeedDropFactor(World world, BlockPos pos) {
      return ModConfigs.enableSeasonalSeedDropFactor ? SeasonHelper.globalSeasonalSeedDropFactor(world, pos) : 1.0F;
   }

   public float seasonalFruitProductionFactor(World world, BlockPos pos) {
      return ModConfigs.enableSeasonalFruitProductionFactor ? SeasonHelper.globalSeasonalFruitProductionFactor(world, pos) : 1.0F;
   }

   public int getSeasonalTooltipFlags(int dimension) {
      float seasonStart = 0.167F;
      float seasonEnd = 0.833F;
      float threshold = 0.75F;
      if (BlockFruit.getFruitBlockForSpecies(this) != null) {
         int seasonFlags = 0;

         for(int i = 0; i < 4; ++i) {
            float prod1 = this.seasonalFruitProductionFactor((World)null, new BlockPos(dimension, (int)(((float)i + seasonStart) * 64.0F), 0));
            float prod2 = this.seasonalFruitProductionFactor((World)null, new BlockPos(dimension, (int)(((float)i + seasonEnd) * 64.0F), 0));
            if (Math.min(prod1, prod2) >= threshold) {
               seasonFlags |= 1 << i;
            }
         }

         return seasonFlags;
      } else {
         return 0;
      }
   }

   public Species setFlowerSeasonHold(float min, float max) {
      this.flowerSeasonHoldMin = min;
      this.flowerSeasonHoldMax = max;
      return this;
   }

   public boolean testFlowerSeasonHold(World world, BlockPos pos, float seasonValue) {
      return SeasonHelper.isSeasonBetween(seasonValue, this.flowerSeasonHoldMin, this.flowerSeasonHoldMax);
   }

   public ISubstanceEffect getSubstanceEffect(ItemStack itemStack, World world, BlockPos pos) {
      return (ISubstanceEffect)(this.canBoneMeal() && BoneMealHelper.isBoneMeal(itemStack, world, pos, this) ? (new SubstanceFertilize()).setAmount(2).setGrow(true).setPulses(ModConfigs.boneMealGrowthPulses) : this.getSubstanceEffect(itemStack));
   }

   public ISubstanceEffect getSubstanceEffect(ItemStack itemStack) {
      if (itemStack.getItem() instanceof ISubstanceEffectProvider) {
         ISubstanceEffectProvider provider = (ISubstanceEffectProvider)itemStack.getItem();
         return provider.getSubstanceEffect(itemStack);
      } else {
         return null;
      }
   }

   public boolean applySubstance(World world, BlockPos rootPos, BlockPos hitPos, EntityPlayer player, EnumHand hand, ItemStack itemStack) {
      ISubstanceEffect effect = this.getSubstanceEffect(itemStack, world, hitPos);
      if (effect != null) {
         ISubstanceEffect.Result result = effect.apply(world, rootPos, hitPos);
         if (!result.success() && world.isRemote && !result.getErrorKey().isEmpty()) {
            Minecraft.getMinecraft().ingameGUI.setOverlayMessage((new TextComponentTranslation(result.getErrorKey(), result.getArgs())).setStyle(Styles.ERROR), false);
            return false;
         } else if (result.success() && effect.isLingering()) {
            world.spawnEntity(new EntityLingeringEffector(world, rootPos, effect));
            return true;
         } else {
            return result.success();
         }
      } else {
         return false;
      }
   }

   public boolean onTreeActivated(World world, BlockPos rootPos, BlockPos hitPos, IBlockState state, EntityPlayer player, EnumHand hand, ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {
      if (heldItem != null && this.applySubstance(world, rootPos, hitPos, player, hand, heldItem)) {
         consumePlayerItem(player, hand, heldItem);
         return true;
      } else {
         return false;
      }
   }

   public static void consumePlayerItem(EntityPlayer player, EnumHand hand, ItemStack heldItem) {
      if (!player.capabilities.isCreativeMode) {
         ItemStack emptyBottleStack = getEmptyBottleStack(heldItem);
         heldItem.shrink(1);
         if (emptyBottleStack != null) {
            givePlayerEmptyBottle(player, hand, heldItem, emptyBottleStack);
         }
      }

   }

   private static ItemStack getEmptyBottleStack(ItemStack heldItem) {
      Item item = heldItem.getItem();
      if (item instanceof IEmptiable) {
         return ((IEmptiable)item).getEmptyContainer();
      } else {
         return item instanceof ItemPotion ? new ItemStack(Items.GLASS_BOTTLE) : null;
      }
   }

   private static void givePlayerEmptyBottle(EntityPlayer player, EnumHand hand, ItemStack heldItem, ItemStack emptyBottleStack) {
      if (heldItem.isEmpty()) {
         player.setHeldItem(hand, emptyBottleStack);
      } else {
         player.inventory.addItemStackToInventory(emptyBottleStack);
      }

   }

   public boolean useDefaultWailaBody() {
      return true;
   }

   public boolean showSpeciesOnWaila() {
      return this != this.getFamily().getCommonSpecies();
   }

   public boolean isMega() {
      return false;
   }

   public Species getMegaSpecies() {
      return this.megaSpecies;
   }

   public void setMegaSpecies(Species megaSpecies) {
      if (this.megaSpecies == NULLSPECIES) {
         this.megaSpecies = megaSpecies;
      }

   }

   public IAnimationHandler selectAnimationHandler(EntityFallingTree fallingEntity) {
      return this.getFamily().selectAnimationHandler(fallingEntity);
   }

   public BlockBonsaiPot getBonzaiPot() {
      return ModBlocks.blockBonsaiPot;
   }

   public boolean generate(World world, BlockPos rootPos, Biome biome, Random random, int radius, SafeChunkBounds safeBounds) {
      if (this.genFeatureOverride != null) {
         return this.genFeatureOverride.generate(world, rootPos, this, biome, random, radius, safeBounds);
      } else {
         EnumFacing facing = CoordUtils.getRandomDir(random);
         if (this.getJoCodeStore() != null) {
            JoCode code = this.getJoCodeStore().getRandomCode(radius, random);
            if (code != null) {
               code.generate(world, this, rootPos, biome, facing, radius, safeBounds);
               return true;
            }
         }

         return false;
      }
   }

   public JoCodeStore getJoCodeStore() {
      return this.joCodeStore;
   }

   public JoCode getJoCode(String joCodeString) {
      return new JoCode(joCodeString);
   }

   public JoCode getJoCode(World world, BlockPos rootPos, EnumFacing facing) {
      return new JoCode(world, rootPos, facing);
   }

   public void addJoCodes() {
      this.joCodeStore.addCodesFromFile(this, "assets/" + this.getRegistryName().getResourceDomain() + "/trees/" + this.getRegistryName().getResourcePath() + ".txt");
   }

   public Species addGenFeature(IGenFeature module) {
      this.addGenFeature(module, 14);
      return this;
   }

   public Species addGenFeature(IGenFeature module, int allowableFlags) {
      if (module instanceof IFullGenFeature && (allowableFlags & 1) != 0) {
         this.genFeatureOverride = (IFullGenFeature)module;
      }

      if (module instanceof IPreGenFeature && (allowableFlags & 2) != 0) {
         IPreGenFeature feature = (IPreGenFeature)module;
         if (this.preGenFeatures == null) {
            this.preGenFeatures = new ArrayList(1);
         }

         this.preGenFeatures.add(feature);
      }

      if (module instanceof IPostGenFeature && (allowableFlags & 4) != 0) {
         IPostGenFeature feature = (IPostGenFeature)module;
         if (this.postGenFeatures == null) {
            this.postGenFeatures = new ArrayList(1);
         }

         this.postGenFeatures.add(feature);
      }

      if (module instanceof IPostGrowFeature && (allowableFlags & 8) != 0) {
         IPostGrowFeature feature = (IPostGrowFeature)module;
         if (this.postGrowFeatures == null) {
            this.postGrowFeatures = new ArrayList(1);
         }

         this.postGrowFeatures.add(feature);
      }

      return this;
   }

   public BlockPos preGeneration(World world, BlockPos rootPos, int radius, EnumFacing facing, SafeChunkBounds safeBounds, JoCode joCode) {
      if (this.preGenFeatures != null) {
         for(IPreGenFeature feature : this.preGenFeatures) {
            rootPos = feature.preGeneration(world, rootPos, this, radius, facing, safeBounds, joCode);
         }
      }

      return rootPos;
   }

   public void postGeneration(World world, BlockPos rootPos, Biome biome, int radius, List<BlockPos> endPoints, SafeChunkBounds safeBounds, IBlockState initialDirtState) {
      if (this.postGenFeatures != null) {
         for(IPostGenFeature feature : this.postGenFeatures) {
            feature.postGeneration(world, rootPos, this, biome, radius, endPoints, safeBounds, initialDirtState);
         }
      }

   }

   public float getWorldGenTaperingFactor() {
      return 1.5F;
   }

   public int getWorldGenLeafMapHeight() {
      return 32;
   }

   public int getWorldGenAgeIterations() {
      return 3;
   }

   public INodeInspector getNodeInflator(SimpleVoxmap leafMap) {
      return new NodeInflator(this, leafMap);
   }

   public int coordHashCode(BlockPos pos) {
      return CoordUtils.coordHashCode(pos, 2);
   }

   public String toString() {
      return this.getRegistryName().toString();
   }

   public class LogsAndSticks {
      public final int logs;
      public final int sticks;

      public LogsAndSticks(int logs, int sticks) {
         this.logs = logs;
         this.sticks = ModConfigs.dropSticks ? sticks : 0;
      }
   }
}
