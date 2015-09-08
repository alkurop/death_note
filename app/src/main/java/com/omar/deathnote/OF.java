package com.omar.deathnote;

/**
 * Created by omar on 9/8/15.
 */


import java.lang.reflect.Constructor;
import java.util.HashMap;


public class OF {
    private static final HashMap<String, Object> _objects = new HashMap();
    private static final Object _syncObj = new Object();

    public OF() {
    }

    public static <T> T GetAndRegisterIfMissingInstance(Class<T> clazz) {
        Object instance = GetInstance(clazz, new Object[0]);
        if(!ContainsInstance(clazz)) {
            Object var2 = _syncObj;
            synchronized(_syncObj) {
                if(!ContainsInstance(clazz)) {
                    RegisterInstance(instance);
                }
            }
        }

        return (T)instance;
    }

    public static <T> boolean ContainsInstance(Class<T> clazz) {
        return _objects.containsKey(clazz.getName());
    }

    public static <T> T GetInstance(Class<T> clazz, Object... paramsForConstructor) {
        if(!ContainsInstance(clazz)) {
            try {
                int x = paramsForConstructor.length;
                if(clazz.getConstructors().length == 1) {
                    if(x == 0) {
                        return clazz.newInstance();
                    } else {
                        Constructor c = clazz.getConstructors()[0];
                        return CallParameterizedConstructor(clazz, c, paramsForConstructor);
                    }
                } else {
                    throw new Exception("Use GetInstanceWithNullSafeParameterizedConstructor to specify parameter types");
                }
            } catch (Exception var4) {
                return null;
            }
        } else {
            return (T)_objects.get(clazz.getName());
        }
    }

    public static <T> T GetInstanceWithNullSafeParameterizedConstructor(Class<T> clazz, Class[] parameterTypes, Object... paramsForConstructor) {
        if(!ContainsInstance(clazz)) {
            try {
                Constructor x = clazz.getConstructor(parameterTypes);
                return CallParameterizedConstructor(clazz, x, paramsForConstructor);
            } catch (Exception var4) {
                return null;
            }
        } else {
            return(T) _objects.get(clazz.getName());
        }
    }

    public static <T> T RegisterInstance(T instance) {
        String className = instance.getClass().getName();
        if(className.contains("$")) {
            className = className.substring(0, className.indexOf("$"));
        }

        _objects.put(className, instance);
        return instance;
    }

    public static <T> boolean UnregisterInstance(Class<T> clazz) {
        return _objects.remove(clazz.getName()) != null;
    }

    public static void Clear() {
        _objects.clear();
    }

    private static <T> T CallParameterizedConstructor(Class<T> clazz, Constructor c, Object... paramsForConstructor) throws Exception {
        try {
            switch(paramsForConstructor.length) {
                case 0:
                    return (T)c.newInstance(new Object[0]);
                case 1:
                    return(T) c.newInstance(new Object[]{paramsForConstructor[0]});
                case 2:
                    return (T)c.newInstance(new Object[]{paramsForConstructor[0], paramsForConstructor[1]});
                case 3:
                    return (T)c.newInstance(new Object[]{paramsForConstructor[0], paramsForConstructor[1], paramsForConstructor[2]});
                case 4:
                    return (T)c.newInstance(new Object[]{paramsForConstructor[0], paramsForConstructor[1], paramsForConstructor[2], paramsForConstructor[3]});
                case 5:
                    return(T) c.newInstance(new Object[]{paramsForConstructor[0], paramsForConstructor[1], paramsForConstructor[2], paramsForConstructor[3], paramsForConstructor[4]});
                case 6:
                    return (T)c.newInstance(new Object[]{paramsForConstructor[0], paramsForConstructor[1], paramsForConstructor[2], paramsForConstructor[3], paramsForConstructor[4], paramsForConstructor[5]});
                case 7:
                    return (T)c.newInstance(new Object[]{paramsForConstructor[0], paramsForConstructor[1], paramsForConstructor[2], paramsForConstructor[3], paramsForConstructor[4], paramsForConstructor[5], paramsForConstructor[6]});
                case 8:
                    return(T) c.newInstance(new Object[]{paramsForConstructor[0], paramsForConstructor[1], paramsForConstructor[2], paramsForConstructor[3], paramsForConstructor[4], paramsForConstructor[5], paramsForConstructor[6], paramsForConstructor[7]});
                case 9:
                    return(T) c.newInstance(new Object[]{paramsForConstructor[0], paramsForConstructor[1], paramsForConstructor[2], paramsForConstructor[3], paramsForConstructor[4], paramsForConstructor[5], paramsForConstructor[6], paramsForConstructor[7], paramsForConstructor[8]});
                case 10:
                    return(T) c.newInstance(new Object[]{paramsForConstructor[0], paramsForConstructor[1], paramsForConstructor[2], paramsForConstructor[3], paramsForConstructor[4], paramsForConstructor[5], paramsForConstructor[6], paramsForConstructor[7], paramsForConstructor[8], paramsForConstructor[9]});
                default:
                    throw new Exception("Calling constructor with " + paramsForConstructor.length + " parameters is not supported");
            }
        } catch (Exception var4) {
            throw var4;
        }
    }
}

