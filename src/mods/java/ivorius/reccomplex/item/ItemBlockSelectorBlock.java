package ivorius.reccomplex.item;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

public class ItemBlockSelectorBlock extends ItemBlockSelector {
   public ItemBlockSelectorBlock() {
   }

   @Nullable
   public BlockPos hoveredBlock(ItemStack stack, EntityLivingBase entity) {
      RayTraceResult rayTraceResult = entity.rayTrace(300.0D, 0.0F);
      return rayTraceResult != null ? rayTraceResult.getBlockPos() : null;
   }

   public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
      super.addInformation(stack, worldIn, tooltip, flagIn);
      tooltip.add("(Hold ctrl for secondary selection)");
   }
}
