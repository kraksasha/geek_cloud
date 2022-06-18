package com.geekbrains.cloud;

import com.geekbrains.cloud.AuthMessage.AuthMessage;
import com.geekbrains.cloud.AuthMessage.AuthMessageBack;
import com.geekbrains.cloud.DirectoryMessage.DirectoryMessage;
import com.geekbrains.cloud.DirectoryMessage.FileRequestDirectory;
import com.geekbrains.cloud.DirectoryMessageBack.DirectoryMessageBack;
import com.geekbrains.cloud.DirectoryMessageBack.FileRequestDirectoryBack;
import com.geekbrains.cloud.FileRequestServ.FileRequestServ;
import com.geekbrains.cloud.FileRequestServ.FileRequestServBack;
import com.geekbrains.cloud.RegistrationMessage.RegistrationMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.Data;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Data
public class CloudFileHandler extends SimpleChannelInboundHandler<CloudMessage> {
    private Path currentDir;
    private File file;

    public CloudFileHandler() throws IOException {
    }

//    @Override
//    public void channelActive(ChannelHandlerContext ctx) throws Exception {
//        ctx.writeAndFlush(new ListFiles(currentDir));
//    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, CloudMessage cloudMessage) throws Exception {
        if (cloudMessage instanceof FileRequest fileRequest) {
            ctx.writeAndFlush(new FileMessage(currentDir.resolve(fileRequest.getName())));
        } else if (cloudMessage instanceof FileMessage fileMessage) {
            Files.write(currentDir.resolve(fileMessage.getName()), fileMessage.getData());
            ctx.writeAndFlush(new ListFiles(currentDir));
        } else if (cloudMessage instanceof FileRequestDirectory fileRequestDirectory){
            ctx.writeAndFlush(new DirectoryMessage(fileRequestDirectory.getName()));
        } else if (cloudMessage instanceof FileRequestDirectoryBack fileRequestDirectoryBack){
            ctx.writeAndFlush(new DirectoryMessageBack());
        } else if (cloudMessage instanceof FileRequestServ fileRequestServ){
            Path target = Path.of(fileRequestServ.getName());
            currentDir = currentDir.resolve(target);
            ctx.writeAndFlush(new ListFiles(currentDir));
        } else if (cloudMessage instanceof FileRequestServBack){
            currentDir = currentDir.getParent();
            ctx.writeAndFlush(new ListFiles(currentDir));
        } else if (cloudMessage instanceof AuthMessage authMessage){
            String buf[] = authMessage.getName().split(" ");
            String nickName = JdbcApp.retNickName(buf[1],buf[2]);
            ctx.writeAndFlush(new AuthMessageBack(nickName));
        } else if (cloudMessage instanceof AuthMessageClient authMessageClient){
            String mes =  "server_files/" + authMessageClient.getName();
            file = new File(mes);
            file.mkdir();
            currentDir = Path.of(mes);
            ctx.writeAndFlush(new ListFiles(currentDir));
        } else if (cloudMessage instanceof RegistrationMessage registrationMessage){
            JdbcApp.addNewData(registrationMessage.getNickname(),registrationMessage.getLogin(),registrationMessage.getPassword());
        }
    }
}