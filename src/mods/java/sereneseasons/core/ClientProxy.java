package sereneseasons.core;

import com.google.common.base.Preconditions;
import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.IStateMapper;
import net.minecraft.client.renderer.block.statemap.StateMap.Builder;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelLoader;
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
}
