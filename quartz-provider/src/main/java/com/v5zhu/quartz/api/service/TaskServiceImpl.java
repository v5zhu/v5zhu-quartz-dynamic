package com.v5zhu.quartz.api.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.v5zhu.quartz.api.QuartzJobFactory;
import com.v5zhu.quartz.api.QuartzJobFactoryDisallowConcurrentExecution;
import com.v5zhu.quartz.api.TaskService;
import com.v5zhu.quartz.api.dao.ScheduleJobMapper;
import com.v5zhu.quartz.api.domain.ScheduleJob;
import com.v5zhu.quartz.dto.PageInfoDto;
import com.v5zhu.quartz.dto.ScheduleJobDto;
import org.quartz.*;
import org.quartz.impl.matchers.GroupMatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springside.modules.mapper.BeanMapper;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;


/**
 * @author wei9.li@changhong.com
 * @Description: 计划任务管理
 * @date 2015年4月20日 下午2:43:54
 */
@SuppressWarnings("SpringJavaAutowiringInspection")
public class TaskServiceImpl implements TaskService {
    public final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private SchedulerFactoryBean schedulerFactoryBean;

    @Autowired
    private ScheduleJobMapper scheduleJobMapper;

    /**
     * 从数据库中取 区别于getAllJob
     *
     * @return
     */
    @Override
    public PageInfoDto<ScheduleJobDto> getAllTask(int page, int pageSize) {
        //获取第1页，10条内容，默认查询总数count
        PageHelper.startPage(page, pageSize);
        List<ScheduleJob> jobs = scheduleJobMapper.getAll();
        //分页实现
        //或者使用PageInfo类（下面的例子有介绍）
        PageInfo<ScheduleJob> pageInfo = new PageInfo<ScheduleJob>(jobs);

        List<ScheduleJobDto> scheduleJobDtos = BeanMapper.mapList(pageInfo.getList(), ScheduleJobDto.class);
        PageInfoDto<ScheduleJobDto> pageScheduleJob= BeanMapper.map(pageInfo, PageInfoDto.class);
        pageScheduleJob.setList(scheduleJobDtos);

        return pageScheduleJob;
    }

    @Override
    public PageInfoDto<ScheduleJobDto> getTasks(String jobName, int page, int pageSize) throws Exception {
        //获取第1页，10条内容，默认查询总数count
        PageHelper.startPage(page, pageSize);
        List<ScheduleJob> jobs = scheduleJobMapper.getTaskByContent(jobName);
        //分页实现
        //或者使用PageInfo类（下面的例子有介绍）
        PageInfo<ScheduleJob> pageInfo = new PageInfo<ScheduleJob>(jobs);

        List<ScheduleJobDto> scheduleJobDtos = BeanMapper.mapList(pageInfo.getList(), ScheduleJobDto.class);
        PageInfoDto<ScheduleJobDto> pageScheduleJob= BeanMapper.map(pageInfo, PageInfoDto.class);
        pageScheduleJob.setList(scheduleJobDtos);

        return pageScheduleJob;

    }

    /**
     * 添加到数据库中 区别于addJob
     */
    @Override
    public void addTask(ScheduleJobDto jobDto) {
        ScheduleJob job = BeanMapper.map(jobDto, ScheduleJob.class);
        job.setCreateTime(new Date());
        job.setJobStatus("0");
        scheduleJobMapper.insertSelective(job);
    }

    @Override
    public void editTask(ScheduleJobDto jobDto) throws Exception {
        ScheduleJob job = scheduleJobMapper.selectByPrimaryKey(jobDto.getJobId());
        Date date = job.getCreateTime();
        String jobStatus = job.getJobStatus();

        BeanMapper.copy(jobDto, job);
        job.setCreateTime(date);
        job.setJobStatus(jobStatus);
        job.setUpdateTime(new Date());
        scheduleJobMapper.updateByPrimaryKey(job);
    }

    /**
     * 从数据库中查询job
     */
    @Override
    public ScheduleJobDto getTaskById(Long jobId) {
        return BeanMapper.map(scheduleJobMapper.selectByPrimaryKey(jobId), ScheduleJobDto.class);
    }

    @Override
    public void delTaskById(Long jobId) throws Exception {
        scheduleJobMapper.deleteByPrimaryKey(jobId);
    }

    /**
     * 更改任务状态
     *
     * @throws Exception
     */
    public void changeStatus(Long jobId, String cmd) {
        ScheduleJob job = scheduleJobMapper.selectByPrimaryKey(jobId);
        if (job == null) {
            return;
        }
        if ("stop".equals(cmd)) {
            deleteJob(job);
            job.setJobStatus(ScheduleJob.STATUS_NOT_RUNNING);
        } else if ("start".equals(cmd)) {
            job.setJobStatus(ScheduleJob.STATUS_RUNNING);
            addJob(job);
        }
        scheduleJobMapper.updateByPrimaryKeySelective(job);
    }

    /**
     * 更改任务 cron表达式
     *
     * @throws Exception
     */
    @Override
    public void updateCron(Long jobId) {
        ScheduleJob job = scheduleJobMapper.selectByPrimaryKey(jobId);
        if (job == null) {
            return;
        }
//        job.setCronExpression(cron);
        if (ScheduleJob.STATUS_RUNNING.equals(job.getJobStatus())) {
            updateJobCron(job);
        }
//        scheduleJobMapper.updateByPrimaryKeySelective(job);
    }

    /**
     * 添加任务
     *
     * @param job
     * @throws Exception
     */
    private void addJob(ScheduleJob job) {
        if (job == null || !ScheduleJob.STATUS_RUNNING.equals(job.getJobStatus())) {
            return;
        }
        Scheduler scheduler = schedulerFactoryBean.getScheduler();
        logger.debug(scheduler + ".......................................................................................add");
        TriggerKey triggerKey = TriggerKey.triggerKey(job.getJobName(), job.getJobGroup());
        try {
            CronTrigger trigger = (CronTrigger) scheduler.getTrigger(triggerKey);

            // 不存在，创建一个
            if (null == trigger) {
                Class clazz = ScheduleJob.CONCURRENT_IS.equals(job.getIsConcurrent()) ? QuartzJobFactory.class : QuartzJobFactoryDisallowConcurrentExecution.class;

                JobDetail jobDetail = JobBuilder.newJob(clazz).withIdentity(job.getJobName(), job.getJobGroup()).build();

                jobDetail.getJobDataMap().put("scheduleJob", job);

                CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(job.getCronExpression());

                trigger = TriggerBuilder.newTrigger().withIdentity(job.getJobName(), job.getJobGroup()).withSchedule(scheduleBuilder).build();

                scheduler.scheduleJob(jobDetail, trigger);
            } else {
                // Trigger已存在，那么更新相应的定时设置
                CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(job.getCronExpression());

                // 按新的cronExpression表达式重新构建trigger
                trigger = trigger.getTriggerBuilder().withIdentity(triggerKey).withSchedule(scheduleBuilder).build();

                // 按新的trigger重新设置job执行
                scheduler.rescheduleJob(triggerKey, trigger);
            }
        } catch (SchedulerException e) {
            logger.error(e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void addJob(ScheduleJobDto job) {
        addJob(BeanMapper.map(job, ScheduleJob.class));
    }

    @Override
    @PostConstruct
    public void init() throws Exception {

        Scheduler scheduler = schedulerFactoryBean.getScheduler();

        // 这里获取任务信息数据
        List<ScheduleJob> jobList = scheduleJobMapper.getAll();

        for (ScheduleJob job : jobList) {
            addJob(job);
        }
    }

    /**
     * 获取所有计划中的任务列表
     *
     * @return
     * @throws SchedulerException
     */
    @Override
    public List<ScheduleJobDto> getAllJob() {
        Scheduler scheduler = schedulerFactoryBean.getScheduler();
        GroupMatcher<JobKey> matcher = GroupMatcher.anyJobGroup();
        try {
            Set<JobKey> jobKeys = scheduler.getJobKeys(matcher);
            List<ScheduleJob> jobList = new ArrayList<ScheduleJob>();
            for (JobKey jobKey : jobKeys) {
                List<? extends Trigger> triggers = scheduler.getTriggersOfJob(jobKey);
                for (Trigger trigger : triggers) {
                    ScheduleJob job = new ScheduleJob();
                    job.setJobName(jobKey.getName());
                    job.setJobGroup(jobKey.getGroup());
                    job.setDescription("触发器:" + trigger.getKey());
                    Trigger.TriggerState triggerState = scheduler.getTriggerState(trigger.getKey());
                    job.setJobStatus(triggerState.name());
                    if (trigger instanceof CronTrigger) {
                        CronTrigger cronTrigger = (CronTrigger) trigger;
                        String cronExpression = cronTrigger.getCronExpression();
                        job.setCronExpression(cronExpression);
                    }
                    jobList.add(job);
                }
            }
            return BeanMapper.mapList(jobList, ScheduleJobDto.class);
        } catch (SchedulerException e) {
            e.printStackTrace();

        }
        return null;
    }

    /**
     * 所有正在运行的job
     *
     * @return
     * @throws SchedulerException
     */
    public List<ScheduleJobDto> getRunningJob() {
        Scheduler scheduler = schedulerFactoryBean.getScheduler();
        try {
            List<JobExecutionContext> executingJobs = scheduler.getCurrentlyExecutingJobs();
            List<ScheduleJob> jobList = new ArrayList<ScheduleJob>(executingJobs.size());
            for (JobExecutionContext executingJob : executingJobs) {
                ScheduleJob job = new ScheduleJob();
                JobDetail jobDetail = executingJob.getJobDetail();
                JobKey jobKey = jobDetail.getKey();
                Trigger trigger = executingJob.getTrigger();
                job.setJobName(jobKey.getName());
                job.setJobGroup(jobKey.getGroup());
                job.setDescription("触发器:" + trigger.getKey());
                Trigger.TriggerState triggerState = scheduler.getTriggerState(trigger.getKey());
                job.setJobStatus(triggerState.name());
                if (trigger instanceof CronTrigger) {
                    CronTrigger cronTrigger = (CronTrigger) trigger;
                    String cronExpression = cronTrigger.getCronExpression();
                    job.setCronExpression(cronExpression);
                }
                jobList.add(job);
            }
            return BeanMapper.mapList(jobList, ScheduleJobDto.class);

        } catch (SchedulerException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 暂停一个job
     *
     * @param jobGroup
     * @param jobName
     * @throws SchedulerException
     */
    @Override
    public void pauseJob(String jobGroup, String jobName) throws Exception {
        Scheduler scheduler = schedulerFactoryBean.getScheduler();
        JobKey jobKey = JobKey.jobKey(jobGroup, jobName);
        try {
            scheduler.pauseJob(jobKey);
        } catch (SchedulerException e) {
            e.printStackTrace();
            String[] infos = {jobGroup, jobName, e.getMessage()};
            logger.error("停止任务:group [{}],name [{}] 失败,异常信息[{}]", infos);
            throw new Exception("停止任务失败");
        }
    }

    /**
     * 暂停一个job
     *
     * @param scheduleJob
     * @throws Exception
     */
    private void pauseJob(ScheduleJobDto scheduleJob) throws Exception {
        pauseJob(scheduleJob.getJobName(), scheduleJob.getJobGroup());
    }

    ;

    /**
     * 恢复一个job
     *
     * @param jobGroup
     * @param jobName
     * @throws SchedulerException
     */
    public void resumeJob(String jobGroup, String jobName) {
        Scheduler scheduler = schedulerFactoryBean.getScheduler();
        JobKey jobKey = JobKey.jobKey(jobName, jobGroup);
        try {
            scheduler.resumeJob(jobKey);
        } catch (SchedulerException e) {
            //todo throw coreException
            e.printStackTrace();
        }
    }

    /**
     * 删除一个job
     *
     * @param job
     * @throws SchedulerException
     */
    public void deleteJob(ScheduleJob job) {
        deleteJob(job.getJobGroup(), job.getJobName());
    }

    /**
     * 删除一个job
     *
     * @param jobGroup
     * @param jobName
     * @throws SchedulerException
     */
    @Override
    public void deleteJob(String jobGroup, String jobName) {
        Scheduler scheduler = schedulerFactoryBean.getScheduler();
        JobKey jobKey = JobKey.jobKey(jobName, jobGroup);
        try {
            scheduler.deleteJob(jobKey);
            logger.info("任务分组[{}],任务名称 = [{}]------------------已停止", jobGroup, jobName);
        } catch (SchedulerException e) {
            //todo throw coreException
            e.printStackTrace();
        }

    }

    /**
     * 立即执行job
     *
     * @param jobGroup
     * @param jobName
     * @throws SchedulerException
     */
    @Override
    public void runAJobNow(String jobGroup, String jobName) {
        Scheduler scheduler = schedulerFactoryBean.getScheduler();
        JobKey jobKey = JobKey.jobKey(jobName, jobGroup);
        try {
            scheduler.triggerJob(jobKey);
        } catch (SchedulerException e) {
            //todo throw coreException
            e.printStackTrace();
        }
    }

    /**
     * 更新job时间表达式
     *
     * @param scheduleJob
     * @throws SchedulerException
     */
    public void updateJobCron(ScheduleJob scheduleJob) {
        Scheduler scheduler = schedulerFactoryBean.getScheduler();

        TriggerKey triggerKey = TriggerKey.triggerKey(scheduleJob.getJobName(), scheduleJob.getJobGroup());

        CronTrigger trigger = null;
        try {
            trigger = (CronTrigger) scheduler.getTrigger(triggerKey);
            CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(scheduleJob.getCronExpression());
            trigger = trigger.getTriggerBuilder().withIdentity(triggerKey).withSchedule(scheduleBuilder).build();
            scheduler.rescheduleJob(triggerKey, trigger);
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }

    /**
     * 更新job时间表达式
     *
     * @param jobGroup
     * @param jobName
     * @param cronExpression
     * @throws SchedulerException
     */
    @Override
    public void updateJobCron(String jobGroup, String jobName, String cronExpression) {
        Scheduler scheduler = schedulerFactoryBean.getScheduler();

        TriggerKey triggerKey = TriggerKey.triggerKey(jobName, jobGroup);

        CronTrigger trigger = null;
        try {
            trigger = (CronTrigger) scheduler.getTrigger(triggerKey);
            CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(cronExpression);

            trigger = trigger.getTriggerBuilder().withIdentity(triggerKey).withSchedule(scheduleBuilder).build();

            scheduler.rescheduleJob(triggerKey, trigger);
        } catch (SchedulerException e) {
            e.printStackTrace();
        }


    }

    @Override
    public Boolean verifyCronExpression(String cronExpression) {
        try {
            CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(cronExpression);
        } catch (Exception e) {
            logger.error("cron表达式有误，不能被解析！");
            return false;
        }
        return true;
    }

    public static void main(String[] args) {
        CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule("xxxxx");
    }
}
