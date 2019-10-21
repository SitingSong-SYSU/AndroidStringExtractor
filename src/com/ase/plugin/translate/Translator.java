package com.ase.plugin.translate;


public class Translator {
    private static String appId = "TestAppId";
    private static String securityKey = "TestSecurityKey";

    private static TransApi api;

    public static synchronized TransApi getApi() {
        if (api == null) {
            api = new TransApi(appId, securityKey);
        }
        return api;
    }

    public static String toEnglish(String src) {
        String json = getApi().getTransResult(src, "zh", "en");
        TransResponse response = JsonUtils.fromJson(json, TransResponse.class);
        if (response.trans_result != null && response.trans_result.size() > 0) {
            return response.trans_result.get(0).dst;
        }
        return "";
    }

}
