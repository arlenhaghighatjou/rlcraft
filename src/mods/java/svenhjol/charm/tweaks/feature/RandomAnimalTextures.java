package svenhjol.charm.tweaks.feature;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.EntityOcelot;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import svenhjol.charm.tweaks.render.RenderOcelotTextures;
import svenhjol.charm.tweaks.render.RenderWolfTextures;
import svenhjol.meson.Feature;
import svenhjol.meson.iface.IMesonEnum;

public class RandomAnimalTextures extends Feature {
   public static Map<RandomAnimalTextures.SET, String[]> sets = new HashMap();
   public static boolean enableCats;
   public static boolean enableWolves;

   public RandomAnimalTextures() {
   }

   public String getDescription() {
      return "Like Quark's random animal textures, this feature lets wolves and cats spawn with additional skin textures.";
   }

   public void configure() {
      super.configure();
      enableCats = this.propBoolean("Random cat textures", "Enable random textures depending on the cat type, borrowed lovingly from Minecraft 1.14.", true);
      enableWolves = this.propBoolean("Random wolf textures", "Enable random wolf textures with brown, grey and black variants.", true);
      String[] ocelots = this.propStringList("Ocelot textures", "Textures to use when rendering Ocelots.", new String[]{"minecraft:ocelot"});
      String[] redCats = this.propStringList("Red cat textures", "Textures to use when rendering Red cats (Cat Type 1).", new String[]{"minecraft:red", "charm:persian", "charm:tabby"});
      String[] blackCats = this.propStringList("Black cat textures", "Textures to use when rendering Black cats (Cat Type 2).", new String[]{"minecraft:black", "charm:all_black"});
      String[] otherCats = this.propStringList("Siamese / other cat textures", "Textures to use when rendering Siamese and other cats (Cat Type 3).", new String[]{"minecraft:siamese", "charm:calico", "charm:white", "charm:ragdoll", "charm:british_shorthair", "charm:jellie"});
      String[] wolves = this.propStringList("Wolf textures", "Textures to use when rendering wolves.", new String[]{"minecraft:wolf", "charm:brown", "charm:grey", "charm:black"});
      sets.put(RandomAnimalTextures.SET.OCELOTS, ocelots);
      sets.put(RandomAnimalTextures.SET.REDCATS, redCats);
      sets.put(RandomAnimalTextures.SET.BLACKCATS, blackCats);
      sets.put(RandomAnimalTextures.SET.OTHERCATS, otherCats);
      sets.put(RandomAnimalTextures.SET.WOLVES, wolves);
   }

   public void preInitClient(FMLPreInitializationEvent event) {
      if (enableWolves) {
         RenderingRegistry.registerEntityRenderingHandler(EntityWolf.class, RenderWolfTextures.factory());
      }

      if (enableCats) {
         RenderingRegistry.registerEntityRenderingHandler(EntityOcelot.class, RenderOcelotTextures.factory());
      }

   }

   @SideOnly(Side.CLIENT)
   public static ResourceLocation getWolfTexture(EntityWolf entity) {
      String[] set = (String[])sets.get(RandomAnimalTextures.SET.WOLVES);
      String tex;
      if (set.length > 0) {
         tex = getRandomTexture(entity, set);
      } else {
         tex = "minecraft:wolf";
      }

      if (entity.isTamed()) {
         tex = tex + "_tame";
      } else if (entity.isAngry()) {
         tex = tex + "_angry";
      }

      return getTextureFromString(RandomAnimalTextures.MOB.WOLF, tex);
   }

   @SideOnly(Side.CLIENT)
   public static ResourceLocation getOcelotTexture(EntityOcelot entity) {
      String[] set;
      String defTex;
      switch(entity.getTameSkin()) {
      case 0:
         set = (String[])sets.get(RandomAnimalTextures.SET.OCELOTS);
         defTex = "minecraft:ocelot";
         break;
      case 1:
         set = (String[])sets.get(RandomAnimalTextures.SET.REDCATS);
         defTex = "minecraft:red";
         break;
      case 2:
         set = (String[])sets.get(RandomAnimalTextures.SET.BLACKCATS);
         defTex = "minecraft:black";
         break;
      default:
         set = (String[])sets.get(RandomAnimalTextures.SET.OTHERCATS);
         defTex = "minecraft:siamese";
      }

      String tex;
      if (set.length > 0) {
         tex = getRandomTexture(entity, set);
      } else {
         tex = defTex;
      }

      return getTextureFromString(RandomAnimalTextures.MOB.CAT, tex);
   }

   public static String getRandomTexture(Entity entity, String[] set) {
      UUID id = entity.getUniqueID();
      int choice = Math.abs((int)(id.getMostSignificantBits() % (long)set.length));
      return set[choice];
   }

   public static ResourceLocation getTextureFromString(RandomAnimalTextures.MOB mob, String texture) {
      String prefix = "textures/entity/" + mob.getName() + "/";
      String[] a = texture.split(":");
      return new ResourceLocation(a[0], prefix + a[1] + ".png");
   }

   public static enum MOB implements IMesonEnum {
      CAT,
      WOLF;

      private MOB() {
      }
   }

   public static enum SET {
      OCELOTS,
      REDCATS,
      BLACKCATS,
      OTHERCATS,
      WOLVES;

      private SET() {
      }
   }
}
