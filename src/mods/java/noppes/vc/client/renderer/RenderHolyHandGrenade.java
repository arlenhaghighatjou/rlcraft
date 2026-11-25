package noppes.vc.client.renderer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import noppes.vc.EntityHolyHandGrenade;
import noppes.vc.VCWeapons;

@SideOnly(Side.CLIENT)
public class RenderHolyHandGrenade extends Render {
   ItemStack item = new ItemStack(VCWeapons.holy_hand_grenade);

   public RenderHolyHandGrenade() {
      super(Minecraft.getMinecraft().getRenderManager());
   }

   public void doRenderProjectile(EntityHolyHandGrenade par1EntityProjectile, double x, double y, double z, float entityYaw, float partialTicks) {
      Minecraft mc = Minecraft.getMinecraft();
      GlStateManager.pushMatrix();
      GlStateManager.translate((float)x, (float)y, (float)z);
      GlStateManager.enableRescaleNormal();
      GlStateManager.rotate(par1EntityProjectile.prevRotationYaw + (par1EntityProjectile.rotationYaw - par1EntityProjectile.prevRotationYaw) * partialTicks - 180.0F, 0.0F, 1.0F, 0.0F);
      GlStateManager.rotate(par1EntityProjectile.prevRotationPitch + (par1EntityProjectile.rotationPitch - par1EntityProjectile.prevRotationPitch) * partialTicks, 1.0F, 0.0F, 0.0F);
      GlStateManager.translate(0.0D, -0.125D, 0.0D);
      mc.getRenderItem().renderItem(this.item, TransformType.THIRD_PERSON_RIGHT_HAND);
      GlStateManager.disableRescaleNormal();
      GlStateManager.popMatrix();
      GlStateManager.enableLighting();
   }

   public void doRender(Entity par1Entity, double par2, double par4, double par6, float par8, float par9) {
      this.doRenderProjectile((EntityHolyHandGrenade)par1Entity, par2, par4, par6, par8, par9);
   }

   protected ResourceLocation getEntityTexture(Entity par1Entity) {
      return TextureMap.LOCATION_BLOCKS_TEXTURE;
   }
}
