package Indicator;

import exchangeAPI.*;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.Queue;

import DB.DB;
import DB.DB_ohlc;

import com.google.gson.*;

/*
 * 중복되있는 함수가 많음.
 * HLCV를 가지고 오는 함수 하나면 거의 모든 함수를 구현할 수 있지만
 * C,V를 각각 가져오는 함수가 구현되어있고
 * OHLC의  평균값을 구하는 함수도 별도로 구현되어 있다.
 * 
 * 컴포넌트화가 필요하다.
 */

public class IndicatorFunction {

	public static double[][] get_HLCV_HistoryArray(CryptowatchAPI crypt, String exchange, String coin, String base,
			int interval, int period_day) throws Exception {

		// return[0] = 고가, return[1] = 저가, return[2] = 종가, return[3] = 볼륨
		int size = period_day;
		double ret[][] = new double[size][4];

		long now = System.currentTimeMillis() / 1000;

		String symb;

		System.out.println(exchange);
		System.out.println(exchange.equals("BINANCE"));
		System.out.println(exchange.equals("binance"));

		if (exchange.equals("binance") || exchange.equals("BINANCE") || exchange.equals("HITBTC")
				|| exchange.equals("hitbtc")) {
			symb = coin + base;
		} else {
			symb = coin;
		}

		DB_ohlc db = new DB_ohlc();
		ResultSet rs = null;

		// 갯수 만큼 가지고 옴! LIMIT!
		String sql = String.format("SELECT h,l,c,v FROM %sOHLC_%s_%s ORDER BY uTime ASC LIMIT %s", exchange, interval,
				symb, period_day);

		rs = db.Query(sql, "select");
		rs.next();
		int cnt = 0;
		try {
			while (rs.next()) {
				ret[cnt][0] = rs.getDouble(1);
				ret[cnt][1] = rs.getDouble(2);
				ret[cnt][2] = rs.getDouble(3);
				ret[cnt][3] = rs.getDouble(4);
				cnt++;

			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		db.clean();

		// 체결데이터에서 OHLC를 만들어내는 부분
		long after;
		now = System.currentTimeMillis() / 1000;
		after = now - (now % interval);

		// 최근 H, L : 체결데이터테이블에서 High Low를 select
		String recentHL = String.format(
				"Select MAX(c), MIN(c), SUM(v) FROM %sOHLC_%s_%s WHERE uTime BETWEEN %s and %s; ", exchange, interval,
				symb, after, now);

		// C : 체결데이터에서 uTime이 가장 큰(오래된) 데이터의 가격을 select
		String selectForC = String.format(
				" SELECT price FROM %sOneMinute%s WHERE uTime IN ( SELECT MAX(uTime) FROM %sOneMinute%s WHERE uTime BETWEEN %s and %s);",
				exchange, symb, exchange, symb, after - 1, now + 1);

		DB_ohlc db1 = new DB_ohlc();
		DB_ohlc db2 = new DB_ohlc();
		DB_ohlc db3 = new DB_ohlc();

		ResultSet rs1 = db1.Query(recentHL, "select");
		ResultSet rs2 = db2.Query(selectForC, "select");

		double o, h, l, c, v;

		if (!rs1.next() || !rs2.next()) {
			ret[size - 1][0] = ret[size - 2][0];
			ret[size - 1][1] = ret[size - 2][1];
			ret[size - 1][2] = ret[size - 2][2];
			ret[size - 1][3] = 0;
		} else {
			h = rs1.getDouble(1);
			l = rs1.getDouble(2);
			v = rs1.getDouble(3);
			c = rs2.getDouble(1);
			ret[size - 1][0] = h;
			ret[size - 1][1] = l;
			ret[size - 1][2] = c;
			ret[size - 1][3] = v;
		}

		db1.clean();
		db2.clean();
		db3.clean();

		return ret;

	}

	// 사용되지 않음.
	public static double[][] get_HLCV_Test(CryptowatchAPI crypt, String exchange, String coin, String base,
			int interval, int period_day) throws Exception {

		int size = period_day;
		double ret[][] = new double[size][5];

		long now = System.currentTimeMillis() / 1000;

		String symb;
		System.out.println(exchange);
		System.out.println(exchange.equals("BINANCE"));
		System.out.println(exchange.equals("binance"));

		if (exchange.equals("binance") || exchange.equals("BINANCE") || exchange.equals("HITBTC")
				|| exchange.equals("hitbtc")) {
			symb = coin + base;
		} else {
			symb = coin;
		}

		DB_ohlc db = new DB_ohlc();
		ResultSet rs = null;
		// 시간을 기준으로 select하는 쿼리
		// String sql = String.format("SELECT h,l,c,v,uTime FROM %sOHLC_%s_%s WHERE
		// uTime BETWEEN %s AND %s ORDER BY uTime ASC", exchange,
		// interval, symb, now - ((period_day - 1) * interval) - 100, now + 50);

		// 갯수를 기준으로 select하는 쿼리
		String sql = String.format("SELECT h,l,c,v,uTime FROM %sOHLC_%s_%s ORDER BY uTime ASC LIMIT %s", exchange,
				interval, symb, period_day);

		rs = db.Query(sql, "select");

		int cnt = 0;
		try {
			while (rs.next()) {
				ret[cnt][0] = rs.getDouble(1);
				ret[cnt][1] = rs.getDouble(2);
				ret[cnt][2] = rs.getDouble(3);
				ret[cnt][3] = rs.getDouble(4);
				ret[cnt][4] = rs.getDouble(5);
				cnt++;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		db.clean();

		return ret;

	}

	// 배열이 아닌 큐를 만드는 함수.
	public static Queue<Double> getHistoryQueue(String exchange, String coin, String base, int interval, int period_day)
			throws Exception {

		Queue<Double> historyQueue = new LinkedList<Double>();
		long now = System.currentTimeMillis() / 1000;

		String symb;
		System.out.println(exchange);
		System.out.println(exchange.equals("BINANCE"));
		System.out.println(exchange.equals("binance"));

		if (exchange.equals("binance") || exchange.equals("BINANCE") || exchange.equals("HITBTC")
				|| exchange.equals("hitbtc")) {
			symb = coin + base;
		} else {
			symb = coin;
		}

		DB_ohlc db = new DB_ohlc();
		ResultSet rs = null;

		String sql = String.format("SELECT c FROM %sOHLC_%s_%s ORDER BY uTime ASC LIMIT %s", exchange, interval, symb,
				period_day);

		// 시간 기준 쿼리
		// String sql = String.format("SELECT c FROM %sOHLC_%s_%s WHERE uTime BETWEEN %s
		// AND %s ORDER BY uTime ASC",
		// exchange, interval, symb, now - ((period_day - 1) * interval) - 100, now +
		// 50);

		rs = db.Query(sql, "select");
		double temp = 0;
		try {
			while (rs.next()) {
				double c = rs.getDouble(1);
				historyQueue.add(c);
				temp = c;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// System.out.println("indicatorFunc rs result number : " + cnt);
		db.clean();

		historyQueue.poll();

		long after;
		now = System.currentTimeMillis() / 1000;
		after = now - (now % interval);

		// 가격만 필요하므로 C
		String selectForC = String.format(
				" SELECT price FROM %sOneMinute%s WHERE uTime IN ( SELECT MAX(uTime) FROM %sOneMinute%s WHERE uTime BETWEEN %s and %s);",
				exchange, symb, exchange, symb, after - 1, now + 1);

		DB_ohlc db2 = new DB_ohlc();

		ResultSet rs2 = db2.Query(selectForC, "select");

		double cc;

		if (!rs2.next()) {
			historyQueue.add(temp);
		} else {
			cc = rs2.getDouble(1);
			historyQueue.add(cc);
		}

		db2.clean();

		return historyQueue;
	}

	// 가격리스트. 아무것도 적혀있지 않는 HistoryArray는 가격임.
	public static double[] getHistoryArray(CryptowatchAPI crypt, String exchange, String coin, String base,
			int interval, int period_day) throws Exception {

		int size = period_day;
		double ret[] = new double[size];
		long now = System.currentTimeMillis() / 1000;
		String symb;
		System.out.println(exchange);
		System.out.println(exchange.equals("BINANCE"));
		System.out.println(exchange.equals("binance"));

		if (exchange.equals("binance") || exchange.equals("BINANCE") || exchange.equals("HITBTC")
				|| exchange.equals("hitbtc")) {
			symb = coin + base;
		} else {
			symb = coin;
		}

		DB_ohlc db = new DB_ohlc();
		ResultSet rs = null;
		String sql = String.format("SELECT c FROM %sOHLC_%s_%s ORDER BY uTime ASC LIMIT %s", exchange, interval, symb,
				period_day);

		// 시간 기준 쿼리
		// String sql = String.format("SELECT c FROM %sOHLC_%s_%s WHERE uTime BETWEEN %s
		// AND %s ORDER BY uTime ASC",
		// exchange, interval, symb, now - ((period_day - 1) * interval) - 100, now +
		// 50);

		rs = db.Query(sql, "select");

		int cnt = 0;
		rs.next();
		try {
			while (rs.next()) {
				ret[cnt++] = rs.getDouble(1);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		db.clean();

		long after;
		now = System.currentTimeMillis() / 1000;
		after = now - (now % interval);

		String selectForC = String.format(
				" SELECT price FROM %sOneMinute%s WHERE uTime IN ( SELECT MAX(uTime) FROM %sOneMinute%s WHERE uTime BETWEEN %s and %s);",
				exchange, symb, exchange, symb, after - 1, now + 1);

		DB_ohlc db2 = new DB_ohlc();

		ResultSet rs2 = db2.Query(selectForC, "select");

		double c;

		if (!rs2.next()) {
			// 만약 그 사이에 하나도 체결이 이뤄지지 않으면
			// 그냥 그 전의 OHLC데이터를 그대로 사용
			ret[size - 1] = ret[size - 2];
		} else {
			// 있으면 이거쓰고~
			c = rs2.getDouble(1);
			ret[size - 1] = c;
		}

		db2.clean();

		return ret;
	}

	public static double[] getHistoryVolumeArray(CryptowatchAPI crypt, String exchange, String coin, String base,
			int interval, int period_day) throws Exception {
		int size = period_day;
		double ret[] = new double[size];
		long now = System.currentTimeMillis() / 1000;
		String symb;
		System.out.println(exchange);
		System.out.println(exchange.equals("BINANCE"));
		System.out.println(exchange.equals("binance"));

		if (exchange.equals("binance") || exchange.equals("BINANCE") || exchange.equals("HITBTC")
				|| exchange.equals("hitbtc")) {
			symb = coin + base;
		} else {
			symb = coin;
		}

		DB_ohlc db = new DB_ohlc();
		ResultSet rs = null;
		// String sql = String.format("SELECT v FROM %sOHLC_%s_%s WHERE uTime BETWEEN %s
		// AND %s ORDER BY uTime ASC",
		// exchange, interval, symb, now - ((period_day - 1) * interval) - 100, now +
		// 50);
		String sql = String.format("SELECT v FROM %sOHLC_%s_%s ORDER BY uTime ASC LIMIT %s", exchange, interval, symb,
				period_day);

		rs = db.Query(sql, "select");

		int cnt = 0;
		rs.next();
		try {
			while (rs.next()) {
				ret[cnt++] = rs.getDouble(1);

			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		db.clean();

		long after;
		now = System.currentTimeMillis() / 1000;
		after = now - (now % interval);

		String recentV = String.format("Select SUM(v) FROM %sOHLC_%s_%s WHERE uTime BETWEEN %s and %s; ", exchange,
				interval, symb, after, now);

		DB_ohlc db2 = new DB_ohlc();

		ResultSet rs2 = db2.Query(recentV, "select");

		double v;

		if (!rs2.next()) {
			// volume의 경우 하나도 체결이 이뤄지지 안으면 0이다.
			ret[size - 1] = 0;
		} else {
			v = rs2.getDouble(1);
			ret[size - 1] = v;
		}

		db2.clean();

		return ret;
	}

	// H L C 평균!
	public static double[] getHistoryMeanPriceArray(CryptowatchAPI crypt, String exchange, String coin, String base,
			int interval, int period_day) throws Exception {

		int size = period_day;
		double ret[] = new double[size];
		long now = System.currentTimeMillis() / 1000;
		String symb;
		System.out.println(exchange);
		System.out.println(exchange.equals("BINANCE"));
		System.out.println(exchange.equals("binance"));

		if (exchange.equals("binance") || exchange.equals("BINANCE") || exchange.equals("HITBTC")
				|| exchange.equals("hitbtc")) {
			symb = coin + base;
		} else {
			symb = coin;
			// 빗썸은 그냥 coin만
			// 테이블 이름을 참고
		}

		DB_ohlc db = new DB_ohlc();
		ResultSet rs = null;
		
		// 시간 기준 쿼리
		// String sql = String.format("SELECT h,l,c FROM %sOHLC_%s_%s WHERE uTime BETWEEN %s
		// AND %s ORDER BY uTime ASC",
		// exchange, interval, symb, now - ((period_day - 1) * interval) - 100, now +
		// 50);
		
		String sql = String.format("SELECT h,l,c FROM %sOHLC_%s_%s ORDER BY uTime ASC LIMIT %s", exchange, interval,
				symb, period_day);

		rs = db.Query(sql, "select");

		int cnt = 0;
		rs.next();
		try {
			while (rs.next()) {
				ret[cnt++] = (rs.getDouble(1) + rs.getDouble(2) + rs.getDouble(3)) / 3;

			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		db.clean();

		long after;
		now = System.currentTimeMillis() / 1000;
		after = now - (now % interval);
		String recentHL = String.format(
				"Select MAX(c), MIN(c), SUM(v) FROM %sOHLC_%s_%s WHERE uTime BETWEEN %s and %s; ", exchange, interval,
				symb, after, now);

		String selectForC = String.format(
				" SELECT price FROM %sOneMinute%s WHERE uTime IN ( SELECT MAX(uTime) FROM %sOneMinute%s WHERE uTime BETWEEN %s and %s);",
				exchange, symb, exchange, symb, after - 1, now + 1);

		DB_ohlc db1 = new DB_ohlc();
		DB_ohlc db2 = new DB_ohlc();

		ResultSet rs1 = db1.Query(recentHL, "select");
		ResultSet rs2 = db2.Query(selectForC, "select");

		double h, l, c;

		if (!rs2.next() || !rs1.next()) {

			ret[size - 1] = ret[size - 2];

		} else {
			h = rs1.getDouble(1);
			l = rs1.getDouble(2);
			c = rs2.getDouble(1);

			ret[size - 1] = (h + l + c) / 3;
		}

		return ret;
	}

	// 지수평균
	public static double getEMA(double[] arr) {

		int len = arr.length;
		double d[] = new double[len];
		d[0] = arr[0];

		// 다이나믹 프로그래밍
		for (int i = 1; i < len; i++) {
			double k;
			k = 2.0 / (i + 2);
			d[i] = d[i - 1] * (1 - k) + arr[i] * k;
		}

		return d[len - 1];
	}

	// 사용하지 않음.
	public static double getMACDEma(double[] arr) {

		int len = arr.length;
		double d[] = new double[len];
		d[0] = sumDouble(arr) / len;

		for (int i = 1; i < len; i++) {

			double factor = (2 / (i + 1 + 1));
			d[i] = (arr[i] - d[i - 1]) * factor + d[i - 1];
		}

		return d[len - 1];

	}

	public static double sumDouble(double[] arr) {

		double ret = 0;

		for (int i = 0; i < arr.length; i++) {

			ret += arr[i];
		}

		return ret;
	}

	public static double[] makeSublist(double[] arr, int s, int e) {

		double[] ret = new double[e - s + 1];

		for (int i = 0; i < e - s + 1; i++) {

			ret[i] = arr[i + s];

		}
		return ret;
	}

	public static double[][] get_HLCV_HistoryArray2(CryptowatchAPI crypt, String exchange, String coin, String base,
			int interval, long startUnix, long endUnix) {

		long size = (endUnix - startUnix) / interval;
		double ret[][] = new double[5][(int) size];

		long now = System.currentTimeMillis() / 1000;

		String symb;
		if (exchange == "binance") {
			symb = coin + base;
		} else {
			symb = coin;
		}

		DB_ohlc db = new DB_ohlc();
		ResultSet rs = null;
		String sql = String.format("SELECT h,l,c,v,uTime FROM %sOHLC_%s_%s WHERE uTime BETWEEN %s AND %s", exchange,
				interval, symb, startUnix - 10, endUnix + 10);

		int cnt = 0;
		try {
			rs = db.Query(sql, "select");
			while (rs.next()) {
				ret[0][cnt] = rs.getDouble(1);
				ret[1][cnt] = rs.getDouble(2);
				ret[2][cnt] = rs.getDouble(3);
				ret[3][cnt] = rs.getDouble(4);
				ret[4][cnt] = rs.getLong(5);
				cnt++;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		db.clean();
		return ret;
	}

}