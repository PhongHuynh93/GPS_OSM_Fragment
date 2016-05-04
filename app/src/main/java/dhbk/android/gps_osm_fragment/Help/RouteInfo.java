package dhbk.android.gps_osm_fragment.Help;

/**
 * Created by Thien Nhan on 4/7/2016.
 */
public class RouteInfo {
    private String name;
    private String description;
    private String img;
    private String route;
    private String time;
    private String id;

    public RouteInfo() {
    }

    public RouteInfo(String name, String description, String img, String route, String time) {
        this.name = name;
        this.description = description;
        this.img = img;
        this.route = route;
        this.time = time;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRoute() {
        return route;
    }

    public void setRoute(String route) {
        this.route = route;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
