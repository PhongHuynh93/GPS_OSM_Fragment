package dhbk.android.gps_osm_fragment.Help;

/**
 * Created by Jhordan on 23/07/15.
 */
public class Chat {

    private String message;
    private String author;

    // Default constructor is required for Firebase object mapping
    @SuppressWarnings("unuser")
    public Chat(){}

    public Chat(String message, String author) {
        this.message = message;
        this.author = author;
    }

    public String getMessage() {
        return message;
    }

    public String getAuthor() {
        return author;
    }




}
