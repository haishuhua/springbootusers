package com.techprimers.db.services;

import com.techprimers.db.constants.GlobalProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import twitter4j.*;
import twitter4j.conf.ConfigurationBuilder;

@Component
public class TwitterService {

    private static String key = GlobalProperties.getInstance().getProperty("twitter.key");
    private static String secret = GlobalProperties.getInstance().getProperty("twitter.secret");
    private static String token = GlobalProperties.getInstance().getProperty("twitter.token");
    private static String secretToken = GlobalProperties.getInstance().getProperty("twitter.secrettoken");

    public TwitterService() {

    }

    public static TwitterFactory getTwitterFactory() {

        ConfigurationBuilder cb = new ConfigurationBuilder();
        cb.setDebugEnabled(true);
        cb.setOAuthConsumerKey(key);
        cb.setOAuthConsumerSecret(secret);
        cb.setOAuthAccessToken(token);
        cb.setOAuthAccessTokenSecret(secretToken);
        System.out.println("here" );
        TwitterFactory tf = new TwitterFactory(cb.build());
        return tf;
    }

}
