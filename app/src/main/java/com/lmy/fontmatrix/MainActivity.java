package com.lmy.fontmatrix;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import java.io.InputStream;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        drawString("叶子。￥○◆");
    }

    private byte[][] arr;//返回的二维数组
    private boolean[][] arr1;
    int all_16_32 = 16;//16*16
    int all_2_4 = 2;//一个汉字等于两个字节gbk
    int all_32_128 = 32;//汉字解析之后所占字节数
    int all_ascii_width = 8;//ascii 8*16
    int all_ascii_height = 16;//ascii 8*16
    int all_sscii_16 = 16;//字母解析8*16所占字节数

    /**
     * 解析成点阵
     * @param str
     * @return
     */
    public byte[][] drawString(String str) {
        byte[] data = null;
        int[] code = null;
        int byteCount;
        int lCount;
        for (int i = 0; i < str.length(); i++) {
            if (str.charAt(i) < 0xff) {
                        // 字母
                arr = new byte[all_ascii_height][all_ascii_width];
                data = read(str.charAt(i));
                byteCount = 0;
                for (int line = 0; line < 16; line++) {
                    lCount = 0;
                    for (int k = 0; k < 1; k++) {
                        for (int j = 0; j < 8; j++) {
                            if (((data[byteCount] >> (7 - j)) & 0x1) == 1) {
                                arr[line][lCount] = 1;
                                System.out.print("●");
                            } else {
                                System.out.print("○");
                                arr[line][lCount] = 0;
                            }
                            lCount++;
                        }
                        byteCount++;
                    }
                    System.out.println();
                }

            } else {
                arr = new byte[all_16_32][all_16_32];
                code = getByteCode(str.substring(i, i + 1));
                data = read(code[0], code[1]);
                byteCount = 0;
                for (int line = 0; line < all_16_32; line++) {
                    lCount = 0;
                    for (int k = 0; k < all_2_4; k++) {
                        for (int j = 0; j < 8; j++) {
                            if (((data[byteCount] >> (7 - j)) & 0x1) == 1) {
                                arr[line][lCount] = 1;
                                System.out.print("●");
                            } else {
                                System.out.print("○");
                                arr[line][lCount] = 0;
                            }
                            lCount++;
                        }
                        byteCount++;
                    }
                    System.out.println();
                }
            }
        }

        return arr;
    }

    public boolean[][] drawString_(String str) {
        byte[] data = null;
        int[] code = null;
        int byteCount;
        int lCount;
        arr1 = new boolean[all_16_32][all_16_32];
        for (int i = 0; i < str.length(); i++) {
            if (str.charAt(i) < 0x80) {
                continue;
            }
            code = getByteCode(str.substring(i, i + 1));
            data = read(code[0], code[1]);
            byteCount = 0;
            for (int line = 0; line < all_16_32; line++) {
                lCount = 0;
                for (int k = 0; k < all_2_4; k++) {
                    for (int j = 0; j < 8; j++) {
                        if (((data[byteCount] >> (7 - j)) & 0x1) == 1) {
                            arr1[line][lCount] = true;
                            System.out.print("*");
                        } else {
                            System.out.print(" ");
                            arr1[line][lCount] = false;
                        }
                        lCount++;
                    }
                    byteCount++;
                }
                System.out.println();
            }
        }
        return arr1;
    }

    /**
     * 读取中非汉字
     *
     * @param char_
     * @return
     */
    protected byte[] read(char char_) {
        byte[] data = null;
        int ascii = (int) char_;
        try {
            data = new byte[all_sscii_16];//定义缓存区大小
            InputStream in = getResources().openRawResource(R.raw.asc16);//打开字库
            long offset = ascii * 16;//asii码在字库里的偏移量
            in.skip(offset);
            in.read(data, 0, all_sscii_16);
            in.close();

        } catch (Exception e) {
            System.err.println("SORRY,THE FILE CAN'T BE READ");
        }
        return data;
    }

    /**
     * 读取字库中的汉字
     *
     * @param areaCode
     * @param posCode
     * @return
     */
    protected byte[] read(int areaCode, int posCode) {
        byte[] data = null;
        try {
            int area = areaCode - 0xa0;//区码
            int pos = posCode - 0xa0;//位码
            InputStream in = getResources().openRawResource(R.raw.hzk16);//读取中文库
            // InputStream in1=getResources().getAssets().open("hzk16");
            long offset = all_32_128 * ((area - 1) * 94 + pos - 1);//偏移量
            in.skip(offset);
            data = new byte[all_32_128];
            in.read(data, 0, all_32_128);
            in.close();
        } catch (Exception ex) {
            System.err.println("SORRY,THE FILE CAN'T BE READ");
        }
        return data;

    }

    /**
     * 获取汉字的区，位（ascii码不需要区码，位码）
     *
     * @param str
     * @return
     */
    protected int[] getByteCode(String str) {
        int[] byteCode = new int[2];
        try {
            byte[] data = str.getBytes("GBK");
            byteCode[0] = data[0] < 0 ? 256 + data[0] : data[0];
            byteCode[1] = data[1] < 0 ? 256 + data[1] : data[1];
            System.out.print("------------------------------" + data.length);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return byteCode;
    }

}
