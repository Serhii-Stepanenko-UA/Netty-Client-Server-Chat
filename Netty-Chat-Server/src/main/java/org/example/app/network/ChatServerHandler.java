package org.example.app.network;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.example.app.utils.Constants;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

// Обробка каналу на боці сервера.
public class ChatServerHandler extends SimpleChannelInboundHandler<String> {

	// Шаблон часу. HH - години, mm - хвилини, ss - секунди.
	private final static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("HH:mm:ss");
	private static int count = 1;

	// Список підключених клієнтських каналів.
	static final List<Channel> channels = new ArrayList<>();

	// Щоразу, коли клієнт підключається до сервера через канал,
	// його канал додається до списку каналів.
	@Override
	public void channelActive(final ChannelHandlerContext ctx) {
		String client = "CLIENT-" + count;
		String time = DATE_FORMAT.format(new Date()); // Час підключення
		System.out.println(client + " joined, TIME " + time + ", " + ctx);
		channels.add(ctx.channel());
        count += 1;
		for (Channel ch : channels) {
			ch.writeAndFlush("[SERVER] " + client + " has successfully connected.");
		}
	}

	// Коли повідомлення отримано від клієнта,
	// воно надсилається у всі канали (загальний чат)
	@Override
	public void channelRead0(ChannelHandlerContext ctx, String msg) {
		if (msg.equalsIgnoreCase(Constants.CLIENT_CLOSE_MSG)) {
			System.out.println("Closing connection for client - " + ctx);
			for (Channel ch : channels) {
				ch.writeAndFlush(msg);
			}
			ctx.close();
		} else {
			// new Date() - поточна дата
			String time = DATE_FORMAT.format(new Date()); // Час
			System.out.println("CLIENT - ( " + time + " ) " + msg);
			for (Channel ch : channels) {
				ch.writeAndFlush("( " + time + " ) " + msg);
			}
		}
	}

	// При винятку - канал закривається
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		System.out.println("Closing connection for client - " + ctx);
		ctx.close();
	}
}
