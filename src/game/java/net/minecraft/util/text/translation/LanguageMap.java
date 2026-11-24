package net.minecraft.util.text.translation;

import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import java.io.InputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.IllegalFormatException;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.lax1dude.eaglercraft.v1_8.IOUtils;
import net.lax1dude.eaglercraft.v1_8.EagRuntime;
import net.lax1dude.eaglercraft.v1_8.HString;
import net.lax1dude.eaglercraft.v1_8.sp.SingleplayerServerController;

public class LanguageMap
{
    private static final Pattern NUMERIC_VARIABLE_PATTERN = Pattern.compile("%(\\d+\\$)?[\\d\\.]*[df]");
    private static final Splitter EQUAL_SIGN_SPLITTER = Splitter.on('=').limit(2);
    private static final LanguageMap instance = new LanguageMap();
    private final Map<String, String> languageList = Maps.<String, String>newHashMap();
    private long lastUpdateTimeInMilliseconds;

    public LanguageMap()
    {
    }

    public static void initClient()
    {
        try (InputStream inputstream = EagRuntime.getRequiredResourceStream("/assets/minecraft/lang/en_us.lang"))
        {
            inject(instance, inputstream);
        }
        catch (IOException e)
        {
            EagRuntime.debugPrintStackTrace(e);
        }
    }

    public static void initServer(List<String> strs)
    {
        instance.languageList.clear();
        parseLines(strs, instance.languageList);
        instance.lastUpdateTimeInMilliseconds = EagRuntime.steadyTimeMillis();
    }

    public static List<String> dump()
    {
        List<String> ret = new ArrayList<String>(instance.languageList.size());

        for (Entry<String, String> etr : instance.languageList.entrySet())
        {
            ret.add(etr.getKey() + "=" + etr.getValue());
        }

        return ret;
    }

    public static void inject(InputStream inputstream)
    {
        inject(instance, inputstream);
    }

    private static void inject(LanguageMap inst, InputStream inputstream)
    {
        Map<String, String> map = parseLangFile(inputstream);
        inst.languageList.putAll(map);
        inst.lastUpdateTimeInMilliseconds = EagRuntime.steadyTimeMillis();
    }

    public static Map<String, String> parseLangFile(InputStream inputstream)
    {
        Map<String, String> table = Maps.newHashMap();
        try
        {
            inputstream = net.minecraftforge.fml.common.FMLCommonHandler.instance().loadLanguage(table, inputstream);
            if (inputstream == null) return table;

            parseLines(IOUtils.readLines(inputstream, StandardCharsets.UTF_8), table);
        }
        catch (Exception ex) {}
        return table;
    }

    private static void parseLines(List<String> lines, Map<String, String> table)
    {
        for (int i = 0, l = lines.size(); i < l; ++i)
        {
            String s = lines.get(i);

            if (!s.isEmpty() && s.charAt(0) != '#')
            {
                String[] astring = (String[])Iterables.toArray(EQUAL_SIGN_SPLITTER.split(s), String.class);

                if (astring != null && astring.length == 2)
                {
                    table.put(astring[0], fixNumericVariables(astring[1]));
                }
            }
        }
    }

    private static String fixNumericVariables(String str)
    {
        Matcher matcher = NUMERIC_VARIABLE_PATTERN.matcher(str);

        if (!matcher.find())
        {
            return str;
        }

        StringBuilder sb = new StringBuilder(str.length());
        int last = 0;

        do
        {
            sb.append(str, last, matcher.start());
            String position = matcher.group(1);
            sb.append('%');

            if (position != null)
            {
                sb.append(position);
            }

            sb.append('s');
            last = matcher.end();
        }
        while (matcher.find());

        sb.append(str, last, str.length());
        return sb.toString();
    }

    static LanguageMap getInstance()
    {
        return instance;
    }

    @SideOnly(Side.CLIENT)

    public static synchronized void replaceWith(Map<String, String> p_135063_0_)
    {
        instance.languageList.clear();
        instance.languageList.putAll(p_135063_0_);
        instance.lastUpdateTimeInMilliseconds = EagRuntime.steadyTimeMillis();
        SingleplayerServerController.updateLocale(dump());
    }

    public synchronized String translateKey(String key)
    {
        return this.tryTranslateKey(key);
    }

    public synchronized String translateKeyFormat(String key, Object... format)
    {
        String s = this.tryTranslateKey(key);

        try
        {
            return HString.format(s, format);
        }
        catch (IllegalFormatException var5)
        {
            return "Format error: " + s;
        }
    }

    private String tryTranslateKey(String key)
    {
        String s = this.languageList.get(key);
        return s == null ? key : s;
    }

    public synchronized boolean isKeyTranslated(String key)
    {
        return this.languageList.containsKey(key);
    }

    public long getLastUpdateTimeInMilliseconds()
    {
        return this.lastUpdateTimeInMilliseconds;
    }
}
