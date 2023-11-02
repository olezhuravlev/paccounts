package org.paccounts.config;


import java.util.HashMap;

public class CollectionsFactory {

    public HashMap<String, Object> getRestResponseCollection(String... initials) {

        HashMap<String, Object> result = new HashMap<>();
        for (String key : initials) {
            result.put(key, null);
        }

        return result;
    }
}
