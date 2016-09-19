package com.v5zhu.quartz.consumer.controller;

import com.v5zhu.quartz.api.TaskService;
import com.v5zhu.quartz.dto.PageInfoDto;
import com.v5zhu.quartz.dto.ScheduleJobDto;
import com.v5zhu.quartz.support.RetObj;
import com.v5zhu.quartz.support.spring.SpringUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;

@SuppressWarnings("SpringJavaAutowiringInspection")
@Controller
public class JobTaskController {
    // 日志记录器
    public final Logger logger = Logger.getLogger(this.getClass());
    @Autowired
    private TaskService taskService;

    @RequestMapping("tasks")
    public String taskList(HttpServletRequest request, @RequestParam(value = "name", defaultValue = "") String name ,
                           @RequestParam(value = "page",defaultValue = "1")int page) {
        PageInfoDto<ScheduleJobDto> pageInfoDto = null;
        //@RequestParam int pageNumbe,
        int pageSize = 10;
        try {
            if(StringUtils.isBlank(name)){
            pageInfoDto = taskService.getAllTask(page,pageSize);
            }else {
                //搜索任务
                pageInfoDto = taskService.getTasks(name, page, pageSize);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        request.setAttribute("taskPage", pageInfoDto);
        return "task/taskList";
    }

    @RequestMapping(value = "task", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<Object> taskList(@RequestBody ScheduleJobDto scheduleJob) {
        RetObj retObj = new RetObj();
        retObj.setCode(403);
        Boolean verified = null;
        try {
            verified = taskService.verifyCronExpression(scheduleJob.getCronExpression());
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (!verified) {
            retObj.setMsg("cron表达式有误，不能被解析！");
            return new ResponseEntity(retObj, HttpStatus.FORBIDDEN);
        }

        Object obj = null;
        try {
            if (StringUtils.isNotBlank(scheduleJob.getSpringId())) {
                obj = SpringUtils.getBean(scheduleJob.getSpringId());
            } else {
                Class clazz = Class.forName(scheduleJob.getBeanClass());
                obj = clazz.newInstance();
            }

            if (obj == null) {
                retObj.setMsg("未找到目标类！");
                return new ResponseEntity(retObj, HttpStatus.FORBIDDEN);
            } else {
                Class clazz = obj.getClass();
                Method method = null;

                method = clazz.getMethod(scheduleJob.getMethodName(), null);

                if (method == null) {
                    retObj.setMsg("未找到目标方法！");
                    return new ResponseEntity(retObj, HttpStatus.FORBIDDEN);
                }
                taskService.addTask(scheduleJob);
            }
        } catch (NoSuchBeanDefinitionException e) {
            retObj.setCode(403);
            retObj.setMsg(e.getMessage());
            return new ResponseEntity(retObj, HttpStatus.FORBIDDEN);
        } catch (Exception e) {
            e.printStackTrace();
            retObj.setCode(403);
            retObj.setMsg("保存失败，检查 name group 组合是否有重复！");
            return new ResponseEntity(retObj, HttpStatus.FORBIDDEN);
        }
        return new ResponseEntity<Object>(new OkPacket(), HttpStatus.CREATED);
    }

    //编辑任务
    @RequestMapping(value = "task/edit", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<Object> taskEdit(@RequestBody ScheduleJobDto scheduleJob) {
        RetObj retObj = new RetObj();
        retObj.setCode(403);
        Boolean verified = null;
        if (scheduleJob.getJobId() == null) {
            retObj.setMsg("任务ID为空，请重新请求");
            return new ResponseEntity<Object>(retObj, HttpStatus.FORBIDDEN);
        }
        try {
            verified = taskService.verifyCronExpression(scheduleJob.getCronExpression());
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (!verified) {
            retObj.setMsg("cron表达式有误，不能被解析！");
            return new ResponseEntity(retObj, HttpStatus.FORBIDDEN);
        }

        Object obj = null;
        try {
            if (StringUtils.isNotBlank(scheduleJob.getSpringId())) {
                obj = SpringUtils.getBean(scheduleJob.getSpringId());
            } else {
                Class clazz = Class.forName(scheduleJob.getBeanClass());
                obj = clazz.newInstance();
            }

            if (obj == null) {
                retObj.setMsg("未找到目标类！");
                return new ResponseEntity(retObj, HttpStatus.FORBIDDEN);
            } else {
                Class clazz = obj.getClass();
                Method method = null;

                method = clazz.getMethod(scheduleJob.getMethodName(), null);

                if (method == null) {
                    retObj.setMsg("未找到目标方法！");
                    return new ResponseEntity(retObj, HttpStatus.FORBIDDEN);
                }
                taskService.editTask(scheduleJob);
            }
        } catch (NoSuchBeanDefinitionException e) {
            retObj.setCode(403);
            retObj.setMsg(e.getMessage());
            return new ResponseEntity(retObj, HttpStatus.FORBIDDEN);
        } catch (Exception e) {
            e.printStackTrace();
            retObj.setCode(403);
            retObj.setMsg("保存失败，检查 name group 组合是否有重复！");
            return new ResponseEntity(retObj, HttpStatus.FORBIDDEN);
        }
        return new ResponseEntity<Object>(new OkPacket(), HttpStatus.OK);
    }

    @RequestMapping(value = "task/{jobId}/status", method = RequestMethod.PUT)
    @ResponseBody
    public ResponseEntity<Object> changeJobStatus(@PathVariable Long jobId, @RequestBody String[] cmd) throws Exception {
        RetObj retObj = new RetObj();
        retObj.setCode(403);
        if (cmd.length < 1) {
            retObj.setMsg("任务状态改变失败！");
            return new ResponseEntity<Object>(retObj, HttpStatus.FORBIDDEN);
        } else {
            taskService.changeStatus(jobId, cmd[0]);
        }
        return new ResponseEntity<Object>( new OkPacket(), HttpStatus.OK);
    }

    @RequestMapping(value = "task/{jobId}/cron", method = RequestMethod.PUT)
    @ResponseBody
    public RetObj updateCron(@PathVariable Long jobId) throws Exception {
        RetObj retObj = new RetObj();
        retObj.setCode(403);
//        Boolean verified = taskService.verifyCronExpression(cron[0]);
//        if (!verified) {
//            retObj.setMsg("cron表达式有误，不能被解析！");
//            return retObj;
//        }
//        try {
        taskService.updateCron(jobId);
//        } catch (SchedulerException e) {
//            retObj.setMsg("cron更新失败！");
//            return retObj;
//        }
        retObj.setCode(200);
        return retObj;
    }

    @RequestMapping(value = "task/{jobId}/deletion", method = RequestMethod.DELETE)
    @ResponseBody
    public RetObj deleteJob(@PathVariable Long jobId) throws Exception {
        RetObj retObj = new RetObj();
        retObj.setCode(403);

        ScheduleJobDto task = taskService.getTaskById(jobId);
        //如果任务处于运行状态，需要先停止任务
        if(task.getJobStatus() != null &&
                task.getJobStatus().equals(ScheduleJobDto.STATUS_RUNNING)){
            taskService.changeStatus(jobId, "stop");
        }

        taskService.delTaskById(jobId);
        retObj.setCode(200);
        return retObj;
    }
}
