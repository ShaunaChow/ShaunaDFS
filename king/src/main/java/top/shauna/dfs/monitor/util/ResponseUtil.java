package top.shauna.dfs.monitor.util;

import com.alibaba.fastjson.JSON;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpRequest;
import top.shauna.dfs.monitor.exceptions.NotSupportException;
import top.shauna.dfs.monitor.exceptions._404Exception;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public class ResponseUtil {
    public static Map<String,Node> map = new ConcurrentHashMap<>();

    public static String getRespLine(int code) {
        return "HTTP/1.1 "+code+" OK\r\n";
    }

    public static String getRespHeader(String body){
        StringBuilder sb = new StringBuilder();
        sb.append("Server: ShaunaWeb\r\n")
                .append("Content-Type: text/html\r\n")
                .append("Content-Length: "+body.length()+"\r\n")
                .append("Connection: keep-alive\r\n")
                .append("\r\n");
        return sb.toString();
    }

    public static String getBody(HttpRequest request) throws Exception {
        HttpMethod method = request.method();
        if ("GET".equalsIgnoreCase(method.toString())){
            String uri = request.uri();
            String methodName;
            String args;
            if (uri.contains("?")){
                methodName = uri.substring(0,uri.indexOf("?"));
                args = uri.substring(uri.indexOf("?")+1);
            }else{
                methodName = uri;
                args = "";
            }
            if (map.containsKey(methodName)) {
                Node node = map.get(methodName);
                return JSON.toJSONString(getRes(node, args));
            }else{
                throw new _404Exception("404");
            }
        }else if("POST".equalsIgnoreCase(method.toString())){
            throw new NotSupportException("暂时不支持的请求方式");
        }else{
            throw new NotSupportException("不支持的请求方式");
        }
    }

    private static Object getRes(Node node, String args) throws InvocationTargetException, IllegalAccessException {
        /** 按照参数名称注入参数内容还有待完善！！可参考Spring AOP字节码操作！！！ **/
        Method method = node.method;
        if(args==null||args.equals("")){
            return method.invoke(node.obj);
        }
        Parameter[] parameters = method.getParameters();
        Class<?>[] parameterTypes = method.getParameterTypes();
        String[] argSplit = args.split("&");
        Object[] pathValues = new Object[parameters.length];
        for (int i=0;i<pathValues.length;i++){
            String s = argSplit[i];
            if (parameterTypes[i]==String.class)  {
                pathValues[i] = s.substring(s.indexOf("=")+1);
            }else {
                pathValues[i] = JSON.parseObject(s.substring(s.indexOf("=") + 1), parameterTypes[i]);
            }
        }
        return method.invoke(node.obj,pathValues);
    }

    public static class Node{
        public Method method;
        public Object obj;

        public Node(Method method, Object obj){
            this.method = method;
            this.obj = obj;
        }
    }
}
