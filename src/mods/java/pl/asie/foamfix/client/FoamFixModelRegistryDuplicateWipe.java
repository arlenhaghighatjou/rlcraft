package pl.asie.foamfix.client;

import gnu.trove.map.hash.TIntObjectHashMap;
import java.lang.reflect.Field;
import java.util.Map;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockModelShapes;
import net.minecraft.client.renderer.ItemModelMesher;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelManager;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.util.registry.IRegistry;
import net.minecraftforge.client.ItemModelMesherForge;
import net.minecraftforge.client.event.TextureStitchEvent.Post;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import net.minecraftforge.registries.IRegistryDelegate;
import pl.asie.foamfix.FoamFix;
import pl.asie.foamfix.ProxyClient;

public class FoamFixModelRegistryDuplicateWipe {
   public FoamFixModelRegistryDuplicateWipe() {
   }

   @SubscribeEvent
   public void onTextureStitchPost(Post event) {
      ItemModelMesher imm = Minecraft.getMinecraft().getRenderItem().getItemModelMesher();
      BlockModelShapes bms = Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelShapes();
      ModelManager mgr = bms.getModelManager();
      Field f = ObfuscationReflectionHelper.findField(ModelManager.class, "modelRegistry");

      try {
         IRegistry<ModelResourceLocation, IBakedModel> registry = (IRegistry)f.get(mgr);
         FoamFix.getLogger().info("Clearing unnecessary model registry of size " + registry.getKeys().size() + ".");

         for(ModelResourceLocation l : registry.getKeys()) {
            registry.putObject(l, ProxyClient.DUMMY_MODEL);
         }
      } catch (Exception var11) {
         var11.printStackTrace();
      }

      f = ObfuscationReflectionHelper.findField(BlockModelShapes.class, "bakedModelStore");

      try {
         Map<IBlockState, IBakedModel> modelStore = (Map)f.get(bms);
         FoamFix.getLogger().info("Clearing unnecessary model store of size " + modelStore.size() + ".");
         modelStore.clear();
      } catch (Exception var10) {
         var10.printStackTrace();
      }

      if (imm instanceof ItemModelMesherForge) {
         f = ReflectionHelper.findField(ItemModelMesherForge.class, new String[]{"models"});

         try {
            Map<IRegistryDelegate<Item>, TIntObjectHashMap<IBakedModel>> modelStore = (Map)f.get(imm);
            FoamFix.getLogger().info("Clearing unnecessary item shapes cache of size " + modelStore.size() + ".");
            modelStore.clear();
         } catch (Exception var9) {
            var9.printStackTrace();
         }
      }

   }
}
