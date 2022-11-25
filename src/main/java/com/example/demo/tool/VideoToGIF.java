package com.example.demo.tool;

import ws.schild.jave.Encoder;
import ws.schild.jave.EncoderException;
import ws.schild.jave.MultimediaObject;
import ws.schild.jave.encode.EncodingAttributes;
import ws.schild.jave.encode.VideoAttributes;
import ws.schild.jave.info.MultimediaInfo;
import ws.schild.jave.info.VideoInfo;
import ws.schild.jave.info.VideoSize;

import java.io.File;
import java.util.Arrays;

public class VideoToGIF {

    //视频输出格式可转gif，还可转换mp3音频
    private static final String outputFormat = "gif";

    /**
     * 获得转化后的文件名
     *
     * @param sourceFilePath : 源视频文件路径
     */
    public static String getNewFileName(String sourceFilePath) {
        File source = new File(sourceFilePath);
        String fileName = source.getName().substring(0, source.getName().lastIndexOf("."));
        return fileName + "." + outputFormat;
    }

    /**
     * 转化音频格式
     *
     * @param sourceFilePath : 源视频文件路径
     * @param targetFilePath : 目标gif文件路径
     */
    public static void transform(String sourceFilePath, String targetFilePath) {
        File source = new File(sourceFilePath);
        File target = new File(targetFilePath);
        try {
            //获得原视频的分辨率
            MultimediaObject mediaObject = new MultimediaObject(source);
            System.out.println("mediaObject.getInfo() = " + mediaObject.getInfo());
            MultimediaInfo multimediaInfo = mediaObject.getInfo();
            VideoInfo videoInfo = multimediaInfo.getVideo();
            VideoSize sourceSize = videoInfo.getSize();
            //设置视频属性
            VideoAttributes video = new VideoAttributes();
            video.setCodec(outputFormat);
            //设置视频帧率 正常为10 ，值越大越流畅
            video.setFrameRate(10);
            //设置视频分辨率
            VideoSize targetSize = new VideoSize(sourceSize.getWidth() / 5, sourceSize.getHeight() / 5);
            video.setSize(targetSize);
            //设置转码属性
            EncodingAttributes attrs = new EncodingAttributes();
            attrs.setVideoAttributes(video);
            // 音频转换格式类
            Encoder encoder = new Encoder();
            encoder.encode(mediaObject, target, attrs);
            System.out.println("转换完成...");
        } catch (EncoderException e) {
            e.printStackTrace();
        }
    }

    /**
     * 批量转化视频格式
     *
     * @param sourceFolderPath : 源视频文件夹路径
     * @param targetFolderPath : 目标gif文件夹路径
     */
    public static void batchTransform(String sourceFolderPath, String targetFolderPath) {
        File sourceFolder = new File(sourceFolderPath);
        System.out.println("sourceFolder = " + sourceFolder);
        if (sourceFolder.list().length != 0) {
            Arrays.asList(sourceFolder.list()).forEach(e -> {
                transform(sourceFolderPath + "\\" + e, targetFolderPath + "\\" + getNewFileName(e));
            });
        }
    }

    public static void main(String[] args) {
        batchTransform("D:\\myworkplace\\test\\src\\main\\resources\\static\\video", "D:\\");
    }
}
