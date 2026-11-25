package artifacts.client.model.layer;

import artifacts.client.model.ModelDrinkingHat;
import artifacts.common.init.ModItems;
import artifacts.common.util.RenderHelper;
import baubles.api.BaubleType;
import baubles.api.BaublesApi;
import javax.annotation.Nonnull;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;

public class LayerDrinkingHat extends LayerBauble {
   private static final ModelBase hat = new ModelDrinkingHat();

   public LayerDrinkingHat(RenderPlayer renderPlayer) {
      super(renderPlayer);
   }

   protected void renderLayer(@Nonnull EntityPlayer player, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
      if (BaublesApi.isBaubleEquipped(player, ModItems.DRINKING_HAT) != -1 && RenderHelper.shouldRenderInSlot(player, EntityEquipmentSlot.HEAD)) {
         ItemStack stack = BaublesApi.getBaublesHandler(player).getStackInSlot(BaubleType.HEAD.getValidSlots()[0]);
         if (stack.getItem() == ModItems.DRINKING_HAT && RenderHelper.shouldItemStackRender(player, stack)) {
            if (player.isSneaking()) {
               GlStateManager.translate(0.0F, 0.2F, 0.0F);
            }

            this.modelPlayer.bipedHead.postRender(scale);
            hat.render(player, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
         }
      }
   }
}
