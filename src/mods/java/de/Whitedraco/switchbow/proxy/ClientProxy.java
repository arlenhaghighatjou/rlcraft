package de.Whitedraco.switchbow.proxy;

import de.Whitedraco.switchbow.entity.EntityLightningBoltSwitchBow;
import de.Whitedraco.switchbow.entity.arrow.EntityArrowAir;
import de.Whitedraco.switchbow.entity.arrow.EntityArrowBone;
import de.Whitedraco.switchbow.entity.arrow.EntityArrowBouncy;
import de.Whitedraco.switchbow.entity.arrow.EntityArrowBurial;
import de.Whitedraco.switchbow.entity.arrow.EntityArrowChorus;
import de.Whitedraco.switchbow.entity.arrow.EntityArrowDiamond;
import de.Whitedraco.switchbow.entity.arrow.EntityArrowDragonBreath;
import de.Whitedraco.switchbow.entity.arrow.EntityArrowEnderperle;
import de.Whitedraco.switchbow.entity.arrow.EntityArrowFire;
import de.Whitedraco.switchbow.entity.arrow.EntityArrowFirework;
import de.Whitedraco.switchbow.entity.arrow.EntityArrowFrost;
import de.Whitedraco.switchbow.entity.arrow.EntityArrowKnockback;
import de.Whitedraco.switchbow.entity.arrow.EntityArrowLightningBolt;
import de.Whitedraco.switchbow.entity.arrow.EntityArrowLove;
import de.Whitedraco.switchbow.entity.arrow.EntityArrowLuck;
import de.Whitedraco.switchbow.entity.arrow.EntityArrowPiercing;
import de.Whitedraco.switchbow.entity.arrow.EntityArrowProtector;
import de.Whitedraco.switchbow.entity.arrow.EntityArrowRedstone;
import de.Whitedraco.switchbow.entity.arrow.EntityArrowSplit;
import de.Whitedraco.switchbow.entity.arrow.EntityArrowSplitLove;
import de.Whitedraco.switchbow.entity.arrow.EntityArrowSprinkler;
import de.Whitedraco.switchbow.entity.arrow.EntityArrowTNT;
import de.Whitedraco.switchbow.entity.arrow.EntityArrowTorch;
import de.Whitedraco.switchbow.entity.arrow.EntityArrowTriple;
import de.Whitedraco.switchbow.entity.arrow.EntityArrowUnderwater;
import de.Whitedraco.switchbow.entity.arrow.EntityArrowVampier;
import de.Whitedraco.switchbow.entity.arrow.EntityArrowWither;
import de.Whitedraco.switchbow.entity.arrow.EntityArrowoftheSearcher;
import de.Whitedraco.switchbow.entity.arrow.upgrade.EntityArrowAirUpgrade;
import de.Whitedraco.switchbow.entity.arrow.upgrade.EntityArrowBoneAOE;
import de.Whitedraco.switchbow.entity.arrow.upgrade.EntityArrowBurialAOE;
import de.Whitedraco.switchbow.entity.arrow.upgrade.EntityArrowFireUpgrade;
import de.Whitedraco.switchbow.entity.arrow.upgrade.EntityArrowLightningBoltStorm;
import de.Whitedraco.switchbow.entity.arrow.upgrade.EntityArrowLuckUpgrade;
import de.Whitedraco.switchbow.entity.arrow.upgrade.EntityArrowSplitLoveUpgrade;
import de.Whitedraco.switchbow.entity.arrow.upgrade.EntityArrowSplitUpgrade;
import de.Whitedraco.switchbow.entity.living.EntityLittleIronGolem;
import de.Whitedraco.switchbow.particle.FireFX;
import de.Whitedraco.switchbow.particle.TransformationFX;
import de.Whitedraco.switchbow.renderer.RenderLightnigBoltSwitchBow;
import de.Whitedraco.switchbow.renderer.arrow.RenderArrowSplit;
import de.Whitedraco.switchbow.renderer.arrow.RenderArrows;
import de.Whitedraco.switchbow.renderer.arrow.upgrade.RenderArrowSplitUpgrade;
import de.Whitedraco.switchbow.renderer.living.RenderLittleIronGolem;
import de.Whitedraco.switchbow.renderer.tileentity.RenderTileEntityPodest;
import de.Whitedraco.switchbow.tileentity.TileEntityPodest;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.item.Item;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.RenderingRegistry;

public class ClientProxy extends CommonProxy {
   public static final KeyBinding SwitchBow_KeyBinding_Up = new KeyBinding("key.switchbow.switchup", 0, "category.switchbow");
   public static final KeyBinding SwitchBow_KeyBinding_Down = new KeyBinding("key.switchbow.switchdown", 0, "category.switchbow");

   public ClientProxy() {
   }

   public void registerRenderers() {
      ClientRegistry.registerKeyBinding(SwitchBow_KeyBinding_Up);
      ClientRegistry.registerKeyBinding(SwitchBow_KeyBinding_Down);
      RenderManager rm = Minecraft.getMinecraft().getRenderManager();
      RenderingRegistry.registerEntityRenderingHandler(EntityArrowLightningBolt.class, new RenderArrows(rm, new ResourceLocation("switchbow:textures/models/arrows/ArrowLightningBolt.png")));
      RenderingRegistry.registerEntityRenderingHandler(EntityArrowTNT.class, new RenderArrows(rm, new ResourceLocation("switchbow:textures/models/arrows/ArrowTNT.png")));
      RenderingRegistry.registerEntityRenderingHandler(EntityArrowFrost.class, new RenderArrows(rm, new ResourceLocation("switchbow:textures/models/arrows/ArrowFrost.png")));
      RenderingRegistry.registerEntityRenderingHandler(EntityArrowAir.class, new RenderArrows(rm, new ResourceLocation("switchbow:textures/models/arrows/ArrowAir.png")));
      RenderingRegistry.registerEntityRenderingHandler(EntityArrowEnderperle.class, new RenderArrows(rm, new ResourceLocation("switchbow:textures/models/arrows/ArrowEnderperle.png")));
      RenderingRegistry.registerEntityRenderingHandler(EntityArrowVampier.class, new RenderArrows(rm, new ResourceLocation("switchbow:textures/models/arrows/ArrowVampier.png")));
      RenderingRegistry.registerEntityRenderingHandler(EntityArrowWither.class, new RenderArrows(rm, new ResourceLocation("switchbow:textures/models/arrows/ArrowWither.png")));
      RenderingRegistry.registerEntityRenderingHandler(EntityArrowTorch.class, new RenderArrows(rm, new ResourceLocation("switchbow:textures/models/arrows/ArrowTorch.png")));
      RenderingRegistry.registerEntityRenderingHandler(EntityArrowSplit.class, new RenderArrowSplit(rm, new ResourceLocation("switchbow:textures/models/arrows/ArrowSplit.png")));
      RenderingRegistry.registerEntityRenderingHandler(EntityArrowUnderwater.class, new RenderArrows(rm, new ResourceLocation("switchbow:textures/models/arrows/ArrowUnderwater.png")));
      RenderingRegistry.registerEntityRenderingHandler(EntityArrowDiamond.class, new RenderArrows(rm, new ResourceLocation("switchbow:textures/models/arrows/ArrowDiamond.png")));
      RenderingRegistry.registerEntityRenderingHandler(EntityArrowLove.class, new RenderArrows(rm, new ResourceLocation("switchbow:textures/models/arrows/ArrowLove.png")));
      RenderingRegistry.registerEntityRenderingHandler(EntityArrowFire.class, new RenderArrows(rm, new ResourceLocation("switchbow:textures/models/arrows/ArrowFire.png")));
      RenderingRegistry.registerEntityRenderingHandler(EntityArrowBone.class, new RenderArrows(rm, new ResourceLocation("switchbow:textures/models/arrows/ArrowBone.png")));
      RenderingRegistry.registerEntityRenderingHandler(EntityArrowFirework.class, new RenderArrows(rm, new ResourceLocation("switchbow:textures/models/arrows/ArrowFirework.png")));
      RenderingRegistry.registerEntityRenderingHandler(EntityArrowSplitLove.class, new RenderArrows(rm, new ResourceLocation("switchbow:textures/models/arrows/ArrowSplitLove.png")));
      RenderingRegistry.registerEntityRenderingHandler(EntityArrowLuck.class, new RenderArrows(rm, new ResourceLocation("switchbow:textures/models/arrows/ArrowLuck.png")));
      RenderingRegistry.registerEntityRenderingHandler(EntityArrowRedstone.class, new RenderArrows(rm, new ResourceLocation("switchbow:textures/models/arrows/ArrowRedstone.png")));
      RenderingRegistry.registerEntityRenderingHandler(EntityArrowBurial.class, new RenderArrows(rm, new ResourceLocation("switchbow:textures/models/arrows/ArrowBurial.png")));
      RenderingRegistry.registerEntityRenderingHandler(EntityArrowChorus.class, new RenderArrows(rm, new ResourceLocation("switchbow:textures/models/arrows/ArrowChorus.png")));
      RenderingRegistry.registerEntityRenderingHandler(EntityArrowSprinkler.class, new RenderArrows(rm, new ResourceLocation("switchbow:textures/models/arrows/ArrowSprinkler.png")));
      RenderingRegistry.registerEntityRenderingHandler(EntityArrowDragonBreath.class, new RenderArrows(rm, new ResourceLocation("switchbow:textures/models/arrows/ArrowDragonBreath.png")));
      RenderingRegistry.registerEntityRenderingHandler(EntityArrowTriple.class, new RenderArrows(rm, new ResourceLocation("textures/entity/arrow.png")));
      RenderingRegistry.registerEntityRenderingHandler(EntityArrowProtector.class, new RenderArrows(rm, new ResourceLocation("switchbow:textures/models/arrows/ArrowProtector.png")));
      RenderingRegistry.registerEntityRenderingHandler(EntityArrowKnockback.class, new RenderArrows(rm, new ResourceLocation("switchbow:textures/models/arrows/ArrowKnockback.png")));
      RenderingRegistry.registerEntityRenderingHandler(EntityArrowBouncy.class, new RenderArrows(rm, new ResourceLocation("switchbow:textures/models/arrows/ArrowBouncy.png")));
      RenderingRegistry.registerEntityRenderingHandler(EntityArrowoftheSearcher.class, new RenderArrows(rm, new ResourceLocation("switchbow:textures/models/arrows/ArrowoftheSearcher.png")));
      RenderingRegistry.registerEntityRenderingHandler(EntityArrowPiercing.class, new RenderArrows(rm, new ResourceLocation("switchbow:textures/models/arrows/ArrowPiercing.png")));
      RenderingRegistry.registerEntityRenderingHandler(EntityArrowLightningBoltStorm.class, new RenderArrows(rm, new ResourceLocation("switchbow:textures/models/arrows/ArrowLightningBolt.png")));
      RenderingRegistry.registerEntityRenderingHandler(EntityArrowBoneAOE.class, new RenderArrows(rm, new ResourceLocation("switchbow:textures/models/arrows/ArrowBone.png")));
      RenderingRegistry.registerEntityRenderingHandler(EntityArrowSplitUpgrade.class, new RenderArrowSplitUpgrade(rm, new ResourceLocation("switchbow:textures/models/arrows/ArrowSplit.png")));
      RenderingRegistry.registerEntityRenderingHandler(EntityArrowBurialAOE.class, new RenderArrows(rm, new ResourceLocation("switchbow:textures/models/arrows/ArrowBurial.png")));
      RenderingRegistry.registerEntityRenderingHandler(EntityArrowFireUpgrade.class, new RenderArrows(rm, new ResourceLocation("switchbow:textures/models/arrows/ArrowFire.png")));
      RenderingRegistry.registerEntityRenderingHandler(EntityArrowLuckUpgrade.class, new RenderArrows(rm, new ResourceLocation("switchbow:textures/models/arrows/ArrowLuck.png")));
      RenderingRegistry.registerEntityRenderingHandler(EntityArrowAirUpgrade.class, new RenderArrows(rm, new ResourceLocation("switchbow:textures/models/arrows/ArrowAir.png")));
      RenderingRegistry.registerEntityRenderingHandler(EntityArrowSplitLoveUpgrade.class, new RenderArrows(rm, new ResourceLocation("switchbow:textures/models/arrows/ArrowSplitLove.png")));
      ClientRegistry.bindTileEntitySpecialRenderer(TileEntityPodest.class, new RenderTileEntityPodest());
      RenderingRegistry.registerEntityRenderingHandler(EntityLittleIronGolem.class, new RenderLittleIronGolem(rm));
      RenderingRegistry.registerEntityRenderingHandler(EntityLightningBoltSwitchBow.class, new RenderLightnigBoltSwitchBow(rm));
   }

   public void SpawnParticle(World worldIn, double posx, double posy, double posz, double motionx, double motiony, double motionz, double EntityPosx, double EntityPosz, int Winkel) {
      FMLClientHandler.instance().getClient().effectRenderer.addEffect(new FireFX(worldIn, posx, posy, posz, motionx, motiony, motionz, EntityPosx, EntityPosz, Winkel));
   }

   public void SpawnParticleFreeze(double posX, double posY, double posZ, double d0, double d1, double d2) {
      Minecraft.getMinecraft().world.spawnParticle(EnumParticleTypes.SPELL_MOB, posX, posY, posZ, d0, d1, d2, new int[0]);
   }

   public void SpawnParticleWater(double posX, double posY, double posZ, double d0, double d1, double d2) {
      Minecraft.getMinecraft().world.spawnParticle(EnumParticleTypes.WATER_DROP, posX, posY, posZ, d0, d1, d2, new int[0]);
   }

   public void SpawnPodestParticle(World worldIn, double posx, double posy, double posz, double motionx, double motiony, double motionz) {
      FMLClientHandler.instance().getClient().effectRenderer.addEffect(new TransformationFX(Minecraft.getMinecraft().world, posx, posy, posz, motionx, motiony, motionz));
   }

   public void registerItemModel(Item item) {
      ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation("switchbow:" + item.getUnlocalizedName().substring(5)));
   }
}
