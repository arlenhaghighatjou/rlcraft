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
