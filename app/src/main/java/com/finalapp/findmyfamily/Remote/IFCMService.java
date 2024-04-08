package com.finalapp.findmyfamily.Remote;

import android.database.Observable;

import com.finalapp.findmyfamily.model.MyResponse;
import com.finalapp.findmyfamily.model.Request;

import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface IFCMService {
    @Headers({
            "Content-Type:application/json",
            "Authorization:key=AAAA-gxrdOQ:APA91bEVawSKTMWmXWstcnq-ypkq5jWauLtldqWnpM8tgctb_kfP7_-rM5WZQKw8ZbS7aEz2JEsFmeMii1nb8T_lgX7Rng7hLs47CwMvIUgQLSJ896hSDOrWF8dFVWtZxbv6MZqRFmpS"
    })
    @POST("fcm/send")
    Observable<MyResponse> sendFrienRequestToUser(@Body Request body);

}

