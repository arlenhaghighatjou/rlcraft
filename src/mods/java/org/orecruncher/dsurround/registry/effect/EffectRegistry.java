package org.orecruncher.dsurround.registry.effect;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import javax.annotation.Nonnull;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.orecruncher.dsurround.ModBase;
import org.orecruncher.dsurround.ModOptions;
import org.orecruncher.dsurround.registry.Registry;
import org.orecruncher.dsurround.registry.config.EntityConfig;
import org.orecruncher.dsurround.registry.config.ModConfiguration;
import org.orecruncher.dsurround.registry.effect.theme.GloamwoodTheme;
import org.orecruncher.dsurround.registry.effect.theme.ThemeInfo;

@SideOnly(Side.CLIENT)
public class EffectRegistry extends Registry {
   public static final EntityEffectInfo DEFAULT = new EntityEffectInfo();
   public static final ResourceLocation DEFAULT_THEME = new ResourceLocation("dsurround", "default");
   private static final ThemeInfo DEFAULT_THEME_INFO = new ThemeInfo();
   protected final Map<Class<? extends Entity>, EntityEffectInfo> effects = new Reference2ObjectOpenHashMap();
   protected EntityEffectInfo playerEffects;
   protected final Map<ResourceLocation, ThemeInfo> themes;
   protected ThemeInfo activeTheme;

   public EffectRegistry() {
      super("Effects Registry");
      this.playerEffects = DEFAULT;
      this.themes = new Object2ObjectOpenHashMap();
      this.activeTheme = DEFAULT_THEME_INFO;
   }

   protected void preInit() {
      this.playerEffects = DEFAULT;
      this.effects.clear();
      this.themes.clear();
      this.activeTheme = DEFAULT_THEME_INFO;
      this.themes.put(DEFAULT_THEME, DEFAULT_THEME_INFO);
      this.themes.put(new ResourceLocation("dsurround", "gloamwood"), new GloamwoodTheme());
   }

   protected void init(@Nonnull ModConfiguration cfg) {
      for(Entry<String, EntityConfig> e : cfg.entities.entrySet()) {
         String entityName = (String)e.getKey();
         EntityConfig entityEffects = (EntityConfig)e.getValue();
         if ("minecraft:player".equals(entityName)) {
            this.playerEffects = new EntityEffectInfo(entityEffects);
         } else {
            Class<? extends Entity> clazz = EntityList.getClassFromName(entityName);
            if (clazz != null) {
               this.effects.put(clazz, new EntityEffectInfo(entityEffects));
            } else {
               ModBase.log().warn("Unrecognized resource name for entity: %s", new Object[]{entityName});
            }
         }
      }

   }

   protected void postInit() {
      label36:
      for(ResourceLocation r : EntityList.getEntityNameList()) {
         Class<? extends Entity> clazz = EntityList.getClass(r);
         if (clazz != null) {
            if (!this.effects.containsKey(clazz)) {
               Iterator var4 = this.effects.entrySet().iterator();

               Entry<Class<? extends Entity>, EntityEffectInfo> e;
               while(true) {
                  if (!var4.hasNext()) {
                     continue label36;
                  }

                  e = (Entry)var4.next();
                  if (((Class)e.getKey()).isAssignableFrom(clazz)) {
                     break;
                  }
               }

               this.effects.put(clazz, e.getValue());
            }
         } else {
            ModBase.log().debug("Forge reported entity %s but not found in it's own registry!", new Object[]{r.toString()});
         }
      }

   }

   protected void complete() {
      if (ModOptions.logging.enableDebugLogging) {
         ModBase.log().info("Entity Effect Configuration", new Object[0]);
         ModBase.log().info("===========================", new Object[0]);

         for(ResourceLocation r : EntityList.getEntityNameList()) {
            Class<?> clazz = EntityList.getClass(r);
            if (clazz != null) {
               EntityEffectInfo info = (EntityEffectInfo)this.effects.getOrDefault(clazz, DEFAULT);
               ModBase.log().info("%s = %s", new Object[]{r.toString(), info.toString()});
            }
         }
      }

   }

   @Nonnull
   public EntityEffectInfo getEffects(@Nonnull Entity entity) {
      return entity instanceof EntityPlayer ? this.playerEffects : (EntityEffectInfo)this.effects.getOrDefault(entity.getClass(), DEFAULT);
   }

   @Nonnull
   public ThemeInfo setTheme(@Nonnull ResourceLocation theme) {
      this.activeTheme = (ThemeInfo)this.themes.getOrDefault(theme, DEFAULT_THEME_INFO);
      return this.activeTheme;
   }
}
