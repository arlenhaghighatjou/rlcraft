package com.ferreusveritas.dynamictrees.blocks;

import com.ferreusveritas.dynamictrees.ModBlocks;
import com.ferreusveritas.dynamictrees.api.TreeRegistry;
import com.ferreusveritas.dynamictrees.api.cells.ICellKit;
import com.ferreusveritas.dynamictrees.api.treedata.ILeavesProperties;
import com.ferreusveritas.dynamictrees.cells.CellKits;
import com.ferreusveritas.dynamictrees.trees.TreeFamily;
import java.util.Random;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class LeavesProperties implements ILeavesProperties {
   public static final LeavesProperties NULLPROPERTIES = new LeavesProperties() {
      public ILeavesProperties setTree(TreeFamily tree) {
         return this;
      }

      public TreeFamily getTree() {
         return TreeFamily.NULLFAMILY;
      }

      public IBlockState getPrimitiveLeaves() {
         return Blocks.AIR.getDefaultState();
      }

      public ItemStack getPrimitiveLeavesItemStack() {
         return ItemStack.EMPTY;
      }

      public ILeavesProperties setDynamicLeavesState(IBlockState state) {
         return this;
      }

      public IBlockState getDynamicLeavesState() {
         return Blocks.AIR.getDefaultState();
      }

      public IBlockState getDynamicLeavesState(int hydro) {
         return Blocks.AIR.getDefaultState();
      }

      public ICellKit getCellKit() {
         return CellKits.NULLCELLKIT;
      }

      public int getFlammability() {
         return 0;
      }

      public int getFireSpreadSpeed() {
         return 0;
      }

      public int getSmotherLeavesMax() {
         return 0;
      }

      public int getLightRequirement() {
         return 15;
      }

      public boolean updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
         return false;
      }
   };
   protected IBlockState primitiveLeaves;
   protected ItemStack primitiveLeavesItemStack;
   protected ICellKit cellKit;
   protected TreeFamily tree;
   protected IBlockState[] dynamicLeavesBlockHydroStates;

   private LeavesProperties() {
      this.tree = TreeFamily.NULLFAMILY;
      this.dynamicLeavesBlockHydroStates = new IBlockState[5];
   }

   public LeavesProperties(IBlockState primitiveLeaves) {
      this(primitiveLeaves, new ItemStack(Item.getItemFromBlock(primitiveLeaves.getBlock()), 1, primitiveLeaves.getBlock().damageDropped(primitiveLeaves)));
   }

   public LeavesProperties(IBlockState primitiveLeaves, ICellKit cellKit) {
      this(primitiveLeaves, new ItemStack(Item.getItemFromBlock(primitiveLeaves.getBlock()), 1, primitiveLeaves.getBlock().damageDropped(primitiveLeaves)), cellKit);
   }

   public LeavesProperties(IBlockState primitiveLeaves, ItemStack primitiveLeavesItemStack) {
      this(primitiveLeaves, primitiveLeavesItemStack, TreeRegistry.findCellKit(new ResourceLocation("dynamictrees", "deciduous")));
   }

   public LeavesProperties(IBlockState primitiveLeaves, ItemStack primitiveLeavesItemStack, ICellKit cellKit) {
      this.tree = TreeFamily.NULLFAMILY;
      this.dynamicLeavesBlockHydroStates = new IBlockState[5];
      this.primitiveLeaves = primitiveLeaves != null ? primitiveLeaves : ModBlocks.blockStates.air;
      this.primitiveLeavesItemStack = primitiveLeavesItemStack != null ? primitiveLeavesItemStack : ItemStack.EMPTY;
      this.cellKit = cellKit;
   }

   public IBlockState getPrimitiveLeaves() {
      return this.primitiveLeaves;
   }

   public ItemStack getPrimitiveLeavesItemStack() {
      return this.primitiveLeavesItemStack;
   }

   public ILeavesProperties setDynamicLeavesState(IBlockState state) {
      this.dynamicLeavesBlockHydroStates[0] = Blocks.AIR.getDefaultState();

      for(int i = 1; i <= 4; ++i) {
         this.dynamicLeavesBlockHydroStates[i] = state.withProperty(BlockDynamicLeaves.HYDRO, Integer.valueOf(i));
      }

      return this;
   }

   public IBlockState getDynamicLeavesState() {
      return this.dynamicLeavesBlockHydroStates[4];
   }

   public IBlockState getDynamicLeavesState(int hydro) {
      return this.dynamicLeavesBlockHydroStates[MathHelper.clamp(hydro, 0, 4)];
   }

   public ILeavesProperties setTree(TreeFamily tree) {
      this.tree = tree;
      return this;
   }

   public TreeFamily getTree() {
      return this.tree;
   }

   public int getFlammability() {
      return 60;
   }

   public int getFireSpreadSpeed() {
      return 30;
   }

   public int getSmotherLeavesMax() {
      return 4;
   }

   public int getLightRequirement() {
      return 13;
   }

   public ICellKit getCellKit() {
      return this.cellKit;
   }

   @SideOnly(Side.CLIENT)
   public int foliageColorMultiplier(IBlockState state, IBlockAccess world, BlockPos pos) {
      return Minecraft.getMinecraft().getBlockColors().colorMultiplier(this.getPrimitiveLeaves(), world, pos, 0);
   }

   public boolean updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
      return true;
   }

   public boolean appearanceChangesWithHydro() {
      return false;
   }

   public int getRadiusForConnection(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, BlockBranch from, EnumFacing side, int fromRadius) {
      return fromRadius == 1 && from.getFamily().isCompatibleDynamicLeaves(blockAccess.getBlockState(pos), blockAccess, pos) ? 1 : 0;
   }
}
