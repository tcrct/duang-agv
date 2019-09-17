package com.duangframework.agv.model;

import com.duangframework.agv.core.Telegram;

public class EmptyTelegram extends Telegram {

    @Override
    public String toString() {
        return "EmptyTelegram{" +
                "id='" + id + '\'' +
                ", positionId='" + positionId + '\'' +
                '}';
    }
}

