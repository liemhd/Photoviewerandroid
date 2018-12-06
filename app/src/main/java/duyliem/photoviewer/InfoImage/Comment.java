package duyliem.photoviewer.InfoImage;

/**
 * Created by Duy Liem on 01/12/2018.
 */

public class Comment {
    public String avatar;
    public String comment;
    public String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Comment() {
    }

    public Comment(String avatar, String comment, String name) {
        this.avatar = avatar;
        this.comment = comment;
        this.name = name;
    }

    public String getAvt() {
        return avatar;
    }

    public void setAvt(String avatar) {
        this.avatar = avatar;
    }

    public String getCmt() {
        return comment;
    }

    public void setCmt(String comment) {
        this.comment = comment;
    }
}
