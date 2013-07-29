package com.example.android.skeletonapp;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Random;

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

import com.google.gson.Gson;

public class SkeletonActivity extends Activity {
	
	private Gson gson;
    
    public SkeletonActivity() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.skeleton_activity);
        
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
			Thread.sleep(500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
    
    private class FileCreateTask extends AsyncTask<Void, Void, Void> {
    	
    	@Override
    	protected Void doInBackground(Void... params) {
    		
    		makeIt(false);
    		makeIt(true);
    		
    		return null;
    	}
    	
    	@Override
    	protected void onPostExecute(Void result) {
    		Log.d("adsf", "DONE");
    	}
    	
    	
    	private void makeIt(boolean doHref) {
    		try {
	    		String baseUrl = "http://api.mapmyfitness.com/something/v9";
	    		Random rnd = new Random();
	    		List<Thing> list = new ArrayList<Thing>();
	    		
	    		for(int i=0; i<365; i++) {
	    			Thing thing = new Thing();
	    			
	    			String id = Math.abs(rnd.nextInt()) + "";
	    			String childA = Math.abs(rnd.nextInt()) + ""; 
	    			String childB = Math.abs(rnd.nextInt()) + "";
	    			
	    			if (doHref) {
	    				id = baseUrl + "/thingabob/" + id;
	    				childA = baseUrl + "/childA/" + childA;
	    				childB = baseUrl + "/childB/" + childB;
	    			}
	    			
	    			thing.setId(id);
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
	    		
	    		String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/output_"+doHref+".txt";
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
    
    private class DownloadTask extends AsyncTask<Void, Void, Void> {
    	
    	@Override
    	protected Void doInBackground(Void... params) {
    		
    		getIt("https://dl.dropboxusercontent.com/u/35637124/output_false.txt", "db_false",
					new SummaryStatistics(), new SummaryStatistics(), new SummaryStatistics(), new SummaryStatistics());
    		getIt("https://dl.dropboxusercontent.com/u/35637124/output_false.txt", "db_false",
    				new SummaryStatistics(), new SummaryStatistics(), new SummaryStatistics(), new SummaryStatistics());
    		
    		int testRuns = 40;
    		
    		SummaryStatistics fullSummaryFalse = new SummaryStatistics();
    		SummaryStatistics fetchSummaryFalse = new SummaryStatistics();
    		SummaryStatistics saveSummaryFalse = new SummaryStatistics();
    		SummaryStatistics loadSummaryFalse = new SummaryStatistics();
    		
    		SummaryStatistics fullSummaryTrue = new SummaryStatistics();
    		SummaryStatistics fetchSummaryTrue = new SummaryStatistics();
    		SummaryStatistics saveSummaryTrue = new SummaryStatistics();
    		SummaryStatistics loadSummaryTrue = new SummaryStatistics();
    		
    		for (int i=0; i<testRuns; i++) {
    			
//    			pause();
//    			dur1 += getIt("file:///storage/emulated/0/output_false.txt", "db_false");
//    			pause();
//    			dur2 += getIt("file:///storage/emulated/0/output_true.txt", "db_true");
    			
    			pause();
    			getIt("https://dl.dropboxusercontent.com/u/35637124/output_false.txt", "db_false",
    					fetchSummaryFalse, saveSummaryFalse, loadSummaryFalse, fullSummaryFalse);
    			
    			pause();
    			getIt("https://dl.dropboxusercontent.com/u/35637124/output_true.txt", "db_true",
    					fetchSummaryTrue, saveSummaryTrue, loadSummaryTrue, fullSummaryTrue);
    		}
    		
    		Log.d("asdf", "fetchFalse="+fetchSummaryFalse.toString());
    		Log.d("asdf", "fetchTrue="+fetchSummaryTrue.toString());
    		
    		Log.d("asdf", "saveFalse="+saveSummaryFalse.toString());
    		Log.d("asdf", "saveTrue="+saveSummaryTrue.toString());
    		
    		Log.d("asdf", "loadFalse="+loadSummaryFalse.toString());
    		Log.d("asdf", "loadTrue="+loadSummaryTrue.toString());
    		
    		Log.d("asdf", "fullFalse="+fullSummaryFalse.toString());
    		Log.d("asdf", "fullTrue="+fullSummaryTrue.toString());
    		
    		return null;
    	}
    	
    	@Override
    	protected void onPostExecute(Void result) {
    		Log.d("asdf", "DONE");
    	}
    	
    	private void getIt(String path, String dbName, 
    			SummaryStatistics fetchSummary, SummaryStatistics saveSummary, 
    			SummaryStatistics loadSummary, SummaryStatistics fullSummary) {
    		
    		File dbfile = getDatabasePath(dbName);
			dbfile.delete();
			
    		long fullStart = System.currentTimeMillis();
    		try {
    			long fetchStart = System.currentTimeMillis();
    			URL url = new URL(path);
				HttpURLConnection http = (HttpURLConnection) url.openConnection();
				Reader reader = new InputStreamReader(new BufferedInputStream(http.getInputStream()));
				Gson gson = getGson();
				ThingList things = gson.fromJson(reader, ThingList.class);
				http.disconnect();
				long fetchDur = System.currentTimeMillis() - fetchStart;
				fetchSummary.addValue(fetchDur);
				
				long saveStart = System.currentTimeMillis();
				TestOpenHelper helper = new TestOpenHelper(getApplicationContext(), dbName);
				SQLiteDatabase database = helper.getWritableDatabase();
				database.beginTransaction();
				List<String> ids = new ArrayList<String>();
				for(Thing thing:things.getThings()) {
					ids.add(thing.getId());
					storeIt(database, thing);
				}
				database.setTransactionSuccessful();
				database.endTransaction();
				database.close();
				long saveDur = System.currentTimeMillis() - saveStart;
				saveSummary.addValue(saveDur);
				
				Collections.shuffle(ids);
				
				long loadStart = System.currentTimeMillis();
				database = helper.getWritableDatabase();
				for(String id:ids) {
					unstoreIt(database, id);
				}
				database.close();
				long loadDur = System.currentTimeMillis() - loadStart;
				loadSummary.addValue(loadDur);
				
				
				
			} catch (IOException e) {
				e.printStackTrace();
			}
    		
    		long fullDur = System.currentTimeMillis() - fullStart;
    		fullSummary.addValue(fullDur);
    		
    		Log.d("asdf", path);
    		Log.d("asdf", "dur="+fullDur);
    	}
    	
    	private void storeIt(SQLiteDatabase db, Thing thing) {
    		ContentValues cv = new ContentValues(4);
			cv.put("_id", thing.getId());
			cv.put("childA", thing.getChildA());
			cv.put("childB", thing.getChildB());
			cv.put("fieldA", thing.getFieldA());
			cv.put("fieldB", thing.getFieldB());
			cv.put("fieldC", thing.getFieldC());
			cv.put("name", thing.getName());
			cv.put("date", thing.getDate().getTime());
			db.insert(TestOpenHelper.DICTIONARY_TABLE_NAME, null, cv);
    	}
    	
    	private Thing unstoreIt(SQLiteDatabase db, String id) {
    		
    		String[] cols = new String[]{"_id", "childA", "childB", "fieldA", "fieldB", "fieldC", "name", "date"};
    		
    		Cursor cursor = db.query(TestOpenHelper.DICTIONARY_TABLE_NAME, cols, "_id=?", new String[]{id}, null, null, null);
    		
    		cursor.moveToFirst();
    		Thing thing = new Thing();
    		thing.setId(cursor.getString(0));
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
        private static final String DICTIONARY_TABLE_CREATE =
                    "CREATE TABLE " + DICTIONARY_TABLE_NAME + " (" +
                    "_id" + " VARCHAR PRIMARY KEY, " +
                    "childA" + " VARCHAR, " +
                    "childB" + " VARCHAR, " +
                    "fieldA" + " BIT, " +
                    "fieldB" + " INTEGER, " +
                    "fieldC" + " DECIMAL, " +
                    "name" + " VARCHAR, " +
                    "date" + " VARCHAR);";

        TestOpenHelper(Context context, String file) {
            super(context, file, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(DICTIONARY_TABLE_CREATE);
			
        }

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			
		}
    }

}
