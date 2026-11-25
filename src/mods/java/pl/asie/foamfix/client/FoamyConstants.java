package pl.asie.foamfix.client;

import net.minecraft.client.renderer.vertex.VertexFormatElement;
import net.minecraft.client.renderer.vertex.VertexFormatElement.EnumType;
import net.minecraft.client.renderer.vertex.VertexFormatElement.EnumUsage;

public final class FoamyConstants {
   public static final VertexFormatElement VLF_NORMAL = new VertexFormatElement(0, EnumType.FLOAT, EnumUsage.NORMAL, 4);

   public FoamyConstants() {
   }
}
