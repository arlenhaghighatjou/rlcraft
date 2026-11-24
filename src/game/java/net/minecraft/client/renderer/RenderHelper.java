package net.minecraft.client.renderer;

import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.lax1dude.eaglercraft.v1_8.opengl.ext.deferred.DeferredStateManager;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderHelper
{
    private static final Vec3d LIGHT0_POS = (new Vec3d(0.20000000298023224D, 1.0D, -0.699999988079071D)).normalize();
    private static final Vec3d LIGHT1_POS = (new Vec3d(-0.20000000298023224D, 1.0D, 0.699999988079071D)).normalize();

    public static void disableStandardItemLighting()
    {
        if (!DeferredStateManager.isInDeferredPass())
        {
            GlStateManager.disableLighting();
            GlStateManager.disableMCLight(0);
            GlStateManager.disableMCLight(1);
            GlStateManager.disableColorMaterial();
        }
    }

    public static void enableStandardItemLighting()
    {
        if (!DeferredStateManager.isInDeferredPass())
        {
            GlStateManager.enableLighting();
            GlStateManager.enableMCLight(0, 0.6F, LIGHT0_POS.x, LIGHT0_POS.y, LIGHT0_POS.z, 0.0D);
            GlStateManager.enableMCLight(1, 0.6F, LIGHT1_POS.x, LIGHT1_POS.y, LIGHT1_POS.z, 0.0D);
            GlStateManager.setMCLightAmbient(0.4F, 0.4F, 0.4F);
            GlStateManager.enableColorMaterial();
        }
    }

    public static void enableGUIStandardItemLighting()
    {
        if (!DeferredStateManager.isInDeferredPass())
        {
            GlStateManager.pushMatrix();
            GlStateManager.rotate(-30.0F, 0.0F, 1.0F, 0.0F);
            GlStateManager.rotate(165.0F, 1.0F, 0.0F, 0.0F);
            enableStandardItemLighting();
            GlStateManager.popMatrix();
        }
    }
}
