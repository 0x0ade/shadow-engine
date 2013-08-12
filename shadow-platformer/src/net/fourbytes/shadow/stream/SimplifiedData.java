package net.fourbytes.shadow.stream;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ObjectMap;

import net.fourbytes.shadow.Entity;
import net.fourbytes.shadow.Block;
import net.fourbytes.shadow.GameObject;
import net.fourbytes.shadow.Layer;
import net.fourbytes.shadow.Level;
import net.fourbytes.shadow.TypeBlock;

/**
 * SimplifiedData stores data of f.e. {@link GameObject}s in a simplified manner by 
 * converting layers to integers, leaving out useless stuff and others.
 */
public class SimplifiedData extends Data {
	
	public String clazz;
	public ObjectMap<String, Object> fields = new ObjectMap<String, Object>();
	
	public SimplifiedData() {
		super();
	}
	
	/**
	 * Converts an Object to it's correspondent {@link SimplifiedData}. <br>
	 * This is the general method linking to the subclasses maintaining the given type.
	 * @param o Object to convert
	 * @return {@link Data} to send thru streams.
	 */
	public final static SimplifiedData autopack(Object o) {
		SimplifiedData sd = new SimplifiedData();
		sd.pack(o);
		
		return sd;
	}
	
	/**
	 * Converts an Object to it's correspondent {@link SimplifiedData}.
	 * @param o Object to convert
	 * @return {@link Data} to send thru streams.
	 */
	public final void pack(Object o){
		Class clazz = o.getClass();
		this.clazz = clazz.getName();
		
		Field[] fields = clazz.getDeclaredFields();
		
		for (int i = 0; i < fields.length; i++) {
			Field f = fields[i];
			int flags = f.getModifiers();
			if (Modifier.isStatic(flags) || Modifier.isFinal(flags) || !Modifier.isPublic(flags)) {
				continue;
			}
			String name = f.getName();
			Class type = f.getDeclaringClass();
			Object value = null;
			
			try {
				value = f.get(o);
			} catch (Exception e) {
				e.printStackTrace();
				continue;
			}
			
			if (value instanceof Layer) {
				//TODO Get layer number
			}
			
			if (value instanceof Level) {
				value = null;
			}
			
			if (value instanceof GameObject) {
				value = null;
			}
			
			this.fields.put(name, value);
		}
	}
	
	/**
	 * Converts this {@link SimplifiedData} back to it's correspondent Object.
	 * @return Object} packed in this {@link SimplifiedData}.
	 */
	public Object unpack() {
		//TODO Stub
		return null;
	}
	
}
