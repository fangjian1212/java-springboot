package com.fangjian.framework.utils.self.more;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import java.io.*;

/**
 * @description:
 * @author: fangjian<jian.fang@chinaredstar.com>
 * @date: Create in 11:52 2017/12/20
 * @modified by:
 */
public class Base64ImgUtil {

    /**
     * @param imgStr base64编码字符串
     * @param path   图片路径-具体到文件
     * @return
     * @Description: 将base64编码字符串转换为图片
     * @Author:
     * @CreateTime:
     */
    public static boolean generateImage(String imgStr, String path) {
        if (imgStr == null) {
            return false;
        }
        BASE64Decoder decoder = new BASE64Decoder();
        OutputStream out = null;
        try {
            //解密
            byte[] b = decoder.decodeBuffer(imgStr);
            //处理数据
            for (int i = 0; i < b.length; ++i) {
                if (b[i] < 0) {
                    b[i] += 256;
                }
            }
            out = new FileOutputStream(path);
            out.write(b);
            out.flush();
            out.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } finally {
            close(out);
        }
    }

    /**
     * @return
     * @Description: 根据图片地址转换为base64编码字符串
     * @Author:
     * @CreateTime:
     */
    public static String getImageStr(String imgFile) {
        InputStream inputStream = null;
        byte[] data = null;
        try {
            inputStream = new FileInputStream(imgFile);
            data = new byte[inputStream.available()];
            inputStream.read(data);
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            close(inputStream);
        }
        // 加密
        BASE64Encoder encoder = new BASE64Encoder();
        return encoder.encode(data);
    }


    private static void close(Closeable closeable) {
        if (null != closeable) {
            try {
                closeable.close();
            } catch (IOException e) {
            }
        }
    }

    public static void main(String[] args) {
        String strImg = getImageStr("G:/web/uploadPicture/out/artifacts/uploadPicture.war/assets/1.jpg");
        System.out.println(strImg);
        generateImage(strImg, "F:/86619-107.jpg");
    }
}
