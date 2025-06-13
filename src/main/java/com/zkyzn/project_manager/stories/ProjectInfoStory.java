package com.zkyzn.project_manager.stories;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zkyzn.project_manager.enums.DocumentTypeEnum;
import com.zkyzn.project_manager.enums.Operator;
import com.zkyzn.project_manager.enums.PhaseStatusEnum;
import com.zkyzn.project_manager.models.ProjectInfo;
import com.zkyzn.project_manager.models.ProjectPhase;
import com.zkyzn.project_manager.models.ProjectPlan;
import com.zkyzn.project_manager.services.*;
import com.zkyzn.project_manager.so.project.dashboard.PhaseDetail;
import com.zkyzn.project_manager.so.project.info.*;
import com.zkyzn.project_manager.utils.ExcelUtil;
import com.zkyzn.project_manager.utils.FileUtil;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
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

    @Value("/opt/software/file")
    private String fileRootPath;

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
        String filePath = FileUtil.getAbsolutePathByUrlAndRootPath(req.getImportExcelFilePath(), fileRootPath);
        List<ProjectInfo> projectInfoList = ExcelUtil.parseProjectInfoSheet(filePath, "");

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

        // 项目的阶段信息列表
        List<ProjectPhase> list = projectPhaseService.getPhasesByProjectId(projectId);
        List<PhaseDetail> phaseDetails = new ArrayList<>();

        // 根据项目阶段生成项目阶段详情
        for (ProjectPhase phase : list) {
            PhaseDetail phaseDetail = new PhaseDetail();
            List<ProjectPlan> plans = projectPlanService.getPlansByPhase(projectId, phase.getPhaseName());
            // 复制阶段基本属性
            BeanUtils.copyProperties(phase, phaseDetail);
            phaseDetail.setProjectPlanList(plans);
            phaseDetails.add(phaseDetail);
        }
        resp.setPhaseDetails(phaseDetails);

        resp.setPhaseProgressList(projectPhaseService.getPhaseProgressDetails(projectId));

        return resp;
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
            // 默认状态
            phase.setPhaseStatus(PhaseStatusEnum.NOT_STARTED.getDisplayName());

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
        filePath = FileUtil.getAbsolutePathByUrlAndRootPath(filePath, fileRootPath);
        List<ProjectPlan> planList = ExcelUtil.parseProjectPlan(filePath, projectId, responsiblePersons);
        if (!planList.isEmpty()) {
            projectPlanService.saveBatch(planList);

            // 根据计划列表planList生成阶段列表
            List<ProjectPhase> phaseList = generatePhases(planList);
            projectPhaseService.saveBatch(phaseList);
        }
    }
}
