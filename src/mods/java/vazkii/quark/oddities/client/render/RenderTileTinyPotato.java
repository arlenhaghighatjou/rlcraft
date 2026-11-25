package vazkii.quark.oddities.client.render;

import java.util.Calendar;
import javax.annotation.Nonnull;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBiped;
import net.lax1dude.eaglercraft.v1_8.opengl.WorldRenderer;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.lax1dude.eaglercraft.v1_8.opengl.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import vazkii.quark.base.client.ContributorRewardHandler;
import vazkii.quark.oddities.block.BlockTinyPotato;
import vazkii.quark.oddities.client.model.ModelTinyPotato;
import vazkii.quark.oddities.feature.TinyPotato;
import vazkii.quark.oddities.item.ItemBlockTinyPotato;
import vazkii.quark.oddities.tile.TileTinyPotato;

public class RenderTileTinyPotato extends TileEntitySpecialRenderer<TileTinyPotato> {
   private static final ResourceLocation texture = new ResourceLocation("quark", "textures/blocks/tiny_potato/tiny_potato.png");
   private static final ResourceLocation textureGrayscale = new ResourceLocation("quark", "textures/blocks/tiny_potato/tiny_potato_gray.png");
   private static final ResourceLocation textureHalloween = new ResourceLocation("quark", "textures/blocks/tiny_potato/tiny_potato_halloween.png");
   private static final ResourceLocation textureAngery = new ResourceLocation("quark", "textures/blocks/tiny_potato/angry_potato.png");
   private static final ResourceLocation textureGoldHat = new ResourceLocation("minecraft", "textures/models/armor/gold_layer_1.png");
   private static final ResourceLocation textureDiamondHat = new ResourceLocation("minecraft", "textures/models/armor/diamond_layer_1.png");
   private static final ModelTinyPotato model = new ModelTinyPotato();
   private static final ModelBiped armorModel = new ModelBiped(1.0F);

   public RenderTileTinyPotato() {
   }

   public static boolean isTheSpookDay(World world) {
      Calendar calendar = world.getCurrentDate();
      return calendar.get(2) + 1 == 10 && calendar.get(5) == 31;
   }

   public void render(@Nonnull TileTinyPotato potato, double x, double y, double z, float partialTicks, int destroyStage, float unused) {
      if (potato.getWorld().isBlockLoaded(potato.getPos(), false) && potato.getWorld().getBlockState(potato.getPos()).getBlock() == TinyPotato.tiny_potato) {
         IBlockState potatoState = potato.getWorld().getBlockState(potato.getPos());
         GlStateManager.pushMatrix();
         GlStateManager.enableRescaleNormal();
         GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
         GlStateManager.translate(x, y, z);
         Minecraft mc = Minecraft.getMinecraft();
         mc.renderEngine.bindTexture(isTheSpookDay(potato.getWorld()) ? textureHalloween : texture);
         String name = potato.name.toLowerCase().trim();
         GlStateManager.translate(0.5F, 1.5F, 0.5F);
         GlStateManager.scale(1.0F, -1.0F, -1.0F);
         float rotY = ((EnumFacing)potatoState.getValue(BlockTinyPotato.FACING)).getHorizontalAngle();
         GlStateManager.rotate(rotY, 0.0F, 1.0F, 0.0F);
         float jump = (float)potato.jumpTicks;
         if (jump > 0.0F) {
            jump -= partialTicks;
         }

         float up = (float)(-Math.abs(Math.sin((double)(jump / 10.0F) * 3.141592653589793D))) * 0.2F;
         float rotZ = (float)Math.sin((double)(jump / 10.0F) * 3.141592653589793D) * 2.0F;
         GlStateManager.translate(0.0F, up, 0.0F);
         GlStateManager.rotate(rotZ, 0.0F, 0.0F, 1.0F);
         GlStateManager.pushMatrix();
         int patronTier = -1;
         switch(name.hashCode()) {
         case -802493076:
            if (name.equals("pahimar")) {
               patronTier = 0;
            }
            break;
         case -376228516:
            if (name.equals("aureylian")) {
               patronTier = 4;
            }
            break;
         case 98634762:
            if (name.equals("grumm")) {
               patronTier = 3;
            }
            break;
         case 293018219:
            if (name.equals("kyle hyde")) {
               patronTier = 1;
            }
            break;
         case 1295949366:
            if (name.equals("dinnerbone")) {
               patronTier = 2;
            }
         }

         switch(patronTier) {
         case 0:
            GlStateManager.scale(1.0F, 0.3F, 1.0F);
            GlStateManager.translate(0.0F, 3.5F, 0.0F);
            break;
         case 1:
            mc.renderEngine.bindTexture(textureGrayscale);
            break;
         case 2:
         case 3:
            GlStateManager.rotate(180.0F, 0.0F, 0.0F, 1.0F);
            GlStateManager.translate(0.0F, -2.625F, 0.0F);
            break;
         case 4:
            GlStateManager.color(1.0F, 0.5F, 1.0F);
         }

         if (potato.angery) {
            mc.renderEngine.bindTexture(textureAngery);
         }

         boolean render = !name.equals("mami") && !name.equals("soaryn") && (!name.equals("eloraam") || jump == 0.0F);
         if (render) {
            model.render();
         }

         GlStateManager.popMatrix();
         patronTier = ContributorRewardHandler.getTier(name);
         if (patronTier > 0) {
            GlStateManager.pushMatrix();
            float s = 0.028571429F;
            GlStateManager.scale(s, s, s);
            GlStateManager.translate(0.0F, 46.0F, 0.0F);
            mc.renderEngine.bindTexture(patronTier > 1 ? textureDiamondHat : textureGoldHat);
            armorModel.bipedHead.render(1.0F);
            GlStateManager.popMatrix();
         }

         GlStateManager.pushMatrix();
         mc.renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
         GlStateManager.pushMatrix();
         GlStateManager.rotate(180.0F, 0.0F, 0.0F, 1.0F);
         GlStateManager.translate(0.0F, -1.0F, 0.0F);
         float s = 0.2857143F;
         GlStateManager.scale(s, s, s);
         EnumFacing potatoFacing = (EnumFacing)potato.getWorld().getBlockState(potato.getPos()).getValue(BlockTinyPotato.FACING);

         for(int i = 0; i < potato.getSizeInventory(); ++i) {
            ItemStack stack = potato.getStackInSlot(i);
            if (!stack.isEmpty()) {
               GlStateManager.pushMatrix();
               EnumFacing side = ((EnumFacing[])EnumFacing.class.getEnumConstants())[i];
               if (side.getAxis() != Axis.Y) {
                  float sideAngle = side.getHorizontalAngle() - potatoFacing.getHorizontalAngle();
                  side = EnumFacing.fromAngle((double)sideAngle);
               }

               boolean block = stack.getItem() instanceof ItemBlock;
               boolean mySon = stack.getItem() instanceof ItemBlockTinyPotato;
               switch(side) {
               case UP:
                  if (mySon) {
                     GlStateManager.translate(0.0F, 0.6F, 0.5F);
                  } else if (block) {
                     GlStateManager.translate(0.0F, 0.3F, 0.5F);
                  }

                  GlStateManager.translate(0.0F, -0.5F, -0.4F);
                  break;
               case DOWN:
                  GlStateManager.translate(0.0D, -2.3D, -0.88D);
                  if (mySon) {
                     GlStateManager.translate(0.0D, 0.65D, 0.6D);
                  } else if (block) {
                     GlStateManager.translate(0.0D, 1.0D, 0.6D);
                  }
                  break;
               case NORTH:
                  GlStateManager.translate(0.0D, -1.9D, 0.02D);
                  if (mySon) {
                     GlStateManager.translate(0.0D, 1.0D, 0.6D);
                  } else if (block) {
                     GlStateManager.translate(0.0D, 1.0D, 0.6D);
                  }
                  break;
               case SOUTH:
                  GlStateManager.translate(0.0D, -1.6D, -0.89D);
                  if (mySon) {
                     GlStateManager.translate(0.0D, 1.4D, 0.5D);
                  } else if (block) {
                     GlStateManager.translate(0.0D, 1.0D, 0.5D);
                  }
                  break;
               case EAST:
                  if (mySon) {
                     GlStateManager.translate(-0.4F, 0.65F, 0.0F);
                  } else if (block) {
                     GlStateManager.translate(-0.4F, 0.8F, 0.0F);
                  } else {
                     GlStateManager.rotate(-90.0F, 0.0F, 1.0F, 0.0F);
                  }

                  GlStateManager.translate(-0.3F, -1.9F, 0.04F);
                  break;
               case WEST:
                  if (mySon) {
                     GlStateManager.translate(1.0F, 0.65F, 1.0F);
                  } else if (block) {
                     GlStateManager.translate(1.0F, 0.8F, 1.0F);
                  } else {
                     GlStateManager.rotate(-90.0F, 0.0F, 1.0F, 0.0F);
                  }

                  GlStateManager.translate(-0.3F, -1.9F, -0.92F);
               }

               if (mySon) {
                  GlStateManager.scale(1.1D, 1.1D, 1.1D);
               } else if (block) {
                  GlStateManager.scale(0.5D, 0.5D, 0.5D);
               }

               if (block && side == EnumFacing.NORTH) {
                  GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
               }

               this.renderItem(stack);
               GlStateManager.popMatrix();
            }
         }

         GlStateManager.popMatrix();
         GlStateManager.popMatrix();
         GlStateManager.rotate(-rotZ, 0.0F, 0.0F, 1.0F);
         GlStateManager.rotate(-rotY, 0.0F, 1.0F, 0.0F);
         GlStateManager.color(1.0F, 1.0F, 1.0F);
         GlStateManager.scale(1.0F, -1.0F, -1.0F);
         RayTraceResult pos = mc.objectMouseOver;
         if (!name.isEmpty() && pos != null && potato.getPos().equals(pos.getBlockPos())) {
            GlStateManager.pushMatrix();
            GlStateManager.translate(0.0F, -0.6F, 0.0F);
            GlStateManager.rotate(-mc.getRenderManager().playerViewY, 0.0F, 1.0F, 0.0F);
            GlStateManager.rotate(mc.getRenderManager().playerViewX, 1.0F, 0.0F, 0.0F);
            float f = 1.6F;
            float f1 = 0.016666668F * f;
            GlStateManager.scale(-f1, -f1, f1);
            GlStateManager.disableLighting();
            GlStateManager.translate(0.0F, 0.0F / f1, 0.0F);
            GlStateManager.depthMask(false);
            GlStateManager.enableBlend();
            OpenGlHelper.glBlendFunc(770, 771, 1, 0);
            Tessellator tessellator = Tessellator.getInstance();
            WorldRenderer worldrenderer = tessellator.getBuffer();
            GlStateManager.disableTexture2D();
            worldrenderer.begin(7, DefaultVertexFormats.POSITION_COLOR);
            int i = mc.fontRenderer.getStringWidth(potato.name) / 2;
            worldrenderer.pos((double)(-i - 1), -1.0D, 0.0D).color(0.0F, 0.0F, 0.0F, 0.25F).endVertex();
            worldrenderer.pos((double)(-i - 1), 8.0D, 0.0D).color(0.0F, 0.0F, 0.0F, 0.25F).endVertex();
            worldrenderer.pos((double)(i + 1), 8.0D, 0.0D).color(0.0F, 0.0F, 0.0F, 0.25F).endVertex();
            worldrenderer.pos((double)(i + 1), -1.0D, 0.0D).color(0.0F, 0.0F, 0.0F, 0.25F).endVertex();
            tessellator.draw();
            GlStateManager.enableTexture2D();
            GlStateManager.depthMask(true);
            mc.fontRenderer.drawString(potato.name, -mc.fontRenderer.getStringWidth(potato.name) / 2, 0, 16777215);
            if (name.equals("pahimar") || name.equals("soaryn")) {
               GlStateManager.translate(0.0F, 14.0F, 0.0F);
               String str = name.equals("pahimar") ? "[WIP]" : "(soon)";
               GlStateManager.depthMask(false);
               GlStateManager.enableBlend();
               OpenGlHelper.glBlendFunc(770, 771, 1, 0);
               GlStateManager.disableTexture2D();
               worldrenderer.begin(7, DefaultVertexFormats.POSITION_COLOR);
               i = mc.fontRenderer.getStringWidth(str) / 2;
               worldrenderer.pos((double)(-i - 1), -1.0D, 0.0D).color(0.0F, 0.0F, 0.0F, 0.25F).endVertex();
               worldrenderer.pos((double)(-i - 1), 8.0D, 0.0D).color(0.0F, 0.0F, 0.0F, 0.25F).endVertex();
               worldrenderer.pos((double)(i + 1), 8.0D, 0.0D).color(0.0F, 0.0F, 0.0F, 0.25F).endVertex();
               worldrenderer.pos((double)(i + 1), -1.0D, 0.0D).color(0.0F, 0.0F, 0.0F, 0.25F).endVertex();
               tessellator.draw();
               GlStateManager.enableTexture2D();
               GlStateManager.depthMask(true);
               mc.fontRenderer.drawString(str, -mc.fontRenderer.getStringWidth(str) / 2, 0, 16777215);
            }

            GlStateManager.enableLighting();
            GlStateManager.disableBlend();
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            GlStateManager.scale(1.0F / -f1, 1.0F / -f1, 1.0F / f1);
            GlStateManager.popMatrix();
         }

         GlStateManager.popMatrix();
      }
   }

   private void renderItem(ItemStack stack) {
      Minecraft.getMinecraft().getRenderItem().renderItem(stack, TransformType.HEAD);
   }

   private void renderBlock(Block block) {
      Minecraft.getMinecraft().renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
      Minecraft.getMinecraft().getBlockRendererDispatcher().renderBlockBrightness(block.getDefaultState(), 1.0F);
   }
}
