package net.minecraftforge.fml.common.discovery;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nullable;

public class ASMDataTable
{
	private final Map<String, Set<ASMData>> annotations = new LinkedHashMap<>();

	public Set<ASMData> getAll(String annotation)
	{
		Set<ASMData> values = annotations.get(annotation);
		return values == null ? Collections.emptySet() : Collections.unmodifiableSet(values);
	}

	public void addASMData(String annotation, String className, @Nullable String objectName, @Nullable Map<String, Object> information)
	{
		annotations.computeIfAbsent(annotation, key -> new LinkedHashSet<>()).add(new ASMData(annotation, className, objectName, information));
	}

	@Nullable
	public static String getOwnerModID(Set<ASMData> mods, ASMData target)
	{
		for (ASMData data : mods)
		{
			if (target.getClassName().startsWith(data.getClassName())) return (String)data.getAnnotationInfo().get("modid");
		}
		return null;
	}

	public static final class ASMData
	{
		private final String annotationName;
		private final String className;
		private final String objectName;
		private final Map<String, Object> annotationInfo;

		public ASMData(String annotationName, String className, @Nullable String objectName, @Nullable Map<String, Object> information)
		{
			this.annotationName = annotationName;
			this.className = className;
			this.objectName = objectName;
			this.annotationInfo = information == null ? Collections.emptyMap() : Collections.unmodifiableMap(new LinkedHashMap<>(information));
		}

		public String getAnnotationName() { return annotationName; }
		public String getClassName() { return className; }
		public String getObjectName() { return objectName; }
		public Map<String, Object> getAnnotationInfo() { return annotationInfo; }
		public ASMData copy(Map<String, Object> information) { return new ASMData(annotationName, className, objectName, information); }
	}
}
