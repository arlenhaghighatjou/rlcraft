package org.orecruncher.dsurround.client.renderer;

import com.animania.addons.farm.common.entity.pigs.EntityAnimaniaPig;
import com.animania.addons.farm.common.entity.pigs.EntityPigletBase;
import com.animania.addons.farm.common.entity.sheep.EntityEweBase;
import com.animania.api.interfaces.IFoodEating;
import com.animania.api.interfaces.ISleeping;
import java.util.Set;
import javax.annotation.Nonnull;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.orecruncher.dsurround.ModBase;
import org.orecruncher.dsurround.ModOptions;
import org.orecruncher.dsurround.client.keyboard.KeyHandler;

@SideOnly(Side.CLIENT)
public final class AnimaniaBadge implements BadgeRenderLayer.IEntityBadgeProvider {
   private static final ItemStack WATER_BUCKET = new ItemStack(Items.WATER_BUCKET);
   private static final BadgeRenderLayer.IBadgeDisplayCheck BADGE_DISPLAY_CHECK = () -> {
      return KeyHandler.ANIMANIA_BADGES == null || KeyHandler.ANIMANIA_BADGES.isKeyDown() || KeyHandler.ANIMANIA_BADGES.getKeyCode() == 0;
   };

   public AnimaniaBadge() {
   }

   @Nonnull
   public ItemStack getStackToDisplay(@Nonnull EntityLivingBase e) {
      IFoodEating fe = (IFoodEating)e;
      if (!fe.getWatered()) {
         return WATER_BUCKET;
      } else {
         return !fe.getFed() ? this.getFoodItem(fe) : ItemStack.EMPTY;
      }
   }

   private ItemStack getFoodItem(IFoodEating fe) {
      Set<ItemStack> food = fe.getFoodItems();
      if (food.size() > 0) {
         ItemStack item = (ItemStack)food.iterator().next();
         return item.copy();
      } else {
         return ItemStack.EMPTY;
      }
   }

   public float adjustY(@Nonnull EntityLivingBase e) {
      if (e instanceof EntityEweBase) {
         return 1.0F;
      } else {
         return e instanceof EntityPigletBase ? -0.5F : 0.15F;
      }
   }

   public float scale(@Nonnull EntityLivingBase e) {
      if (e instanceof EntityEweBase) {
         return 1.5F;
      } else {
         return e instanceof EntityAnimaniaPig && !(e instanceof EntityPigletBase) ? 0.75F : 0.5F;
      }
   }

   public boolean show(@Nonnull EntityLivingBase e) {
      if (e instanceof ISleeping) {
         return !((ISleeping)e).getSleeping();
      } else {
         return true;
      }
   }

   public static void initialize() {
      if (ModOptions.speechbubbles.enableAnimaniaBadges) {
         BadgeRenderLayer layer = new BadgeRenderLayer(BADGE_DISPLAY_CHECK, new AnimaniaBadge());
         RenderManager rm = Minecraft.getMinecraft().getRenderManager();

         for(ResourceLocation r : EntityList.getEntityNameList()) {
            Class<? extends Entity> clazz = EntityList.getClass(r);
            if (clazz != null && IFoodEating.class.isAssignableFrom(clazz)) {
               Render<Entity> renderer = rm.getEntityClassRenderObject(clazz);
               if (renderer instanceof RenderLivingBase) {
                  ((RenderLivingBase)renderer).addLayer(layer);
                  ModBase.log().debug("Adding Animania badging to %s", new Object[]{renderer.getClass().getSimpleName()});
               }
            }
         }

      }
   }
}
