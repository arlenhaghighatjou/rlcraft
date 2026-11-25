package com.ferreusveritas.dynamictrees.blocks;

import com.ferreusveritas.dynamictrees.ModBlocks;
import com.ferreusveritas.dynamictrees.ModConfigs;
import com.ferreusveritas.dynamictrees.api.IFutureBreakable;
import com.ferreusveritas.dynamictrees.api.TreeHelper;
import com.ferreusveritas.dynamictrees.api.network.INodeInspector;
import com.ferreusveritas.dynamictrees.api.network.MapSignal;
import com.ferreusveritas.dynamictrees.api.treedata.ITreePart;
import com.ferreusveritas.dynamictrees.entities.EntityFallingTree;
import com.ferreusveritas.dynamictrees.event.FutureBreak;
import com.ferreusveritas.dynamictrees.systems.nodemappers.NodeDestroyer;
import com.ferreusveritas.dynamictrees.systems.nodemappers.NodeExtState;
import com.ferreusveritas.dynamictrees.systems.nodemappers.NodeNetVolume;
import com.ferreusveritas.dynamictrees.systems.nodemappers.NodeSpecies;
import com.ferreusveritas.dynamictrees.trees.Species;
import com.ferreusveritas.dynamictrees.trees.TreeFamily;
import com.ferreusveritas.dynamictrees.util.BlockBounds;
import com.ferreusveritas.dynamictrees.util.BranchDestructionData;
import com.ferreusveritas.dynamictrees.util.SimpleVoxmap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.material.EnumPushReaction;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.Enchantments;
import net.minecraft.item.Item;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.BlockPos.MutableBlockPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.property.IUnlistedProperty;
import net.minecraftforge.common.property.Properties.PropertyAdapter;
import net.minecraftforge.event.ForgeEventFactory;

public abstract class BlockBranch extends Block implements ITreePart, IFutureBreakable {
   public static final int RADMAX_NORMAL = 8;
   public static final IUnlistedProperty[] CONNECTIONS = new IUnlistedProperty[]{new PropertyAdapter(PropertyInteger.create("radiusd", 0, 8)), new PropertyAdapter(PropertyInteger.create("radiusu", 0, 8)), new PropertyAdapter(PropertyInteger.create("radiusn", 0, 8)), new PropertyAdapter(PropertyInteger.create("radiuss", 0, 8)), new PropertyAdapter(PropertyInteger.create("radiusw", 0, 8)), new PropertyAdapter(PropertyInteger.create("radiuse", 0, 8))};
   private TreeFamily tree = TreeFamily.NULLFAMILY;
   public static BlockBranch.EnumDestroyMode destroyMode = BlockBranch.EnumDestroyMode.SLOPPY;

   public BlockBranch(Material material, String name) {
      super(material);
      this.setUnlocalizedName(name);
      this.setRegistryName(name);
      this.setHarvestLevel("axe", 0);
   }

   public IProperty<?>[] getIgnorableProperties() {
      return new IProperty[0];
   }

   public void setFamily(TreeFamily tree) {
      this.tree = tree;
   }

   public TreeFamily getFamily() {
      return this.tree;
   }

   public TreeFamily getFamily(IBlockState state, IBlockAccess world, BlockPos pos) {
      return this.getFamily();
   }

   public boolean isSameTree(ITreePart treepart) {
      return this.isSameTree(TreeHelper.getBranch(treepart));
   }

   public boolean isSameTree(BlockBranch branch) {
      return branch != null && this.getFamily() == branch.getFamily();
   }

   public abstract int branchSupport(IBlockState var1, IBlockAccess var2, BlockBranch var3, BlockPos var4, EnumFacing var5, int var6);

   public boolean isWood(IBlockAccess world, BlockPos pos) {
      return this.getFamily().isWood();
   }

   public abstract boolean checkForRot(World var1, BlockPos var2, Species var3, int var4, EaglercraftRandom var5, float var6, boolean var7);

   public static int setSupport(int branches, int leaves) {
      return (branches & 15) << 4 | leaves & 15;
   }

   public static int getBranchSupport(int support) {
      return support >> 4 & 15;
   }

   public static int getLeavesSupport(int support) {
      return support & 15;
   }

   public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
      ItemStack heldItem = player.getHeldItem(hand);
      return TreeHelper.getTreePart(state).getFamily(state, world, pos).onTreeActivated(world, pos, state, player, hand, heldItem, facing, hitX, hitY, hitZ);
   }

   public boolean isOpaqueCube(IBlockState state) {
      return false;
   }

   public boolean isFullCube(IBlockState state) {
      return false;
   }

   public boolean shouldSideBeRendered(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side) {
      return true;
   }

   public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
      return BlockFaceShape.UNDEFINED;
   }

   public int getRadius(IBlockState state) {
      return 1;
   }

   public abstract int setRadius(World var1, BlockPos var2, int var3, EnumFacing var4, int var5);

   public int setRadius(World world, BlockPos pos, int radius, EnumFacing originDir) {
      return this.setRadius(world, pos, radius, originDir, 2);
   }

   public abstract IBlockState getStateForRadius(int var1);

   public int getMaxRadius() {
      return 8;
   }

   public boolean shouldAnalyse() {
      return true;
   }

   public BranchDestructionData destroyBranchFromNode(World world, BlockPos cutPos, EnumFacing toolDir, boolean wholeTree) {
      IBlockState blockState = world.getBlockState(cutPos);
      NodeSpecies nodeSpecies = new NodeSpecies();
      MapSignal signal = this.analyse(blockState, world, cutPos, (EnumFacing)null, new MapSignal(new INodeInspector[]{nodeSpecies}));
      Species species = nodeSpecies.getSpecies();
      NodeExtState extStateMapper = new NodeExtState(cutPos);
      this.analyse(blockState, world, cutPos, wholeTree ? null : signal.localRootDir, new MapSignal(new INodeInspector[]{extStateMapper}));
      NodeNetVolume volumeSum = new NodeNetVolume();
      NodeDestroyer destroyer = new NodeDestroyer(species);
      destroyMode = BlockBranch.EnumDestroyMode.HARVEST;
      this.analyse(blockState, world, cutPos, wholeTree ? null : signal.localRootDir, new MapSignal(new INodeInspector[]{volumeSum, destroyer}));
      destroyMode = BlockBranch.EnumDestroyMode.SLOPPY;
      List<BlockPos> endPoints = destroyer.getEnds();
      Map<BlockPos, IBlockState> destroyedLeaves = new HashMap();
      List<BlockBranch.BlockItemStack> leavesDropsList = new ArrayList();
      this.destroyLeaves(world, cutPos, species, endPoints, destroyedLeaves, leavesDropsList);
      endPoints = (List)endPoints.stream().map((p) -> {
         return p.subtract(cutPos);
      }).collect(Collectors.toList());
      int trunkHeight = 1;

      for(BlockPos iter = new BlockPos(0, 1, 0); extStateMapper.getExtStateMap().containsKey(iter); iter = iter.up()) {
         ++trunkHeight;
      }

      EnumFacing cutDir = signal.localRootDir;
      if (cutDir == null) {
         cutDir = EnumFacing.DOWN;
      }

      return new BranchDestructionData(species, extStateMapper.getExtStateMap(), destroyedLeaves, leavesDropsList, endPoints, volumeSum.getVolume(), cutPos, cutDir, toolDir, trunkHeight);
   }

   public void rot(World world, BlockPos pos) {
      this.breakDeliberate(world, pos, BlockBranch.EnumDestroyMode.ROT);
   }

   protected void destroyLeaves(World world, BlockPos cutPos, Species species, List<BlockPos> endPoints, Map<BlockPos, IBlockState> destroyedLeaves, List<BlockBranch.BlockItemStack> drops) {
      if (!world.isRemote && !endPoints.isEmpty()) {
         BlockBounds bounds = (new BlockBounds(endPoints)).expand(3);
         SimpleVoxmap vmap = new SimpleVoxmap(bounds);

         for(BlockPos endPos : endPoints) {
            for(BlockPos leafPos : BlockPos.getAllInBoxMutable(endPos.add(-3, -3, -3), endPos.add(3, 3, 3))) {
               vmap.setVoxel(leafPos, (byte)1);
            }

            vmap.setVoxel(endPos, (byte)0);
         }

         TreeFamily family = species.getFamily();
         BlockBranch familyBranch = family.getDynamicBranch();
         int primaryThickness = (int)family.getPrimaryThickness();

         for(MutableBlockPos findPos : bounds.expand(3).iterate()) {
            IBlockState findState = world.getBlockState(findPos);
            if (familyBranch.getRadius(findState) == primaryThickness) {
               for(MutableBlockPos leafpos : species.getLeavesProperties().getCellKit().getLeafCluster().getAllNonZero()) {
                  vmap.setVoxel(findPos.getX() + leafpos.getX(), findPos.getY() + leafpos.getY(), findPos.getZ() + leafpos.getZ(), (byte)0);
               }
            }
         }

         ArrayList<ItemStack> dropList = new ArrayList();

         for(SimpleVoxmap.Cell cell : vmap.getAllNonZeroCells()) {
            MutableBlockPos pos = cell.getPos();
            IBlockState state = world.getBlockState(pos);
            if (species.isCompatibleLeaves(world, pos, state)) {
               dropList.clear();
               species.getTreeHarvestDrops(world, pos, dropList, world.rand);
               BlockPos imPos = pos.toImmutable();
               BlockPos relPos = imPos.subtract(cutPos);
               world.setBlockState(imPos, ModBlocks.blockStates.air, 0);
               destroyedLeaves.put(relPos, state);
               dropList.forEach((i) -> {
                  drops.add(new BlockBranch.BlockItemStack(i, relPos));
               });
            }
         }
      }

   }

   public boolean canFall() {
      return false;
   }

   public List<ItemStack> getLogDrops(World world, BlockPos pos, Species species, float volume) {
      List<ItemStack> ret = new ArrayList();
      volume = volume * ModConfigs.treeHarvestMultiplier;
      return species.getLogsDrops(world, pos, ret, volume);
   }

   public void futureBreak(IBlockState state, World world, BlockPos cutPos, EntityLivingBase entity) {
      double reachDistance = entity instanceof EntityPlayerMP ? entity.getEntityAttribute(EntityPlayer.REACH_DISTANCE).getAttributeValue() : 5.0D;
      RayTraceResult rtResult = this.playerRayTrace(entity, reachDistance, 1.0F);
      EnumFacing toolDir = rtResult != null ? (entity.isSneaking() ? rtResult.sideHit.getOpposite() : rtResult.sideHit) : EnumFacing.DOWN;
      if (toolDir == null) {
         toolDir = EnumFacing.DOWN;
      }

      BranchDestructionData destroyData = this.destroyBranchFromNode(world, cutPos, toolDir, false);
      ItemStack heldItem = entity.getHeldItemMainhand();
      int fortune = EnchantmentHelper.getEnchantmentLevel(Enchantments.FORTUNE, heldItem);
      float fortuneFactor = 1.0F + 0.25F * (float)fortune;
      float woodVolume = destroyData.woodVolume;
      List<ItemStack> woodItems = this.getLogDrops(world, cutPos, destroyData.species, woodVolume * fortuneFactor);
      if (entity.getActiveHand() == null) {
         entity.setActiveHand(EnumHand.MAIN_HAND);
      }

      float chance = 1.0F;
      if (entity instanceof EntityPlayer) {
         chance = ForgeEventFactory.fireBlockHarvesting(woodItems, world, cutPos, state, fortune, chance, false, (EntityPlayer)entity);
      }

      List<ItemStack> woodDropList = (List)woodItems.stream().filter((i) -> {
         return world.rand.nextFloat() <= finalChance;
      }).collect(Collectors.toList());
      EntityFallingTree.dropTree(world, destroyData, woodDropList, EntityFallingTree.DestroyType.HARVEST);
      this.damageAxe(entity, heldItem, this.getRadius(state), woodVolume);
   }

   public boolean removedByPlayer(IBlockState state, World world, BlockPos cutPos, EntityPlayer player, boolean canHarvest) {
      return this.removedByEntity(state, world, cutPos, player);
   }

   public boolean removedByEntity(IBlockState state, World world, BlockPos cutPos, EntityLivingBase entity) {
      FutureBreak.add(new FutureBreak(state, world, cutPos, entity, 0));
      return false;
   }

   protected void sloppyBreak(World world, BlockPos cutPos, EntityFallingTree.DestroyType destroyType) {
      BranchDestructionData destroyData = this.destroyBranchFromNode(world, cutPos, EnumFacing.DOWN, false);
      List<ItemStack> woodDropList = Collections.EMPTY_LIST;
      destroyData.leavesDrops.clear();
      EntityFallingTree.dropTree(world, destroyData, woodDropList, destroyType);
   }

   @Nullable
   public RayTraceResult playerRayTrace(EntityLivingBase entity, double blockReachDistance, float partialTicks) {
      Vec3d vec3d = entity.getPositionEyes(partialTicks);
      Vec3d vec3d1 = entity.getLook(partialTicks);
      Vec3d vec3d2 = vec3d.addVector(vec3d1.x * blockReachDistance, vec3d1.y * blockReachDistance, vec3d1.z * blockReachDistance);
      return entity.world.rayTraceBlocks(vec3d, vec3d2, false, false, true);
   }

   public void damageAxe(EntityLivingBase entity, ItemStack heldItem, int radius, float woodVolume) {
      if (heldItem != null) {
         Set<String> toolClasses = heldItem.getItem().getToolClasses(heldItem);
         if (heldItem.getItem() instanceof ItemAxe || toolClasses.contains("axe") || toolClasses.contains("mattock")) {
            int damage;
            switch(ModConfigs.axeDamageMode) {
            case VANILLA:
            default:
               damage = 1;
               break;
            case THICKNESS:
               damage = Math.max(1, radius) / 2;
               break;
            case VOLUME:
               damage = (int)woodVolume;
            }

            --damage;
            if (damage > 0) {
               heldItem.damageItem(damage, entity);
            }
         }

      }
   }

   public void breakBlock(World world, BlockPos pos, IBlockState state) {
      if (!world.isRemote && destroyMode == BlockBranch.EnumDestroyMode.SLOPPY) {
         IBlockState toBlockState = world.getBlockState(pos);
         Block toBlock = toBlockState.getBlock();
         if (toBlock instanceof BlockBranch) {
            return;
         }

         if (toBlock == Blocks.AIR) {
            world.setBlockState(pos, state, 0);
            this.sloppyBreak(world, pos, EntityFallingTree.DestroyType.VOID);
            this.setBlockStateIgnored(world, pos, ModBlocks.blockStates.air, 2);
            return;
         }

         if (toBlock == Blocks.FIRE) {
            world.setBlockState(pos, state, 0);
            this.sloppyBreak(world, pos, EntityFallingTree.DestroyType.FIRE);
            this.setBlockStateIgnored(world, pos, ModBlocks.blockStates.air, 2);
            return;
         }

         if (!toBlock.hasTileEntity(toBlockState) && world.getTileEntity(pos) == null) {
            world.setBlockState(pos, state, 0);
            this.sloppyBreak(world, pos, EntityFallingTree.DestroyType.VOID);
            this.setBlockStateIgnored(world, pos, toBlockState, 2);
            return;
         }

         for(EnumFacing dir : EnumFacing.VALUES) {
            BlockPos offPos = pos.offset(dir);
            IBlockState offState = world.getBlockState(offPos);
            if (offState.getBlock() instanceof BlockBranch) {
               this.sloppyBreak(world, offPos, EntityFallingTree.DestroyType.VOID);
            }
         }
      }

   }

   public void setBlockStateIgnored(World world, BlockPos pos, IBlockState state, int flags) {
      destroyMode = BlockBranch.EnumDestroyMode.IGNORE;
      world.setBlockState(pos, state, flags);
      destroyMode = BlockBranch.EnumDestroyMode.SLOPPY;
   }

   public void onBlockHarvested(World world, BlockPos pos, IBlockState state, EntityPlayer player) {
   }

   public void breakDeliberate(World world, BlockPos pos, BlockBranch.EnumDestroyMode mode) {
      destroyMode = mode;
      world.setBlockToAir(pos);
      destroyMode = BlockBranch.EnumDestroyMode.SLOPPY;
   }

   public Item getItemDropped(IBlockState state, EaglercraftRandom rand, int fortune) {
      return null;
   }

   public int quantityDropped(EaglercraftRandom random) {
      return 0;
   }

   public boolean canSilkHarvest(World world, BlockPos pos, IBlockState state, EntityPlayer player) {
      return false;
   }

   public EnumPushReaction getMobilityFlag(IBlockState state) {
      return EnumPushReaction.BLOCK;
   }

   public void onBlockExploded(World world, BlockPos pos, Explosion explosion) {
      IBlockState state = world.getBlockState(pos);
      if (state.getBlock() == this) {
         Species species = TreeHelper.getExactSpecies(world, pos);
         BranchDestructionData destroyData = this.destroyBranchFromNode(world, pos, EnumFacing.DOWN, false);
         float woodVolume = destroyData.woodVolume;
         List<ItemStack> woodDropList = this.getLogDrops(world, pos, species, woodVolume);
         EntityFallingTree treeEntity = EntityFallingTree.dropTree(world, destroyData, woodDropList, EntityFallingTree.DestroyType.BLAST);
         if (treeEntity != null) {
            Vec3d expPos = explosion.getPosition();
            EntityLivingBase placer = explosion.getExplosivePlacedBy();
            float size = placer instanceof EntityCreeper ? (float)(((EntityCreeper)placer).getPowered() ? 6 : 3) : 4.0F;
            double distance = treeEntity.getDistance(expPos.x, expPos.y, expPos.z);
            if (distance / (double)size <= 1.0D && distance != 0.0D) {
               treeEntity.motionX += (treeEntity.posX - expPos.x) / distance;
               treeEntity.motionY += (treeEntity.posY - expPos.y) / distance;
               treeEntity.motionZ += (treeEntity.posZ - expPos.z) / distance;
            }
         }
      }

   }

   public final ITreePart.TreePartType getTreePartType() {
      return ITreePart.TreePartType.BRANCH;
   }

   public class BlockItemStack {
      public final ItemStack stack;
      public final BlockPos pos;

      public BlockItemStack(ItemStack stack, BlockPos pos) {
         this.stack = stack;
         this.pos = pos;
      }
   }

   public static enum EnumAxeDamage {
      VANILLA,
      THICKNESS,
      VOLUME;

      private EnumAxeDamage() {
      }
   }

   public static enum EnumDestroyMode {
      IGNORE,
      SLOPPY,
      SETRADIUS,
      HARVEST,
      ROT,
      OVERFLOW;

      private EnumDestroyMode() {
      }
   }
}
