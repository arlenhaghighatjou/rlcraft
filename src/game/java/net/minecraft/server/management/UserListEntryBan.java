package net.minecraft.server.management;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.google.gson.JsonObject;

public abstract class UserListEntryBan<T> extends UserListEntry<T>
{
	public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z");
	protected final Date banStartDate;
	protected final String bannedBy;
	protected final Date banEndDate;
	protected final String reason;

	public UserListEntryBan(T value, Date start, String banner, Date end, String reason)
	{
		super(value);
		this.banStartDate = start == null ? new Date() : start;
		this.bannedBy = banner == null ? "(Unknown)" : banner;
		this.banEndDate = end;
		this.reason = reason == null ? "Banned by an operator." : reason;
	}

	protected UserListEntryBan(T value, JsonObject json)
	{
		super(value, json);
		this.banStartDate = parseDate(json, "created", new Date());
		this.bannedBy = json.has("source") ? json.get("source").getAsString() : "(Unknown)";
		this.banEndDate = parseDate(json, "expires", null);
		this.reason = json.has("reason") ? json.get("reason").getAsString() : "Banned by an operator.";
	}

	private static Date parseDate(JsonObject json, String key, Date fallback)
	{
		if (!json.has(key) || "forever".equals(json.get(key).getAsString())) return fallback;
		try
		{
			return DATE_FORMAT.parse(json.get(key).getAsString());
		}
		catch (ParseException ex)
		{
			return fallback;
		}
	}

	public Date getBanEndDate()
	{
		return banEndDate;
	}

	public String getBanReason()
	{
		return reason;
	}

	boolean hasBanExpired()
	{
		return banEndDate != null && banEndDate.before(new Date());
	}

	protected void onSerialization(JsonObject data)
	{
		data.addProperty("created", DATE_FORMAT.format(banStartDate));
		data.addProperty("source", bannedBy);
		data.addProperty("expires", banEndDate == null ? "forever" : DATE_FORMAT.format(banEndDate));
		data.addProperty("reason", reason);
	}
}
