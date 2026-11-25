package pl.asie.foamfix.client.deduplicator;

import com.google.common.base.Joiner;
import com.google.common.base.Predicate;
import com.google.common.base.Strings;
import com.google.common.cache.Cache;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.EnumBiMap;
import com.google.common.collect.ImmutableBiMap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSortedSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import com.google.common.collect.SortedSetMultimap;
import com.google.common.collect.UnmodifiableIterator;
import com.google.common.collect.ImmutableList.Builder;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.invoke.MethodHandles.Lookup;
import java.lang.ref.Reference;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import javax.vecmath.Matrix4f;
import javax.vecmath.Vector3f;
import javax.vecmath.Vector4f;
import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.audio.SoundManager;
import net.minecraft.client.renderer.BlockModelShapes;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.BlockFaceUV;
import net.minecraft.client.renderer.block.model.BlockPartFace;
import net.minecraft.client.renderer.block.model.BlockPartRotation;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.block.model.ModelBlock;
import net.minecraft.client.renderer.block.model.ModelManager;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.model.SimpleBakedModel;
import net.minecraft.client.renderer.block.model.WeightedBakedModel;
import net.minecraft.client.renderer.block.model.multipart.Multipart;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.renderer.vertex.VertexFormatElement;
import net.minecraft.client.resources.IResourcePack;
import net.minecraft.client.resources.LanguageManager;
import net.minecraft.client.resources.SimpleReloadableResourceManager;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.math.BlockPos.MutableBlockPos;
import net.minecraft.util.registry.IRegistry;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentKeybind;
import net.minecraft.util.text.TextComponentScore;
import net.minecraft.util.text.TextComponentSelector;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.client.model.BakedItemModel;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.PerspectiveMapWrapper;
import net.minecraftforge.client.model.animation.AnimationItemOverrideList;
import net.minecraftforge.client.model.animation.ModelBlockAnimation;
import net.minecraftforge.client.model.pipeline.UnpackedBakedQuad;
import net.minecraftforge.common.model.TRSRTransformation;
import net.minecraftforge.common.property.IUnlistedProperty;
import net.minecraftforge.fml.common.IWorldGenerator;
import org.apache.logging.log4j.Logger;
import pl.asie.foamfix.client.FoamyItemLayerModel;
import pl.asie.foamfix.client.FoamyMultipartBakedModel;
import pl.asie.foamfix.client.IDeduplicatingStorage;
import pl.asie.foamfix.client.condition.FoamyConditionAnd;
import pl.asie.foamfix.client.condition.FoamyConditionOr;
import pl.asie.foamfix.client.condition.FoamyConditionPropertyValue;
import pl.asie.foamfix.shared.FoamFixShared;
import pl.asie.foamfix.util.DeduplicatingStorageTrove;
import pl.asie.foamfix.util.HashingStrategies;
import pl.asie.foamfix.util.MethodHandleHelper;

public class Deduplicator {
   private static final Map<Class, Boolean> SHOULD_PROCESS_CLASS = new IdentityHashMap();
   private static final Set<Class> IMMUTABLE_CLASS = Sets.newIdentityHashSet();
   private static final Set<Class> TRIM_ARRAYS_CLASSES = Sets.newIdentityHashSet();
   private static final Style STYLE_EMPTY = new Style();
   private static final MethodHandle FIELD_UNPACKED_DATA_GETTER = MethodHandleHelper.findFieldGetter(UnpackedBakedQuad.class, "unpackedData");
   private static final MethodHandle IPAM_MW_TRANSFORMS_GETTER = MethodHandleHelper.findFieldGetter(PerspectiveMapWrapper.class, "transforms");
   private static final MethodHandle IPAM_MW_TRANSFORMS_SETTER = MethodHandleHelper.findFieldSetter(PerspectiveMapWrapper.class, "transforms");
   private static final MethodHandle BIM_TRANSFORMS_GETTER = MethodHandleHelper.findFieldGetter("net.minecraftforge.client.model.BakedItemModel", "transforms");
   private static final MethodHandle BIM_TRANSFORMS_SETTER = MethodHandleHelper.findFieldSetter("net.minecraftforge.client.model.BakedItemModel", "transforms");
   private static final MethodHandle IOL_OVERRIDES_GETTER = MethodHandleHelper.findFieldGetter(ItemOverrideList.class, "overrides", "overrides");
   private static final MethodHandle IOL_OVERRIDES_SETTER = MethodHandleHelper.findFieldSetter(ItemOverrideList.class, "overrides", "overrides");
   public int successfulTrims = 0;
   public int successfuls = 0;
   public int maxRecursion = 0;
   private final Map<Object, Optional> JAVA_OPTIONALS = new IdentityHashMap();
   private final Map<Object, com.google.common.base.Optional> GUAVA_OPTIONALS = new IdentityHashMap();
   private final IDeduplicatingStorage<float[]> FLOATA_STORAGE = new DeduplicatingStorageTrove<float[]>(HashingStrategies.FLOAT_ARRAY);
   private final IDeduplicatingStorage<float[][]> FLOATAA_STORAGE = new DeduplicatingStorageTrove<float[][]>(HashingStrategies.FLOAT_ARRAY_ARRAY);
   private final IDeduplicatingStorage<ItemCameraTransforms> ICT_STORAGE = new DeduplicatingStorageTrove<ItemCameraTransforms>(HashingStrategies.ITEM_CAMERA_TRANSFORMS);
   private final IDeduplicatingStorage<Object> RESOURCE_LOCATION_STORAGE = new DeduplicatingStorageTrove<Object>(HashingStrategies.GENERIC);
   private final IDeduplicatingStorage<Object> IMMUTABLE_COLLECTION_STORAGE = new DeduplicatingStorageTrove<Object>(HashingStrategies.GENERIC);
   private final Set<Object> deduplicatedObjects = Sets.newIdentityHashSet();
   private final Map<Class, DeduplicatorFunction> DEDUPLICATOR_FUNCTIONS = new IdentityHashMap();
   private final Map<Class, Deduplicator0Function> DEDUPLICATOR_0_FUNCTIONS = new IdentityHashMap();
   private static final boolean cSetProp;

   private static void addClassFromName(Set<Class> set, String className) {
      try {
         set.add(Class.forName(className));
      } catch (ClassNotFoundException var3) {
         ;
      }

   }

   private static <T> void addClassFromName(Map<Class, T> map, String className, T value) {
      try {
         map.put(Class.forName(className), value);
      } catch (ClassNotFoundException var4) {
         ;
      }

   }

   private static void forbidClass(Class c) {
      SHOULD_PROCESS_CLASS.put(c, Boolean.valueOf(false));
   }

   private static void permitClass(Class c) {
      SHOULD_PROCESS_CLASS.put(c, Boolean.valueOf(true));
   }

   private boolean shouldCheckClass(Class c) {
      Boolean result = (Boolean)SHOULD_PROCESS_CLASS.get(c);
      if (result == null) {
         if (!c.isPrimitive() && !c.isEnum() && (!c.isArray() || this.shouldCheckClass(c.getComponentType()))) {
            if (BakedQuad.class.isAssignableFrom(c)) {
               result = false;
            } else if (!Item.class.isAssignableFrom(c) && !Block.class.isAssignableFrom(c) && !World.class.isAssignableFrom(c) && !Entity.class.isAssignableFrom(c) && !Logger.class.isAssignableFrom(c) && !IRegistry.class.isAssignableFrom(c) && !SimpleReloadableResourceManager.class.isAssignableFrom(c) && !IResourcePack.class.isAssignableFrom(c) && !IProperty.class.isAssignableFrom(c) && !IUnlistedProperty.class.isAssignableFrom(c) && !IWorldGenerator.class.isAssignableFrom(c) && !IRecipe.class.isAssignableFrom(c) && !Ingredient.class.isAssignableFrom(c) && !IBlockState.class.isAssignableFrom(c) && !LanguageManager.class.isAssignableFrom(c) && !MutableBlockPos.class.isAssignableFrom(c)) {
               if (ItemOverrideList.class.isAssignableFrom(c)) {
                  result = false;
               } else {
                  result = true;
               }
            } else {
               result = false;
            }
         } else {
            result = false;
         }

         SHOULD_PROCESS_CLASS.put(c, result);
      }

      return result.booleanValue();
   }

   public void addResourceLocation(Object o) {
      this.RESOURCE_LOCATION_STORAGE.deduplicate(o);
   }

   public void addResourceLocation(Collection coll) {
      for(Object o : coll) {
         this.RESOURCE_LOCATION_STORAGE.deduplicate(o);
      }

   }

   public Deduplicator() {
      this.DEDUPLICATOR_0_FUNCTIONS.put(float[].class, (o) -> {
         return this.FLOATA_STORAGE.deduplicate((float[])o);
      });
      Deduplicator0Function FLOATA_DEDUP = (Deduplicator0Function)this.DEDUPLICATOR_0_FUNCTIONS.get(float[].class);
      this.DEDUPLICATOR_0_FUNCTIONS.put(float[][].class, (o) -> {
         float[][] arr = this.FLOATAA_STORAGE.deduplicate((float[][])o);
         if (arr != o) {
            this.successfuls += arr.length;
         } else {
            for(int i = 0; i < arr.length; ++i) {
               float[] n = (float[])FLOATA_DEDUP.deduplicate(arr[i]);
               if (n != arr[i]) {
                  ++this.successfuls;
               }

               arr[i] = n;
            }
         }

         return arr;
      });
      Deduplicator0Function FLOATAA_DEDUP = (Deduplicator0Function)this.DEDUPLICATOR_0_FUNCTIONS.get(float[][].class);
      this.DEDUPLICATOR_0_FUNCTIONS.put(float[][][].class, (o) -> {
         float[][][] arr = (float[][][])o;

         for(int i = 0; i < arr.length; ++i) {
            float[][] n = (float[][])FLOATAA_DEDUP.deduplicate(arr[i]);
            if (n != arr[i]) {
               ++this.successfuls;
            }

            arr[i] = n;
         }

         return arr;
      });
      Deduplicator0Function FLOATAAA_DEDUP = (Deduplicator0Function)this.DEDUPLICATOR_0_FUNCTIONS.get(float[][][].class);
      Map var10000 = this.DEDUPLICATOR_0_FUNCTIONS;
      IDeduplicatingStorage var10002 = this.RESOURCE_LOCATION_STORAGE;
      Objects.requireNonNull(this.RESOURCE_LOCATION_STORAGE);
      var10000.put(ResourceLocation.class, var10002::deduplicate);

      for(Class c : Lists.newArrayList(new Class[]{ModelResourceLocation.class, Vec3d.class, Vec3i.class, BlockPos.class, TRSRTransformation.class})) {
         IDeduplicatingStorage<Object> OBJECT_STORAGE = new DeduplicatingStorageTrove<Object>(HashingStrategies.GENERIC);
         var10000 = this.DEDUPLICATOR_0_FUNCTIONS;
         Objects.requireNonNull(OBJECT_STORAGE);
         var10000.put(c, OBJECT_STORAGE::deduplicate);
      }

      if (FoamFixShared.config.clSmallModelConditions) {
         IDeduplicatingStorage<FoamyConditionOr.PredicateImpl> STORAGE = new DeduplicatingStorageTrove<FoamyConditionOr.PredicateImpl>(new FoamyConditionOr.PredicateImpl.HashingStrategy());
         this.DEDUPLICATOR_0_FUNCTIONS.put(FoamyMultipartBakedModel.class, (obj) -> {
            return STORAGE.deduplicate((FoamyConditionOr.PredicateImpl)obj);
         });
         STORAGE = new DeduplicatingStorageTrove<FoamyConditionOr.PredicateImpl>(new FoamyConditionAnd.PredicateImpl.HashingStrategy());
         this.DEDUPLICATOR_0_FUNCTIONS.put(FoamyMultipartBakedModel.class, (obj) -> {
            return STORAGE.deduplicate((FoamyConditionAnd.PredicateImpl)obj);
         });
         STORAGE = new DeduplicatingStorageTrove<FoamyConditionOr.PredicateImpl>(new FoamyConditionPropertyValue.HashingStrategy());
         this.DEDUPLICATOR_0_FUNCTIONS.put(FoamyMultipartBakedModel.class, (obj) -> {
            return STORAGE.deduplicate((FoamyConditionPropertyValue)obj);
         });
         STORAGE = new DeduplicatingStorageTrove<FoamyConditionOr.PredicateImpl>(new FoamyConditionPropertyValue.SingletonPredicatePositive.HashingStrategy());
         this.DEDUPLICATOR_0_FUNCTIONS.put(FoamyMultipartBakedModel.class, (obj) -> {
            return STORAGE.deduplicate((FoamyConditionPropertyValue.SingletonPredicatePositive)obj);
         });
         STORAGE = new DeduplicatingStorageTrove<FoamyConditionOr.PredicateImpl>(new FoamyConditionPropertyValue.SingletonPredicateNegative.HashingStrategy());
         this.DEDUPLICATOR_0_FUNCTIONS.put(FoamyMultipartBakedModel.class, (obj) -> {
            return STORAGE.deduplicate((FoamyConditionPropertyValue.SingletonPredicateNegative)obj);
         });
         STORAGE = new DeduplicatingStorageTrove<FoamyConditionOr.PredicateImpl>(new FoamyConditionPropertyValue.PredicateNegative.HashingStrategy());
         this.DEDUPLICATOR_0_FUNCTIONS.put(FoamyMultipartBakedModel.class, (obj) -> {
            return STORAGE.deduplicate((FoamyConditionPropertyValue.PredicateNegative)obj);
         });
         STORAGE = new DeduplicatingStorageTrove<FoamyConditionOr.PredicateImpl>(new FoamyConditionPropertyValue.PredicatePositive.HashingStrategy());
         this.DEDUPLICATOR_0_FUNCTIONS.put(FoamyMultipartBakedModel.class, (obj) -> {
            return STORAGE.deduplicate((FoamyConditionPropertyValue.PredicatePositive)obj);
         });
      }

      IDeduplicatingStorage<FoamyMultipartBakedModel> FOAMY_MULTIPART_STORAGE = new DeduplicatingStorageTrove<FoamyMultipartBakedModel>(new FoamyMultipartBakedModelHashingStrategy());
      this.DEDUPLICATOR_0_FUNCTIONS.put(FoamyMultipartBakedModel.class, (obj) -> {
         return FOAMY_MULTIPART_STORAGE.deduplicate((FoamyMultipartBakedModel)obj);
      });
      if (FoamFixShared.isCoremod) {
         this.DEDUPLICATOR_0_FUNCTIONS.put(Style.class, (obj) -> {
            return this.deduplicateStyleIfCoremodPresent((Style)obj);
         });
      } else {
         this.DEDUPLICATOR_0_FUNCTIONS.put(Style.class, (obj) -> {
            return obj;
         });
      }

      this.DEDUPLICATOR_0_FUNCTIONS.put(ItemCameraTransforms.class, (obj) -> {
         return this.ICT_STORAGE.deduplicate((ItemCameraTransforms)obj);
      });
      this.DEDUPLICATOR_FUNCTIONS.put(BlockPartFace.class, (o, recursion) -> {
         float[] n = (float[])FLOATA_DEDUP.deduplicate(((BlockPartFace)o).blockFaceUV.uvs);
         if (n != ((BlockPartFace)o).blockFaceUV.uvs) {
            ++this.successfuls;
         }

         return o;
      });
      if (FoamFixShared.config.expUnpackBakedQuads) {
         this.DEDUPLICATOR_FUNCTIONS.put(BakedQuad.class, (o, recursion) -> {
            BakedQuad quad = (BakedQuad)o;
            net.minecraftforge.client.model.pipeline.UnpackedBakedQuad.Builder builder = new net.minecraftforge.client.model.pipeline.UnpackedBakedQuad.Builder(quad.getFormat());
            quad.pipe(builder);
            o = builder.build();
            return o;
         });
      } else {
         this.DEDUPLICATOR_FUNCTIONS.put(BakedQuad.class, (o, recursion) -> {
            return o;
         });
      }

      this.DEDUPLICATOR_FUNCTIONS.put(UnpackedBakedQuad.class, (o, recursion) -> {
         try {
            float[][][] array = FIELD_UNPACKED_DATA_GETTER.invokeExact((UnpackedBakedQuad)o);
            FLOATAAA_DEDUP.deduplicate(array);
         } catch (Throwable var4) {
            var4.printStackTrace();
         }

         return o;
      });
      this.DEDUPLICATOR_FUNCTIONS.put(ItemOverrideList.class, (obj, recursion) -> {
         if (obj != ItemOverrideList.NONE) {
            List list = IOL_OVERRIDES_GETTER.invokeExact((ItemOverrideList)obj);
            if (list.isEmpty()) {
               ++this.successfuls;
               return ItemOverrideList.NONE;
            }
         }

         return obj;
      });
      this.DEDUPLICATOR_FUNCTIONS.put(AnimationItemOverrideList.class, (obj, recursion) -> {
         List list = IOL_OVERRIDES_GETTER.invokeExact((ItemOverrideList)obj);
         if (list.isEmpty()) {
            ++this.successfuls;
            IOL_OVERRIDES_SETTER.invokeExact((ItemOverrideList)obj, ImmutableList.of());
         }

         return obj;
      });

      for(Class c : this.DEDUPLICATOR_FUNCTIONS.keySet()) {
         permitClass(c);
      }

      addClassFromName(this.DEDUPLICATOR_FUNCTIONS, "com.google.common.collect.RegularImmutableList", new RegularImmutableListDeduplicatorFunction(this));
      addClassFromName(this.DEDUPLICATOR_FUNCTIONS, "com.google.common.collect.SingletonImmutableBiMap", new SingletonImmutableBiMapDeduplicatorFunction(this));
      addClassFromName(this.DEDUPLICATOR_FUNCTIONS, "com.google.common.collect.SingletonImmutableList", new SingletonImmutableListDeduplicatorFunction(this));
      addClassFromName(this.DEDUPLICATOR_FUNCTIONS, "com.google.common.collect.SingletonImmutableSet", new SingletonImmutableSetDeduplicatorFunction(this));
   }

   private Deduplicator0Function getDeduplicate0Func(Class c) {
      Deduplicator0Function func = (Deduplicator0Function)this.DEDUPLICATOR_0_FUNCTIONS.get(c);
      if (func == null && (ImmutableList.class.isAssignableFrom(c) || ImmutableMap.class.isAssignableFrom(c) || ImmutableSet.class.isAssignableFrom(c))) {
         IDeduplicatingStorage var10000 = this.IMMUTABLE_COLLECTION_STORAGE;
         Objects.requireNonNull(this.IMMUTABLE_COLLECTION_STORAGE);
         func = var10000::deduplicate;
         this.DEDUPLICATOR_0_FUNCTIONS.put(c, func);
      }

      return func;
   }

   private Style deduplicateStyleIfCoremodPresent(Style s) {
      while(true) {
         if (s.bold == null && s.italic == null && s.obfuscated == null && s.strikethrough == null && s.underlined == null && s.clickEvent == null && s.hoverEvent == null && s.color == null && s.insertion == null) {
            s = s.parentStyle;
            if (s != null) {
               continue;
            }

            return STYLE_EMPTY;
         }

         return s;
      }
   }

   private boolean trimArray(Object o) {
      if (o instanceof ArrayList) {
         ((ArrayList)o).trimToSize();
         ++this.successfulTrims;
         return true;
      } else {
         return false;
      }
   }

   private DeduplicatorFunction createDeduplicatorFunction(Class c) {
      DeduplicatorFunction func = null;
      boolean isIdentity = false;
      boolean continueProcessing = true;
      if (Reference.class.isAssignableFrom(c)) {
         func = (obj, recursion) -> {
            return this.deduplicateObject(((Reference)obj).get(), recursion + 1);
         };
         continueProcessing = false;
      } else if (IBakedModel.class.isAssignableFrom(c)) {
         Deduplicator0Function immMapFunc = this.getDeduplicate0Func(ImmutableMap.class);
         if (PerspectiveMapWrapper.class.isAssignableFrom(c)) {
            func = (obj, recursion) -> {
               try {
                  Object to = IPAM_MW_TRANSFORMS_GETTER.invoke(obj);
                  Object toD = immMapFunc.deduplicate(to);
                  if (toD != null && to != toD) {
                     ++this.successfuls;
                     IPAM_MW_TRANSFORMS_SETTER.invoke(obj, toD);
                  }
               } catch (Throwable var6) {
                  var6.printStackTrace();
               }

               return obj;
            };
         } else if (c == BakedItemModel.class) {
            func = (obj, recursion) -> {
               try {
                  Object to = BIM_TRANSFORMS_GETTER.invoke(obj);
                  Object toD = immMapFunc.deduplicate(to);
                  if (toD != null && to != toD) {
                     ++this.successfuls;
                     BIM_TRANSFORMS_SETTER.invoke(obj, toD);
                  }
               } catch (Throwable var6) {
                  var6.printStackTrace();
               }

               return obj;
            };
         } else if (c == SimpleBakedModel.class) {
            func = (obj, recursion) -> {
               for(EnumFacing facing : EnumFacing.VALUES) {
                  List l = ((IBakedModel)obj).getQuads((IBlockState)null, facing, 0L);
                  this.trimArray(l);
               }

               return obj;
            };
         }
      } else if (Optional.class.isAssignableFrom(c)) {
         func = (obj, recursion) -> {
            Optional opt = (Optional)obj;
            if (opt.isPresent()) {
               Object b = this.deduplicateObject(opt.get(), recursion + 1);
               if (b != null) {
                  Optional optCached = (Optional)this.JAVA_OPTIONALS.get(b);
                  if (optCached != null) {
                     ++this.successfuls;
                     return optCached;
                  } else if (b != opt.get()) {
                     Optional opt2 = Optional.of(b);
                     this.JAVA_OPTIONALS.put(b, opt2);
                     return opt2;
                  } else {
                     this.JAVA_OPTIONALS.put(opt.get(), opt);
                     return opt;
                  }
               } else {
                  return opt;
               }
            } else {
               return opt;
            }
         };
         continueProcessing = false;
      } else if (com.google.common.base.Optional.class.isAssignableFrom(c)) {
         func = (obj, recursion) -> {
            com.google.common.base.Optional opt = (com.google.common.base.Optional)obj;
            if (opt.isPresent()) {
               Object b = this.deduplicateObject(opt.get(), recursion + 1);
               if (b != null) {
                  com.google.common.base.Optional optCached = (com.google.common.base.Optional)this.GUAVA_OPTIONALS.get(b);
                  if (optCached != null) {
                     ++this.successfuls;
                     return optCached;
                  } else if (b != opt.get()) {
                     com.google.common.base.Optional opt2 = com.google.common.base.Optional.of(b);
                     this.GUAVA_OPTIONALS.put(b, opt2);
                     return opt2;
                  } else {
                     this.GUAVA_OPTIONALS.put(opt.get(), opt);
                     return opt;
                  }
               } else {
                  return opt;
               }
            } else {
               return opt;
            }
         };
         continueProcessing = false;
      } else if (Multimap.class.isAssignableFrom(c)) {
         if (!ImmutableMultimap.class.isAssignableFrom(c) && !SortedSetMultimap.class.isAssignableFrom(c)) {
            func = (obj, recursion) -> {
               for(Object key : ((Multimap)obj).keySet()) {
                  List l = Lists.newArrayList(((Multimap)obj).values());

                  for(int i = 0; i < l.size(); ++i) {
                     l.set(i, this.deduplicateObject(l.get(i), recursion + 1));
                  }

                  ((Multimap)obj).replaceValues(key, l);
               }

               return obj;
            };
         } else {
            func = (obj, recursion) -> {
               for(Object value : ((Multimap)obj).values()) {
                  this.deduplicateObject(value, recursion + 1);
               }

               return obj;
            };
         }

         continueProcessing = false;
      } else if (Map.class.isAssignableFrom(c)) {
         if (!SortedMap.class.isAssignableFrom(c) && !IMMUTABLE_CLASS.contains(c)) {
            if (ImmutableBiMap.class.isAssignableFrom(c)) {
               func = (obj, recursion) -> {
                  ImmutableMap im = (ImmutableMap)obj;
                  com.google.common.collect.ImmutableMap.Builder newMap = ImmutableBiMap.builder();
                  boolean deduplicated = false;
                  UnmodifiableIterator var6 = im.keySet().iterator();

                  while(var6.hasNext()) {
                     Object key = var6.next();
                     key = this.deduplicateObject(key, recursion + 1);
                     Object a = im.get(key);
                     Object b = this.deduplicateObject(a, recursion + 1);
                     newMap.put(key, b != null ? b : a);
                     if (b != null && b != a) {
                        deduplicated = true;
                     }
                  }

                  return deduplicated ? newMap.build() : obj;
               };
            } else if (ImmutableMap.class.isAssignableFrom(c)) {
               func = (obj, recursion) -> {
                  ImmutableMap im = (ImmutableMap)obj;
                  com.google.common.collect.ImmutableMap.Builder newMap = ImmutableMap.builder();
                  boolean deduplicated = false;
                  UnmodifiableIterator var6 = im.keySet().iterator();

                  while(var6.hasNext()) {
                     Object key = var6.next();
                     key = this.deduplicateObject(key, recursion + 1);
                     Object a = im.get(key);
                     Object b = this.deduplicateObject(a, recursion + 1);
                     newMap.put(key, b != null ? b : a);
                     if (b != null && b != a) {
                        deduplicated = true;
                     }
                  }

                  return deduplicated ? newMap.build() : obj;
               };
            } else {
               func = (obj, recursion) -> {
                  try {
                     for(Object key : ((Map)obj).keySet()) {
                        key = this.deduplicateObject(key, recursion + 1);
                        Object value = ((Map)obj).get(key);
                        Object valueD = this.deduplicateObject(value, recursion + 1);
                        if (valueD != null && value != valueD) {
                           ((Map)obj).put(key, valueD);
                        }
                     }
                  } catch (UnsupportedOperationException var8) {
                     IMMUTABLE_CLASS.add(c);
                     this.DEDUPLICATOR_FUNCTIONS.remove(c);

                     for(Object v : ((Map)obj).values()) {
                        this.deduplicateObject(v, recursion + 1);
                     }
                  }

                  return obj;
               };
            }
         } else {
            func = (obj, recursion) -> {
               for(Object v : ((Map)obj).keySet()) {
                  this.deduplicateObject(v, recursion + 1);
               }

               for(Object v : ((Map)obj).values()) {
                  this.deduplicateObject(v, recursion + 1);
               }

               return obj;
            };
         }

         continueProcessing = false;
      } else if (Collection.class.isAssignableFrom(c)) {
         if (List.class.isAssignableFrom(c)) {
            if (IMMUTABLE_CLASS.contains(c)) {
               func = (obj, recursion) -> {
                  List l = (List)obj;

                  for(int i = 0; i < l.size(); ++i) {
                     this.deduplicateObject(l.get(i), recursion + 1);
                  }

                  return obj;
               };
            } else if (ImmutableList.class.isAssignableFrom(c)) {
               func = (obj, recursion) -> {
                  ImmutableList il = (ImmutableList)obj;
                  Builder builder = ImmutableList.builder();
                  boolean deduplicated = false;

                  for(int i = 0; i < il.size(); ++i) {
                     Object a = il.get(i);
                     Object b = this.deduplicateObject(a, recursion + 1);
                     builder.add(b != null ? b : a);
                     if (b != null && b != a) {
                        deduplicated = true;
                     }
                  }

                  if (deduplicated) {
                     return builder.build();
                  } else {
                     return obj;
                  }
               };
            } else {
               func = (obj, recursion) -> {
                  List l = (List)obj;

                  try {
                     for(int i = 0; i < l.size(); ++i) {
                        l.set(i, this.deduplicateObject(l.get(i), recursion + 1));
                     }
                  } catch (UnsupportedOperationException var7) {
                     IMMUTABLE_CLASS.add(c);
                     this.DEDUPLICATOR_FUNCTIONS.remove(c);

                     for(int i = 0; i < l.size(); ++i) {
                        this.deduplicateObject(l.get(i), recursion + 1);
                     }
                  }

                  return obj;
               };
            }
         } else if (ImmutableSet.class.isAssignableFrom(c)) {
            if (!ImmutableSortedSet.class.isAssignableFrom(c)) {
               func = (obj, recursion) -> {
                  com.google.common.collect.ImmutableSet.Builder builder = new com.google.common.collect.ImmutableSet.Builder();

                  for(Object o1 : (Set)obj) {
                     builder.add(this.deduplicateObject(o1, recursion + 1));
                  }

                  return builder.build();
               };
            } else {
               func = (obj, recursion) -> {
                  for(Object o1 : (Set)obj) {
                     this.deduplicateObject(o1, recursion + 1);
                  }

                  return obj;
               };
            }
         } else {
            if (Set.class.isAssignableFrom(c) && !SortedSet.class.isAssignableFrom(c)) {
               MethodHandle constructor;
               try {
                  constructor = MethodHandles.publicLookup().findConstructor(c, MethodType.methodType(Void.TYPE));
               } catch (Exception var16) {
                  constructor = null;
               }

               if (constructor != null) {
                  try {
                     Collection nctest = constructor.invoke();
                     if (nctest != null) {
                        func = (obj, recursion) -> {
                           Collection nc = constructorFinal.invoke();

                           for(Object o1 : (Collection)obj) {
                              nc.add(this.deduplicateObject(o1, recursion + 1));
                           }

                           return nc;
                        };
                     }
                  } catch (Throwable var15) {
                     func = null;
                  }
               }
            }

            if (func == null) {
               func = (obj, recursion) -> {
                  for(Object o1 : (Collection)obj) {
                     this.deduplicateObject(o1, recursion + 1);
                  }

                  return obj;
               };
            }
         }

         continueProcessing = false;
      } else if (c.isArray()) {
         func = (obj, recursion) -> {
            for(int i = 0; i < Array.getLength(obj); ++i) {
               Object entry = Array.get(obj, i);
               Object entryD = this.deduplicateObject(entry, recursion + 1);
               if (entryD != null && entry != entryD) {
                  Array.set(obj, i, entryD);
               }
            }

            return obj;
         };
         continueProcessing = false;
      }

      if (func == null) {
         Deduplicator0Function d0func = this.getDeduplicate0Func(c);
         if (d0func == null) {
            isIdentity = true;
            func = (obj, recursion) -> {
               return obj;
            };
         } else {
            func = (obj, recursion) -> {
               Object n = d0func.deduplicate(obj);
               if (n != obj) {
                  ++this.successfuls;
               }

               return n;
            };
            continueProcessing = false;
         }
      }

      if (continueProcessing) {
         boolean canTrim = Predicate.class.isAssignableFrom(c) || TRIM_ARRAYS_CLASSES.contains(c);
         Lookup lookup = MethodHandles.lookup();
         if (cSetProp) {
            Class cc = c;

            while(true) {
               for(Field f : cc.getDeclaredFields()) {
                  if ((f.getModifiers() & 8) == 0 && this.shouldCheckClass(f.getType())) {
                     System.out.println("-> " + f.getType().getName());
                  }
               }

               if ((cc = cc.getSuperclass()) == Object.class) {
                  break;
               }
            }
         }

         Builder<MethodHandle> fsBuilder = ImmutableList.builder();
         Class cc = c;

         while(true) {
            for(Field f : cc.getDeclaredFields()) {
               if ((f.getModifiers() & 8) == 0 && this.shouldCheckClass(f.getType())) {
                  try {
                     f.setAccessible(true);
                     fsBuilder.add(lookup.unreflectGetter(f));
                     fsBuilder.add(lookup.unreflectSetter(f));
                  } catch (IllegalAccessException var14) {
                     var14.printStackTrace();
                  }
               }
            }

            if ((cc = cc.getSuperclass()) == Object.class) {
               break;
            }
         }

         MethodHandle[] methodHandles = (MethodHandle[])fsBuilder.build().toArray(new MethodHandle[0]);
         if (methodHandles.length > 0) {
            if (canTrim) {
               func = (obj, recursion) -> {
                  obj = oldFunc.deduplicate(obj, recursion);

                  for(int i = 0; i < methodHandles.length; i += 2) {
                     try {
                        Object value = methodHandles[i].invoke(obj);
                        Object valueD = this.deduplicateObject(value, recursion + 1);
                        if (valueD != null) {
                           this.trimArray(valueD);
                           if (valueD != value) {
                              methodHandles[i + 1].invoke(obj, valueD);
                           }
                        }
                     } catch (IllegalAccessException var8) {
                        ;
                     } catch (Throwable var9) {
                        var9.printStackTrace();
                     }
                  }

                  return obj;
               };
            } else {
               func = (obj, recursion) -> {
                  obj = oldFunc.deduplicate(obj, recursion);

                  for(int i = 0; i < methodHandles.length; i += 2) {
                     try {
                        Object value = methodHandles[i].invoke(obj);
                        Object valueD = this.deduplicateObject(value, recursion + 1);
                        if (valueD != null && valueD != value) {
                           methodHandles[i + 1].invoke(obj, valueD);
                        }
                     } catch (IllegalAccessException var8) {
                        ;
                     } catch (Throwable var9) {
                        var9.printStackTrace();
                     }
                  }

                  return obj;
               };
            }
         } else if (isIdentity) {
            forbidClass(c);
         }
      }

      return func;
   }

   public Object deduplicateObject(Object o, int parentRecursion) {
      if (o != null && parentRecursion <= this.maxRecursion) {
         Class c = o.getClass();
         if (!this.shouldCheckClass(c)) {
            return o;
         } else if (!this.deduplicatedObjects.add(o)) {
            return o;
         } else {
            DeduplicatorFunction func = (DeduplicatorFunction)this.DEDUPLICATOR_FUNCTIONS.get(c);
            if (func == null) {
               if (cSetProp) {
                  System.out.println(Strings.repeat("-", parentRecursion) + "- " + c.getName());
               }

               func = this.createDeduplicatorFunction(c);
               this.DEDUPLICATOR_FUNCTIONS.put(c, func);
            }

            try {
               return func.deduplicate(o, parentRecursion);
            } catch (Throwable var6) {
               return o;
            }
         }
      } else {
         return o;
      }
   }

   static {
      TRIM_ARRAYS_CLASSES.add(TextComponentKeybind.class);
      TRIM_ARRAYS_CLASSES.add(TextComponentScore.class);
      TRIM_ARRAYS_CLASSES.add(TextComponentSelector.class);
      TRIM_ARRAYS_CLASSES.add(TextComponentString.class);
      TRIM_ARRAYS_CLASSES.add(TextComponentTranslation.class);
      TRIM_ARRAYS_CLASSES.add(VertexFormat.class);
      TRIM_ARRAYS_CLASSES.add(ModelBlock.class);
      TRIM_ARRAYS_CLASSES.add(ItemOverrideList.class);
      TRIM_ARRAYS_CLASSES.add(FoamyItemLayerModel.DynamicItemModel.class);
      TRIM_ARRAYS_CLASSES.add(SimpleBakedModel.class);
      TRIM_ARRAYS_CLASSES.add(WeightedBakedModel.class);
      TRIM_ARRAYS_CLASSES.add(Multipart.class);
      forbidClass(Object.class);
      forbidClass(String.class);
      forbidClass(Integer.class);
      forbidClass(Long.class);
      forbidClass(Byte.class);
      forbidClass(Boolean.class);
      forbidClass(Float.class);
      forbidClass(Double.class);
      forbidClass(Short.class);
      forbidClass(Class.class);
      forbidClass(Field.class);
      forbidClass(Method.class);
      forbidClass(MethodHandle.class);
      forbidClass(Vector3f.class);
      forbidClass(Vector4f.class);
      forbidClass(Matrix4f.class);
      forbidClass(org.lwjgl.util.vector.Vector3f.class);
      forbidClass(org.lwjgl.util.vector.Vector4f.class);
      forbidClass(org.lwjgl.util.vector.Matrix4f.class);
      forbidClass(Random.class);
      forbidClass(TextureAtlasSprite.class);
      forbidClass(ItemStack.class);
      forbidClass(Gson.class);
      forbidClass(GsonBuilder.class);
      forbidClass(EnumBiMap.class);
      forbidClass(ModelLoader.class);
      forbidClass(Minecraft.class);
      forbidClass(BlockModelShapes.class);
      forbidClass(BlockFaceUV.class);
      forbidClass(ModelManager.class);
      forbidClass(BlockPartRotation.class);
      forbidClass(ModelBlockAnimation.class);
      forbidClass(BufferBuilder.class);
      forbidClass(SoundHandler.class);
      forbidClass(SoundManager.class);
      forbidClass(GameSettings.class);
      forbidClass(Logger.class);
      forbidClass(Joiner.class);
      forbidClass(Tessellator.class);
      forbidClass(Cache.class);
      forbidClass(LoadingCache.class);
      forbidClass(VertexFormatElement.class);
      String s = System.getProperty("pl.asie.foamfix.debugDeduplicatedClasses", "");
      cSetProp = !s.isEmpty();
   }
}
