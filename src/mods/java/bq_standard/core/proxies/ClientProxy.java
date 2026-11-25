package bq_standard.core.proxies;

import betterquesting.api.api.ApiReference;
import betterquesting.api.api.QuestingAPI;
import betterquesting.api.client.importers.IImportRegistry;
import bq_standard.client.theme.BQSTextures;
import bq_standard.core.BQ_Standard;
import bq_standard.importers.AdvImporter;
import bq_standard.importers.NativeFileImporter;
import bq_standard.importers.ftbq.FTBQQuestImporter;
import bq_standard.importers.hqm.HQMBagImporter;
import bq_standard.importers.hqm.HQMQuestImporter;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ClientProxy extends CommonProxy {
   public ClientProxy() {
   }

   public boolean isClient() {
      return true;
   }

   public void registerHandlers() {
      super.registerHandlers();
   }

   public void registerExpansion() {
      super.registerExpansion();
      ((IImportRegistry)QuestingAPI.getAPI(ApiReference.IMPORT_REG)).registerImporter(NativeFileImporter.INSTANCE);
      ((IImportRegistry)QuestingAPI.getAPI(ApiReference.IMPORT_REG)).registerImporter(HQMQuestImporter.INSTANCE);
      ((IImportRegistry)QuestingAPI.getAPI(ApiReference.IMPORT_REG)).registerImporter(HQMBagImporter.INSTANCE);
      ((IImportRegistry)QuestingAPI.getAPI(ApiReference.IMPORT_REG)).registerImporter(FTBQQuestImporter.INSTANCE);
      ((IImportRegistry)QuestingAPI.getAPI(ApiReference.IMPORT_REG)).registerImporter(AdvImporter.INSTANCE);
      BQSTextures.registerTextures();
   }

   public void registerRenderers() {
      super.registerRenderers();
      this.registerItemModelSubtypes(BQ_Standard.lootChest, 0, 104, BQ_Standard.lootChest.getRegistryName().toString());
   }

   @SideOnly(Side.CLIENT)
   private void registerBlockModel(Block block) {
      this.registerBlockModel(block, 0, block.getRegistryName().toString());
   }

   @SideOnly(Side.CLIENT)
   private void registerBlockModel(Block block, int meta, String name) {
      Item item = Item.getItemFromBlock(block);
      ModelResourceLocation model = new ModelResourceLocation(name, "inventory");
      if (!name.equals(item.getRegistryName())) {
         ModelBakery.registerItemVariants(item, new ResourceLocation[]{model});
      }

      ModelLoader.setCustomModelResourceLocation(item, meta, model);
   }

   @SideOnly(Side.CLIENT)
   private void registerItemModelSubtypes(Item item, int metaStart, int metaEnd, String name) {
      if (metaStart > metaEnd) {
         int tmp = metaStart;
         metaStart = metaEnd;
         metaEnd = tmp;
      }

      for(int m = metaStart; m <= metaEnd; ++m) {
         this.registerItemModel(item, m, name);
      }

   }

   @SideOnly(Side.CLIENT)
   private void registerItemModel(Item item) {
      this.registerItemModel(item, 0, item.getRegistryName().toString());
   }

   @SideOnly(Side.CLIENT)
   private void registerItemModel(Item item, int meta, String name) {
      ModelResourceLocation model = new ModelResourceLocation(name, "inventory");
      if (!name.equals(item.getRegistryName().toString())) {
         ModelBakery.registerItemVariants(item, new ResourceLocation[]{model});
      }

      ModelLoader.setCustomModelResourceLocation(item, meta, model);
   }
}
