package rustic.common.blocks;

import java.util.ArrayList;
import java.util.List;
import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLeaves;
import net.minecraft.block.BlockPlanks.EnumType;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.IStateMapper;
import net.minecraft.client.renderer.block.statemap.StateMap.Builder;
import net.minecraft.client.renderer.color.IBlockColor;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.ColorizerFoliage;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeColorHelper;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import rustic.common.items.ModItems;
import rustic.core.ClientProxy;
import rustic.core.Rustic;

public class BlockLeavesRustic extends BlockLeaves implements IColoredBlock {
   public static final PropertyEnum VARIANT = PropertyEnum.create("variant", BlockPlanksRustic.EnumType.class);

   public BlockLeavesRustic() {
      this.setRegistryName("leaves");
      this.setUnlocalizedName("rustic.leaves");
      this.setCreativeTab(Rustic.farmingTab);
      ItemBlock item = new ItemBlock(this) {
         public String getUnlocalizedName(ItemStack stack) {
            IBlockState state = BlockLeavesRustic.this.getStateFromMeta(stack.getMetadata());
            return this.getUnlocalizedName() + "_" + state.getValue(BlockPlanksRustic.VARIANT);
         }

         public int getMetadata(int damage) {
            return damage;
         }
      };
      item.setHasSubtypes(true);
      item.setMaxDamage(0);
      item.setRegistryName(this.getRegistryName());
      GameRegistry.findRegistry(Block.class).register(this);
      GameRegistry.findRegistry(Item.class).register(item);
      this.setDefaultState(this.blockState.getBaseState().withProperty(VARIANT, BlockPlanksRustic.EnumType.OLIVE).withProperty(CHECK_DECAY, Boolean.valueOf(true)).withProperty(DECAYABLE, Boolean.valueOf(true)));
      Blocks.FIRE.setFireInfo(this, 30, 60);
   }

   protected void dropApple(World worldIn, BlockPos pos, IBlockState state, int chance) {
      if (worldIn.rand.nextInt(chance) == 0) {
         switch((BlockPlanksRustic.EnumType)state.getValue(VARIANT)) {
         case OLIVE:
            spawnAsEntity(worldIn, pos, new ItemStack(ModItems.OLIVES));
            break;
         case IRONWOOD:
            spawnAsEntity(worldIn, pos, new ItemStack(ModItems.IRONBERRIES));
         }
      }

   }

   public Item getItemDropped(IBlockState state, EaglercraftRandom rand, int fortune) {
      return Item.getItemFromBlock(ModBlocks.SAPLING);
   }

   public List<ItemStack> getDrops(IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
      List<ItemStack> ret = new ArrayList();
      EaglercraftRandom rand = world instanceof World ? ((World)world).rand : new EaglercraftRandom();
      int chance = this.getSaplingDropChance(state);
      if (fortune > 0) {
         chance -= 2 << fortune;
         if (chance < 10) {
            chance = 10;
         }
      }

      if (rand.nextInt(chance) == 0) {
         ItemStack drop = new ItemStack(this.getItemDropped(state, rand, fortune), 1, this.damageDropped(state));
         if (!drop.isEmpty()) {
            ret.add(drop);
         }
      }

      chance = 0;
      switch((BlockPlanksRustic.EnumType)state.getValue(VARIANT)) {
      case OLIVE:
         chance = 6;
         break;
      case IRONWOOD:
         chance = 16;
      }

      if (fortune > 0) {
         chance -= fortune;
         if (chance < 2) {
            chance = 2;
         }
      }

      this.captureDrops(true);
      if (world instanceof World) {
         this.dropApple((World)world, pos, state, chance);
      }

      ret.addAll(this.captureDrops(false));
      return ret;
   }

   @SideOnly(Side.CLIENT)
   public void initModel() {
      IStateMapper custom_mapper = (new Builder()).ignore(new IProperty[]{CHECK_DECAY, DECAYABLE}).build();
      ModelLoader.setCustomStateMapper(this, custom_mapper);

      for(int i = 0; i < BlockPlanksRustic.EnumType.values().length && i < 4; ++i) {
         ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), i, new ModelResourceLocation(this.getRegistryName() + "_" + BlockPlanksRustic.EnumType.byMetadata(i).getName(), "inventory"));
      }

      ClientProxy.addColoredBlock(this);
   }

   public List<ItemStack> onSheared(ItemStack item, IBlockAccess world, BlockPos pos, int fortune) {
      return NonNullList.withSize(1, new ItemStack(this, 1, ((BlockPlanksRustic.EnumType)world.getBlockState(pos).getValue(VARIANT)).getMetadata()));
   }

   public int damageDropped(IBlockState state) {
      return ((BlockPlanksRustic.EnumType)state.getValue(VARIANT)).getMetadata();
   }

   protected ItemStack getSilkTouchDrop(IBlockState state) {
      return new ItemStack(Item.getItemFromBlock(this), 1, ((BlockPlanksRustic.EnumType)state.getValue(VARIANT)).getMetadata());
   }

   public void harvestBlock(World worldIn, EntityPlayer player, BlockPos pos, IBlockState state, @Nullable TileEntity te, ItemStack stack) {
      if (!worldIn.isRemote && stack.getItem() == Items.SHEARS) {
         player.addStat(StatList.getBlockStats(this));
      } else {
         super.harvestBlock(worldIn, player, pos, state, te, stack);
      }

   }

   protected int getSaplingDropChance(IBlockState state) {
      return 20;
   }

   public void getSubBlocks(CreativeTabs tab, NonNullList<ItemStack> list) {
      for(BlockPlanksRustic.EnumType blockplanks$enumtype : BlockPlanksRustic.EnumType.values()) {
         if (blockplanks$enumtype.getMetadata() < 4) {
            list.add(new ItemStack(Item.getItemFromBlock(this), 1, blockplanks$enumtype.getMetadata()));
         }
      }

   }

   @SideOnly(Side.CLIENT)
   public BlockRenderLayer getBlockLayer() {
      return Blocks.LEAVES.getBlockLayer();
   }

   public boolean isOpaqueCube(IBlockState state) {
      return Blocks.LEAVES.isOpaqueCube(state);
   }

   @SideOnly(Side.CLIENT)
   public boolean shouldSideBeRendered(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side) {
      return Blocks.LEAVES.shouldSideBeRendered(state, world, pos, side);
   }

   public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player) {
      return new ItemStack(this, 1, ((BlockPlanksRustic.EnumType)state.getValue(VARIANT)).getMetadata());
   }

   public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
      return this.getStateFromMeta(meta).withProperty(CHECK_DECAY, Boolean.valueOf(false)).withProperty(DECAYABLE, Boolean.valueOf(false));
   }

   public IBlockState getStateFromMeta(int meta) {
      return this.getDefaultState().withProperty(VARIANT, BlockPlanksRustic.EnumType.byMetadata(meta & 3)).withProperty(DECAYABLE, Boolean.valueOf((meta & 4) == 0)).withProperty(CHECK_DECAY, Boolean.valueOf((meta & 8) > 0));
   }

   public int getMetaFromState(IBlockState state) {
      int i = 0;
      i = i | ((BlockPlanksRustic.EnumType)state.getValue(VARIANT)).getMetadata();
      if (!((Boolean)state.getValue(DECAYABLE)).booleanValue()) {
         i |= 4;
      }

      if (((Boolean)state.getValue(CHECK_DECAY)).booleanValue()) {
         i |= 8;
      }

      return i;
   }

   protected BlockStateContainer createBlockState() {
      return new BlockStateContainer(this, new IProperty[]{CHECK_DECAY, DECAYABLE, VARIANT});
   }

   @SideOnly(Side.CLIENT)
   public IBlockColor getBlockColor() {
      return new IBlockColor() {
         public int colorMultiplier(IBlockState state, IBlockAccess worldIn, BlockPos pos, int tintIndex) {
            return worldIn != null && pos != null ? BiomeColorHelper.getFoliageColorAtPos(worldIn, pos) : ColorizerFoliage.getFoliageColorBasic();
         }
      };
   }

   @SideOnly(Side.CLIENT)
   public IItemColor getItemColor() {
      return new IItemColor() {
         public int colorMultiplier(ItemStack stack, int tintIndex) {
            if (!(stack.getItem() instanceof ItemBlock)) {
               return 16777215;
            } else {
               IBlockState state = ((ItemBlock)stack.getItem()).getBlock().getStateFromMeta(stack.getMetadata());
               IBlockColor blockColor = ((IColoredBlock)state.getBlock()).getBlockColor();
               return blockColor == null ? 16777215 : blockColor.colorMultiplier(state, (IBlockAccess)null, (BlockPos)null, tintIndex);
            }
         }
      };
   }

   public EnumType getWoodType(int meta) {
      return null;
   }
}
