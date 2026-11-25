package me.ichun.mods.ichunutil.client.entity.head;

import java.util.HashMap;
import javax.annotation.Nullable;
import me.ichun.mods.ichunutil.api.client.head.HeadBase;
import me.ichun.mods.ichunutil.client.model.util.ModelHelper;
import me.ichun.mods.ichunutil.common.iChunUtil;
import me.ichun.mods.ichunutil.common.module.tabula.project.components.CubeInfo;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBat;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelBlaze;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelChicken;
import net.minecraft.client.model.ModelCreeper;
import net.minecraft.client.model.ModelDragon;
import net.minecraft.client.model.ModelEnderMite;
import net.minecraft.client.model.ModelGhast;
import net.minecraft.client.model.ModelGuardian;
import net.minecraft.client.model.ModelHorse;
import net.minecraft.client.model.ModelIllager;
import net.minecraft.client.model.ModelIronGolem;
import net.minecraft.client.model.ModelLlama;
import net.minecraft.client.model.ModelMagmaCube;
import net.minecraft.client.model.ModelOcelot;
import net.minecraft.client.model.ModelParrot;
import net.minecraft.client.model.ModelQuadruped;
import net.minecraft.client.model.ModelRabbit;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.model.ModelShulker;
import net.minecraft.client.model.ModelSilverfish;
import net.minecraft.client.model.ModelSlime;
import net.minecraft.client.model.ModelSnowMan;
import net.minecraft.client.model.ModelSpider;
import net.minecraft.client.model.ModelSquid;
import net.minecraft.client.model.ModelVillager;
import net.minecraft.client.model.ModelWither;
import net.minecraft.client.model.ModelWolf;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.entity.monster.AbstractIllager;
import net.minecraft.entity.monster.AbstractSkeleton;
import net.minecraft.entity.monster.EntityBlaze;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntityElderGuardian;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.monster.EntityEndermite;
import net.minecraft.entity.monster.EntityGhast;
import net.minecraft.entity.monster.EntityGuardian;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.entity.monster.EntityMagmaCube;
import net.minecraft.entity.monster.EntityPigZombie;
import net.minecraft.entity.monster.EntityPolarBear;
import net.minecraft.entity.monster.EntityShulker;
import net.minecraft.entity.monster.EntitySilverfish;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.entity.monster.EntitySnowman;
import net.minecraft.entity.monster.EntitySpider;
import net.minecraft.entity.monster.EntityVex;
import net.minecraft.entity.monster.EntityWitch;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.monster.EntityZombieVillager;
import net.minecraft.entity.passive.AbstractHorse;
import net.minecraft.entity.passive.EntityBat;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.entity.passive.EntityCow;
import net.minecraft.entity.passive.EntityLlama;
import net.minecraft.entity.passive.EntityMooshroom;
import net.minecraft.entity.passive.EntityOcelot;
import net.minecraft.entity.passive.EntityParrot;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.entity.passive.EntityRabbit;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.entity.passive.EntitySquid;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.entity.player.EntityPlayer;

public class HeadHandler {
   public static HashMap<Class<? extends EntityLivingBase>, HeadBase> modelOffsetHelpers = new HashMap<Class<? extends EntityLivingBase>, HeadBase>() {
      {
         this.put(AbstractHorse.class, new HeadHorse());
         this.put(AbstractIllager.class, (new HeadBase()).setEyeOffset(0.0F, 0.2F, 0.25F).setHalfInterpupillaryDistance(0.11875F).setEyeScale(0.7F));
         this.put(AbstractSkeleton.class, new HeadBiped());
         this.put(EntityPlayer.class, new HeadPlayer());
         this.put(EntityBat.class, new HeadBat());
         this.put(EntityBlaze.class, (new HeadBase()).setEyeOffset(0.0F, 0.0F, 0.25F));
         this.put(EntityChicken.class, (new HeadBase()).setEyeOffset(0.0F, 0.28125F, 0.125F).setHalfInterpupillaryDistance(0.09375F).setEyeScale(0.375F));
         this.put(EntityCow.class, (new HeadBase()).setEyeOffset(0.0F, 0.0625F, 0.375F).setHalfInterpupillaryDistance(0.1875F));
         this.put(EntityCreeper.class, (new HeadBase()).setEyeOffset(0.0F, 0.3125F, 0.25F));
         this.put(EntityDragon.class, new HeadDragon());
         this.put(EntityElderGuardian.class, new HeadElderGuardian());
         this.put(EntityEnderman.class, new HeadEnderman());
         this.put(EntityEndermite.class, new HeadEndermite());
         this.put(EntityGhast.class, new HeadGhast());
         this.put(EntityGuardian.class, new HeadGuardian());
         this.put(EntityIronGolem.class, (new HeadBase()).setEyeOffset(0.0F, 0.375F, 0.34375F));
         this.put(EntityLlama.class, new HeadLlama());
         this.put(EntityMagmaCube.class, new HeadMagmaCube());
         this.put(EntityMooshroom.class, (new HeadBase()).setEyeOffset(0.0F, 0.0625F, 0.375F).setHalfInterpupillaryDistance(0.1875F));
         this.put(EntityOcelot.class, new HeadOcelot());
         this.put(EntityParrot.class, new HeadParrot());
         this.put(EntityPig.class, (new HeadBase()).setEyeOffset(0.0F, 0.03125F, 0.5F).setHalfInterpupillaryDistance(0.1875F));
         this.put(EntityPigZombie.class, new HeadPigZombie());
         this.put(EntityPolarBear.class, (new HeadBase()).setEyeOffset(0.0F, -0.03125F, 0.1875F).setEyeScale(0.4F));
         this.put(EntityRabbit.class, new HeadRabbit());
         this.put(EntitySheep.class, new HeadSheep());
         this.put(EntityShulker.class, new HeadShulker());
         this.put(EntitySilverfish.class, (new HeadBase()).setEyeOffset(0.0F, 0.0F, 0.0625F).setHalfInterpupillaryDistance(0.0625F).setEyeScale(0.5F));
         this.put(EntitySlime.class, (new HeadBase()).setEyeOffset(0.0F, -1.1875F, 0.25F));
         this.put(EntitySnowman.class, new HeadSnowman());
         this.put(EntitySpider.class, new HeadSpider());
         this.put(EntitySquid.class, (new HeadBase()).setEyeOffset(0.0F, 0.0625F, 0.375F).setHalfInterpupillaryDistance(0.1875F));
         this.put(EntityVex.class, new HeadBiped());
         this.put(EntityVillager.class, (new HeadBase()).setEyeOffset(0.0F, 0.2F, 0.25F).setHalfInterpupillaryDistance(0.11875F).setEyeScale(0.7F));
         this.put(EntityWitch.class, (new HeadBase()).setEyeOffset(0.0F, 0.2F, 0.25F).setHalfInterpupillaryDistance(0.11875F).setEyeScale(0.7F));
         this.put(EntityWither.class, new HeadWither());
         this.put(EntityWolf.class, new HeadWolf());
         this.put(EntityZombie.class, new HeadBase());
         this.put(EntityZombieVillager.class, (new HeadBase()).setEyeOffset(0.0F, 0.2F, 0.25F).setHalfInterpupillaryDistance(0.11875F).setEyeScale(0.7F));
      }
   };

   public HeadHandler() {
   }

   @Nullable
   public static HeadBase getHelperBase(Class<? extends EntityLivingBase> clz) {
      HeadBase helper = (HeadBase)modelOffsetHelpers.get(clz);

      for(Class clzz = clz.getSuperclass(); helper == null && clzz != EntityLivingBase.class; clzz = clzz.getSuperclass()) {
         helper = getHelperBase(clzz);
         if (helper != null) {
            helper = helper.clone();
            modelOffsetHelpers.put(clz, helper);
            break;
         }
      }

      return helper;
   }

   public static void setHeadModel(HeadBase helper, RenderLivingBase renderer) {
      if (helper.headModel == null || iChunUtil.config.aggressiveHeadTracking == 1 || iChunUtil.config.aggressiveHeadTracking == 2 && renderer instanceof RenderPlayer) {
         ModelBase model = renderer.getMainModel();
         helper.headModel = new ModelRenderer[1];
         if (model instanceof ModelBiped) {
            helper.headModel[0] = ((ModelBiped)model).bipedHead;
         } else if (model instanceof ModelHorse) {
            helper.headModel[0] = iChunUtil.config.horseEasterEgg == 1 ? ((ModelHorse)model).body : ((ModelHorse)model).head;
         } else if (model instanceof ModelLlama) {
            helper.headModel[0] = iChunUtil.config.horseEasterEgg == 1 ? ((ModelLlama)model).body : ((ModelLlama)model).head;
         } else if (model instanceof ModelIllager) {
            helper.headModel[0] = ((ModelIllager)model).head;
         } else if (model instanceof ModelBat) {
            helper.headModel[0] = ((ModelBat)model).batHead;
         } else if (model instanceof ModelBlaze) {
            helper.headModel[0] = ((ModelBlaze)model).blazeHead;
         } else if (model instanceof ModelChicken) {
            helper.headModel[0] = ((ModelChicken)model).head;
         } else if (model instanceof ModelQuadruped) {
            helper.headModel[0] = ((ModelQuadruped)model).head;
         } else if (model instanceof ModelCreeper) {
            helper.headModel[0] = ((ModelCreeper)model).head;
         } else if (model instanceof ModelDragon) {
            helper.headModel[0] = ((ModelDragon)model).head;
         } else if (model instanceof ModelEnderMite) {
            helper.headModel[0] = ((ModelEnderMite)model).bodyParts[0];
         } else if (model instanceof ModelGhast) {
            helper.headModel[0] = ((ModelGhast)model).body;
         } else if (model instanceof ModelGuardian) {
            helper.headModel[0] = ((ModelGuardian)model).guardianBody;
         } else if (model instanceof ModelIronGolem) {
            helper.headModel[0] = ((ModelIronGolem)model).ironGolemHead;
         } else if (model instanceof ModelMagmaCube) {
            helper.headModel[0] = ((ModelMagmaCube)model).core;
         } else if (model instanceof ModelOcelot) {
            helper.headModel[0] = ((ModelOcelot)model).ocelotHead;
         } else if (model instanceof ModelParrot) {
            helper.headModel[0] = ((ModelParrot)model).head;
         } else if (model instanceof ModelRabbit) {
            helper.headModel[0] = ((ModelRabbit)model).rabbitHead;
         } else if (model instanceof ModelShulker) {
            helper.headModel[0] = ((ModelShulker)model).head;
         } else if (model instanceof ModelSilverfish) {
            helper.headModel[0] = ((ModelSilverfish)model).silverfishBodyParts[0];
         } else if (model instanceof ModelSlime) {
            helper.headModel[0] = ((ModelSlime)model).slimeBodies;
         } else if (model instanceof ModelSnowMan) {
            helper.headModel[0] = ((ModelSnowMan)model).head;
         } else if (model instanceof ModelSpider) {
            helper.headModel[0] = ((ModelSpider)model).spiderHead;
         } else if (model instanceof ModelSquid) {
            helper.headModel[0] = ((ModelSquid)model).squidBody;
         } else if (model instanceof ModelVillager) {
            helper.headModel[0] = ((ModelVillager)model).villagerHead;
         } else if (model instanceof ModelWither) {
            helper.headModel = ((ModelWither)model).heads;
         } else if (model instanceof ModelWolf) {
            helper.headModel[0] = ((ModelWolf)model).wolfHeadMain;
         }

         for(ModelRenderer head : helper.headModel) {
            if (head == null) {
               helper.headModel = null;
               break;
            }
         }
      }

   }

   @Nullable
   public static CubeInfo getHeadInfo(HeadBase helper) {
      if (helper.headInfo == null && helper.headModel != null && !helper.headModel[0].cubeList.isEmpty()) {
         ModelBox box = (ModelBox)helper.headModel[0].cubeList.get(0);
         helper.headInfo = ModelHelper.createCubeInfoFromModelBox(helper.headModel[0], box, box.boxName != null ? box.boxName.substring(box.boxName.lastIndexOf(".") + 1) : "");
      }

      return (CubeInfo)helper.headInfo;
   }
}
