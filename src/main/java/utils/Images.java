package utils;

public enum Images {
    INPUT_IMG_NORMAL_BMP( "src/test/resources/TSR_Sonic_the_Hedgehog_bmp.bmp"),
    INPUT_IMG_SMALL_PNG_LINK("https://upload.wikimedia.org/wikipedia/commons/c/ca/1x1.png"),
    INPUT_IMG_NORMAL_PNG("src/test/resources/normapPNG.png"),
    INPUT_IMG_SMALL_JPG("src/test/resources/1x1.jpg"),
    INPUT_IMG_NORMAL_JPG_LINK("https://cdn02.nintendo-europe.com/media/images/10_share_images/games_15/nintendo_switch_download_software_1/H2x1_NSwitchDS_SonicMania.jpg"),
    INPUT_IMG_SMALL_GIF("src/test/resources/1x1_gif.gif"),
    INPUT_IMG_NORMAL_GIF ("src/test/resources/normalGIF.gif"),
    INPUT_IMG_SMALL_BMP_LINK("https://lmcnulty.gitlab.io/blog/bmp-output/1x1.bmp"),

    INPUT_IMG_LARGE_PNG ("src/test/resources/largePNG.png"),
    INPUT_IMG_LARGE_JPG ("src/test/resources/largeJPG.jpg"),
    INPUT_IMG_LARGE_GIF_LINK ("https://i.imgur.com/6fWJ7XQ.gif"),
    INPUT_IMG_LARGE_BMP ("src/test/resources/largeBMP.bmp"),
    INPUT_INVALID_FILE ("src/test/resources/encoded-20210405133937.txt");

    public final String path;
    Images(String path){ this.path = path;}

}
