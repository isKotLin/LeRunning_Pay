package com.vigorchip.lerunning;

import android.app.Activity;
import android.os.Bundle;

/**
 * Created by wr-app1 on 2017/12/15.
 */
public class Hello extends Activity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hello);

    }
    /**
     * {378,242},  第二个密码的秘钥	245568
     * {562,901},  第三个密码的秘钥	957335
     * {555,761},  第四个密码的秘钥	932051
     * {044,289},  第五个密码的秘钥	419003
     * {221,034},  第六个密码的秘钥	338744
     * {561,765},  第七个密码的秘钥	958055
     * {324,008},  第八个密码的秘钥	203706
     * {017,201},  第九个密码的秘钥	414515
     * {387,969},  第十个密码的秘钥	12259
     * {763,245},  第11个密码的秘钥	884575
     * {811,093},  第12个密码的秘钥	676663
     * {286,379},  第13个密码的秘钥	145945
     * {656,241},  第14个密码的秘钥	799571
     * {368,067},  第15个密码的秘钥	255649
     * {590,287},  第16个密码的秘钥	961981
     * {353,190},  第17个密码的秘钥	238628
     * {242,795},  第18个密码的秘钥	381465
     * {587,249},  第19个密码的秘钥	964563
     * {379,486},  第20个密码的秘钥	244812
     * {827,045},  第21个密码的秘钥	692743
     * {699,579},  第22个密码的秘钥	820137
     * {842,131},  第23个密码的秘钥	709585
     * {358,339},  第24个密码的秘钥	233921
     * {459,139},  第25个密码的秘钥	68577
     * {336,851},  第26个密码的秘钥	223409
     * {241,967},  第27个密码的秘钥	382269
     * {887,063},  第28个密码的秘钥	760757
     * {147,171},  第29个密码的秘钥	284609
     * {189,662},  第30个密码的秘钥	306092
     */
//    private static final int[][] ARR = {{378, 242}, {562, 901}, {555, 761}, {044, 289}, {221, 034}, {561, 765}, {324, 8}, {017, 201},
//            {387, 969}, {763, 245}, {811, 93}, {286, 379}, {656, 241}, {368, 067}, {590, 287}, {353, 190}, {242, 795}, {587, 249}, {379, 486},
//            {827, 045}, {699, 579}, {842, 131}, {358, 339}, {459, 139}, {336, 851}, {241, 967}, {887, 063}, {147, 171}, {189, 662}
//    };
//
//    public ContextResult<String> acquire(Long deviceId) {
////        LOGGER.info(">>>PasswordHandler deviceId {}" + deviceId);
//        lock.lock();
//        String result;
//        try {
//            //deviceId%1000L = A                           deviceId%1000000L = B
//            Long m1H = (deviceId % 1000L * 123) % 1000L ^ (deviceId % 1000000L / 1000L);
//            //                 deviceId%1000000000L = C              deviceId % 1000000000000L / 1000000000L = D
//            Long m1L = (m1H ^ (deviceId % 1000000000L / 1000000L) ^ (deviceId % 1000000000000L / 1000000000L));
//            //mhs数组装的是密钥
//            Long[] mhs = {m1H, m1L};
//            //创建strings集合
//            List<String> strings = new ArrayList<>();
//            //将
//            strings.add(String.format("%03d", (m1H % 1000)) + String.format("%03d", (m1L % 1000)));
//            for (int[] anArr : ARR) {
//                StringBuilder mh = new StringBuilder();
//                for (int y = 0; y < anArr.length; y++) {
//                    mh.append(String.format("%03d", (mhs[y] ^ anArr[y]) % 1000));
//                }
//                strings.add(mh.toString());
//            }
//            result = JSON.toJSONString(strings);
//        } catch (Exception e) {
//            return ResultUtils.fail();
//        } finally {
//            lock.unlock();
//        }
//        return ResultUtils.success(result);
//    }
}
