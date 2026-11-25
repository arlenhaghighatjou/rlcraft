package rustic.common.blocks.fluids;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import rustic.common.potions.PotionsRustic;

public class FluidBooze extends FluidDrinkable {
   public static final String QUALITY_NBT_KEY = "Quality";
   private float inebriationChance = 0.5F;

   public FluidBooze(String fluidName, ResourceLocation still, ResourceLocation flowing) {
      super(fluidName, still, flowing);
   }

   public FluidBooze setInebriationChance(float chance) {
      this.inebriationChance = chance;
      return this;
   }

   public float getInebriationChance() {
      return this.inebriationChance;
   }

   public void onDrank(World world, EntityPlayer player, ItemStack stack, FluidStack fluid) {
      float quality = this.getQuality(fluid);
      this.inebriate(world, player, quality);
      this.affectPlayer(world, player, quality);
   }

   protected void affectPlayer(World world, EntityPlayer player, float quality) {
   }

   public float getQuality(FluidStack fluid) {
      float quality = 0.0F;
      if (fluid.tag != null && fluid.tag.hasKey("Quality", 5)) {
         quality = fluid.tag.getFloat("Quality");
      }

      return Math.max(Math.min(quality, 1.0F), 0.0F);
   }

   protected void inebriate(World world, EntityPlayer player, float quality) {
      int duration = (int)(12000.0F * Math.max(1.0F - Math.abs(quality - 0.75F), 0.0F));
      float inebriationChanceMod = Math.max(Math.min(1.0F - Math.abs(0.67F * (quality - 0.75F)), 1.0F), 0.0F);
      PotionEffect tipsyEffect = player.getActivePotionEffect(PotionsRustic.TIPSY);
      if (world.rand.nextFloat() < this.inebriationChance * inebriationChanceMod) {
         if (tipsyEffect == null) {
            player.addPotionEffect(new PotionEffect(PotionsRustic.TIPSY, duration, 0, false, false));
         } else if (tipsyEffect.getAmplifier() < 3) {
            player.addPotionEffect(new PotionEffect(PotionsRustic.TIPSY, duration, tipsyEffect.getAmplifier() + 1, false, false));
         }
      }

   }
}
