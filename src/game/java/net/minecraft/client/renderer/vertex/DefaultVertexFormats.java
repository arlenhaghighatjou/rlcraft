package net.minecraft.client.renderer.vertex;

import net.lax1dude.eaglercraft.v1_8.opengl.VertexFormat;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class DefaultVertexFormats
{
    public static final VertexFormat BLOCK = VertexFormat.BLOCK;
    public static final VertexFormat ITEM = VertexFormat.ITEM;
    public static final VertexFormat OLDMODEL_POSITION_TEX_NORMAL = VertexFormat.OLDMODEL_POSITION_TEX_NORMAL;
    public static final VertexFormat PARTICLE_POSITION_TEX_COLOR_LMAP = VertexFormat.PARTICLE_POSITION_TEX_COLOR_LMAP;
    public static final VertexFormat POSITION = VertexFormat.POSITION;
    public static final VertexFormat POSITION_COLOR = VertexFormat.POSITION_COLOR;
    public static final VertexFormat POSITION_TEX = VertexFormat.POSITION_TEX;
    public static final VertexFormat POSITION_NORMAL = VertexFormat.POSITION_NORMAL;
    public static final VertexFormat POSITION_TEX_COLOR = VertexFormat.POSITION_TEX_COLOR;
    public static final VertexFormat POSITION_TEX_NORMAL = VertexFormat.POSITION_TEX_NORMAL;
    public static final VertexFormat POSITION_TEX_LMAP_COLOR = VertexFormat.POSITION_TEX_LMAP_COLOR;
    public static final VertexFormat POSITION_TEX_COLOR_NORMAL = VertexFormat.POSITION_TEX_COLOR_NORMAL;
    public static final VertexFormatElement POSITION_3F = VertexFormatElement.POSITION_3F;
    public static final VertexFormatElement COLOR_4UB = VertexFormatElement.COLOR_4UB;
    public static final VertexFormatElement TEX_2F = VertexFormatElement.TEX_2F;
    public static final VertexFormatElement TEX_2S = VertexFormatElement.TEX_2S;
    public static final VertexFormatElement NORMAL_3B = VertexFormatElement.NORMAL_3B;
    public static final VertexFormatElement PADDING_1B = VertexFormatElement.PADDING_1B;
}
