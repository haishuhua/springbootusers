package com.techprimers.db.constants;


import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.Set;

@Component
@PropertySource("classpath:global.properties")
@ConfigurationProperties
public class GlobalProperties {
    private final Properties configProp = new Properties();

    private GlobalProperties()
    {
        //Private constructor to restrict new instances
        InputStream in = this.getClass().getClassLoader().getResourceAsStream("global.properties");
        System.out.println("Read all properties from file");
        try {
            configProp.load(in);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //Bill Pugh Solution for singleton pattern
    private static class LazyHolder
    {
        private static final GlobalProperties INSTANCE = new GlobalProperties();
    }

    public static GlobalProperties getInstance()
    {
        return LazyHolder.INSTANCE;
    }

    public String getProperty(String key){
        return configProp.getProperty(key);
    }

    public Set<String> getAllPropertyNames(){
        return configProp.stringPropertyNames();
    }

    public boolean containsKey(String key){
        return configProp.containsKey(key);
    }
}

