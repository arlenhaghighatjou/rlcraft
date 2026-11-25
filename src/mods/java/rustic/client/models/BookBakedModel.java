package rustic.client.models;

import java.util.ArrayList;
import java.util.List;
import javax.vecmath.Matrix3f;
import javax.vecmath.Matrix4f;
import javax.vecmath.Vector3f;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.SimpleModelFontRenderer;
import rustic.common.items.ModItems;

public class BookBakedModel implements IBakedModel {
   private static final ResourceLocation font = new ResourceLocation("minecraft", "textures/font/ascii.png");
   private static final ResourceLocation font2 = new ResourceLocation("minecraft", "font/ascii");
   public static final ModelResourceLocation modelResourceLocation = new ModelResourceLocation(ModItems.BOOK.getRegistryName().toString());
   private final IBakedModel parent;
   private List<BakedQuad> textQuads;

   public BookBakedModel(IBakedModel parent) {
      this.parent = parent;
      this.textQuads = new ArrayList();
      this.initText();
   }

   private void initText() {
      Matrix4f m = new Matrix4f();
      m.m20 = 0.0078125F;
      m.m01 = m.m12 = -m.m20;
      m.m33 = 1.0F;
      Matrix3f rotation = new Matrix3f();
      m.getRotationScale(rotation);
      Matrix3f angleZ = new Matrix3f();
      angleZ.rotZ(-1.5708F);
      rotation.mul(rotation, angleZ);
      m.setRotationScale(rotation);
      m.setScale(1.0F * m.getScale());
      m.setTranslation(new Vector3f(0.25F, 0.2505F, 0.328125F));
      SimpleModelFontRenderer fontRenderer = new SimpleModelFontRenderer(Minecraft.getMinecraft().gameSettings, font, Minecraft.getMinecraft().getTextureManager(), false, m, DefaultVertexFormats.ITEM) {
         protected float renderUnicodeChar(char c, boolean italic) {
            return super.renderDefaultChar(126, italic);
         }
      };
      int maxLineWidth = 64;
      TextureAtlasSprite fontSprite = Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(font2.toString());
      fontRenderer.setSprite(fontSprite);
      fontRenderer.setFillBlanks(false);
      int yOffset = 2;
      String title = "Almanac";
      List<String> lines = fontRenderer.listFormattedStringToWidth(title, maxLineWidth);

      for(int line = 0; line < lines.size(); ++line) {
         int offset = (maxLineWidth - fontRenderer.getStringWidth((String)lines.get(line))) / 2;
         fontRenderer.drawString((String)lines.get(line), offset, yOffset, 4276545);
         yOffset += fontRenderer.FONT_HEIGHT;
      }

      this.textQuads = fontRenderer.build();
   }

   public List<BakedQuad> getQuads(IBlockState state, EnumFacing side, long rand) {
      List<BakedQuad> quads = new ArrayList();
      quads.addAll(this.parent.getQuads(state, side, rand));
      quads.addAll(this.textQuads);
      return quads;
   }

   public boolean isAmbientOcclusion() {
      return this.parent.isAmbientOcclusion();
   }

   public boolean isGui3d() {
      return this.parent.isGui3d();
   }

   public boolean isBuiltInRenderer() {
      return this.parent.isBuiltInRenderer();
   }

   public TextureAtlasSprite getParticleTexture() {
      return this.parent.getParticleTexture();
   }

   public ItemOverrideList getOverrides() {
      return this.parent.getOverrides();
   }

   public ItemCameraTransforms getItemCameraTransforms() {
      return this.parent.getItemCameraTransforms();
   }
}
