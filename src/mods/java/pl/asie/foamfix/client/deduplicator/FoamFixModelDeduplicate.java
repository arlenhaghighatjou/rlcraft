package pl.asie.foamfix.client.deduplicator;

import gnu.trove.map.TObjectIntMap;
import gnu.trove.map.hash.TObjectIntHashMap;
import java.io.File;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.function.ToIntFunction;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.model.MultipartBakedModel;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.lax1dude.eaglercraft.v1_8.opengl.VertexFormat;
import net.minecraft.stats.StatBase;
import net.minecraft.stats.StatList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.fml.common.ProgressManager;
import net.minecraftforge.fml.common.ProgressManager.ProgressBar;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import pl.asie.foamfix.FoamFix;
import pl.asie.foamfix.client.FoamyMultipartBakedModel;
import pl.asie.foamfix.shared.FoamFixShared;
import pl.asie.foamfix.util.MethodHandleHelper;

public final class FoamFixModelDeduplicate {
   public static final FoamFixModelDeduplicate INSTANCE = new FoamFixModelDeduplicate();

   private FoamFixModelDeduplicate() {
   }

   private void debugCountModels(ModelBakeEvent event) {
      List<String> bmNames = new ArrayList();
      TObjectIntMap<String> bmCountMod = new TObjectIntHashMap();
      TObjectIntMap<String> bmCountVariant = new TObjectIntHashMap();

      for(ModelResourceLocation loc : event.getModelRegistry().getKeys()) {
         bmNames.add(loc.toString());
         bmCountMod.adjustOrPutValue(loc.getResourceDomain(), 1, 1);
         bmCountVariant.adjustOrPutValue(loc.getResourceDomain() + ":" + loc.getResourcePath(), 1, 1);
      }

      List<String> bmCountModKeys = new ArrayList(bmCountMod.keySet());
      List<String> bmCountVariantKeys = new ArrayList(bmCountVariant.keySet());
      bmNames.sort(Comparator.naturalOrder());
      Objects.requireNonNull(bmCountMod);
      bmCountModKeys.sort(Comparator.comparingInt(bmCountMod::get).reversed());
      Objects.requireNonNull(bmCountVariant);
      bmCountVariantKeys.sort(Comparator.comparingInt(bmCountVariant::get).reversed());

      try {
         File outFile = new File("foamfixBakedModelNames.txt");
         PrintWriter writer = new PrintWriter(outFile);

         for(String s : bmNames) {
            writer.println(s);
         }

         writer.close();
         outFile = new File("foamfixBakedModelCountsPerMod.txt");
         writer = new PrintWriter(outFile);

         for(String s : bmCountModKeys) {
            writer.println(s + ": " + bmCountMod.get(s));
         }

         writer.close();
         outFile = new File("foamfixBakedModelCountsPerBlock.txt");
         writer = new PrintWriter(outFile);

         for(String s : bmCountVariantKeys) {
            writer.println(s + ": " + bmCountVariant.get(s));
         }

         writer.close();
      } catch (Exception var11) {
         var11.printStackTrace();
      }

   }

   @SubscribeEvent(
      priority = EventPriority.LOW
   )
   public void onModelBake(ModelBakeEvent event) {
      if (FoamFixShared.config.dbgCountModels) {
         this.debugCountModels(event);
      }

      Map<ResourceLocation, IModel> cache;
      try {
         cache = MethodHandleHelper.findFieldGetter(ModelLoaderRegistry.class, "cache").invoke();
      } catch (Throwable var16) {
         var16.printStackTrace();
         cache = Collections.emptyMap();
      }

      if (FoamFixShared.config.clWipeModelCache) {
         int itemsCleared = 0;
         FoamFix.getLogger().info("Clearing ModelLoaderRegistry cache (" + cache.size() + " items)...");
         int cacheSize = cache.size();
         cache.entrySet().removeIf((e) -> {
            ResourceLocation r = (ResourceLocation)e.getKey();
            if (!"minecraft".equals(r.getResourceDomain()) && !"fml".equals(r.getResourceDomain()) && !"forge".equals(r.getResourceDomain())) {
               return true;
            } else if (r.getResourcePath().endsWith("/generated")) {
               return false;
            } else {
               return !r.getResourcePath().startsWith("builtin/");
            }
         });
         itemsCleared = itemsCleared + (cacheSize - cache.size());
         FoamFix.getLogger().info("Cleared " + itemsCleared + " objects.");
         cache = Collections.emptyMap();
      }

      if (FoamFixShared.config.geDeduplicate || FoamFixShared.config.clDeduplicateModels) {
         Deduplicator deduplicator = new Deduplicator();
         deduplicator.maxRecursion = FoamFixShared.config.clDeduplicateRecursionLevel;
         deduplicator.addResourceLocation(ForgeRegistries.BLOCKS.getKeys());
         deduplicator.addResourceLocation(ForgeRegistries.ITEMS.getKeys());
         if (FoamFixShared.config.geDeduplicate) {
            FoamFix.getLogger().info("Deduplicating...");

            try {
               if (cache != null) {
                  int bakeBarLength = 2;
                  if (FoamFixShared.config.clDeduplicateIModels) {
                     bakeBarLength += cache.size();
                  }

                  ProgressBar bakeBar = ProgressManager.push("FoamFix: deduplicating", bakeBarLength);

                  try {
                     bakeBar.step("Vertex formats");

                     for(Field f : DefaultVertexFormats.class.getDeclaredFields()) {
                        if (f.getType() == VertexFormat.class) {
                           f.setAccessible(true);
                           deduplicator.deduplicateObject(f.get((Object)null), 0);
                        }
                     }
                  } catch (Exception var18) {
                     ;
                  }

                  if (FoamFixShared.config.clDeduplicateIModels) {
                     for(ResourceLocation loc : cache.keySet()) {
                        IModel model = (IModel)cache.get(loc);
                        String modelName = loc.toString();
                        bakeBar.step(String.format("[%s]", modelName));

                        try {
                           deduplicator.addResourceLocation(loc);
                           deduplicator.deduplicateObject(model, 0);
                        } catch (Exception var15) {
                           ;
                        }
                     }
                  }

                  try {
                     bakeBar.step("Stats");

                     for(Field f : StatList.class.getDeclaredFields()) {
                        if (f.getType() == StatBase[].class) {
                           f.setAccessible(true);

                           for(StatBase statBase : (StatBase[])f.get((Object)null)) {
                              deduplicator.deduplicateObject(statBase, 0);
                           }
                        }
                     }

                     for(StatBase statBase : StatList.ALL_STATS) {
                        deduplicator.deduplicateObject(statBase, 0);
                     }
                  } catch (Exception var17) {
                     ;
                  }

                  ProgressManager.pop(bakeBar);
               }
            } catch (Throwable var19) {
               var19.printStackTrace();
            }

            if (FoamFixShared.config.clDeduplicateModels) {
               int stepCounter = 0;
               int stepEvery = FoamFixShared.config.clDeduplicateStepEvery;
               int stepCount = (event.getModelRegistry().getKeys().size() + (stepEvery - 1)) / stepEvery;
               ProgressBar bakeBar = ProgressManager.push("FoamFix: deduplicating", stepCount);
               deduplicator.maxRecursion = FoamFixShared.config.clDeduplicateRecursionLevel;
               FoamFix.getLogger().info("Deduplicating models...");

               for(ModelResourceLocation loc : event.getModelRegistry().getKeys()) {
                  IBakedModel model = (IBakedModel)event.getModelRegistry().getObject(loc);
                  String modelName = loc.toString();
                  if (stepEvery == 1 || stepCounter % stepEvery == 0) {
                     bakeBar.step(modelName);
                  }

                  ++stepCounter;
                  if (model.getClass() == MultipartBakedModel.class) {
                     ++deduplicator.successfuls;
                     model = new FoamyMultipartBakedModel((MultipartBakedModel)model);
                  }

                  try {
                     deduplicator.addResourceLocation(loc);
                     event.getModelRegistry().putObject(loc, (IBakedModel)deduplicator.deduplicateObject(model, 0));
                  } catch (Exception var14) {
                     ;
                  }
               }

               ProgressManager.pop(bakeBar);
               FoamFix.getLogger().info("Deduplicated " + deduplicator.successfuls + " (+ " + deduplicator.successfulTrims + ") objects.");
            }
         }
      }

   }
}
