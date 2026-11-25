package ivorius.reccomplex.world.gen.feature;

import java.util.function.BiConsumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.util.math.ChunkPos;
import net.minecraftforge.fml.common.FMLLog;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.Logger;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.layout.SyslogLayout;

public class WorldgenMonitor extends AbstractAppender {
   private String modName;
   private BiConsumer<ChunkPos, Integer> consumer;
   private Pattern regex;

   public WorldgenMonitor(String modName, BiConsumer<ChunkPos, Integer> consumer) {
      super(modName + "_worldgen_monitor", (Filter)null, SyslogLayout.newBuilder().build());
      this.modName = modName;
      this.consumer = consumer;
      this.regex = Pattern.compile(modName + " loaded a new chunk \\(([^,]*), ([^,]*)  Dimension: ([^)]*)\\)");
   }

   public static void create(String modName, BiConsumer<ChunkPos, Integer> consumer) {
      AbstractAppender appender = new WorldgenMonitor(modName, consumer);
      appender.start();
      ((Logger)FMLLog.log).addAppender(appender);
   }

   public void append(LogEvent event) {
      String message = event.getMessage().getFormattedMessage();
      Matcher matcher = this.regex.matcher(message);
      if (matcher.find()) {
         int l = Integer.valueOf(matcher.group(1)).intValue();
         int r = Integer.valueOf(matcher.group(2)).intValue();
         int d = Integer.valueOf(matcher.group(3)).intValue();
         this.consumer.accept(new ChunkPos(l, r), Integer.valueOf(d));
      }

   }
}
