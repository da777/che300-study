package com.che300.design.adapter;

/**
 * @author liujialiang
 */
public class AudioPlayer implements MediaPlayer {
    MediaAdapter mediaAdapter;

    @Override
    public void play(String audioType, String fileName) {

        //播放mp3音乐文件的内置支持
        if ("mp3".equalsIgnoreCase(audioType)) {
            System.out.println("Playing mp3 file. Name: " + fileName);
        } else if ("vlc".equalsIgnoreCase(audioType) || "mp4".equalsIgnoreCase(audioType)) {
            mediaAdapter = new MediaAdapter(audioType);
            mediaAdapter.play(audioType, fileName);
        } else {
            System.out.println("no support this file format:" + audioType);
        }

        //mediaAdapter提供了播放其他文件格式的支持

    }
}
