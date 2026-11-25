package com.teamacronymcoders.base.proxies;

import com.teamacronymcoders.base.client.ClientHelper;
import com.teamacronymcoders.base.client.models.IHasModel;
import com.teamacronymcoders.base.client.models.handler.ModelHandler;
import com.teamacronymcoders.base.client.models.sided.ModelLoaderSidedBlock;
import com.teamacronymcoders.base.modulesystem.IModule;
import com.teamacronymcoders.base.modulesystem.ModuleHandler;
import com.teamacronymcoders.base.modulesystem.proxies.IModuleProxy;
import com.teamacronymcoders.base.registrysystem.pieces.RegistrySide;
import com.teamacronymcoders.base.renderer.entity.loader.EntityRendererLoader;
import com.teamacronymcoders.base.util.files.ResourceLoader;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Optional;
import java.util.function.Consumer;
import javax.annotation.Nonnull;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.client.resources.IResource;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.obj.OBJLoader;
import net.minecraftforge.fml.common.discovery.ASMDataTable;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.io.IOUtils;

@SideOnly(Side.CLIENT)
public class LibClientProxy extends LibCommonProxy {
   private static ResourceLoader resourceLoader;
   private ModelHandler modelHandler;

   public LibClientProxy() {
   }

   public void addOBJDomain() {
      OBJLoader.INSTANCE.addDomain(this.getMod().getID());
   }

   public void addSidedBlockDomain() {
      ModelLoaderSidedBlock.getInstance().addDomain(this.getMod().getID());
   }

   public void setAllItemModels(IHasModel model) {
      this.getModelHandler().registerModels(model);
   }

   public void registerFluidModel(Block fluidBlock, ResourceLocation resourceLocation) {
      Item fluidItem = Item.getItemFromBlock(fluidBlock);
      final ModelResourceLocation modelResourceLocation = new ModelResourceLocation(resourceLocation, "normal");
      ModelBakery.registerItemVariants(fluidItem, new ResourceLocation[0]);
      ModelLoader.setCustomMeshDefinition(fluidItem, (stack) -> {
         return modelResourceLocation;
      });
      ModelLoader.setCustomStateMapper(fluidBlock, new StateMapperBase() {
         @Nonnull
         protected ModelResourceLocation getModelResourceLocation(@Nonnull IBlockState state) {
            return modelResourceLocation;
         }
      });
   }

   public RegistrySide getRegistrySide() {
      return RegistrySide.CLIENT;
   }

   public IModuleProxy getModuleProxy(IModule module) {
      return this.getModuleProxy(module.getClientProxyPath());
   }

   public String getFileContents(ResourceLocation location) {
      location = new ResourceLocation(location.getResourceDomain(), "templates/" + location.getResourcePath() + ".json");
      IResource resource = ClientHelper.getResource(location);
      String fileContents = "";
      if (resource != null) {
         InputStream inputStream = resource.getInputStream();

         try {
            fileContents = IOUtils.toString(inputStream, Charset.defaultCharset());
         } catch (IOException var6) {
            this.getMod().getLogger().getLogger().error("Failed to get File: " + location + " Exception: " + var6);
         }
      }

      return fileContents;
   }

   public void createResourceLoader(String modid) {
      if (resourceLoader == null) {
         resourceLoader = new ResourceLoader();

         try {
            resourceLoader.setup();
         } catch (IllegalAccessException | NoSuchFieldException var3) {
            this.getMod().getLogger().getLogger().error(var3);
         }
      }

      resourceLoader.createImportantFolders(modid);
   }

   public void handleSounds() {
      Optional.ofNullable(resourceLoader).ifPresent((loader) -> {
         loader.handleSoundsJsonForMod(this.getMod());
      });
   }

   private ModelHandler getModelHandler() {
      if (this.modelHandler == null) {
         this.modelHandler = new ModelHandler(this.getMod());
      }

      return this.modelHandler;
   }

   public void loadEntityRenderers(ASMDataTable table, ModuleHandler moduleHandler) {
      EntityRendererLoader.loadRenderersFor(table, moduleHandler);
   }

   public World getWorld(MessageContext ctx) {
      return Minecraft.getMinecraft().world;
   }
}
