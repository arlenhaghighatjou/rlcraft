package com.fantasticsource.mctools.animation;

import com.fantasticsource.mctools.ClientTickTimer;
import com.fantasticsource.mctools.MCTools;
import com.fantasticsource.mctools.Network;
import com.fantasticsource.tools.ReflectionTool;
import com.fantasticsource.tools.component.CBoolean;
import com.fantasticsource.tools.component.CDouble;
import com.fantasticsource.tools.component.CLong;
import com.fantasticsource.tools.component.CUUID;
import com.fantasticsource.tools.component.Component;
import com.fantasticsource.tools.component.path.CPath;
import net.lax1dude.eaglercraft.v1_8.netty.ByteBuf;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.lax1dude.eaglercraft.v1_8.EaglercraftUUID;
import java.util.function.Function;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.entity.layers.LayerArmorBase;
import net.minecraft.client.renderer.entity.layers.LayerBipedArmor;
import net.minecraft.client.renderer.entity.layers.LayerHeldItem;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.client.event.RenderLivingEvent.Pre;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerEvent.StartTracking;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class CBipedAnimation extends Component {
   public static Field RENDER_LIVING_BASE_LAYER_RENDERERS_FIELD;
   public static Field LAYER_ARMOR_BASE_MODEL_LEGGINGS_FIELD;
   public static Field LAYER_ARMOR_BASE_MODEL_ARMOR_FIELD;
   public static Field LAYER_HELD_ITEM_LIVING_ENTITY_RENDERER_FIELD;
   public static final HashMap<Entity, ArrayList<CBipedAnimation>> ANIMATION_DATA = new HashMap();
   protected static boolean initialized = false;
   public EaglercraftUUID id;
   public long startTime;
   public long pauseTime;
   public long pauseAt;
   public long removeAt;
   double rate;
   public CModelRendererAnimation head;
   public CModelRendererAnimation chest;
   public CModelRendererAnimation leftArm;
   public CModelRendererAnimation rightArm;
   public CModelRendererAnimation leftLeg;
   public CModelRendererAnimation rightLeg;
   public CModelRendererAnimation leftItem;
   public CModelRendererAnimation rightItem;
   public CPath.CPathData handItemSwap;
   public boolean bodyFacesLookDirection;

   public CBipedAnimation() {
      this(new CModelRendererAnimation(), new CModelRendererAnimation(), new CModelRendererAnimation(), new CModelRendererAnimation(), new CModelRendererAnimation(), new CModelRendererAnimation(), new CModelRendererAnimation(), new CModelRendererAnimation());
   }

   public CBipedAnimation(CModelRendererAnimation head, CModelRendererAnimation chest, CModelRendererAnimation leftArm, CModelRendererAnimation rightArm, CModelRendererAnimation leftLeg, CModelRendererAnimation rightLeg, CModelRendererAnimation leftItem, CModelRendererAnimation rightItem) {
      this.id = EaglercraftUUID.randomUUID();
      this.startTime = 0L;
      this.pauseTime = -1L;
      this.pauseAt = Long.MIN_VALUE;
      this.removeAt = Long.MIN_VALUE;
      this.rate = 1.0D;
      this.handItemSwap = new CPath.CPathData();
      this.bodyFacesLookDirection = false;
      this.head = head;
      this.chest = chest;
      this.leftArm = leftArm;
      this.rightArm = rightArm;
      this.leftLeg = leftLeg;
      this.rightLeg = rightLeg;
      this.leftItem = leftItem;
      this.rightItem = rightItem;
   }

   public static CBipedAnimation getCurrent(Entity entity) {
      CBipedAnimation result = new CBipedAnimation();
      CPath.CPathData[] resultData = result.getAllData();
      ArrayList<CBipedAnimation> toRemove = new ArrayList();
      ArrayList<CBipedAnimation> animations = (ArrayList)ANIMATION_DATA.getOrDefault(entity, new ArrayList());

      for(CBipedAnimation animation : animations) {
         if (animation.pauseAt != Long.MIN_VALUE && animation.pauseTime == -1L) {
            long runTime = (long)((double)(System.currentTimeMillis() - animation.startTime) * animation.rate);
            if (runTime >= animation.pauseAt) {
               animation.pauseAllInternal(System.currentTimeMillis() - (runTime - animation.pauseAt));
            }
         }

         if (animation.removeAt != Long.MIN_VALUE) {
            if (animation.pauseTime > -1L) {
               if ((double)(animation.pauseTime - animation.startTime) * animation.rate > (double)animation.removeAt) {
                  toRemove.add(animation);
                  continue;
               }
            } else if ((double)(System.currentTimeMillis() - animation.startTime) * animation.rate > (double)animation.removeAt) {
               toRemove.add(animation);
               continue;
            }
         }

         int i = 0;

         for(CPath.CPathData data : animation.getAllData()) {
            if (data.path != null) {
               CPath.CPathData data2 = resultData[i];
               data2.path = data.path;
               data2.startTime = data.startTime;
               data2.pauseTime = data.pauseTime;
               data2.rate = data.rate;
            }

            ++i;
         }
      }

      animations.removeAll(toRemove);
      return result;
   }

   public static void addAnimation(Entity entity, CBipedAnimation animation) {
      ((ArrayList)ANIMATION_DATA.computeIfAbsent(entity, (o) -> {
         return new ArrayList();
      })).add(animation);
      if (!entity.world.isRemote) {
         MCTools.sendToAllTracking(Network.WRAPPER, new Network.AddBipedAnimationsPacket(entity, new CBipedAnimation[]{animation}), entity);
      }

   }

   public static void updateAnimation(Entity entity, CBipedAnimation animation) {
      if (entity.world.isRemote) {
         throw new IllegalStateException("This method should not be called on the client side!");
      } else {
         ArrayList<CBipedAnimation> list = (ArrayList)ANIMATION_DATA.get(entity);
         if (list != null && list.remove(animation)) {
            MCTools.sendToAllTracking(Network.WRAPPER, new Network.UpdateBipedAnimationsPacket(entity, new CBipedAnimation[]{animation}), entity);
         }

      }
   }

   public static void removeAnimation(Entity entity, CBipedAnimation animation) {
      ArrayList<CBipedAnimation> list = (ArrayList)ANIMATION_DATA.get(entity);
      if (list != null) {
         list.remove(animation);
         if (!entity.world.isRemote) {
            MCTools.sendToAllTracking(Network.WRAPPER, new Network.RemoveBipedAnimationPacket(entity, animation), entity);
         }
      }

   }

   @SideOnly(Side.CLIENT)
   public static boolean removeAnimations(Entity entity, EaglercraftUUID id) {
      ArrayList<CBipedAnimation> list = (ArrayList)ANIMATION_DATA.get(entity);
      if (list == null) {
         return false;
      } else {
         int size = list.size();
         list.removeIf((animation) -> {
            return animation.id.equals(id);
         });
         return list.size() != size;
      }
   }

   public CPath.CPathData[] getAllData() {
      CPath.CPathData[] result = new CPath.CPathData[73];
      result[0] = this.handItemSwap;
      int i = 1;

      for(CModelRendererAnimation subAnimation : new CModelRendererAnimation[]{this.head, this.chest, this.rightArm, this.leftArm, this.rightLeg, this.leftLeg, this.rightItem, this.leftItem}) {
         for(CPath.CPathData data : subAnimation.getAllData()) {
            result[i++] = data;
         }
      }

      return result;
   }

   public CBipedAnimation setAllRates(double rate) {
      this.rate = rate;

      for(CPath.CPathData data : this.getAllData()) {
         data.rate = rate;
      }

      return this;
   }

   public CBipedAnimation setAllStartTimes(long time) {
      this.startTime = time;

      for(CPath.CPathData data : this.getAllData()) {
         data.startTime = time;
      }

      return this;
   }

   public CBipedAnimation pauseAll(@Nullable Entity entity) {
      return this.pauseAll(entity, System.currentTimeMillis());
   }

   public CBipedAnimation pauseAll(@Nullable Entity entity, long time) {
      this.pauseAllInternal(time);
      if (entity != null && !entity.world.isRemote) {
         MCTools.sendToAllTracking(Network.WRAPPER, new Network.UpdateBipedAnimationsPacket(entity, new CBipedAnimation[]{this}), entity);
      }

      return this;
   }

   protected CBipedAnimation pauseAllInternal(long time) {
      this.pauseTime = time;

      for(CPath.CPathData data : this.getAllData()) {
         data.pause(time);
      }

      return this;
   }

   public CBipedAnimation unpauseAll(@Nullable Entity entity) {
      return this.unpauseAll(entity, System.currentTimeMillis());
   }

   public CBipedAnimation unpauseAll(@Nullable Entity entity, long time) {
      if (this.pauseTime > -1L) {
         this.startTime += time - this.pauseTime;
         this.pauseTime = -1L;
      }

      for(CPath.CPathData data : this.getAllData()) {
         data.unpause(time);
      }

      return this;
   }

   public CBipedAnimation write(ByteBuf buf) {
      (new CUUID()).set(this.id).write(buf);
      buf.writeDouble(this.rate);
      buf.writeLong(this.startTime);
      buf.writeLong(this.pauseTime);
      buf.writeLong(this.pauseAt);
      buf.writeLong(this.removeAt);
      writeMarkedOrNull(buf, this.handItemSwap);
      this.head.write(buf);
      this.chest.write(buf);
      this.leftArm.write(buf);
      this.rightArm.write(buf);
      this.leftLeg.write(buf);
      this.rightLeg.write(buf);
      this.leftItem.write(buf);
      this.rightItem.write(buf);
      buf.writeBoolean(this.bodyFacesLookDirection);
      return this;
   }

   public CBipedAnimation read(ByteBuf buf) {
      this.id = (new CUUID()).read(buf).value;
      this.rate = buf.readDouble();
      this.startTime = buf.readLong();
      this.pauseTime = buf.readLong();
      this.pauseAt = buf.readLong();
      this.removeAt = buf.readLong();
      this.handItemSwap = (CPath.CPathData)readMarkedOrNull(buf);
      this.head.read(buf);
      this.chest.read(buf);
      this.leftArm.read(buf);
      this.rightArm.read(buf);
      this.leftLeg.read(buf);
      this.rightLeg.read(buf);
      this.leftItem.read(buf);
      this.rightItem.read(buf);
      this.bodyFacesLookDirection = buf.readBoolean();
      return this;
   }

   public CBipedAnimation save(OutputStream stream) {
      (new CUUID()).set(this.id).save(stream);
      (new CDouble()).set(this.rate).save(stream);
      (new CLong()).set(this.startTime).save(stream).set(this.pauseTime).save(stream).set(this.pauseAt).save(stream).set(this.removeAt).save(stream);
      saveMarkedOrNull(stream, this.handItemSwap);
      this.head.save(stream);
      this.chest.save(stream);
      this.leftArm.save(stream);
      this.rightArm.save(stream);
      this.leftLeg.save(stream);
      this.rightLeg.save(stream);
      this.leftItem.save(stream);
      this.rightItem.save(stream);
      (new CBoolean()).set(this.bodyFacesLookDirection).save(stream);
      return this;
   }

   public CBipedAnimation load(InputStream stream) {
      this.id = (new CUUID()).load(stream).value;
      this.rate = (new CDouble()).load(stream).value;
      CLong cl = new CLong();
      this.startTime = cl.load(stream).value;
      this.pauseTime = cl.load(stream).value;
      this.pauseAt = cl.load(stream).value;
      this.removeAt = cl.load(stream).value;
      this.handItemSwap = (CPath.CPathData)loadMarkedOrNull(stream);
      this.head.load(stream);
      this.chest.load(stream);
      this.leftArm.load(stream);
      this.rightArm.load(stream);
      this.leftLeg.load(stream);
      this.rightLeg.load(stream);
      this.leftItem.load(stream);
      this.rightItem.load(stream);
      this.bodyFacesLookDirection = (new CBoolean()).load(stream).value;
      return this;
   }

   public static void init(FMLPostInitializationEvent event) {
      if (!initialized) {
         initialized = true;
         MinecraftForge.EVENT_BUS.register(CBipedAnimation.class);
         MinecraftForge.EVENT_BUS.register(ModelPlayerEdit.class);
         if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT) {
            ClientTickTimer.schedule(1, () -> {
               RENDER_LIVING_BASE_LAYER_RENDERERS_FIELD = ReflectionTool.getField(RenderLivingBase.class, "layerRenderers", "layerRenderers");
               LAYER_ARMOR_BASE_MODEL_LEGGINGS_FIELD = ReflectionTool.getField(LayerArmorBase.class, "modelLeggings", "modelLeggings");
               LAYER_ARMOR_BASE_MODEL_ARMOR_FIELD = ReflectionTool.getField(LayerArmorBase.class, "modelArmor", "modelArmor");
               LAYER_HELD_ITEM_LIVING_ENTITY_RENDERER_FIELD = ReflectionTool.getField(LayerHeldItem.class, "livingEntityRenderer", "livingEntityRenderer");
               Field renderLivingBaseMainModelField = ReflectionTool.getField(RenderLivingBase.class, "mainModel", "mainModel");
               RenderManager renderManager = Minecraft.getMinecraft().getRenderManager();
               Map<String, RenderPlayer> map = renderManager.getSkinMap();
               ReflectionTool.set(renderLivingBaseMainModelField, map.get("default"), new ModelPlayerEdit(0.0F, false));
               ReflectionTool.set(renderLivingBaseMainModelField, map.get("slim"), new ModelPlayerEdit(0.0F, true));

               for(Render<? extends Entity> render : renderManager.entityRenderMap.values()) {
                  if (render instanceof RenderLivingBase) {
                     ModelBase oldModel = ((RenderLivingBase)render).getMainModel();
                     if (oldModel != null) {
                        if (oldModel.getClass() == ModelPlayer.class) {
                           ReflectionTool.set(renderLivingBaseMainModelField, render, new ModelPlayerEdit((ModelPlayer)oldModel));
                        } else if (oldModel.getClass() == ModelBiped.class) {
                           ReflectionTool.set(renderLivingBaseMainModelField, render, new ModelBipedEdit((ModelBiped)oldModel));
                        }
                     }
                  }
               }

            });
         }
      }

   }

   @SubscribeEvent
   public static void renderLivingBase(Pre event) {
      List<LayerRenderer> layers = (List)ReflectionTool.get(RENDER_LIVING_BASE_LAYER_RENDERERS_FIELD, event.getRenderer());

      for(int i = 0; i < layers.size(); ++i) {
         LayerRenderer layer = (LayerRenderer)layers.get(i);
         if (layer instanceof LayerBipedArmor) {
            ModelBase leggingsModel = (ModelBase)ReflectionTool.get(LAYER_ARMOR_BASE_MODEL_LEGGINGS_FIELD, layer);
            ModelBase armorModel = (ModelBase)ReflectionTool.get(LAYER_ARMOR_BASE_MODEL_ARMOR_FIELD, layer);
            if (leggingsModel.getClass() == ModelBiped.class) {
               ReflectionTool.set(LAYER_ARMOR_BASE_MODEL_LEGGINGS_FIELD, layer, new ModelBipedEdit((ModelBiped)leggingsModel));
            }

            if (armorModel.getClass() == ModelBiped.class) {
               ReflectionTool.set(LAYER_ARMOR_BASE_MODEL_ARMOR_FIELD, layer, new ModelBipedEdit((ModelBiped)armorModel));
            }
         } else if (layer.getClass() == LayerHeldItem.class) {
            layers.set(i, new LayerHeldItemEdit((RenderLivingBase)ReflectionTool.get(LAYER_HELD_ITEM_LIVING_ENTITY_RENDERER_FIELD, layer)));
         }
      }

      EntityLivingBase entity = event.getEntity();
      ArrayList<CBipedAnimation> animations = (ArrayList)ANIMATION_DATA.get(entity);
      if (animations != null) {
         for(CBipedAnimation animation : animations) {
            if (animation.bodyFacesLookDirection) {
               entity.setRenderYawOffset(entity.rotationYawHead);
               break;
            }
         }
      }

   }

   @SubscribeEvent
   public static void startTracking(StartTracking event) {
      Entity entity = event.getTarget();
      ArrayList<CBipedAnimation> list = (ArrayList)ANIMATION_DATA.get(entity);
      if (list != null && list.size() > 0) {
         Network.WRAPPER.sendTo(new Network.AddBipedAnimationsPacket(entity, (CBipedAnimation[])list.toArray(new CBipedAnimation[0])), (EntityPlayerMP)event.getEntityPlayer());
      }

   }
}
