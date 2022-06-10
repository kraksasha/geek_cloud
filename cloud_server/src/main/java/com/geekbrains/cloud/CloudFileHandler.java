package com.geekbrains.cloud;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.Data;

import java.nio.file.Files;
import java.nio.file.Path;
@Data
public class CloudFileHandler extends SimpleChannelInboundHandler<CloudMessage> {
    private Path currentDir;

    public CloudFileHandler() {
        currentDir = Path.of("server_files");
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        ctx.writeAndFlush(new ListFiles(currentDir));
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, CloudMessage cloudMessage) throws Exception {
        if (cloudMessage instanceof FileRequest fileRequest) {
            ctx.writeAndFlush(new FileMessage(currentDir.resolve(fileRequest.getName())));
        } else if (cloudMessage instanceof FileMessage fileMessage) {
            Files.write(currentDir.resolve(fileMessage.getName()), fileMessage.getData());
            ctx.writeAndFlush(new ListFiles(currentDir));
        } else if (cloudMessage instanceof FileRequestDirectory fileRequestDirectory){
            ctx.writeAndFlush(new DirectoryMessage(currentDir.resolve(fileRequestDirectory.getName())));
        } else if (cloudMessage instanceof FileRequestDirectoryBack fileRequestDirectoryBack){
            ctx.writeAndFlush(new DirectoryMessageBack());
        } else if (cloudMessage instanceof FileRequestServ fileRequestServ){
            Path target = Path.of(fileRequestServ.getName());
            currentDir = currentDir.resolve(target);
            ctx.writeAndFlush(new ListFiles(currentDir));
        } else if (cloudMessage instanceof FileRequestServBack){
            currentDir = currentDir.getParent();
            ctx.writeAndFlush(new ListFiles(currentDir));
        }
    }

}
