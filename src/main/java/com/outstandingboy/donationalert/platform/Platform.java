package com.outstandingboy.donationalert.platform;

import com.outstandingboy.donationalert.entity.Donation;
import io.reactivex.functions.Consumer;
import io.reactivex.subjects.Subject;

public interface Platform {
    void subscribeDonation(Consumer<Donation> onNext);
    void subscribeMessage(Consumer<String> onNext);
    void close();

    Subject<Donation> getDonationObservable();
    Subject<String> getMessageObservable();
}
