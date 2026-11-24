package net.minecraft.client.renderer.block.model;

import net.lax1dude.eaglercraft.v1_8.minecraft.EaglerTextureAtlasSprite;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class BakedQuad implements net.minecraftforge.client.model.pipeline.IVertexProducer
{
    protected final int[] vertexData;
    protected final int tintIndex;
    protected final EnumFacing face;
    protected final EaglerTextureAtlasSprite sprite;
	private volatile net.optifine.model.QuadBounds bounds;

    /**
     * @deprecated Use constructor with the format argument.
     */
    @Deprecated
    public BakedQuad(int[] vertexDataIn, int tintIndexIn, EnumFacing faceIn, EaglerTextureAtlasSprite spriteIn)
    {
        this(vertexDataIn, tintIndexIn, faceIn, spriteIn, true, net.minecraft.client.renderer.vertex.DefaultVertexFormats.ITEM);
    }

    public BakedQuad(int[] vertexDataIn, int tintIndexIn, EnumFacing faceIn, EaglerTextureAtlasSprite spriteIn, boolean applyDiffuseLighting, net.lax1dude.eaglercraft.v1_8.opengl.VertexFormat format)
    {
        this.format = format;
        this.applyDiffuseLighting = applyDiffuseLighting;
        this.vertexData = vertexDataIn;
        this.tintIndex = tintIndexIn;
        this.face = faceIn;
        this.sprite = spriteIn;
    }

    public EaglerTextureAtlasSprite getSprite()
    {
        return this.sprite;
    }

    public int[] getVertexData()
    {
        return this.vertexData;
    }

    public boolean hasTintIndex()
    {
        return this.tintIndex != -1;
    }

    public int getTintIndex()
    {
        return this.tintIndex;
    }

    public EnumFacing getFace()
    {
        return this.face;
    }

	private net.optifine.model.QuadBounds getBounds() {
		net.optifine.model.QuadBounds current = this.bounds;
		if (current == null) {
			current = new net.optifine.model.QuadBounds(this.vertexData);
			this.bounds = current;
		}
		return current;
	}

	public boolean isFaceQuad() {
		return this.getBounds().isFaceQuad(this.getFace());
	}

	public boolean isFullQuad() {
		return this.getBounds().isFullQuad(this.getFace());
	}

	public float getMidX() {
		net.optifine.model.QuadBounds bounds = this.getBounds();
		return (bounds.getMinX() + bounds.getMaxX()) * 0.5F;
	}

	public float getMidZ() {
		net.optifine.model.QuadBounds bounds = this.getBounds();
		return (bounds.getMinZ() + bounds.getMaxZ()) * 0.5F;
	}

    protected final net.lax1dude.eaglercraft.v1_8.opengl.VertexFormat format;
    protected final boolean applyDiffuseLighting;

    @Override
    public void pipe(net.minecraftforge.client.model.pipeline.IVertexConsumer consumer)
    {
        net.minecraftforge.client.model.pipeline.LightUtil.putBakedQuad(consumer, this);
    }

    public net.lax1dude.eaglercraft.v1_8.opengl.VertexFormat getFormat()
    {
        return format;
    }

    public boolean shouldApplyDiffuseLighting()
    {
        return applyDiffuseLighting;
    }
}
