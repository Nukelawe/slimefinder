package slimefinder.io.properties;

import java.util.*;

import slimefinder.util.Position;

public abstract class AbstractProperties extends Properties {

    protected LinkedHashMap<String, Object> defaultValues;
    protected Map<String, Object> values;
    public final String filename;

    public AbstractProperties(String filename) {
        this.filename = filename + ".properties";
        defaultValues = new LinkedHashMap<>();
        values = new HashMap<>();
        setDefaults();
    }

    /*
     * Entry set of the properties specified by the default key insertion order.
     */
    @Override
    public Set<Map.Entry<Object,Object>> entrySet() {
        TreeSet<Map.Entry<Object,Object>> orderedSet = new TreeSet<>((o1, o2) -> {
            Object defaultKeyOrder[] = defaultValues.keySet().toArray();
            for (int i = 0; i < defaultKeyOrder.length; i++) {
                if (defaultKeyOrder[i].equals(o1.getKey())) return -1;
                if (defaultKeyOrder[i].equals(o2.getKey())) return 1;
            }
            return 0;
        });
        orderedSet.addAll(super.entrySet());
        return orderedSet;
    }

    /**
     * To manually set the properties for testing
     */
    public void setProperty(String key, Object value) {
        values.put(key, value);
    }

    protected abstract void setDefaults();

    public Integer getInt(String property) {
        return (Integer) values.get(property);
    }

    public Long getLong(String property) {
        return (Long) values.get(property);
    }

    public Boolean getBoolean(String property) {
        return (Boolean) values.get(property);
    }

    public String getString(String property) {
        return (String) values.get(property);
    }

    public Position getPosition(String property) {
        return (Position) values.get(property);
    }
}
