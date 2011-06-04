package com.sciabarra.fatwire;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

import COM.FutureTense.Interfaces.ICS;

public class AssemblerHelper {

	static ICS ics;
	static {
		try {
			ics = COM.FutureTense.CS.Factory.newCS();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	
	private static boolean hasQuotes(String value) {
		//System.out.println("isAlphanumeric" + value);
		if (value == null)
			return false;
		for (int i = 0; i < value.length(); i++)
			switch (value.charAt(i)) {
			case '\'':
			case '"':
			case '\\':
			case ';':
				return true;
			}
		return false;
	}

	private static boolean isNumeric(String value) {
		if (value == null)
			return false;
		for (int i = 0; i < value.length(); i++)
			if (!Character.isDigit(value.charAt(i)))
				return false;
		return true;
	}

	public static String query(String fieldSelect, String table,
			String fieldWhere, String value, boolean isString) {

		try {
			// prevent sql injection
			if (hasQuotes(value))
				return value;

			String sql = "SELECT " + fieldSelect + " FROM " + table + " WHERE "
					+ fieldWhere + "=";

			if (isString)
				sql += "'" + value + "'";
			else
				sql += value;

			//System.out.println("sql=" + sql);

			StringBuffer errstr = new StringBuffer();
			COM.FutureTense.Interfaces.IList list = ics.SQL(table, sql, null,
					1, true, errstr);
			if (list != null && list.hasData()) {
				if (list.numRows() > 1) {
					// I cannot resolve the id to a name since it is not unique
					//System.out.println("!!! not unique " + fieldSelect + " for " + sql);
					return value;
				}
				if (list.moveTo(1)) {
					String result = list.getValue(fieldSelect);
					//System.out.println("<<<" + result);
					return result;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return value;
	}

	/*
	 * Replace a cid with a name, url encoded if it is unique
	 */
	public static String cid2name(String c, String cid) {
		try {
			return URLEncoder.encode(query("name", c, "id", cid, false),
					"UTF-8");
		} catch (UnsupportedEncodingException e) {
			// something is going wrong, don't translate
			return cid;
		}
	}

	/**
	 * Replace a name with a cid, url decoded
	 * 
	 * @param c
	 * @param name
	 * @return
	 */
	public static String name2cid(String c, String name) {
		//System.out.println("name2cid:" + c + " " + name);
		if (isNumeric(name))
			return name;
		try {
			return query("id", c, "name", URLDecoder.decode(name, "UTF-8"),
					true);
		} catch (UnsupportedEncodingException e) {
			// trying again...
			return name;
		}
	}

}
