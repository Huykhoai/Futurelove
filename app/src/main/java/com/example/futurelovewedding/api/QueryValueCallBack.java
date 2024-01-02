package com.example.futurelovewedding.api;

public interface QueryValueCallBack {
    void onQueryValueReceived(String query);
    void onApiCallFailed(Throwable tb);
}
