package duyliem.photoviewer.Object;

/**
 * Created by Duy Liem on 10/10/2018.
 */

public class Photo {
    public String name;
    public String links;
    public String tag;
    public String type;
    public String created;
    public String links_thum;

    public Photo(String name, String links, String tag, String type, String created, String links_thum) {
        this.name = name;
        this.links = links;
        this.tag = tag;
        this.type = type;
        this.created= created;
        this.links_thum= links_thum;
    }

    public Photo() {

    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLinks() {
        return links;
    }

    public void setLinks(String links) {
        this.links = links;
    }

    public String getLinks_thum() {
        return links_thum;
    }

    public void setLinks_thum(String links_thum) {
        this.links_thum = links_thum;
    }
}
