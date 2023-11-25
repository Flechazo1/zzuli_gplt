package cn.edu.zzuli.acm.ws;

import cn.edu.zzuli.acm.entity.R;
import cn.edu.zzuli.acm.enums.InfoConst;
import cn.edu.zzuli.acm.enums.ResCodeConst;
import cn.edu.zzuli.acm.util.ContextUtils;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@ServerEndpoint("/link")
@Component
@Slf4j
public class LinkEndpoint {

    //用来存储每一个客户端对应的 ChatEndpoint 对象
    private static Map<String, LinkEndpoint> onLinkClients = new ConcurrentHashMap<>();

    //当前对应的 session 对象
    private Session session;


    /**
     * 在 websocket 连接建立时调用
     * @param session 注意这个session，并不是 HttpSession
     * @param endpointConfig
     */
    @OnOpen
    public void onOpen(Session session, EndpointConfig endpointConfig) {
        this.session = session;
        //将当前客户端 加入到我们的 map中
        onLinkClients.put(session.getId(), this);
        //向当前客户端发送初始化信息

//        ApplicationContext applicationContext = ContextUtils.getApplicationContext();
//        RedisTemplate<String, Object> redisTemplate = applicationContext.getBean("redisTemplate", RedisTemplate.class);
//        R r = new R();
//        r.add("isICPC", redisTemplate.opsForValue().get(InfoConst.IS_ICPC.KEY()))
//                .add("init", true);
//        try {
//            session.getBasicRemote().sendText(JSON.toJSONString(r));
//        } catch (IOException e) {
//            log.info("websocket 连接建立失败, error: ", e);
//        }

    }

    /**
     * 接收到 客户端发送数据时调用
     * @param message
     * @param session
     */
    @OnMessage
    public void onMessage(String message, Session session) {
        try {
            session.getBasicRemote().sendText(JSON.toJSONString(
                    //202 代表当前是心跳检测的回应。
                    R.success().setCode(ResCodeConst.HURT_CHECK.CODE())
                            .appendMsg("pong!")
            ));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 在连接关闭时 调用
     * @param session
     */
    @OnClose
    public void onClose(Session session) {
        onLinkClients.remove(session.getId());
    }

    /**
     * 连接异常的时候关闭
     * @param session
     * @param throwable
     */
    @OnError
    public void onError(Session session, Throwable throwable) {
//        try {
            System.out.println(throwable);
//            log.warn(throwable.getMessage());
            if (onLinkClients.containsKey(session.getId())) {
                onLinkClients.remove(session.getId());
            }
//            session.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

    /**
     * 对所有客户端广播这个消息。
     * @param r
     */
    public static void broadCastToAllUsers(R r) {
        r.setCode(ResCodeConst.BROADCAST.CODE());
        onLinkClients.forEach((k, v) -> {
            try {
                if (v.session.isOpen()) {
                    v.session.getBasicRemote()
                            .sendText(JSON.toJSONString(r));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

    }


}
