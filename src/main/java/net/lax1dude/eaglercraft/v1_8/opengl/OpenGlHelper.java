/*
 * Copyright (c) 2022 lax1dude. All Rights Reserved.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * 
 */

package net.lax1dude.eaglercraft.v1_8.opengl;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;

public class OpenGlHelper {

	public static final int defaultTexUnit = RealOpenGLEnums.GL_TEXTURE0;

	public static final int lightmapTexUnit = RealOpenGLEnums.GL_TEXTURE1;

	public static void setLightmapTextureCoords(int unit, float x, float y) {
		GlStateManager.setActiveTexture(lightmapTexUnit);
		GlStateManager.texCoords2D(x, y);
		GlStateManager.setActiveTexture(defaultTexUnit);
	}

	public static void renderDirections(int len) {
		GlStateManager.disableTexture2D();
		GlStateManager.depthMask(false);
		Tessellator tessellator = Tessellator.getInstance();
		WorldRenderer worldrenderer = tessellator.getBuffer();
		EaglercraftGPU.glLineWidth(4.0F);
		worldrenderer.begin(RealOpenGLEnums.GL_LINES, DefaultVertexFormats.POSITION_COLOR);
		worldrenderer.pos(0.0D, 0.0D, 0.0D).color(0, 0, 0, 255).endVertex();
		worldrenderer.pos((double) len, 0.0D, 0.0D).color(0, 0, 0, 255).endVertex();
		worldrenderer.pos(0.0D, 0.0D, 0.0D).color(0, 0, 0, 255).endVertex();
		worldrenderer.pos(0.0D, (double) len, 0.0D).color(0, 0, 0, 255).endVertex();
		worldrenderer.pos(0.0D, 0.0D, 0.0D).color(0, 0, 0, 255).endVertex();
		worldrenderer.pos(0.0D, 0.0D, (double) len).color(0, 0, 0, 255).endVertex();
		tessellator.draw();
		EaglercraftGPU.glLineWidth(2.0F);
		worldrenderer.begin(RealOpenGLEnums.GL_LINES, DefaultVertexFormats.POSITION_COLOR);
		worldrenderer.pos(0.0D, 0.0D, 0.0D).color(255, 0, 0, 255).endVertex();
		worldrenderer.pos((double) len, 0.0D, 0.0D).color(255, 0, 0, 255).endVertex();
		worldrenderer.pos(0.0D, 0.0D, 0.0D).color(0, 255, 0, 255).endVertex();
		worldrenderer.pos(0.0D, (double) len, 0.0D).color(0, 255, 0, 255).endVertex();
		worldrenderer.pos(0.0D, 0.0D, 0.0D).color(127, 127, 255, 255).endVertex();
		worldrenderer.pos(0.0D, 0.0D, (double) len).color(127, 127, 255, 255).endVertex();
		tessellator.draw();
		EaglercraftGPU.glLineWidth(1.0F);
		GlStateManager.depthMask(true);
		GlStateManager.enableTexture2D();
	}
	

}