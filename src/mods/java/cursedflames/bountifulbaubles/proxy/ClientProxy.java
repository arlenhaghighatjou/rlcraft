package cursedflames.bountifulbaubles.proxy;

import cursedflames.bountifulbaubles.client.layer.BountfulRenderLayer;
import cursedflames.bountifulbaubles.client.model.ModelCrownGold;
import cursedflames.bountifulbaubles.client.model.ModelSunglasses;
import cursedflames.bountifulbaubles.client.particle.ParticleGradient;
import cursedflames.bountifulbaubles.client.render.RenderFlare;
import cursedflames.bountifulbaubles.entity.EntityFlare;
import cursedflames.bountifulbaubles.entity.ThrowableAdvancedRenderFactory;
import cursedflames.bountifulbaubles.entity.ThrowableDefaultRenderFactory;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ClientProxy implements ISideProxy {
   private static final ModelCrownGold modelCrownGold = new ModelCrownGold();
   private static final ModelSunglasses modelSunglasses1 = new ModelSunglasses();
   private static final ModelSunglasses modelSunglasses2 = new ModelSunglasses();

   public ClientProxy() {
   }

   public String translateWithArgs(String string, Object... args) {
      return I18n.format(string, args);
   }

   public String translate(String string) {
      return net.minecraft.util.text.translation.I18n.translateToLocal(string);
   }

   public boolean hasTranslationKey(String string) {
      return I18n.hasKey(string);
   }

   public void spawnParticleGradient(World world, double x, double y, double z, float r, float g, float b, float rT, float gT, float bT, int maxAge) {
      Minecraft.getMinecraft().effectRenderer.addEffect(new ParticleGradient(world, x, y, z, r, g, b, rT, gT, bT, maxAge));
   }

   public void spawnParticleGradient(World world, double x, double y, double z, float r, float g, float b, float rT, float gT, float bT, int maxAge, double velX, double velY, double velZ) {
      Minecraft.getMinecraft().effectRenderer.addEffect(new ParticleGradient(world, x, y, z, r, g, b, rT, gT, bT, maxAge, velX, velY, velZ));
   }

   public ModelBiped getArmorModel(String modelName) {
      if (modelName.equals("crownGold")) {
         return modelCrownGold;
      } else if (modelName.equals("sunglasses1")) {
         return modelSunglasses1;
      } else {
         return modelName.equals("sunglasses2") ? modelSunglasses2 : null;
      }
   }

   public <T extends Entity> void registerWithRenderer(String name, Class<T> c, Item i, int id) {
      RenderingRegistry.registerEntityRenderingHandler(c, new ThrowableDefaultRenderFactory(i));
   }

   public <T extends Entity> void registerWithRenderer(String name, Class<T> c, ResourceLocation texture, int id) {
      RenderingRegistry.registerEntityRenderingHandler(c, new ThrowableAdvancedRenderFactory(texture));
   }

   public void registerEntityRenderingHandlers() {
      RenderingRegistry.registerEntityRenderingHandler(EntityFlare.class, RenderFlare::new);
   }

   @SideOnly(Side.CLIENT)
   public void addRenderLayer() {
      Map<String, RenderPlayer> skinMap = Minecraft.getMinecraft().getRenderManager().getSkinMap();

      for(Entry<String, RenderPlayer> map : skinMap.entrySet()) {
         if (((String)map.getKey()).contentEquals("slim")) {
            RenderPlayer render = (RenderPlayer)map.getValue();
            render.addLayer(new BountfulRenderLayer(true, render));
         } else {
            RenderPlayer render = (RenderPlayer)map.getValue();
            render.addLayer(new BountfulRenderLayer(false, render));
         }
      }

   }
}
