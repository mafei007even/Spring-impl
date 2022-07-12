package com.mafei.spring.interfaces;

import com.mafei.spring.MaFeiApplicationContext;

/**
 * @author mafei007
 * @date 2022/7/8 02:38
 */
public interface ApplicationContextAware {

    void setApplicationContext(MaFeiApplicationContext applicationContext);

}
