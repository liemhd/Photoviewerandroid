package duyliem.photoviewer.Collection;

/**
 * Created by Duy Liem on 30/10/2018.
 */

public class Collection {
    public String name;
    public String links;


    public Collection(String nameAlbum, String imageAlbum) {
        this.name = nameAlbum;
        this.links = imageAlbum;
    }

    public Collection(){

    }

    public String getNameAlbum() {
        return name;
    }

    public void setNameAlbum(String nameAlbum) {
        this.name = nameAlbum;
    }

    public String getImageAlbum() {
        return links;
    }

    public void setImageAlbum(String imageAlbum) {
        this.links = imageAlbum;
    }
}
