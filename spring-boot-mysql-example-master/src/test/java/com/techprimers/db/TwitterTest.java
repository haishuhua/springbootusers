package com.techprimers.db;

import com.techprimers.db.services.TwitterService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import twitter4j.*;

public class TwitterTest {

    @Autowired
    TwitterService t;

    @Test
    public void trendsTest() throws TwitterException {


        TwitterFactory tf = TwitterService.getTwitterFactory();

        Twitter twitter = tf.getInstance();
        Trends trends = twitter.getPlaceTrends(1);


        int count = 0;
        for (Trend trend : trends.getTrends()) {
            if (count < 10) {
                System.out.println(trend.getName());
                count++;
            }
        }

        Query query = new Query("weather");
        QueryResult result = twitter.search(query);
        for (Status status : result.getTweets()) {
            System.out.println("@" + status.getUser().getScreenName() + ":" + status.getText());
        }


    }


}
