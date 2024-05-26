package com.outstandingboy.donationalert.platform;

import com.outstandingboy.donationalert.common.entity.Donation;
import com.outstandingboy.donationalert.common.entity.Message;
import com.outstandingboy.donationalert.common.event.Topic;

public interface Platform {
    Topic<Donation> getDonationTopic();

    Topic<Message> getMessageTopic();
    Topic<String> getLegacyMessageTopic();
}
