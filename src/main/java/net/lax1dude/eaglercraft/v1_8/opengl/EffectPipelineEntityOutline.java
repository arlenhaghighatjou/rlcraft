package net.lax1dude.eaglercraft.v1_8.opengl;

import static net.lax1dude.eaglercraft.v1_8.internal.PlatformOpenGL.*;
import static net.lax1dude.eaglercraft.v1_8.opengl.RealOpenGLEnums.*;

import net.lax1dude.eaglercraft.v1_8.EagRuntime;
import net.lax1dude.eaglercraft.v1_8.internal.IProgramGL;
import net.lax1dude.eaglercraft.v1_8.internal.IShaderGL;
import net.lax1dude.eaglercraft.v1_8.internal.IUniformGL;

public final class EffectPipelineEntityOutline {
	private static final GameOverlayFramebuffer mask = new GameOverlayFramebuffer();
	private static final GameOverlayFramebuffer swap = new GameOverlayFramebuffer(false);
	private static IProgramGL shader;
	private static IUniformGL texelUniform;
	private static IUniformGL blurUniform;
	private static int width;
	private static int height;

	private EffectPipelineEntityOutline() {
	}

	public static void initialize() {
		if (shader != null) {
			return;
		}
		String source = EagRuntime.getRequiredResourceString("/assets/eagler/glsl/entity_outline.fsh");
		IShaderGL fragment = _wglCreateShader(GL_FRAGMENT_SHADER);
		IProgramGL program = null;
		try {
			_wglShaderSource(fragment, GLSLHeader.getFragmentHeaderCompat(source,
					"precision mediump float;\nprecision mediump sampler2D;\n"));
			_wglCompileShader(fragment);
			if (_wglGetShaderi(fragment, GL_COMPILE_STATUS) != GL_TRUE) {
				throw new IllegalStateException(_wglGetShaderInfoLog(fragment));
			}
			program = _wglCreateProgram();
			_wglAttachShader(program, DrawUtils.vshLocal);
			_wglAttachShader(program, fragment);
			if (EaglercraftGPU.checkOpenGLESVersion() == 200) {
				VSHInputLayoutParser.applyLayout(program, DrawUtils.vshLocalLayout);
			}
			_wglLinkProgram(program);
			if (_wglGetProgrami(program, GL_LINK_STATUS) != GL_TRUE) {
				throw new IllegalStateException(_wglGetProgramInfoLog(program));
			}
			_wglDetachShader(program, DrawUtils.vshLocal);
			_wglDetachShader(program, fragment);
			EaglercraftGPU.bindGLShaderProgram(program);
			_wglUniform1i(_wglGetUniformLocation(program, "u_mask"), 0);
			texelUniform = _wglGetUniformLocation(program, "u_texel");
			blurUniform = _wglGetUniformLocation(program, "u_blurDir");
			shader = program;
		} finally {
			if (program != null && shader != program) {
				_wglDeleteProgram(program);
			}
			_wglDeleteShader(fragment);
		}
	}

	public static void capture(int width, int height, Runnable drawEntities) {
		initialize();
		EffectPipelineEntityOutline.width = width;
		EffectPipelineEntityOutline.height = height;
		int sceneColorMask = GlStateManager.colorMaskBits;
		boolean fog = GlStateManager.stateFog;
		boolean blending = GlStateManager.stateBlend;
		boolean depth = GlStateManager.stateDepthTest;
		boolean depthMask = GlStateManager.stateDepthMask;
		boolean alpha = GlStateManager.stateAlphaTest;
		GlStateManager.setActiveTexture(GL_TEXTURE0);
		GlStateManager.colorMask(true, true, true, true);
		try {
			mask.beginRender(width, height);
			GlStateManager.depthMask(true);
			GlStateManager.clearColor(0.0F, 0.0F, 0.0F, 0.0F);
			GlStateManager.clear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
			GlStateManager.disableFog();
			GlStateManager.disableBlend();
			GlStateManager.enableDepth();
			GlStateManager.depthFunc(GL_ALWAYS);
			drawEntities.run();
			GlStateManager.depthFunc(GL_LEQUAL);
			GlStateManager.disableDepth();
			GlStateManager.disableAlpha();
			GlStateManager.disableBlend();
			GlStateManager.depthMask(false);
			pass(mask, swap, 0.0F, 0.0F);
			pass(swap, mask, 1.0F, 0.0F);
			pass(mask, swap, 0.0F, 1.0F);
		} finally {
			EffectPipelineFXAA.bindSceneFramebuffer();
			GlStateManager.depthFunc(GL_LEQUAL);
			GlStateManager.depthMask(depthMask);
			if (depth) GlStateManager.enableDepth(); else GlStateManager.disableDepth();
			if (alpha) GlStateManager.enableAlpha(); else GlStateManager.disableAlpha();
			if (fog) GlStateManager.enableFog(); else GlStateManager.disableFog();
			if (blending) GlStateManager.enableBlend(); else GlStateManager.disableBlend();
			GlStateManager.colorMask((sceneColorMask & 1) != 0, (sceneColorMask & 2) != 0,
					(sceneColorMask & 4) != 0, (sceneColorMask & 8) != 0);
		}
	}

	private static void pass(GameOverlayFramebuffer source, GameOverlayFramebuffer target, float x, float y) {
		target.beginRender(width, height);
		EaglercraftGPU.bindGLShaderProgram(shader);
		GlStateManager.setActiveTexture(GL_TEXTURE0);
		GlStateManager.bindTexture(source.getTexture());
		_wglUniform2f(texelUniform, 1.0F / width, 1.0F / height);
		_wglUniform2f(blurUniform, x, y);
		DrawUtils.drawStandardQuad2D();
	}

	public static void composite() {
		if (swap.getTexture() < 0) {
			return;
		}
		GlStateManager.setActiveTexture(GL_TEXTURE0);
		GlStateManager.bindTexture(swap.getTexture());
		GlStateManager.disableAlpha();
		GlStateManager.disableDepth();
		GlStateManager.depthMask(false);
		GlStateManager.enableBlend();
		GlStateManager.tryBlendFuncSeparate(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA, GL_ZERO, GL_ONE);
		TextureCopyUtil.disableAlign();
		TextureCopyUtil.blitTexture();
		GlStateManager.disableBlend();
		GlStateManager.depthMask(true);
		GlStateManager.enableDepth();
		GlStateManager.enableAlpha();
	}

	static void destroy() {
		mask.destroy();
		swap.destroy();
		if (shader != null) {
			_wglDeleteProgram(shader);
			shader = null;
		}
	}
}
