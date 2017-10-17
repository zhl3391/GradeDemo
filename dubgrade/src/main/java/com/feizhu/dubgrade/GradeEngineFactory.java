package com.feizhu.dubgrade;


import com.feizhu.dubgrade.chisheng.ChiShengGradeEngine;
import com.feizhu.dubgrade.xiansheng.XianShengGradeEngine;
import com.feizhu.dubgrade.xunfei.XunFeiGradeEngine;

/**
 * Created by zhouhl on 2016/11/11.
 * 引擎工厂
 */
public class GradeEngineFactory {

    public static final int TYPE_XUNFEI = 0;  //科大讯飞
    public static final int TYPE_CHISHENG = 1; //驰声
    public static final int TYPE_XIANSHENG = 2; //先声

    public static GradeEngine createGradeEngine(int type) {
        switch (type) {
            case TYPE_XUNFEI:
                return new XunFeiGradeEngine();
            case TYPE_CHISHENG:
                return new ChiShengGradeEngine();
            case TYPE_XIANSHENG:
                return new XianShengGradeEngine();
            default:
                throw new IllegalArgumentException ("类型错误！");
        }
    }
}
