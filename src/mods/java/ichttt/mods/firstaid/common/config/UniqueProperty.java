package ichttt.mods.firstaid.common.config;

import java.lang.reflect.Field;
import java.util.Locale;
import java.util.Objects;
import net.minecraftforge.common.config.Config.LangKey;
import net.minecraftforge.common.config.Config.Name;
import net.minecraftforge.fml.client.config.ConfigGuiType;
import net.minecraftforge.fml.client.config.IConfigElement;

public class UniqueProperty {
   private final String name;
   private final String langKey;
   private final UniqueProperty.Type type;

   public static String getLangKey(Field field, String modid, String category) {
      LangKey la = (LangKey)field.getAnnotation(LangKey.class);
      return la != null ? la.value() : modid + "." + (category.isEmpty() ? "" : category + ".") + field.getName().toLowerCase(Locale.ENGLISH);
   }

   public static UniqueProperty fromProperty(Field field, String modid, String category, UniqueProperty.Type type) {
      String name;
      if (field.isAnnotationPresent(Name.class)) {
         name = ((Name)field.getAnnotation(Name.class)).value();
      } else {
         name = field.getName();
      }

      String langKey = getLangKey(field, modid, category);
      return new UniqueProperty(name, langKey, type);
   }

   public UniqueProperty(String name, String langKey, UniqueProperty.Type type) {
      this.name = name;
      this.langKey = langKey;
      this.type = type;
   }

   public boolean matches(IConfigElement element) {
      boolean var10000;
      if (element.getLanguageKey().equals(this.langKey)) {
         label19: {
            if (element.isProperty()) {
               if (!UniqueProperty.Type.fromType(element.getType()).equals(this.type)) {
                  break label19;
               }
            } else if (this.type != UniqueProperty.Type.CATEGORY) {
               break label19;
            }

            if (Objects.equals(element.getName(), this.name)) {
               var10000 = true;
               return var10000;
            }
         }
      }

      var10000 = false;
      return var10000;
   }

   public static enum Type {
      STRING,
      INTEGER,
      BOOLEAN,
      DOUBLE,
      COLOR,
      MOD_ID,
      CATEGORY,
      UNKNOWN;

      private Type() {
      }

      public static UniqueProperty.Type fromType(net.minecraftforge.common.config.Property.Type type) {
         if (type == null) {
            return UNKNOWN;
         } else {
            switch(type) {
            case STRING:
               return STRING;
            case INTEGER:
               return INTEGER;
            case BOOLEAN:
               return BOOLEAN;
            case DOUBLE:
               return DOUBLE;
            case COLOR:
               return COLOR;
            case MOD_ID:
               return MOD_ID;
            default:
               throw new RuntimeException("Unknown property type " + type);
            }
         }
      }

      public static UniqueProperty.Type fromType(ConfigGuiType type) {
         if (type == null) {
            return UNKNOWN;
         } else {
            switch(type) {
            case STRING:
               return STRING;
            case INTEGER:
               return INTEGER;
            case BOOLEAN:
               return BOOLEAN;
            case DOUBLE:
               return DOUBLE;
            case COLOR:
               return COLOR;
            case MOD_ID:
               return MOD_ID;
            case CONFIG_CATEGORY:
               return CATEGORY;
            default:
               throw new RuntimeException("Unknown property type " + type);
            }
         }
      }
   }
}
