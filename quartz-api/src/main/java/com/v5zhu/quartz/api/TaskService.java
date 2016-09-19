package com.v5zhu.quartz.api;


import com.v5zhu.quartz.dto.PageInfoDto;
import com.v5zhu.quartz.dto.ScheduleJobDto;

import java.util.List;

/**
 * Created by wei9.li@changhong.com on 2015/4/20.
 */
public interface TaskService {

    /**
     * 从数据库中取 区别于getAllJob
     *
     * @return
     */
    public PageInfoDto<ScheduleJobDto> getAllTask(int page, int pageSize);

    /**
     * 根据搜索内容 从数据库中取任务
     * @param content 搜索内容
     * @param page
     * @param pageSize
     * @return
     */
    public PageInfoDto<ScheduleJobDto> getTasks(String content, int page, int pageSize) throws Exception;

    /**
     * 添加到数据库中 区别于addJob
     *
     * @param jobDto
     */
    public void addTask(ScheduleJobDto jobDto) throws Exception;

    /**
     * 修改任务并保持到数据库
     * @param jobDto
     */
    public void editTask(ScheduleJobDto jobDto) throws Exception;

    /**
     * 从数据库中查询job
     *
     * @param jobId
     */
    public ScheduleJobDto getTaskById(Long jobId) throws Exception;

    /**
     * 根据ID删除定时任务
     * @param jobId
     * @throws Exception
     */
    public void delTaskById(Long jobId)throws Exception;

    /**
     * 更改任务状态
     *
     * @throws Exception
     */
    public void changeStatus(Long jobId, String cmd) throws Exception;

    /**
     * 更改任务 cron表达式
     *
     * @param jobId
     */
    public void updateCron(Long jobId) throws Exception;

    /**
     * 添加任务
     *
     * @param jobDto
     * @throws Exception
     */
    public void addJob(ScheduleJobDto jobDto) throws Exception;

    public void init() throws Exception;

    /**
     * 获取所有计划中的任务列表
     *
     * @return
     * @throws Exception
     */
    public List<ScheduleJobDto> getAllJob() throws Exception;

    /**
     * 所有正在运行的job
     *
     * @return
     * @throws Exception
     */
    public List<ScheduleJobDto> getRunningJob() throws Exception;


    /**
     * 暂停一个job
     *
     * @param jobGroup
     * @param jobName
     * @throws Exception
     */
    public void pauseJob(String jobGroup, String jobName) throws Exception;

    /**
     * 恢复一个job
     *
     * @param jobGroup
     * @param jobName
     * @throws Exception
     */
    public void resumeJob(String jobGroup, String jobName) throws Exception;

    /**
     * 删除一个job
     *
     * @param jobGroup
     * @param jobName
     * @throws Exception
     */
    public void deleteJob(String jobGroup, String jobName) throws Exception;


    /**
     * 立即执行job
     *
     * @param jobGroup
     * @param jobName
     * @throws Exception
     */
    public void runAJobNow(String jobGroup, String jobName) throws Exception;

    /**
     * 更新job时间表达式
     *
     * @param jobGroup
     * @param jobName
     * @param cronExpression
     * @throws Exception
     */
    public void updateJobCron(String jobGroup, String jobName, String cronExpression)
            throws Exception;

    /**
     * 检查表达式
     *
     * @return
     * @throws Exception
     */

    public Boolean verifyCronExpression(String cronExpression) throws Exception;
}
