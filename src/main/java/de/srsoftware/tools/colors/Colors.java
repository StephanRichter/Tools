package de.srsoftware.tools.colors;

import java.awt.Color;

public class Colors {
	public static Color component(Color bg) {
		// int min=Math.min(bg.getRed(), Math.min(bg.getGreen(), bg.getBlue()));
		// int max=Math.max(bg.getRed(), Math.max(bg.getGreen(), bg.getBlue()));
		// max+=min;
		int treshold = 200;
		int red = (bg.getRed() > treshold) ? 0 : 255;
		int green = (bg.getGreen() > treshold) ? 0 : 255;
		int blue = (bg.getBlue() > treshold) ? 0 : 255;
		return new Color(red, green, blue);
		// return new Color(max-bg.getRed(),max-bg.getGreen(),max-bg.getBlue());
	}
	
	private static int hexToInt(String hex) {
		byte[] bytes = hex.getBytes();
		int result = 0;
		for (byte b : bytes) {
			result *= 16;
			if (b>47 && b<58) {
				result += b-48;
			} else if (b>96 && b<103) {
				result += b-87;
			} else throw new IllegalArgumentException(hex+" is not a valid hex string!");
		}
		return result;
	}
	
	public static Color lookup(String name) {
		if (name.equals("clAqua")) return new Color(0, 255, 255);
		if (name.equals("clBlack")) return Color.BLACK;
		if (name.equals("clBlue")) return Color.BLUE;
		if (name.equals("clMaroon")) return new Color(128, 0, 0);
		if (name.equals("clGreen")) return Color.GREEN;
		if (name.equals("clOlive")) return new Color(128, 128, 128);
		if (name.equals("clNavy")) return new Color(0, 0, 128);
		if (name.equals("clPurple")) return new Color(128, 0, 128);
		if (name.equals("clTeal")) return new Color(128, 0, 128);
		if (name.equals("clGray")) return new Color(128, 128, 128);
		if (name.equals("clSilver")) return new Color(192, 192, 192);
		if (name.equals("clRed")) return Color.RED;
		if (name.equals("clLime")) return new Color(0, 255, 0);
		if (name.equals("clYellow")) return Color.YELLOW;
		if (name.equals("clFuchsia")) return new Color(255, 0, 255);
		if (name.equals("clWhite")) return Color.WHITE;
		return Color.BLACK;
	}
	
	public static Color parse(String hex) {
		if (hex.startsWith("#")) hex=hex.substring(1);
		hex = hex.toLowerCase();
		String r = hex.substring(0, 2);
		String g = hex.substring(2, 4);
		String b = hex.substring(4, 6);		
		return new Color(hexToInt(r)/255f, hexToInt(g)/255f, hexToInt(b)/255f);
	}

	public static String toString(Color c) {
		String dummy = Integer.toHexString(c.getBlue());
		if (dummy.length() < 2) dummy = "0" + dummy;
		String result = dummy;
		dummy = Integer.toHexString(c.getGreen());
		if (dummy.length() < 2) dummy = "0" + dummy;
		result = result + dummy;

		dummy = Integer.toHexString(c.getRed());
		if (dummy.length() < 2) dummy = "0" + dummy;
		result = result + dummy;

		return "$00" + result.toUpperCase();
	}

	public static String toXml(Color c) {
		String dummy = Integer.toHexString(c.getRed());
		if (dummy.length() < 2) dummy = "0" + dummy;
		String result = dummy;
		dummy = Integer.toHexString(c.getGreen());
		if (dummy.length() < 2) dummy = "0" + dummy;
		result = result + dummy;

		dummy = Integer.toHexString(c.getBlue());
		if (dummy.length() < 2) dummy = "0" + dummy;
		result = result + dummy;

		return "#" + result.toUpperCase();
	}
}
