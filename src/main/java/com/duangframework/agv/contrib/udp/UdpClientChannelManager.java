package com.duangframework.agv.contrib.udp;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.handler.logging.LoggingHandler;
import org.opentcs.contrib.tcp.netty.ConnectionEventListener;
import org.opentcs.util.Assertions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

public class UdpClientChannelManager<O,I> {
    private static final Logger LOG = LoggerFactory.getLogger(UdpClientChannelManager.class);
    private static final String LOGGING_HANDLER_NAME = "ChannelLoggingHandler";
    private final ConnectionEventListener<I> connectionEventListener;
    private final Supplier<List<ChannelHandler>> channelSupplier;
    private final int readTimeout;
    private Bootstrap bootstrap;
    private EventLoopGroup workerGroup;
    private ChannelFuture channelFuture;
    private boolean initialized;
    private ScheduledFuture<?> connectFuture;
    private final boolean loggingEnabled;


    public UdpClientChannelManager(@Nonnull ConnectionEventListener<I> connEventListener, Supplier<List<ChannelHandler>> channelSupplier, int readTimeout, boolean enableLogging) {
        this.connectionEventListener = (ConnectionEventListener) Objects.requireNonNull(connEventListener, "connEventListener");
        this.channelSupplier = (Supplier)Objects.requireNonNull(channelSupplier, "channelSupplier");
        this.readTimeout = readTimeout;
        this.loggingEnabled = enableLogging;
    }

    public void initialize() {
        if (!this.initialized) {
            this.bootstrap = new Bootstrap();
            this.workerGroup = new NioEventLoopGroup();
            this.bootstrap.group(this.workerGroup);
            //引导该 NioDatagramChannel（无连接的）
            this.bootstrap.channel(NioDatagramChannel.class);
            // 设置 SO_BROADCAST 套接字选项
            this.bootstrap.option(ChannelOption.SO_BROADCAST, true);

            this.bootstrap.handler(new ChannelInitializer<SocketChannel>() {
                protected void initChannel(SocketChannel ch) {
                    if (UdpClientChannelManager.this.loggingEnabled) {
                        ch.pipeline().addFirst("ChannelLoggingHandler", new LoggingHandler(UdpClientChannelManager.this.getClass()));
                    }
                    Iterator channelIterator = ((List)UdpClientChannelManager.this.channelSupplier.get()).iterator();
                    while(channelIterator.hasNext()) {
                        ChannelHandler handler = (ChannelHandler)channelIterator.next();
                        ch.pipeline().addLast(new ChannelHandler[]{handler});
                    }
                }
            });
            this.initialized = true;
        }
    }

    public boolean isInitialized() {
        return this.initialized;
    }

    public void terminate() {
        if (this.initialized) {
            this.cancelConnect();
            this.disconnect();
            this.workerGroup.shutdownGracefully();
            this.workerGroup = null;
            this.bootstrap = null;
            this.initialized = false;
        }
    }

    public void connect(@Nonnull String host, int port) {
        Objects.requireNonNull(host, "host");
        Assertions.checkState(this.isInitialized(), "Not initialized");
        if (this.isConnected()) {
            LOG.debug("Already connected, doing nothing.");
        } else {
            LOG.debug("Initiating connection attempt to {}:{}...", host, port);
            this.channelFuture = this.bootstrap.connect(host, port);
            this.channelFuture.addListener((future) -> {
                if (future.isSuccess()) {
                    this.connectionEventListener.onConnect();
                } else {
                    this.connectionEventListener.onFailedConnectionAttempt();
                }

            });
            this.connectFuture = null;
        }
    }

    public void cancelConnect() {
        if (this.connectFuture != null) {
            this.connectFuture.cancel(false);
            this.connectFuture = null;
        }
    }

    public void disconnect() {
        if (this.isConnected()) {
            if (this.channelFuture != null) {
                this.channelFuture.channel().disconnect();
                this.channelFuture = null;
            }

        }
    }

    public boolean isConnected() {
        return this.channelFuture != null && this.channelFuture.channel().isActive();
    }

    public void send(O telegram) {
        if (this.isConnected()) {
            this.channelFuture.channel().writeAndFlush(telegram);
        }
    }

    public void setLoggingEnabled(boolean enabled) {
        Assertions.checkState(this.initialized, "Not initialized.");
        if (this.channelFuture == null) {
            LOG.debug("No channel future available, doing nothing.");
        } else {
            ChannelPipeline pipeline = this.channelFuture.channel().pipeline();
            if (enabled && pipeline.get("ChannelLoggingHandler") == null) {
                pipeline.addFirst("ChannelLoggingHandler", new LoggingHandler(this.getClass()));
            } else if (!enabled && pipeline.get("ChannelLoggingHandler") != null) {
                pipeline.remove("ChannelLoggingHandler");
            }
        }
    }
}
