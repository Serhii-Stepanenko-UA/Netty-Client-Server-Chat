package org.example.app.network;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import org.example.app.utils.Constants;

import java.util.Scanner;

// Конфігурація клієнта
public class ChatClient {

    static String input;
    static Channel channel;

    public void start(Scanner scanner, String userName) throws Exception {
        // Оскільки це клієнт не потрібна boss груп.
        // Створюємо одну групу EventLoopGroup.
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap bst = new Bootstrap();
            bst.group(group) // Встановлення EventLoopGroup для обробки всіх подій для клієнта.
                    .channel(NioSocketChannel.class) // Використання NIO для нового з'єднання
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) {
                            ChannelPipeline pln = ch.pipeline();
                            // Комунікація сокет-канал відбувається у потоках байтів.
                            // Декодер рядків і кодувальник допомагають перетворенню
                            // між байтами та рядком.
                            pln.addLast(new StringDecoder());
                            pln.addLast(new StringEncoder());
                            // Клієнтский обробник
                            pln.addLast(new ChatClientHandler());
                        }
                    });
            // Старт клієнта
            ChannelFuture ft = bst.connect(Constants.HOST,
                    Constants.PORT).sync();
            // Цикл отримання вхідних повідомлень чату від користувача,
            // а потім відправлення на сервер.
            while (scanner.hasNext()) {
                input = scanner.nextLine();
                if (input.equalsIgnoreCase("exit")) {
                    channel = ft.sync().channel();
                    channel.writeAndFlush("[" + userName + "]: " + input.toUpperCase());
                    channel.flush();
                    System.exit(0);
                } else {
                    channel = ft.sync().channel();
                    channel.writeAndFlush("[" + userName + "]: " + input);
                    channel.flush();
                }
            }
            // Очікування поки з'єднання не буде закрито.
            ft.channel().closeFuture().sync();
        } finally {
            // Завершення всіх циклів обробки подій,
            // щоб завершити всі потоки.
            group.shutdownGracefully();
        }
    }
}
