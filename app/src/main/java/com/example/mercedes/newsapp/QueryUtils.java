package com.example.mercedes.newsapp;

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
import java.util.List;

public final class QueryUtils {
    /**
     * Tag for the log messages
     */
    private static final String LOG_TAG = QueryUtils.class.getSimpleName();
    private static final String LOG_QUERY_UTILS = "QueryUtils";

    /**
     * Json key names
     */
    private static final String RESPONSE = "response";
    private static final String RESULTS = "results";
    private static final String TITLE = "webTitle";
    private static final String SECTION = "sectionName";
    private static final String DATE = "webPublicationDate";
    private static final String URL = "webUrl";
    private static final String TAGS = "tags";

    // OK connection code
    private static final int OK_CODE = 200;
    // Read timeout
    private static final int READ_TIME_OUT = 10000;
    //Connect Timeout
    private static final int CONNECT_TIME_OUT = 15000;
    //Request method
    private static final String REQUEST_METHOD = "GET";

    /**
     * Create a private constructor because no one should ever create a {@link QueryUtils} object.
     * This class is only meant to hold static variables and methods, which can be accessed
     * directly from the class name QueryUtils (and an object instance of QueryUtils is not needed).
     */
    private QueryUtils() {
    }

    /**
     * Query The Guardian API dataset and return a list of {@link Article} objects.
     */
    public static List<Article> fetchNewsData(String requestUrl) {
        // Create URL object
        URL url = createUrl(requestUrl);

        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem making the HTTP request.", e);
        }

        // Extract relevant fields from the JSON response, create a list of {@link Article}s and return it
        return extractNewsFromJson(jsonResponse);
    }

    /**
     * Returns new URL object from the given string URL.
     */
    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Error with creating URL", e);
        }
        return url;
    }

    /**
     * Make an HTTP request to the given URL and return a String as the response.
     */
    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        // If the URL is null, then return early.
        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(READ_TIME_OUT /* milliseconds */);
            urlConnection.setConnectTimeout(CONNECT_TIME_OUT /* milliseconds */);
            urlConnection.setRequestMethod(REQUEST_METHOD);
            urlConnection.connect();

            // If the request was successful (response code 200),
            // then read the input stream and parse the response.
            if (urlConnection.getResponseCode() == OK_CODE) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the book JSON results.", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                // Closing the input stream could throw an IOException, which is why
                // the makeHttpRequest(URL url) method signature specifies than an IOException
                // could be thrown.
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    /**
     * Convert the {@link InputStream} into a String which contains the
     * whole JSON response from the server.
     */
    private static String readFromStream(InputStream inputStream) throws IOException {
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

    /**
     * Return a list of {@link Article} objects that has been built up from
     * parsing a JSON response.
     */
    public static ArrayList<Article> extractNewsFromJson(String newsJSON) {

        // Create an empty ArrayList that we can start adding articles to
        ArrayList<Article> news = new ArrayList<>();

        // Try to parse the SAMPLE_JSON_RESPONSE. If there's a problem with the way the JSON
        // is formatted, a JSONException exception object will be thrown.
        // Catch the exception so the app doesn't crash, and print the error message to the logs.
        try {

            // Create a JSONObject from the SAMPLE_JSON_RESPONSE string
            JSONObject baseJsonResponse = new JSONObject(newsJSON);

            // Create a JSONObject associated with the key called "response",
            JSONObject responseJson = baseJsonResponse.getJSONObject(RESPONSE);

            // Extract the JSONArray associated with the key called "results",
            // which represents a list of articles.
            if (responseJson.has(RESULTS)) {
                JSONArray resultsArray = responseJson.getJSONArray(RESULTS);

                // For each article in the resultsArray, create an {@link Article} object
                for (int i = 0; i < resultsArray.length(); i++) {

                    // Get a single article at position i within the list of articles
                    JSONObject currentArticle = resultsArray.getJSONObject(i);

                    // Get a the Json Object with the key "webTitle" and store it in String variable title
                    String title = currentArticle.getString(TITLE);
                    // Get a the Json Object with the key "sectionName" and store it in String variable section
                    String section = currentArticle.getString(SECTION);
                    // Get a the Json Object with the key "webPublicationDate" and store it in String variable date
                    String date = currentArticle.getString(DATE);

                    // Get a the Json Object with the key "webUrl" and store it in String variable url
                    String url = currentArticle.getString(URL);

                    String contributor = "";

                    if (currentArticle.has(TAGS)) {

                        // Extract the JSONArray associated with the key called "tags",
                        JSONArray tagsArray = currentArticle.getJSONArray(TAGS);

                        if (tagsArray.length() > 0) {
                            // Get a single tag at position 0
                            JSONObject currentTag = tagsArray.getJSONObject(0);
                            // Get a the Json Object with the key "webTitle" and store it in String variable contributor
                            contributor = currentTag.getString(TITLE);
                        } else {
                            contributor = "Author N/A";
                        }
                    } else {
                        contributor = "Author N/A";
                    }

                    // Create a new {@link Article} object with the title, contributor, section, date,
                    // and url from the JSON response.
                    Article article = new Article(title, contributor, section, date, url);

                    // Add the new {@link Article} to the news list.
                    news.add(article);
                }
            }
        } catch (JSONException e) {
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.
            Log.e(LOG_QUERY_UTILS, "Problem parsing the earthquake JSON results", e);
        }

        // Return the news list
        return news;
    }
}
