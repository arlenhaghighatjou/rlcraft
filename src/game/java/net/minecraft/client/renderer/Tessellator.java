package net.minecraft.client.renderer;

import net.lax1dude.eaglercraft.v1_8.opengl.WorldVertexBufferUploader;
import net.lax1dude.eaglercraft.v1_8.opengl.WorldRenderer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class Tessellator
{
    private final WorldRenderer buffer;
    private static final Tessellator INSTANCE = new Tessellator(2097152);

    public static Tessellator getInstance()
    {
        return INSTANCE;
    }

    public Tessellator(int bufferSize)
    {
        this.buffer = new WorldRenderer(bufferSize);
    }

    public void draw()
    {
        this.buffer.finishDrawing();
        WorldVertexBufferUploader.func_181679_a(this.buffer);
    }

    public void uploadDisplayList(int displayList)
    {
        this.buffer.finishDrawing();
        WorldVertexBufferUploader.uploadDisplayList(displayList, this.buffer);
    }

    public WorldRenderer getBuffer()
    {
        return this.buffer;
    }
}
