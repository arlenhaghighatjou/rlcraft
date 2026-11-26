package com.lycanitesmobs.client;

import com.lycanitesmobs.CommonProxy;
import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.client.gui.buttons.MainTab;
import com.lycanitesmobs.client.gui.buttons.TabManager;
import com.lycanitesmobs.client.gui.overlays.BaseOverlay;
import com.lycanitesmobs.client.localisation.LanguageLoader;
import com.lycanitesmobs.client.localisation.LanguageManager;
import com.lycanitesmobs.client.model.EquipmentPartModelLoader;
import com.lycanitesmobs.client.model.projectile.AetherwaveModel;
import com.lycanitesmobs.client.model.projectile.ChaosOrbModel;
import com.lycanitesmobs.client.model.projectile.CrystalShardModel;
import com.lycanitesmobs.client.model.projectile.LightBallModel;
import com.lycanitesmobs.client.renderer.EquipmentPartRenderer;
import com.lycanitesmobs.client.renderer.EquipmentRenderer;
import com.lycanitesmobs.client.renderer.RenderRegister;
import com.lycanitesmobs.core.block.BlockFluidBase;
import com.lycanitesmobs.core.info.CreatureInfo;
import com.lycanitesmobs.core.info.ModInfo;
import com.lycanitesmobs.core.info.Subspecies;
import com.lycanitesmobs.core.item.ItemBase;
import com.lycanitesmobs.core.item.equipment.ItemEquipment;
import com.lycanitesmobs.core.item.equipment.ItemEquipmentPart;
import com.lycanitesmobs.core.tileentity.TileEntityEquipment;
import com.lycanitesmobs.core.tileentity.TileEntityEquipmentPart;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemAir;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ClientProxy extends CommonProxy {
   FontRenderer fontRenderer;
   public static IItemColor itemColor = (stack, tintIndex) -> {
      Item item = stack.getItem();
      if (item != null && item instanceof ItemBase) {
         ItemBase itemBase = (ItemBase)item;
         return itemBase.getColorFromItemstack(stack, tintIndex);
      } else {
         return 16777215;
      }
   };

   public ClientProxy() {
   }

   public void initLanguageManager() {
      LanguageManager.getInstance();
   }

   @SideOnly(Side.CLIENT)
   public FontRenderer getFontRenderer() {
      return Minecraft.getMinecraft().fontRenderer;
   }

   public void registerEvents() {
      (new KeyHandler(Minecraft.getMinecraft())).register(FMLCommonHandler.instance().bus());
      (new BaseOverlay(Minecraft.getMinecraft())).register(MinecraftForge.EVENT_BUS);
      (new ClientEventListener()).register(MinecraftForge.EVENT_BUS);
      IResourceManager resourceManager = Minecraft.getMinecraft().getResourceManager();
      if (resourceManager instanceof IReloadableResourceManager) {
         ((IReloadableResourceManager)resourceManager).registerReloadListener(LanguageLoader.getInstance());
      }

   }

   public void registerTextures() {
      ModInfo group = LycanitesMobs.modInfo;
      AssetManager.addTexture("GUIBeastiaryBackground", group, "textures/guis/beastiary/background.png");
      AssetManager.addTexture("GUIPetLevel", group, "textures/guis/beastiary/level.png");
      AssetManager.addTexture("GUIPetSpirit", group, "textures/guis/beastiary/spirit.png");
      AssetManager.addTexture("GUIPetSpiritEmpty", group, "textures/guis/beastiary/spirit_empty.png");
      AssetManager.addTexture("GUIPetSpiritUsed", group, "textures/guis/beastiary/spirit_used.png");
      AssetManager.addTexture("GUIPetSpiritFilling", group, "textures/guis/beastiary/spirit_filling.png");
      AssetManager.addTexture("GUIPetBarHealth", group, "textures/guis/beastiary/bar_health.png");
      AssetManager.addTexture("GUIPetBarRespawn", group, "textures/guis/beastiary/bar_respawn.png");
      AssetManager.addTexture("GUIBarExperience", group, "textures/guis/beastiary/bar_experience.png");
      AssetManager.addTexture("GUIPetBarEmpty", group, "textures/guis/beastiary/bar_empty.png");
      AssetManager.addTexture("GUIInventoryCreature", group, "textures/guis/inventory_creature.png");
      AssetManager.addTexture("GUIMinionLg", group, "textures/guis/minion_lg.png");
      AssetManager.addTexture("GUIEquipmentForge", group, "textures/guis/equipmentforge.png");
      TabManager.registerTab(new MainTab(0));
   }

   public void registerRenders(ModInfo modInfo) {
      AssetManager.addModel("lightball", new LightBallModel());
      AssetManager.addModel("crystalshard", new CrystalShardModel());
      AssetManager.addModel("aetherwave", new AetherwaveModel());
      AssetManager.addModel("chaosorb", new ChaosOrbModel());
      ModelLoaderRegistry.registerLoader(new EquipmentPartModelLoader());
      ClientRegistry.bindTileEntitySpecialRenderer(TileEntityEquipmentPart.class, new EquipmentPartRenderer());
      ClientRegistry.bindTileEntitySpecialRenderer(TileEntityEquipment.class, new EquipmentRenderer());
      RenderRegister renderRegister = new RenderRegister(modInfo);
      renderRegister.registerRenderFactories();
   }

   public void registerModels(ModInfo groupInfo) {
      AssetManager.registerModels();
   }

   public void loadCreatureModel(CreatureInfo creature, String modelClassName) throws ClassNotFoundException {
      creature.modelClass = LycanitesModels.forName(modelClassName);
   }

   public void loadSubspeciesModel(Subspecies subspecies, String modelClassName) throws ClassNotFoundException {
      subspecies.modelClass = LycanitesModels.forName(modelClassName);
   }

   public EntityPlayer getClientPlayer() {
      return Minecraft.getMinecraft().player;
   }

   public void addBlockRender(ModInfo group, Block block) {
      if (block instanceof BlockFluidBase) {
         BlockFluidBase blockFluid = (BlockFluidBase)block;
         Item item = Item.getItemFromBlock(block);
         ModelBakery.registerItemVariants(item, new ResourceLocation[0]);
         final ModelResourceLocation fluidLocation = new ModelResourceLocation(blockFluid.group.modid + ":fluid", blockFluid.getFluid().getName());
         ModelLoader.setCustomMeshDefinition(item, (itemStack) -> {
            return fluidLocation;
         });
         ModelLoader.setCustomStateMapper(block, new StateMapperBase() {
            protected ModelResourceLocation getModelResourceLocation(IBlockState state) {
               return fluidLocation;
            }
         });
      } else {
         this.addItemRender(group, Item.getItemFromBlock(block));
      }
   }

   public void addItemRender(ModInfo group, Item item) {
      if (group == null) {
         group = LycanitesMobs.modInfo;
      }

      if (item instanceof ItemEquipmentPart) {
         ForgeHooksClient.registerTESRItemStack(item, 0, TileEntityEquipmentPart.class);
      }

      if (item instanceof ItemEquipment) {
         ForgeHooksClient.registerTESRItemStack(item, 0, TileEntityEquipment.class);
      }

      if (item instanceof ItemBase) {
         ItemBase itemBase = (ItemBase)item;
         ModelLoader.setCustomModelResourceLocation(item, 0, itemBase.getModelResourceLocation());
      } else if (!(item instanceof ItemAir)) {
         ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation(item.getRegistryName(), "inventory"));
      }
   }

   public EntityPlayer getPlayer(MessageContext ctx) {
      return (EntityPlayer)(ctx.side == Side.CLIENT ? Minecraft.getMinecraft().player : ctx.getServerHandler().player);
   }
}
