package com.mafei.test;

import com.mafei.spring.anno.Component;

/**
 * @author mafei007
 * @date 2022/7/8 03:50
 */
@Component
public class PostService implements PostInterface {
    @Override
    public void post() {
        System.out.println("post........");
    }
}
