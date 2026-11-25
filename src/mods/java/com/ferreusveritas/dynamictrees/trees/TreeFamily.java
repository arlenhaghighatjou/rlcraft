package com.ferreusveritas.dynamictrees.trees;

import com.ferreusveritas.dynamictrees.api.TreeHelper;
import com.ferreusveritas.dynamictrees.api.treedata.ILeavesProperties;
import com.ferreusveritas.dynamictrees.blocks.BlockBranch;
import com.ferreusveritas.dynamictrees.blocks.BlockBranchBasic;
import com.ferreusveritas.dynamictrees.blocks.BlockBranchThick;
import com.ferreusveritas.dynamictrees.blocks.BlockDynamicLeaves;
import com.ferreusveritas.dynamictrees.blocks.BlockSurfaceRoot;
import com.ferreusveritas.dynamictrees.blocks.LeavesProperties;
import com.ferreusveritas.dynamictrees.cells.CellMetadata;
import com.ferreusveritas.dynamictrees.entities.EntityFallingTree;
import com.ferreusveritas.dynamictrees.entities.animation.IAnimationHandler;
import com.ferreusveritas.dynamictrees.util.BranchDestructionData;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLeaves;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.registries.IForgeRegistry;
import org.apache.logging.log4j.LogManager;

public class TreeFamily {
   public static final TreeFamily NULLFAMILY = new TreeFamily() {
      public void setCommonSpecies(Species species) {
      }

      public Species getCommonSpecies() {
         return Species.NULLSPECIES;
      }

      public List<Block> getRegisterableBlocks(List<Block> blockList) {
         return blockList;
      }

      public List<Item> getRegisterableItems(List<Item> itemList) {
         return itemList;
      }

      public boolean onTreeActivated(World world, BlockPos hitPos, IBlockState state, EntityPlayer player, EnumHand hand, ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {
         return false;
      }

      public ItemStack getStick(int qty) {
         return ItemStack.EMPTY;
      }
   };
   private final ResourceLocation name;
   @Nonnull
   protected Species commonSpecies = Species.NULLSPECIES;
   private BlockBranch dynamicBranch;
   private IBlockState primitiveLog = Blocks.AIR.getDefaultState();
   private ItemStack primitiveLogItemStack = ItemStack.EMPTY;
   private final List<BlockBranch> validBranches = new LinkedList();
   protected boolean hasConiferVariants = false;
   private ItemStack stick;
   public boolean canSupportCocoa = false;
   @SideOnly(Side.CLIENT)
   public int woodColor;
   private final LinkedList<TreeFamily.ISpeciesLocationOverride> speciesLocationOverrides = new LinkedList();
   LinkedList<TreeFamily.IConnectable> vanillaConnectables = new LinkedList();

   public TreeFamily() {
      this.name = new ResourceLocation("dynamictrees", "null");
   }

   public TreeFamily(ResourceLocation name) {
      this.name = name;
      this.setDynamicBranch(this.createBranch());
      this.stick = new ItemStack(Items.STICK);
      this.createSpecies();
   }

   public void createSpecies() {
   }

   public void registerSpecies(IForgeRegistry<Species> speciesRegistry) {
      speciesRegistry.register(this.getCommonSpecies());
   }

   public void setCommonSpecies(@Nonnull Species species) {
      this.commonSpecies = species;
   }

   public Species getCommonSpecies() {
      return this.commonSpecies;
   }

   public Species getSpeciesForLocation(World world, BlockPos trunkPos) {
      for(TreeFamily.ISpeciesLocationOverride override : this.speciesLocationOverrides) {
         Species species = override.getSpeciesForLocation(world, trunkPos);
         if (species.isValid()) {
            return species;
         }
      }

      return this.getCommonSpecies();
   }

   public void addSpeciesLocationOverride(TreeFamily.ISpeciesLocationOverride override) {
      this.speciesLocationOverrides.add(override);
   }

   public boolean onTreeActivated(World world, BlockPos hitPos, IBlockState state, EntityPlayer player, EnumHand hand, ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {
      BlockPos rootPos = TreeHelper.findRootNode(world, hitPos);
      return rootPos != BlockPos.ORIGIN ? TreeHelper.getExactSpecies(world, hitPos).onTreeActivated(world, rootPos, hitPos, state, player, hand, heldItem, side, hitX, hitY, hitZ) : false;
   }

   public List<Block> getRegisterableBlocks(List<Block> blockList) {
      if (this.isThick()) {
         BlockBranchThick branch = (BlockBranchThick)this.getDynamicBranch();
         blockList.add(branch.getPairSide(false));
         blockList.add(branch.getPairSide(true));
      } else {
         blockList.add(this.getDynamicBranch());
      }

      return blockList;
   }

   public List<Item> getRegisterableItems(List<Item> itemList) {
      Block branch = this.getDynamicBranch();
      itemList.add((new ItemBlock(branch)).setRegistryName(branch.getRegistryName()));
      if (this.isThick()) {
         branch = ((BlockBranchThick)branch).getPairSide(true);
         itemList.add((new ItemBlock(branch)).setRegistryName(branch.getRegistryName()));
      }

      this.getCommonSpecies().getSeed().ifValid((s) -> {
         itemList.add(s);
      });
      return itemList;
   }

   public ResourceLocation getName() {
      return this.name;
   }

   public boolean isWood() {
      return true;
   }

   public BlockBranch createBranch() {
      String branchName = this.getName() + "branch";
      return (BlockBranch)(this.isThick() ? new BlockBranchThick(branchName) : new BlockBranchBasic(branchName));
   }

   protected TreeFamily setDynamicBranch(BlockBranch gBranch) {
      this.dynamicBranch = gBranch;
      this.dynamicBranch.setFamily(this);
      this.addValidBranches(this.dynamicBranch);
      return this;
   }

   public BlockBranch getDynamicBranch() {
      return this.dynamicBranch;
   }

   public boolean isThick() {
      return false;
   }

   @SideOnly(Side.CLIENT)
   public int getWoodColor() {
      return this.woodColor;
   }

   @SideOnly(Side.CLIENT)
   public int getRootColor(IBlockState state, IBlockAccess blockAccess, BlockPos pos) {
      return this.getWoodColor();
   }

   protected TreeFamily setStick(ItemStack itemStack) {
      this.stick = itemStack;
      return this;
   }

   public ItemStack getStick(int qty) {
      ItemStack stack = this.stick.copy();
      stack.setCount(MathHelper.clamp(qty, 0, 64));
      return stack;
   }

   protected TreeFamily setPrimitiveLog(IBlockState primLog) {
      return this.setPrimitiveLog(primLog, new ItemStack(Item.getItemFromBlock(primLog.getBlock()), 1, primLog.getBlock().damageDropped(primLog)));
   }

   protected TreeFamily setPrimitiveLog(IBlockState primLog, ItemStack primLogStack) {
      this.primitiveLog = primLog;
      this.primitiveLogItemStack = primLogStack;
      return this;
   }

   public IBlockState getPrimitiveLog() {
      return this.primitiveLog;
   }

   public ItemStack getPrimitiveLogItemStack(int qty) {
      ItemStack stack = this.primitiveLogItemStack.copy();
      stack.setCount(MathHelper.clamp(qty, 0, 64));
      return stack;
   }

   public int getRadiusForCellKit(IBlockAccess blockAccess, BlockPos pos, IBlockState blockState, EnumFacing dir, BlockBranch branch) {
      int radius = branch.getRadius(blockState);
      int meta = 0;
      if (this.hasConiferVariants && radius == 1 && blockAccess.getBlockState(pos.down()).getBlock() == branch) {
         meta = 1;
      }

      return CellMetadata.radiusAndMeta(radius, meta);
   }

   public float getPrimaryThickness() {
      return 1.0F;
   }

   public float getSecondaryThickness() {
      return 2.0F;
   }

   public void addValidBranches(BlockBranch... branches) {
      this.validBranches.addAll(Arrays.asList(branches));
   }

   public int getBranchBlockIndex(BlockBranch block) {
      int index = this.validBranches.indexOf(block);
      if (index < 0) {
         LogManager.getLogger().warn("Block {} not valid branch for {}.", block, this);
         return 0;
      } else {
         return index;
      }
   }

   @Nullable
   public BlockBranch getValidBranchBlock(int index) {
      return index < this.validBranches.size() ? (BlockBranch)this.validBranches.get(index) : null;
   }

   public int getMaxSignalDepth() {
      return 32;
   }

   public BlockSurfaceRoot getSurfaceRoots() {
      return null;
   }

   public IAnimationHandler selectAnimationHandler(EntityFallingTree fallingEntity) {
      return fallingEntity.defaultAnimationHandler();
   }

   public boolean isCompatibleDynamicLeaves(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos) {
      BlockDynamicLeaves leaves = TreeHelper.getLeaves(blockState);
      return leaves != null && this == leaves.getFamily(blockState, blockAccess, pos);
   }

   public void addConnectableVanillaLeaves(TreeFamily.IConnectable connectable) {
      this.vanillaConnectables.add(connectable);
   }

   public boolean isCompatibleVanillaLeaves(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos) {
      Block block = blockState.getBlock();
      if (!(block instanceof BlockDynamicLeaves) && block instanceof BlockLeaves) {
         for(TreeFamily.IConnectable connectable : this.vanillaConnectables) {
            if (connectable.isConnectable(blockState)) {
               return true;
            }
         }
      }

      return false;
   }

   public boolean isCompatibleGenericLeaves(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos) {
      return this.isCompatibleDynamicLeaves(blockState, blockAccess, pos) || this.isCompatibleVanillaLeaves(blockState, blockAccess, pos);
   }

   public HashMap<BlockPos, IBlockState> getFellingLeavesClusters(BranchDestructionData destructionData) {
      return null;
   }

   public ILeavesProperties getCommonLeaves() {
      return LeavesProperties.NULLPROPERTIES;
   }

   public String toString() {
      return this.getName().toString();
   }

   public interface IConnectable {
      boolean isConnectable(IBlockState var1);
   }

   public interface ISpeciesLocationOverride {
      Species getSpeciesForLocation(World var1, BlockPos var2);
   }
}
