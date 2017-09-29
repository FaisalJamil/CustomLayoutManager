/*
 * Copyright (c) 2017. Faisal Jamil
 */

package com.example.faisal.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Protocol;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

/*
This class is written to safely and quickly mock json file as server
and then we'll change to real url when server starts working
*/
public class MockClient implements Interceptor {

    public MockClient() {}

    @Override
    public Response intercept(Chain chain) throws IOException {

            HttpUrl url = chain.request().url();
            //here determine what to do with base url
            String path = url.encodedPath().substring(0, url.encodedPath().lastIndexOf("path") + 4);
            switch (path) {
                case "/some/path":
                    String response = getFile(Const.filename);
                    return getResponse(200, response, chain);

                case "/add/path":
                    RequestBody requestBody = chain.request().body();
                    if (requestBody instanceof FormBody) { // we need to use FormUrlEncoded for Post
                        FormBody body = (FormBody) requestBody;
                        Map<String, String> attribute = new HashMap<>();
                        for (int i = 0; i < body.size(); i++) {
                            attribute.put(cleanString(body.encodedName(i)), cleanString(body.encodedValue(i)));
                        }
                        Gson gson = new GsonBuilder().create();
                        JsonObject newItem = gson.toJsonTree(attribute).getAsJsonObject();
                        if (addItemToFile(Const.filename, newItem))
                            return getResponse(200, newItem.toString(), chain);
                    } else
                        return getResponse(405, "Method Not Allowed", chain);
                case "/delete/path":
                    List<String> segments = chain.request().url().pathSegments();
                    int position = Integer.valueOf(segments.get(2));
                    return getResponse(200, deleteItemFromFile(position, Const.filename), chain);
                case "/move/path":
                    RequestBody reqBody = chain.request().body();
                    FormBody body = (FormBody) reqBody;
                    Map<String, Integer> pos = new HashMap<>();
                    for (int i = 0; i < body.size(); i++) {
                        pos.put(cleanString(body.encodedName(i)), Integer.valueOf(cleanString(body.encodedValue(i))));
                    }
                    return getResponse(200, moveItemFromFile(pos, Const.filename), chain);
            }
        return null;
    }

    private Response getResponse (int code, String response, Chain chain){
        return new Response.Builder()
                .code(200)
                .message(response)
                .request(chain.request())
                .protocol(Protocol.HTTP_1_1)
                .body(ResponseBody.create(MediaType.parse("application/json"), response.getBytes()))
                .addHeader("content-type", "application/json")
                .build();
    }

    private String getFile(String fileName) {
        StringBuilder result = new StringBuilder("");
        final File file = new File(Const.path, fileName);
        try {
            Scanner scanner = new Scanner(file);
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                result.append(line).append("\n");
            }
            scanner.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return cleanString(result.toString());
    }

    private boolean addItemToFile (String fileName, JsonObject newItem){
        try {
            String json = getFile(fileName); // get current json from file
            JsonParser parser = new JsonParser();
            JsonObject root = parser.parse(json).getAsJsonObject();
            JsonArray items = root.getAsJsonArray("items");
            items.add(newItem); // append new item

            final File file = new File(Const.path, fileName);
            Writer output = new BufferedWriter(new FileWriter(file));
            output.write(root.toString());
            output.close();
            return true;
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }

    private String deleteItemFromFile(int position, String fileName) {
        try {
            String json = getFile(fileName); // get current json from file
            JsonParser parser = new JsonParser();
            JsonObject root = parser.parse(json).getAsJsonObject();
            JsonArray items = root.getAsJsonArray("items");
            JsonObject item = (JsonObject) items.get(position);
            items.remove(position);

            final File file = new File(Const.path, fileName);
            Writer output = new BufferedWriter(new FileWriter(file));
            output.write(root.toString());
            output.close();
            return item.toString();
        }catch (Exception e){
            e.printStackTrace();
        }
        return null; // if an error occurs, will be handled in presenter
    }

    private String moveItemFromFile(Map<String, Integer> pos, String fileName) {
        try {
            String json = getFile(fileName); // get current json from file
            JsonParser parser = new JsonParser();
            JsonObject root = parser.parse(json).getAsJsonObject();
            JsonArray items = root.getAsJsonArray("items");

            JsonElement temp = items.get(pos.get("to"));
            items.set(pos.get("to"), items.get(pos.get("from")));
            items.set(pos.get("from"), temp);

            final File file = new File(Const.path, fileName);
            Writer output = new BufferedWriter(new FileWriter(file));
            output.write(root.toString());
            output.close();
            return "1";
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }


    private String cleanString (String target){
        String result = null;
        try {
            result = URLDecoder.decode(target, "UTF-8"); // remove url decoding
            result = result.replaceAll("^\"|\"$", ""); //remove all double qoutes
            result = result.replaceAll("\\s+",""); // remove any spaces
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}