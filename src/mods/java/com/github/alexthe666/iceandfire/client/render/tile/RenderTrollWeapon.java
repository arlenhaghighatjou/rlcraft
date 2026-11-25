package com.github.alexthe666.iceandfire.client.render.tile;

import com.github.alexthe666.iceandfire.client.model.ModelTrollWeapon;
import com.github.alexthe666.iceandfire.enums.EnumTroll;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import org.lwjgl.opengl.GL11;

public class RenderTrollWeapon extends TileEntitySpecialRenderer {
   private static final ModelBase MODEL = new ModelTrollWeapon();

   public RenderTrollWeapon() {
   }

   public void render(TileEntity entity, double x, double y, double z, float f, int f1, float alpha) {
      this.renderItem(EnumTroll.Weapon.AXE, x, y, z, f, f1, alpha);
   }

   public void renderItem(EnumTroll.Weapon weapon, double x, double y, double z, float f, int f1, float alpha) {
      GL11.glPushMatrix();
      GL11.glTranslatef((float)x + 0.5F, (float)y - 0.75F, (float)z + 0.5F);
      GL11.glPushMatrix();
      Minecraft.getMinecraft().getTextureManager().bindTexture(weapon.TEXTURE);
      GL11.glPushMatrix();
      MODEL.render((Entity)null, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F);
      GL11.glPopMatrix();
      GL11.glPopMatrix();
      GL11.glPopMatrix();
   }
}
