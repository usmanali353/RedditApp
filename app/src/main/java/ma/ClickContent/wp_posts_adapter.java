package ma.ClickContent;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.StringTokenizer;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import ma.ClickContent.Upvote_model.check_upvote;
import ma.ClickContent.wp_posts_model.wp_posts;

/**
 * Created by HelloWorldSolution on 6/20/2018.
 */

public class wp_posts_adapter extends RecyclerView.Adapter<wp_posts_viewholder> {
    ArrayList<wp_posts> post_list;
    Context context;
    public wp_posts_adapter(ArrayList<wp_posts> post_list, Context context) {
        this.post_list = post_list;
        this.context=context;
    }

    @NonNull
    @Override
    public wp_posts_viewholder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v= LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.login_layout,null);

        return new wp_posts_viewholder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final wp_posts_viewholder wp_posts_viewholder, final int i) {
        long timeInMilliseconds;
      //  String date = "yyyy-mm-dd hh:mm:ss";
        StringTokenizer tk = new StringTokenizer(post_list.get(i).getData().getPostDate());

        String date = tk.nextToken();  // <---  yyyy-mm-dd
        String time = tk.nextToken();
        try {
            CharSequence timeAgo = DateUtils.getRelativeTimeSpanString(milliseconds(date),
                    System.currentTimeMillis(), DateUtils.DAY_IN_MILLIS);
            wp_posts_viewholder.timestamp.setText(timeAgo);
        } catch (Exception e) {
            e.printStackTrace();
        }
        wp_posts_viewholder.name.setText(post_list.get(i).getData().getPostAuthor());
        if (post_list.get(i).getUpdvoting()!=null){
            if(!post_list.get(i).getUpdvoting().equals("")){
                wp_posts_viewholder.like_img.setImageResource(R.drawable.ic_arrow_upward_blue_24dp);
                wp_posts_viewholder.upvotestxt.setTextColor(context.getResources().getColor(R.color.colorPrimary));
                wp_posts_viewholder.upvotestxt.setText("Upvoted");
            }
        }
        wp_posts_viewholder.post.setText(post_list.get(i).getData().getPostTitle());
        wp_posts_viewholder.profilepic.setImageResource(R.drawable.login);
            Glide.with(context).load(post_list.get(i).getImgPath()).into(wp_posts_viewholder.feedimage);
        wp_posts_viewholder.like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
              final String  username = preferences.getString("@string/SessionUsername", "");
                if (username.equals("")) {
                    Toast.makeText(context,"You are not Logged in",Toast.LENGTH_LONG).show();
                }else if (!username.equals("helloworld678")) {
                    Toast.makeText(context,"Only Admin can upvote post",Toast.LENGTH_LONG).show();
                }else{
                    View kv = LayoutInflater.from(context).inflate(R.layout.keywords_layout, null);
                    final AppCompatEditText keywords = (AppCompatEditText) kv.findViewById(R.id.keywords);
                    final AlertDialog keywords_alert = new AlertDialog.Builder(context)
                            .setTitle("Upvote Post")
                            .setMessage("Enter keyword for this post")
                            .setView(kv)
                            .setPositiveButton("upvote", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if (!keywords.getText().toString().isEmpty()) {
                                        Retrofit retrofit = new Retrofit.Builder()
                                                .baseUrl("http://www.palmsaresweaty.com/")
                                                .addConverterFactory(GsonConverterFactory.create())
                                                .build();
                                        FeedAPI feedAPI = retrofit.create(FeedAPI.class);
                                        Call<check_upvote> call =feedAPI.get_upvote_status("helloworld678","flamings345",String.valueOf(post_list.get(i).getData().getID()),keywords.getText().toString());
                                          call.enqueue(new Callback<check_upvote>() {
                                              @Override
                                              public void onResponse(Call<check_upvote> call, Response<check_upvote> response) {
                                                  if(response.body()!=null) {
                                                      Toast.makeText(context, response.body().getData().getMessage(), Toast.LENGTH_LONG).show();
                                                      wp_posts_viewholder.like_img.setImageResource(R.drawable.ic_arrow_upward_blue_24dp);
                                                      wp_posts_viewholder.upvotestxt.setTextColor(context.getResources().getColor(R.color.colorPrimary));
                                                      wp_posts_viewholder.upvotestxt.setText("Upvoted");
                                                  }else{
                                                      Log.e("upvote_problem",response.toString());
                                                  }
                                              }

                                              @Override
                                              public void onFailure(Call<check_upvote> call, Throwable throwable) {
                                                  Log.e("upvote_error",throwable.toString());
                                              }
                                          });
                                    } else {
                                        Toast.makeText(context, "Please enter the Keywords", Toast.LENGTH_LONG).show();
                                    }
                                    dialog.dismiss();
                                }
                            }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            }).show();
                    keywords_alert.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(context.getResources().getColor(R.color.primary_dark));
                    keywords_alert.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(context.getResources().getColor(R.color.primary_dark));
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return post_list.size();
    }
    public long milliseconds(String date)
    {
        //String date_ = date;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try
        {
            Date mDate = sdf.parse(date);
            long timeInMilliseconds = mDate.getTime();
            System.out.println("Date in milli :: " + timeInMilliseconds);
            return timeInMilliseconds;
        }
        catch (Exception e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return 0;
    }
}
class wp_posts_viewholder extends RecyclerView.ViewHolder{
    TextView name,post;
    TextView timestamp,upvotestxt;
    LinearLayout like;
    ImageView profilepic,feedimage,like_img;

    public wp_posts_viewholder(View itemView) {
        super(itemView);
        name=(TextView) itemView.findViewById(R.id.name);
        timestamp=(TextView) itemView.findViewById(R.id.timeofpost);
        post=(TextView) itemView.findViewById(R.id.post);
      //  comments=(LinearLayout)itemView.findViewById(R.id.layout_comment);
        profilepic=(ImageView)itemView.findViewById(R.id.profilePic);
        //share=(LinearLayout)itemView.findViewById(R.id.layout_share);
        like=(LinearLayout)itemView.findViewById(R.id.layout_like);
        feedimage=(ImageView)itemView.findViewById(R.id.feedImage1);
        upvotestxt=(TextView) itemView.findViewById(R.id.upvotetxt);
        like_img=(ImageView) itemView.findViewById(R.id.like_img);
    }
}