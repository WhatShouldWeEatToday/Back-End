package kit.project.whatshouldweeattoday.domain.util;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JsonUtil {

    public static List<Map<String, Object>> getListMapFromJsonArray(JSONArray jsonArray) {
        List<Map<String, Object>> list = new ArrayList<>();

        for (Object obj : jsonArray) {
            if (obj instanceof JSONObject) {
                JSONObject jsonObj = (JSONObject) obj;
                Map<String, Object> map = new HashMap<>();

                for (Object key : jsonObj.keySet()) {
                    String keyStr = (String) key;
                    Object value = jsonObj.get(keyStr);
                    map.put(keyStr, value);
                }
                list.add(map);
            }
        }
        return list;
    }
}