package dhbk.android.gps_osm_fragment.Help;

/**
 * Created by huynhducthanhphong on 4/26/16.
 */

public class Nick {
    private String nick;
    private String email;

    public Nick() {
    }

    public Nick(String nick, String email) {

        this.nick = nick;
        this.email = email;
    }

    public String getNick() {
        return nick;
    }

    public String getEmail() {
        return email;
    }
}
