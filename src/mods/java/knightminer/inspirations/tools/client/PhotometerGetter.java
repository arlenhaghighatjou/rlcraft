package knightminer.inspirations.tools.client;

import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.RayTraceResult.Type;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class PhotometerGetter implements IItemPropertyGetter {
   public PhotometerGetter() {
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

         BlockPos pos = null;
         if (entity == Minecraft.getMinecraft().player) {
            EntityPlayer player = Minecraft.getMinecraft().player;
            if (player.getHeldItemMainhand() == stack || player.getHeldItemOffhand() == stack) {
               RayTraceResult trace = Minecraft.getMinecraft().objectMouseOver;
               if (trace != null && trace.typeOfHit == Type.BLOCK) {
                  pos = trace.getBlockPos();
                  if (world.isBlockFullCube(pos)) {
                     pos = pos.offset(trace.sideHit);
                  }
               }
            }
         }

         if (pos == null) {
            pos = new BlockPos(entity);
         }

         return (float)world.getLightFromNeighborsFor(EnumSkyBlock.BLOCK, pos);
      }
   }
}
