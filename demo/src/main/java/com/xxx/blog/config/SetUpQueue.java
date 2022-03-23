package com.xxx.blog.config;

import com.xxx.blog.vo.params.QueueResult;

public interface SetUpQueue {
    void QueueAdd(QueueResult queueResult);
    void QueueConsume();
    int QueueSize();
}
