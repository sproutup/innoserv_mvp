package test;

import java.io.File;
import javax.activation.MimetypesFileTypeMap;

class MediaTypeTest {
    public static void main(String[] args) {
        System.out.println(System.getProperty("java.version"));

        File f = new File(System.getProperty("java.home"), "lib");
        f = new File(f, "mime.types");
        System.out.println(f.exists() + " \t - " +f);

        f = new File(System.getProperty("user.home"), ".mime.types");
        System.out.println(f.exists() + " \t - " +f);

        MimetypesFileTypeMap mfm = new MimetypesFileTypeMap();
        System.out.println(mfm.getContentType("a.js"));
        System.out.println(mfm.getContentType("a.png"));
        System.out.println(mfm.getContentType("a.jpg"));
        System.out.println(mfm.getContentType("a.au"));
        System.out.println(mfm.getContentType("a.htm"));
        System.out.println(mfm.getContentType("a.mov"));
        System.out.println(mfm.getContentType("a.mpeg4"));
        System.out.println(mfm.getContentType("a.mp4"));
        System.out.println(mfm.getContentType("a.jpeg"));
    }
}