package com.zkyzn.project_manager.stories;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zkyzn.project_manager.enums.DocumentTypeEnum;
import com.zkyzn.project_manager.enums.Operator;
import com.zkyzn.project_manager.models.ProjectInfo;
import com.zkyzn.project_manager.models.ProjectPhase;
import com.zkyzn.project_manager.models.ProjectPlan;
import com.zkyzn.project_manager.services.*;
import com.zkyzn.project_manager.so.project_info.*;
import com.zkyzn.project_manager.utils.ExcelUtil;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Mr-ti
 */
@Service
public class ProjectInfoStory {

    @Resource
    private ProjectInfoService projectInfoService;

    @Resource
    private ProjectDocumentService projectDocumentService;

    @Resource
    private ProjectPlanService projectPlanService;

    @Resource
    private ProjectPhaseService projectPhaseService;

    @Resource
    private ProgressHistoryStory progressHistoryService;

    /**
     * 创建项目, 所有异常都会触发回滚
     *
     * @param req 项目请求参数
     * @return 项目编号
     */
    @Transactional(rollbackFor = Exception.class)
    public String createProject(ProjectInfoReq req) {

        // 判断项目编号是否存在，如果存在则返回错误
        if (projectInfoService.existsByProjectNumber(req.getProjectNumber())) {
            return "项目编号已存在";
        }

        //  插入项目信息数据表
        boolean insert = projectInfoService.saveProject(req);

        List<ProjectDocumentReq> projectDocumentList = req.getProjectDocumentList();
        if (insert && null != projectDocumentList) {

            // 将项目文档插入项目文档表，创建项目时，projectDocumentList中projectId为空，需要将projectDocument的projectId字段设置为projectInfo的projectId
            ProjectInfo projectInfo = projectInfoService.getByProjectNumber(req.getProjectNumber());
            Long projectId = projectInfo.getProjectId();
            // 如果文档类型是计划书，需要解析其中的任务
            for (ProjectDocumentReq projectDocument : projectDocumentList) {
                if (projectDocument.getProjectId() == null) {
                    projectDocument.setProjectId(projectId);
                    projectDocumentService.save(projectDocument);
                }
                if (DocumentTypeEnum.PROJECT_PLAN.getChineseName().equals(projectDocument.getDocumentType())) {

                    // 创建一个列表用于收集项目参与人信息
                    List<String> responsiblePersons = new ArrayList<>();

                    //根据项目计划生成项目计划和项目阶段，插入相应的数据表中
                    initPlanAndPhaseByDocument(projectDocument, projectInfo.getProjectId(), responsiblePersons);

                    // 更新项目参与人信息，将responsiblePersons列表中的字符串以逗号分隔拼接
                    projectInfo.setProjectParticipants(String.join(" ", responsiblePersons));
                    insert = projectInfoService.updateById(projectInfo);
                }
            }
        }

        //  如果插入成功则返回项目编号
        return insert ? req.getProjectNumber() : null;
    }


    /**
     * 更新项目
     *
     * @param req 项目请求参数
     * @return 项目编号
     */
    @Transactional(rollbackFor = Exception.class)
    public String updateProject(ProjectInfoReq req) {
        // 判断项目编号是否存在，如果不存在则返回错误
        if (!projectInfoService.existsByProjectNumber(req.getProjectNumber())) {
            return "项目编号不存在";
        }

        // 更新tab_project_document表
        List<ProjectDocumentReq> projectDocumentReqList = req.getProjectDocumentList();
        if (null != projectDocumentReqList) {

            // 根据Operator字段排序，D在前面，C在后面，确保先删除，再创建
            projectDocumentReqList.sort((o1, o2) -> {
                if (Operator.DELETE.getCode().equals(o1.getOperator()) && Operator.CREATE.getCode().equals(o2.getOperator())) {
                    return -1;
                } else if (Operator.CREATE.getCode().equals(o1.getOperator()) && Operator.DELETE.getCode().equals(o2.getOperator())) {
                    return 1;
                } else {
                    return 0;
                }
            });

            for (ProjectDocumentReq projectDocumentReq : projectDocumentReqList) {
                // 如果文档类型是计划书，需要解析其中的任务，把原有的项目阶段和项目任务删除
                if (DocumentTypeEnum.PROJECT_PLAN.getChineseName().equals(projectDocumentReq.getDocumentType())) {

                    // 删除项目相关的项目阶段
                    projectPhaseService.removeByProjectId(req.getProjectId());

                    // 删除项目相关的plan
                    projectPlanService.removeByProjectId(req.getProjectId());

                    if (Operator.CREATE.getCode().equals(projectDocumentReq.getOperator())) {
                        // 创建一个列表用于收集项目参与人信息
                        List<String> responsiblePersons = new ArrayList<>();

                        // 根据项目计划生成项目计划和项目阶段，插入相应的数据表中
                        initPlanAndPhaseByDocument(projectDocumentReq, req.getProjectId(), responsiblePersons);

                        // 更新项目参与人信息，将responsiblePersons列表中的字符串以逗号分隔拼接
                        req.setProjectParticipants(String.join(",", responsiblePersons));
                        projectInfoService.updateById(req);
                    }
                }

                if (Operator.CREATE.getCode().equals(projectDocumentReq.getOperator())) {
                    projectDocumentReq.setProjectId(req.getProjectId());
                    projectDocumentService.save(projectDocumentReq);
                }else if (Operator.DELETE.getCode().equals(projectDocumentReq.getOperator())) {
                    projectDocumentService.removeByProjectType(projectDocumentReq.getDocumentType());
                }
            }
        }
        // 更新tab_project_info表
        boolean insert = projectInfoService.updateById(req);

        //  如果插入成功则返回项目编号
        return insert ? req.getProjectNumber() : null;
    }

    /**
     * 根据项目编号获取项目信息
     *
     * @param projectNumber 项目编号
     * @return 项目信息
     */
    public ProjectInfoResp getProjectByProjectNumber(String projectNumber) {
        ProjectInfoResp resp = new ProjectInfoResp();

        ProjectInfo projectInfo = projectInfoService.getByProjectNumber(projectNumber);
        if (projectInfo != null) {
            //复制父类属性
            BeanUtils.copyProperties(projectInfo, resp);
            resp.setProjectDocumentList(projectDocumentService.listByProjectId(resp.getProjectId()));
        }else {
            return null;
        }

        return resp;
    }


    /**
     * 分页查询项目信息
     *
     * @param current 当前页
     * @param size    分页大小
     * @return 项目信息
     */
    public Page<ProjectInfo> pageProjectInfo(
            int current,
            int size,
            ProjectInfo condition,
            LocalDate startDateBegin,
            LocalDate startDateEnd
    ) {
        return projectInfoService.pageProject(
                (long) current,
                (long) size,
                condition,
                startDateBegin,
                startDateEnd
        );
    }

    /**
     * 删除项目
     *
     * @param projectNumber 项目编号
     * @return 是否删除成功
     */
    public Boolean deleteProjectByProjectNumber(String projectNumber) {
        // todo: 删除项目时需要关联删除一些其他的表数据

        return projectInfoService.removeByProjectNumber(projectNumber);
    }


    /**
     * 更新项目信息表中的收藏状态
     *
     * @param projectNumber 项目编号
     * @param req    请求参数，主要提取是否收藏状态
     * @return 是否收藏成功
     */
    public Boolean favoriteProject(String projectNumber, ProjectInfoReq req) {

        ProjectInfo projectInfo = projectInfoService.getByProjectNumber(projectNumber);
        if (projectInfo == null) {
            return false;
        }
        projectInfo.setIsFavorite(req.getIsFavorite());
        return projectInfoService.updateById(projectInfo);
    }

    /**
     * 批量导入项目
     *
     * @param req 项目请求参数
     * @return 是否导入成功
     */
    public Boolean importProjectBatch(ProjectImportReq req) {
        List<ProjectInfo> projectInfoList = ExcelUtil.parseProjectInfoSheet(req.getImportExcelFilePath(), "");

        // todo: 输出错误文档

        //  批量插入项目信息数据表
        return projectInfoService.saveBatch(projectInfoList);
    }

    /**
     * 获取项目详情
     *
     * @param projectNumber 项目编号
     * @return 项目详情
     */
    public ProjectDetailResp getProjectDetail(String projectNumber) {
        ProjectDetailResp resp = new ProjectDetailResp();

        // 获取项目基本信息
        ProjectInfo projectInfo = projectInfoService.getByProjectNumber(projectNumber);
        if (projectInfo == null) {
            return null;
        }

        resp.setProjectInfo(projectInfo);
        Long projectId = projectInfo.getProjectId();

        // 1. 全周期进度
        resp.setOverallProgress(calculateOverallProgress(projectId));

        // 2. 月进度
        resp.setMonthlyProgress(calculateMonthlyProgress(projectId));

        // 3. 周进度
        resp.setWeeklyProgress(calculateWeeklyProgress(projectId));

        // 4. 上周完成项数量
        resp.setLastWeekCompletedCount(projectPlanService.countLastWeekCompleted(projectId));

        // 5. 项目拖期项
        resp.setDelayedItems(calculateDelayedItems(projectId));

        // 6. 项目进度明细
        resp.setPhaseProgressList(projectPhaseService.getPhaseProgressDetails(projectId));

        // 7. 科室月进度
        resp.setDepartmentProgressList(convertDepartmentProgress(
                projectPlanService.getDepartmentProgress(projectId)
        ));

        // 8. 主要风险项 (示例数据)
        resp.setRiskItems(getRiskItems(projectId));

        // 9. 回款计划 (示例数据)
        resp.setPaymentPlans(getPaymentPlans(projectId));

        // 10. 代办事项列表
        resp.setUpcomingTasks(convertUpcomingTasks(
                projectPlanService.getUpcomingTasks(projectId, 7)
        ));

        // 11. 计划变更清单
        resp.setChangeRecords(projectPlanService.getChangeRecords(projectId));

        // 13. 项目的阶段信息列表
        resp.setPhases(projectPhaseService.getPhasesByProjectId(projectId));

        return resp;
    }

    /**
     * 计算项目进度
     * @param projectId
     * @return
     */
    private ProjectDetailResp.Progress calculateOverallProgress(Long projectId) {
        Long totalTasks = projectPlanService.countByProjectId(projectId);
        Long completedTasks = projectPlanService.countByProjectIdAndStatus(projectId, "已完成");

        ProjectDetailResp.Progress progress = new ProjectDetailResp.Progress();
        progress.setCurrentRate(calculateRate(completedTasks, totalTasks));
        progress.setDailyChangeRate(calculateDailyChange(projectId, "overall"));
        return progress;
    }

    /**
     * 计算月度进度
     * @param projectId
     * @return
     */
    private ProjectDetailResp.Progress calculateMonthlyProgress(Long projectId) {
        LocalDate now = LocalDate.now();
        YearMonth thisMonth = YearMonth.from(now);
        LocalDate firstDayOfMonth = thisMonth.atDay(1);
        LocalDate lastDayOfMonth = thisMonth.atEndOfMonth();

        Long monthlyTasks = projectPlanService.countByDateRange(projectId, firstDayOfMonth, lastDayOfMonth);
        Long monthlyCompleted = projectPlanService.countCompletedByDateRange(projectId, firstDayOfMonth, lastDayOfMonth);

        ProjectDetailResp.Progress progress = new ProjectDetailResp.Progress();
        progress.setCurrentRate(calculateRate(monthlyCompleted, monthlyTasks));
        progress.setDailyChangeRate(calculateDailyChange(projectId, "monthly"));
        return progress;
    }

    /**
     * 计算周进度
     * @param projectId
     * @return
     */
    private ProjectDetailResp.Progress calculateWeeklyProgress(Long projectId) {
        LocalDate now = LocalDate.now();
        LocalDate startOfWeek = now.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate endOfWeek = now.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));

        Long weeklyTasks = projectPlanService.countByDateRange(projectId, startOfWeek, endOfWeek);
        Long weeklyCompleted = projectPlanService.countCompletedByDateRange(projectId, startOfWeek, endOfWeek);

        ProjectDetailResp.Progress progress = new ProjectDetailResp.Progress();
        progress.setCurrentRate(calculateRate(weeklyCompleted, weeklyTasks));
        progress.setDailyChangeRate(calculateDailyChange(projectId, "weekly"));
        return progress;
    }

    /**
     * 计算延期数量
     * @param projectId
     * @return
     */
    private ProjectDetailResp.RiskItem calculateDelayedItems(Long projectId) {
        Long delayedCount = projectPlanService.countDelayedTasks(projectId);

        // 获取昨天的拖期项数量
        LocalDate yesterday = LocalDate.now().minusDays(1);
        BigDecimal yesterdayDelayed = progressHistoryService.getProgressRate(projectId, "delayed", yesterday);

        ProjectDetailResp.RiskItem item = new ProjectDetailResp.RiskItem();
        item.setCount(delayedCount);

        if (yesterdayDelayed != null && yesterdayDelayed.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal change = BigDecimal.valueOf(delayedCount)
                    .subtract(yesterdayDelayed)
                    .divide(yesterdayDelayed, 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100));
            item.setRiskDetail("较昨日变化: " + change.setScale(2, RoundingMode.HALF_UP) + "%");
        } else {
            item.setRiskDetail("无昨日对比数据");
        }

        return item;
    }

    /**
     * 计算科室月进度
     * @param numerator
     * @param denominator
     * @return
     */
    private BigDecimal calculateRate(Long numerator, Long denominator) {
        if (denominator == 0) return BigDecimal.ZERO;
        return BigDecimal.valueOf(numerator)
                .divide(BigDecimal.valueOf(denominator), 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100));
    }

    /**
     * 计算每日变化率
     * @param projectId
     * @param type
     * @return
     */
    private BigDecimal calculateDailyChange(Long projectId, String type) {
        LocalDate yesterday = LocalDate.now().minusDays(1);

        // 获取昨天的进度值
        BigDecimal yesterdayRate = progressHistoryService.getProgressRate(projectId, type, yesterday);

        // 获取今天的进度值
        BigDecimal todayRate = progressHistoryService.getProgressRate(projectId, type, LocalDate.now());

        if (yesterdayRate == null || yesterdayRate.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }

        // 计算变化率
        return todayRate.subtract(yesterdayRate)
                .divide(yesterdayRate, 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100));
    }

    /**
     * 计算科室进度
     * @param progressMap
     * @return
     */
    private List<ProjectDetailResp.DepartmentProgress> convertDepartmentProgress(
            Map<String, BigDecimal> progressMap) {

        return progressMap.entrySet().stream()
                .map(entry -> {
                    ProjectDetailResp.DepartmentProgress dp = new ProjectDetailResp.DepartmentProgress();
                    dp.setDepartment(entry.getKey());
                    dp.setProgressRate(entry.getValue());
                    return dp;
                })
                .collect(Collectors.toList());
    }

    /**
     * 计算计划变更清单
     * @param plans
     * @return
     */
    private List<ProjectDetailResp.UpcomingTask> convertUpcomingTasks(List<ProjectPlan> plans) {
        return plans.stream()
                .map(plan -> {
                    ProjectDetailResp.UpcomingTask task = new ProjectDetailResp.UpcomingTask();
                    task.setTaskContent(plan.getTaskDescription());
                    task.setResponsiblePerson(plan.getResponsiblePerson());
                    task.setEndDate(plan.getEndDate());
                    // 根据结束时间计算优先级
                    long daysUntilDue = ChronoUnit.DAYS.between(LocalDate.now(), plan.getEndDate());
                    if (daysUntilDue <= 1) {
                        task.setPriority("高");
                    } else if (daysUntilDue <= 3) {
                        task.setPriority("中");
                    } else {
                        task.setPriority("低");
                    }
                    return task;
                })
                .collect(Collectors.toList());
    }

    /**
     * 获取风险项
     * @param projectId
     * @return
     */
    private List<ProjectDetailResp.RiskItem> getRiskItems(Long projectId) {
        // todo: 实际项目中应从风险表中获取
        List<ProjectDetailResp.RiskItem> risks = new ArrayList<>();

        ProjectDetailResp.RiskItem risk1 = new ProjectDetailResp.RiskItem();
        risk1.setRiskName("技术风险");
        risk1.setRiskDetail("关键技术难点尚未突破");
        risks.add(risk1);

        ProjectDetailResp.RiskItem risk2 = new ProjectDetailResp.RiskItem();
        risk2.setRiskName("资源风险");
        risk2.setRiskDetail("关键技术人员短缺");
        risks.add(risk2);

        return risks;
    }

    /**
     * 获取回款计划
     * @param projectId
     * @return
     */
    private List<ProjectDetailResp.PaymentPlan> getPaymentPlans(Long projectId) {
        // todo: 实际项目中应从回款表中获取
        List<ProjectDetailResp.PaymentPlan> payments = new ArrayList<>();

        ProjectDetailResp.PaymentPlan plan1 = new ProjectDetailResp.PaymentPlan();
        plan1.setItemName("首付款");
        plan1.setPlannedAmount(new BigDecimal("100000.00"));
        plan1.setActualAmount(new BigDecimal("100000.00"));
        plan1.setPlannedDate(LocalDate.now().minusMonths(1));
        payments.add(plan1);

        ProjectDetailResp.PaymentPlan plan2 = new ProjectDetailResp.PaymentPlan();
        plan2.setItemName("中期款");
        plan2.setPlannedAmount(new BigDecimal("150000.00"));
        plan2.setActualAmount(null);
        plan2.setPlannedDate(LocalDate.now().plusMonths(1));
        payments.add(plan2);

        return payments;
    }

    /**
     * 根据解析的计划列表生成阶段列表
     * @param planList
     * @return
     */
    public List<ProjectPhase> generatePhases(List<ProjectPlan> planList) {
        // 1. 按任务包分组
        Map<String, List<ProjectPlan>> plansByPackage = planList.stream()
                .collect(Collectors.groupingBy(ProjectPlan::getTaskPackage));

        List<ProjectPhase> phaseList = new ArrayList<>();

        // 2. 遍历每个任务包分组
        for (Map.Entry<String, List<ProjectPlan>> entry : plansByPackage.entrySet()) {
            String taskPackage = entry.getKey();
            List<ProjectPlan> plansInPackage = entry.getValue();

            // 3. 获取第一个项目的项目ID（所有任务属于同一项目）
            Long projectId = plansInPackage.get(0).getProjectId();

            // 4. 获取时间范围：最早开始时间和最晚结束时间
            LocalDate startDate = plansInPackage.stream()
                    .map(ProjectPlan::getStartDate)
                    .min(LocalDate::compareTo)
                    .orElse(null);

            LocalDate endDate = plansInPackage.stream()
                    .map(ProjectPlan::getEndDate)
                    .max(LocalDate::compareTo)
                    .orElse(null);

            // 5. 合并责任人（去重）
            String responsiblePerson = plansInPackage.stream()
                    .map(ProjectPlan::getResponsiblePerson)
                    .distinct()
                    .collect(Collectors.joining(", "));

            // 6. 合并成果（去重并过滤空值）
            String deliverable = plansInPackage.stream()
                    .map(ProjectPlan::getDeliverable)
                    .filter(d -> d != null && !d.isBlank())
                    .distinct()
                    .collect(Collectors.joining(", "));

            // 7. 合并成果类型（去重并过滤空值）
            String deliverableType = plansInPackage.stream()
                    .map(ProjectPlan::getDeliverableType)
                    .filter(dt -> dt != null && !dt.isBlank())
                    .distinct()
                    .collect(Collectors.joining(", "));

            // 8. 创建阶段对象
            ProjectPhase phase = new ProjectPhase();
            phase.setProjectId(projectId);
            phase.setPhaseName(taskPackage);
            phase.setStartDate(startDate);
            phase.setEndDate(endDate);
            phase.setResponsiblePerson(responsiblePerson);
            phase.setDeliverable(deliverable);
            phase.setDeliverableType(deliverableType);
            phase.setPhaseStatus("未开始"); // 默认状态

            phaseList.add(phase);
        }

        return phaseList;
    }


    /**
     * 根据项目计划生成项目计划和项目阶段，插入相应的数据表中
     * @param projectDocumentReq
     * @param projectId
     */
    private void initPlanAndPhaseByDocument(ProjectDocumentReq projectDocumentReq, Long projectId, List<String> responsiblePersons) {
        // 根据文件路径获取计划书，计划书是excel格式，需要解析计划书，获取任务列表
        String filePath = projectDocumentReq.getFilePath();
        List<ProjectPlan> planList = ExcelUtil.parseProjectPlan(filePath, projectId, responsiblePersons);
        if (!planList.isEmpty()) {
            projectPlanService.saveBatch(planList);

            // 根据计划列表planList生成阶段列表
            List<ProjectPhase> phaseList = generatePhases(planList);
            projectPhaseService.saveBatch(phaseList);
        }
    }
}
