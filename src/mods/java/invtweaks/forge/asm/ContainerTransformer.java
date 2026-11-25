package invtweaks.forge.asm;

import com.google.common.collect.Lists;
import invtweaks.forge.asm.compatibility.CompatibilityConfigLoader;
import invtweaks.forge.asm.compatibility.ContainerInfo;
import invtweaks.forge.asm.compatibility.MethodInfo;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.function.Consumer;
import net.minecraft.launchwrapper.IClassTransformer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

public class ContainerTransformer implements IClassTransformer {
   private static final String VALID_INVENTORY_METHOD = "invtweaks$validInventory";
   private static final String VALID_CHEST_METHOD = "invtweaks$validChest";
   private static final String LARGE_CHEST_METHOD = "invtweaks$largeChest";
   private static final String SHOW_BUTTONS_METHOD = "invtweaks$showButtons";
   private static final String ROW_SIZE_METHOD = "invtweaks$rowSize";
   private static final String SLOT_MAP_METHOD = "invtweaks$slotMap";
   private static final String CONTAINER_CLASS_INTERNAL = "net/minecraft/inventory/Container";
   private static final String SLOT_MAPS_VANILLA_CLASS = "invtweaks/container/VanillaSlotMaps";
   private static final String ANNOTATION_CHEST_CONTAINER = "Linvtweaks/api/container/ChestContainer;";
   private static final String ANNOTATION_CHEST_CONTAINER_ROW_CALLBACK = "Linvtweaks/api/container/ChestContainer$RowSizeCallback;";
   private static final String ANNOTATION_CHEST_CONTAINER_LARGE_CALLBACK = "Linvtweaks/api/container/ChestContainer$IsLargeCallback;";
   private static final String ANNOTATION_INVENTORY_CONTAINER = "Linvtweaks/api/container/InventoryContainer;";
   private static final String ANNOTATION_IGNORE_CONTAINER = "Linvtweaks/api/container/IgnoreContainer;";
   private static final String ANNOTATION_CONTAINER_SECTION_CALLBACK = "Linvtweaks/api/container/ContainerSectionCallback;";
   private static final Logger logger = LogManager.getLogger();
   private static List<String> uninterestingPackages = Lists.newArrayList(new String[]{"net.minecraft.", "net.minecraftforge.", "joptsimple.", "com.mojang.", "com.google.gson.", "io.netty.", "oshi.", "com.sun.jna.", "com.ibm.icu.", "org.slf4j.", "javassist.", "gnu.trove.", "paulscode.sound.", "com.jcraft.jogg.", "com.jcraft.jorbis.", "it.unimi.dsi.fastutil."});
   @NotNull
   private static Map<String, ContainerInfo> standardClasses = new HashMap();
   @NotNull
   private static Map<String, ContainerInfo> configClasses = new HashMap();

   public ContainerTransformer() {
      lateInit();
   }

   private static void transformContainer(@NotNull ClassNode clazz, @NotNull ContainerInfo info) {
      ASMHelper.generateBooleanMethodConst(clazz, "invtweaks$showButtons", info.showButtons);
      ASMHelper.generateBooleanMethodConst(clazz, "invtweaks$validInventory", info.validInventory);
      ASMHelper.generateBooleanMethodConst(clazz, "invtweaks$validChest", info.validChest);
      if (info.largeChestMethod != null) {
         if (info.largeChestMethod.isStatic) {
            ASMHelper.generateForwardingToStaticMethod(clazz, "invtweaks$largeChest", info.largeChestMethod.methodName, info.largeChestMethod.methodType.getReturnType(), info.largeChestMethod.methodClass, info.largeChestMethod.methodType.getArgumentTypes()[0]);
         } else {
            ASMHelper.generateSelfForwardingMethod(clazz, "invtweaks$largeChest", info.largeChestMethod.methodName, info.largeChestMethod.methodType.getReturnType());
         }
      } else {
         ASMHelper.generateBooleanMethodConst(clazz, "invtweaks$largeChest", info.largeChest);
      }

      if (info.rowSizeMethod != null) {
         if (info.rowSizeMethod.isStatic) {
            ASMHelper.generateForwardingToStaticMethod(clazz, "invtweaks$rowSize", info.rowSizeMethod.methodName, info.rowSizeMethod.methodType.getReturnType(), info.rowSizeMethod.methodClass, info.rowSizeMethod.methodType.getArgumentTypes()[0]);
         } else {
            ASMHelper.generateSelfForwardingMethod(clazz, "invtweaks$rowSize", info.rowSizeMethod.methodName, info.rowSizeMethod.methodType.getReturnType());
         }
      } else {
         ASMHelper.generateIntegerMethodConst(clazz, "invtweaks$rowSize", info.rowSize);
      }

      if (info.slotMapMethod.isStatic) {
         ASMHelper.generateForwardingToStaticMethod(clazz, "invtweaks$slotMap", info.slotMapMethod.methodName, info.slotMapMethod.methodType.getReturnType(), info.slotMapMethod.methodClass, info.slotMapMethod.methodType.getArgumentTypes()[0]);
      } else {
         ASMHelper.generateSelfForwardingMethod(clazz, "invtweaks$slotMap", info.slotMapMethod.methodName, info.slotMapMethod.methodType.getReturnType());
      }

   }

   private static void transformBaseContainer(@NotNull ClassNode clazz) {
      ASMHelper.generateBooleanMethodConst(clazz, "invtweaks$showButtons", false);
      ASMHelper.generateBooleanMethodConst(clazz, "invtweaks$validInventory", false);
      ASMHelper.generateBooleanMethodConst(clazz, "invtweaks$validChest", false);
      ASMHelper.generateBooleanMethodConst(clazz, "invtweaks$largeChest", false);
      ASMHelper.generateIntegerMethodConst(clazz, "invtweaks$rowSize", (short)9);
      ASMHelper.generateForwardingToStaticMethod(clazz, "invtweaks$slotMap", "unknownContainerSlots", Type.getObjectType("java/util/Map"), Type.getObjectType("invtweaks/container/VanillaSlotMaps"), Type.getObjectType("net/minecraft/inventory/Container"));
   }

   private static void transformCreativeContainer(@NotNull ClassNode clazz) {
      ASMHelper.generateForwardingToStaticMethod(clazz, "invtweaks$showButtons", "containerCreativeIsInventory", Type.BOOLEAN_TYPE, Type.getObjectType("invtweaks/container/VanillaSlotMaps"));
      ASMHelper.generateForwardingToStaticMethod(clazz, "invtweaks$validInventory", "containerCreativeIsInventory", Type.BOOLEAN_TYPE, Type.getObjectType("invtweaks/container/VanillaSlotMaps"));
      ASMHelper.generateBooleanMethodConst(clazz, "invtweaks$validChest", false);
      ASMHelper.generateBooleanMethodConst(clazz, "invtweaks$largeChest", false);
      ASMHelper.generateIntegerMethodConst(clazz, "invtweaks$rowSize", (short)9);
      ASMHelper.generateForwardingToStaticMethod(clazz, "invtweaks$slotMap", "containerCreativeSlots", Type.getObjectType("java/util/Map"), Type.getObjectType("invtweaks/container/VanillaSlotMaps"));
   }

   private static void transformHorseInventoryContainer(@NotNull ClassNode clazz) {
      ASMHelper.generateForwardingToStaticMethod(clazz, "invtweaks$showButtons", "containerHorseIsInventory", Type.BOOLEAN_TYPE, Type.getObjectType("invtweaks/container/VanillaSlotMaps"));
      ASMHelper.generateForwardingToStaticMethod(clazz, "invtweaks$validInventory", "containerHorseIsInventory", Type.BOOLEAN_TYPE, Type.getObjectType("invtweaks/container/VanillaSlotMaps"));
      ASMHelper.generateBooleanMethodConst(clazz, "invtweaks$validChest", true);
      ASMHelper.generateBooleanMethodConst(clazz, "invtweaks$largeChest", false);
      ASMHelper.generateIntegerMethodConst(clazz, "invtweaks$rowSize", (short)5);
      ASMHelper.generateForwardingToStaticMethod(clazz, "invtweaks$slotMap", "containerHorseSlots", Type.getObjectType("java/util/Map"), Type.getObjectType("invtweaks/container/VanillaSlotMaps"));
   }

   private static void transformInvTweaksObfuscation(@NotNull ClassNode clazz) {
      Type containertype = Type.getObjectType("net/minecraft/inventory/Container");

      for(MethodNode method : clazz.methods) {
         if ("isValidChest".equals(method.name)) {
            ASMHelper.replaceSelfForwardingMethod(method, "invtweaks$validChest", containertype);
         } else if ("isValidInventory".equals(method.name)) {
            ASMHelper.replaceSelfForwardingMethod(method, "invtweaks$validInventory", containertype);
         } else if ("showButtons".equals(method.name)) {
            ASMHelper.replaceSelfForwardingMethod(method, "invtweaks$showButtons", containertype);
         } else if ("getSpecialChestRowSize".equals(method.name)) {
            ASMHelper.replaceSelfForwardingMethod(method, "invtweaks$rowSize", containertype);
         } else if ("getContainerSlotMap".equals(method.name)) {
            ASMHelper.replaceSelfForwardingMethod(method, "invtweaks$slotMap", containertype);
         } else if ("isLargeChest".equals(method.name)) {
            ASMHelper.replaceSelfForwardingMethod(method, "invtweaks$largeChest", containertype);
         }
      }

   }

   private static void transformTextField(@NotNull ClassNode clazz) {
      for(MethodNode method : clazz.methods) {
         if (("setFocused".equals(method.name) || "setFocused".equals(method.name)) && "(Z)V".equals(method.desc)) {
            InsnList code = method.instructions;
            AbstractInsnNode returnNode = null;
            ListIterator<AbstractInsnNode> iterator = code.iterator();

            while(iterator.hasNext()) {
               AbstractInsnNode insn = (AbstractInsnNode)iterator.next();
               if (insn.getOpcode() == 177) {
                  returnNode = insn;
                  break;
               }
            }

            if (returnNode != null) {
               code.insertBefore(returnNode, new VarInsnNode(21, 1));
               code.insertBefore(returnNode, new MethodInsnNode(184, "invtweaks/forge/InvTweaksMod", "setTextboxModeStatic", "(Z)V", false));
               logger.info("InvTweaks: successfully transformed setFocused/setFocused");
            } else {
               logger.fatal("InvTweaks: unable to find return in setFocused/setFocused");
            }
         }
      }

   }

   @NotNull
   public static MethodInfo getVanillaSlotMapInfo(String name) {
      return getSlotMapInfo(Type.getObjectType("invtweaks/container/VanillaSlotMaps"), name, true);
   }

   @NotNull
   private static MethodInfo getSlotMapInfo(Type mClass, String name, boolean isStatic) {
      return new MethodInfo(Type.getMethodType(Type.getObjectType("java/util/Map"), new Type[]{Type.getObjectType("net/minecraft/inventory/Container")}), mClass, name, isStatic);
   }

   private static void lateInit() {
      standardClasses.put("net.minecraft.inventory.ContainerPlayer", new ContainerInfo(true, true, false, getVanillaSlotMapInfo("containerPlayerSlots")));
      standardClasses.put("net.minecraft.inventory.ContainerMerchant", new ContainerInfo(true, true, false));
      standardClasses.put("net.minecraft.inventory.ContainerRepair", new ContainerInfo(true, true, false, getVanillaSlotMapInfo("containerRepairSlots")));
      standardClasses.put("net.minecraft.inventory.ContainerHopper", new ContainerInfo(true, true, false));
      standardClasses.put("net.minecraft.inventory.ContainerBeacon", new ContainerInfo(true, true, false));
      standardClasses.put("net.minecraft.inventory.ContainerBrewingStand", new ContainerInfo(true, true, false, getVanillaSlotMapInfo("containerBrewingSlots")));
      standardClasses.put("net.minecraft.inventory.ContainerWorkbench", new ContainerInfo(true, true, false, getVanillaSlotMapInfo("containerWorkbenchSlots")));
      standardClasses.put("net.minecraft.inventory.ContainerEnchantment", new ContainerInfo(false, true, false, getVanillaSlotMapInfo("containerEnchantmentSlots")));
      standardClasses.put("net.minecraft.inventory.ContainerFurnace", new ContainerInfo(true, true, false, getVanillaSlotMapInfo("containerFurnaceSlots")));
      standardClasses.put("net.minecraft.inventory.ContainerDispenser", new ContainerInfo(true, false, true, (short)3, getVanillaSlotMapInfo("containerChestDispenserSlots")));
      standardClasses.put("net.minecraft.inventory.ContainerChest", new ContainerInfo(true, false, true, getVanillaSlotMapInfo("containerChestDispenserSlots")));
      standardClasses.put("net.minecraft.inventory.ContainerShulkerBox", new ContainerInfo(true, false, true, getVanillaSlotMapInfo("containerChestDispenserSlots")));

      try {
         configClasses = CompatibilityConfigLoader.load("config/InvTweaksCompatibility.xml");
      } catch (FileNotFoundException var1) {
         configClasses = new HashMap();
      } catch (Exception var2) {
         configClasses = new HashMap();
         var2.printStackTrace();
      }

   }

   private static MethodNode findAnnotatedMethod(@NotNull ClassNode cn, @NotNull String annotationDesc) {
      label26:
      for(MethodNode method : cn.methods) {
         if (method.visibleAnnotations != null) {
            Iterator var4 = method.visibleAnnotations.iterator();

            while(true) {
               if (!var4.hasNext()) {
                  continue label26;
               }

               AnnotationNode methodAnnotation = (AnnotationNode)var4.next();
               if (annotationDesc.equals(methodAnnotation.desc)) {
                  break;
               }
            }

            return method;
         }
      }

      return null;
   }

   private static byte[] doTransform(byte[] bytes, Consumer<ClassNode> transform) {
      ClassReader cr = new ClassReader(bytes);
      ClassNode cn = new ClassNode(262144);
      ClassWriter cw = new ClassWriter(3);
      cr.accept(cn, 0);
      transform.accept(cn);
      cn.accept(cw);
      return cw.toByteArray();
   }

   @Nullable
   public byte[] transform(String name, String transformedName, @Nullable byte[] bytes) {
      if (bytes != null && bytes.length != 0) {
         if ("net.minecraft.client.gui.GuiTextField".equals(transformedName)) {
            return doTransform(bytes, ContainerTransformer::transformTextField);
         } else if ("net.minecraft.inventory.Container".equals(transformedName)) {
            return doTransform(bytes, ContainerTransformer::transformBaseContainer);
         } else if ("net.minecraft.client.gui.inventory.GuiContainerCreative$ContainerCreative".equals(transformedName)) {
            return doTransform(bytes, ContainerTransformer::transformCreativeContainer);
         } else if ("net.minecraft.inventory.ContainerHorseInventory".equals(transformedName)) {
            return doTransform(bytes, ContainerTransformer::transformHorseInventoryContainer);
         } else if ("invtweaks.InvTweaksObfuscation".equals(transformedName)) {
            return doTransform(bytes, ContainerTransformer::transformInvTweaksObfuscation);
         } else {
            ContainerInfo standardInfo = (ContainerInfo)standardClasses.get(transformedName);
            if (standardInfo != null) {
               return doTransform(bytes, (cnx) -> {
                  transformContainer(cn, standardInfo);
               });
            } else {
               ContainerInfo configInfo = (ContainerInfo)configClasses.get(transformedName);
               if (configInfo != null) {
                  return doTransform(bytes, (cnx) -> {
                     transformContainer(cn, configInfo);
                  });
               } else {
                  for(String uninterestingPackage : uninterestingPackages) {
                     if (transformedName.startsWith(uninterestingPackage)) {
                        return bytes;
                     }
                  }

                  ClassReader cr = new ClassReader(bytes);
                  ClassNode cn = new ClassNode(262144);
                  ClassWriter cw = new ClassWriter(3);
                  cr.accept(cn, 0);
                  if (cn.visibleAnnotations != null) {
                     for(AnnotationNode annotation : cn.visibleAnnotations) {
                        if (annotation != null) {
                           ContainerInfo apiInfo = null;
                           if ("Linvtweaks/api/container/ChestContainer;".equals(annotation.desc)) {
                              short rowSize = 9;
                              boolean isLargeChest = false;
                              boolean showButtons = true;
                              if (annotation.values != null) {
                                 for(int i = 0; i < annotation.values.size(); i += 2) {
                                    String valueName = (String)annotation.values.get(i);
                                    Object value = annotation.values.get(i + 1);
                                    byte var19 = -1;
                                    switch(valueName.hashCode()) {
                                    case -109640368:
                                       if (valueName.equals("isLargeChest")) {
                                          var19 = 1;
                                       }
                                       break;
                                    case 629511620:
                                       if (valueName.equals("showButtons")) {
                                          var19 = 2;
                                       }
                                       break;
                                    case 1386516731:
                                       if (valueName.equals("rowSize")) {
                                          var19 = 0;
                                       }
                                    }

                                    switch(var19) {
                                    case 0:
                                       rowSize = (short)((Integer)value).intValue();
                                       break;
                                    case 1:
                                       isLargeChest = ((Boolean)value).booleanValue();
                                       break;
                                    case 2:
                                       showButtons = ((Boolean)value).booleanValue();
                                    }
                                 }
                              }

                              apiInfo = new ContainerInfo(showButtons, false, true, isLargeChest, rowSize);
                              MethodNode row_method = findAnnotatedMethod(cn, "Linvtweaks/api/container/ChestContainer$RowSizeCallback;");
                              if (row_method != null) {
                                 apiInfo.rowSizeMethod = new MethodInfo(Type.getMethodType(row_method.desc), Type.getObjectType(cn.name), row_method.name);
                              }

                              MethodNode large_method = findAnnotatedMethod(cn, "Linvtweaks/api/container/ChestContainer$IsLargeCallback;");
                              if (large_method != null) {
                                 apiInfo.largeChestMethod = new MethodInfo(Type.getMethodType(large_method.desc), Type.getObjectType(cn.name), large_method.name);
                              }
                           } else if (!"Linvtweaks/api/container/InventoryContainer;".equals(annotation.desc)) {
                              if ("Linvtweaks/api/container/IgnoreContainer;".equals(annotation.desc)) {
                                 transformBaseContainer(cn);
                                 cn.accept(cw);
                                 return cw.toByteArray();
                              }
                           } else {
                              boolean showOptions = true;
                              if (annotation.values != null) {
                                 for(int i = 0; i < annotation.values.size(); i += 2) {
                                    String valueName = (String)annotation.values.get(i);
                                    Object value = annotation.values.get(i + 1);
                                    if ("showOptions".equals(valueName)) {
                                       showOptions = ((Boolean)value).booleanValue();
                                    }
                                 }
                              }

                              apiInfo = new ContainerInfo(showOptions, true, false);
                           }

                           if (apiInfo != null) {
                              MethodNode method = findAnnotatedMethod(cn, "Linvtweaks/api/container/ContainerSectionCallback;");
                              if (method != null) {
                                 apiInfo.slotMapMethod = new MethodInfo(Type.getMethodType(method.desc), Type.getObjectType(cn.name), method.name);
                              }

                              transformContainer(cn, apiInfo);
                              cn.accept(cw);
                              return cw.toByteArray();
                           }
                        }
                     }
                  }

                  return bytes;
               }
            }
         }
      } else {
         return bytes;
      }
   }
}
