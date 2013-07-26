package net.fourbytes.shadow;

import java.util.HashMap;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ArrayMap;
import com.badlogic.gdx.utils.ObjectMap;

public final class TimeChecker {
	private TimeChecker() {
	}
	
	public static class Time {
		public Array<Long> millis = new Array<Long>();
		public Array<String> names = new Array<String>();
		public int size = 0;
		public Time() {
		}
	}
	
	public static ObjectMap<Object, Time> map = new ObjectMap<Object, Time>();
	
	public static Time begin(Object key) {
		Time time = new Time();
		time.millis.add(System.currentTimeMillis());
		time.names.add("stub");
		time.size++;
		map.put(key, time);
		return time;
	}
	
	public static Time point(Object key, String name) {
		Time time = map.get(key);
		time.millis.add(System.currentTimeMillis());
		time.names.add(name);
		time.size++;
		return time;
	}
	
	public static Time point(Object key) {
		return point(key, map.get(key).size+"");
	}
	
	public static Time end(Object key, String name) {
		Time time = map.get(key);
		time.millis.add(System.currentTimeMillis());
		time.names.add(name);
		time.size++;
		
		System.out.println("Times for \""+key+"\": ");
		long ll = 0;
		for (int i = 0; i < time.size; i++) {
			long l = time.millis.get(i);
			if (i != 0) {
				String nname = time.names.get(i);
				System.out.println(nname+": "+(l-ll));
			}
			ll = l;
		}
		System.out.println("Summary (last - first): "+(time.millis.get(time.size-1)-time.millis.get(0)));
		
		map.remove(key);
		return time;
	}
	
	public static Time end(Object key) {
		return end(key, map.get(key).size+"");
	}
	
}
