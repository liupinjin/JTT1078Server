package com.tsingtech.jtt1078.handler;

import com.tsingtech.jtt1078.codec.JTT1078FrameDecoder;
import com.tsingtech.jtt1078.config.JTT1078ServerProperties;
import com.tsingtech.jtt1078.live.handler.HttpServerHandler;
import com.tsingtech.jtt1078.util.BeanUtil;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.timeout.IdleStateHandler;

import java.util.concurrent.TimeUnit;

/**
 * @author chrisliu
 * @mail chrisliu.top@gmail.com
 * @since 2020/4/7 10:19
 */
public class Jtt1078ServerChannelInitializer extends ChannelInitializer<NioSocketChannel> {

    private static JTT1078ServerProperties JTT1078ServerProperties;
    static {
        JTT1078ServerProperties = BeanUtil.getBean(JTT1078ServerProperties.class);
    }

    private static final ChannelHandler INSTANCE = new HttpServerHandler();
    public static final ExceptionHandler exceptionHandler = new ExceptionHandler();

    @Override
    protected void initChannel(NioSocketChannel ch) {
        if (ch.localAddress().getPort() == JTT1078ServerProperties.getLivePort()) {
            ch.pipeline().addLast(new IdleStateHandler(0, 60, 0, TimeUnit.SECONDS))
                    .addLast(new HttpServerCodec())
                    .addLast(new HttpObjectAggregator(65536))
                    .addLast(INSTANCE);
        } else if (ch.localAddress().getPort() == JTT1078ServerProperties.getPort()){
            ch.pipeline().addLast(new IdleStateHandler(5, 0, 0, TimeUnit.SECONDS))
                    .addLast(new JTT1078FrameDecoder()).addLast(new VideoMessageHandler())
                    .addLast(new AudioMessageHandler()).addLast(exceptionHandler);
        }
    }
}
