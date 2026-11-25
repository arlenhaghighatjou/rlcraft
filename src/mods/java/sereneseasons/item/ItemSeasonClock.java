package sereneseasons.item;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import sereneseasons.api.season.SeasonHelper;
import sereneseasons.config.SeasonsConfig;
import sereneseasons.season.SeasonTime;

public class ItemSeasonClock extends Item {
   public ItemSeasonClock() {
      this.addPropertyOverride(new ResourceLocation("time"), new IItemPropertyGetter() {
         @SideOnly(Side.CLIENT)
         double rotation;
         @SideOnly(Side.CLIENT)
         double rota;
         @SideOnly(Side.CLIENT)
         int ticks;

         @SideOnly(Side.CLIENT)
         public float apply(ItemStack stack, World world, EntityLivingBase entity) {
            Entity holder = (Entity)(entity != null ? entity : stack.getItemFrame());
            if (world == null && holder != null) {
               world = holder.world;
            }

            if (world == null) {
               return 0.0F;
            } else {
               double d0;
               if (SeasonsConfig.isDimensionWhitelisted(world.provider.getDimension())) {
                  int seasonCycleTicks = SeasonHelper.getSeasonState(world).getSeasonCycleTicks();
                  d0 = (double)((float)seasonCycleTicks / (float)SeasonTime.ZERO.getCycleDuration());
               } else {
                  d0 = Math.random();
               }

               d0 = this.actualFrame(world, d0);
               return MathHelper.positiveModulo((float)d0, 1.0F);
            }
         }

         @SideOnly(Side.CLIENT)
         private double actualFrame(World world, double frame) {
            if (world.getTotalWorldTime() != (long)this.ticks) {
               this.ticks = (int)world.getTotalWorldTime();
               double newFrame = frame - this.rotation;
               if (newFrame < -0.5D) {
                  ++newFrame;
               }

               this.rota += newFrame * 0.1D;
               this.rota *= 0.9D;
               this.rotation += this.rota;
            }

            return this.rotation;
         }
      });
   }
}
