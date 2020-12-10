package com.netty.study.pipeline.pipeline;

/**
 * @author WangChen
 * @since 2020-12-10 10:06
 **/
public class DefaultPipeline implements Pipeline {

    AbstractHandler head;
    AbstractHandler tail;

    protected DefaultPipeline() {
        this.head = new HeadHandler(true, false);
        this.tail = new TailHandler(false, true);
        this.head.next = tail;
        this.tail.prev = head;
    }

    @Override
    public Pipeline addLast(AbstractHandler handler) {
        synchronized (this) {
            AbstractHandler prev = this.tail.prev;
            handler.prev = prev;
            handler.next = this.tail;
            prev.next = handler;
            this.tail.prev = handler;
        }
        return this;
    }

    @Override
    public Pipeline addFirst(AbstractHandler handler) {
        synchronized (this) {
            AbstractHandler next = this.head.next;
            handler.prev = this.head;
            handler.next = next;
            this.head.next = handler;
            next.prev = handler;
        }
        return this;
    }


    @Override
    public void invokeInbound(Object msg) {
        AbstractHandler.invokeInbound(head, msg);
    }

    @Override
    public void invokeOutbound(Object msg) {
        AbstractHandler.invokeOutbound(tail, msg);
    }


    static class HeadHandler extends AbstractHandler {

        public HeadHandler(boolean inbound, boolean outbound) {
            super(inbound, outbound);
        }

        @Override
        void invoke0(Object msg) {
            System.out.println("头节点");
        }
    }


    static class TailHandler extends AbstractHandler {

        public TailHandler(boolean inbound, boolean outbound) {
            super(inbound, outbound);
        }

        @Override
        void invoke0(Object msg) {
            System.out.println("尾节点");
        }
    }
}
