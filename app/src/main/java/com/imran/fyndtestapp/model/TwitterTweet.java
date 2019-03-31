package com.imran.fyndtestapp.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

@Entity(tableName = "twitter_feeds")
public class TwitterTweet {

    @SerializedName("created_at")
    @ColumnInfo(name = "createdAt")
    private String createdAt;

    @PrimaryKey(autoGenerate = true)
    @SerializedName("id")
    private long id;

    @SerializedName("text")
    @ColumnInfo(name = "text")
    private String text;

    @SerializedName("in_reply_to_status_id")
    @ColumnInfo(name = "inReplyToStatusId")
    private String inReplyToStatusId;

    @SerializedName("in_reply_to_user_id")
    @ColumnInfo(name = "inReplyToUserId")
    private String inReplyToUserId;

    @SerializedName("in_reply_to_screen_name")
    @ColumnInfo(name = "inReplyToScreenName")
    private String inReplyToScreenName;

    @SerializedName("user")
    @ColumnInfo(name = "twitterUser")
    private TwitterUser twitterUser;

    public String getCreatedAt() {
        return createdAt;
    }

    public long getId() {
        return id;
    }

    public String getInReplyToScreenName() {
        return inReplyToScreenName;
    }

    public String getInReplyToStatusId() {
        return inReplyToStatusId;
    }

    public String getInReplyToUserId() {
        return inReplyToUserId;
    }

    public String getText() {
        return text;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setInReplyToScreenName(String inReplyToScreenName) {
        this.inReplyToScreenName = inReplyToScreenName;
    }

    public void setInReplyToStatusId(String inReplyToStatusId) {
        this.inReplyToStatusId = inReplyToStatusId;
    }

    public void setInReplyToUserId(String inReplyToUserId) {
        this.inReplyToUserId = inReplyToUserId;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setTwitterUser(TwitterUser twitterUser) {
        this.twitterUser = twitterUser;
    }

    public TwitterUser getTwitterUser() {
        return twitterUser;
    }

}

