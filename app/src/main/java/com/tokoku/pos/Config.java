package com.tokoku.pos;

public class Config {
	
	public static boolean isByPassLogin = false;
	
	//public static String SERVER_URL = "http://192.168.1.9:8888";
	//public static String SERVER_URL = "http://192.168.88.45:8888";
	//public static String SERVER_URL = "http://192.168.0.102:8888";
	//public static String SERVER_URL = "http://20.194.21.242:8888";
	//public static String SERVER_URL = "http://20.194.35.112:8888";
	//public static String SERVER_URL = "http://192.168.169.2:8888";
	//public static String SERVER_URL = "http://192.168.1.161:8888";
	//public static String SERVER_URL = "http://10.188.16.112:8888";
	
	public static String SERVER_URL = "http://pos-tokoku.appspot.com";
	
	public static boolean isDebug() {
		
		return isDevelopment() && isByPassLogin;
	}
		
	public static boolean isDevelopment() {
		
		
		return !SERVER_URL.equals("http://pos-tokoku.appspot.com");
	}
	
	private static boolean isMenuReportExpanded = false;
	private static boolean isMenuFavoriteExpanded = false;
	private static boolean isMenuDataReferenceExpanded = false;

	public static void resetMenus() {
		
		isMenuReportExpanded = false;
		isMenuFavoriteExpanded = false;
		isMenuDataReferenceExpanded = false;
	}
	
	public static void setMenusExpanded(boolean isExpanded) {
		
		setMenuDataReferenceExpanded(isExpanded);
		setMenuFavoriteExpanded(isExpanded);
		setMenuReportExpanded(isExpanded);
	}
	
	public static boolean isMenuReportExpanded() {
		return isMenuReportExpanded;
	}

	public static void setMenuReportExpanded(boolean isMenuReportExpanded) {
		Config.isMenuReportExpanded = isMenuReportExpanded;
	}

	public static boolean isMenuFavoriteExpanded() {
		return isMenuFavoriteExpanded;
	}

	public static void setMenuFavoriteExpanded(boolean isMenuFavoriteExpanded) {
		Config.isMenuFavoriteExpanded = isMenuFavoriteExpanded;
	}

	public static boolean isMenuDataReferenceExpanded() {
		return isMenuDataReferenceExpanded;
	}

	public static void setMenuDataReferenceExpanded(boolean isMenuDataReferenceExpanded) {
		Config.isMenuDataReferenceExpanded = isMenuDataReferenceExpanded;
	}
}