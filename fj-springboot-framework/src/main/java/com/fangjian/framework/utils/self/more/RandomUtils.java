package com.fangjian.framework.utils.self.more;

/**
 * Created by fangjian on 2016/9/24.
 */
public class RandomUtils {


    /**
     * [n,m)
     *
     * @param n
     * @param m
     * @return
     */
    public static int getRandomFromNtoM(int n, int m) {
        if (n < 0 || m < 0) {
            return 0;
        }
        if (n >= m) {
            return Math.min(n, m);
        }
        return (int) (Math.random() * (m - n) + n);
    }


}
