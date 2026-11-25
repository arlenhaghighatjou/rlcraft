package com.fantasticsource.mctools.gui.element.text.filter;

public abstract class TextFilter<T> {
   public TextFilter() {
   }

   public abstract String transformInput(String var1);

   public abstract boolean acceptable(String var1);

   public abstract T parse(String var1);
}
