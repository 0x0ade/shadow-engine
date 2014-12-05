package net.fourbytes.shadow.systems;

import com.badlogic.gdx.utils.ObjectMap;
import net.fourbytes.shadow.network.Data;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Simple snapshot of the system's data.
 */
public class SystemData extends Data {

    public String systemName;
    public ObjectMap<String, Object> data = new ObjectMap<String, Object>();

    public SystemData() {
    }

    public SystemData(ISystem system) {
        this.systemName = system.getName();

        Class<? extends ISystem> clazz = system.getClass();
        for (Method method : clazz.getMethods()) {
            String name = method.getName();
            if (name.startsWith("get") && hasMethod0(clazz, "set"+name.substring(3), method.getReturnType())) {
                try {
                    data.put(name.substring(3), method.invoke(system));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void set(ISystem system) {
        Class<? extends ISystem> clazz = system.getClass();
        for (Method method : clazz.getMethods()) {
            String name = method.getName();
            if (name.startsWith("set") && hasMethod0(clazz, "get"+name.substring(3))) {
                try {
                    method.invoke(system, data.get(name.substring(3)));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    protected boolean hasMethod0(Class<?> clazz, String name, Class<?>... types) {
        try {
            clazz.getMethod(name, types);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

}