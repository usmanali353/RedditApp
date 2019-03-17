package ma.ClickContent;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by MA on 6/13/2018.
 */

public class comments_adapter extends RecyclerView.Adapter<comments_viewholder> {
    public comments_adapter(Context context, ArrayList<Comment> commentArrayList) {
        this.context = context;
        this.commentArrayList = commentArrayList;
    }

    Context context;
    ArrayList<Comment> commentArrayList;
    @Override
    public comments_viewholder onCreateViewHolder(ViewGroup parent, int viewType) {

         View v=  LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_list_layout,parent,false);
        return new comments_viewholder(v);
    }

    @Override
    public void onBindViewHolder(comments_viewholder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return commentArrayList.size();
    }
}
class comments_viewholder extends RecyclerView.ViewHolder{
    TextView name,post;
    public comments_viewholder(View itemView) {
        super(itemView);
        name=(TextView) itemView.findViewById(R.id.name);
        post=(TextView) itemView.findViewById(R.id.post);
    }
}