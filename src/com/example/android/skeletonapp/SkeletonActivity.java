package com.example.android.skeletonapp;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import org.afree.chart.AFreeChart;
import org.afree.chart.ChartFactory;
import org.afree.chart.axis.ValueAxis;
import org.afree.chart.plot.CategoryPlot;
import org.afree.chart.plot.PlotOrientation;
import org.afree.data.category.DefaultCategoryDataset;
import org.apache.commons.math3.stat.descriptive.SummaryStatistics;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Window;

import com.google.gson.Gson;

public class SkeletonActivity extends Activity {
	
	enum TestType {
		NUMBER,
		STRING,
		HREF;
		
	}
	
	private Gson gson;
	
	private ChartView chartView;
	private AFreeChart chart;
	private DefaultCategoryDataset dataset;
    
    public SkeletonActivity() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        chartView = new ChartView(this);
        setContentView(chartView);
        
        dataset = new DefaultCategoryDataset();
        chart = ChartFactory.createBarChart("stuff", "test", "duration", dataset, PlotOrientation.VERTICAL, true, false, false);
        CategoryPlot plot = chart.getCategoryPlot();
        plot.getRangeAxis().setLowerBound(0.0);
        chartView.setChart(chart);
        
//        FileCreateTask task = new FileCreateTask();
//        task.execute();
        
        DownloadTask task = new DownloadTask();
        task.execute();
        
        
    }
    
    private Gson getGson() {
    	if (gson == null) {
    		gson = new Gson();
    	}
    	return gson;
    }
    
    private void pause() {
		try {
			Thread.sleep(200);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
    
    protected class FileCreateTask extends AsyncTask<Void, Void, Void> {
    	
    	@Override
    	protected Void doInBackground(Void... params) {
    		
    		makeIt(TestType.NUMBER);
    		makeIt(TestType.STRING);
    		makeIt(TestType.HREF);
    		
    		return null;
    	}
    	
    	@Override
    	protected void onPostExecute(Void result) {
    		Log.d("adsf", "DONE");
    	}
    	
    	
    	private void makeIt(TestType testType) {
    		try {
	    		String baseUrl = "http://api.somewebsite.com/something/v9";
	    		Random rnd = new Random();
	    		List<Thing> list = new ArrayList<Thing>();
	    		
	    		for(int i=0; i<365; i++) {
	    			Thing thing = new Thing();
	    			
	    			long idNum = Math.abs(rnd.nextInt());
	    			String idStr = idNum + "";
	    			String childA = Math.abs(rnd.nextInt()) + ""; 
	    			String childB = Math.abs(rnd.nextInt()) + "";
	    			
	    			if (testType == TestType.HREF) {
	    				idStr = baseUrl + "/thingabob/" + idStr;
	    				childA = baseUrl + "/childA/" + childA;
	    				childB = baseUrl + "/childB/" + childB;
	    			}
	    			
	    			thing.setIdNum(idNum);
	    			thing.setIdStr(idStr);
	    			thing.setChildA(childA);
	    			thing.setChildB(childB);
	    			thing.setFieldA(rnd.nextBoolean());
	    			thing.setFieldB(rnd.nextInt());
	    			thing.setFieldC(rnd.nextDouble());
	    			thing.setName(Long.toHexString(rnd.nextLong()));
	    			thing.setDate(new Date(System.currentTimeMillis() + rnd.nextInt(100000000)));
	    			
	    			list.add(thing);
	    		}
	    		
	    		ThingList things = new ThingList();
	    		things.setThings(list);
	    		
	    		String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/output_"+testType+".txt";
	    		FileWriter writer = new FileWriter(path);
	    		
	    		Gson gson = getGson();
	    		gson.toJson(things, ThingList.class, writer);
	    		writer.close();
	    		
	    		Log.d("asdf", "path="+path);
	    		
    		} catch(Exception e) {
    			throw new RuntimeException(e);
    		}
    	}
    }
    
    protected class DownloadTask extends AsyncTask<Void, Void, Void> {
    	
    	@Override
    	protected Void doInBackground(Void... params) {
    		
//    		String numberPath = "https://dl.dropboxusercontent.com/u/35637124/output_NUMBER.txt";
//    		String stringPath = "https://dl.dropboxusercontent.com/u/35637124/output_STRING.txt";
//    		String hrefPath = "https://dl.dropboxusercontent.com/u/35637124/output_HREF.txt";
    		
    		String numberPath = "file://" + Environment.getExternalStorageDirectory().getAbsolutePath() + "/output_NUMBER.txt";
    		String stringPath = "file://" + Environment.getExternalStorageDirectory().getAbsolutePath() + "/output_STRING.txt";
    		String hrefPath = "file://" + Environment.getExternalStorageDirectory().getAbsolutePath() + "/output_HREF.txt";
    		
    		getIt(TestType.NUMBER, numberPath, "db_NUMBER",
					new Stats("NUMBER"));
    		getIt(TestType.STRING, stringPath, "db_STRING",
    				new Stats("STRING"));
    		getIt(TestType.HREF, hrefPath, "db_HREF",
    				new Stats("HREF"));
    		
    		int testRuns = 40;
    		
    		Stats num = new Stats("NUMBER");
    		Stats str = new Stats("STRING");
    		Stats href = new Stats("HREF");
    		
    		for (int i=0; i<testRuns; i++) {
    			
    			pause();
        		getIt(TestType.NUMBER, numberPath, "db_NUMBER", num);
        		num.update(dataset);
        		publishProgress();
        		
        		pause();
        		getIt(TestType.STRING, stringPath, "db_STRING", str);
        		str.update(dataset);
        		publishProgress();
        		
        		pause();
        		getIt(TestType.HREF, hrefPath, "db_HREF", href);
        		href.update(dataset);
        		publishProgress();
    		}
    		
    		Log.d("asdf", str.toString());
    		Log.d("asdf", num.toString());
    		Log.d("asdf", href.toString());
    		
    		return null;
    	}
    	
    	@Override
    	protected void onPostExecute(Void result) {
    		
    		Log.d("asdf", "DONE");
    	}
    	
    	@Override
    	protected void onProgressUpdate(Void... values) {
    		chartView.restoreAutoBounds();
    		chartView.repaint();
    	}
    	
    	private void getIt(TestType testType, String path, String dbName, Stats stats) {
    		
    		File dbfile = getDatabasePath(dbName);
			dbfile.delete();
			
    		stats.start("full");
    		try {
    			stats.start("fetch");
    			URL url = new URL(path);
				URLConnection http = url.openConnection();
				Reader reader = new InputStreamReader(new BufferedInputStream(http.getInputStream()));
				Gson gson = getGson();
				ThingList things = gson.fromJson(reader, ThingList.class);
				reader.close();
				stats.end("fetch");
				
				stats.start("save");
				TestOpenHelper helper = new TestOpenHelper(getApplicationContext(), dbName, testType);
				SQLiteDatabase database = helper.getWritableDatabase();
				database.beginTransaction();
				List ids = new ArrayList();
				for(Thing thing:things.getThings()) {
					if (testType == TestType.NUMBER) {
						ids.add(thing.getIdNum());
					} else {
						ids.add(thing.getIdStr());
					}
					storeIt(testType, database, thing);
				}
				database.setTransactionSuccessful();
				database.endTransaction();
				database.close();
				stats.end("save");
				
				Collections.shuffle(ids);
				
				stats.start("load");
				database = helper.getWritableDatabase();
				for(Object id:ids) {
					unstoreIt(testType, database, id.toString());
				}
				database.close();
				stats.end("load");
				
			} catch (IOException e) {
				e.printStackTrace();
			}
    		
    		stats.end("full");
    		
    		Log.d("asdf", path);
    	}
    	
    	private void storeIt(TestType testType, SQLiteDatabase db, Thing thing) {
    		ContentValues cv = new ContentValues(4);
    		if (testType == TestType.NUMBER) {
    			cv.put("_id", thing.getIdNum());
    		} else {
    			cv.put("_id", thing.getIdStr());
    		}
			
			cv.put("childA", thing.getChildA());
			cv.put("childB", thing.getChildB());
			cv.put("fieldA", thing.getFieldA());
			cv.put("fieldB", thing.getFieldB());
			cv.put("fieldC", thing.getFieldC());
			cv.put("name", thing.getName());
			cv.put("date", thing.getDate().getTime());
			db.insert(TestOpenHelper.DICTIONARY_TABLE_NAME, null, cv);
    	}
    	
    	private Thing unstoreIt(TestType testType, SQLiteDatabase db, String id) {
    		
    		String[] cols = new String[]{"_id", "childA", "childB", "fieldA", "fieldB", "fieldC", "name", "date"};
    		
    		Cursor cursor = db.query(TestOpenHelper.DICTIONARY_TABLE_NAME, cols, "_id=?", new String[]{id}, null, null, null);
    		
    		cursor.moveToFirst();
    		Thing thing = new Thing();
    		if (testType == TestType.NUMBER) {
    			thing.setIdNum(cursor.getLong(0));
    		} else {
    			thing.setIdStr(cursor.getString(0));
    		}
    		
    		thing.setChildA(cursor.getString(1));
    		thing.setChildB(cursor.getString(2));
    		thing.setFieldA(cursor.getInt(3)==1);
    		thing.setFieldB(cursor.getInt(4));
    		thing.setFieldC(cursor.getDouble(5));
    		thing.setName(cursor.getString(6));
    		thing.setDate(new Date(cursor.getLong(7)));
    		cursor.close();
    		
    		return thing;
    	}
    }
    
    public class TestOpenHelper extends SQLiteOpenHelper {

        private static final int DATABASE_VERSION = 2;
        public static final String DICTIONARY_TABLE_NAME = "stuff";
        
        private TestType testType;

        TestOpenHelper(Context context, String file, TestType testType) {
            super(context, file, null, DATABASE_VERSION);
            this.testType = testType;
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(getCreateText(testType));
			
        }

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			
		}
		
		public String getCreateText(TestType testType) {
			String answer = "CREATE TABLE " + DICTIONARY_TABLE_NAME + " (";
	        if (testType == TestType.NUMBER) {
	        	answer += "_id" + " INTEGER PRIMARY KEY, ";
	        } else {
	        	answer += "_id" + " VARCHAR PRIMARY KEY, ";
	        }
            
	        answer +=
	            "childA" + " VARCHAR, " +
	            "childB" + " VARCHAR, " +
	            "fieldA" + " BIT, " +
	            "fieldB" + " INTEGER, " +
	            "fieldC" + " DECIMAL, " +
	            "name" + " VARCHAR, " +
	            "date" + " VARCHAR);";
	        
	        return answer;
		}
    }
    
    
    protected class Stats {
    	
    	private String name;
    	private Map<String, Long> startTimes = new HashMap<String, Long>();
    	private Map<String, SummaryStatistics> stats = new LinkedHashMap<String, SummaryStatistics>();
    	
    	public Stats(String name) {
    		this.name = name;
    	}
    	
    	public void start(String param) {
    		long now = System.currentTimeMillis();
    		startTimes.put(param, now);
    	}
    	
    	public void end(String param) {
    		long now = System.currentTimeMillis();
    		long start = startTimes.remove(param);
    		long dur = now - start;
    		SummaryStatistics summary = stats.get(param);
    		if (summary == null) {
    			summary = new SummaryStatistics();
    			stats.put(param, summary);
    		}
    		summary.addValue(dur);
    	}
    	
    	@Override
    	public String toString() {
    		StringBuilder sb = new StringBuilder("====="+name+ "=====\n");
    		
    		for(Entry<String, SummaryStatistics> entry : stats.entrySet()){
    			
    			sb.append("@"+entry.getKey() + ": "+entry.getValue().toString());
    		}
    		
    		return sb.toString();
    	}
    	
    	public void update(DefaultCategoryDataset ds) {
    		
    		for(Entry<String, SummaryStatistics> entry : stats.entrySet()){
    			
    			ds.setValue(entry.getValue().getMean(), entry.getKey(), name);
    		}
    	}
    }

}
