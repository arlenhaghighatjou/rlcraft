package net.silentchaos512.scalinghealth.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.config.Configuration;

/** @deprecated */
@Deprecated
public class EntityMatchList {
   private static final Pattern WILDCARD_PATTERN = Pattern.compile("\\*");
   private final List<String> list = new ArrayList();
   private boolean whitelist = false;

   public EntityMatchList() {
   }

   public void add(String str) {
      this.list.add(str);
   }

   /** @deprecated */
   @Deprecated
   public void clear() {
      this.list.clear();
   }

   public boolean matches(Entity entity) {
      boolean contains = this.contains(entity);
      return this.whitelist == contains;
   }

   /** @deprecated */
   @Deprecated
   public boolean contains(Entity entity) {
      ResourceLocation resource = EntityList.getKey(entity);
      if (resource == null) {
         return false;
      } else {
         String id = resource.toString();
         String idOld = EntityList.getEntityString(entity);

         for(String entry : this.list) {
            if (entry.endsWith("*")) {
               String regex = WILDCARD_PATTERN.matcher(entry).replaceAll(Matcher.quoteReplacement(".*"));
               return id.matches("(?i)" + regex);
            }

            if (entry.equalsIgnoreCase(id) || entry.equalsIgnoreCase(idOld) || entry.equalsIgnoreCase("minecraft:" + id)) {
               return true;
            }
         }

         return false;
      }
   }

   public void loadConfig(Configuration config, String name, String category, String[] defaults, boolean defaultWhitelist, String comment) {
      this.clear();
      Collections.addAll(this.list, config.getStringList(name + " List", category, defaults, comment));
      this.whitelist = config.getBoolean(name + " IsWhitelist", category, defaultWhitelist, "If true, the list is a whitelist. Otherwise it is a blacklist.");
   }
}
