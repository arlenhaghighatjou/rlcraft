package shadows.placebo.util;

import java.io.File;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementList;
import net.minecraft.advancements.AdvancementManager;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.AdvancementTreeNode;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.DisplayInfo;
import net.minecraft.advancements.FrameType;
import net.minecraft.advancements.ICriterionInstance;
import net.minecraft.advancements.Advancement.Builder;
import net.minecraft.advancements.critereon.DamageSourcePredicate;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.KilledTrigger.Instance;
import net.minecraft.command.FunctionObject.CacheableFunction;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

public class AdvancementHelper {
   public static final AdvancementList ADVANCEMENTS = (AdvancementList)ReflectionHelper.getPrivateValue(AdvancementManager.class, (Object)null, new String[]{"ADVANCEMENT_LIST", "ADVANCEMENT_LIST"});
   private static final Map<ResourceLocation, Builder> TO_REGISTER = new HashMap();
   private static Constructor<Builder> ctr = null;

   public AdvancementHelper() {
   }

   public static void preInit(FMLPreInitializationEvent e) {
      reload();
      MinecraftForge.EVENT_BUS.register(new AdvancementHelper());
   }

   public static void addAdvancements() {
      (new AdvancementHelper.AdvancementBuilderBuilder("placebo", "test")).setDisplayInfo(createDisplay("test.title", "test.desc", new ItemStack(Items.APPLE), (String)null, FrameType.TASK, true, true, false)).setRewards(createRewards(50.0F)).setParent("adventure/root").addCriteria("test_crit", new Instance(CriteriaTriggers.PLAYER_KILLED_ENTITY.getId(), EntityPredicate.ANY, DamageSourcePredicate.ANY)).addConditionGroup("test_crit").build().register();
   }

   @SubscribeEvent
   public void worldServerCaps(AttachCapabilitiesEvent<World> e) {
      World world = (World)e.getObject();
      ReflectionHelper.setPrivateValue(World.class, world, new AdvancementHelper.HackedAdvancementManager(new File(new File(world.getSaveHandler().getWorldDirectory(), "data"), "advancements")), new String[]{"advancementManager", "advancementManager"});
   }

   public static void reload() {
      TO_REGISTER.clear();
      addAdvancements();
      ADVANCEMENTS.loadAdvancements(TO_REGISTER);

      for(Advancement advancement : ADVANCEMENTS.getRoots()) {
         if (advancement.getDisplay() != null) {
            AdvancementTreeNode.layout(advancement);
         }
      }

   }

   public static Builder genBuilder(@Nullable ResourceLocation parent, @Nullable DisplayInfo info, AdvancementRewards rewards, Map<String, Criterion> criteria, String[][] requirements) {
      if (ctr == null) {
         try {
            ctr = Builder.class.getDeclaredConstructors()[0];
            ctr.setAccessible(true);
         } catch (Exception var7) {
            throw new RuntimeException("Failed to access Advancement.Builder constructor!");
         }
      }

      try {
         return (Builder)ctr.newInstance(parent, info, rewards, criteria, requirements);
      } catch (Exception var6) {
         throw new RuntimeException("Failed instantiate Advancement.Builder!");
      }
   }

   public static DisplayInfo createDisplay(String langKey, String descKey, ItemStack stack, String background, FrameType type, boolean toast, boolean announce, boolean hidden) {
      return new DisplayInfo(stack, new TextComponentTranslation(langKey, new Object[0]), new TextComponentTranslation(descKey, new Object[0]), background != null && !background.isEmpty() ? new ResourceLocation(background) : null, type, toast, announce, hidden);
   }

   public static AdvancementRewards createRewards(float experience, String[] lootTables, String[] recipeNames, CacheableFunction func) {
      ResourceLocation[] tables = new ResourceLocation[lootTables.length];
      ResourceLocation[] recipes = new ResourceLocation[recipeNames.length];

      for(int i = 0; i < lootTables.length; ++i) {
         tables[i] = new ResourceLocation(lootTables[i]);
      }

      for(int i = 0; i < recipeNames.length; ++i) {
         recipes[i] = new ResourceLocation(recipeNames[i]);
      }

      return new AdvancementRewards(0, tables, recipes, func);
   }

   public static AdvancementRewards createRewards(float experience, String[] lootTables, String[] recipeNames) {
      return createRewards(experience, lootTables, recipeNames, CacheableFunction.EMPTY);
   }

   public static AdvancementRewards createRewards(float experience, String[] lootTables) {
      return createRewards(experience, lootTables, new String[0]);
   }

   public static AdvancementRewards createRewards(float experience) {
      return createRewards(experience, new String[0]);
   }

   public static void registerAdvancement(ResourceLocation name, Builder b) {
      if (TO_REGISTER.get(name) == null) {
         TO_REGISTER.put(name, b);
      } else {
         throw new RuntimeException("Attempted to register duplicate advancement! Name: " + name.toString());
      }
   }

   public static class AdvancementBuilderBuilder {
      private DisplayInfo info;
      private AdvancementRewards rewards;
      private Map<String, Criterion> criteria;
      private ResourceLocation parent;
      private final List<String[]> REQUIRED;
      private final ResourceLocation name;

      public AdvancementBuilderBuilder(ResourceLocation name) {
         this.criteria = new HashMap();
         this.REQUIRED = new ArrayList();
         this.name = name;
      }

      public AdvancementBuilderBuilder(String modid, String name) {
         this(new ResourceLocation(modid, name));
      }

      public AdvancementHelper.AdvancementBuilderBuilder setDisplayInfo(DisplayInfo info) {
         if (this.info != null) {
            throw new RuntimeException("Tried to set display info when it was already set!");
         } else {
            this.info = info;
            return this;
         }
      }

      public AdvancementHelper.AdvancementBuilderBuilder addCriteria(String name, ICriterionInstance criterion) {
         this.criteria.put(name, new Criterion(criterion));
         return this;
      }

      public AdvancementHelper.AdvancementBuilderBuilder setRewards(AdvancementRewards rewards) {
         if (this.rewards != null) {
            throw new RuntimeException("Tried to set rewards when it was already set!");
         } else {
            this.rewards = rewards;
            return this;
         }
      }

      public AdvancementHelper.AdvancementBuilderBuilder setParent(String advancement) {
         this.parent = new ResourceLocation(advancement);
         return this;
      }

      public AdvancementHelper.AdvancementBuilderBuilder addConditionGroup(String... group) {
         this.REQUIRED.add(group);
         return this;
      }

      public AdvancementHelper.BuilderResult build() {
         String[][] reqs = new String[this.REQUIRED.size()][];

         for(int i = 0; i < this.REQUIRED.size(); ++i) {
            reqs[i] = (String[])this.REQUIRED.get(i);
         }

         return new AdvancementHelper.BuilderResult(this.name, AdvancementHelper.genBuilder(this.parent, this.info, this.rewards, this.criteria, reqs));
      }
   }

   public static class BuilderResult {
      public final Builder builder;
      public final ResourceLocation name;

      public BuilderResult(ResourceLocation name, Builder builder) {
         this.name = name;
         this.builder = builder;
      }

      public void register() {
         AdvancementHelper.registerAdvancement(this.name, this.builder);
      }
   }

   public static class HackedAdvancementManager extends AdvancementManager {
      public HackedAdvancementManager(File advancementsDirIn) {
         super(advancementsDirIn);
      }

      public void reload() {
         super.reload();
         AdvancementHelper.reload();
      }
   }
}
