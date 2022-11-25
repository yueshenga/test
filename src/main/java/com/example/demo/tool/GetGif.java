package com.example.demo.tool;

import com.madgag.gif.fmsware.AnimatedGifEncoder;
import lombok.SneakyThrows;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;

public class GetGif {

    //照片合成Gif
    @SneakyThrows
    public static void getGif() {
        BufferedImage image1 = ImageIO.read(new File("D:\\myworkplace\\test\\src\\main\\resources\\static\\image\\bg1.jpeg"));
        BufferedImage image2 = ImageIO.read(new File("D:\\myworkplace\\test\\src\\main\\resources\\static\\image\\bg2.jpeg"));
        BufferedImage image3 = ImageIO.read(new File("D:\\myworkplace\\test\\src\\main\\resources\\static\\image\\bg3.jpeg"));
        AnimatedGifEncoder e = new AnimatedGifEncoder();
        //生成的图片路径
        e.start(new FileOutputStream("D:\\myworkplace\\test\\src\\main\\resources\\static\\image\\gif.gif"));
        //图片宽高
        e.setSize(1800, 1000);
        //图片之间间隔时间
        e.setDelay(400);
        //重复次数 0表示无限重复 默认不重复
        e.setRepeat(0);
        //添加图片
        e.addFrame(image1);
        e.addFrame(image2);
        e.addFrame(image3);
        e.finish();
        System.out.println("生成Gif图片成功！");
    }

    //自定义GIF动图
    public static void getCustomerGif(){
        AnimatedGifEncoder encoder = new AnimatedGifEncoder();
        encoder.start("D:\\myworkplace\\test\\src\\main\\resources\\static\\image\\gif.gif");
        encoder.setTransparent(Color.WHITE);
        encoder.setRepeat(0);
        encoder.setDelay(50);
        BufferedImage img = new BufferedImage(200, 180, BufferedImage.TYPE_3BYTE_BGR);
        Graphics2D g2d = img.createGraphics();
        for (int i = 0; i < 100; i++) {
            g2d.setColor(Color.WHITE);
            g2d.fillRect(0, 0, 200, 180);
            g2d.setColor(Color.BLUE);
            g2d.drawOval(0, i, 120, 120);
            encoder.addFrame(img);
        }
        g2d.dispose();
        encoder.finish();
        System.out.println("生成自定义Gif图片成功！");
    }

    public static void main(String[] args) {
//        getGif();
        getCustomerGif();
    }
}
