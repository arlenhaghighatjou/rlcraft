package org.jwaresoftware.mcmods.lib;

public final class BurnTimes {
   public static final int NONE = -1;
   public static final int STANDARD_COOK_TIME = 200;
   public static final int SHORTENED_COOK_TIME = 100;
   public static final int FULL_STACK_BURN_TIME = 200 * SharedGlue.PINNED_MAX_STACK_SIZE(-1, 10.0F);
   public static final int STICK_BURN_TIME = 100;
   public static final int WOOL_BURN_TIME = 100;
   public static final int WOODEN_ITEM_BURN_TIME = 300;
   public static final int BOOK_BURN_TIME = 200;
   public static final int COAL_BURN_TIME = 1600;
   public static final int DENSE_COAL_BURN_TIME = 6400;
   public static final int COALBLOCK_BURN_TIME = 16000;
   public static final int BLAZEROD_BURN_TIME = 2400;
   public static final int MAGMA_BURN_TIME = 4800;
   public static final int LOG_BURN_TIME = 300;
   public static final int DENSE_LOG_BURN_TIME = 400;
   public static final int PLANKS_BURN_TIME = 300;
   public static final int LAVA_BUCKET_BURN_TIME = 20000;
   public static final int BIG_LAVA_BUCKET_BURN_TIME = 23040;
   public static final int MAX_BURN_TIME = 640000;

   public BurnTimes() {
   }
}
