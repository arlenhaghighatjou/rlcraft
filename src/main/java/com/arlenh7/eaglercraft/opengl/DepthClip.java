package com.arlenh7.eaglercraft.opengl;

import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.lax1dude.eaglercraft.v1_8.opengl.RealOpenGLEnums;
import net.lax1dude.eaglercraft.v1_8.opengl.VertexFormat;
import net.lax1dude.eaglercraft.v1_8.opengl.WorldRenderer;
import net.lax1dude.eaglercraft.v1_8.opengl.WorldVertexBufferUploader;

public class DepthClip {

	public static void begin(int x, int y, int width, int height, int screenWidth, int screenHeight, double z, WorldRenderer buffer) {
		GlStateManager.clear(RealOpenGLEnums.GL_DEPTH_BUFFER_BIT);
		GlStateManager.enableDepth();
		GlStateManager.depthMask(true);
		GlStateManager.depthFunc(RealOpenGLEnums.GL_ALWAYS);
		GlStateManager.colorMask(false, false, false, false);
		GlStateManager.disableTexture2D();
		buffer.begin(RealOpenGLEnums.GL_QUADS, VertexFormat.POSITION);
		quad(buffer, 0, 0, screenWidth, y, z);
		quad(buffer, 0, y + height, screenWidth, screenHeight, z);
		quad(buffer, 0, y, x, y + height, z);
		quad(buffer, x + width, y, screenWidth, y + height, z);
		buffer.finishDrawing();
		WorldVertexBufferUploader.func_181679_a(buffer);
		GlStateManager.enableTexture2D();
		GlStateManager.colorMask(true, true, true, true);
		GlStateManager.depthFunc(RealOpenGLEnums.GL_LEQUAL);
		GlStateManager.depthMask(false);
	}

	public static void end() {
		GlStateManager.depthMask(true);
		GlStateManager.clear(RealOpenGLEnums.GL_DEPTH_BUFFER_BIT);
		GlStateManager.disableDepth();
	}

	private static void quad(WorldRenderer buffer, int left, int top, int right, int bottom, double z) {
		buffer.pos((double) left, (double) top, z).endVertex();
		buffer.pos((double) left, (double) bottom, z).endVertex();
		buffer.pos((double) right, (double) bottom, z).endVertex();
		buffer.pos((double) right, (double) top, z).endVertex();
	}
}
