package ivorius.reccomplex.item;

import ivorius.reccomplex.RecurrentComplex;
import ivorius.reccomplex.network.PacketSyncItem;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagFloat;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.lang3.tuple.Pair;

public class ItemBlockSelectorFloating extends ItemBlockSelector implements ItemSyncableTags, ItemInputHandler {
   public static final float SCROLL_DISTANCE_SPEED = 0.004F;

   public ItemBlockSelectorFloating() {
   }

   public static BlockPos getHoveredBlock(EntityLivingBase entity, float selectionRange) {
      Vec3d look = entity.getLookVec();
      int blockX = MathHelper.floor(look.x * (double)selectionRange + entity.posX);
      int blockY = MathHelper.floor(look.y * (double)selectionRange + entity.posY + (double)entity.getEyeHeight());
      int blockZ = MathHelper.floor(look.z * (double)selectionRange + entity.posZ);
      return new BlockPos(blockX, blockY, blockZ);
   }

   @Nullable
   public BlockPos hoveredBlock(ItemStack stack, EntityLivingBase entity) {
      return getHoveredBlock(entity, this.getSelectionRange(stack));
   }

   public float getSelectionRange(ItemStack stack) {
      return stack.hasTagCompound() && stack.getTagCompound().hasKey("selectionRange", 5) ? stack.getTagCompound().getFloat("selectionRange") : 2.0F;
   }

   public void setSelectionRange(ItemStack stack, float range) {
      stack.setTagInfo("selectionRange", new NBTTagFloat(range));
   }

   public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
      super.addInformation(stack, worldIn, tooltip, flagIn);
      tooltip.add(String.format("Range: %.02f", this.getSelectionRange(stack)));
      tooltip.add("(Hold ctrl and scroll to modify range)");
      tooltip.add("(Hold ctrl for secondary selection)");
   }

   public List<Pair<String, Integer>> getSyncedNBTTags() {
      return Collections.singletonList(Pair.of("selectionRange", Integer.valueOf(5)));
   }

   @SideOnly(Side.CLIENT)
   public boolean onMouseInput(EntityPlayer player, ItemStack stack, int button, boolean buttonState, int dWheel) {
      if (modifierKeyDown() && dWheel != 0) {
         this.setSelectionRange(stack, MathHelper.clamp(this.getSelectionRange(stack) + (float)dWheel * 0.004F, 0.0F, 40.0F));
         RecurrentComplex.network.sendToServer(new PacketSyncItem(player.inventory.currentItem, stack));
         return true;
      } else {
         return false;
      }
   }
}
