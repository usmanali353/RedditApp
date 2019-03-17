package ma.ClickContent;




import java.util.ArrayList;
import java.util.Map;

import ma.ClickContent.check_registeration.wp_register;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.HeaderMap;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;
import ma.ClickContent.Upvote_model.check_upvote;
import ma.ClickContent.wp_posts_model.wp_posts;

/**
 * Created by User on 4/18/2017.
 */

public interface FeedAPI {
    @POST("wp-json/contenteclick/v1/login")
    Call<wp_register> get_Login_status(@Query("username")String username,@Query("password") String password);
    @POST("wp-json/contenteclick/v1/register")
    Call<wp_register> get_registration_status(@Query("username") String Username, @Query("email") String email, @Query("password") String password);
    @GET("wp-json/contenteclick/v1/post")
    Call<ArrayList<wp_posts>> get_posts_from_wp();
    @GET("wp-json/contenteclick/v1/post/username={username}/password={password}/post_id={post_id}/keyword={keyword}")
    Call<check_upvote> get_upvote_status(
    @Path("username") String username,
    @Path("password") String password,
    @Path("post_id") String post_id,
    @Path("keyword") String keyword
);
    @GET("wp-json/contenteclick/v1/posts/limit={limit}")
    Call<ArrayList<wp_posts>> get_limited_posts_from_wp(@Path("limit")int limit);
    @GET("wp-json/contenteclick/v1/post/s={keyword}")
    Call<ArrayList<wp_posts>> get_search_results(@Path("keyword") String keyword);
}
