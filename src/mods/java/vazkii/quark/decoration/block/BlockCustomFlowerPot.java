package vazkii.quark.decoration.block;

import javax.annotation.Nonnull;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFlowerPot;
import net.minecraft.block.SoundType;
import net.minecraft.block.BlockFlowerPot.EnumFlowerType;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.statemap.IStateMapper;
import net.minecraft.client.renderer.color.IBlockColor;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityFlowerPot;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ChunkCache;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk.EnumCreateEntityType;
import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.ItemHandlerHelper;
import vazkii.arl.interf.IBlockColorProvider;
import vazkii.arl.interf.IStateMapperProvider;
import vazkii.quark.decoration.client.state.FlowerPotStateMapper;
import vazkii.quark.decoration.feature.ColoredFlowerPots;

public class BlockCustomFlowerPot extends BlockFlowerPot implements IBlockColorProvider, IStateMapperProvider {
   public static final PropertyBool CUSTOM = PropertyBool.create("custom");
   public static final String TAG_TEXTURE_PATH = "texture_path";

   public BlockCustomFlowerPot() {
      this.setHardness(0.0F);
      this.setSoundType(SoundType.STONE);
      this.setDefaultState(this.blockState.getBaseState().withProperty(CONTENTS, EnumFlowerType.EMPTY).withProperty(LEGACY_DATA, Integer.valueOf(0)).withProperty(CUSTOM, Boolean.valueOf(false)));
   }

   @Nonnull
   protected ExtendedBlockState createBlockState() {
      return new ExtendedBlockState(this, new IProperty[]{CONTENTS, LEGACY_DATA, CUSTOM}, new IUnlistedProperty[]{TEXTURE});
   }

   public boolean onBlockActivated(World world, @Nonnull BlockPos pos, @Nonnull IBlockState state, EntityPlayer player, @Nonnull EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
      ItemStack stack = player.getHeldItem(hand);
      TileEntity te = world.getTileEntity(pos);
      if (!(te instanceof TileEntityFlowerPot)) {
         return false;
      } else {
         TileEntityFlowerPot flowerPot = (TileEntityFlowerPot)te;
         ItemStack flower = flowerPot.getFlowerItemStack();
         if (flower.isEmpty()) {
            if (!ColoredFlowerPots.isFlower(stack)) {
               return false;
            }

            flowerPot.setItemStack(stack);
            flowerPot.getTileData().removeTag("texture_path");
            player.addStat(StatList.FLOWER_POTTED);
            if (!player.capabilities.isCreativeMode) {
               stack.shrink(1);
            }
         } else {
            ItemHandlerHelper.giveItemToPlayer(player, flower, player.inventory.currentItem);
            flowerPot.setItemStack(ItemStack.EMPTY);
         }

         flowerPot.markDirty();
         world.notifyBlockUpdate(pos, state, state, 3);
         return true;
      }
   }

   @Nonnull
   public IBlockState getActualState(IBlockState state, IBlockAccess world, @Nonnull BlockPos pos) {
      state = super.getActualState(state, world, pos);
      if (state.getValue(CONTENTS) == EnumFlowerType.EMPTY) {
         TileEntity te = world instanceof ChunkCache ? ((ChunkCache)world).getTileEntity(pos, EnumCreateEntityType.CHECK) : world.getTileEntity(pos);
         if (te instanceof TileEntityFlowerPot && !((TileEntityFlowerPot)te).getFlowerItemStack().isEmpty()) {
            state = state.withProperty(CUSTOM, Boolean.valueOf(true));
         }
      }

      return state;
   }

   @Nonnull
   public IBlockState getExtendedState(@Nonnull IBlockState state, IBlockAccess world, BlockPos pos) {
      if (!((Boolean)state.getValue(CUSTOM)).booleanValue()) {
         return state;
      } else {
         TileEntity te = world.getTileEntity(pos);
         if (te instanceof TileEntityFlowerPot) {
            TileEntityFlowerPot flowerPot = (TileEntityFlowerPot)te;
            String texture = flowerPot.getTileData().getString("texture_path");
            if (texture.isEmpty()) {
               ItemStack stack = flowerPot.getFlowerItemStack();
               if (!stack.isEmpty()) {
                  Block block = Block.getBlockFromItem(stack.getItem());
                  if (block != Blocks.AIR) {
                     IBlockState blockState = block.getStateFromMeta(stack.getItem().getMetadata(stack));
                     texture = Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelShapes().getTexture(blockState).getIconName();
                     flowerPot.getTileData().setString("texture_path", texture);
                  }
               }
            }

            if (!texture.isEmpty()) {
               state = ((IExtendedBlockState)state).withProperty(TEXTURE, texture);
            }
         }

         return state;
      }
   }

   public boolean hasComparatorInputOverride(IBlockState state) {
      return ColoredFlowerPots.enableComparatorLogic;
   }

   public int getComparatorInputOverride(IBlockState blockState, World world, BlockPos pos) {
      if (!ColoredFlowerPots.enableComparatorLogic) {
         return 0;
      } else {
         TileEntity te = world.getTileEntity(pos);
         return te instanceof TileEntityFlowerPot ? this.getComparatorSignal(((TileEntityFlowerPot)te).getFlowerItemStack()) : 0;
      }
   }

   private int getComparatorSignal(ItemStack stack) {
      return stack.isEmpty() ? 0 : ColoredFlowerPots.getFlowerComparatorPower(stack);
   }

   public IItemColor getItemColor() {
      return (stack, i) -> {
         return -1;
      };
   }

   public IBlockColor getBlockColor() {
      return (state, world, pos, index) -> {
         if (world != null && pos != null) {
            TileEntity tileentity = world.getTileEntity(pos);
            if (tileentity instanceof TileEntityFlowerPot) {
               ItemStack stack = ((TileEntityFlowerPot)tileentity).getFlowerItemStack();
               return ColoredFlowerPots.getStackBlockColorsSafe(stack, world, pos, 0);
            }
         }

         return -1;
      };
   }

   @SideOnly(Side.CLIENT)
   public IStateMapper getStateMapper() {
      return FlowerPotStateMapper.INSTANCE;
   }
}
