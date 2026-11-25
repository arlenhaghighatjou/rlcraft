package com.charles445.rltweaker.command;

import com.charles445.rltweaker.RLTweaker;
import com.charles445.rltweaker.config.ModConfig;
import com.charles445.rltweaker.config.annotation.RLConfig;
import com.charles445.rltweaker.config.annotation.SpecialEnum;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import javax.annotation.Nullable;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.common.config.FieldWrapper;
import net.minecraftforge.common.config.Config.Type;

public class CommandRLTweakerConfig extends CommandBase {
   private final List<String> tabCompletionsCommands = Arrays.asList("rlcraft282", "improvementsonly");

   public CommandRLTweakerConfig() {
   }

   public String getName() {
      return "rltweakerconfig";
   }

   public String getUsage(ICommandSender sender) {
      return "/rltweakerconfig help";
   }

   public int getRequiredPermissionLevel() {
      return 4;
   }

   public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
      if (args.length == 0) {
         this.help(sender);
      } else {
         String firstArgument = args[0].toLowerCase(Locale.ENGLISH);
         if (firstArgument.equals("help")) {
            this.inform("/rltweakerconfig <type>", sender);
            this.inform("Sets RLTweaker config to default values based on the config type given", sender);
            this.inform("Available config types are:\nimprovementsonly\nrlcraft282", sender);
            this.inform("WARNING, this replaces all rltweaker config! Add --confirm to the end of the command to confirm", sender);
         } else {
            boolean confirm = false;

            for(int i = 0; i < args.length; ++i) {
               if (args[i].equals("--confirm")) {
                  confirm = true;
                  break;
               }
            }

            if (!confirm) {
               this.inform("WARNING, this replaces all rltweaker config! Add --confirm to the end of the command to confirm", sender);
            } else {
               byte var7 = -1;
               switch(firstArgument.hashCode()) {
               case -1148788538:
                  if (firstArgument.equals("rlcraft282")) {
                     var7 = 0;
                  }
                  break;
               case -881961079:
                  if (firstArgument.equals("improvementsonly")) {
                     var7 = 1;
                  }
               }

               switch(var7) {
               case 0:
                  this.updateConfigDefaultsWithAnnotation(RLConfig.RLCraftTwoEightTwo.class, sender);
                  break;
               case 1:
                  this.updateConfigDefaultsWithAnnotation(RLConfig.ImprovementsOnly.class, sender);
                  break;
               default:
                  this.help(sender);
               }

            }
         }
      }
   }

   private void updateConfigDefaultsWithAnnotation(Class<? extends Annotation> annotation, ICommandSender sender) {
      try {
         this.diveInstance(ModConfig.client, annotation);
         this.diveInstance(ModConfig.patches, annotation);
         this.diveInstance(ModConfig.server, annotation);
         ConfigManager.sync("rltweaker", Type.INSTANCE);
         this.inform("Finished replacing config values, restart the game to complete the configuration", sender);
      } catch (IllegalAccessException | NoSuchMethodException | SecurityException | InvocationTargetException | IllegalArgumentException var4) {
         var4.printStackTrace();
         this.inform("Failed to set defaults, there was an error. Check console for details.", sender);
      }

   }

   private void diveInstance(Object instance, Class<? extends Annotation> annotationClazz) throws IllegalArgumentException, IllegalAccessException, NoSuchMethodException, SecurityException, InvocationTargetException {
      if (instance != null) {
         for(Field f : instance.getClass().getDeclaredFields()) {
            if (!f.isSynthetic()) {
               int modifiers = f.getModifiers();
               if (Modifier.isPublic(modifiers)) {
                  boolean hasSpecial = false;
                  if (f.isAnnotationPresent(RLConfig.SpecialSignature.class)) {
                     hasSpecial = true;
                  }

                  if (!hasSpecial && !f.isAnnotationPresent(annotationClazz)) {
                     if (FieldWrapper.hasWrapperFor(f)) {
                        RLTweaker.logger.debug("Unmarked default for: " + annotationClazz.getSimpleName() + " : " + instance.getClass().getSimpleName() + " : " + f.getType().getName() + " : " + f.getName());
                     } else if (f.getType().getName().startsWith("com.charles445.rltweaker.config.")) {
                        f.setAccessible(true);
                        this.diveInstance(f.get(instance), annotationClazz);
                     }
                  } else {
                     f.setAccessible(true);
                     if (hasSpecial) {
                        RLConfig.SpecialSignature signature = (RLConfig.SpecialSignature)RLConfig.SpecialSignature.class.cast(f.getAnnotation(RLConfig.SpecialSignature.class));
                        String annotationValue = this.testSpecial(signature.value(), signature.pass(), signature.fail());
                        if (this.replaceFieldWithNewValue(instance, f, annotationValue, f.getType().getName())) {
                           RLTweaker.logger.debug("Replaced field: " + instance.getClass().getSimpleName() + " : " + f.getName());
                        }
                     } else {
                        Annotation annotation = (Annotation)annotationClazz.cast(f.getAnnotation(annotationClazz));
                        Method m = annotationClazz.getDeclaredMethod("value");
                        if (m == null) {
                           RLTweaker.logger.error("Null method for value in class: " + annotationClazz.getSimpleName() + " : " + instance.getClass().getSimpleName() + " : " + f.getType().getName() + " : " + f.getName());
                        } else {
                           String annotationValue = (String)m.invoke(annotation);
                           if (annotationValue == null) {
                              RLTweaker.logger.error("Null string for annotaton value in class: " + annotationClazz.getSimpleName() + " : " + instance.getClass().getSimpleName() + " : " + f.getType().getName() + " : " + f.getName());
                           } else if (this.replaceFieldWithNewValue(instance, f, annotationValue, f.getType().getName())) {
                              RLTweaker.logger.debug("Replaced field: " + instance.getClass().getSimpleName() + " : " + f.getName());
                           }
                        }
                     }
                  }
               }
            }
         }

      }
   }

   private String testSpecial(SpecialEnum special, String pass, String fail) {
      switch(special) {
      case MAX_ENTITY_RADIUS_HIGH:
         return World.MAX_ENTITY_RADIUS > 2.0D ? pass : fail;
      default:
         RLTweaker.logger.error("testSpecial failed, invalid case");
         return fail;
      }
   }

   private boolean replaceFieldWithNewValue(Object instance, Field f, String annotationValue, String typeString) {
      try {
         byte var6 = -1;
         switch(typeString.hashCode()) {
         case -1325958191:
            if (typeString.equals("double")) {
               var6 = 2;
            }
            break;
         case 2889:
            if (typeString.equals("[D")) {
               var6 = 3;
            }
            break;
         case 2894:
            if (typeString.equals("[I")) {
               var6 = 5;
            }
            break;
         case 2911:
            if (typeString.equals("[Z")) {
               var6 = 1;
            }
            break;
         case 104431:
            if (typeString.equals("int")) {
               var6 = 4;
            }
            break;
         case 64711720:
            if (typeString.equals("boolean")) {
               var6 = 0;
            }
            break;
         case 392722245:
            if (typeString.equals("[Ljava.lang.String;")) {
               var6 = 7;
            }
            break;
         case 1195259493:
            if (typeString.equals("java.lang.String")) {
               var6 = 6;
            }
         }

         switch(var6) {
         case 0:
            return this.replaceBoolean(instance, f, annotationValue);
         case 1:
            return this.unsupported(typeString);
         case 2:
            return this.replaceDouble(instance, f, annotationValue);
         case 3:
            return this.unsupported(typeString);
         case 4:
            return this.replaceInteger(instance, f, annotationValue);
         case 5:
            return this.replaceIntegerArray(instance, f, annotationValue);
         case 6:
            return this.replaceString(instance, f, annotationValue);
         case 7:
            return this.replaceStringArray(instance, f, annotationValue);
         default:
            return this.unsupported(typeString);
         }
      } catch (IllegalAccessException | IllegalArgumentException var7) {
         RLTweaker.logger.error("Error replacing field: " + annotationValue + " " + typeString, var7);
         return false;
      }
   }

   private boolean unsupported(String typeString) {
      RLTweaker.logger.error("Unsupported config default type: " + typeString);
      return false;
   }

   private boolean replaceBoolean(Object instance, Field f, String annotationValue) throws IllegalArgumentException, IllegalAccessException {
      String var4 = annotationValue.trim().toLowerCase(Locale.ENGLISH);
      byte var5 = -1;
      switch(var4.hashCode()) {
      case 3569038:
         if (var4.equals("true")) {
            var5 = 1;
         }
         break;
      case 97196323:
         if (var4.equals("false")) {
            var5 = 0;
         }
      }

      switch(var5) {
      case 0:
         f.setBoolean(instance, false);
         return true;
      case 1:
         f.setBoolean(instance, true);
         return true;
      default:
         RLTweaker.logger.error("Invalid boolean config default: " + instance.getClass().getSimpleName() + " : " + f.getName() + " : " + annotationValue);
         return false;
      }
   }

   private boolean replaceDouble(Object instance, Field f, String annotationValue) throws IllegalArgumentException, IllegalAccessException {
      try {
         f.setDouble(instance, Double.parseDouble(annotationValue));
         return true;
      } catch (NumberFormatException var5) {
         RLTweaker.logger.error("Invalid double config default: " + instance.getClass().getSimpleName() + " : " + f.getName() + " : " + annotationValue);
         return false;
      }
   }

   private boolean replaceInteger(Object instance, Field f, String annotationValue) throws IllegalArgumentException, IllegalAccessException {
      try {
         f.setInt(instance, Integer.parseInt(annotationValue));
         return true;
      } catch (NumberFormatException var5) {
         RLTweaker.logger.error("Invalid int config default: " + instance.getClass().getSimpleName() + " : " + f.getName() + " : " + annotationValue);
         return false;
      }
   }

   private boolean replaceString(Object instance, Field f, String annotationValue) throws IllegalArgumentException, IllegalAccessException {
      f.set(instance, annotationValue);
      return true;
   }

   private boolean replaceIntegerArray(Object instance, Field f, String annotationValue) throws IllegalArgumentException, IllegalAccessException {
      String[] split = annotationValue.split(this.getSplitRegex());
      int[] container = new int[split.length];

      try {
         for(int i = 0; i < split.length; ++i) {
            container[i] = Integer.parseInt(split[i]);
         }
      } catch (NumberFormatException var7) {
         RLTweaker.logger.error("Invalid int[] config default: " + instance.getClass().getSimpleName() + " : " + f.getName() + " : " + annotationValue);
         return false;
      }

      f.set(instance, container);
      return true;
   }

   private boolean replaceStringArray(Object instance, Field f, String annotationValue) throws IllegalArgumentException, IllegalAccessException {
      String[] split = annotationValue.split(this.getSplitRegex());
      f.set(instance, split);
      return true;
   }

   private String getSplitRegex() {
      return "\\|";
   }

   public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {
      if (args.length == 1) {
         return getListOfStringsMatchingLastWord(args, this.tabCompletionsCommands);
      } else {
         return args.length == 0 ? this.tabCompletionsCommands : Collections.emptyList();
      }
   }

   private void help(ICommandSender sender) {
      this.inform(this.getUsage(sender), sender);
   }

   public void inform(String s, ICommandSender sender) {
      sender.sendMessage(new TextComponentString(s));
   }
}
