package com.zlx.demo.netty_http;

import java.util.concurrent.atomic.AtomicInteger;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;

public class NettyServerHttpHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		System.out.println("--- accepted client---");
		ctx.fireChannelActive();
	}

	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) {
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		cause.printStackTrace();
		ctx.close();
	}

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest msg) throws Exception {
		System.out.println(msg.toString());

		// 2.1 设置响应信息
		FullHttpResponse httpResponse = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
		// 响应内容类型
		httpResponse.headers().set("Content-Type", "text/html;charset=UTF-8");
		//响应内容
		ByteBuf buff = Unpooled.copiedBuffer("hello client".getBytes());
		httpResponse.content().writeBytes(buff);
		buff.release();
		//写入请求，并且当写入完成后关闭连接
		ctx.writeAndFlush(httpResponse).addListener(ChannelFutureListener.CLOSE);
	}
}
