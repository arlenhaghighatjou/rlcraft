package com.teamacronymcoders.contenttweaker.api.utils;

import com.google.common.collect.Lists;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import javax.annotation.Nonnull;

public class ResourceList<RESOURCE> {
   private Class<RESOURCE> resourceClass;
   private List<Class> classesToCheck;
   protected Map<String, RESOURCE> resources;
   private Map<String, String> mappings;

   public ResourceList(@Nonnull Class<RESOURCE> resourceClass) {
      this(resourceClass, resourceClass, (Map)null);
   }

   public ResourceList(@Nonnull Class<RESOURCE> resourceClass, Map<String, String> mappings) {
      this(resourceClass, resourceClass, mappings);
   }

   public ResourceList(@Nonnull Class<RESOURCE> resourceClass, @Nonnull Class classToCheck) {
      this(resourceClass, Lists.newArrayList(new Class[]{classToCheck}), (Map)null);
   }

   public ResourceList(@Nonnull Class<RESOURCE> resourceClass, @Nonnull Class classToCheck, Map<String, String> mappings) {
      this(resourceClass, Lists.newArrayList(new Class[]{classToCheck}), mappings);
   }

   public ResourceList(@Nonnull Class<RESOURCE> resourceClass, @Nonnull List<Class> classesToCheck) {
      this(resourceClass, classesToCheck, (Map)null);
   }

   public ResourceList(@Nonnull Class<RESOURCE> resourceClass, @Nonnull List<Class> classesToCheck, Map<String, String> mappings) {
      this.resourceClass = resourceClass;
      this.classesToCheck = classesToCheck;
      this.resources = new HashMap();
      this.mappings = mappings;
      this.loadResources();
   }

   public void addClassToCheck(Class classToCheck) {
      this.classesToCheck.add(classToCheck);
   }

   public void addResource(String name, RESOURCE resource) {
      if (this.mappings != null && this.mappings.containsKey(name)) {
         name = (String)this.mappings.get(name);
      }

      this.resources.put(name.toLowerCase(Locale.US), resource);
   }

   public RESOURCE getResource(String name) {
      return (RESOURCE)this.resources.get(name.toLowerCase(Locale.US));
   }

   public Collection<RESOURCE> getAllResources() {
      return this.resources.values();
   }

   public List<String> getAllNames() {
      return Lists.newArrayList(this.resources.keySet());
   }

   private void loadResources() {
      this.classesToCheck.forEach((clazz) -> {
         Reflection.getStaticFieldsOfType(this.resourceClass, clazz).forEach(this::addResource);
      });
   }
}
