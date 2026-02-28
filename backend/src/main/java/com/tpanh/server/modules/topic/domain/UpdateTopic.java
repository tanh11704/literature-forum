package com.tpanh.server.modules.topic.domain;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateTopic {

    private String title;
    private String content;
}
