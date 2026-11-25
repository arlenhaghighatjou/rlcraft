package vazkii.quark.experimental.features;

import java.io.PrintStream;
import java.util.function.Consumer;
import org.apache.commons.io.output.NullOutputStream;
import vazkii.quark.base.Quark;
import vazkii.quark.base.module.Feature;

public class PrintSuppressor extends Feature {
   public static boolean suppressOut;
   public static boolean suppressErr;

   public PrintSuppressor() {
   }

   public void setupConfig() {
      suppressOut = this.loadPropBool("Suppress STDOUT", "", true);
      suppressErr = this.loadPropBool("Suppress STDERR", "", false);
   }

   public void postInit() {
      Quark.LOG.warn("Quark print suppression is enabled. Important info might be missing. Suppressing STDOUT=%b, STDERR=%b", Boolean.valueOf(suppressOut), Boolean.valueOf(suppressErr));
      if (suppressOut) {
         this.oppressFreedomOfSpeech(System::setOut);
      }

      if (suppressErr) {
         this.oppressFreedomOfSpeech(System::setErr);
      }

   }

   private void oppressFreedomOfSpeech(Consumer<PrintStream> consumer) {
      PrintStream oppressedStream = new PrintStream(new NullOutputStream());
      consumer.accept(oppressedStream);
   }

   public String getFeatureDescription() {
      return "Suppresses all STDOUT (and STDERR, if enabled) messages so they don't show up in the console.\nUse this if some mod left behind debug messages and you don't want to see them.\nIf important messages end up being disabled by this, tell the modders in case to switch to a proper logger.";
   }

   public boolean requiresMinecraftRestartToEnable() {
      return true;
   }
}
