package com.ir6.ecommerce.service.async;

import com.ir6.ecommerce.constant.AsyncTaskStatus;
import com.ir6.ecommerce.vo.AsyncTaskInfo;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * <h1>异步任务执行监控切面</h1>
 * */
@Slf4j
@Aspect
@Component
public class AsyncTaskMonitor {

    @Autowired
    private AsyncTaskManager taskManager;

    /**
     * <h2>异步任务执行的环绕切面</h2>
     * 环绕切面让我们可以在方法执行之前和执行之后做一些 "额外" 的操作
     * */
    @Around("execution(* com.ir6.ecommerce.service.async.AsyncServiceImpl.*(..))")
    public Object taskHandle(ProceedingJoinPoint pjp) {
        String taskId = pjp.getArgs()[1].toString();
        AsyncTaskInfo taskInfo = taskManager.getTaskInfo(taskId);
        taskInfo.setStatus(AsyncTaskStatus.RUNNING);
        taskManager.setTaskInfo(taskInfo);
        log.info("AsyncTaskMonitor is monitoring async task: [{}]", taskId);

        AsyncTaskStatus status;
        Object result = null;
        try {
            result = pjp.proceed();
            status = AsyncTaskStatus.SUCCESS;
        } catch (Throwable ex) {
            status = AsyncTaskStatus.FAILED;
            log.error("AsyncTaskMonitor: async task [{}] is failed, Error Info: [{}]", taskId, ex.getMessage(), ex);
        }
        taskInfo.setEndTime(new Date());
        taskInfo.setStatus(status);
        taskInfo.setTotalTime(String.valueOf(taskInfo.getEndTime().getTime() - taskInfo.getStartTime().getTime()));

        taskManager.setTaskInfo(taskInfo);

        return result;
    }

}
