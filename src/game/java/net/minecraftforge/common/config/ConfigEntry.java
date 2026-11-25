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

import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

public final class ConfigEntry
{
    final String name;
    final Class<?> type;
    final Supplier<Object> getter;
    final Consumer<Object> setter;
    final Enum<?>[] enumValues;
    final Map<String, Object> map;
    final ConfigEntry[] children;
    String comment;
    String langKey;
    boolean requiresMcRestart;
    boolean requiresWorldRestart;
    boolean slidingOption;
    boolean hasRange;
    boolean intRange;
    double min;
    double max;

    private ConfigEntry(String name, Class<?> type, Supplier<Object> getter, Consumer<Object> setter, Enum<?>[] enumValues, Map<String, Object> map, ConfigEntry[] children)
    {
        this.name = name;
        this.type = type;
        this.getter = getter;
        this.setter = setter;
        this.enumValues = enumValues;
        this.map = map;
        this.children = children;
    }

    public static ConfigEntry value(String name, Class<?> type, Supplier<Object> getter, Consumer<Object> setter)
    {
        return new ConfigEntry(name, type, getter, setter, null, null, null);
    }

    @SuppressWarnings("unchecked")
    public static <E extends Enum<E>> ConfigEntry enumValue(String name, E[] values, Supplier<E> getter, Consumer<E> setter)
    {
        return new ConfigEntry(name, Enum.class, () -> getter.get(), v -> setter.accept((E)v), values, null, null);
    }

    @SuppressWarnings("unchecked")
    public static ConfigEntry map(String name, Map<String, ?> map, Class<?> valueType)
    {
        return new ConfigEntry(name, valueType, null, null, null, (Map<String, Object>)map, null);
    }

    public static ConfigEntry category(String name, ConfigEntry... children)
    {
        return new ConfigEntry(name, null, null, null, null, null, children);
    }

    public ConfigEntry comment(String... lines)
    {
        this.comment = ConfigManager.NEW_LINE.join(lines);
        return this;
    }

    public ConfigEntry langKey(String langKey)
    {
        this.langKey = langKey;
        return this;
    }

    public ConfigEntry requiresMcRestart()
    {
        this.requiresMcRestart = true;
        return this;
    }

    public ConfigEntry requiresWorldRestart()
    {
        this.requiresWorldRestart = true;
        return this;
    }

    public ConfigEntry slidingOption()
    {
        this.slidingOption = true;
        return this;
    }

    public ConfigEntry range(int min, int max)
    {
        this.hasRange = true;
        this.intRange = true;
        this.min = min;
        this.max = max;
        return this;
    }

    public ConfigEntry range(double min, double max)
    {
        this.hasRange = true;
        this.intRange = false;
        this.min = min;
        this.max = max;
        return this;
    }
}
