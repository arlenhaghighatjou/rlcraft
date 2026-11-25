package sereneseasons.core;

import com.google.common.base.Preconditions;
import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.IStateMapper;
import net.minecraft.client.renderer.block.statemap.StateMap.Builder;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import sereneseasons.api.ISSBlock;
import sereneseasons.util.inventory.CreativeTabSS;

public class ClientProxy extends CommonProxy {
   public ClientProxy() {
   }

   public void registerRenderers() {
   }

   public void registerItemVariantModel(Item item, String name, int metadata) {
      Preconditions.checkNotNull(item, "Cannot register models for null item " + name);
      Preconditions.checkArgument(item != Items.AIR, "Cannot register models for air (" + name + ")");
      ModelLoader.registerItemVariants(item, new ResourceLocation[]{new ResourceLocation("sereneseasons:" + name)});
      ModelLoader.setCustomModelResourceLocation(item, metadata, new ModelResourceLocation("sereneseasons:" + name, "inventory"));
   }

   public void registerBlockSided(Block block) {
      if (block instanceof ISSBlock) {
         ISSBlock bopBlock = (ISSBlock)block;
         IProperty[] nonRenderingProperties = bopBlock.getNonRenderingProperties();
         if (nonRenderingProperties != null) {
            IStateMapper custom_mapper = (new Builder()).ignore(nonRenderingProperties).build();
            ModelLoader.setCustomStateMapper(block, custom_mapper);
         }
      }

   }

   public void registerItemSided(Item item) {
      if (item.getHasSubtypes()) {
         NonNullList<ItemStack> subItems = NonNullList.create();
         item.getSubItems(CreativeTabSS.instance, subItems);

         for(ItemStack subItem : subItems) {
            String subItemName = item.getUnlocalizedName(subItem);
            subItemName = subItemName.substring(subItemName.indexOf(".") + 1);
            ModelLoader.registerItemVariants(item, new ResourceLocation[]{new ResourceLocation("sereneseasons", subItemName)});
            ModelLoader.setCustomModelResourceLocation(item, subItem.getMetadata(), new ModelResourceLocation("sereneseasons:" + subItemName, "inventory"));
         }
      } else {
         ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation("sereneseasons:" + item.delegate.name().getResourcePath(), "inventory"));
      }

   }

   private static <E extends Entity> void registerEntityRenderer(Class<E> entityClass, Class<? extends Render<E>> renderClass) {
      RenderingRegistry.registerEntityRenderingHandler(entityClass, new ClientProxy.EntityRenderFactory(renderClass));
   }

   private static class EntityRenderFactory<E extends Entity> implements IRenderFactory<E> {
      private Class<? extends Render<E>> renderClass;

      private EntityRenderFactory(Class<? extends Render<E>> renderClass) {
         this.renderClass = renderClass;
      }

      public Render<E> createRenderFor(RenderManager manager) {
         Render<E> renderer = null;

         try {
            renderer = (Render)this.renderClass.getConstructor(RenderManager.class).newInstance(manager);
         } catch (Exception var4) {
            var4.printStackTrace();
         }

         return renderer;
      }
   }
}
