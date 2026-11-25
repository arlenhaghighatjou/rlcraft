package vazkii.neat;

import net.lax1dude.eaglercraft.v1_8.opengl.EaglercraftGPU;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.Stack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.lax1dude.eaglercraft.v1_8.opengl.WorldRenderer;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.RenderManager;
import net.lax1dude.eaglercraft.v1_8.minecraft.EaglerTextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.EventBus;

public class HealthBarRenderer {
   List<EntityLivingBase> renderedEntities = new ArrayList();

   public HealthBarRenderer() {
   }

   public void register(EventBus bus) {
      bus.register(RenderWorldLastEvent.class, this::onRenderWorldLast);
   }

   public void onRenderWorldLast(RenderWorldLastEvent event) {
      Minecraft mc = Minecraft.getMinecraft();
      if ((NeatConfig.renderInF1 || Minecraft.isGuiEnabled()) && NeatConfig.draw) {
         Entity cameraEntity = mc.getRenderViewEntity();
         BlockPos renderingVector = cameraEntity.getPosition();
         Frustum frustum = new Frustum();
         float partialTicks = event.getPartialTicks();
         double viewX = cameraEntity.lastTickPosX + (cameraEntity.posX - cameraEntity.lastTickPosX) * (double)partialTicks;
         double viewY = cameraEntity.lastTickPosY + (cameraEntity.posY - cameraEntity.lastTickPosY) * (double)partialTicks;
         double viewZ = cameraEntity.lastTickPosZ + (cameraEntity.posZ - cameraEntity.lastTickPosZ) * (double)partialTicks;
         frustum.setPosition(viewX, viewY, viewZ);
         if (NeatConfig.showOnlyFocused) {
            Entity focused = getEntityLookedAt(mc.player);
            if (focused != null && focused instanceof EntityLivingBase && focused.isEntityAlive()) {
               this.renderHealthBar((EntityLivingBase)focused, partialTicks, cameraEntity);
            }
         } else {
            WorldClient client = mc.world;

            for(Entity entity : client.entityList) {
               if (entity != null && entity instanceof EntityLivingBase && entity != mc.player && entity.isInRangeToRender3d((double)renderingVector.getX(), (double)renderingVector.getY(), (double)renderingVector.getZ()) && (entity.ignoreFrustumCheck || frustum.isBoundingBoxInFrustum(entity.getEntityBoundingBox())) && entity.isEntityAlive() && entity.getRecursivePassengers().isEmpty()) {
                  this.renderHealthBar((EntityLivingBase)entity, partialTicks, cameraEntity);
               }
            }
         }

      }
   }

   public void renderHealthBar(EntityLivingBase passedEntity, float partialTicks, Entity viewPoint) {
      Stack<EntityLivingBase> ridingStack = new Stack();
      EntityLivingBase entity = passedEntity;
      ridingStack.push(passedEntity);

      while(entity.getRidingEntity() != null && entity.getRidingEntity() instanceof EntityLivingBase) {
         entity = (EntityLivingBase)entity.getRidingEntity();
         ridingStack.push(entity);
      }

      Minecraft mc = Minecraft.getMinecraft();
      float pastTranslate = 0.0F;

      while(!ridingStack.isEmpty()) {
         entity = (EntityLivingBase)ridingStack.pop();
         boolean boss = !entity.isNonBoss();
         String entityID = EntityList.getEntityString(entity);
         if (!NeatConfig.blacklist.contains(entityID)) {
            float distance = passedEntity.getDistance(viewPoint);
            if (distance <= (float)NeatConfig.maxDistance && passedEntity.canEntityBeSeen(viewPoint) && !entity.isInvisible() && (NeatConfig.showOnBosses || boss) && (NeatConfig.showOnPlayers || !(entity instanceof EntityPlayer))) {
               double x = passedEntity.lastTickPosX + (passedEntity.posX - passedEntity.lastTickPosX) * (double)partialTicks;
               double y = passedEntity.lastTickPosY + (passedEntity.posY - passedEntity.lastTickPosY) * (double)partialTicks;
               double z = passedEntity.lastTickPosZ + (passedEntity.posZ - passedEntity.lastTickPosZ) * (double)partialTicks;
               float scale = 0.026666673F;
               float maxHealth = entity.getMaxHealth();
               float health = Math.min(maxHealth, entity.getHealth());
               if (maxHealth > 0.0F) {
                  float percent = (float)((int)(health / maxHealth * 100.0F));
                  RenderManager renderManager = Minecraft.getMinecraft().getRenderManager();
                  GlStateManager.pushMatrix();
                  GlStateManager.translate((float)(x - renderManager.viewerPosX), (float)(y - renderManager.viewerPosY + (double)passedEntity.height + NeatConfig.heightAbove), (float)(z - renderManager.viewerPosZ));
                  EaglercraftGPU.glNormal3f(0.0F, 1.0F, 0.0F);
                  GlStateManager.rotate(-renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
                  GlStateManager.rotate(renderManager.playerViewX, 1.0F, 0.0F, 0.0F);
                  GlStateManager.scale(-scale, -scale, scale);
                  boolean lighting = GlStateManager.isLightingEnabled();
                  GlStateManager.disableLighting();
                  GlStateManager.depthMask(false);
                  GlStateManager.disableDepth();
                  GlStateManager.disableTexture2D();
                  GlStateManager.enableBlend();
                  GlStateManager.blendFunc(770, 771);
                  Tessellator tessellator = Tessellator.getInstance();
                  WorldRenderer buffer = tessellator.getBuffer();
                  float padding = (float)NeatConfig.backgroundPadding;
                  int bgHeight = NeatConfig.backgroundHeight;
                  int barHeight = NeatConfig.barHeight;
                  float size = (float)NeatConfig.plateSize;
                  int r = 0;
                  int g = 255;
                  int b = 0;
                  ItemStack stack = null;
                  if (entity instanceof IMob) {
                     r = 255;
                     g = 0;
                     EnumCreatureAttribute attr = entity.getCreatureAttribute();
                     switch(attr) {
                     case ARTHROPOD:
                        stack = new ItemStack(Items.SPIDER_EYE);
                        break;
                     case UNDEAD:
                        stack = new ItemStack(Items.ROTTEN_FLESH);
                        break;
                     default:
                        stack = new ItemStack(Items.SKULL, 1, 4);
                     }
                  }

                  if (boss) {
                     stack = new ItemStack(Items.SKULL);
                     size = (float)NeatConfig.plateSizeBoss;
                     r = 128;
                     g = 0;
                     b = 128;
                  }

                  int armor = entity.getTotalArmorValue();
                  boolean useHue = !NeatConfig.colorByType;
                  if (useHue) {
                     float hue = Math.max(0.0F, health / maxHealth / 3.0F - 0.07F);
                     int color = MathHelper.hsvToRGB(hue, 1.0F, 1.0F);
                     r = color >> 16 & 255;
                     g = color >> 8 & 255;
                     b = color & 255;
                  }

                  GlStateManager.translate(0.0F, pastTranslate, 0.0F);
                  float s = 0.5F;
                  String name = I18n.format(entity.getDisplayName().getFormattedText(), new Object[0]);
                  if (entity instanceof EntityLiving && ((EntityLiving)entity).hasCustomName()) {
                     name = TextFormatting.ITALIC + ((EntityLiving)entity).getCustomNameTag();
                  } else if (entity instanceof EntityVillager) {
                     name = I18n.format("entity.Villager.name", new Object[0]);
                  }

                  float namel = (float)mc.fontRenderer.getStringWidth(name) * s;
                  if (namel + 20.0F > size * 2.0F) {
                     size = namel / 2.0F + 10.0F;
                  }

                  float healthSize = size * (health / maxHealth);
                  if (NeatConfig.drawBackground) {
                     buffer.begin(7, DefaultVertexFormats.POSITION_COLOR);
                     buffer.pos((double)(-size - padding), (double)(-bgHeight), 0.0D).color(0, 0, 0, 64).endVertex();
                     buffer.pos((double)(-size - padding), (double)((float)barHeight + padding), 0.0D).color(0, 0, 0, 64).endVertex();
                     buffer.pos((double)(size + padding), (double)((float)barHeight + padding), 0.0D).color(0, 0, 0, 64).endVertex();
                     buffer.pos((double)(size + padding), (double)(-bgHeight), 0.0D).color(0, 0, 0, 64).endVertex();
                     tessellator.draw();
                  }

                  buffer.begin(7, DefaultVertexFormats.POSITION_COLOR);
                  buffer.pos((double)(-size), 0.0D, 0.0D).color(127, 127, 127, 127).endVertex();
                  buffer.pos((double)(-size), (double)barHeight, 0.0D).color(127, 127, 127, 127).endVertex();
                  buffer.pos((double)size, (double)barHeight, 0.0D).color(127, 127, 127, 127).endVertex();
                  buffer.pos((double)size, 0.0D, 0.0D).color(127, 127, 127, 127).endVertex();
                  tessellator.draw();
                  buffer.begin(7, DefaultVertexFormats.POSITION_COLOR);
                  buffer.pos((double)(-size), 0.0D, 0.0D).color(r, g, b, 127).endVertex();
                  buffer.pos((double)(-size), (double)barHeight, 0.0D).color(r, g, b, 127).endVertex();
                  buffer.pos((double)(healthSize * 2.0F - size), (double)barHeight, 0.0D).color(r, g, b, 127).endVertex();
                  buffer.pos((double)(healthSize * 2.0F - size), 0.0D, 0.0D).color(r, g, b, 127).endVertex();
                  tessellator.draw();
                  GlStateManager.enableTexture2D();
                  GlStateManager.pushMatrix();
                  GlStateManager.translate(-size, -4.5F, 0.0F);
                  GlStateManager.scale(s, s, s);
                  mc.fontRenderer.drawString(name, 0, 0, 16777215);
                  GlStateManager.pushMatrix();
                  float s1 = 0.75F;
                  GlStateManager.scale(s1, s1, s1);
                  int h = NeatConfig.hpTextHeight;
                  String maxHpStr = TextFormatting.BOLD + "" + (double)Math.round((double)maxHealth * 100.0D) / 100.0D;
                  String hpStr = "" + (double)Math.round((double)health * 100.0D) / 100.0D;
                  String percStr = (int)percent + "%";
                  if (maxHpStr.endsWith(".0")) {
                     maxHpStr = maxHpStr.substring(0, maxHpStr.length() - 2);
                  }

                  if (hpStr.endsWith(".0")) {
                     hpStr = hpStr.substring(0, hpStr.length() - 2);
                  }

                  if (NeatConfig.showCurrentHP) {
                     mc.fontRenderer.drawString(hpStr, 2, h, 16777215);
                  }

                  if (NeatConfig.showMaxHP) {
                     mc.fontRenderer.drawString(maxHpStr, (int)(size / (s * s1) * 2.0F) - 2 - mc.fontRenderer.getStringWidth(maxHpStr), h, 16777215);
                  }

                  if (NeatConfig.showPercentage) {
                     mc.fontRenderer.drawString(percStr, (int)(size / (s * s1)) - mc.fontRenderer.getStringWidth(percStr) / 2, h, -1);
                  }

                  if (NeatConfig.enableDebugInfo && mc.gameSettings.showDebugInfo) {
                     mc.fontRenderer.drawString("ID: \"" + entityID + "\"", 0, h + 16, -1);
                  }

                  GlStateManager.popMatrix();
                  GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
                  int off = 0;
                  s1 = 0.5F;
                  GlStateManager.scale(s1, s1, s1);
                  GlStateManager.translate(size / (s * s1) * 2.0F - 16.0F, 0.0F, 0.0F);
                  mc.renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
                  if (stack != null && NeatConfig.showAttributes) {
                     this.renderIcon(off, 0, stack, 16, 16);
                     off -= 16;
                  }

                  if (armor > 0 && NeatConfig.showArmor) {
                     int ironArmor = armor % 5;
                     int diamondArmor = armor / 5;
                     if (!NeatConfig.groupArmor) {
                        ironArmor = armor;
                        diamondArmor = 0;
                     }

                     stack = new ItemStack(Items.IRON_CHESTPLATE);

                     for(int i = 0; i < ironArmor; ++i) {
                        this.renderIcon(off, 0, stack, 16, 16);
                        off -= 4;
                     }

                     stack = new ItemStack(Items.DIAMOND_CHESTPLATE);

                     for(int i = 0; i < diamondArmor; ++i) {
                        this.renderIcon(off, 0, stack, 16, 16);
                        off -= 4;
                     }
                  }

                  GlStateManager.popMatrix();
                  GlStateManager.disableBlend();
                  GlStateManager.enableDepth();
                  GlStateManager.depthMask(true);
                  if (lighting) {
                     GlStateManager.enableLighting();
                  }

                  GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
                  GlStateManager.popMatrix();
                  pastTranslate -= (float)(bgHeight + barHeight) + padding;
               }
            }
         }
      }

   }

   private void renderIcon(int vertexX, int vertexY, ItemStack stack, int intU, int intV) {
      try {
         IBakedModel iBakedModel = Minecraft.getMinecraft().getRenderItem().getItemModelMesher().getItemModel(stack);
         EaglerTextureAtlasSprite textureAtlasSprite = Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(iBakedModel.getParticleTexture().getIconName());
         Minecraft.getMinecraft().getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
         Tessellator tessellator = Tessellator.getInstance();
         WorldRenderer buffer = tessellator.getBuffer();
         buffer.begin(7, DefaultVertexFormats.POSITION_TEX);
         buffer.pos((double)vertexX, (double)(vertexY + intV), 0.0D).tex((double)textureAtlasSprite.getMinU(), (double)textureAtlasSprite.getMaxV()).endVertex();
         buffer.pos((double)(vertexX + intU), (double)(vertexY + intV), 0.0D).tex((double)textureAtlasSprite.getMaxU(), (double)textureAtlasSprite.getMaxV()).endVertex();
         buffer.pos((double)(vertexX + intU), (double)vertexY, 0.0D).tex((double)textureAtlasSprite.getMaxU(), (double)textureAtlasSprite.getMinV()).endVertex();
         buffer.pos((double)vertexX, (double)vertexY, 0.0D).tex((double)textureAtlasSprite.getMinU(), (double)textureAtlasSprite.getMinV()).endVertex();
         tessellator.draw();
      } catch (Exception var10) {
         ;
      }

   }

   public static Entity getEntityLookedAt(Entity e) {
      Entity foundEntity = null;
      double finalDistance = 32.0D;
      double distance = 32.0D;
      RayTraceResult pos = raycast(e, 32.0D);
      Vec3d positionVector = e.getPositionVector();
      if (e instanceof EntityPlayer) {
         positionVector = positionVector.addVector(0.0D, (double)e.getEyeHeight(), 0.0D);
      }

      if (pos != null) {
         distance = pos.hitVec.distanceTo(positionVector);
      }

      Vec3d lookVector = e.getLookVec();
      Vec3d reachVector = positionVector.addVector(lookVector.x * 32.0D, lookVector.y * 32.0D, lookVector.z * 32.0D);
      Entity lookedEntity = null;
      List<Entity> entitiesInBoundingBox = e.getEntityWorld().getEntitiesWithinAABBExcludingEntity(e, e.getEntityBoundingBox().grow(lookVector.x * 32.0D, lookVector.y * 32.0D, lookVector.z * 32.0D).expand(1.0D, 1.0D, 1.0D));
      double minDistance = distance;

      for(Entity entity : entitiesInBoundingBox) {
         if (entity.canBeCollidedWith()) {
            float collisionBorderSize = entity.getCollisionBorderSize();
            AxisAlignedBB hitbox = entity.getEntityBoundingBox().expand((double)collisionBorderSize, (double)collisionBorderSize, (double)collisionBorderSize);
            RayTraceResult interceptPosition = hitbox.calculateIntercept(positionVector, reachVector);
            if (hitbox.contains(positionVector)) {
               if (0.0D < minDistance || minDistance == 0.0D) {
                  lookedEntity = entity;
                  minDistance = 0.0D;
               }
            } else if (interceptPosition != null) {
               double distanceToEntity = positionVector.distanceTo(interceptPosition.hitVec);
               if (distanceToEntity < minDistance || minDistance == 0.0D) {
                  lookedEntity = entity;
                  minDistance = distanceToEntity;
               }
            }
         }

         if (lookedEntity != null && (minDistance < distance || pos == null)) {
            foundEntity = lookedEntity;
         }
      }

      return foundEntity;
   }

   public static RayTraceResult raycast(Entity e, double len) {
      Vec3d vec = new Vec3d(e.posX, e.posY, e.posZ);
      if (e instanceof EntityPlayer) {
         vec = vec.add(new Vec3d(0.0D, (double)e.getEyeHeight(), 0.0D));
      }

      Vec3d look = e.getLookVec();
      return look == null ? null : raycast(e.getEntityWorld(), vec, look, len);
   }

   public static RayTraceResult raycast(World world, Vec3d origin, Vec3d ray, double len) {
      Vec3d end = origin.add(ray.normalize().scale(len));
      RayTraceResult pos = world.rayTraceBlocks(origin, end);
      return pos;
   }
}
