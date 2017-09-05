package utils;

/**
 * @author j.chen@91kge.com  create on 2017/8/29
 */
public class StringUtils {

    public static String zipStr(String jsonString) {
        return (jsonString + "").replaceAll("</?[^>]+>", "").replaceAll("\\s+|\\t|\\n|\\r", "");
    }



}
