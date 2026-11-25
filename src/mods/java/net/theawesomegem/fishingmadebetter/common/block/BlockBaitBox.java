package net.theawesomegem.fishingmadebetter.common.block;

import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.theawesomegem.fishingmadebetter.common.block.tileentity.TileEntityBaitBox;
import net.theawesomegem.fishingmadebetter.common.registry.FMBCreativeTab;

public class BlockBaitBox extends BlockTileEntityBase<TileEntityBaitBox> {
   public BlockBaitBox() {
      super(Material.WOOD, "baitbox");
      this.setCreativeTab(FMBCreativeTab.instance);
      this.setHardness(2.0F);
      this.setSoundType(SoundType.WOOD);
   }

   @SideOnly(Side.CLIENT)
   public void addInformation(ItemStack stack, @Nullable World player, List<String> tooltip, ITooltipFlag advanced) {
      tooltip.add(TextFormatting.GRAY + I18n.format("tile.fishingmadebetter.baitbox.tooltip", new Object[0]) + TextFormatting.RESET);
   }

   public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
      if (!world.isRemote && hand.equals(EnumHand.MAIN_HAND)) {
         TileEntity tileEntity = world.getTileEntity(pos);
         if (tileEntity instanceof TileEntityBaitBox) {
            ((TileEntityBaitBox)tileEntity).handleRightClick(world, pos, state, player);
         }

         return true;
      } else {
         return true;
      }
   }

   public Item getItemDropped(IBlockState state, Random rand, int fortune) {
      return Item.getItemFromBlock(this);
   }

   public void breakBlock(World world, BlockPos pos, IBlockState state) {
      TileEntity tileEntity = world.getTileEntity(pos);
      if (tileEntity instanceof TileEntityBaitBox) {
         IItemHandler inventory = (IItemHandler)tileEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, (EnumFacing)null);

         for(int i = 0; i < inventory.getSlots(); ++i) {
            ItemStack stack = inventory.getStackInSlot(i);
            if (!stack.isEmpty() && !world.isRemote) {
               EntityItem entityIn = new EntityItem(world, (double)pos.getX(), (double)pos.getY(), (double)pos.getZ(), stack);
               entityIn.setDefaultPickupDelay();
               world.spawnEntity(entityIn);
            }
         }
      }

      super.breakBlock(world, pos, state);
   }

   public EnumBlockRenderType getRenderType(IBlockState state) {
      return EnumBlockRenderType.MODEL;
   }

   public Class<TileEntityBaitBox> getTileEntityClass() {
      return TileEntityBaitBox.class;
   }

   @Nullable
   public TileEntityBaitBox createTileEntity(World world, IBlockState state) {
      return new TileEntityBaitBox();
   }
}
