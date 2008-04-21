package org.garbagecollected.util;

import static org.garbagecollected.util.DollarMaps.$$;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.garbagecollected.util.DollarMaps.$$;

import com.googlecode.gchartjava.AxisInfo;
import com.googlecode.gchartjava.Color;
import com.googlecode.gchartjava.Data;
import com.googlecode.gchartjava.Line;
import com.googlecode.gchartjava.LineChart;

/** Comparing performance to regular maps. */
@SuppressWarnings("unused")
public class Benchmark {
  private static long proxy(int count) {
    long start = System.currentTimeMillis();
    int orig = count;
    $$<String> dollar = getMap(count);
    while (count-->0) {
      for (String[] s : dollar.asEasy()) {
        if (true) continue;
      }
    }
    
    long elapsed = System.currentTimeMillis() - start;
    System.out.printf("%10s%,13d%8d%1s%n", "Easy", orig, 
                      elapsed, "ms");
    return elapsed;
  }

  static $$<String> getMap(int count) {
    $$<String> dollar = $$("sdf","adfj");
    while (count-->0) {
      dollar.$(""+count, ""+count);
    }
    return dollar;
  }
  
  private static long noProxy(int count) {
    long start = System.currentTimeMillis();
    int orig = count;
    $$<String> dollar = getMap(count);
    while (count-->0) {
      for (Map.Entry<String, String> e : dollar.asHashMap().entrySet()) {
        if (true) continue;
      }
    }
    
    long elapsed = System.currentTimeMillis() - start;
    System.out.printf("%10s%,13d%8d%1s%n", "EntrySet", orig, 
                      elapsed, "ms");
    return elapsed;
  }
  
  public static void main(String[] args) {
    List<Integer> multipliers = Arrays.asList(1, 2, 3, 4, 5);
    float[] proxyX = new float[multipliers.size()];
    float[] noProxyX = new float[multipliers.size()];
    float totalProxy = 0F;
    float totalNoProxy = 0F;
    int counter = 0;
    
    for (int n : multipliers) {
      int number = n * 1000;
      proxyX[counter] = (float)proxy(number);
      totalProxy+=proxyX[counter];
      noProxyX[counter++] = (float)noProxy(number);
      totalNoProxy+= noProxyX[counter-1];
    }
    totalNoProxy = 5000;
    totalProxy = 5000;
    Data proxyXData = new Data(toPercentages(proxyX, totalProxy));
    Data noProxyXData = new Data(toPercentages(noProxyX, totalNoProxy));
    
    Line proxyLine = new Line(proxyXData);
    proxyLine.setLegend("Easy Map");
    proxyLine.setColor(Color.RED);
    
    Line noProxyLine = new Line(noProxyXData);
    noProxyLine.setLegend("EntrySet Map");
    noProxyLine.setColor(Color.BLUE);
    
    LineChart chart = new LineChart(proxyLine, noProxyLine);
    
    chart.addYAxisInfo(new AxisInfo("","1 sec","2 sec","3 sec", "4 sec", "5 sec"));
    
    List<String> xLabels = new LinkedList<String>();
    for(Integer i : multipliers) {
      xLabels.add(String.format(Locale.US, "%,d", i));
    }
    chart.addXAxisInfo(new AxisInfo(xLabels));
    chart.addXAxisInfo(new AxisInfo("","","","","Objects Created"));
    
    chart.setTitle("DollarMaps Easy Iteration Benchmark on JDK 6");
    chart.setSize(400, 300);
    System.out.println(chart.createURLString());
    
  }
  
  static float[] toPercentages(float[] values, float total) {
    for (int i = 0; i < values.length; i++) {
      values[i] = (int)(values[i]/(total/100));
    }
    return values;
  }
}
