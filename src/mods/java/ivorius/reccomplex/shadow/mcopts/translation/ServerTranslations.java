package ivorius.reccomplex..shadow.mcopts.translation;

import java.util.List;
import java.util.function.BiFunction;
import javax.annotation.Nonnull;
import net.minecraft.command.CommandException;
import net.minecraft.command.WrongUsageException;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import org.apache.commons.lang3.StringUtils;

public abstract class ServerTranslations {
   public ServerTranslations() {
   }

   @Nonnull
   public static TextComponentTranslation join(Object... components) {
      return join(", ", components);
   }

   @Nonnull
   public static TextComponentTranslation join(String join, Object... components) {
      return new TextComponentTranslation(StringUtils.repeat("%s", join, components.length), components);
   }

   @Nonnull
   public static TextComponentTranslation join(List<?> components) {
      return join(components.toArray());
   }

   public <T> T object(BiFunction<String, Object[], T> fun, String key, Object... params) {
      return (T)(this.translateServerSide() ? fun.apply(Translations.format(key, this.convertParams(params)), new Object[0]) : fun.apply(key, params));
   }

   public CommandException commandException(String key, Object... params) {
      return (CommandException)this.object(CommandException::new, key, params);
   }

   public CommandException wrongUsageException(String key, Object... params) {
      return (CommandException)this.object(WrongUsageException::new, key, params);
   }

   public ITextComponent format(String key, Object... params) {
      return (ITextComponent)(this.translateServerSide() ? new TextComponentString(Translations.format(key, this.convertParams(params))) : new TextComponentTranslation(key, params));
   }

   public ITextComponent get(String key) {
      return (ITextComponent)(this.translateServerSide() ? new TextComponentString(Translations.get(key)) : new TextComponentTranslation(key, new Object[0]));
   }

   public String usage(String key) {
      return this.translateServerSide() ? Translations.get(key) : key;
   }

   public Object[] convertParams(Object... params) {
      if (this.translateServerSide()) {
         Object[] array = new Object[params.length];

         for(int i = 0; i < array.length; ++i) {
            if (params[i] instanceof TextComponentTranslation) {
               array[i] = Translations.format(((TextComponentTranslation)params[i]).getKey(), this.convertParams(((TextComponentTranslation)params[i]).getFormatArgs()));
            } else if (params[i] instanceof ITextComponent) {
               array[i] = ((ITextComponent)params[i]).getUnformattedText();
            } else {
               array[i] = params[i];
            }
         }

         return array;
      } else {
         return params;
      }
   }

   public abstract boolean translateServerSide();
}
