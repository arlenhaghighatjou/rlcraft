package com.charles445.simpledifficulty.block;

import com.charles445.simpledifficulty.api.SDCapabilities;
import com.charles445.simpledifficulty.api.SDItems;
import com.charles445.simpledifficulty.api.item.IItemCanteen;
import com.charles445.simpledifficulty.api.thirst.ThirstEnum;
import com.charles445.simpledifficulty.api.thirst.ThirstUtil;
import com.charles445.simpledifficulty.config.ModConfig;
import com.charles445.simpledifficulty.util.SoundUtil;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockRainCollector extends Block {
   public static final PropertyInteger LEVEL = PropertyInteger.create("level", 0, 3);

   public BlockRainCollector() {
      super(Material.IRON, MapColor.STONE);
      this.setDefaultState(this.blockState.getBaseState().withProperty(LEVEL, Integer.valueOf(0)));
      this.setHardness(2.0F);
      this.setSoundType(SoundType.METAL);
      this.setTickRandomly(true);
   }

   public void randomTick(World world, BlockPos pos, IBlockState state, Random random) {
      if (world.rand.nextInt(Math.max(1, ModConfig.server.miscellaneous.rainCollectorFillChance)) == 0 && world.isRainingAt(pos.up())) {
         float f = world.getBiome(pos).getTemperature(pos);
         if (world.getBiomeProvider().getTemperatureAtHeight(f, pos.getY()) >= 0.15F) {
            IBlockState iblockstate = world.getBlockState(pos);
            if (((Integer)iblockstate.getValue(LEVEL)).intValue() < 3) {
               world.setBlockState(pos, iblockstate.cycleProperty(LEVEL), 2);
            }
         }
      }

   }

   public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
      ItemStack itemstack = player.getHeldItem(hand);
      if (itemstack.isEmpty()) {
         if (player.isSneaking()) {
            int amount = ((Integer)state.getValue(LEVEL)).intValue();
            if (amount > 0 && SDCapabilities.getThirstData(player).isThirsty()) {
               SoundUtil.commonPlayPlayerSound(player, SoundEvents.ENTITY_GENERIC_DRINK);
               if (!world.isRemote) {
                  this.setWaterLevel(world, pos, state, amount - 1);
                  ThirstUtil.takeDrink(player, ThirstEnum.PURIFIED);
               }
            }
         }

         return true;
      } else {
         int amount = ((Integer)state.getValue(LEVEL)).intValue();
         Item item = itemstack.getItem();
         if (item == Items.BUCKET) {
            if (amount > 0 && !world.isRemote) {
               if (!player.capabilities.isCreativeMode) {
                  itemstack.shrink(1);
                  if (itemstack.isEmpty()) {
                     player.setHeldItem(hand, ThirstUtil.createPurifiedWaterBucket());
                  } else if (!player.inventory.addItemStackToInventory(ThirstUtil.createPurifiedWaterBucket())) {
                     player.dropItem(ThirstUtil.createPurifiedWaterBucket(), false);
                  }
               }

               this.setWaterLevel(world, pos, state, amount - 1);
               SoundUtil.serverPlayBlockSound(world, pos, SoundEvents.ITEM_BUCKET_FILL);
            }

            return true;
         } else if (item == Items.GLASS_BOTTLE) {
            if (amount > 0 && !world.isRemote && !player.capabilities.isCreativeMode) {
               itemstack.shrink(1);
               if (itemstack.isEmpty()) {
                  player.setHeldItem(hand, new ItemStack(SDItems.purifiedWaterBottle));
               } else if (!player.inventory.addItemStackToInventory(new ItemStack(SDItems.purifiedWaterBottle))) {
                  player.dropItem(new ItemStack(SDItems.purifiedWaterBottle), false);
               } else if (player instanceof EntityPlayerMP) {
                  ((EntityPlayerMP)player).sendContainerToPlayer(player.inventoryContainer);
               }

               this.setWaterLevel(world, pos, state, amount - 1);
               SoundUtil.serverPlayBlockSound(world, pos, SoundEvents.ITEM_BOTTLE_FILL);
            }

            return true;
         } else if (item == SDItems.canteen) {
            if (amount > 0 && !world.isRemote && !player.capabilities.isCreativeMode) {
               IItemCanteen canteen = (IItemCanteen)item;
               canteen.getThirstEnum(itemstack);
               if (canteen.tryAddDose(itemstack, ThirstEnum.PURIFIED)) {
                  this.setWaterLevel(world, pos, state, amount - 1);
                  SoundUtil.serverPlayBlockSound(world, pos, SoundEvents.ITEM_BUCKET_FILL);
               }
            }

            return true;
         } else {
            return false;
         }
      }
   }

   public void setWaterLevel(World world, BlockPos pos, IBlockState state, int level) {
      world.setBlockState(pos, state.withProperty(LEVEL, Integer.valueOf(MathHelper.clamp(level, 0, 3))), 2);
      world.updateComparatorOutputLevel(pos, this);
   }

   public boolean hasComparatorInputOverride(IBlockState state) {
      return true;
   }

   public int getComparatorInputOverride(IBlockState blockState, World worldIn, BlockPos pos) {
      return ((Integer)blockState.getValue(LEVEL)).intValue();
   }

   public IBlockState getStateFromMeta(int meta) {
      return this.getDefaultState().withProperty(LEVEL, Integer.valueOf(meta));
   }

   public int getMetaFromState(IBlockState state) {
      return ((Integer)state.getValue(LEVEL)).intValue();
   }

   protected BlockStateContainer createBlockState() {
      return new BlockStateContainer(this, new IProperty[]{LEVEL});
   }

   public boolean isOpaqueCube(IBlockState state) {
      return false;
   }

   public boolean isFullCube(IBlockState state) {
      return false;
   }

   @SideOnly(Side.CLIENT)
   public BlockRenderLayer getBlockLayer() {
      return BlockRenderLayer.CUTOUT;
   }
}
