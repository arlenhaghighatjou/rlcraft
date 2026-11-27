package knightminer.inspirations.library.client;

import net.lax1dude.eaglercraft.v1_8.HString;
import com.arlenh7.eaglercraft.awt.Color;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import javax.annotation.Nullable;
import knightminer.inspirations.Inspirations;
import knightminer.inspirations.library.InspirationsRegistry;
import knightminer.inspirations.library.ItemMetaKey;
import knightminer.inspirations.library.util.TextureBlockUtil;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.lax1dude.eaglercraft.v1_8.opengl.WorldRenderer;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.lax1dude.eaglercraft.v1_8.minecraft.EaglerTextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.text.WordUtils;
import slimeknights.mantle.client.ModelHelper;

@SideOnly(Side.CLIENT)
public final class ClientUtil {
   public static final String TAG_TEXTURE_PATH = "texture_path";
   private static final Minecraft mc = Minecraft.getMinecraft();
   private static Map<ItemMetaKey, Integer> colorCache = new HashMap();
   private static Set<Item> unsafe = new HashSet();
   private static final Map<String, String> NORMALIZED_NAMES = new HashMap();

   private ClientUtil() {
   }

   public static int getStackColor(ItemStack stack) {
      return ((Integer)colorCache.computeIfAbsent(new ItemMetaKey(stack), ClientUtil::getStackColor)).intValue();
   }

   private static Integer getStackColor(ItemMetaKey key) {
      IBakedModel model = mc.getRenderItem().getItemModelWithOverrides(key.makeItemStack(), (World)null, (EntityLivingBase)null);
      if (model == null) {
         return Integer.valueOf(-1);
      } else {
         EaglerTextureAtlasSprite sprite = model.getParticleTexture();
         if (sprite == null) {
            return Integer.valueOf(-1);
         } else {
            int[] pixels = sprite.getFrameTextureData(0)[0];
            float r = 0.0F;
            float g = 0.0F;
            float b = 0.0F;
            float count = 0.0F;
            float[] hsb = new float[3];

            for(int argb : pixels) {
               int ca = argb >> 24 & 255;
               int cr = argb >> 16 & 255;
               int cg = argb >> 8 & 255;
               int cb = argb & 255;
               if (ca > 127 && NumberUtils.max(cr, cg, cb) > 31) {
                  Color.RGBtoHSB(ca, cr, cg, hsb);
                  float weight = hsb[1];
                  r += (float)cr * weight;
                  g += (float)cg * weight;
                  b += (float)cb * weight;
                  count += weight;
               }
            }

            if (count > 0.0F) {
               r /= count;
               g /= count;
               b /= count;
            }

            return -16777216 | (int)r << 16 | (int)g << 8 | (int)b;
         }
      }
   }

   public static void onResourceReload(IResourceManager manager) {
      colorCache.clear();
   }

   public static EaglerTextureAtlasSprite getSprite(ResourceLocation location) {
      TextureMap textureMapBlocks = mc.getTextureMapBlocks();
      EaglerTextureAtlasSprite sprite = null;
      if (location != null) {
         sprite = textureMapBlocks.getTextureExtry(location.toString());
      }

      if (sprite == null) {
         sprite = textureMapBlocks.getMissingSprite();
      }

      return sprite;
   }

   public static void renderFilledSprite(EaglerTextureAtlasSprite sprite, int x, int y, int size, int filled) {
      double uMin = (double)sprite.getMinU();
      double uMax = (double)sprite.getMaxU();
      double vMin = (double)sprite.getMinV();
      double vMax = (double)sprite.getMaxV();
      uMax = uMax - (double)(16 - size) / 16.0D * (uMax - uMin);
      vMax = vMax - (double)(16 - filled) / 16.0D * (vMax - vMin);
      Tessellator tessellator = Tessellator.getInstance();
      WorldRenderer bufferBuilder = tessellator.getBuffer();
      bufferBuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
      bufferBuilder.pos((double)x, (double)(y + size), 100.0D).tex(uMin, vMax).endVertex();
      bufferBuilder.pos((double)(x + size), (double)(y + size), 100.0D).tex(uMax, vMax).endVertex();
      bufferBuilder.pos((double)(x + size), (double)(y + size - filled), 100.0D).tex(uMax, vMin).endVertex();
      bufferBuilder.pos((double)x, (double)(y + size - filled), 100.0D).tex(uMin, vMin).endVertex();
      tessellator.draw();
   }

   public static String getTexturePath(TileEntity te) {
      String texture = te.getTileData().getString("texture_path");
      if (texture.isEmpty()) {
         ItemStack stack = new ItemStack(te.getTileData().getCompoundTag("texture"));
         if (!stack.isEmpty()) {
            Block block = Block.getBlockFromItem(stack.getItem());
            texture = ModelHelper.getTextureFromBlock(block, stack.getItem().getMetadata(stack)).getIconName();
            te.getTileData().setString("texture_path", texture);
         }
      }

      return texture;
   }

   public static IBlockState writeTextureBlockState(IBlockAccess world, BlockPos pos, IBlockState state) {
      TileEntity te = world.getTileEntity(pos);
      if (te != null) {
         String texture = getTexturePath(te);
         if (!texture.isEmpty()) {
            state = ((IExtendedBlockState)state).withProperty(TextureBlockUtil.TEXTURE_PROP, texture);
         }
      }

      return state;
   }

   public static int getStackBlockColorsSafe(ItemStack stack, @Nullable IBlockAccess world, @Nullable BlockPos pos, int index) {
      if (stack.isEmpty()) {
         return -1;
      } else {
         Item item = stack.getItem();
         if (!unsafe.contains(item)) {
            try {
               return getStackBlockColors(stack, world, pos, index);
            } catch (Exception var6) {
               Inspirations.log.error(HString.format("Caught exception getting block colors for %s", item.getRegistryName()), var6);
               unsafe.add(item);
            }
         }

         return mc.getItemColors().colorMultiplier(stack, index);
      }
   }

   public static int getStackBlockColors(ItemStack stack, @Nullable IBlockAccess world, @Nullable BlockPos pos, int index) {
      if (!stack.isEmpty() && stack.getItem() instanceof ItemBlock) {
         ItemBlock item = (ItemBlock)stack.getItem();
         IBlockState iblockstate = item.getBlock().getStateFromMeta(item.getMetadata(stack));
         return mc.getBlockColors().colorMultiplier(iblockstate, world, pos, index);
      } else {
         return -1;
      }
   }

   public static void renderJEICauldronFluid(Minecraft mc, int x, int y, ResourceLocation location, float[] color, int level) {
      GlStateManager.enableBlend();
      mc.renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
      GlStateManager.color(color[0], color[1], color[2]);
      EaglerTextureAtlasSprite sprite = getSprite(location);
      if (level == 0) {
         renderFilledSprite(sprite, x, y, 16, 16);
      } else {
         int height = 10 * level / InspirationsRegistry.getCauldronMax();
         renderFilledSprite(sprite, x, y, 10, height);
      }

      GlStateManager.color(1.0F, 1.0F, 1.0F);
      GlStateManager.disableBlend();
   }

   public static String normalizeName(String name) {
      return (String)NORMALIZED_NAMES.computeIfAbsent(name, (s) -> {
         return WordUtils.capitalizeFully(name.replace('_', ' '));
      });
   }
}
