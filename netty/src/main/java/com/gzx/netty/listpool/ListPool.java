package com.gzx.netty.listpool;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author GZX
 * @Description
 * @Date 2020/5/25 17:02
 * @Version V1.0
 */
public class ListPool {

    private List[] listPool;

    private int currentIdx;
    private int capacity;

    // 当前缓存剩余的list数量
    private int count;


    public ListPool(int catacity) {
        this.listPool = new List[catacity];
        for (int i = 0; i < listPool.length; ++i) {
            listPool[i] = new ArrayList();
        }
        capacity = catacity;
        currentIdx = catacity - 1;
        count = catacity;
    }

    public List getListFormPool() {
        if (count == 0) {
            return new ArrayList();
        }
        --count;

        // 防止创建新的List的时候，添加到listPool的时候导致角标越界异常
        int idx = (currentIdx - 1) & (capacity - 1);
        List list = listPool[idx];
        currentIdx = idx;
        return list;
    }

    public void cacheList2Pool(List cache) {
        cache.clear();
        int idx = currentIdx;
        listPool[idx] = cache;
        currentIdx = (idx + 1) & (capacity - 1);
        ++count;
    }


}
