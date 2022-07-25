package com.ir6.ecommerce.service.async;

import com.ir6.ecommerce.constant.AsyncTaskStatus;
import com.ir6.ecommerce.goods.GoodsInfo;
import com.ir6.ecommerce.vo.AsyncTaskInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * <h1>异步任务执行管理器</h1>
 * 对异步任务进行包装管理, 记录并塞入异步任务执行信息
 * */
@Slf4j
@Component
public class AsyncTaskManager {
    /** 异步任务执行信息容器 */
    private final Map<String, AsyncTaskInfo> taskContainer = new HashMap<>(16);

    @Autowired
    private IAsyncService asyncService;

    //提交异步任务
    public AsyncTaskInfo submit(List<GoodsInfo> goodsInfos) {
        AsyncTaskInfo taskInfo = initTask();
        asyncService.asyncImportGoods(goodsInfos, taskInfo.getTaskId());
        return taskInfo;
    }

    //设置异步任务执行状态信息  --> 通过AOP横切异步任务来做状态管理
    public void setTaskInfo(AsyncTaskInfo taskInfo) {
        taskContainer.put(taskInfo.getTaskId(), taskInfo);
    }

    //获取异步任务执行状态信息
    public AsyncTaskInfo getTaskInfo(String taskId) {
        return taskContainer.get(taskId);
    }

    public AsyncTaskInfo initTask() {
        AsyncTaskInfo taskInfo = new AsyncTaskInfo();
        taskInfo.setTaskId(UUID.randomUUID().toString());
        taskInfo.setStatus(AsyncTaskStatus.STARTED);
        taskInfo.setStartTime(new Date());

        // 初始化的时候就要把异步任务执行信息放入到存储容器中
        taskContainer.put(taskInfo.getTaskId(), taskInfo);
        return taskInfo;
    }

}
