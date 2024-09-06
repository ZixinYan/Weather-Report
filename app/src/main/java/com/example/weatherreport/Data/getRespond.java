package com.example.weatherreport.Data;

public class getRespond {
/*百度api提供的代码示例，只用于参考
        public static String URL = "https://api.map.baidu.com/geocoding/v3?";

        public static String AK = "ctUhHi7R6jHCdfo1rTOUKCOM654YwHXz";

        public static void main(String[] args) throws Exception {

            SearchHttpAK snCal = new SearchHttpAK();

            Map params = new LinkedHashMap<String, String>();
            params.put("address", "北京市海淀区上地十街10号");
            params.put("output", "json");
            params.put("ak", AK);
            params.put("callback", "showLocation");


            snCal.requestGetAK(URL, params);
        }

        /**
         * 默认ak
         * 选择了ak，使用IP白名单校验：
         * 根据您选择的AK已为您生成调用代码
         * 检测到您当前的ak设置了IP白名单校验
         * 您的IP白名单中的IP非公网IP，请设置为公网IP，否则将请求失败
         * 请在IP地址为0.0.0.0/0 外网IP的计算发起请求，否则将请求失败
         */
        /*
        public void requestGetAK(String strUrl, Map<String, String> param) throws Exception {
            if (strUrl == null || strUrl.length() <= 0 || param == null || param.size() <= 0) {
                return;
            }

            StringBuffer queryString = new StringBuffer();
            queryString.append(strUrl);
            for (Map.Entry<?, ?> pair : param.entrySet()) {
                queryString.append(pair.getKey() + "=");
                //    第一种方式使用的 jdk 自带的转码方式  第二种方式使用的 spring 的转码方法 两种均可
                //    queryString.append(URLEncoder.encode((String) pair.getValue(), "UTF-8").replace("+", "%20") + "&");
                queryString.append(UriUtils.encode((String) pair.getValue(), "UTF-8") + "&");
            }

            if (queryString.length() > 0) {
                queryString.deleteCharAt(queryString.length() - 1);
            }

            java.net.URL url = new URL(queryString.toString());
            System.out.println(queryString.toString());
            URLConnection httpConnection = (HttpURLConnection) url.openConnection();
            httpConnection.connect();

            InputStreamReader isr = new InputStreamReader(httpConnection.getInputStream());
            BufferedReader reader = new BufferedReader(isr);
            StringBuffer buffer = new StringBuffer();
            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line);
            }
            reader.close();
            isr.close();
            System.out.println("AK: " + buffer.toString());
        }
    }
    */
}
