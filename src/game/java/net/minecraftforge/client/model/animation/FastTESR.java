/*
 * Minecraft Forge
 * Copyright (c) 2016-2020.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation version 2.1
 * of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package net.minecraftforge.client.model.animation;

import static net.lax1dude.eaglercraft.v1_8.opengl.RealOpenGLEnums.*;

import net.minecraft.client.Minecraft;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.lax1dude.eaglercraft.v1_8.opengl.WorldRenderer;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.tileentity.TileEntity;

public abstract class FastTESR<T extends TileEntity> extends TileEntitySpecialRenderer<T>
{
    @Override
    public final void render(T te, double x, double y, double z, float partialTicks, int destroyStage, float partial)
    {
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer buffer = tessellator.getBuffer();
        this.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
        RenderHelper.disableStandardItemLighting();
        GlStateManager.blendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        GlStateManager.enableBlend();
        GlStateManager.disableCull();

        if (Minecraft.isAmbientOcclusionEnabled())
        {
            GlStateManager.shadeModel(GL_SMOOTH);
        }
        else
        {
            GlStateManager.shadeModel(GL_FLAT);
        }

        buffer.begin(GL_QUADS, DefaultVertexFormats.BLOCK);

        renderTileEntityFast(te, x, y, z, partialTicks, destroyStage, partial, buffer);
        buffer.setTranslation(0, 0, 0);

        tessellator.draw();

        RenderHelper.enableStandardItemLighting();
    }

    @Override
    public abstract void renderTileEntityFast(T te, double x, double y, double z, float partialTicks, int destroyStage, float partial, WorldRenderer buffer);
}
