package net.minecraft.client.renderer.block.model;

import java.util.Arrays;
import net.lax1dude.eaglercraft.v1_8.minecraft.EaglerTextureAtlasSprite;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class BakedQuadRetextured extends BakedQuad
{
    private final EaglerTextureAtlasSprite texture;

    public BakedQuadRetextured(BakedQuad quad, EaglerTextureAtlasSprite textureIn)
    {
        super(Arrays.copyOf(quad.getVertexData(), quad.getVertexData().length), quad.tintIndex, quad.getFace(), quad.getSprite(), quad.applyDiffuseLighting, quad.format);
        this.texture = textureIn;
        this.remapQuad();
    }

    private void remapQuad()
    {
        for (int i = 0; i < 4; ++i)
        {
            int j = format.getIntegerSize() * i;
            int uvIndex = format.getUvOffsetById(0) / 4;
            this.vertexData[j + uvIndex] = Float.floatToRawIntBits(this.texture.getInterpolatedU((double)this.sprite.getSpriteU16(Float.intBitsToFloat(this.vertexData[j + uvIndex]))));
            this.vertexData[j + uvIndex + 1] = Float.floatToRawIntBits(this.texture.getInterpolatedV((double)this.sprite.getSpriteV16(Float.intBitsToFloat(this.vertexData[j + uvIndex + 1]))));
        }
    }

    @Override
    public EaglerTextureAtlasSprite getSprite()
    {
        return texture;
    }
}
