package com.netty.study.pipeline.pipeline;

/**
 * @author WangChen
 * @since 2020-12-10 15:00
 **/
public class PipelineFactory {

    public static Pipeline newInstance(){
        return new DefaultPipeline();
    }
}
