package pl.asie.foamfix.coremod;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.io.IOException;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;
import java.util.function.BooleanSupplier;
import net.minecraft.launchwrapper.IClassTransformer;
import net.minecraft.launchwrapper.LaunchClassLoader;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.commons.ClassRemapper;
import org.objectweb.asm.commons.Remapper;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import pl.asie.foamfix.coremod.patches.BlockPosPatch;
import pl.asie.foamfix.coremod.patches.EntityDataManagerPatch;
import pl.asie.foamfix.coremod.patches.FastAirLookupPatch;
import pl.asie.foamfix.coremod.patches.GhostBusterBlockStateAccessPatch;
import pl.asie.foamfix.coremod.patches.GhostBusterDefinitionPatch;
import pl.asie.foamfix.coremod.patches.ModelLoaderCleanupPatch;
import pl.asie.foamfix.coremod.patches.ReturnIfBooleanTruePatch;
import pl.asie.foamfix.coremod.patches.WorldServerRemovalPatch;
import pl.asie.foamfix.coremod.patches.network.DeflaterCompressionLevelPatch;
import pl.asie.foamfix.ghostbuster.GhostBusterDefinition;
import pl.asie.foamfix.shared.FoamFixShared;
import pl.asie.patchy.Patchy;
import pl.asie.patchy.TransformerFunction;
import pl.asie.patchy.TransformerHandler;
import pl.asie.patchy.handlers.TransformerHandlerByteArray;
import pl.asie.patchy.handlers.TransformerHandlerClassNode;
import pl.asie.patchy.handlers.TransformerHandlerClassVisitor;
import pl.asie.patchy.helpers.ConstructorReplacingTransformer;

public class FoamFixTransformer implements IClassTransformer {
   private static final Patchy patchy = new Patchy();

   public FoamFixTransformer() {
   }

   private static byte[] getClassBytes(String className) throws IOException {
      ClassLoader loader = FoamFixTransformer.class.getClassLoader();
      if (loader instanceof LaunchClassLoader) {
         return ((LaunchClassLoader)FoamFixTransformer.class.getClassLoader()).getClassBytes(className);
      } else {
         throw new RuntimeException("Incompatible class loader for FoamFixTransformer.getClassBytes: " + loader.getClass().getName());
      }
   }

   public static ClassNode spliceClasses(ClassNode data, String className, boolean addMethods, String... methods) {
      try {
         byte[] dataSplice = getClassBytes(className);
         return spliceClasses(data, dataSplice, className, addMethods, methods);
      } catch (IOException var5) {
         throw new RuntimeException(var5);
      }
   }

   public static ClassNode spliceClasses(ClassNode nodeData, byte[] dataSplice, String className, boolean addMethods, String... methods) {
      if (dataSplice == null) {
         throw new RuntimeException("Class " + className + " not found! This is a FoamFix bug!");
      } else {
         Set<String> methodSet = Sets.newHashSet(methods);
         List<String> methodList = Lists.newArrayList(methods);
         ClassReader readerSplice = new ClassReader(dataSplice);
         final String className2 = className.replace('.', '/');
         final String targetClassName2 = nodeData.name;
         String targetClassName = targetClassName2.replace('/', '.');
         Remapper remapper = new Remapper() {
            public String map(String name) {
               return className2.equals(name) ? targetClassName2 : name;
            }
         };
         ClassNode nodeSplice = new ClassNode();
         readerSplice.accept(new ClassRemapper(nodeSplice, remapper), 8);

         for(String s : nodeSplice.interfaces) {
            if (s.contains("IFoamFix")) {
               nodeData.interfaces.add(s);
               System.out.println("Added INTERFACE: " + s);
            }
         }

         for(int i = 0; i < nodeSplice.methods.size(); ++i) {
            MethodNode mn = (MethodNode)nodeSplice.methods.get(i);
            if (methodSet.contains(mn.name)) {
               boolean added = false;

               for(int j = 0; j < nodeData.methods.size(); ++j) {
                  MethodNode oldMn = (MethodNode)nodeData.methods.get(j);
                  if (oldMn.name.equals(mn.name) && oldMn.desc.equals(mn.desc)) {
                     System.out.println("Spliced in METHOD: " + targetClassName + "." + mn.name);
                     nodeData.methods.set(j, mn);
                     boolean isConstructor = oldMn.name.charAt(0) == '<';
                     if (nodeData.superName != null && nodeData.name.equals(nodeSplice.superName) && !isConstructor) {
                        ListIterator<AbstractInsnNode> nodeListIterator = mn.instructions.iterator();

                        while(nodeListIterator.hasNext()) {
                           AbstractInsnNode node = (AbstractInsnNode)nodeListIterator.next();
                           if (node instanceof MethodInsnNode && node.getOpcode() == 183) {
                              MethodInsnNode methodNode = (MethodInsnNode)node;
                              if (targetClassName2.equals(methodNode.owner)) {
                                 methodNode.owner = nodeData.superName;
                              }
                           }
                        }
                     }

                     if (!isConstructor) {
                        oldMn.name = (String)methodList.get(methodList.indexOf(oldMn.name) & -2) + "_foamfix_old";
                        nodeData.methods.add(oldMn);
                     }

                     added = true;
                     break;
                  }
               }

               if (!added && addMethods) {
                  System.out.println("Added METHOD: " + targetClassName + "." + mn.name);
                  nodeData.methods.add(mn);
                  added = true;
               }
            }
         }

         for(int i = 0; i < nodeSplice.fields.size(); ++i) {
            FieldNode mn = (FieldNode)nodeSplice.fields.get(i);
            if (methodSet.contains(mn.name)) {
               boolean added = false;

               for(int j = 0; j < nodeData.fields.size(); ++j) {
                  FieldNode otherNode = (FieldNode)nodeData.fields.get(j);
                  if (otherNode.name.equals(mn.name) && otherNode.desc.equals(mn.desc)) {
                     System.out.println("Spliced in FIELD: " + targetClassName + "." + mn.name);
                     nodeData.fields.set(j, mn);
                     added = true;
                     break;
                  }
               }

               if (!added) {
                  System.out.println("Added FIELD: " + targetClassName + "." + mn.name);
                  nodeData.fields.add(mn);
                  added = true;
               }
            }
         }

         return nodeData;
      }
   }

   public static ClassNode replaceClasses(ClassNode data, String className) {
      try {
         byte[] dataSplice = getClassBytes(className);
         return replaceClasses(data, dataSplice, className);
      } catch (IOException var3) {
         throw new RuntimeException(var3);
      }
   }

   public static ClassNode replaceClasses(ClassNode nodeData, byte[] dataSplice, String className) {
      System.out.println("replacing " + nodeData.name + " with " + className);
      if (dataSplice == null) {
         throw new RuntimeException("Class " + className + " not found! This is a FoamFix bug!");
      } else {
         ClassReader readerSplice = new ClassReader(dataSplice);
         final String className2 = className.replace('.', '/');
         final String targetClassName2 = nodeData.name;
         String targetClassName = targetClassName2.replace('/', '.');
         Remapper remapper = new Remapper() {
            public String map(String name) {
               return className2.equals(name) ? targetClassName2 : name;
            }
         };
         ClassNode nodeSplice = new ClassNode();
         readerSplice.accept(new ClassRemapper(nodeSplice, remapper), 8);
         return nodeSplice;
      }
   }

   public static void init() {
      patchy.registerHandler(byte[].class, new TransformerHandlerByteArray(patchy));
      patchy.registerHandler(ClassNode.class, new TransformerHandlerClassNode(patchy));
      patchy.registerHandler(ClassVisitor.class, new TransformerHandlerClassVisitor(patchy));
      TransformerHandler<byte[]> handler = patchy.<byte[]>getHandler(byte[].class);
      TransformerHandler<ClassNode> handlerCN = patchy.<ClassNode>getHandler(ClassNode.class);
      TransformerHandler<ClassVisitor> handlerCV = patchy.<ClassVisitor>getHandler(ClassVisitor.class);
      if (FoamFixShared.config.geSmallPropertyStorage) {
         FoamFixShared.emitWarningIfPresent("coremod.smallPropertyStorage", FoamFixShared::hasIdPatch, "JustEnoughIDs/NotEnoughIDs", false);
         patchy.addTransformerId("smallPropertyStorage_v1");
         handlerCN.add((data) -> {
            return spliceClasses(data, "pl.asie.foamfix.common.FoamyBlockStateContainer", false, "createState", "createState", "foamfix_mapper", "foamfix_mapper");
         }, "net.minecraft.block.state.BlockStateContainer");
         handlerCN.add((data) -> {
            return spliceClasses(data, "pl.asie.foamfix.common.FoamyExtendedBlockStateContainer", false, "createState", "createState", "foamfix_mapper", "foamfix_mapper");
         }, "net.minecraftforge.common.property.ExtendedBlockState");
      }

      if (FoamFixShared.config.gePatchChunkSerialization) {
         boolean compatible = true;
         if (FoamFixShared.emitWarningIfPresent("coremod.patchChunkSerialization", FoamFixShared::hasIdPatch, "JustEnoughIDs/NotEnoughIDs", true)) {
            compatible = false;
         }

         if (FoamFixShared.emitWarningIfPresent("coremod.patchChunkSerialization", FoamFixShared::hasSponge, "SpongeForge", true)) {
            compatible = false;
         }

         if (compatible) {
            patchy.addTransformerId("patchChunkSerialization_v1");
            handlerCN.add((data) -> {
               return spliceClasses(data, "pl.asie.foamfix.coremod.injections.BlockStateContainerSpongeInject", false, "getSerializedSize", "getSerializedSize");
            }, "net.minecraft.world.chunk.BlockStateContainer");
         }
      }

      if (FoamFixShared.config.twImmediateLightingUpdates) {
         patchy.addTransformerId("immediateLightingUpdates_v1");
         handlerCN.add((data) -> {
            return spliceClasses(data, "pl.asie.foamfix.coremod.injections.client.RenderGlobalImmediateInject", false, "notifyLightSet", "notifyLightSet");
         }, "net.minecraft.client.renderer.RenderGlobal");
      }

      if (FoamFixShared.config.clDynamicItemModels) {
         patchy.addTransformerId("dynamicItemModels_v1");
         handlerCN.add((data) -> {
            return spliceClasses(data, "pl.asie.foamfix.client.FoamFixDynamicItemModels", false, "bake", "bake");
         }, "net.minecraftforge.client.model.ItemLayerModel");
      }

      if (FoamFixShared.config.clCheapMinimumLighter) {
         patchy.addTransformerId("cheapMinimumLighter_v1");
         handlerCN.add((data) -> {
            return spliceClasses(data, "pl.asie.foamfix.coremod.injections.client.BlockInfoInject", true, "getRawB", "getRawB", "getRawS", "getRawS", "updateAO", "updateAO");
         }, "net.minecraftforge.client.model.pipeline.BlockInfo");
      }

      if (FoamFixShared.config.geBlockPosPatch) {
         boolean compatible = true;
         if (FoamFixShared.emitWarningIfPresent("coremod.optimizedBlockPos", FoamFixShared::hasSponge, "SpongeForge", true)) {
            compatible = false;
         }

         if (compatible) {
            patchy.addTransformerId("blockPosPatch_v1");
            handlerCN.add(BlockPosPatch::patchVec3i, "net.minecraft.util.math.Vec3i");
            handlerCV.add(BlockPosPatch::patchOtherClass);
         }
      }

      if (FoamFixShared.config.geFasterEntityLookup) {
         patchy.addTransformerId("fasterClassInheritanceMultiMap_v1");
         handlerCV.add(new ConstructorReplacingTransformer("net.minecraft.util.ClassInheritanceMultiMap", "pl.asie.foamfix.coremod.common.FoamyClassInheritanceMultiMap", new String[]{"<init>"}), "net.minecraft.world.chunk.Chunk");
      }

      if (FoamFixShared.config.clSmallModelConditions) {
         patchy.addTransformerId("smallModelConditions_v2");
         handlerCV.add(new ConstructorReplacingTransformer("net.minecraft.client.renderer.block.model.multipart.ConditionPropertyValue", "pl.asie.foamfix.client.condition.FoamyConditionPropertyValue", new String[0]), "net.minecraft.client.renderer.block.model.multipart.Selector$Deserializer");
         handlerCV.add(new ConstructorReplacingTransformer("net.minecraft.client.renderer.block.model.multipart.ConditionOr", "pl.asie.foamfix.client.condition.FoamyConditionOr", new String[0]), "net.minecraft.client.renderer.block.model.multipart.Selector$Deserializer");
         handlerCV.add(new ConstructorReplacingTransformer("net.minecraft.client.renderer.block.model.multipart.ConditionAnd", "pl.asie.foamfix.client.condition.FoamyConditionAnd", new String[0]), "net.minecraft.client.renderer.block.model.multipart.Selector$Deserializer");
      }

      if (FoamFixShared.config.clOpenUrlLinux) {
         patchy.addTransformerId("openUrlLinux_v1");
         handlerCN.add((data) -> {
            return spliceClasses(data, "pl.asie.foamfix.coremod.injections.client.GuiScreenLinuxInject", false, "openWebLink", "openWebLink");
         });
      }

      if (FoamFixShared.config.neMicroOptimizations) {
         patchy.addTransformerId("neMicroOptimizations_writeString_v1");
         handlerCN.add((data) -> {
            return spliceClasses(data, "pl.asie.foamfix.coremod.injections.network.PacketBufferInject", false, "writeString", "writeString");
         }, "net.minecraft.network.PacketBuffer");
         handlerCN.add((data) -> {
            return spliceClasses(data, "pl.asie.foamfix.coremod.injections.network.ByteBufUtilsInject", false, "writeUTF8String");
         }, "net.minecraftforge.fml.common.network.ByteBufUtils");
      }

      if (FoamFixShared.neDeflaterCompression >= 0) {
         patchy.addTransformerId("neDeflaterCompression");
         handlerCN.add(new DeflaterCompressionLevelPatch(), "net.minecraft.network.NettyCompressionEncoder");
      }

      if (FoamFixShared.config.geFasterAirLookup) {
         patchy.addTransformerId("fasterAirLookup_v1");
         handlerCN.add(new FastAirLookupPatch(), "net.minecraft.item.ItemStack");
      }

      if (FoamFixShared.config.geFasterPropertyComparisons) {
         patchy.addTransformerId("fasterPropertyComparisons_v2");
         handlerCN.add((data) -> {
            return spliceClasses(data, "pl.asie.foamfix.coremod.injections.PropertyFasterComparisonsInject$Bool", true, "equals", "equals", "hashCode", "hashCode");
         }, "net.minecraft.block.properties.PropertyBool");
         handlerCN.add((data) -> {
            return spliceClasses(data, "pl.asie.foamfix.coremod.injections.PropertyFasterComparisonsInject$Enum", false, "equals", "equals");
         }, "net.minecraft.block.properties.PropertyEnum");

         for(String s : new String[]{"net.minecraft.block.properties.PropertyInteger", "net.minecraft.block.properties.PropertyEnum"}) {
            handlerCN.add((data) -> {
               return spliceClasses(data, "pl.asie.foamfix.coremod.injections.CachingHashCodeInject", true, "hashCode", "hashCode", "foamfix_hashCode", "foamfix_hashCode", "foamfix_hashCode_calced", "foamfix_hashCode_calced");
            }, s);
         }
      }

      if (FoamFixShared.config.geFasterEntityDataManager) {
         patchy.addTransformerId("fasterEntityDataManager_v1");
         handlerCN.add(new EntityDataManagerPatch(), "net.minecraft.network.datasync.EntityDataManager");
      }

      patchy.addTransformerId("disableTextureAnimations_v1");
      handlerCN.add(new ReturnIfBooleanTruePatch("clDisableTextureAnimations", new String[]{"updateAnimations", "updateAnimations"}), "net.minecraft.client.renderer.texture.TextureMap");
      if (FoamFixShared.config.geFasterHopper) {
         patchy.addTransformerId("fasterHopper_v1");
         handlerCV.add(new ConstructorReplacingTransformer("net.minecraft.tileentity.TileEntityHopper", "pl.asie.foamfix.common.TileEntityFasterHopper", new String[]{"createNewTileEntity", "createNewTileEntity"}), "net.minecraft.block.BlockHopper");
         patchy.addTransformerId("tileEntityGetKeyWrap_v1");
         handlerCN.add((data) -> {
            return spliceClasses(data, "pl.asie.foamfix.coremod.injections.TileEntityGetKeyWrapInject", false, "getKey", "getKey");
         }, "net.minecraft.tileentity.TileEntity");
      }

      if (FoamFixShared.config.geFixWorldEntityCleanup) {
         patchy.addTransformerId("fixWorldEntityCleanup_v1");
         handlerCN.add((data) -> {
            return spliceClasses(data, "pl.asie.foamfix.coremod.injections.WorldRemovalInject", true, "foamfix_removeUnloadedEntities", "foamfix_removeUnloadedEntities");
         }, "net.minecraft.world.World");
         handlerCN.add(new WorldServerRemovalPatch(), "net.minecraft.world.WorldServer");
      }

      if (FoamFixShared.config.txEnable) {
         patchy.addTransformerId("fastTextureAtlasSprite_v1");
         handlerCV.add(new ConstructorReplacingTransformer("net.minecraft.client.renderer.texture.TextureAtlasSprite", "pl.asie.foamfix.client.FastTextureAtlasSprite", new String[]{"makeAtlasSprite", "makeAtlasSprite"}), "net.minecraft.client.renderer.texture.TextureAtlasSprite");
      }

      if (FoamFixShared.config.geMobSpawnerCheckSpeed > 2) {
         patchy.addTransformerId("geMobSpawnerCheckSpeed_v2");
         handlerCN.add((data) -> {
            return spliceClasses(data, "pl.asie.foamfix.coremod.injections.CachingMobSpawnerLogicInject", false, "isActivated", "isActivated", "foamfix_activatedCache", "foamfix_activatedCache", "foamfix_activatedCachePESize", "foamfix_activatedCachePESize", "foamfix_activatedCacheTime", "foamfix_activatedCacheTime", "foamfix_forcedCache", "foamfix_forcedCache", "foamfix_forcedCacheTime", "foamfix_forcedCacheTime");
         }, "net.minecraft.tileentity.MobSpawnerBaseLogic");
      }

      if (FoamFixShared.config.gbPatchBeds) {
         patchy.addTransformerId("gbPatchBeds_v1");
         handlerCN.add((data) -> {
            return spliceClasses(data, "pl.asie.foamfix.coremod.injections.BlockBedInject", false, "neighborChanged", "neighborChanged");
         }, "net.minecraft.block.BlockBed");
      }

      if (FoamFixShared.config.gbPatchFluids) {
         patchy.addTransformerId("gbPatchFluids_v2");
         handlerCN.add(new GhostBusterDefinitionPatch(GhostBusterDefinition.updateTick(4)), "net.minecraftforge.fluids.BlockFluidClassic");
         handlerCN.add(new GhostBusterDefinitionPatch(GhostBusterDefinition.updateTick(1)), "net.minecraftforge.fluids.BlockFluidFinite");
      }

      if (FoamFixShared.config.gbPatchBopGrass) {
         patchy.addTransformerId("gbPatchBopGrass_v1");
         handlerCN.add(new GhostBusterDefinitionPatch(new GhostBusterDefinition("spreadGrass", "spreadGrass", 1, 2, 3)), "biomesoplenty.common.block.BlockBOPGrass");
      }

      if (FoamFixShared.config.gbPatchFarmland) {
         patchy.addTransformerId("gbPatchFarmland_v1");
         handlerCV.add(new GhostBusterBlockStateAccessPatch(new String[]{"hasWater", "hasWater"}), "net.minecraft.block.BlockFarmland");
      }

      if (FoamFixShared.config.clJeiCreativeSearch) {
         patchy.addTransformerId("clJeiCreativeSearch_v1");
         handlerCN.add((data) -> {
            return spliceClasses(data, "pl.asie.foamfix.coremod.patches.jei.SearchTreeJEIManagerInject", false, "onResourceManagerReload", "onResourceManagerReload");
         }, "net.minecraft.client.util.SearchTreeManager");
      }

      if (FoamFixShared.config.clWipeModelCache) {
         patchy.addTransformerId("wipeModelCache_v1");
         handlerCN.add((data) -> {
            return spliceClasses(data, "pl.asie.foamfix.coremod.injections.client.VanillaModelWrapperInject", false, "bakeNormal", "bakeNormal");
         }, "net.minecraftforge.client.model.ModelLoader$VanillaModelWrapper");
      }

      if (FoamFixShared.config.clClearCachesOnUnload) {
         patchy.addTransformerId("clearCachesOnUnload_v2");
         handlerCN.add((data) -> {
            return spliceClasses(data, "pl.asie.foamfix.coremod.injections.client.AnimationModelBaseClearCacheInject", false, "render", "render");
         }, "net.minecraftforge.client.model.animation.AnimationModelBase");
      }

      patchy.addTransformerId("modelResourceLocationFastConstruct_v1");
      handlerCN.add((data) -> {
         return spliceClasses(data, "pl.asie.foamfix.coremod.injections.client.ModelResourceLocationFastConstructInject", false, "<init>", "<init>");
      }, "net.minecraft.client.renderer.block.model.ModelResourceLocation");
      if (FoamFixShared.config.gbCustomRules != null && FoamFixShared.config.gbCustomRules.length > 0) {
         for(String s : FoamFixShared.config.gbCustomRules) {
            String[] sSplit = s.split(";");

            try {
               GhostBusterDefinition definition = new GhostBusterDefinition(sSplit[1], sSplit[1], Integer.parseInt(sSplit[2]), Integer.parseInt(sSplit[3]), Integer.parseInt(sSplit[4]));
               handlerCN.add(new GhostBusterDefinitionPatch(definition), sSplit[0]);
            } catch (Throwable var9) {
               throw new RuntimeException("Could not parse custom rule: " + s, var9);
            }
         }
      }

      if (FoamFixShared.config.clModelLoaderCleanup) {
         patchy.addTransformerId("modelLoaderCleanup_v1");
         handlerCN.add(new ModelLoaderCleanupPatch(), "net.minecraft.client.renderer.block.model.ModelManager");
      }

   }

   public byte[] transform(String name, String transformedName, byte[] dataOrig) {
      return FoamFixShared.isCoremod ? patchy.transform(name, transformedName, dataOrig) : dataOrig;
   }
}
