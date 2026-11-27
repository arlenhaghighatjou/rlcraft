package net.mcft.copy.backpacks;

import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import net.mcft.copy.backpacks.api.BackpackHelper;
import net.mcft.copy.backpacks.api.BackpackRegistry;
import net.mcft.copy.backpacks.api.IBackpack;
import net.mcft.copy.backpacks.block.entity.TileEntityBackpack;
import net.mcft.copy.backpacks.client.BakedModelDefaultTexture;
import net.mcft.copy.backpacks.client.KeyBindingHandler;
import net.mcft.copy.backpacks.client.RendererBackpack;
import net.mcft.copy.backpacks.misc.util.MiscUtils;
import net.mcft.copy.backpacks.misc.util.NbtUtils;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.color.IBlockColor;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.lax1dude.eaglercraft.v1_8.minecraft.EaglerTextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.lax1dude.eaglercraft.v1_8.opengl.VertexFormat;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EnumPlayerModelParts;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.event.RenderPlayerEvent.Post;
import net.minecraftforge.client.event.RenderPlayerEvent.Pre;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.client.model.pipeline.IVertexConsumer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ProxyClient extends ProxyCommon {
   public static IBakedModel MODEL_BACKPACK;
   public static IBakedModel MODEL_BACKPACK_TOP;
   public static IBakedModel MODEL_BACKPACK_STRAPS;
   public static IBakedModel MODEL_BACKPACK_ENCH;
   public static IBakedModel MODEL_BACKPACK_ENCH_TOP;
   public static AxisAlignedBB MODEL_BACKPACK_BOX;
   private static final Set<Class<? extends EntityLivingBase>> _hasLayerChecked = new HashSet();
   public static final IItemColor ITEM_COLOR = (stack, tintIndex) -> {
      return ((Integer)NbtUtils.get(stack, Integer.valueOf(10511680), "display", "color")).intValue();
   };
   public static final IBlockColor BLOCK_COLOR = (state, world, pos, tintIndex) -> {
      ItemStack stack = ItemStack.EMPTY;
      if (world != null && pos != null) {
         IBackpack backpack = BackpackHelper.getBackpack(world.getTileEntity(pos));
         if (backpack != null) {
            stack = backpack.getStack();
         }
      }

      return ITEM_COLOR.colorMultiplier(stack, tintIndex);
   };
   private static DataParameter<Byte> PLAYER_MODEL_FLAG = EntityPlayer.PLAYER_MODEL_FLAG;
   private boolean _disabledCape = false;

   public ProxyClient() {
   }

   public void preInit() {
      super.preInit();
      (new KeyBindingHandler()).register(MinecraftForge.EVENT_BUS);
   }

   public void init() {
      super.init();
      Minecraft mc = Minecraft.getMinecraft();
      mc.getBlockColors().registerBlockColorHandler(BLOCK_COLOR, new Block[]{MiscUtils.getBlockFromItem(BackpacksContent.BACKPACK)});
      mc.getItemColors().registerItemColorHandler(ITEM_COLOR, new Item[]{BackpacksContent.BACKPACK});
      ClientRegistry.bindTileEntitySpecialRenderer(TileEntityBackpack.class, new RendererBackpack.TileEntity());
      RenderManager manager = mc.getRenderManager();
      Map<String, RenderPlayer> skinMap = manager.getSkinMap();

      for(RenderPlayer renderer : skinMap.values()) {
         renderer.addLayer(new RendererBackpack.Layer(renderer));
      }

   }

   public void initBackpackLayers() {
      BackpackRegistry.getDefaultEntityEntries().stream().map(BackpackRegistry.BackpackEntityEntry::getEntityClass).filter(Objects::nonNull).forEach(ProxyClient::ensureHasBackpackLayer);
   }

   public static void ensureHasBackpackLayer(Class<? extends EntityLivingBase> entityClass) {
      if (_hasLayerChecked.add(entityClass)) {
         RenderManager manager = Minecraft.getMinecraft().getRenderManager();
         Render<?> render = manager.getEntityClassRenderObject(entityClass);
         if (render instanceof RenderLivingBase) {
            ((RenderLivingBase)render).addLayer(new RendererBackpack.Layer((RenderLivingBase)render));
         }
      }
   }

   @SubscribeEvent
   public void onModelBake(ModelBakeEvent event) {
      MODEL_BACKPACK = bakeBlockModel("wearablebackpacks:block/backpack");
      MODEL_BACKPACK_TOP = bakeBlockModel("wearablebackpacks:block/backpack_top");
      MODEL_BACKPACK_STRAPS = bakeBlockModel("wearablebackpacks:block/backpack_straps");
      MODEL_BACKPACK_ENCH = new BakedModelDefaultTexture(MODEL_BACKPACK);
      MODEL_BACKPACK_ENCH_TOP = new BakedModelDefaultTexture(MODEL_BACKPACK_TOP);
      MODEL_BACKPACK_BOX = ProxyClient.ModelAABBCalculator.calcFrom(MODEL_BACKPACK, MODEL_BACKPACK_TOP);
   }

   private static IBakedModel bakeBlockModel(String location) {
      IModel model = getModel(new ResourceLocation(location));
      return model.bake(model.getDefaultState(), DefaultVertexFormats.BLOCK, (loc) -> {
         return Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(loc.toString());
      });
   }

   private static IModel getModel(ResourceLocation location) {
      try {
         IModel model = ModelLoaderRegistry.getModel(location);
         if (model == null) {
            WearableBackpacks.LOG.error("Model " + location + " is missing! THIS WILL CAUSE A CRASH!");
         }

         return model;
      } catch (Exception var2) {
         var2.printStackTrace();
         return null;
      }
   }

   @SubscribeEvent
   public void onRegisterModels(ModelRegistryEvent event) {
      ModelLoader.setCustomModelResourceLocation(BackpacksContent.BACKPACK, 0, new ModelResourceLocation("wearablebackpacks:backpack", "inventory"));
   }

   private static void setWearing(EntityPlayer player, EnumPlayerModelParts part, boolean value) {
      byte current = ((Byte)player.getDataManager().get(PLAYER_MODEL_FLAG)).byteValue();
      if (value) {
         current = (byte)(current | part.getPartMask());
      } else {
         current = (byte)(current & ~part.getPartMask());
      }

      player.getDataManager().set(PLAYER_MODEL_FLAG, Byte.valueOf(current));
   }

   @SubscribeEvent(
      priority = EventPriority.LOWEST
   )
   public void onRenderPlayerPre(Pre event) {
      EntityPlayer player = event.getEntityPlayer();
      if (BackpackHelper.getBackpack(player) != null && player.isWearing(EnumPlayerModelParts.CAPE)) {
         setWearing(player, EnumPlayerModelParts.CAPE, false);
         this._disabledCape = true;
      }
   }

   @SubscribeEvent(
      priority = EventPriority.HIGHEST
   )
   public void onRenderPlayerPost(Post event) {
      if (this._disabledCape) {
         EntityPlayer player = event.getEntityPlayer();
         setWearing(player, EnumPlayerModelParts.CAPE, true);
         this._disabledCape = false;
      }
   }

   private static class ModelAABBCalculator implements IVertexConsumer {
      private double minX = 1.0D;
      private double minY = 1.0D;
      private double minZ = 1.0D;
      private double maxX = 0.0D;
      private double maxY = 0.0D;
      private double maxZ = 0.0D;

      private ModelAABBCalculator() {
      }

      public static AxisAlignedBB calcFrom(IBakedModel... models) {
         ProxyClient.ModelAABBCalculator calc = new ProxyClient.ModelAABBCalculator();

         for(IBakedModel model : models) {
            calc.put(model);
         }

         return calc.getAABB();
      }

      public void put(IBakedModel model) {
         for(BakedQuad quad : model.getQuads((IBlockState)null, (EnumFacing)null, 0L)) {
            quad.pipe(this);
         }

      }

      public AxisAlignedBB getAABB() {
         return new AxisAlignedBB(this.minX, this.minY, this.minZ, this.maxX, this.maxY, this.maxZ);
      }

      public VertexFormat getVertexFormat() {
         return DefaultVertexFormats.POSITION;
      }

      public void setQuadTint(int tint) {
      }

      public void setQuadOrientation(EnumFacing orientation) {
      }

      public void setApplyDiffuseLighting(boolean diffuse) {
      }

      public void setTexture(EaglerTextureAtlasSprite texture) {
      }

      public void put(int element, float... data) {
         if ((double)data[0] < this.minX) {
            this.minX = (double)data[0];
         }

         if ((double)data[0] > this.maxX) {
            this.maxX = (double)data[0];
         }

         if ((double)data[1] < this.minY) {
            this.minY = (double)data[1];
         }

         if ((double)data[1] > this.maxY) {
            this.maxY = (double)data[1];
         }

         if ((double)data[2] < this.minZ) {
            this.minZ = (double)data[2];
         }

         if ((double)data[2] > this.maxZ) {
            this.maxZ = (double)data[2];
         }

      }
   }
}
