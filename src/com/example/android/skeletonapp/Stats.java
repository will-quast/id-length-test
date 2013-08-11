package com.example.android.skeletonapp;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.afree.chart.AFreeChart;
import org.afree.chart.ChartFactory;
import org.afree.chart.plot.CategoryPlot;
import org.afree.chart.plot.PlotOrientation;
import org.afree.data.category.DefaultCategoryDataset;
import org.apache.commons.math3.stat.descriptive.SummaryStatistics;

import android.content.Context;
import android.view.ViewGroup;

public class Stats {
	
	private String title;
	private ChartView view;
	private AFreeChart chart;
	private DefaultCategoryDataset chartModel;
	
	private Map<String, Map<String, Long>> clocks;
	private Map<String, Map<String, SummaryStatistics>> stats;
	
	public Stats(Context context, String title, ViewGroup container) {
		this.title = title;
		
		view = new ChartView(context);
		chartModel = new DefaultCategoryDataset();
        chart = ChartFactory.createBarChart(title, "test", "duration", chartModel, PlotOrientation.VERTICAL, true, false, false);
        CategoryPlot plot = chart.getCategoryPlot();
        plot.getRangeAxis().setLowerBound(0.0);
        view.setChart(chart);
        container.addView(view);
        reset();
	}
	
	public void start(String domain, String range) {
		long now = System.currentTimeMillis();
		Map<String, Long> domainStart = clocks.get(domain);
		if (domainStart == null) {
			domainStart = new HashMap<String, Long>();
			clocks.put(domain, domainStart);
		}
		domainStart.put(range, now);
	}
	
	public void end(String domain, String range) {
		long now = System.currentTimeMillis();
		Map<String, Long> domainStart = clocks.get(domain);
		if (domainStart == null) {
			domainStart = new LinkedHashMap<String, Long>();
			clocks.put(domain, domainStart);
		}
		
		long dur = now - domainStart.get(range);
		
		Map<String, SummaryStatistics> domainSummary = stats.get(domain);
		if (domainSummary == null) {
			domainSummary = new LinkedHashMap<String, SummaryStatistics>();
			stats.put(domain, domainSummary);
		}
		
		SummaryStatistics summary = domainSummary.get(range);
		if (summary == null) {
			summary = new SummaryStatistics();
			domainSummary.put(range, summary);
		}
		summary.addValue(dur);
	}
	
	public void update() {
		
		for(Entry<String, Map<String, SummaryStatistics>> domainEntry : stats.entrySet()) {
			String domainText = domainEntry.getKey();
			Map<String, SummaryStatistics> domainStats = domainEntry.getValue();
			for(Entry<String, SummaryStatistics> range : domainStats.entrySet()) {
				String rangeText = range.getKey();
				SummaryStatistics summary = range.getValue();
				chartModel.setValue(summary.getMean(), rangeText, domainText);
			}
		}
		
		view.restoreAutoBounds();
		view.repaint();
	}
	
	public void reset() {
		clocks = new HashMap<String, Map<String, Long>>();
		stats = new LinkedHashMap<String, Map<String, SummaryStatistics>>();
		chartModel.clear();
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("====="+title+ "=====\n");
		
		for(Entry<String, Map<String, SummaryStatistics>> domainEntry : stats.entrySet()) {
			String domainText = domainEntry.getKey();
			Map<String, SummaryStatistics> domainStats = domainEntry.getValue();
			for(Entry<String, SummaryStatistics> range : domainStats.entrySet()) {
				sb.append("@=="+domainText+"==" +range.getKey() + ": "+range.getValue().toString());
			}
		}
		
		return sb.toString();
	}
}
