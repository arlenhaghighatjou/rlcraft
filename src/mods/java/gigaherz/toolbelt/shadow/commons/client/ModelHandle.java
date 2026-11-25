package gigaherz.toolbelt.shadow.commons.client;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import java.util.Map;
import java.util.Map.Entry;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.lax1dude.eaglercraft.v1_8.opengl.WorldRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.lax1dude.eaglercraft.v1_8.opengl.VertexFormat;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraft.crash.CrashReport;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ReportedException;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.client.model.pipeline.LightUtil;
import net.minecraftforge.common.model.IModelState;

public class ModelHandle {
   static Map<String, IBakedModel> loadedModels = Maps.newHashMap();
   private final Map<String, String> textureReplacements = Maps.newHashMap();
   private final ResourceLocation model;
   private final String key;
   private final VertexFormat vertexFormat;
   private final IModelState state;
   private final boolean uvLock;
   private static int reloadCount = 0;
   private int cacheCount = 0;
   private IBakedModel cacheCopy;
   private static boolean initialized = false;

   private ModelHandle(ResourceLocation model) {
      this.model = model;
      this.vertexFormat = DefaultVertexFormats.ITEM;
      this.state = null;
      this.uvLock = false;
      this.key = this.computeKey();
   }

   private ModelHandle(ModelHandle handle, String texChannel, String resloc) {
      this.model = handle.model;
      this.vertexFormat = handle.vertexFormat;
      this.state = handle.state;
      this.uvLock = handle.uvLock;
      this.textureReplacements.putAll(handle.textureReplacements);
      this.textureReplacements.put(texChannel, resloc);
      this.key = this.computeKey();
   }

   private ModelHandle(ModelHandle handle, VertexFormat fmt) {
      this.model = handle.model;
      this.vertexFormat = fmt;
      this.state = handle.state;
      this.uvLock = handle.uvLock;
      this.textureReplacements.putAll(handle.textureReplacements);
      this.key = this.computeKey();
   }

   private ModelHandle(ModelHandle handle, IModelState state) {
      this.model = handle.model;
      this.vertexFormat = handle.vertexFormat;
      this.state = state;
      this.uvLock = handle.uvLock;
      this.textureReplacements.putAll(handle.textureReplacements);
      this.key = this.computeKey();
   }

   private ModelHandle(ModelHandle handle, boolean uvLock) {
      this.model = handle.model;
      this.vertexFormat = handle.vertexFormat;
      this.state = handle.state;
      this.uvLock = uvLock;
      this.textureReplacements.putAll(handle.textureReplacements);
      this.key = this.computeKey();
   }

   private String computeKey() {
      StringBuilder b = new StringBuilder();
      b.append(this.model.toString());

      for(Entry<String, String> entry : this.textureReplacements.entrySet()) {
         b.append("//");
         b.append((String)entry.getKey());
         b.append("/");
         b.append((String)entry.getValue());
      }

      b.append("//VF:");
      b.append(this.vertexFormat.hashCode());
      b.append("//S:");
      b.append(this.state != null ? this.state.hashCode() : "n");
      b.append("//UVL:");
      b.append(this.uvLock);
      return b.toString();
   }

   public ModelHandle replace(String texChannel, String resloc) {
      return this.textureReplacements.containsKey(texChannel) && ((String)this.textureReplacements.get(texChannel)).equals(resloc) ? this : new ModelHandle(this, texChannel, resloc);
   }

   public ModelHandle vertexFormat(VertexFormat fmt) {
      return this.vertexFormat == fmt ? this : new ModelHandle(this, fmt);
   }

   public ModelHandle state(IModelState newState) {
      return this.state == newState ? this : new ModelHandle(this, newState);
   }

   public ModelHandle uvLock(boolean uvLock) {
      return this.uvLock == uvLock ? this : new ModelHandle(this, uvLock);
   }

   public ResourceLocation getModel() {
      return this.model;
   }

   public String getKey() {
      return this.key;
   }

   public Map<String, String> getTextureReplacements() {
      return this.textureReplacements;
   }

   public VertexFormat getVertexFormat() {
      return this.vertexFormat;
   }

   @Nullable
   public IModelState getState() {
      return this.state;
   }

   public boolean uvLocked() {
      return this.uvLock;
   }

   public IBakedModel get() {
      return this.cacheCount == reloadCount && this.cacheCopy != null ? this.cacheCopy : (this.cacheCopy = loadModel(this));
   }

   public void render() {
      renderModel(this.get(), this.getVertexFormat());
   }

   public void render(int color) {
      renderModel(this.get(), this.getVertexFormat(), color);
   }

   public static void init() {
      if (!initialized) {
         initialized = true;
         IResourceManager rm = Minecraft.getMinecraft().getResourceManager();
         if (rm instanceof IReloadableResourceManager) {
            ((IReloadableResourceManager)rm).registerReloadListener(new IResourceManagerReloadListener() {
               public void onResourceManagerReload(IResourceManager __) {
                  ModelHandle.loadedModels.clear();
                  ModelHandle.reloadCount++;
               }
            });
         }

      }
   }

   @Nonnull
   public static ModelHandle of(String model) {
      return new ModelHandle(new ResourceLocation(model));
   }

   @Nonnull
   public static ModelHandle of(ResourceLocation model) {
      return new ModelHandle(model);
   }

   private static void renderModel(IBakedModel model, VertexFormat fmt) {
      Tessellator tessellator = Tessellator.getInstance();
      WorldRenderer worldrenderer = tessellator.getBuffer();
      worldrenderer.begin(7, fmt);

      for(BakedQuad bakedquad : model.getQuads((IBlockState)null, (EnumFacing)null, 0L)) {
         worldrenderer.addVertexData(bakedquad.getVertexData());
      }

      tessellator.draw();
   }

   private static void renderModel(IBakedModel model, VertexFormat fmt, int color) {
      Tessellator tessellator = Tessellator.getInstance();
      WorldRenderer worldrenderer = tessellator.getBuffer();
      worldrenderer.begin(7, fmt);

      for(BakedQuad bakedquad : model.getQuads((IBlockState)null, (EnumFacing)null, 0L)) {
         LightUtil.renderQuadColor(worldrenderer, bakedquad, color);
      }

      tessellator.draw();
   }

   private static IBakedModel loadModel(ModelHandle handle) {
      IBakedModel model = (IBakedModel)loadedModels.get(handle.getKey());
      if (model != null) {
         return model;
      } else {
         try {
            IModel mod = ModelLoaderRegistry.getModel(handle.getModel());
            if (handle.getTextureReplacements().size() > 0) {
               mod = mod.retexture(ImmutableMap.copyOf(handle.getTextureReplacements()));
            }

            if (handle.uvLocked()) {
               mod = mod.uvlock(true);
            }

            IModelState state = handle.getState();
            if (state == null) {
               state = mod.getDefaultState();
            }

            model = mod.bake(state, handle.getVertexFormat(), ModelLoader.defaultTextureGetter());
            loadedModels.put(handle.getKey(), model);
            return model;
         } catch (Exception var4) {
            throw new ReportedException(new CrashReport("Error loading custom model " + handle.getModel(), var4));
         }
      }
   }
}
