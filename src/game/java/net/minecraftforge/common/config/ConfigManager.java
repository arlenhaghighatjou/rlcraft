/*
 * Minecraft Forge
 * Copyright (c) 2016-2020.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation version 2.1
 * of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package net.minecraftforge.common.config;

import net.lax1dude.eaglercraft.v1_8.internal.vfs2.VFile2;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import com.google.common.collect.Maps;

import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.LoaderException;
import net.minecraftforge.fml.common.LoaderState;

import org.apache.commons.lang3.StringUtils;

public class ConfigManager
{
    static Map<Class<?>, ITypeAdapter> ADAPTERS = Maps.newHashMap();
    private static Map<String, Configuration> CONFIGS = Maps.newHashMap();
    private static Map<String, List<ConfigDescriptor>> MOD_CONFIGS = Maps.newHashMap();

    static
    {
        register(boolean.class,     TypeAdapters.bool);
        register(boolean[].class,   TypeAdapters.boolA);
        register(Boolean.class,     TypeAdapters.Bool);
        register(Boolean[].class,   TypeAdapters.BoolA);
        register(float.class,       TypeAdapters.flt);
        register(float[].class,     TypeAdapters.fltA);
        register(Float.class,       TypeAdapters.Flt);
        register(Float[].class,     TypeAdapters.FltA);
        register(double.class,      TypeAdapters.dbl);
        register(double[].class,    TypeAdapters.dblA);
        register(Double.class,      TypeAdapters.Dbl);
        register(Double[].class,    TypeAdapters.DblA);
        register(byte.class,        TypeAdapters.byt);
        register(byte[].class,      TypeAdapters.bytA);
        register(Byte.class,        TypeAdapters.Byt);
        register(Byte[].class,      TypeAdapters.BytA);
        register(char.class,        TypeAdapters.chr);
        register(char[].class,      TypeAdapters.chrA);
        register(Character.class,   TypeAdapters.Chr);
        register(Character[].class, TypeAdapters.ChrA);
        register(short.class,       TypeAdapters.shrt);
        register(short[].class,     TypeAdapters.shrtA);
        register(Short.class,       TypeAdapters.Shrt);
        register(Short[].class,     TypeAdapters.ShrtA);
        register(int.class,         TypeAdapters.int_);
        register(int[].class,       TypeAdapters.intA);
        register(Integer.class,     TypeAdapters.Int);
        register(Integer[].class,   TypeAdapters.IntA);
        register(String.class,      TypeAdapters.Str);
        register(String[].class,    TypeAdapters.StrA);
    }
    private static void register(Class<?> cls, ITypeAdapter adpt)
    {
        ADAPTERS.put(cls, adpt);
    }

    public static void register(String modid, String name, String category, String langKey, ConfigEntry... entries)
    {
        MOD_CONFIGS.computeIfAbsent(modid, k -> new ArrayList<>()).add(new ConfigDescriptor(modid, name, category, langKey, entries));
    }

    /**
     * Bounces to sync().
     * TODO: remove
     */
    public static void load(String modid, Config.Type type)
    {
        sync(modid, type);
    }

    /**
     * Synchronizes configuration data between the file on disk, the {@code Configuration} object and the registered
     * mod configuration entries containing the configuration variables.
     *
     * When first called, this method will try to load the configuration from disk. If this fails, because the file
     * does not exist, it will be created with default values derived from the mods config entries default values
     * and comments and ranges, as well as configuration names based on the registered {@code ConfigDescriptor}.
     *
     * Note, that this method is being called by the {@link net.minecraftforge.fml.common.FMLModContainer}, so the mod needn't call it in init().
     *
     * If this method is called after the initial load, it will check whether the values in the Configuration object differ
     * from the values in the corresponding variables. If they differ, it will either overwrite the variables if the Configuration
     * object is marked as changed (e.g. if it was changed with the ConfigGui) or otherwise overwrite the Configuration object's values.
     * It then proceeds to saving the changes to disk.
     * @param modid the mod's ID for which the configuration shall be loaded
     * @param type the configuration type, currently always {@code Config.Type.INSTANCE}
     */
    public static void sync(String modid, Config.Type type)
    {
        FMLLog.log.debug("Attempting to inject config entries into {} for type {}", modid, type);
        VFile2 configDir = Loader.instance().getConfigDir();
        List<ConfigDescriptor> descriptors = MOD_CONFIGS.get(modid);

        if (descriptors == null)
            return;

        for (int i = 0, l = descriptors.size(); i < l; ++i)
        {
            ConfigDescriptor descriptor = descriptors.get(i);
            try
            {
                VFile2 file = new VFile2(configDir, descriptor.name + ".cfg");

                Configuration cfg = CONFIGS.get(file.getPath());
                if (cfg == null)
                {
                    cfg = new Configuration(file);
                    cfg.load();
                    CONFIGS.put(file.getPath(), cfg);
                }

                sync(cfg, descriptor.entries, modid, descriptor.category, !Loader.instance().hasReachedState(LoaderState.AVAILABLE));

                cfg.save();

            }
            catch (Exception e)
            {
                FMLLog.log.error("An error occurred trying to load a config for {} into {}", modid, descriptor.name, e);
                throw new LoaderException(e);
            }
        }
    }

    public static ConfigDescriptor[] getModConfigs(String modid)
    {
        List<ConfigDescriptor> descriptors = MOD_CONFIGS.get(modid);
        return descriptors == null ? new ConfigDescriptor[0] : descriptors.toArray(new ConfigDescriptor[0]);
    }

    public static boolean hasConfigForMod(String modid)
    {
        return MOD_CONFIGS.containsKey(modid);
    }

    // =======================================================
    //                    INTERNAL
    // =======================================================
    static Configuration getConfiguration(String modid, String name)
    {
        if (Strings.isNullOrEmpty(name))
            name = modid;
        VFile2 configDir = Loader.instance().getConfigDir();
        VFile2 configFile = new VFile2(configDir, name + ".cfg");
        return CONFIGS.get(configFile.getPath());
    }

    private static void sync(Configuration cfg, ConfigEntry[] entries, String modid, String category, boolean loading)
    {
        for (int e = 0; e < entries.length; ++e)
        {
            ConfigEntry entry = entries[e];

            String comment = entry.comment;

            String langKey = entry.langKey != null ? entry.langKey : modid + "." + (category.isEmpty() ? "" : category + Configuration.CATEGORY_SPLITTER) + entry.name.toLowerCase(Locale.ENGLISH);

            boolean requiresMcRestart = entry.requiresMcRestart;
            boolean requiresWorldRestart = entry.requiresWorldRestart;
            boolean hasSlidingControl = entry.slidingOption;

            if (entry.children == null)
            {
                if (Strings.isNullOrEmpty(category))
                    throw new RuntimeException("An empty category may not contain anything but objects representing categories!");
                try
                {
                    IFieldWrapper wrapper = FieldWrapper.get(entry, category);
                    ITypeAdapter adapt = wrapper.getTypeAdapter();
                    Property.Type propType = adapt.getType();

                    for (String key : wrapper.getKeys()) //Iterate the fully qualified property names the field provides
                    {
                        String suffix = StringUtils.replaceOnce(key, wrapper.getCategory() + Configuration.CATEGORY_SPLITTER, "");

                        boolean existed = exists(cfg, wrapper.getCategory(), suffix);
                        if (!existed || loading) //Creates keys in category specified by the wrapper if new ones are programaticaly added
                        {
                            Property property = property(cfg, wrapper.getCategory(), suffix, propType, adapt.isArrayAdapter());
                            adapt.setDefaultValue(property, wrapper.getValue(key));
                            if (!existed)
                                adapt.setValue(property, wrapper.getValue(key));
                            else
                                wrapper.setValue(key, adapt.getValue(property));
                        }
                        else //If the key is not new, sync according to shouldReadFromVar()
                        {
                            Property property = property(cfg, wrapper.getCategory(), suffix, propType, adapt.isArrayAdapter());
                            Object propVal = adapt.getValue(property);
                            Object mapVal = wrapper.getValue(key);
                            if (shouldReadFromVar(property, propVal, mapVal))
                                adapt.setValue(property, mapVal);
                            else
                                wrapper.setValue(key, propVal);
                        }
                    }


                    ConfigCategory confCat = cfg.getCategory(wrapper.getCategory());

                    for (Property property : confCat.getOrderedValues()) //Iterate the properties to check for new data from the config side
                    {
                        String key = confCat.getQualifiedName() + Configuration.CATEGORY_SPLITTER + property.getName();
                        if (!wrapper.handlesKey(key))
                            continue;

                        if (loading || !wrapper.hasKey(key))
                        {
                            Object value = wrapper.getTypeAdapter().getValue(property);
                            wrapper.setValue(key, value);
                        }
                    }

                    if (loading)
                        wrapper.setupConfiguration(cfg, comment, langKey, requiresMcRestart, requiresWorldRestart, hasSlidingControl);

                }
                catch (Exception ex)
                {
                    String format = "Error syncing entry '%s' of mod '%s'!";
                    String error = String.format(format, entry.name, modid);
                    throw new RuntimeException(error, ex);
                }
            }
            else
            {
                //Setup the sub category with its respective name, comment, language key, etc.
                String sub = (category.isEmpty() ? "" : category + Configuration.CATEGORY_SPLITTER) + entry.name.toLowerCase(Locale.ENGLISH);
                ConfigCategory confCat = cfg.getCategory(sub);
                confCat.setComment(comment);
                confCat.setLanguageKey(langKey);
                confCat.setRequiresMcRestart(requiresMcRestart);
                confCat.setRequiresWorldRestart(requiresWorldRestart);

                sync(cfg, entry.children, modid, sub, loading);
            }
        }
    }

    static final Joiner NEW_LINE = Joiner.on('\n');
    static final Joiner PIPE = Joiner.on('|');

    private static Property property(Configuration cfg, String category, String property, Property.Type type, boolean isList)
    {
        Property prop = cfg.getCategory(category).get(property);
        if (prop == null)
        {
            if (isList)
                prop = new Property(property, new String[0], type);
            else
                prop = new Property(property, (String)null, type);
            cfg.getCategory(category).put(property, prop);
        }
        return prop;
    }

    private static boolean exists(Configuration cfg, String category, String property)
    {
        return cfg.hasCategory(category) && cfg.getCategory(category).containsKey(property);
    }

    private static boolean shouldReadFromVar(Property property, Object propValue, Object fieldValue)
    {
        if (!propValue.equals(fieldValue))
        {
            if (property.hasChanged())
                return false;
            else
                return true;
        }
        return false;
    }

}
