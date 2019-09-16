/**
 * Copyright (c) Fraunhofer IML
 */
package com.duangframework.agv.core;

/**
 * 声明能够发送电报/请求的通信适配器的方法
 *
 * @author Laotang
 */
public interface ITelegramSender {

  /**
   *  发送电报
   *
   * @param telegram The {@link Telegram} to be sent.
   */
  void sendTelegram(Telegram telegram);
}
