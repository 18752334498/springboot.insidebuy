package com.yucong.util;

import java.util.List;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.CuratorFrameworkFactory.Builder;
import org.apache.curator.framework.recipes.cache.NodeCache;
import org.apache.curator.framework.recipes.cache.NodeCacheListener;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCache.StartMode;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.retry.BoundedExponentialBackoffRetry;
import org.springframework.util.StringUtils;

public class ZkUtil {

    private CuratorFramework client = null;

    public ZkUtil(String zkUrl, String zkHome) {

        RetryPolicy retryPolicy = new BoundedExponentialBackoffRetry(1000, 3, 3);
        Builder builder = CuratorFrameworkFactory.builder().connectString(zkUrl).sessionTimeoutMs(10000).retryPolicy(retryPolicy);
        if (!StringUtils.isEmpty(zkUrl)) {
            builder = builder.namespace(zkHome);
        }

        client = builder.build();
        client.start();
    }

    /**
     * 
     * @Title: findDate
     * @Description: 获取节点数据
     * @param 节点路径
     * @return
     * @throws Exception
     * @return: String
     */
    public String findData(String node) throws Exception {
        if (client.checkExists().forPath(node) == null) {
            return "";
        }
        byte[] data = client.getData().forPath(node);
        return new String(data);
    }

    /**
     * 
     * @Title: findChildren
     * @Description: 获取节点子节点
     * @param 节点路径
     * @return
     * @throws Exception
     * @return: List<String>
     */
    public List<String> findChildren(String node) throws Exception {
        if (client.checkExists().forPath(node) == null) {
            return null;
        }
        List<String> children = client.getChildren().forPath(node);
        return children;
    }

    /**
     * 
     * @Title: updateNode
     * @Description: 修改节点数据
     * @param path
     * @param data
     * @throws Exception
     * @return: void
     */
    public void updateData(String path, String data) throws Exception {
        if (client.checkExists().forPath(path) == null) {
            return;
        }
        client.setData().forPath(path, data.getBytes());
    }

    /**
     * 
     * @Title: deleteNode
     * @Description: 级联删除节点
     * @param 节点路径
     * @throws Exception
     * @return: void
     */
    public void deleteNode(String node) throws Exception {
        if (client.checkExists().forPath(node) == null) {
            return;
        }
        client.delete().guaranteed().deletingChildrenIfNeeded().forPath(node);
    }

    /**
     * 
     * @Title: addDataWatcher
     * @Description: 添加数据监听
     * @param 节点路径
     * @param 操作逻辑接口
     * @throws Exception
     * @return: void
     */
    public void addDataWatcher(String node, final ZkDataOp op) throws Exception {
        final NodeCache cache = new NodeCache(client, node);
        cache.start(true);
        cache.getListenable().addListener(new NodeCacheListener() {
            public void nodeChanged() throws Exception {
                op.process(cache);
            }
        });
    }

    /**
     * 
     * @Title: addChildWatcher
     * @Description: 添加子节点监听
     * @param 节点路径
     * @param 操作逻辑接口
     * @throws Exception
     * @return: void
     */
    public void addChildWatcher(String node, final ZkChildOp op) throws Exception {
        final PathChildrenCache cache = new PathChildrenCache(this.client, node, true);
        cache.start(StartMode.POST_INITIALIZED_EVENT);
        cache.getListenable().addListener(new PathChildrenCacheListener() {
            public void childEvent(CuratorFramework client, PathChildrenCacheEvent event) throws Exception {
                op.process(cache, event);
            }
        });
    }

    public interface ZkDataOp {
        public void process(NodeCache nodeCache);
    }

    public interface ZkChildOp {
        public void process(PathChildrenCache cache, PathChildrenCacheEvent event);
    }

}
