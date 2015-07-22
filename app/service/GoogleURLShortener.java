package service;


import play.Play;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.googleapis.auth.oauth2.*;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.urlshortener.Urlshortener;
import com.google.api.services.urlshortener.UrlshortenerScopes;
import com.google.api.services.urlshortener.model.Url;
import com.google.api.services.urlshortener.model.UrlHistory;

import java.util.Arrays;

import play.Application;
import play.Logger;


/**
 * authored by nitin jain 7/22/15
 */
public class GoogleURLShortener {

    private static final long serialVersionUID = 1L;

    /** Email of the Service Account */
    private static final String SERVICE_ACCOUNT_EMAIL = Play.application().configuration().getString("google.urlshortener.api.account.email");

    /** Path to the Service Account's Private Key file */
    private static final String SERVICE_ACCOUNT_PKCS12_FILE_PATH = Play.application().configuration().getString("google.urlshortener.api.account.pkcs12.filepath");

    public static Urlshortener urlshortener;

    protected static Urlshortener getUrlshortener() throws Exception{
            if (urlshortener == null){
                Logger.debug("Instantiating Google shortener");

                HttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
                JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();

                GoogleCredential credential = new GoogleCredential.Builder()
                        .setTransport(httpTransport)
                        .setJsonFactory(jsonFactory)
                        .setServiceAccountId(SERVICE_ACCOUNT_EMAIL)
                        .setServiceAccountScopes(Arrays.asList(UrlshortenerScopes.URLSHORTENER))
                        .setServiceAccountPrivateKeyFromP12File(
                                new java.io.File(SERVICE_ACCOUNT_PKCS12_FILE_PATH))
                        .build();
                 urlshortener = new Urlshortener.Builder(httpTransport, jsonFactory, null)
                        .setHttpRequestInitializer(credential)
                         .setApplicationName("sproutup")
                         .build();

            }
            return urlshortener;
    }


    public static String shortenURL(String longUrl)  {
        GoogleURLShortener gs = new GoogleURLShortener();
        String shortUrlAsString = null;
        Url toInsert = new Url().setLongUrl(longUrl);
        try {
            Url shortURL = getUrlshortener().url().insert(toInsert).execute();
            shortUrlAsString = shortURL.getId();
            Logger.debug(longUrl + " [URL] shortened to:" + shortUrlAsString);

        } catch (Exception e) {
            Logger.debug("encountered problem shortening URL using Google API");
            e.printStackTrace();
        }

        return shortUrlAsString;
    }

        public static void main(String[] args) throws Exception {


            String longUrl = "www.yahoo.com";
            shortenURL(longUrl);
        }


}
