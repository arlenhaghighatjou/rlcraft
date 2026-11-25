package ivorius.reccomplex.item;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import ivorius.ivtoolkit.blocks.BlockPositions;
import ivorius.reccomplex.RCConfig;
import ivorius.reccomplex.RecurrentComplex;
import ivorius.reccomplex.capability.SelectionOwner;
import ivorius.reccomplex.network.PacketItemEvent;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;

public class ItemBlockSelector extends Item implements ItemEventHandler {
   public ItemBlockSelector() {
   }

   @SideOnly(Side.CLIENT)
   public void sendClickToServer(ItemStack usedItem, World world, EntityPlayer player, BlockPos position) {
      ByteBuf buf = Unpooled.buffer();
      BlockPositions.maybeWriteToBuffer(position, buf);
      buf.writeBoolean(modifierKeyDown());
      RecurrentComplex.network.sendToServer(new PacketItemEvent(player.inventory.currentItem, buf, "select"));
   }

   @SideOnly(Side.CLIENT)
   public static boolean modifierKeyDown() {
      boolean modifier = false;

      for(int k : RCConfig.blockSelectorModifierKeys) {
         modifier |= Keyboard.isKeyDown(k);
      }

      return modifier;
   }

   public void onClientEvent(String context, ByteBuf payload, EntityPlayer sender, ItemStack stack, int itemSlot) {
      if ("select".equals(context)) {
         BlockPos coord = BlockPositions.maybeReadFromBuffer(payload);
         boolean secondary = payload.readBoolean();
         SelectionOwner selectionOwner = SelectionOwner.getOwner(sender, (EnumFacing)null);
         if (selectionOwner != null) {
            if (secondary) {
               selectionOwner.setSelectedPoint2(coord);
            } else {
               selectionOwner.setSelectedPoint1(coord);
            }
         }
      }

   }

   @Nonnull
   public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer player, EnumHand hand) {
      ItemStack stack = player.getHeldItem(hand);
      if (worldIn.isRemote) {
         BlockPos position = this.hoveredBlock(stack, player);
         this.sendClickToServer(stack, worldIn, player, position);
      }

      return new ActionResult(EnumActionResult.SUCCESS, stack);
   }

   @Nullable
   public BlockPos hoveredBlock(ItemStack stack, EntityLivingBase entity) {
      return null;
   }
}
