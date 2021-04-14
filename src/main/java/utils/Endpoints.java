package utils;

public class Endpoints {
    public static final String GET_ACC = "/account/{username}";
    public static final String POST_IMAGE_REC = "/image";
    public static final String GET_IMAGE_REC = "/image/{id}";
    public static final String FAV_IMAGE_REC = "/image/{id}/favorite";
    public static final String POST_IMAGE_INFO_REC = "/image/{imageHash}";
    public static final String DELETE_IMAGE_REC = "/account/{username}/image/{deleteHash}";

}
