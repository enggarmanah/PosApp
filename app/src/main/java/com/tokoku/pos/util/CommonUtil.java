package com.tokoku.pos.util;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicLong;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.TypedValue;

import com.android.pos.dao.Product;
import com.android.pos.dao.ProductGroup;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.tokoku.pos.Config;
import com.tokoku.pos.Constant;
import com.tokoku.pos.R;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;

import java.io.ByteArrayInputStream;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

@SuppressLint("SimpleDateFormat")
public class CommonUtil {
	
	public static Boolean LOCK = true;
	
	private static final AtomicLong LAST_TIME_MS = new AtomicLong();
	
	public static GoogleAnalytics analytics;
	public static Tracker tracker;
	
	private static Context mContext;
	private static String mCertDN;
	
	private static boolean isDemo = false;

    private static HashMap<String,String> nonStdLangIso3Iso2Map;
    private static HashMap<String,String> nonStdCountryIso3Iso2Map;

    static {

        nonStdLangIso3Iso2Map = new HashMap<String,String>();
        nonStdCountryIso3Iso2Map = new HashMap<String,String>();

        nonStdLangIso3Iso2Map.put("alb","sq");
        nonStdLangIso3Iso2Map.put("arg","an");
        nonStdLangIso3Iso2Map.put("arm","hy");
        nonStdLangIso3Iso2Map.put("ave","ae");
        nonStdLangIso3Iso2Map.put("baq","eu");
        nonStdLangIso3Iso2Map.put("ben","bn");
        nonStdLangIso3Iso2Map.put("bih","bh");
        nonStdLangIso3Iso2Map.put("bos","bs");
        nonStdLangIso3Iso2Map.put("bul","bg");
        nonStdLangIso3Iso2Map.put("bur","my");
        nonStdLangIso3Iso2Map.put("che","ce");
        nonStdLangIso3Iso2Map.put("chi","zh");
        nonStdLangIso3Iso2Map.put("chu","cu");
        nonStdLangIso3Iso2Map.put("chv","cv");
        nonStdLangIso3Iso2Map.put("cor","kw");
        nonStdLangIso3Iso2Map.put("scr","hr");
        nonStdLangIso3Iso2Map.put("cze","cs");
        nonStdLangIso3Iso2Map.put("div","dv");
        nonStdLangIso3Iso2Map.put("dut","nl");
        nonStdLangIso3Iso2Map.put("epo","eo");
        nonStdLangIso3Iso2Map.put("est","et");
        nonStdLangIso3Iso2Map.put("fao","fo");
        nonStdLangIso3Iso2Map.put("fij","fj");
        nonStdLangIso3Iso2Map.put("gla","gd");
        nonStdLangIso3Iso2Map.put("geo","ka");
        nonStdLangIso3Iso2Map.put("ger","de");
        nonStdLangIso3Iso2Map.put("gre","el");
        nonStdLangIso3Iso2Map.put("grn","gn");
        nonStdLangIso3Iso2Map.put("hat","ht");
        nonStdLangIso3Iso2Map.put("her","hz");
        nonStdLangIso3Iso2Map.put("hmo","ho");
        nonStdLangIso3Iso2Map.put("ice","is");
        nonStdLangIso3Iso2Map.put("ido","io");
        nonStdLangIso3Iso2Map.put("ind","id");
        nonStdLangIso3Iso2Map.put("ina","ia");
        nonStdLangIso3Iso2Map.put("ile","ie");
        nonStdLangIso3Iso2Map.put("iku","iu");
        nonStdLangIso3Iso2Map.put("ipk","ik");
        nonStdLangIso3Iso2Map.put("gle","ga");
        nonStdLangIso3Iso2Map.put("jpn","ja");
        nonStdLangIso3Iso2Map.put("jav","jv");
        nonStdLangIso3Iso2Map.put("kal","kl");
        nonStdLangIso3Iso2Map.put("kan","kn");
        nonStdLangIso3Iso2Map.put("kas","ks");
        nonStdLangIso3Iso2Map.put("kaz","kk");
        nonStdLangIso3Iso2Map.put("khm","km");
        nonStdLangIso3Iso2Map.put("kin","rw");
        nonStdLangIso3Iso2Map.put("kir","ky");
        nonStdLangIso3Iso2Map.put("kom","kv");
        nonStdLangIso3Iso2Map.put("kua","kj");
        nonStdLangIso3Iso2Map.put("lao","lo");
        nonStdLangIso3Iso2Map.put("lav","lv");
        nonStdLangIso3Iso2Map.put("lin","ln");
        nonStdLangIso3Iso2Map.put("lit","lt");
        nonStdLangIso3Iso2Map.put("ltz","lb");
        nonStdLangIso3Iso2Map.put("mac","mk");
        nonStdLangIso3Iso2Map.put("mlg","mg");
        nonStdLangIso3Iso2Map.put("may","ms");
        nonStdLangIso3Iso2Map.put("mal","ml");
        nonStdLangIso3Iso2Map.put("mlt","mt");
        nonStdLangIso3Iso2Map.put("glv","gv");
        nonStdLangIso3Iso2Map.put("mao","mi");
        nonStdLangIso3Iso2Map.put("mar","mr");
        nonStdLangIso3Iso2Map.put("mah","mh");
        nonStdLangIso3Iso2Map.put("mon","mn");
        nonStdLangIso3Iso2Map.put("nav","nv");
        nonStdLangIso3Iso2Map.put("nbl","nr");
        nonStdLangIso3Iso2Map.put("ndo","ng");
        nonStdLangIso3Iso2Map.put("sme","se");
        nonStdLangIso3Iso2Map.put("nob","nb");
        nonStdLangIso3Iso2Map.put("orm","om");
        nonStdLangIso3Iso2Map.put("pli","pi");
        nonStdLangIso3Iso2Map.put("per","fa");
        nonStdLangIso3Iso2Map.put("pol","pl");
        nonStdLangIso3Iso2Map.put("por","pt");
        nonStdLangIso3Iso2Map.put("pus","ps");
        nonStdLangIso3Iso2Map.put("roh","rm");
        nonStdLangIso3Iso2Map.put("rum","ro");
        nonStdLangIso3Iso2Map.put("run","rn");
        nonStdLangIso3Iso2Map.put("sag","sg");
        nonStdLangIso3Iso2Map.put("srd","sc");
        nonStdLangIso3Iso2Map.put("scc","sr");
        nonStdLangIso3Iso2Map.put("snd","sd");
        nonStdLangIso3Iso2Map.put("slo","sk");
        nonStdLangIso3Iso2Map.put("sot","st");
        nonStdLangIso3Iso2Map.put("spa","es");
        nonStdLangIso3Iso2Map.put("swe","sv");
        nonStdLangIso3Iso2Map.put("tgl","tl");
        nonStdLangIso3Iso2Map.put("tah","ty");
        nonStdLangIso3Iso2Map.put("tat","tt");
        nonStdLangIso3Iso2Map.put("tib","bo");
        nonStdLangIso3Iso2Map.put("tsn","tn");
        nonStdLangIso3Iso2Map.put("tur","tr");
        nonStdLangIso3Iso2Map.put("tuk","tk");
        nonStdLangIso3Iso2Map.put("uig","ug");
        nonStdLangIso3Iso2Map.put("wln","wa");
        nonStdLangIso3Iso2Map.put("wel","cy");
        nonStdLangIso3Iso2Map.put("fry","fy");
        nonStdLangIso3Iso2Map.put("zha","za");

        nonStdCountryIso3Iso2Map.put("ALA","AX");
        nonStdCountryIso3Iso2Map.put("AND","AD");
        nonStdCountryIso3Iso2Map.put("AGO","AO");
        nonStdCountryIso3Iso2Map.put("ATA","AQ");
        nonStdCountryIso3Iso2Map.put("ATG","AG");
        nonStdCountryIso3Iso2Map.put("ARM","AM");
        nonStdCountryIso3Iso2Map.put("ABW","AW");
        nonStdCountryIso3Iso2Map.put("AUT","AT");
        nonStdCountryIso3Iso2Map.put("BHS","BS");
        nonStdCountryIso3Iso2Map.put("BGD","BD");
        nonStdCountryIso3Iso2Map.put("BRB","BB");
        nonStdCountryIso3Iso2Map.put("BLR","BY");
        nonStdCountryIso3Iso2Map.put("BLZ","BZ");
        nonStdCountryIso3Iso2Map.put("BEN","BJ");
        nonStdCountryIso3Iso2Map.put("BIH","BA");
        nonStdCountryIso3Iso2Map.put("BRN","BN");
        nonStdCountryIso3Iso2Map.put("BDI","BI");
        nonStdCountryIso3Iso2Map.put("CPV","CV");
        nonStdCountryIso3Iso2Map.put("CYM","KY");
        nonStdCountryIso3Iso2Map.put("CAF","CF");
        nonStdCountryIso3Iso2Map.put("TCD","TD");
        nonStdCountryIso3Iso2Map.put("CHL","CL");
        nonStdCountryIso3Iso2Map.put("CHN","CN");
        nonStdCountryIso3Iso2Map.put("MAC","MO");
        nonStdCountryIso3Iso2Map.put("COM","KM");
        nonStdCountryIso3Iso2Map.put("COG","CG");
        nonStdCountryIso3Iso2Map.put("COD","CD");
        nonStdCountryIso3Iso2Map.put("COK","CK");
        nonStdCountryIso3Iso2Map.put("DNK","DK");
        nonStdCountryIso3Iso2Map.put("SLV","SV");
        nonStdCountryIso3Iso2Map.put("GNQ","GQ");
        nonStdCountryIso3Iso2Map.put("EST","EE");
        nonStdCountryIso3Iso2Map.put("FLK","FK");
        nonStdCountryIso3Iso2Map.put("FRO","FO");
        nonStdCountryIso3Iso2Map.put("GUF","GF");
        nonStdCountryIso3Iso2Map.put("PYF","PF");
        nonStdCountryIso3Iso2Map.put("ATF","TF");
        nonStdCountryIso3Iso2Map.put("GRL","GL");
        nonStdCountryIso3Iso2Map.put("GRD","GD");
        nonStdCountryIso3Iso2Map.put("GLP","GP");
        nonStdCountryIso3Iso2Map.put("GIN","GN");
        nonStdCountryIso3Iso2Map.put("GNB","GW");
        nonStdCountryIso3Iso2Map.put("GUY","GY");
        nonStdCountryIso3Iso2Map.put("IRQ","IQ");
        nonStdCountryIso3Iso2Map.put("IRL","IE");
        nonStdCountryIso3Iso2Map.put("ISR","IL");
        nonStdCountryIso3Iso2Map.put("JAM","JM");
        nonStdCountryIso3Iso2Map.put("KAZ","KZ");
        nonStdCountryIso3Iso2Map.put("PRK","KP");
        nonStdCountryIso3Iso2Map.put("KOR","KR");
        nonStdCountryIso3Iso2Map.put("LBR","LR");
        nonStdCountryIso3Iso2Map.put("LBY","LY");
        nonStdCountryIso3Iso2Map.put("MDG","MG");
        nonStdCountryIso3Iso2Map.put("MDV","MV");
        nonStdCountryIso3Iso2Map.put("MLT","MT");
        nonStdCountryIso3Iso2Map.put("MTQ","MQ");
        nonStdCountryIso3Iso2Map.put("MYT","YT");
        nonStdCountryIso3Iso2Map.put("MEX","MX");
        nonStdCountryIso3Iso2Map.put("FSM","FM");
        nonStdCountryIso3Iso2Map.put("MNE","ME");
        nonStdCountryIso3Iso2Map.put("MOZ","MZ");
        nonStdCountryIso3Iso2Map.put("NIU","NU");
        nonStdCountryIso3Iso2Map.put("MNP","MP");
        nonStdCountryIso3Iso2Map.put("PAK","PK");
        nonStdCountryIso3Iso2Map.put("PLW","PW");
        nonStdCountryIso3Iso2Map.put("PNG","PG");
        nonStdCountryIso3Iso2Map.put("PRY","PY");
        nonStdCountryIso3Iso2Map.put("PCN","PN");
        nonStdCountryIso3Iso2Map.put("POL","PL");
        nonStdCountryIso3Iso2Map.put("PRT","PT");
        nonStdCountryIso3Iso2Map.put("MAF","MF");
        nonStdCountryIso3Iso2Map.put("SPM","PM");
        nonStdCountryIso3Iso2Map.put("SEN","SN");
        nonStdCountryIso3Iso2Map.put("SRB","RS");
        nonStdCountryIso3Iso2Map.put("SYC","SC");
        nonStdCountryIso3Iso2Map.put("SVK","SK");
        nonStdCountryIso3Iso2Map.put("SVN","SI");
        nonStdCountryIso3Iso2Map.put("SLB","SB");
        nonStdCountryIso3Iso2Map.put("SGS","GS");
        nonStdCountryIso3Iso2Map.put("SUR","SR");
        nonStdCountryIso3Iso2Map.put("SWZ","SZ");
        nonStdCountryIso3Iso2Map.put("SWE","SE");
        nonStdCountryIso3Iso2Map.put("TUN","TN");
        nonStdCountryIso3Iso2Map.put("TUR","TR");
        nonStdCountryIso3Iso2Map.put("TKM","TM");
        nonStdCountryIso3Iso2Map.put("TUV","TV");
        nonStdCountryIso3Iso2Map.put("UKR","UA");
        nonStdCountryIso3Iso2Map.put("ARE","AE");
        nonStdCountryIso3Iso2Map.put("URY","UY");
        nonStdCountryIso3Iso2Map.put("WLF","WF");
        nonStdCountryIso3Iso2Map.put("ESH","EH");
    }

	public static void setDemo(boolean status) {
		
		isDemo = status;
	}
	
	public static boolean isDemo() {
		
		return isDemo;
	}
	
	public static void initTracker(Context context) {
		
		mContext = context;
		
		if (Config.isDevelopment()) {
			return;
		}
		
		analytics = GoogleAnalytics.getInstance(context);
	    analytics.setLocalDispatchPeriod(1800);

	    tracker = analytics.newTracker("UA-64012601-1"); 
	    tracker.enableExceptionReporting(true);
	    tracker.enableAdvertisingIdCollection(false);
	    tracker.enableAutoActivityTracking(true);
	}
	
	public static Tracker getTracker() {
		
		return tracker;
	}
	
	public static void sendEvent(final String category, final String action) {
		
		if (tracker == null) {
			return;
		}
		
		getTracker().send(new HitBuilders.EventBuilder().setCategory(category).setAction(action).build());
		analytics.dispatchLocalHits();
	}
		
	public static void sendEvent(final String category, final String action, final String label) {
		
		if (tracker == null) {
			return;
		}
		
		getTracker().send(new HitBuilders.EventBuilder().setCategory(category).setAction(action).setLabel(label).build());
		analytics.dispatchLocalHits();
	}
	
	public static String generateRefId() {
	    
		long now = System.currentTimeMillis();

		while (true) {

			long lastTime = LAST_TIME_MS.get();

			if (lastTime >= now) {
				now = lastTime + 1;
			}

			if (LAST_TIME_MS.compareAndSet(lastTime, now)) {
				break;
			}
			
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		Date date = new Date(now);
		
		return formatDateTimeMiliSeconds(date); 
	}
	
	public static int convertDpToPix(int dp) {
		
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, mContext.getResources().getDisplayMetrics());
	}
	
	public static boolean compareString(String str1, String str2) {
		
		if (str1 != null && str2 != null && str1.equals(str2)) {
			return true;
		} else {
			return false;
		}
	}
	
	public static boolean isEmpty(String str) {
		
		if (str != null && str.length() != 0) {
			return false;
		} else {
			return true;
		}
	}
	
	public static int getMaxIndex(int recordCount, int limit) {
		
		System.out.println("record count : " + recordCount + " limit : " + limit);
		
		return recordCount > limit ? limit : recordCount;
	}
	
	public static Long getNvlId(ProductGroup prdGroup) {
		
		if (prdGroup == null) {
			return null;
		} else {
			return prdGroup.getId();
		}
	}
	
	public static Integer getNvlInt(Integer value) {
		
		if (value == null) {
			return 0;
		} else {
			return value;
		}
	}
	
	public static Float getNvlFloat(Float value) {
		
		if (value == null) {
			return Float.valueOf(0);
		} else {
			return value;
		}
	}
	
	public static Long getNvlLong(Long value) {
		
		if (value == null) {
			return Long.valueOf(0);
		} else {
			return value;
		}
	}
	
	public static String getNvlString(String value) {
		
		if (value == null) {
			return Constant.EMPTY_STRING;
		} else {
			return value;
		}
	}
	
	public static String getSqlLikeString(String value) {
		
		if (isEmpty(value)) {
			return "%";
		} else {
			
			String[] queries = value.split(" ");
			String likeStr = "";
			
			for (String str : queries) {
				
				if (isEmpty(likeStr)) {
					likeStr += "%" + str + "%";
				} else {
					likeStr += " %" + str + "%";
				}
			}
			
			return likeStr;
		}
	}
	
	public static Integer strToInt(String s) {
		
		Integer number = null;
		
		try {
			number = Integer.parseInt(s);
		} catch (NumberFormatException e) {
			// do nothing
		}
		
		return number;
	}
	
	public static Float strToFloat(String s) {
		
		Float number = null;
		
		try {
			number = Float.parseFloat(s);
		} catch (NumberFormatException e) {
			// do nothing
		}
		
		return number;
	}
	
	public static String intToStr(Integer i) {
		
		String number = null;
		
		try {
			if (i != null) {
				number = String.valueOf(i);
			}
		} catch (NumberFormatException e) {
			// do nothing
		}
		
		return number;
	}
	
	public static String floatToStr(Float i) {
		
		String number = null;
		
		try {
			if (i != null) {
				number = String.valueOf(i);
			}
		} catch (NumberFormatException e) {
			// do nothing
		}
		
		return number;
	}
	
	public static String trim(String str) {
		
		if (!isEmpty(str)) {
			return str.trim(); 
		} else {
			return Constant.EMPTY_STRING;
		}
	}
	
	static Locale locale;
	
	public static Locale getLocale() {
		
		if (locale == null) {
			locale = Locale.getDefault();
		}
		
		return locale;
	}
	
	public static void setLocale(Locale newLocale) {
		
		locale = newLocale;
	}
	
	public static Locale parseLocale(String localeStr) {
		
		String[] localeArray = !CommonUtil.isEmpty(localeStr) ? localeStr.split(",") : null;
		
		Locale locale = null;
		
		if (!CommonUtil.isEmpty(localeStr)) {
			locale = new Locale(localeArray[0], localeArray[1]);
		} else {
			locale = Locale.getDefault();
		}
		
		return locale;
	}
	
	public static DateFormat getDateFormat() {
		
		return SimpleDateFormat.getDateInstance(DateFormat.DEFAULT, getLocale());
	}
	
	public static DateFormat getDateFormat(String format) {
		
		return new SimpleDateFormat(format, getLocale());
	}
	
	public static DateFormat getDateTimeFormat() {
		
		return new SimpleDateFormat("dd MMM yyyy, HH:mm", getLocale());
	}
				
	public static DateFormat getTimeFormat() {
		
		return new SimpleDateFormat("HH:mm", getLocale());
	}
	
	public static DateFormat getDateMonthTimeFormat() {
		
		return new SimpleDateFormat("dd MMM, HH:mm", getLocale());
	}
		
	public static DateFormat getDateTimeMiliSecondsFormat() {
		
		return new SimpleDateFormat("yyyyMMddHHmmssSSS", getLocale());
	}
	
	public static DateFormat getDayDateTimeFormat() {
		
		return new SimpleDateFormat("EEE, dd MMM yyyy, HH:mm", getLocale());
	}
	
	public static String getOtpKey() {
		
		//SimpleDateFormat sdf = new SimpleDateFormat("MMddyyyy");
		//return sdf.format(new Date());
		
		return "";
	}
	
	private static void removeTime(Calendar cal) {
		
		cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
	}
	
	public static Date getFirstDayOfYear(Date date) {
		
		Calendar cal = Calendar.getInstance();
		
		cal.setTime(date);
		cal.set(Calendar.DATE, 1);
		cal.set(Calendar.MONTH, 0);
		
		return cal.getTime();
	}
	
	public static Date getLastDayOfYear(Date date) {
		
		Calendar cal = Calendar.getInstance();
		
		cal.setTime(date);
		cal.set(Calendar.DATE, 1);
		cal.set(Calendar.MONTH, 0);
		cal.add(Calendar.YEAR, 1);
		cal.add(Calendar.MILLISECOND, -1);
		
		return cal.getTime();
	}
	
	public static Date getFirstDayOfMonth(Date date) {
		
		Calendar cal = Calendar.getInstance();
		
		cal.setTime(date);
		cal.set(Calendar.DATE, 1);
		
		return cal.getTime();
	}
	
	public static Date getEndDay(Date date) {
		
		Calendar cal = Calendar.getInstance();
		
		cal.setTime(date);
		cal.add(Calendar.DATE, 1);
		cal.add(Calendar.MILLISECOND, -1);
		
		return cal.getTime();
	}
	
	public static Date getLastDayOfMonth(Date date) {
		
		Calendar cal = Calendar.getInstance();
		
		cal.setTime(date);
		cal.set(Calendar.DATE, 1);
		cal.add(Calendar.MONTH, 1);
		cal.add(Calendar.MILLISECOND, -1);
		
		return cal.getTime();
	}
	
	public static Date getCurrentYear() {
		
		Calendar cal = Calendar.getInstance();
		
		removeTime(cal);
		
		cal.set(Calendar.DATE, 1);
		cal.set(Calendar.MONTH, 0);
		
		return cal.getTime();
	}
	
	public static Date getCurrentMonth() {
		
		Calendar cal = Calendar.getInstance();
		
		removeTime(cal);
		
		cal.set(Calendar.DATE, 1);
		
		return cal.getTime();
	}
	
	public static Date parseDate(String dateStr) {
		
		Date date = null;
		
		try {
			date = getDateFormat().parse(dateStr);
		} catch (ParseException e) {
			// do nothing
		}
		
		return date;
	}
	
	public static Date parseDate(String dateStr, String format) {
		
		Date date = null;
		
		try {
			date = getDateFormat(format).parse(dateStr);
		} catch (ParseException e) {
			// do nothing
		}
		
		return date;
	}
	
	public static String formatReservationNo(String str) {
		
		return str != null && str.length() == 1 ? "0" + str : str;
	}
	
	public static String formatDate(Date inputDate) {
		
		String dateStr = Constant.EMPTY_STRING;
		
		try {
			dateStr = getDateFormat().format(inputDate);
		} catch (Exception e) {
			// do nothing
		}
		
		return dateStr;
	}
	
	public static String formatDayDate(Date inputDate) {
		
		String dateStr = Constant.EMPTY_STRING;
		
		try {
			dateStr = getDateFormat("EEE, dd MMM yyyy").format(inputDate);
		} catch (Exception e) {
			// do nothing
		}
		
		return dateStr;
	}
	
	public static String formatYear(Date inputDate) {
		
		String dateStr = Constant.EMPTY_STRING;
		
		try {
			dateStr = getDateFormat("yyyy").format(inputDate);
		} catch (Exception e) {
			// do nothing
		}
		
		return dateStr;
	}
	
	public static String formatMonth(Date inputDate) {
		
		String dateStr = Constant.EMPTY_STRING;
		
		try {
			dateStr = getDateFormat("MMMM yyyy").format(inputDate);
		} catch (Exception e) {
			// do nothing
		}
		
		return dateStr;
	}
	
	public static String formatDateTime(Date inputDate) {
		
		String dateStr = Constant.EMPTY_STRING;
		
		try {
			dateStr = getDateTimeFormat().format(inputDate);
		} catch (Exception e) {
			// do nothing
		}
		
		return dateStr;
	}
	
	public static String formatTime(Date inputDate) {
		
		String dateStr = Constant.EMPTY_STRING;
		
		try {
			dateStr = getTimeFormat().format(inputDate);
		} catch (Exception e) {
			// do nothing
		}
		
		return dateStr;
	}
	
	public static String formatDateMonthTime(Date inputDate) {
		
		String dateStr = Constant.EMPTY_STRING;
		
		try {
			dateStr = getDateMonthTimeFormat().format(inputDate);
		} catch (Exception e) {
			// do nothing
		}
		
		return dateStr;
	}
	
	public static String formatDayDateTime(Date inputDate) {
		
		String dateStr = Constant.EMPTY_STRING;
		
		try {
			dateStr = getDayDateTimeFormat().format(inputDate);
		} catch (Exception e) {
			// do nothing
		}
		
		return dateStr;
	}
	
	public static String formatDateTimeMiliSeconds(Date inputDate) {
		
		String dateStr = Constant.EMPTY_STRING;
		
		try {
			dateStr = new SimpleDateFormat("yyyyMMddHHmmssSSS", parseLocale("in,ID")).format(inputDate);
		} catch (Exception e) {
			// do nothing
		}
		
		return dateStr;
	}
	
	public static String getTransactionNo() {
		
		Date inputDate = new Date();
		
		String transactionNo = Constant.EMPTY_STRING;
		
		try {
			DateFormat df = new SimpleDateFormat("yyyyMMddHHmmssSSS");
			transactionNo = df.format(inputDate);
		} catch (Exception e) {
			// do nothing
		}
		
		return transactionNo;
	}
	
	public static String formatString(String inputStr) {
		
		if (inputStr != null) {
			return inputStr;
		} else {
			return Constant.EMPTY_STRING;
		}
	}
	
	public static String formatString(Integer inputInt) {
		
		if (inputInt != null) {
			return String.valueOf(inputInt);
		} else {
			return Constant.EMPTY_STRING;
		}
	}
	
	public static String formatString(Long inputInt) {
		
		if (inputInt != null) {
			return String.valueOf(inputInt);
		} else {
			return Constant.EMPTY_STRING;
		}
	}
	
	public static String formatString(Float inputInt) {
		
		if (inputInt != null) {
			return String.valueOf(inputInt);
		} else {
			return Constant.EMPTY_STRING;
		}
	}

	public static String formatString(Double inputInt) {

		if (inputInt != null) {
			return String.valueOf(inputInt);
		} else {
			return Constant.EMPTY_STRING;
		}
	}
	
	public static int getFirstNumberIndex(String input) {
		
		Matcher matcher = Pattern.compile("\\d+").matcher(input);
		matcher.find();
		
		return matcher.start();
	}
	
	public static String formatCurrency(String inputStr) {
		
		String currency = inputStr;
		String formatted = inputStr;
		
		NumberFormat nf = NumberFormat.getCurrencyInstance(getLocale());
		
        try{
            formatted = nf.format(Double.parseDouble(inputStr));
            
            int firstNumberIndex = getFirstNumberIndex(formatted);
            
            String symbol = Constant.EMPTY_STRING;
            String value = formatted;
            
            if (firstNumberIndex != -1) {
            	
            	symbol = formatted.substring(0, firstNumberIndex);
            	value = formatted.substring(firstNumberIndex, formatted.length());
            }
            
            currency = symbol + " " + value;
            
        } catch (NumberFormatException nfe) {
        	// do nothing
        }
        
        return currency;
	}
	
	/*public static String formatNumber(String inputStr) {
		
		String currency = inputStr;
		String formatted = inputStr;
		
		NumberFormat nf = NumberFormat.getCurrencyInstance(getLocale());
		
        try{
            formatted = nf.format(Double.parseDouble(inputStr));
            
            int firstNumberIndex = getFirstNumberIndex(formatted);
            
            if (firstNumberIndex != -1) {
            	currency = formatted.substring(firstNumberIndex, formatted.length());
            }
            
        } catch (NumberFormatException nfe) {
        	// do nothing
        }
        
        return currency;
	}*/
	
	public static boolean isRound(Float value) {
		
		return Math.round(value) == value;
	}
	
	public static boolean isDecimalCurrency() {
		
		String separator = getCurrencyDecimalSeparator();
		
		return separator != null && (separator.contains(".") || separator.contains(","));
	}
	
	public static String getCurrencyDecimalSeparator() {
		
		String separator = formatCurrency(Float.valueOf(0));
		separator = separator.replaceAll("[^.,]", "");
		
		separator = "".equals(separator) ? null : separator;
		
		return separator;
	}
	
	public static String getNumberDecimalSeparator() {
		
		String separator = formatNumber(Float.valueOf(1)/2);
		separator = separator.replaceAll("[^.,]", "");
		
		separator = "".equals(separator) ? null : separator;
		
		return separator;
	}
	
	public static String formatNumber(String inputStr) {
		
		String number = inputStr;
		
		NumberFormat nf = NumberFormat.getNumberInstance(getLocale());
		
        try{
            number = nf.format(Double.parseDouble(inputStr));
        } catch (NumberFormatException nfe) {
        	// do nothing
        }
        
        return number;
	}
	
	public static String formatPercentage(Float inputFloat) {
		
		return formatPercentage(formatString(inputFloat));
	}
	
	public static String formatPercentage(String inputStr) {
		
		String number = inputStr;
		
		NumberFormat nf = NumberFormat.getPercentInstance(getLocale());
		
        try{
            number = nf.format(Float.parseFloat(inputStr) / 100);
        } catch (NumberFormatException nfe) {
        	// do nothing
        }
        
        return number;
	}
	
	public static String formatCurrency(Integer inputInt) {
		
		return formatCurrency(formatString(inputInt));
	}
	
	public static String formatCurrency(Long inputInt) {
		
		return formatCurrency(formatString(inputInt));
	}
	
	public static String formatCurrency(Float inputInt) {
		
		return formatCurrency(formatString(inputInt));
	}

	public static String formatCurrency(Double inputInt) {

		return formatCurrency(formatString(inputInt));
	}
		
	public static String formatCurrencyWithoutSymbol(Float inputInt) {
		
		return formatCurrency(formatString(inputInt)).replace(getCurrencySymbol() + " ", "");
	}
	
	public static String formatNumber(Integer inputInt) {
		
		return formatNumber(formatString(inputInt));
	}
	
	public static String formatNumber(Long inputInt) {
		
		return formatNumber(formatString(inputInt));
	}
	
	public static String formatNumber(Float inputInt) {
		
		return formatNumber(formatString(inputInt));
	}

	public static String formatNumber(Double inputInt) {

		return formatNumber(formatString(inputInt));
	}

	public static String formatPlainNumber(Float inputInt) {
		
		String number = formatNumber(inputInt);
		
		if (".".equals(getNumberDecimalSeparator())) {
			number = number.replaceAll(",", "");
		} else {
			number = number.replaceAll("\\.", "");
		}
		
		return number; 
	}
	
	public static Integer parseIntNumber(String inputStr) {
		
		String unformatted = inputStr;
		Integer number = null;
		
		if (!isEmpty(inputStr)) {
			unformatted = inputStr.replaceAll("[^1234567890.,]", "");
		}
        
		try {
			NumberFormat nf = NumberFormat.getNumberInstance(getLocale());
			number = (nf.parse(unformatted)).intValue();
		} catch (Exception e) {
			// do nothing
		}
		
        return number; 
	}
	
	public static Float parseFloatNumber(String inputStr) {
		
		String unformatted = inputStr;
		Float number = null;
		
		if (!isEmpty(inputStr)) {
			unformatted = inputStr.replaceAll("[^1234567890.,]", "");
		}
        
		try {
			NumberFormat nf = NumberFormat.getNumberInstance(getLocale());
			number = (nf.parse(unformatted)).floatValue();
		} catch (Exception e) {
			// do nothing
		}
		
        return number; 
	}
	
	public static Integer parseIntCurrency(String inputStr) {
		
		String unformatted = inputStr;
		Integer number = null;
		
		if (!isEmpty(inputStr)) {
			unformatted = inputStr.replaceAll("[^1234567890.,]", "");
		}
        
		try {
			number = Integer.valueOf(unformatted);
		} catch (Exception e) {
			// do nothing
		}
		
        return number; 
	}
	
	public static Float parseFloatCurrency(String inputStr) {
		
		String unformatted = inputStr;
		Float number = null;
		
		if (!isEmpty(inputStr)) {
			unformatted = inputStr.replaceAll("[^1234567890.,]", "");
		}
        
		try {
			NumberFormat nf = NumberFormat.getNumberInstance(getLocale());
			number = nf.parse(unformatted).floatValue();
		} catch (Exception e) {
			// do nothing
		}
		
        return number; 
	}
	
	public static String parseCurrencyAsString(String inputStr) {
		
		String unformatted = inputStr;
		
		if (!isEmpty(inputStr)) {
			unformatted = inputStr.replaceAll("[^1234567890.,]", "");
		}
        
		return unformatted; 
	}
	
	public static Integer parseInteger(String inputStr) {
		
		Integer number = null;
		
		try {
			if (!isEmpty(inputStr)) {
				number = Integer.valueOf(inputStr);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
        return number; 
	}
	
	public static Float getCurrentPrice(Product product) {
		
		Float price = product.getPrice1();
		
		if (product.getPromoStart() != null && product.getPromoEnd() != null && product.getPromoPrice() != null) {
			
			Date curDate = new Date();
			
			if (product.getPromoStart().getTime() <= curDate.getTime() &&
				curDate.getTime() <= product.getPromoEnd().getTime()) {
				
				price = product.getPromoPrice();
			}
		}
		
		return price;
	}
		
	public static String getCurrencySymbol() {
		
		String symbol = Constant.EMPTY_STRING;
		NumberFormat nf = NumberFormat.getCurrencyInstance(getLocale());
		
        try{
            String formatted = nf.format(Double.valueOf(0));
            
            int firstNumberIndex = getFirstNumberIndex(formatted);
            
            if (firstNumberIndex != -1) {
            	symbol = formatted.substring(0, firstNumberIndex);
            }
            
        } catch (NumberFormatException nfe) {
        	// do nothing
        }
        
        return symbol;
	}
	
	public static String getShortUnitName(String quantityType) {
		
		String shortDesc = Constant.EMPTY_STRING;
		
		if (Constant.QUANTITY_TYPE_DOZEN.equals(quantityType)) {
			shortDesc = mContext.getString(R.string.quantity_type_dozen_short);
		
		} else if (Constant.QUANTITY_TYPE_KG.equals(quantityType)) {
			shortDesc = mContext.getString(R.string.quantity_type_kg_short);
		
		} else if (Constant.QUANTITY_TYPE_GRAM.equals(quantityType)) {
			shortDesc = mContext.getString(R.string.quantity_type_gram_short);

		} else if (Constant.QUANTITY_TYPE_LITER.equals(quantityType)) {
			shortDesc = mContext.getString(R.string.quantity_type_liter_short);
		
		} else if (Constant.QUANTITY_TYPE_MILILITER.equals(quantityType)) {
			shortDesc = mContext.getString(R.string.quantity_type_milliliter_short);

		} else if (Constant.QUANTITY_TYPE_METER.equals(quantityType)) {
			shortDesc = mContext.getString(R.string.quantity_type_meter_short);
		
		} else if (Constant.QUANTITY_TYPE_PACK.equals(quantityType)) {
			shortDesc = mContext.getString(R.string.quantity_type_pack_short);
		
		} else if (Constant.QUANTITY_TYPE_PIECE.equals(quantityType)) {
			shortDesc = mContext.getString(R.string.quantity_type_piece_short);
		}
		
		return shortDesc;
	}
	
	public static String getCertDN(Context ctx) {
		
		if (!CommonUtil.isEmpty(mCertDN)) {
			return mCertDN;
		}
		
		try {
			PackageInfo pinfo = ctx.getPackageManager().getPackageInfo(ctx.getPackageName(), PackageManager.GET_SIGNATURES);
			Signature signatures[] = pinfo.signatures;

			CertificateFactory cf = CertificateFactory.getInstance("X.509");

			for (int i = 0; i < signatures.length; i++) {
				ByteArrayInputStream stream = new ByteArrayInputStream(signatures[i].toByteArray());
				X509Certificate cert = (X509Certificate) cf.generateCertificate(stream);

				mCertDN = cert.getSubjectDN().getName();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return mCertDN;
	}

	public static String convertLanguageIso3toIso(String lang) {

		String langIso2 = lang;

		if (!CommonUtil.isEmpty(lang)) {

			if (lang.length() > 2) {

                langIso2 = nonStdLangIso3Iso2Map.get(lang);

                if (CommonUtil.isEmpty(langIso2)) {
                    langIso2 = lang.substring(0, 2);
                }
			}
		}

		return langIso2;
	}

    public static String convertCountryIso3toIso(String country) {

        String countryIso2 = country;

        if (!CommonUtil.isEmpty(country)) {

            if (country.length() > 2) {

                countryIso2 = nonStdCountryIso3Iso2Map.get(country);

                if (CommonUtil.isEmpty(countryIso2)) {
                    countryIso2 = country.substring(0, 2);
                }
            }
        }

        return countryIso2;
    }
}
