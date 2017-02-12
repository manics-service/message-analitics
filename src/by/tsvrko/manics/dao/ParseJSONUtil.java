package by.tsvrko.manics.dao;

import by.tsvrko.manics.dao.implementations.dataimport.ChatImportVKImpl;
import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created main.by tsvrko on 1/6/2017.
 */
public final class ParseJSONUtil {

    private static Logger log = Logger.getLogger(ChatImportVKImpl.class.getName());

    private static JSONParser parser = new JSONParser();


    private static JSONObject parseText(String text){
        JSONObject jsonResp = null;
        try {
            jsonResp =(JSONObject)parser.parse(text);
        } catch (ParseException e) {
            log.debug("json can't be parsed",e);
        }
        return jsonResp; }


    public static JSONArray parseChatsJSON(String text) {
        JSONArray jsonArray;
        jsonArray = (JSONArray) parseText(text).get("response");
        jsonArray.remove(0);
        return jsonArray;
    }

    public static JSONArray parseUserJSON(String text) {

        return (JSONArray) parseText(text).get("response");
    }

    public static JSONArray parseMessageJSON(String text) {
        JSONObject jsonResp2 = (JSONObject) parseText(text).get("response");
        return (JSONArray) jsonResp2.get("items");
    }

    public static JSONArray parseUserCountJSON(String text) {
        JSONObject jsonResp2 = (JSONObject) parseText(text).get("response");
        return (JSONArray) jsonResp2.get("users");

    }


    public static String parseJSONArrayCount(String text) {
        JSONArray jsonArray = (JSONArray)parseText(text).get("response");
        return (jsonArray.get(0)).toString();

    }

    public static String parseMessageCount(String text) {
        String messageCount;
        JSONObject jsonResp2 = (JSONObject)parseText(text).get("response");
        try{
            messageCount = (jsonResp2.get("count")).toString();}
        catch (NullPointerException e){
            log.debug("chat contains no messages", e);
            messageCount = "0";
        }
        return messageCount;

    }

    public static List <String> parseChatInfo(String text) {
        List<String> chatInfo = new ArrayList<>();
        String messageCount;
        String lastMessageDate;
        JSONObject jsonResp2 = (JSONObject)parseText(text).get("response");
        try{
            messageCount = (jsonResp2.get("count")).toString();
            JSONArray itemsArray =  (JSONArray) jsonResp2.get("items");
            JSONObject lastMessage = (JSONObject)itemsArray.get(0);
            lastMessageDate = (lastMessage.get("date")).toString();
            chatInfo.add(messageCount);
            chatInfo.add(lastMessageDate);
        }
        catch (NullPointerException e){
            log.debug("chat contains no messages", e);
            messageCount = "0";
            lastMessageDate = "0";
        }
        return chatInfo;
    }


}
