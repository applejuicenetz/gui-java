package de.applejuicenet.client.fassade.controller.json;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

public class DateDeserializer implements JsonDeserializer<Date>
{
	public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException
	{
		String dateAsString = json.getAsJsonPrimitive().getAsString();
		dateAsString = dateAsString.substring(0, 19);
		SimpleDateFormat formater = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		try
		{
			return formater.parse(dateAsString);
		}
		catch (ParseException e)
		{
			throw new RuntimeException(e);
		}
	}
}