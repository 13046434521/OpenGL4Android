package com.jtl.opengl.helper;

import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * 作者:jtl
 * 日期:Created in 2019/9/9 22:33
 * 描述:
 * 更改:
 */
public class ConcurrentHelper<T> {
    private ConcurrentLinkedQueue<T> mConcurrentLinkedQueue = new ConcurrentLinkedQueue<>();

    public void offerData(T data) {
        mConcurrentLinkedQueue.offer(data);
    }

    public T pollData() {
        return mConcurrentLinkedQueue.poll();
    }

    private static class ConcurrentHelperHolder {
    }
}
