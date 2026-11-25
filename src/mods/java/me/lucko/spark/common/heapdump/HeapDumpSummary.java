package me.lucko.spark.common.heapdump;

import java.lang.management.ManagementFactory;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import javax.management.JMX;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import me.lucko.spark.common.command.sender.CommandSender;
import me.lucko.spark.common.platform.PlatformInfo;
import me.lucko.spark.lib.asm.Type;
import me.lucko.spark.proto.SparkProtos;

public final class HeapDumpSummary {
   private static final String DIAGNOSTIC_BEAN = "com.sun.management:type=DiagnosticCommand";
   private static final Pattern OUTPUT_FORMAT = Pattern.compile("^\\s*(\\d+):\\s*(\\d+)\\s*(\\d+)\\s*([^\\s]+).*$");
   private final List<HeapDumpSummary.Entry> entries;

   private static String getRawHeapData() throws Exception {
      MBeanServer beanServer = ManagementFactory.getPlatformMBeanServer();
      ObjectName diagnosticBeanName = ObjectName.getInstance("com.sun.management:type=DiagnosticCommand");
      HeapDumpSummary.DiagnosticCommandMXBean proxy = (HeapDumpSummary.DiagnosticCommandMXBean)JMX.newMXBeanProxy(beanServer, diagnosticBeanName, HeapDumpSummary.DiagnosticCommandMXBean.class);
      return proxy.gcClassHistogram(new String[0]);
   }

   private static String typeToClassName(String type) {
      try {
         return Type.getType(type).getClassName();
      } catch (IllegalArgumentException var2) {
         return type;
      }
   }

   public static HeapDumpSummary createNew() {
      String rawOutput;
      try {
         rawOutput = getRawHeapData();
      } catch (Exception var2) {
         throw new RuntimeException("Unable to get heap dump", var2);
      }

      return new HeapDumpSummary((List)Arrays.stream(rawOutput.split("\n")).map((line) -> {
         Matcher matcher = OUTPUT_FORMAT.matcher(line);
         if (!matcher.matches()) {
            return null;
         } else {
            try {
               return new HeapDumpSummary.Entry(Integer.parseInt(matcher.group(1)), Integer.parseInt(matcher.group(2)), Long.parseLong(matcher.group(3)), typeToClassName(matcher.group(4)));
            } catch (Exception var3) {
               (new IllegalArgumentException("Exception parsing line: " + line, var3)).printStackTrace();
               return null;
            }
         }
      }).filter(Objects::nonNull).collect(Collectors.toList()));
   }

   private HeapDumpSummary(List<HeapDumpSummary.Entry> entries) {
      this.entries = entries;
   }

   public SparkProtos.HeapData toProto(PlatformInfo platformInfo, CommandSender creator) {
      SparkProtos.HeapData.Builder proto = SparkProtos.HeapData.newBuilder();
      proto.setMetadata((SparkProtos.HeapMetadata)SparkProtos.HeapMetadata.newBuilder().setPlatformMetadata(platformInfo.toData().toProto()).setCreator(creator.toData().toProto()).build());

      for(HeapDumpSummary.Entry entry : this.entries) {
         proto.addEntries(entry.toProto());
      }

      return (SparkProtos.HeapData)proto.build();
   }

   public interface DiagnosticCommandMXBean {
      String gcClassHistogram(String[] var1);
   }

   public static final class Entry {
      private final int order;
      private final int instances;
      private final long bytes;
      private final String type;

      Entry(int order, int instances, long bytes, String type) {
         this.order = order;
         this.instances = instances;
         this.bytes = bytes;
         this.type = type;
      }

      public int getOrder() {
         return this.order;
      }

      public int getInstances() {
         return this.instances;
      }

      public long getBytes() {
         return this.bytes;
      }

      public String getType() {
         return this.type;
      }

      public SparkProtos.HeapEntry toProto() {
         return (SparkProtos.HeapEntry)SparkProtos.HeapEntry.newBuilder().setOrder(this.order).setInstances(this.instances).setSize(this.bytes).setType(this.type).build();
      }
   }
}
