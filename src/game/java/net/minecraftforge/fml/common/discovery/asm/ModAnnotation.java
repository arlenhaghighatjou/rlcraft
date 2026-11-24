package net.minecraftforge.fml.common.discovery.asm;

public final class ModAnnotation
{
	private ModAnnotation()
	{
	}

	public static final class EnumHolder
	{
		private final String descriptor;
		private final String value;

		public EnumHolder(String descriptor, String value)
		{
			this.descriptor = descriptor;
			this.value = value;
		}

		public String getDescriptor() { return descriptor; }
		public String getValue() { return value; }
	}
}
