package com.lims.detection.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class WebSocketMessageVO {

    private String type;

    private String title;

    private String content;

    private Object data;

    private Long targetUserId;

    private Long targetDeptId;

    private String sender;

    private LocalDateTime sendTime;

    public WebSocketMessageVO() {
        this.sendTime = LocalDateTime.now();
    }

    public WebSocketMessageVO(String type, String title, String content, Object data) {
        this.type = type;
        this.title = title;
        this.content = content;
        this.data = data;
        this.sendTime = LocalDateTime.now();
    }
}
