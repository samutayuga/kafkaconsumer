package com.samutup.kafka.consumer;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;

/**
 * { "created_at": "Sat May 22 15:45:31 +0000 2021", "id": 1396130110462472195, "id_str":
 * "1396130110462472195", "text": "LLC treasury conversion play #fiat 2 #crypto with this prime
 * category defining domain name &gt;\u2026 https:\/\/t.co\/hdoB7C1Zwc", "source": "\u003ca
 * href=\"http:\/\/twitter.com\/download\/iphone\" rel=\"nofollow\"\u003eTwitter for
 * iPhone\u003c\/a\u003e", "truncated": true, "in_reply_to_status_id": null,
 * "in_reply_to_status_id_str": null, "in_reply_to_user_id": null, "in_reply_to_user_id_str": null,
 * "in_reply_to_screen_name": null, "user": { "id": 992055487758815232, "id_str":
 * "992055487758815232", "name": "Crypto.LLC", "screen_name": "Cryptorah", "location": null, "url":
 * "https:\/\/blockfi.mxuy67.net\/c\/2613816\/684369\/10568", "description": "#Crypto #BlockFi
 * #bitcoin  #btc #eth #link #algo #xrp #xlm #bat #quantum #FinTech #defi ALL #News & #retweets = A
 * zero advice site! click bio link grow your $", "translator_type": "none", "protected": false,
 * "verified": false, "followers_count": 132, "friends_count": 624, "listed_count": 4,
 * "favourites_count": 1300, "statuses_count": 1712, "created_at": "Thu May 03 14:57:14 +0000 2018",
 * "utc_offset": null, "time_zone": null, "geo_enabled": false, "lang": null,
 * "contributors_enabled": false, "is_translator": false, "profile_background_color": "F5F8FA",
 * "profile_background_image_url": "", "profile_background_image_url_https": "",
 * "profile_background_tile": false, "profile_link_color": "1DA1F2", "profile_sidebar_border_color":
 * "C0DEED", "profile_sidebar_fill_color": "DDEEF6", "profile_text_color": "333333",
 * "profile_use_background_image": true, "profile_image_url": "http:\/\/pbs.twimg.com\/profile_images\/992061467284377602\/ukIcD8zT_normal.jpg",
 * "profile_image_url_https": "https:\/\/pbs.twimg.com\/profile_images\/992061467284377602\/ukIcD8zT_normal.jpg",
 * "profile_banner_url": "https:\/\/pbs.twimg.com\/profile_banners\/992055487758815232\/1525361856",
 * "default_profile": true, "default_profile_image": false, "following": null,
 * "follow_request_sent": null, "notifications": null, "withheld_in_countries": [] }, "geo": null,
 * "coordinates": null, "place": null, "contributors": null, "is_quote_status": false,
 * "extended_tweet": { "full_text": "LLC treasury conversion play #fiat 2 #crypto with this prime
 * category defining domain name &gt; https:\/\/t.co\/tF12kFPa3N &lt; bid now or dm 2 seed or 4 a
 * hybrid deal...Is this URL your new Unicorn? @michael_saylor @novogratz @cz_binance @Mashinsky
 * @sc
 *
 * @Kris_HK @elonmusk @JeffBezos @IBM", "display_text_range": [ 0, 285 ], "entities": { "hashtags":
 * [ { "text": "fiat", "indices": [ 29, 34 ] }, { "text": "crypto", "indices": [ 37, 44 ] } ],
 * "urls": [ { "url": "https:\/\/t.co\/tF12kFPa3N", "expanded_url": "https:\/\/www.afternic.com\/domain\/crypto.llc",
 * "display_url": "afternic.com\/domain\/crypto.\u2026", "indices": [ 96, 119 ] } ],
 * "user_mentions": [ { "screen_name": "michael_saylor", "name": "Michael Saylor", "id": 244647486,
 * "id_str": "244647486", "indices": [ 197, 212 ] }, { "screen_name": "novogratz", "name": "Mike
 * Novogratz", "id": 19847181, "id_str": "19847181", "indices": [ 213, 223 ] }, { "screen_name":
 * "cz_binance", "name": "CZ \ud83d\udd36 Binance", "id": 902926941413453824, "id_str":
 * "902926941413453824", "indices": [ 224, 235 ] }, { "screen_name": "Mashinsky", "name": "Alex
 * Mashinsky \u00a9\ufe0f", "id": 22264384, "id_str": "22264384", "indices": [ 236, 246 ] }, {
 * "screen_name": "sc", "name": "Mr. Carter", "id": 17560096, "id_str": "17560096", "indices": [
 * 247, 250 ] }, { "screen_name": "Kris_HK", "name": "Kris | Crypto.com", "id": 84850888, "id_str":
 * "84850888", "indices": [ 251, 259 ] }, { "screen_name": "elonmusk", "name": "Elon Musk", "id":
 * 44196397, "id_str": "44196397", "indices": [ 260, 269 ] }, { "screen_name": "JeffBezos", "name":
 * "Jeff Bezos", "id": 15506669, "id_str": "15506669", "indices": [ 270, 280 ] }, { "screen_name":
 * "IBM", "name": "IBM", "id": 18994444, "id_str": "18994444", "indices": [ 281, 285 ] } ],
 * "symbols": [] } }, "quote_count": 0, "reply_count": 0, "retweet_count": 0, "favorite_count": 0,
 * "entities": { "hashtags": [ { "text": "fiat", "indices": [ 29, 34 ] }, { "text": "crypto",
 * "indices": [ 37, 44 ] } ], "urls": [ { "url": "https:\/\/t.co\/hdoB7C1Zwc", "expanded_url":
 * "https:\/\/twitter.com\/i\/web\/status\/1396130110462472195", "display_url":
 * "twitter.com\/i\/web\/status\/1\u2026", "indices": [ 97, 120 ] } ], "user_mentions": [],
 * "symbols": [] }, "favorited": false, "retweeted": false, "possibly_sensitive": false,
 * "filter_level": "low", "lang": "en", "timestamp_ms": "1621698331834" }
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class TweetPayload {

  private String id;
  private String text;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getText() {
    return text;
  }

  public void setText(String text) {
    this.text = text;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    TweetPayload that = (TweetPayload) o;
    return Objects.equal(id, that.id) && Objects
        .equal(text, that.text);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(id, text);
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
        .add("id", id)
        .add("text", text)
        .toString();
  }
}
