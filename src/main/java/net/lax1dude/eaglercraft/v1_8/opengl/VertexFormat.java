/*
 * Copyright (c) 2022-2023 lax1dude. All Rights Reserved.
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

import static net.lax1dude.eaglercraft.v1_8.opengl.RealOpenGLEnums.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import net.minecraft.client.renderer.vertex.VertexFormatElement;

public enum VertexFormat {

	BLOCK(true, true, false, true),
	BLOCK_SHADERS(true, true, true, true),
	ITEM(true, true, true, false),
	OLDMODEL_POSITION_TEX_NORMAL(true, false, true, false),
	PARTICLE_POSITION_TEX_COLOR_LMAP(true, true, true, true),
	POSITION(false, false, false, false),
	POSITION_COLOR(false, true, false, false),
	POSITION_TEX(true, false, false, false),
	POSITION_NORMAL(false, false, true, false),
	POSITION_TEX_COLOR(true, true, false, false),
	POSITION_TEX_NORMAL(true, false, true, false),
	POSITION_TEX_LMAP_COLOR(true, true, false, true),
	POSITION_TEX_COLOR_NORMAL(true, true, true, false),
	FORGE_BAKED(VertexFormatElement.POSITION_3F, VertexFormatElement.COLOR_4UB, VertexFormatElement.TEX_2F,
			new VertexFormatElement(0, VertexFormatElement.EnumType.BYTE, VertexFormatElement.EnumUsage.PADDING, 4)),
	FORGE_BLOCK_NORMAL_4F(VertexFormatElement.POSITION_3F, VertexFormatElement.COLOR_4UB,
			VertexFormatElement.TEX_2F, VertexFormatElement.TEX_2S,
			new VertexFormatElement(0, VertexFormatElement.EnumType.FLOAT, VertexFormatElement.EnumUsage.NORMAL, 4)),
	FORGE_LIGHT_GATHERER(VertexFormatElement.TEX_2F, VertexFormatElement.TEX_2S);

	public static final int COMPONENT_POSITION_SIZE = 3;
	public static final int COMPONENT_POSITION_FORMAT = GL_FLOAT;
	public static final int COMPONENT_POSITION_STRIDE = 12;

	public static final int COMPONENT_TEX_SIZE = 2;
	public static final int COMPONENT_TEX_FORMAT = GL_FLOAT;
	public static final int COMPONENT_TEX_STRIDE = 8;

	public static final int COMPONENT_COLOR_SIZE = 4;
	public static final int COMPONENT_COLOR_FORMAT = GL_UNSIGNED_BYTE;
	public static final int COMPONENT_COLOR_STRIDE = 4;

	public static final int COMPONENT_NORMAL_SIZE = 4;
	public static final int COMPONENT_NORMAL_FORMAT = GL_BYTE;
	public static final int COMPONENT_NORMAL_STRIDE = 4;

	public static final int COMPONENT_LIGHTMAP_SIZE = 2;
	public static final int COMPONENT_LIGHTMAP_FORMAT = GL_UNSIGNED_SHORT;
	public static final int COMPONENT_LIGHTMAP_STRIDE = 4;

	public final boolean attribPositionEnabled;
	public final int attribPositionIndex;
	public final int attribPositionOffset;
	public final int attribPositionFormat;
	public final boolean attribPositionNormalized;
	public final int attribPositionSize;
	public final int attribPositionStride;

	public final boolean attribTextureEnabled;
	public final int attribTextureIndex;
	public final int attribTextureOffset;
	public final int attribTextureFormat;
	public final boolean attribTextureNormalized;
	public final int attribTextureSize;
	public final int attribTextureStride;

	public final boolean attribColorEnabled;
	public final int attribColorIndex;
	public final int attribColorOffset;
	public final int attribColorFormat;
	public final boolean attribColorNormalized;
	public final int attribColorSize;
	public final int attribColorStride;

	public final boolean attribNormalEnabled;
	public final int attribNormalIndex;
	public final int attribNormalOffset;
	public final int attribNormalFormat;
	public final boolean attribNormalNormalized;
	public final int attribNormalSize;
	public final int attribNormalStride;

	public final boolean attribLightmapEnabled;
	public final int attribLightmapIndex;
	public final int attribLightmapOffset;
	public final int attribLightmapFormat;
	public final boolean attribLightmapNormalized;
	public final int attribLightmapSize;
	public final int attribLightmapStride;

	public final int attribCount;
	public final int attribStride;
	
	public final int eaglercraftAttribBits;

	private final VertexFormatElement[] elements;
	private final int[] elementOffsets;
	private final List<VertexFormatElement> elementList;
	private final int[] uvOffsets = new int[] { -1, -1 };

	private VertexFormat(boolean texture, boolean color, boolean normal, boolean lightmap) {
		this(createElements(texture, color, normal, lightmap));
	}

	private static VertexFormatElement[] createElements(boolean texture, boolean color, boolean normal,
			boolean lightmap) {
		int cnt = 1 + (color ? 1 : 0) + (texture ? 1 : 0) + (normal ? 2 : 0) + (lightmap ? 1 : 0);
		VertexFormatElement[] ret = new VertexFormatElement[cnt];
		int i = 0;
		ret[i++] = VertexFormatElement.POSITION_3F;
		if(color) {
			ret[i++] = VertexFormatElement.COLOR_4UB;
		}
		if(texture) {
			ret[i++] = VertexFormatElement.TEX_2F;
		}
		if(normal) {
			ret[i++] = VertexFormatElement.NORMAL_3B;
			ret[i++] = VertexFormatElement.PADDING_1B;
		}
		if(lightmap) {
			ret[i++] = VertexFormatElement.TEX_2S;
		}
		return ret;
	}

	private VertexFormat(VertexFormatElement... formatElements) {
		int index = 0;
		int bytes = 0;
		int bitfield = 0;
		int positionIndex = -1;
		int positionOffset = -1;
		int positionFormat = -1;
		int positionSize = -1;
		int textureIndex = -1;
		int textureOffset = -1;
		int textureFormat = -1;
		int textureSize = -1;
		int colorIndex = -1;
		int colorOffset = -1;
		int colorFormat = -1;
		int colorSize = -1;
		int normalIndex = -1;
		int normalOffset = -1;
		int normalFormat = -1;
		int normalSize = -1;
		int lightmapIndex = -1;
		int lightmapOffset = -1;
		int lightmapFormat = -1;
		int lightmapSize = -1;
		elements = formatElements;
		elementOffsets = new int[elements.length];
		for(int i = 0; i < elements.length; ++i) {
			VertexFormatElement element = elements[i];
			elementOffsets[i] = bytes;
			switch(element.getUsage()) {
			case POSITION:
				positionIndex = index++;
				positionOffset = bytes;
				positionFormat = element.getType().getGlConstant();
				positionSize = element.getElementCount();
				break;
			case COLOR:
				colorIndex = index++;
				colorOffset = bytes;
				colorFormat = element.getType().getGlConstant();
				colorSize = element.getElementCount();
				bitfield |= EaglercraftGPU.ATTRIB_COLOR;
				break;
			case NORMAL:
				normalIndex = index++;
				normalOffset = bytes;
				normalFormat = element.getType().getGlConstant();
				normalSize = element.getElementCount();
				bitfield |= EaglercraftGPU.ATTRIB_NORMAL;
				break;
			case UV:
				if(element.getIndex() == 0) {
					textureIndex = index++;
					textureOffset = bytes;
					textureFormat = element.getType().getGlConstant();
					textureSize = element.getElementCount();
					bitfield |= EaglercraftGPU.ATTRIB_TEXTURE;
				}else if(element.getIndex() == 1) {
					lightmapIndex = index++;
					lightmapOffset = bytes;
					lightmapFormat = element.getType().getGlConstant();
					lightmapSize = element.getElementCount();
					bitfield |= EaglercraftGPU.ATTRIB_LIGHTMAP;
				}
				if(element.getIndex() < uvOffsets.length) {
					uvOffsets[element.getIndex()] = bytes;
				}
				break;
			default:
				break;
			}
			bytes += element.getSize();
		}
		attribPositionEnabled = positionIndex != -1;
		attribPositionIndex = positionIndex;
		attribPositionOffset = positionOffset;
		attribPositionFormat = positionFormat;
		attribPositionNormalized = false;
		attribPositionSize = positionSize;
		attribTextureEnabled = textureIndex != -1;
		attribTextureIndex = textureIndex;
		attribTextureOffset = textureOffset;
		attribTextureFormat = textureFormat;
		attribTextureNormalized = false;
		attribTextureSize = textureSize;
		attribColorEnabled = colorIndex != -1;
		attribColorIndex = colorIndex;
		attribColorOffset = colorOffset;
		attribColorFormat = colorFormat;
		attribColorNormalized = true;
		attribColorSize = colorSize;
		attribNormalEnabled = normalIndex != -1;
		attribNormalIndex = normalIndex;
		attribNormalOffset = normalOffset;
		attribNormalFormat = normalFormat;
		attribNormalNormalized = true;
		attribNormalSize = normalSize;
		attribLightmapEnabled = lightmapIndex != -1;
		attribLightmapIndex = lightmapIndex;
		attribLightmapOffset = lightmapOffset;
		attribLightmapFormat = lightmapFormat;
		attribLightmapNormalized = false;
		attribLightmapSize = lightmapSize;
		attribCount = index;
		attribStride = bytes;
		attribPositionStride = attribPositionEnabled ? bytes : -1;
		attribColorStride = attribColorEnabled ? bytes : -1;
		attribTextureStride = attribTextureEnabled ? bytes : -1;
		attribNormalStride = attribNormalEnabled ? bytes : -1;
		attribLightmapStride = attribLightmapEnabled ? bytes : -1;
		eaglercraftAttribBits = bitfield;
		elementList = Collections.unmodifiableList(Arrays.asList(elements));
	}

	public List<VertexFormatElement> getElements() {
		return elementList;
	}

	public int getElementCount() {
		return elements.length;
	}

	public VertexFormatElement getElement(int index) {
		return elements[index];
	}

	public int getOffset(int index) {
		return elementOffsets[index];
	}

	public int getNextOffset() {
		return attribStride;
	}

	public int getSize() {
		return attribStride;
	}

	public int getIntegerSize() {
		return attribStride >> 2;
	}

	public boolean hasNormal() {
		return attribNormalEnabled;
	}

	public boolean hasColor() {
		return attribColorEnabled;
	}

	public int getNormalOffset() {
		return attribNormalOffset;
	}

	public int getColorOffset() {
		return attribColorOffset;
	}

	public boolean hasUvOffset(int id) {
		return uvOffsets[id] != -1;
	}

	public int getUvOffsetById(int id) {
		return uvOffsets[id];
	}
	
}
