package io.github.echoesofnebula.managers;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonWriter.OutputType;
import com.badlogic.gdx.utils.ObjectMap;

public abstract class BaseRegistry<T> {

    private final String filePath;
    private final Class<T> typeClass;
    private ObjectMap<String, T> dataCache;

    protected BaseRegistry(String filePath, Class<T> typeClass) {
        this.filePath = filePath;
        this.typeClass = typeClass;
    }

    @SuppressWarnings("unchecked")
    public void initialize() {
        Json json = new Json();
        json.setOutputType(OutputType.json);
        json.setUsePrototypes(false);
        json.setIgnoreUnknownFields(true);
        json.setTypeName(null);

        FileHandle file = Gdx.files.internal(filePath);

        if (file.exists()) {
            try {
                // Reads any JSON structure straight into our structured map cache!
                dataCache = json.fromJson(ObjectMap.class, typeClass, file.readString());
                System.out.println("Successfully loaded " + dataCache.size + " entries from: " + filePath);
            } catch (Exception e) {
                System.err.println("CRITICAL: Failed to parse registry file [" + filePath + "]: " + e.getMessage());
                dataCache = new ObjectMap<>();
            }
        } else {
            System.err.println("CRITICAL: Registry file not found: " + filePath);
            dataCache = new ObjectMap<>();
        }
    }

    public T get(String key) {
        if (dataCache == null) {
            throw new IllegalStateException("Registry [" + filePath + "] must be initialized before use!");
        }
        return dataCache.get(key);
    }
}
