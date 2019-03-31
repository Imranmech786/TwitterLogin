package com.imran.fyndtestapp.twitter;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.imran.fyndtestapp.R;
import com.imran.fyndtestapp.model.TwitterTweet;

import java.util.List;


public class TwitterFeedsAdapter extends RecyclerView.Adapter<TwitterFeedsAdapter.ViewHolder> {


    private Context mContext;
    private List<TwitterTweet> mTweetList;

    public TwitterFeedsAdapter(Context context, List<TwitterTweet> tweetList) {
        mContext = context;
        mTweetList = tweetList;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.item_row_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int pos) {

        final TwitterTweet twitterTweet = mTweetList.get(pos);
        holder.userName.setText(twitterTweet.getTwitterUser().getName());
        holder.tweetMsg.setText(Html.fromHtml(twitterTweet.getText()));
        String imageUrl = twitterTweet.getTwitterUser().getProfileImageUrl();
        ImageRequest request;
        if (imageUrl != null && !imageUrl.trim().isEmpty()) {
            request = ImageRequestBuilder.newBuilderWithSource(Uri.parse(imageUrl))
                    .build();
        } else {
            request = ImageRequestBuilder.newBuilderWithResourceId(R.color.skeleton_color)
                    .build();
        }
        DraweeController controller = Fresco.newDraweeControllerBuilder()
                .setImageRequest(request)
                .setOldController(holder.profileImage.getController())
                .build();
        holder.profileImage.setController(controller);

    }

    @Override
    public int getItemCount() {
        return mTweetList != null && !mTweetList.isEmpty() ? mTweetList.size() : 0;
    }

    public void update(List<TwitterTweet> tweetList) {
        mTweetList = tweetList;
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private SimpleDraweeView profileImage;
        private TextView userName, tweetMsg;

        private ViewHolder(View itemView) {
            super(itemView);
            profileImage = itemView.findViewById(R.id.profile_image);
            userName = itemView.findViewById(R.id.user_name);
            tweetMsg = itemView.findViewById(R.id.tweet_msg);
        }
    }
}
