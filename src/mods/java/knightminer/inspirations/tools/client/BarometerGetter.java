package knightminer.inspirations.tools.client;

import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BarometerGetter implements IItemPropertyGetter {
   public BarometerGetter() {
   }

   @SideOnly(Side.CLIENT)
   public float apply(ItemStack stack, @Nullable World world, @Nullable EntityLivingBase entityIn) {
      Entity entity = (Entity)(entityIn != null ? entityIn : stack.getItemFrame());
      if (entity == null) {
         return 0.0F;
      } else {
         if (world == null) {
            if (entity.world == null) {
               return 0.0F;
            }

            world = entity.world;
         }

         if (entity.posY < 0.0D) {
            return 0.0F;
         } else {
            double height = entity.posY;
            int worldHeight = world.getHeight();
            return height > (double)worldHeight ? 1.0F : (float)Math.sqrt(height / (double)worldHeight);
         }
      }
   }
}
