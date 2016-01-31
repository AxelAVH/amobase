package de.amo.tools;

import java.io.File;
import java.util.Map;

/**
 * Created by private on 31.01.2016.
 */
public class Environment {

    public static File getOS_DownloadDir() {
        Map<String, String> envVars = System.getenv();
        String userDir = envVars.get("USERPROFILE");
        return new File (userDir,"Downloads");
    }



    public static void main(String[] args) {
        Map<String, String> getenv = System.getenv();

        for (String s : getenv.keySet()) {
            System.out.println(s+" : " + getenv.get(s));
        }

        System.out.println("Hier der Test: " + getenv.get("USERPROFILE"));


        File os_downloadDir = getOS_DownloadDir();

        System.out.println(os_downloadDir);

        if (os_downloadDir.exists()) {
            System.out.println(" Existiert !!");
        }
    }

}
