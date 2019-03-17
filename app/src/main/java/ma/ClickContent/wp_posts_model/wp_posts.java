package ma.ClickContent.wp_posts_model;

import java.util.ArrayList;

/**
 * Created by HelloWorldSolution on 6/20/2018.
 */

public class wp_posts {

    private String img_path;

    public String getImgPath() { return this.img_path; }

    public void setImgPath(String img_path) { this.img_path = img_path; }

    public Object getUpdvoting() {
        return updvoting;
    }

    public void setUpvoting(String upvoting) {
        this.updvoting = upvoting;
    }

    private Object updvoting;

    private Data data;

    public Data getData() { return this.data; }

    public void setData(Data data) { this.data = data; }

}
