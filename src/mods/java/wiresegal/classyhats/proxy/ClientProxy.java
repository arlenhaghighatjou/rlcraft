package wiresegal.classyhats.proxy;

import com.google.common.base.Charsets;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import kotlin.Metadata;
import kotlin.TypeCastException;
import kotlin.jvm.internal.Intrinsics;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.resources.FolderResourcePack;
import net.minecraft.entity.EntityList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.NotNull;
import wiresegal.classyhats.ClassyHats;
import wiresegal.classyhats.ClassyHatsConfig;
import wiresegal.classyhats.block.BlockHatContainer;
import wiresegal.classyhats.client.keybind.KeyBindHandler;
import wiresegal.classyhats.client.render.EntityLayerRendererHat;
import wiresegal.classyhats.client.render.PlayerLayerRendererHat;
import wiresegal.classyhats.client.render.TileRendererHatStand;

@SideOnly(Side.CLIENT)
@Metadata(
   mv = {1, 1, 13},
   bv = {1, 0, 3},
   k = 1,
   d1 = {"\u0000,\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0011\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u0007\u0018\u00002\u00020\u0001:\u0001\rB\u0005\u00a2\u0006\u0002\u0010\u0002J\u0010\u0010\u0007\u001a\u00020\b2\u0006\u0010\t\u001a\u00020\nH\u0016J\u0010\u0010\u000b\u001a\u00020\b2\u0006\u0010\t\u001a\u00020\fH\u0016R\u0016\u0010\u0003\u001a\b\u0012\u0004\u0012\u00020\u00050\u0004X\u0082\u0004\u00a2\u0006\u0004\n\u0002\u0010\u0006\u00a8\u0006\u000e"},
   d2 = {"Lwiresegal/classyhats/proxy/ClientProxy;", "Lwiresegal/classyhats/proxy/CommonProxy;", "()V", "defaultResourcePackFields", "", "", "[Ljava/lang/String;", "onInit", "", "event", "Lnet/minecraftforge/fml/common/event/FMLInitializationEvent;", "onPreInit", "Lnet/minecraftforge/fml/common/event/FMLPreInitializationEvent;", "CustomFolderResourcePack", "classyhats"}
)
public final class ClientProxy extends CommonProxy {
   private final String[] defaultResourcePackFields = new String[]{"aD", "defaultResourcePacks", "defaultResourcePacks"};

   public void onPreInit(@NotNull FMLPreInitializationEvent event) {
      Intrinsics.checkParameterIsNotNull(event, "event");
      super.onPreInit(event);
      ClientRegistry.registerKeyBinding(KeyBindHandler.INSTANCE.getKEY());
      ((List)ReflectionHelper.getPrivateValue(Minecraft.class, Minecraft.getMinecraft(), (String[])Arrays.copyOf(this.defaultResourcePackFields, this.defaultResourcePackFields.length))).add(new ClientProxy.CustomFolderResourcePack(ClassyHatsConfig.INSTANCE.getRpl(), "classyhats_extra"));
   }

   public void onInit(@NotNull FMLInitializationEvent event) {
      Intrinsics.checkParameterIsNotNull(event, "event");
      super.onInit(event);
      ClientRegistry.bindTileEntitySpecialRenderer(BlockHatContainer.TileHatContainer.class, TileRendererHatStand.INSTANCE);
      Minecraft var10000 = Minecraft.getMinecraft();
      Intrinsics.checkExpressionValueIsNotNull(var10000, "Minecraft.getMinecraft()");
      RenderManager var12 = var10000.getRenderManager();
      Intrinsics.checkExpressionValueIsNotNull(var12, "Minecraft.getMinecraft().renderManager");
      Map skinMap = var12.getSkinMap();
      RenderPlayer render = (RenderPlayer)skinMap.get("default");
      if (render != null) {
         ModelRenderer var10003 = render.getMainModel().bipedHead;
         Intrinsics.checkExpressionValueIsNotNull(var10003, "render.mainModel.bipedHead");
         render.addLayer(new PlayerLayerRendererHat(var10003));
      }

      render = (RenderPlayer)skinMap.get("slim");
      if (render != null) {
         ModelRenderer var14 = render.getMainModel().bipedHead;
         Intrinsics.checkExpressionValueIsNotNull(var14, "render.mainModel.bipedHead");
         render.addLayer(new PlayerLayerRendererHat(var14));
      }

      Minecraft var13 = Minecraft.getMinecraft();
      Intrinsics.checkExpressionValueIsNotNull(var13, "Minecraft.getMinecraft()");
      Map map = var13.getRenderManager().entityRenderMap;

      for(ResourceLocation entity : EntityList.getEntityNameList()) {
         if (ClassyHatsConfig.INSTANCE.getNames().contains(entity.toString())) {
            Class clazz = EntityList.getClass(entity);
            Render entityRenderer = (Render)map.get(clazz);
            if (entityRenderer instanceof RenderLiving) {
               ModelBase main = ((RenderLiving)entityRenderer).getMainModel();
               ModelRenderer renderer = main instanceof ModelBiped ? ((ModelBiped)main).bipedHead : null;
               ((RenderLiving)entityRenderer).addLayer(new EntityLayerRendererHat(renderer));
            }
         }
      }

   }

   public ClientProxy() {
   }

   @Metadata(
      mv = {1, 1, 13},
      bv = {1, 0, 3},
      k = 1,
      d1 = {"\u0000 \n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\u0018\u00002\u00020\u0001B\u0015\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\u0002\u0010\u0006J\u0010\u0010\u0007\u001a\u00020\b2\u0006\u0010\t\u001a\u00020\u0005H\u0014J\b\u0010\n\u001a\u00020\u0005H\u0016R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u000b"},
      d2 = {"Lwiresegal/classyhats/proxy/ClientProxy$CustomFolderResourcePack;", "Lnet/minecraft/client/resources/FolderResourcePack;", "source", "Ljava/io/File;", "name", "", "(Ljava/io/File;Ljava/lang/String;)V", "getInputStreamByName", "Ljava/io/InputStream;", "resourceName", "getPackName", "classyhats"}
   )
   public static final class CustomFolderResourcePack extends FolderResourcePack {
      private final String name;

      @NotNull
      public String getPackName() {
         return "FMLFileResourcePack:" + this.name;
      }

      @NotNull
      protected InputStream getInputStreamByName(@NotNull String resourceName) throws IOException {
         Intrinsics.checkParameterIsNotNull(resourceName, "resourceName");

         InputStream var2;
         try {
            InputStream var12 = super.getInputStreamByName(resourceName);
            Intrinsics.checkExpressionValueIsNotNull(var12, "super.getInputStreamByName(resourceName)");
            var2 = var12;
         } catch (IOException var10) {
            if (!Intrinsics.areEqual("pack.mcmeta", resourceName)) {
               throw (Throwable)var10;
            }

            ClassyHats.Companion.getLOGGER().debug("Mod %s is missing a pack.mcmeta file, substituting a dummy one", this.name);
            String var4 = "{\n \"pack\": {\n   \"description\": \"dummy FML pack for " + this.name + "\",\n" + "   \"pack_format\": 2\n" + "}\n" + "}";
            Charset var10000 = Charsets.UTF_8;
            Intrinsics.checkExpressionValueIsNotNull(Charsets.UTF_8, "Charsets.UTF_8");
            Charset var5 = var10000;
            if (var4 == null) {
               throw new TypeCastException("null cannot be cast to non-null type java.lang.String");
            }

            byte[] var11 = var4.getBytes(var5);
            Intrinsics.checkExpressionValueIsNotNull(var11, "(this as java.lang.String).getBytes(charset)");
            byte[] var8 = var11;
            var2 = (InputStream)(new ByteArrayInputStream(var8));
         }

         return var2;
      }

      public CustomFolderResourcePack(@NotNull File source, @NotNull String name) {
         Intrinsics.checkParameterIsNotNull(source, "source");
         Intrinsics.checkParameterIsNotNull(name, "name");
         super(source);
         this.name = name;
      }
   }
}
