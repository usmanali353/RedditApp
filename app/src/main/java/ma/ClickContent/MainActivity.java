package ma.ClickContent;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ProgressBar;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import ma.ClickContent.wp_posts_model.wp_posts;

public class MainActivity extends AppCompatActivity {
RecyclerView post_recyclerview;
    String username;
     SwipeRefreshLayout refresh_layout;
    int scrolled_items,visible_items,total_items;
    int limit=10;
    LinearLayoutManager manager;
    Boolean isScrolling=false;
    ArrayList<wp_posts> temp_post_list;
    ProgressBar pb;
    wp_posts_adapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        post_recyclerview=(RecyclerView)findViewById(R.id.post_recyclerview);
      refresh_layout=(SwipeRefreshLayout) findViewById(R.id.swiprefresh);
        temp_post_list=new ArrayList<>();
        pb=(ProgressBar) findViewById(R.id.progress);
        Drawable drawable=new ProgressBar(this).getIndeterminateDrawable().mutate();
        drawable.setColorFilter(ContextCompat.getColor(this, R.color.primary),
                PorterDuff.Mode.SRC_IN);
        pb.setIndeterminateDrawable(drawable);
        manager=new LinearLayoutManager(MainActivity.this);
        post_recyclerview.setLayoutManager(manager);
        refresh_layout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                get_limited_posts(limit);
            }
        });
       get_limited_posts(limit);



           post_recyclerview.addOnScrollListener(new RecyclerView.OnScrollListener() {
               @Override
               public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                   super.onScrollStateChanged(recyclerView, newState);
                   if(newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL)
                   {
                                        isScrolling = true;
                   }

               }

               @Override
               public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                   super.onScrolled(recyclerView, dx, dy);
                   scrolled_items=manager.findFirstVisibleItemPosition();
                   total_items=manager.getItemCount();
                   visible_items=manager.getChildCount();
                   if(isScrolling && (visible_items + scrolled_items == total_items))
                                        {
                                            isScrolling = false;
                                            limit=limit+10;
                                            if(limit<=400) {
                                                get_limited_posts(limit);
                                            }
                                       }

               }
           });
      //  get_posts_from_wp();
    }

    public void get_posts_from_wp(){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://www.palmsaresweaty.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        FeedAPI feedAPI = retrofit.create(FeedAPI.class);
        Call<ArrayList<wp_posts>> call =feedAPI.get_posts_from_wp();
        call.enqueue(new Callback<ArrayList<wp_posts>>() {
            @Override
            public void onResponse(Call<ArrayList<wp_posts>> call, Response<ArrayList<wp_posts>> response) {

                if(response.body()!=null)
                if(response.body().size()>0) {
                    refresh_layout.setRefreshing(false);
                    wp_posts_adapter adapter=new wp_posts_adapter(response.body(), MainActivity.this);
                    post_recyclerview.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                    Log.e("post_count",String.valueOf(manager.getItemCount()));
                     }

               // Log.e("thumbnail",response.body().get(0).getContent().getRendered());
               // Log.e("comment_url",response.body().get(0).getLinks().getReplies().get(0).getHref());
            }

            @Override
            public void onFailure(Call<ArrayList<wp_posts>> call, Throwable throwable) {
                refresh_layout.setRefreshing(false);
              Log.e("wp_error",throwable.getMessage());
            }
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);

        username = preferences.getString("@string/SessionUsername", "");
        if(username.equals("")){
            getMenuInflater().inflate(R.menu.search_menu,menu);
            MenuItem searchitem=menu.findItem(R.id.search);
            SearchView postsearch=(SearchView) MenuItemCompat.getActionView(searchitem);
            postsearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    search_post(query);
                    return true;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    return false;
                }
            });

        }else{
            getMenuInflater().inflate(R.menu.menu_main,menu);
            MenuItem searchitem=menu.findItem(R.id.search);
            SearchView postsearch=(SearchView) MenuItemCompat.getActionView(searchitem);
            postsearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                  search_post(query);
                    return true;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    return false;
                }
            });

        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==R.id.action_settings){
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
             preferences.edit().remove("@string/SessionUsername").apply();
            Intent i=new Intent(MainActivity.this,MainActivity.class);
            startActivity(i);
            finish();
        }else if(item.getItemId()==R.id.login){
            Intent i=new Intent(MainActivity.this,Login_Activity.class);
            startActivity(i);
        }else if (item.getItemId()==R.id.signup){
            Intent i=new Intent(MainActivity.this,Register.class);
            startActivity(i);
        }
        return super.onOptionsItemSelected(item);

    }
    private void search_post(String keyword){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://www.palmsaresweaty.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        FeedAPI feedAPI = retrofit.create(FeedAPI.class);
        Call<ArrayList<wp_posts>> call =feedAPI.get_search_results(keyword);
        call.enqueue(new Callback<ArrayList<wp_posts>>() {
            @Override
            public void onResponse(Call<ArrayList<wp_posts>> call, Response<ArrayList<wp_posts>> response) {
                if(response.body()!=null) {
                    if (response.body().size() > 0) {
                        refresh_layout.setRefreshing(false);
                        post_recyclerview.setAdapter(new wp_posts_adapter(response.body(), MainActivity.this));
                    } else {
                        refresh_layout.setRefreshing(false);
                        Toast.makeText(MainActivity.this, "No Results Found", Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<ArrayList<wp_posts>> call, Throwable throwable) {
                refresh_layout.setRefreshing(false);
               Toast.makeText(MainActivity.this,throwable.getMessage(),Toast.LENGTH_LONG).show();
            }
        });
    }
    public void get_limited_posts(final int limit){
        pb.setVisibility(View.VISIBLE);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://www.palmsaresweaty.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        FeedAPI feedAPI = retrofit.create(FeedAPI.class);
        Call<ArrayList<wp_posts>> call =feedAPI.get_limited_posts_from_wp(limit);
        call.enqueue(new Callback<ArrayList<wp_posts>>() {
            @Override
            public void onResponse(Call<ArrayList<wp_posts>> call, Response<ArrayList<wp_posts>> response) {
                refresh_layout.setRefreshing(false);
              pb.setVisibility(View.GONE);
                Log.e("limited",response.toString());
                if(response.body()!=null)
                    if(response.body().size()>0) {
                        refresh_layout.setRefreshing(false);
                         //temp_post_list=response.body();
                         // temp_post_list.addAll(response.body());
                        adapter=new wp_posts_adapter(response.body(), MainActivity.this);
                        post_recyclerview.setAdapter(adapter);
                        adapter.notifyDataSetChanged();
                        if(limit>10) {
                            manager.scrollToPosition(limit - 11);
                        }else{
                            manager.scrollToPosition(limit - 10);
                        }
                        Log.e("post_count",String.valueOf(manager.getItemCount()));
                    }

                // Log.e("thumbnail",response.body().get(0).getContent().getRendered());
                // Log.e("comment_url",response.body().get(0).getLinks().getReplies().get(0).getHref());
            }

            @Override
            public void onFailure(Call<ArrayList<wp_posts>> call, Throwable throwable) {
                pb.setVisibility(View.GONE);
                refresh_layout.setRefreshing(false);
                Log.e("wp_error",throwable.getMessage());
            }
        });
    }
}
