package vazkii.quark.world.client.render;

import com.google.common.base.Predicate;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vazkii.quark.base.client.ClientReflectiveAccessor;
import vazkii.quark.world.base.ChainHandler;

@SideOnly(Side.CLIENT)
public class ChainRenderer {
   private static final TIntObjectMap<Entity> RENDER_MAP = new TIntObjectHashMap();

   public ChainRenderer() {
   }

   public static void drawChainSegment(double x, double y, double z, BufferBuilder buf, double offsetX, double offsetY, double offsetZ, double xOff, double zOff, float baseR, float baseG, float baseB, double height) {
      buf.begin(5, DefaultVertexFormats.POSITION_COLOR);
      double centroid = xOff + zOff / 2.0D;

      for(int seg = 0; seg <= 24; ++seg) {
         float r = baseR;
         float g = baseG;
         float b = baseB;
         if (seg % 2 == 0) {
            r = baseR * 0.7F;
            g = baseG * 0.7F;
            b = baseB * 0.7F;
         }

         float amount = (float)seg / 24.0F;
         buf.pos(x + offsetX * (double)amount - centroid, y + offsetY * (double)(amount * amount + amount) * 0.5D + (double)((24.0F - (float)seg) / 18.0F + 0.125F) * height + xOff / 2.0D - zOff / 2.0D, z + offsetZ * (double)amount - xOff / 2.0D).color(r, g, b, 1.0F).endVertex();
         buf.pos(x + offsetX * (double)amount + centroid, y + offsetY * (double)(amount * amount + amount) * 0.5D + (double)((24.0F - (float)seg) / 18.0F + 0.125F) * height + zOff / 2.0D - xOff / 2.0D, z + offsetZ * (double)amount + xOff / 2.0D).color(r, g, b, 1.0F).endVertex();
      }

      Tessellator.getInstance().draw();
   }

   public static void renderChain(Render render, double x, double y, double z, Entity entity, float partTicks) {
      if (ChainHandler.canBeLinked(entity) && !ClientReflectiveAccessor.renderOutlines(render)) {
         renderChain(entity, x, y, z, partTicks);
      }

   }

   private static double interp(double start, double end, double pct) {
      return start + (end - start) * pct;
   }

   private static double prevX(Entity entity) {
      return entity instanceof EntityMinecart ? entity.lastTickPosX : entity.prevPosX;
   }

   private static double prevY(Entity entity) {
      return entity instanceof EntityMinecart ? entity.lastTickPosY : entity.prevPosY;
   }

   private static double prevZ(Entity entity) {
      return entity instanceof EntityMinecart ? entity.lastTickPosZ : entity.prevPosZ;
   }

   private static void renderChain(Entity cart, double x, double y, double z, float partialTicks) {
      Entity entity = (Entity)RENDER_MAP.get(cart.getEntityId());
      if (entity != null) {
         boolean player = entity instanceof EntityPlayer;
         if (player) {
            --y;
         } else {
            y += 0.1D;
         }

         Tessellator tess = Tessellator.getInstance();
         BufferBuilder buf = tess.getBuffer();
         double yaw = interp((double)entity.prevRotationYaw, (double)entity.rotationYaw, (double)(partialTicks * 0.5F)) * 3.141592653589793D / 180.0D;
         double pitch = interp((double)entity.prevRotationPitch, (double)entity.rotationPitch, (double)(partialTicks * 0.5F)) * 3.141592653589793D / 180.0D;
         double rotX = Math.cos(yaw);
         double rotZ = Math.sin(yaw);
         double rotY = Math.sin(pitch);
         double height = player ? (double)entity.getEyeHeight() * 0.7D : 0.0D;
         double pitchMod = Math.cos(pitch);
         double xLocus = interp(prevX(entity), entity.posX, (double)partialTicks);
         double yLocus = interp(prevY(entity), entity.posY, (double)partialTicks) + height;
         double zLocus = interp(prevZ(entity), entity.posZ, (double)partialTicks);
         if (player) {
            xLocus += -rotX * 0.7D - rotZ * 0.5D * pitchMod;
            yLocus = yLocus + (-rotY * 0.5D - 0.25D);
            zLocus += -rotZ * 0.7D + rotX * 0.5D * pitchMod;
            --zLocus;
            yLocus = yLocus + 2.0D;
         }

         double targetX = interp(prevX(cart), cart.posX, (double)partialTicks);
         double targetY = interp(prevY(cart), cart.posY, (double)partialTicks);
         double targetZ = interp(prevZ(cart), cart.posZ, (double)partialTicks);
         if (player) {
            xLocus -= rotX;
            zLocus -= rotZ;
         }

         double offsetX = (double)((float)(xLocus - targetX));
         double offsetY = (double)((float)(yLocus - targetY));
         double offsetZ = (double)((float)(zLocus - targetZ));
         GlStateManager.disableTexture2D();
         GlStateManager.disableLighting();
         GlStateManager.disableCull();
         drawChainSegment(x, y, z, buf, offsetX, offsetY, offsetZ, 0.025D, 0.0D, 0.3F, 0.3F, 0.3F, height);
         drawChainSegment(x, y, z, buf, offsetX, offsetY, offsetZ, 0.0D, 0.025D, 0.3F, 0.3F, 0.3F, height);
         GlStateManager.enableLighting();
         GlStateManager.enableTexture2D();
         GlStateManager.enableCull();
      }

   }

   public static void updateTick() {
      RENDER_MAP.clear();
      World world = Minecraft.getMinecraft().world;
      if (world != null) {
         for(Entity entity : world.getEntities(Entity.class, ChainHandler::canBeLinked)) {
            Entity other = ChainHandler.getLinked(entity);
            if (other != null) {
               RENDER_MAP.put(entity.getEntityId(), other);
            }
         }

      }
   }
}
