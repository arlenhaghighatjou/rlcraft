package org.orecruncher.dsurround.client.handlers.effects;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableList;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Map.Entry;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IResource;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.EntitySquid;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.lang3.StringUtils;
import org.orecruncher.dsurround.ModBase;
import org.orecruncher.dsurround.ModOptions;
import org.orecruncher.dsurround.capabilities.CapabilitySpeechData;
import org.orecruncher.dsurround.capabilities.speech.ISpeechData;
import org.orecruncher.dsurround.client.effects.EntityEffect;
import org.orecruncher.dsurround.client.effects.IEntityEffectFactory;
import org.orecruncher.dsurround.client.effects.IEntityEffectFactoryFilter;
import org.orecruncher.lib.Translations;
import org.orecruncher.lib.WeightTable;
import org.orecruncher.lib.WeightTable.IItem;
import org.orecruncher.lib.collections.ObjectArray;
import org.orecruncher.lib.compat.EntityUtil;
import org.orecruncher.lib.random.XorShiftRandom;

@SideOnly(Side.CLIENT)
public class EntityChatEffect extends EntityEffect {
   private static final String SPLASH_TOKEN = "$MINECRAFT$";
   private static final ResourceLocation SPLASH_TEXT = new ResourceLocation("texts/splashes.txt");
   private static final Translations xlate = new Translations();
   private static final ObjectArray<String> minecraftSplashText = new ObjectArray();
   private static final Map<String, EntityChatEffect.EntityChatData> messages = new Object2ObjectOpenHashMap();
   protected static final Random random;
   protected final EntityChatEffect.EntityChatData data;
   protected long nextChat;
   public static final IEntityEffectFactoryFilter DEFAULT_FILTER;

   protected static void setTimers(@Nonnull Class<? extends Entity> entity, int base, int random) {
      setTimers(EntityUtil.getClassName(entity), base, random);
   }

   static void setTimers(@Nonnull String entity, int base, int random) {
      EntityChatEffect.EntityChatData data = (EntityChatEffect.EntityChatData)messages.get(entity);
      if (data != null) {
         data.baseInterval = base;
         data.baseRandom = random;
      }

   }

   protected static boolean hasMessages(@Nonnull Entity entity) {
      return !(entity instanceof EntityPlayer) && messages.get(EntityUtil.getClassName(entity.getClass())) != null;
   }

   private String getSpeechFormatted(@Nonnull String message) {
      String xlated = xlate.loadString(message);
      if (minecraftSplashText.size() > 0 && "$MINECRAFT$".equals(xlated)) {
         xlated = (String)minecraftSplashText.get(random.nextInt(minecraftSplashText.size()));
      }

      return xlated;
   }

   public EntityChatEffect(@Nonnull Entity entity) {
      this(entity, (String)null);
   }

   public EntityChatEffect(@Nonnull Entity entity, @Nullable String entityName) {
      String theName = StringUtils.isEmpty(entityName) ? EntityUtil.getClassName(entity.getClass()) : entityName;
      this.data = (EntityChatEffect.EntityChatData)messages.get(theName);
      this.nextChat = this.getWorldTicks(entity) + (long)this.getNextChatTime();
   }

   @Nonnull
   public String name() {
      return "Entity Chat";
   }

   protected int getBase() {
      return this.data.baseInterval;
   }

   protected int getRandom() {
      return this.data.baseRandom;
   }

   protected long getWorldTicks(Entity e) {
      return e.getEntityWorld().getTotalWorldTime();
   }

   protected String getChatMessage() {
      String txt = (String)this.data.table.next();
      return txt != null ? xlate.loadString(this.getSpeechFormatted(txt)) : "NO TEXT";
   }

   protected int getNextChatTime() {
      return this.getBase() + random.nextInt(this.getRandom());
   }

   public void update(@Nonnull Entity subject) {
      if (ModOptions.speechbubbles.enableEntityChat) {
         long ticks = this.getWorldTicks(subject);
         long delta = this.nextChat - ticks;
         if (delta <= 0L) {
            ISpeechData data = CapabilitySpeechData.getCapability(subject);
            if (data != null) {
               int expiry = (int)(ModOptions.speechbubbles.speechBubbleDuration * 20.0F);
               data.addMessage(this.getChatMessage(), expiry);
            }

            this.genNextChatTime();
         }

      }
   }

   public void genNextChatTime() {
      this.getState().subject().ifPresent((e) -> {
         this.nextChat = this.getWorldTicks(e) + (long)this.getNextChatTime();
      });
   }

   static {
      xlate.load("/assets/dsurround/dsurround/data/chat/", new String[0]);
      xlate.forAll(new EntityChatEffect.WeightTableBuilder());
      xlate.transform(new EntityChatEffect.Stripper());
      setTimers(EntitySquid.class, 600, 1200);

      try {
         IResource resource = Minecraft.getMinecraft().getResourceManager().getResource(SPLASH_TEXT);
         Throwable var1 = null;

         try {
            InputStreamReader input = new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8);
            BufferedReader bufferedreader = new BufferedReader(input);

            String s;
            while((s = bufferedreader.readLine()) != null) {
               s = s.trim();
               if (!s.isEmpty()) {
                  minecraftSplashText.add(s);
               }
            }
         } catch (Throwable var13) {
            var1 = var13;
            throw var13;
         } finally {
            if (resource != null) {
               if (var1 != null) {
                  try {
                     resource.close();
                  } catch (Throwable var12) {
                     var1.addSuppressed(var12);
                  }
               } else {
                  resource.close();
               }
            }

         }
      } catch (Throwable var15) {
         ;
      }

      random = XorShiftRandom.current();
      DEFAULT_FILTER = (e, eei) -> {
         return eei.effects.contains("chat") && !(e instanceof EntityVillager) && hasMessages(e);
      };
   }

   static class EntityChatData {
      public static final int DEFAULT_INTERVAL = 400;
      public static final int DEFAULT_RANDOM = 1200;
      public int baseInterval = 400;
      public int baseRandom = 1200;
      public final WeightTable<String> table = new WeightTable();

      EntityChatData() {
      }
   }

   public static class Factory implements IEntityEffectFactory {
      public Factory() {
      }

      @Nonnull
      public List<EntityEffect> create(@Nonnull Entity entity) {
         return ImmutableList.of(new EntityChatEffect(entity));
      }
   }

   private static class Stripper implements Function<Entry<String, String>, String> {
      private final Pattern WEIGHT_PATTERN;

      private Stripper() {
         this.WEIGHT_PATTERN = Pattern.compile("^([0-9]*),(.*)");
      }

      public String apply(@Nullable Entry<String, String> input) {
         if (input == null) {
            return "NOT SET";
         } else {
            Matcher matcher = this.WEIGHT_PATTERN.matcher((CharSequence)input.getValue());
            return matcher.matches() ? matcher.group(2) : (String)input.getValue();
         }
      }
   }

   private static class WeightTableBuilder implements Predicate<Entry<String, String>> {
      private final Pattern TYPE_PATTERN = Pattern.compile("chat\\.([a-zA-Z.]*)\\.[0-9]*$");
      private final Pattern WEIGHT_PATTERN = Pattern.compile("^([0-9]*),(.*)");

      public WeightTableBuilder() {
      }

      public boolean apply(@Nullable Entry<String, String> input) {
         if (input == null) {
            return true;
         } else {
            Matcher matcher1 = this.TYPE_PATTERN.matcher((CharSequence)input.getKey());
            if (matcher1.matches()) {
               String key = matcher1.group(1).toLowerCase();
               Matcher matcher2 = this.WEIGHT_PATTERN.matcher((CharSequence)input.getValue());
               if (matcher2.matches()) {
                  EntityChatEffect.EntityChatData data = (EntityChatEffect.EntityChatData)EntityChatEffect.messages.get(key);
                  if (data == null) {
                     EntityChatEffect.messages.put(key, data = new EntityChatEffect.EntityChatData());
                  }

                  final int weight = Integer.parseInt(matcher2.group(1));
                  final String item = matcher2.group(2);
                  data.table.add(new IItem<String>() {
                     public int getWeight() {
                        return weight;
                     }

                     public String getItem() {
                        return item;
                     }
                  });
               } else {
                  ModBase.log().warn("Invalid value in language file: %s", new Object[]{input.getValue()});
               }
            } else {
               ModBase.log().warn("Invalid key in language file: %s", new Object[]{input.getKey()});
            }

            return true;
         }
      }
   }
}
