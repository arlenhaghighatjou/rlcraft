package vazkii.quark.client.feature;

import com.google.common.base.Supplier;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Multimaps;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.entity.passive.EntityCow;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vazkii.quark.base.module.Feature;
import vazkii.quark.client.render.random.RenderChickenRandom;
import vazkii.quark.client.render.random.RenderCowRandom;
import vazkii.quark.client.render.random.RenderPigRandom;

public class RandomAnimalTextures extends Feature {
   private static ListMultimap<RandomAnimalTextures.RandomTextureType, ResourceLocation> textures;
   private static Map<RandomAnimalTextures.RandomTextureType, ResourceLocation> shinyTextures;
   private static final int COW_COUNT = 10;
   private static final int PIG_COUNT = 4;
   private static final int CHICKEN_COUNT = 6;
   private static final int CHICK_COUNT = 3;
   public static boolean enableCow;
   public static boolean enablePig;
   public static boolean enableChicken;
   public static boolean enableChick;
   public static int shinyAnimalChance;

   public RandomAnimalTextures() {
   }

   public void setupConfig() {
      enableCow = this.loadPropBool("Enable Cow", "", true);
      enablePig = this.loadPropBool("Enable Pig", "", true);
      enableChicken = this.loadPropBool("Enable Chicken", "", true);
      enableChick = this.loadPropBool("Enable Chick", "", true);
      shinyAnimalChance = this.loadPropInt("Shiny Animal Chance", "The chance for an animal to have a special \"Shiny\" skin, like a shiny pokemon. This is 1 in X. Set to 0 to disable.", 1024);
   }

   @SideOnly(Side.CLIENT)
   public void preInitClient() {
      textures = Multimaps.newListMultimap(new EnumMap(RandomAnimalTextures.RandomTextureType.class), ArrayList::new);
      shinyTextures = new HashMap();
      registerTextures(RandomAnimalTextures.RandomTextureType.COW, 10, new ResourceLocation("textures/entity/cow/cow.png"));
      registerTextures(RandomAnimalTextures.RandomTextureType.PIG, 4, new ResourceLocation("textures/entity/pig/pig.png"));
      registerTextures(RandomAnimalTextures.RandomTextureType.CHICKEN, 6, new ResourceLocation("textures/entity/chicken.png"));
      registerTextures(RandomAnimalTextures.RandomTextureType.CHICK, 3, (ResourceLocation)null);
      registerOverride(EntityCow.class, RenderCowRandom.factory(), enableCow);
      registerOverride(EntityPig.class, RenderPigRandom.factory(), enablePig);
      registerOverride(EntityChicken.class, RenderChickenRandom.factory(), enableChicken || enableChick);
   }

   @SideOnly(Side.CLIENT)
   public static ResourceLocation getRandomTexture(Entity e, RandomAnimalTextures.RandomTextureType type) {
      return getRandomTexture(e, type, true);
   }

   @SideOnly(Side.CLIENT)
   public static ResourceLocation getRandomTexture(Entity e, RandomAnimalTextures.RandomTextureType type, boolean choose) {
      List<ResourceLocation> styles = textures.get(type);
      if (!choose) {
         return (ResourceLocation)styles.get(styles.size() - 1);
      } else {
         UUID id = e.getUniqueID();
         long most = id.getMostSignificantBits();
         if (shinyAnimalChance > 0 && most % (long)shinyAnimalChance == 0L) {
            return (ResourceLocation)shinyTextures.get(type);
         } else {
            int choice = Math.abs((int)(most % (long)styles.size()));
            return (ResourceLocation)styles.get(choice);
         }
      }
   }

   @SideOnly(Side.CLIENT)
   private static void registerTextures(RandomAnimalTextures.RandomTextureType type, int count, ResourceLocation vanilla) {
      String name = type.name().toLowerCase();

      for(int i = 1; i < count + 1; ++i) {
         textures.put(type, new ResourceLocation("quark", String.format("textures/entity/random/%s%d.png", name, i)));
      }

      if (vanilla != null) {
         textures.put(type, vanilla);
      }

      shinyTextures.put(type, new ResourceLocation("quark", String.format("textures/entity/random/%s_shiny.png", name)));
   }

   @SideOnly(Side.CLIENT)
   private static <T extends Entity> void registerOverride(Class<T> clazz, IRenderFactory<? super T> factory, boolean enabled) {
      if (enabled) {
         RenderingRegistry.registerEntityRenderingHandler(clazz, factory);
      }

   }

   public boolean requiresMinecraftRestartToEnable() {
      return true;
   }

   public static enum RandomTextureType {
      COW,
      PIG,
      CHICKEN,
      CHICK;

      private RandomTextureType() {
      }
   }
}
