package com.example.acer.newsapp;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.net.Uri;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;


public class AsyncTaskloader extends AsyncTaskLoader<ArrayList<articleinfo>> {
    private static ArrayList<Uri> urls = new ArrayList<>();
    private ArrayList<articleinfo> articles = new ArrayList<>();
    private Uri USGS_REQUEST_URL;

    public AsyncTaskloader(Context context, Uri USGS_REQUEST_URL) {
        super(context);
        this.USGS_REQUEST_URL = USGS_REQUEST_URL;

    }

    public static Uri getarticle(int position) {
        return urls.get(position);
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
        super.onStartLoading();
    }

    @Override
    public ArrayList<articleinfo> loadInBackground() {

        URL url =createUrl(USGS_REQUEST_URL.toString());

        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = "";
        try {
            if (url != null && url.toString().length() > 1)
                jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            // TODO Handle the IOException

        }
        if (jsonResponse == null || jsonResponse.isEmpty())
            return null;
        // Extract relevant fields from the JSON response and create an {@link Event} object
        articles = extractFeatureFromJson(jsonResponse);

        return articles;
    }
    private URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException exception) {
            Log.e("error in url", "Error with creating URL", exception);

            return null;
        }
        return url;
    }

    private String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";
        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.connect();
            inputStream = urlConnection.getInputStream();
            jsonResponse = readFromStream(inputStream);


        } catch (IOException e) {
            // TODO: Handle the exception

        } finally {
            if (urlConnection != null) {
                if (urlConnection.getResponseCode() != 200) {
                    jsonResponse = "";


                } else
                    urlConnection.disconnect();
            }
            if (inputStream != null) {
                // function must handle java.io.IOException here
                inputStream.close();
            }

        }
        return jsonResponse;
    }

    private ArrayList<articleinfo> extractFeatureFromJson(String SAMPLE_JSON_RESPONSE) {


        try {

            JSONObject jsonRootObject = new JSONObject(SAMPLE_JSON_RESPONSE);

            JSONObject JSONObject = jsonRootObject.optJSONObject("response");
            JSONArray jsonArray = JSONObject.optJSONArray("results");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject JSONObjects = jsonArray.getJSONObject(i);

                JSONObject fields = JSONObjects.getJSONObject("fields");


                String author = (fields.optString("byline"));
                String Section = (JSONObjects.optString("sectionName"));
                String title = JSONObjects.optString("webTitle");
                String articleDate = JSONObjects.optString("webPublicationDate");

                urls.add(Uri.parse(JSONObjects.optString("webUrl")));//article url

                String justdate = articleDate.substring(0, articleDate.indexOf("T"));

                articles.add(new articleinfo(title, justdate, Section,author));
            }

            return articles;

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

}