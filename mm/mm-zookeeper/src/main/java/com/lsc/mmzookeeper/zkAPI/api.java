package com.lsc.mmzookeeper.zkAPI;


import com.lsc.mmserver.entity.ServerStatus;
import com.lsc.mmzookeeper.entity.ZkResult;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.*;

/**
 * Created by ValarMorghulis on 2019/2/27 17:41.
 */

@RestController
public class api implements Watcher {

    public static Logger log= LoggerFactory.getLogger(api.class);
    public static final String zkServerPath="129.204.34.155:2181," +
            "129.204.34.155:2182,129.204.34.155:2183";
    public static final Integer timeout=5000;

    /*
    * 以上是zookeeper连接的配置
    */


    private ZooKeeper zooKeeper;

    private ZooKeeper connect() throws Exception {      //获取与zookeeper的连接
        ZooKeeper zooKeeper=new ZooKeeper(zkServerPath, timeout, new api());
        log.warn("Connection Status: {}", zooKeeper.getState());
        new Thread().sleep(2000);
        log.warn("Connection Status: {}", zooKeeper.getState());
        return zooKeeper;
    }

    private Integer getIPWeight(ServerStatus serverStatus){   //计算ip的权重
        double load=1-(serverStatus.getCpu()+serverStatus.getRam())/2.0;
        return (int)Math.ceil(load*10);
    }

    private boolean checkPath(String path){    //检查路径是否正确
        boolean flag=true;
        for(int i=0; i<path.length(); i++){
            char ch=path.charAt(i);
            if((ch<'a' || ch>'z')&&(ch<'A' || ch>'Z')&&(ch<'0' || ch>'9')&&(ch!='/')){
                flag=false;
                break;
            }
        }
        return flag;
    }


    //获取path的子节点
    private ZkResult zkGetChildren(String path, boolean watch) throws Exception {
        ZooKeeper zooKeeper=connect();
        ZkResult zkResult=new ZkResult();
        if(zooKeeper==null){
            zkResult.setStat(false);
            zkResult.setMsg("无法连接至zookeeper");
        }else if((!path.startsWith("/"))||(path.length()==0)||(path==null)){
            zkResult.setStat(false);
            zkResult.setMsg("路径名必须以'/'开头");
        }else if(!checkPath(path)) {
            zkResult.setStat(false);
            zkResult.setMsg("路径名必须只含英文字母大小写和数字");
        }else {
            zkResult.setStat(true);
            try {
                List<String> children = zooKeeper.getChildren(path, watch);
                zkResult.setData(children);
                zkResult.setMsg("成功获取该节点的子节点");
            } catch (KeeperException.NoNodeException r) {
                zkResult.setMsg("该节点不存在");
            }
        }
        zooKeeper.close();
        return zkResult;
    }


    //获取节点的数据
    private ZkResult zkGetData(String path, boolean watch) throws Exception {
        ZooKeeper zooKeeper=connect();
        ZkResult zkResult=new ZkResult();
        if(zooKeeper==null){
            zkResult.setStat(false);
            zkResult.setMsg("无法连接至zookeeper");
        }else if(!path.startsWith("/")){
            zkResult.setStat(false);
            zkResult.setMsg("路径名必须以'/'开头");
        }else if(!checkPath(path)) {
            zkResult.setStat(false);
            zkResult.setMsg("路径名必须只含英文字母大小写和数字");
        }else {
            zkResult.setStat(true);
            try {
                String data = new String(zooKeeper.getData(path, watch, null));
                List<String> ds=new ArrayList<String>();
                ds.add(data);
                zkResult.setData(ds);
                zkResult.setMsg("成功获取该节点的数据");
            } catch (KeeperException.NoNodeException r) {
                zkResult.setMsg("该节点不存在");
            }
        }
        zooKeeper.close();
        return zkResult;
    }



    //获取path下最优服务器的ip:端口号
    @RequestMapping("/zkGetBestServer")
    public ZkResult zkGetBestServer(String path) throws Exception {

//        ServerStatus s1=new ServerStatus(0.03, 0.23);
//        ServerStatus s2=new ServerStatus(0.43534, 0.34234);
//        ServerStatus s3=new ServerStatus(0.78567, 0.8978978);

        ZkResult zkResult=new ZkResult();
        ZkResult getSer=zkGetChildren(path, false);  //获取路径下的所有服务器的节点名
        if(!getSer.getStat()){
            zkResult.setStat(false);
            zkResult.setMsg("获取服务器失败");
        }
        else {
            List<String> serName = getSer.getData();
            if(serName.size()==0||serName==null){
                zkResult.setStat(true);
                zkResult.setMsg("当前zookeeper下没有已注册的服务器");
            }
            else {
                List<String> serIP=new ArrayList<String>();
                for (String name: serName){     //遍历节点名获取ip
                    ZkResult serData=zkGetData(path+"/"+name, false);
                    if(!serData.getStat()){
                        continue;
                    }
                    else {
                        serIP.add(serData.getData().get(0));
                    }
                }
                if(serIP.size()==0){
                    zkResult.setStat(false);
                    zkResult.setMsg("获取服务器ip失败");
                }
                else {
                    Map<String, Integer> serverMap=new HashMap<String, Integer>();
                    for (String ip:serIP){
                        serverMap.put(ip, getIPWeight(getIPStatus(ip)));
                        /*
                        * getIPStatus(String ip) 是mmserver里的接口
                        * 参数是String ip
                        * 返回值是ServerStatus，这个类在com.lsc.mmserver.entity.ServerStatus里
                        * 负责将所给ip对应服务器的cpu利用率和内存利用率返回
                        */
                    }

//                    serverMap.put(serIP.get(0), getIPWeight(s1));
//                    serverMap.put(serIP.get(1), getIPWeight(s2));
//                    serverMap.put(serIP.get(2), getIPWeight(s3));

                    Set<String> keySet=serverMap.keySet();
                    List<String> serList =new ArrayList<String>();
                    Iterator<String> iterator=keySet.iterator();
                    while(iterator.hasNext()){
                        String ip=iterator.next();
                        Integer weight=serverMap.get(ip);
                        for (int i=0; i<weight; i++)
                            serList.add(ip);
                    }
                    java.util.Random random=new java.util.Random();
                    int randomPos =random.nextInt(serList.size());
                    zkResult.setStat(true);
                    List<String> server=new ArrayList<String>();
                    server.add(serList.get(randomPos));
                    zkResult.setData(server);
                    zkResult.setMsg("成功返回最优服务器的ip");
                }
            }
        }
        return zkResult;
    }

    @Override
    public void process(WatchedEvent watchedEvent) {
        log.warn("Watch Event:{}", watchedEvent);
    }
}
