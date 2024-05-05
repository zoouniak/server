package com.example.cns.chat.service;

public interface MessageSubscriber {
    void subscribe(String roomId);

    void unsubscribe();

}
