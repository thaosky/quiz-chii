package com.quizchii.chat;


import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class ChatMessage {
    private String senderName;
    private String receiverName;
    private String message;
    private String date;
    private MessageType status;
}
