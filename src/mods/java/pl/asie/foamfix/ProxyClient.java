package pl.asie.foamfix;

import com.google.common.collect.ImmutableList;
import java.net.URI;
import java.util.List;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.lax1dude.eaglercraft.v1_8.minecraft.EaglerTextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.ContextCapabilities;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GLContext;
import pl.asie.foamfix.client.FoamFixDynamicItemModels;
import pl.asie.foamfix.client.FoamFixModelRegistryDuplicateWipe;
import pl.asie.foamfix.client.deduplicator.FoamFixModelDeduplicate;
import pl.asie.foamfix.common.WorldNuller;
import pl.asie.foamfix.shared.FoamFixShared;

public class ProxyClient extends ProxyCommon {
   public static int bakingStage = 0;
   public static final IBakedModel DUMMY_MODEL = new IBakedModel() {
      private final ItemOverrideList itemOverrideList = ItemOverrideList.NONE;

      public List<BakedQuad> getQuads(@Nullable IBlockState state, @Nullable EnumFacing side, long rand) {
         return ImmutableList.of();
      }

      public boolean isAmbientOcclusion() {
         return false;
      }

      public boolean isGui3d() {
         return false;
      }

      public boolean isBuiltInRenderer() {
         return false;
      }

      public EaglerTextureAtlasSprite getParticleTexture() {
         return Minecraft.getMinecraft().getTextureMapBlocks().getTextureExtry(TextureMap.LOCATION_MISSING_TEXTURE.toString());
      }

      public ItemOverrideList getOverrides() {
         return this.itemOverrideList;
      }
   };

   public ProxyClient() {
   }

   @SubscribeEvent(
      priority = EventPriority.HIGHEST
   )
   public void onModelRegistry(ModelRegistryEvent event) {
      bakingStage = 0;
   }

   @SubscribeEvent(
      priority = EventPriority.LOW
   )
   public void onModelBake(ModelBakeEvent event) {
      bakingStage = 1;
      FoamFixModelDeduplicate.INSTANCE.onModelBake(event);
   }

   public void preInit() {
      super.preInit();
      if (FoamFixShared.isCoremod) {
         try {
            MinecraftForge.EVENT_BUS.register(Class.forName("pl.asie.foamfix.coremod.VertexLighterOverrideHandler").newInstance());
         } catch (Exception var2) {
            var2.printStackTrace();
         }
      }

      if (FoamFixShared.config.clDynamicItemModels) {
         FoamFixDynamicItemModels.register();
      }

      this.updateFasterAnimationFlag();
   }

   public void updateFasterAnimationFlag() {
      if (FoamFixShared.config.txFasterAnimation > 0) {
         ContextCapabilities caps = GLContext.getCapabilities();
         boolean copyImageSupported = caps.OpenGL43 || caps.GL_ARB_copy_image;
         if (!copyImageSupported) {
            if (FoamFixShared.config.txFasterAnimation == 2) {
               FoamFix.getLogger().warn("Fast animated textures require OpenGL 4.3 or ARB_copy_image extension, which were not detected. Using original slow path.");
            }

            FoamFix.shouldFasterAnimation = false;
         } else {
            String vendor = GL11.glGetString(7936);
            if (FoamFixShared.config.txFasterAnimation == 2) {
               FoamFix.getLogger().info("Using fast animated textures.");
               FoamFix.shouldFasterAnimation = true;
            } else {
               FoamFix.shouldFasterAnimation = false;
            }
         }
      }

   }

   public void init() {
      super.init();
      if (FoamFixShared.config.gbNotifyNonUnloadedWorlds) {
         WorldNuller.initClient();
      }

      if (FoamFixShared.config.clCleanRedundantModelRegistry) {
         MinecraftForge.EVENT_BUS.register(new FoamFixModelRegistryDuplicateWipe());
      }

   }

   public void postInit() {
      super.postInit();
   }

   public void refreshResources() {
      FMLClientHandler.instance().scheduleResourcesRefresh((type) -> {
         return true;
      });
   }

   public void openUrlLinux(URI url) {
      try {
         (new ProcessBuilder(new String[0])).command("xdg-open", url.toString()).start();
      } catch (Throwable var3) {
         FoamFix.getLogger().error("Couldn't open link!", var3);
      }

   }
}
