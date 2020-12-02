package com.netty.study.rpc.server;

/**
 * @author WangChen
 * @since 2020-12-02 15:22
 **/
public class UserFacade implements IUserFacade{

    @Override
    public String getUserName(Long id) {
        return "NettyServer getUserName id = " + id;
    }

}
