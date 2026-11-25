package knightminer.inspirations.tools.item;

import java.util.Locale;
import java.util.function.BooleanSupplier;
import javax.annotation.Nonnull;
import knightminer.inspirations.common.Config;
import knightminer.inspirations.tools.entity.EntityModArrow;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.item.ItemArrow;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;

public class ItemModArrow extends ItemArrow {
   public ItemModArrow() {
   }

   @Nonnull
   public String getUnlocalizedName(ItemStack stack) {
      return super.getUnlocalizedName(stack) + "." + ItemModArrow.ArrowType.fromMeta(stack.getMetadata()).getName();
   }

   public EntityArrow createArrow(World world, ItemStack stack, EntityLivingBase shooter) {
      return new EntityModArrow(world, shooter, stack.getMetadata());
   }

   public boolean isInfinite(ItemStack stack, ItemStack bow, EntityPlayer player) {
      return false;
   }

   public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
      if (this.isInCreativeTab(tab)) {
         for(ItemModArrow.ArrowType type : ItemModArrow.ArrowType.values()) {
            if (type.isEnabled()) {
               items.add(new ItemStack(this, 1, type.getMeta()));
            }
         }
      }

   }

   public static enum ArrowType {
      CHARGED(() -> {
         return (void)Config.enableChargedArrow;
      });

      private int meta = this.ordinal();
      private BooleanSupplier enabled;

      private ArrowType(BooleanSupplier enabled) {
         this.enabled = enabled;
      }

      public boolean isEnabled() {
         return this.enabled.getAsBoolean();
      }

      public int getMeta() {
         return this.meta;
      }

      public String getName() {
         return this.name().toLowerCase(Locale.US);
      }

      public static ItemModArrow.ArrowType fromMeta(int meta) {
         if (meta >= values().length) {
            meta = 0;
         }

         return values()[meta];
      }
   }
}
