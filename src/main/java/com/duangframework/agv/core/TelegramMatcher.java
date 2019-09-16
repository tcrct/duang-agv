package com.duangframework.agv.core;

import com.duangframework.agv.kit.ToolsKit;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.util.LinkedList;
import java.util.Optional;
import java.util.Queue;

import static java.util.Objects.requireNonNull;

/**
 * 请求响应匹配器
 *
 * @author Laotang
 */
public class TelegramMatcher {

    private static final Logger logger = LoggerFactory.getLogger(TelegramMatcher.class);

    /**请求队列*/
    private final Queue<Telegram> requests = new LinkedList<>();

    /** 电报发送接口*/
    private ITelegramSender telegramSender;

    @Inject
    public TelegramMatcher(@Assisted ITelegramSender telegramSender) {
        this.telegramSender = requireNonNull(telegramSender, "telegramSender");
    }

    /**
     *  将请求电报加入队列中
     * @param requestTelegram   请求电报
     */
    public void enqueueRequestTelegram(@Nonnull Telegram requestTelegram) {
        requireNonNull(requestTelegram, "requestTelegram");

        boolean emptyQueueBeforeEnqueue = requests.isEmpty();

        requests.add(requestTelegram);

        if (emptyQueueBeforeEnqueue) {
            checkForSendingNextRequest();
        }
    }

    /**检查是否发送下一个请求*/
    public void checkForSendingNextRequest() {
        logger.info("检查是否发送下一个请求.");
        if (peekCurrentRequest().isPresent()) {
            telegramSender.sendTelegram(peekCurrentRequest().get());
        }
        else {
            logger.info("没有请求消息发送");
        }
    }

    public Optional<Telegram> peekCurrentRequest() {
        return Optional.ofNullable(requests.peek());
    }

    /**
     * 如果与队列中的第一个请求匹配则返回true,并将该请求在队列中移除
     *
     * @param responseTelegram 要匹配的响应电报
     * @return  如果响应与队列中的第一个请求匹配，则返回true
     */
    public boolean tryMatchWithCurrentRequestTelegram(@Nonnull Telegram responseTelegram) {

        java.util.Objects.requireNonNull(responseTelegram, "responseTelegram");

        //取出队列中的第一位的请求，该请求视为当前请求
        Telegram currentRequestTelegram = requests.peek();
        // 判断该回复里的请求ID与队列里的是否一致，如果一致，则返回true
        if(ToolsKit.isNotEmpty(currentRequestTelegram) && responseTelegram.getId().equals(currentRequestTelegram.getId())){
            // 在队列中移除第一位的
            requests.remove();
            return true;
        }

        if(ToolsKit.isNotEmpty(currentRequestTelegram)) {
            logger.warn("请求队列没有{}的请求对象，传参的请求对象{}， 队列与最新对应的请求对象不匹配",responseTelegram.getId(), currentRequestTelegram.getId());
        } else {
            logger.warn("接收到请求ID{}的响应，但没有请求正在等响应",responseTelegram.getId());
        }

        return false;

    }

}