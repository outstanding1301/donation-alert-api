package com.outstandingboy.donationalert.platform;

import com.outstandingboy.donationalert.common.entity.Donation;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.subjects.Subject;

public interface Platform {
    Disposable subscribeDonation(Consumer<Donation> onNext);

    Disposable subscribeMessage(Consumer<String> onNext);

    void close();

    Subject<Donation> getDonationObservable();

    Subject<String> getMessageObservable();
}
