package com.outstandingboy.donationalert.entity;

import com.google.gson.annotations.SerializedName;
import com.outstandingboy.donationalert.util.Gsons;
import lombok.Getter;

import java.util.List;

public class ChzzkDonationPayload {
    public static class Body {
        @Getter
        public static class Profile {
            private String userIdHash;
            private String nickname;
        }
        @Getter
        public static class Extras {
            private long payAmount;
        }

        @SerializedName("profile")
        private String profileStr;
        @SerializedName("extras")
        private String extrasStr;
        @SerializedName("msg")
        private @Getter String msg;

        private transient Profile profile;
        private transient Extras extras;

        public Profile getProfile() {
            if (profile == null) {
                if (profileStr == null) {
                    profile = new Profile();
                    profile.userIdHash = "anonymous";
                    profile.nickname = "익명의 후원자";
                } else {
                    profile = Gsons.gson().fromJson(profileStr, Profile.class);
                }
            }
            return profile;
        }

        public Extras getExtras() {
            if (extras == null) {
                extras = Gsons.gson().fromJson(extrasStr, Extras.class);
            }
            return extras;
        }
    }
    private List<Body> bdy;

    public Body getBody() {
        return !bdy.isEmpty() ? bdy.get(0) : null;
    }
}
