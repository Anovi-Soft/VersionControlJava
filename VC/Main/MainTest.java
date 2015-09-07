package Main;

import Util.ZipperOriginal;

import java.io.IOException;

/**
 * Created by Андрей on 24.06.2015.
 */
public class MainTest {

    public static void main(String[] args) {
        ZipperOriginal a = new ZipperOriginal();
        try{
            a.zip("c:/tas", "c:/tmp/tmp.zip");
            a.unzip("c:/tmp/tmp.zip", "c:/tmp/tmp");
        }
        catch (IOException e)
        {

        }
    }
}
