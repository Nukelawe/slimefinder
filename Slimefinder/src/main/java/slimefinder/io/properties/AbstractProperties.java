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

    /**
     * @return enumeration for this properties-object determined by defaultValues insert order.
     */
    @Override
    public Enumeration keys() {
        Enumeration keysEnum = super.keys();
        Vector<String> keyList = new Vector<>();
        while(keysEnum.hasMoreElements()){
            keyList.add((String)keysEnum.nextElement());
        }
        Collections.sort(keyList, (s1, s2) -> {
            Object defaultKeyOrder[] = defaultValues.keySet().toArray();
            for (int i = 0; i < defaultKeyOrder.length; i++) {
                if (defaultKeyOrder[i].equals(s1)) return -1;
                if (defaultKeyOrder[i].equals(s2)) return 1;
            }
            return 0;
        });
        return keyList.elements();
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
