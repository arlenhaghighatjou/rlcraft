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

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;

import com.google.common.collect.Lists;

import org.apache.commons.lang3.StringUtils;

import static net.minecraftforge.common.config.ConfigManager.*;

public abstract class FieldWrapper implements IFieldWrapper
{
    protected String category, name;

    protected ConfigEntry entry;

    public FieldWrapper(String category, ConfigEntry entry)
    {
        this.entry = entry;
        this.category = category;
        this.name = entry.name;
    }

    public static IFieldWrapper get(ConfigEntry entry, String category)
    {
        if (entry.enumValues != null)
            return new EnumWrapper(category, entry);
        else if (entry.map != null)
            return new MapWrapper(category, entry);
        else if (ADAPTERS.get(entry.type) != null)
            return new PrimitiveWrapper(category, entry);
        else
            throw new IllegalArgumentException(String.format("Entries of type '%s' are not supported!", entry.type));
    }

    private static class MapWrapper extends FieldWrapper
    {
        private Map<String, Object> theMap;
        private final String baseName;
        ITypeAdapter adapter;

        private MapWrapper(String category, ConfigEntry entry)
        {
            super(category, entry);

            this.baseName = (this.category == null) ? "" : (this.category + ".") + this.name.toLowerCase(Locale.ENGLISH) + ".";
            this.theMap = entry.map;
            this.adapter = ADAPTERS.get(entry.type);

            if (this.adapter == null && Enum.class.isAssignableFrom(entry.type))
            {
                this.adapter = TypeAdapters.Str;
            }

            if (this.adapter == null)
                throw new IllegalArgumentException(String.format("The map '%s' has target values which are neither primitive nor an enum!", entry.name));
        }

        @Override
        public ITypeAdapter getTypeAdapter()
        {
            return adapter;
        }

        @Override
        public String[] getKeys()
        {
            String[] keyArray = new String[theMap.size()];

            int i = 0;
            for (String key : theMap.keySet())
            {
                keyArray[i++] = this.baseName + key;
            }

            return keyArray;
        }

        @Override
        public Object getValue(String key)
        {
            return theMap.get(getSuffix(key));
        }

        @Override
        public void setValue(String key, Object value)
        {
            theMap.put(getSuffix(key), value);
        }

        @Override
        public boolean hasKey(String key)
        {
            return theMap.containsKey(getSuffix(key));
        }

        @Override
        public boolean handlesKey(String key)
        {
            if (key == null)
                return false;
            return key.startsWith(this.baseName);
        }

        @Override
        public void setupConfiguration(Configuration cfg, String desc, String langKey, boolean reqMCRestart, boolean reqWorldRestart)
        {
            this.setupConfiguration(cfg, desc, langKey, reqMCRestart, reqWorldRestart, false);
        }

        @Override
        public void setupConfiguration(Configuration cfg, String desc, String langKey, boolean reqMCRestart, boolean reqWorldRestart, boolean hasSlidingControl)
        {
            ConfigCategory confCat = cfg.getCategory(getCategory());
            confCat.setComment(desc);
            confCat.setLanguageKey(langKey);
            confCat.setRequiresMcRestart(reqMCRestart);
            confCat.setRequiresWorldRestart(reqWorldRestart);
        }

        @Override
        public String getCategory()
        {
            return (this.category == null) ? "" : (this.category + ".") + this.name.toLowerCase(Locale.ENGLISH);
        }

        private String getSuffix(String key)
        {
            return StringUtils.replaceOnce(key, this.baseName, "");
        }

    }

    private static class EnumWrapper extends SingleValueFieldWrapper
    {

        private EnumWrapper(String category, ConfigEntry entry)
        {
            super(category, entry);
        }

        @Override
        public ITypeAdapter getTypeAdapter()
        {
            return TypeAdapters.Str;
        }

        @Override
        public Object getValue(String key)
        {
            if (!hasKey(key))
                throw new IllegalArgumentException("Unsupported Key!");

            return ((Enum<?>) entry.getter.get()).name();
        }

        @Override
        public void setValue(String key, Object value)
        {
            if (!hasKey(key))
                throw new IllegalArgumentException("Unsupported Key!");
            Enum<?>[] values = entry.enumValues;
            for (int i = 0; i < values.length; ++i)
            {
                if (values[i].name().equals(value))
                {
                    entry.setter.accept(values[i]);
                    return;
                }
            }
            throw new IllegalArgumentException("No enum constant " + value);
        }

        @Override
        public void setupConfiguration(Configuration cfg, String desc, String langKey, boolean reqMCRestart, boolean reqWorldRestart) {
            this.setupConfiguration(cfg, desc, langKey, reqMCRestart, reqWorldRestart,false);
        }

        @Override
        public void setupConfiguration(Configuration cfg, String desc, String langKey, boolean reqMCRestart, boolean reqWorldRestart, boolean hasSlidingControl)
        {
            super.setupConfiguration(cfg, desc, langKey, reqMCRestart, reqWorldRestart, hasSlidingControl);

            Property prop = cfg.getCategory(this.category).get(this.name); // Will be setup in general by ConfigManager

            List<String> listValidValues = Lists.newArrayList();
            List<String> listValidValuesDisplay = Lists.newArrayList();
            Enum<?>[] values = entry.enumValues;
            for (int i = 0; i < values.length; ++i)
            {
                listValidValues.add(values[i].name());
                listValidValuesDisplay.add(values[i].toString());
            }

            prop.setValidationPattern(Pattern.compile(PIPE.join(listValidValues)));
            prop.setValidValues(listValidValues.toArray(new String[0]));
            prop.setValidValuesDisplay(listValidValuesDisplay.toArray(new String[0]));

            String validValues = NEW_LINE.join(listValidValues);

            if (desc != null)
                prop.setComment(NEW_LINE.join(new String[] { desc, "Valid values:" }) + "\n" + validValues);
            else
                prop.setComment("Valid values:" + "\n" + validValues);
        }
    }

    private static class PrimitiveWrapper extends SingleValueFieldWrapper
    {

        private PrimitiveWrapper(String category, ConfigEntry entry)
        {
            super(category, entry);
        }

        @Override
        public ITypeAdapter getTypeAdapter()
        {
            return ADAPTERS.get(entry.type);
        }

        @Override
        public Object getValue(String key)
        {
            if (!hasKey(key))
                throw new IllegalArgumentException("Unknown key!");
            return entry.getter.get();
        }

        @Override
        public void setValue(String key, Object value)
        {
            if (!hasKey(key))
                throw new IllegalArgumentException("Unknown key: " + key);
            entry.setter.accept(value);
        }

        @Override
        public void setupConfiguration(Configuration cfg, String desc, String langKey, boolean reqMCRestart, boolean reqWorldRestart) {
            this.setupConfiguration(cfg, desc, langKey, reqMCRestart, reqWorldRestart,false);
        }

        @Override
        public void setupConfiguration(Configuration cfg, String desc, String langKey, boolean reqMCRestart, boolean reqWorldRestart, boolean hasSlidingControl)
        {
            super.setupConfiguration(cfg, desc, langKey, reqMCRestart, reqWorldRestart, hasSlidingControl);

            Property prop = cfg.getCategory(this.category).get(this.name);

            if (entry.hasRange)
            {
                String min;
                String max;
                if (entry.intRange)
                {
                    prop.setMinValue((int)entry.min);
                    prop.setMaxValue((int)entry.max);
                    min = String.valueOf((int)entry.min);
                    max = String.valueOf((int)entry.max);
                }
                else
                {
                    prop.setMinValue(entry.min);
                    prop.setMaxValue(entry.max);
                    min = String.valueOf(entry.min);
                    max = String.valueOf(entry.max);
                }
                if (desc != null)
                    prop.setComment(NEW_LINE.join(new String[] { desc, "Min: " + min, "Max: " + max }));
                else
                    prop.setComment(NEW_LINE.join(new String[] { "Min: " + min, "Max: " + max }));
                prop.setHasSlidingControl(hasSlidingControl);
            }
        }
    }

    private static abstract class SingleValueFieldWrapper extends FieldWrapper
    {
        private SingleValueFieldWrapper(String category, ConfigEntry entry)
        {
            super(category, entry);
        }

        @Override
        public String[] getKeys()
        {
            return asArray(this.category + "." + this.name);
        }

        @Override
        public boolean hasKey(String key)
        {
            return (this.category + "." + this.name).equals(key);
        }

        @Override
        public boolean handlesKey(String key)
        {
            return hasKey(key);
        }

        @Override
        public void setupConfiguration(Configuration cfg, String desc, String langKey, boolean reqMCRestart, boolean reqWorldRestart, boolean hasSlidingControl)
        {
            Property prop = cfg.getCategory(this.category).get(this.name); // Will be setup in general by ConfigManager

            prop.setComment(desc);
            prop.setLanguageKey(langKey);
            prop.setRequiresMcRestart(reqMCRestart);
            prop.setRequiresWorldRestart(reqWorldRestart);
            prop.setHasSlidingControl(hasSlidingControl);
        }

        @Override
        public String getCategory()
        {
            return this.category;
        }

    }

    private static <T> T[] asArray(T... in)
    {
        return in;
    }

    public static class BeanEntry<K, V> implements Entry<K, V>
    {
        private K key;
        private V value;

        public BeanEntry(K key, V value)
        {
            this.key = key;
            this.value = value;
        }

        @Override
        public K getKey()
        {
            return key;
        }

        @Override
        public V getValue()
        {
            return value;
        }

        @Override
        public V setValue(V value)
        {
            throw new UnsupportedOperationException("This is a static bean.");
        }

    }
}
