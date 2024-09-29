package org.example.app.network;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import org.example.app.utils.Constants;

// Конфігурація сервера
public final class ChatServer {

	public void start() throws Exception {
		// Створюємо групи boss та worker.
		// Boss приймає підключення клієнта.
		// Worker обробляє подальшу комунікацію через з'єднання.
		EventLoopGroup bossGroup = new NioEventLoopGroup(1);
		EventLoopGroup workerGroup = new NioEventLoopGroup();

		try {
			ServerBootstrap sbst = new ServerBootstrap();
			sbst.group(bossGroup, workerGroup) // Встановлення boss та worker груп
					.channel(NioServerSocketChannel.class) // Використання NIO для нового з'єднання
					.childHandler(new ChannelInitializer<SocketChannel>() {
						@Override
						public void initChannel(SocketChannel ch) {
							ChannelPipeline pln = ch.pipeline();
							// Комунікація сокет-канал відбувається у потоках байтів.
							// Декодер рядків і кодувальник допомагають перетворенню
							// між байтами та рядком.
							pln.addLast(new StringDecoder());
							pln.addLast(new StringEncoder());
							// Обробник на сервері
							pln.addLast(new ChatServerHandler());
						}
					});
			// Стартуємо сервером
			ChannelFuture ft = sbst.bind(Constants.PORT).sync();
			System.out.println(Constants.SERVER_START_MSG);
			// Очікування, поки сокет сервера не буде закритий.
			ft.channel().closeFuture().sync();
		} finally {
			// Завершення всіх циклів обробки подій,
			// щоб завершити всі потоки.
			bossGroup.shutdownGracefully();
			workerGroup.shutdownGracefully();
		}
	}
}
