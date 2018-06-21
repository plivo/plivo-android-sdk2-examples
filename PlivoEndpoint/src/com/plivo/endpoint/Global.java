package com.plivo.endpoint;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.*;

public class Global{
	public static boolean DEBUG;

	//Util method to convert Map to String.
	public static String mapToString(Map<String, String> map)
	{
		StringBuilder stringBuilder = new StringBuilder();

		for (String key : map.keySet()) {
			if (stringBuilder.length() > 0) {
				stringBuilder.append(",");
			}
			String value = map.get(key);
			stringBuilder.append(key);
			stringBuilder.append(":");
			stringBuilder.append(value);
		}
		return stringBuilder.toString();
	}
}