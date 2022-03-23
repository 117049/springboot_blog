package com.xxx.blog.vo.params;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class QueueResult {
    private int CatagoryId;
    private String msg;
    private Object dataClass;
}
