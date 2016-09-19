package com.v5zhu.quartz.api.dao;

import com.v5zhu.quartz.api.dao.commons.MyBatisRepository;
import com.v5zhu.quartz.api.domain.ScheduleJob;

import java.util.List;

@MyBatisRepository
public interface ScheduleJobMapper {
	int deleteByPrimaryKey(Long jobId);

	int insert(ScheduleJob record);

	int insertSelective(ScheduleJob record);

	ScheduleJob selectByPrimaryKey(Long jobId);

	int updateByPrimaryKeySelective(ScheduleJob record);

	int updateByPrimaryKey(ScheduleJob record);

	List<ScheduleJob> getAll();

    List<ScheduleJob> getTaskByContent(String content);
}