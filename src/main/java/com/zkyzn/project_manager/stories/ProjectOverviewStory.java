package com.zkyzn.project_manager.stories;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.zkyzn.project_manager.enums.PhaseStatusEnum;
import com.zkyzn.project_manager.enums.PaymentStatusEnum;
import com.zkyzn.project_manager.enums.ProjectStatusEnum;
import com.zkyzn.project_manager.enums.TaskStatusEnum;
import com.zkyzn.project_manager.models.ContractNode;
import com.zkyzn.project_manager.models.PaymentNode;
import com.zkyzn.project_manager.models.ProjectInfo;
import com.zkyzn.project_manager.models.ProjectPlan;
import com.zkyzn.project_manager.services.ContractNodeService;
import com.zkyzn.project_manager.services.PaymentNodeService;
import com.zkyzn.project_manager.services.ProjectInfoService;
import com.zkyzn.project_manager.services.ProjectPlanService;
import com.zkyzn.project_manager.so.project.overview.*;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;

/**
 * @author: Mr-ti
 * Date: 2025/6/24 19:36
 */
@Service
public class ProjectOverviewStory {

    @Resource
    private ProjectInfoService projectInfoService;

    @Resource
    private PaymentNodeService paymentNodeService;

    @Resource
    private ContractNodeService contractNodeService;

    @Resource
    private ProjectPlanService projectPlanService;

    /**
     * 获取项目总览统计信息
     */
    public ProjectOverviewResponse getProjectOverviewStats() {
        // 获取所有合同节点和付款节点
        List<ContractNode> allContractNodes = contractNodeService.list();
        List<PaymentNode> allPaymentNodes = paymentNodeService.list();

        ProjectOverviewResponse response = new ProjectOverviewResponse();

        response.setPaymentProgress(calculatePaymentProgress(allContractNodes, allPaymentNodes));
        response.setOverdueProjects(calculateOverdueProjects());
        response.setActiveProjects(calculateActiveProjects());
        response.setMonthlyProgress(calculateMonthlyProgress());
        response.setPersonnelData(calculatePersonnelData());

        return response;
    }

    /**
     * 计算进款进度
     */
    private PaymentProgress calculatePaymentProgress(
            List<ContractNode> allContractNodes,
            List<PaymentNode> allPaymentNodes) {

        LocalDate today = LocalDate.now();

        // 计算当前进度
        BigDecimal currentRate = calculateDailyPaymentProgress(
                today, allContractNodes, allPaymentNodes
        );

        // 计算最近10天历史数据
        List<BigDecimal> last10Days = new ArrayList<>();
        for (int i = 9; i >= 0; i--) {
            LocalDate date = today.minusDays(i);
            BigDecimal dailyRate = calculateDailyPaymentProgress(
                    date, allContractNodes, allPaymentNodes
            );
            last10Days.add(dailyRate);
        }

        // 计算与前一天的变化值
        BigDecimal yesterdayRate = calculateDailyPaymentProgress(
                today.minusDays(1), allContractNodes, allPaymentNodes
        );
        BigDecimal dailyChange = currentRate.subtract(yesterdayRate);

        // 构建结果
        PaymentProgress progress = new PaymentProgress();
        progress.setCurrentRate(currentRate);
        progress.setDailyChange(dailyChange);
        progress.setLast10Days(last10Days);

        return progress;
    }

    /**
     * 计算每日进款进度
     */
    public BigDecimal calculateDailyPaymentProgress(LocalDate date,
                                                    List<ContractNode> contractNodes,
                                                    List<PaymentNode> paymentNodes) {
        // 1. 分组合同节点和付款节点
        Map<String, ContractNode> contractNodeMap = new HashMap<>();
        Map<String, PaymentNode> paymentNodeMap = new HashMap<>();

        // 按节点名称分组合同节点
        for (ContractNode node : contractNodes) {
            contractNodeMap.put(node.getPaymentNodeName(), node);
        }

        // 按节点名称分组付款节点
        for (PaymentNode node : paymentNodes) {
            paymentNodeMap.put(node.getPaymentNodeName(), node);
        }

        // 2. 统计进行中节点
        int totalInProgress = 0;
        int completedInProgress = 0;

        // 遍历所有合同节点（计划节点）
        for (ContractNode contractNode : contractNodes) {
            // 只考虑计划日期在指定日期之前的节点
            if (contractNode.getPlannedPaymentDate().isAfter(date)) {
                continue;
            }

            PaymentNode paymentNode = paymentNodeMap.get(contractNode.getPaymentNodeName());
            String paymentStatus = paymentNode != null ?
                    paymentNode.getPaymentStatus() : PaymentStatusEnum.PENDING.name();

            // 判断是否进行中
            if (isInProgress(paymentStatus)) {
                totalInProgress++;

                // 判断是否已完成
                if (PaymentStatusEnum.COMPLETED.name().equals(paymentStatus)) {
                    completedInProgress++;
                }
            }
        }

        // 3. 计算进度
        if (totalInProgress == 0) {
            return BigDecimal.ZERO;
        }

        return BigDecimal.valueOf(completedInProgress)
                .divide(BigDecimal.valueOf(totalInProgress), 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100));
    }

    /**
     * 判断付款状态是否属于进行中
     */
    private boolean isInProgress(String paymentStatus) {
        return PaymentStatusEnum.PROCESSING.name().equals(paymentStatus) ||
                PaymentStatusEnum.WARNING.name().equals(paymentStatus) ||
                PaymentStatusEnum.COMPLETED.name().equals(paymentStatus);
    }

    /**
     * 计算超期项目
     */
    private OverdueProjects calculateOverdueProjects() {
        // 获取所有超期项目
        List<ProjectInfo> overdueProjects = projectInfoService.findByStatus(ProjectStatusEnum.OVERDUE.name());
        long count = overdueProjects.size();

        // 计算最近10天历史数据
        List<Long> last10Days = new ArrayList<>();
        LocalDate today = LocalDate.now();
        for (int i = 9; i >= 0; i--) {
            LocalDate date = today.minusDays(i);
            long dailyCount = projectInfoService.countOverdueProjectsByDate(date);
            last10Days.add(dailyCount);
        }

        // 计算与前一天的变化百分比
        long yesterdayCount = projectInfoService.countOverdueProjectsByDate(today.minusDays(1));
        BigDecimal dailyChangePercentage = calculatePercentageChange(count, yesterdayCount);

        // 构建结果
        OverdueProjects result = new OverdueProjects();
        result.setCount(count);
        result.setDailyChangePercentage(dailyChangePercentage);
        result.setLast10Days(last10Days);

        return result;
    }

    /**
     * 计算正在执行项目
     */
    private ActiveProjects calculateActiveProjects() {
        // 获取所有执行中项目
        List<ProjectInfo> activeProjects = projectInfoService.findByStatus(PhaseStatusEnum.IN_PROGRESS.name());
        long count = activeProjects.size();

        // 计算最近10天历史数据
        List<Long> last10Days = new ArrayList<>();
        LocalDate today = LocalDate.now();
        for (int i = 9; i >= 0; i--) {
            LocalDate date = today.minusDays(i);
            long dailyCount = projectInfoService.countActiveProjectsByDate(date);
            last10Days.add(dailyCount);
        }

        // 计算与前一天的变化百分比
        long yesterdayCount = projectInfoService.countActiveProjectsByDate(today.minusDays(1));
        BigDecimal dailyChangePercentage = calculatePercentageChange(count, yesterdayCount);

        // 构建结果
        ActiveProjects result = new ActiveProjects();
        result.setCount(count);
        result.setDailyChangePercentage(dailyChangePercentage);
        result.setLast10Days(last10Days);

        return result;
    }

    /**
     * 计算本月项目进度
     */
    private MonthlyProgress calculateMonthlyProgress() {
        YearMonth currentMonth = YearMonth.now();
        LocalDate firstDayOfMonth = currentMonth.atDay(1);
        LocalDate lastDayOfMonth = currentMonth.atEndOfMonth();

        // 计算本月任务总数
        long totalTasks = projectPlanService.countByDateRange(null, firstDayOfMonth, lastDayOfMonth);

        // 计算本月已完成任务
        long completedTasks = projectPlanService.countCompletedByDateRange(
                null, firstDayOfMonth, lastDayOfMonth
        );

        // 计算当前进度
        BigDecimal currentRate = totalTasks > 0 ?
                BigDecimal.valueOf(completedTasks)
                        .divide(BigDecimal.valueOf(totalTasks), 4, RoundingMode.HALF_UP)
                        .multiply(BigDecimal.valueOf(100)) :
                BigDecimal.ZERO;

        // 计算最近10天历史数据
        List<BigDecimal> last10Days = new ArrayList<>();
        LocalDate today = LocalDate.now();
        for (int i = 9; i >= 0; i--) {
            LocalDate date = today.minusDays(i);
            BigDecimal dailyRate = calculateDailyMonthlyProgress(date);
            last10Days.add(dailyRate);
        }

        // 计算与前一天的变化值
        BigDecimal yesterdayRate = calculateDailyMonthlyProgress(today.minusDays(1));
        BigDecimal dailyChange = currentRate.subtract(yesterdayRate);

        // 构建结果
        MonthlyProgress progress = new MonthlyProgress();
        progress.setCurrentRate(currentRate);
        progress.setDailyChange(dailyChange);
        progress.setLast10Days(last10Days);

        return progress;
    }

    private BigDecimal calculateDailyMonthlyProgress(LocalDate date) {
        YearMonth month = YearMonth.from(date);
        LocalDate firstDay = month.atDay(1);
        LocalDate lastDay = month.atEndOfMonth();

        long totalTasks = projectPlanService.countByDateRange(null, firstDay, lastDay);
        long completedTasks = projectPlanService.countCompletedByDateRange(null, firstDay, lastDay);

        return totalTasks > 0 ?
                BigDecimal.valueOf(completedTasks)
                        .divide(BigDecimal.valueOf(totalTasks), 4, RoundingMode.HALF_UP)
                        .multiply(BigDecimal.valueOf(100)) :
                BigDecimal.ZERO;
    }

    /**
     * 计算人员项目数据
     */
    private PersonnelData calculatePersonnelData() {
        // 获取所有执行中项目的任务责任人
        Set<String> responsiblePersons = projectPlanService.findActiveResponsiblePersons();
        int count = responsiblePersons.size();

        // 计算最近10天历史数据
        List<Integer> last10Days = new ArrayList<>();
        LocalDate today = LocalDate.now();
        for (int i = 9; i >= 0; i--) {
            LocalDate date = today.minusDays(i);
            int dailyCount = projectPlanService.countActiveResponsiblePersonsByDate(date);
            last10Days.add(dailyCount);
        }

        // 计算与前一天的变化百分比
        int yesterdayCount = projectPlanService.countActiveResponsiblePersonsByDate(today.minusDays(1));
        BigDecimal dailyChangePercentage = calculatePercentageChange(count, yesterdayCount);

        // 构建结果
        PersonnelData result = new PersonnelData();
        result.setCount(count);
        result.setDailyChangePercentage(dailyChangePercentage);
        result.setLast10Days(last10Days);

        return result;
    }

    /**
     * 计算变化百分比
     */
    private BigDecimal calculatePercentageChange(long current, long previous) {
        if (previous == 0) {
            return current > 0 ? BigDecimal.valueOf(100) : BigDecimal.ZERO;
        }
        return BigDecimal.valueOf(current - previous)
                .divide(BigDecimal.valueOf(previous), 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100));
    }

    /**
     * 获取所有科室当月项目进度
     */
    public List<DepartmentMonthlyProgress> getMonthlyDepartmentProgress() {
        // 1. 获取当前月份范围
        YearMonth currentMonth = YearMonth.now();
        LocalDate firstDayOfMonth = currentMonth.atDay(1);
        LocalDate lastDayOfMonth = currentMonth.atEndOfMonth();

        // 2. 查询当月所有任务
        QueryWrapper<ProjectPlan> wrapper = new QueryWrapper<>();
        wrapper.select("department",
                        "COUNT(*) AS total_tasks",
                        "SUM(CASE WHEN task_status = '" + TaskStatusEnum.COMPLETED.name() + "' THEN 1 ELSE 0 END) AS completed_tasks")
                .between("end_date", firstDayOfMonth, lastDayOfMonth)
                .groupBy("department");

        List<Map<String, Object>> results = projectPlanService.listMaps(wrapper);

        // 3. 处理结果 - 修复类型转换问题
        List<DepartmentMonthlyProgress> progressList = new ArrayList<>();
        for (Map<String, Object> result : results) {
            DepartmentMonthlyProgress progress = new DepartmentMonthlyProgress();
            progress.setDepartment((String) result.get("department"));

            // 使用 Number 类型安全转换
            Number totalTasksNum = (Number) result.get("total_tasks");
            Number completedTasksNum = (Number) result.get("completed_tasks");

            int totalTasks = totalTasksNum != null ? totalTasksNum.intValue() : 0;
            int completedTasks = completedTasksNum != null ? completedTasksNum.intValue() : 0;

            progress.setTotalTasks(totalTasks);
            progress.setCompletedTasks(completedTasks);

            // 计算完成率
            if (totalTasks > 0) {
                BigDecimal completionRate = BigDecimal.valueOf(completedTasks)
                        .divide(BigDecimal.valueOf(totalTasks), 4, RoundingMode.HALF_UP)
                        .multiply(BigDecimal.valueOf(100));
                progress.setCompletionRate(completionRate);
            } else {
                progress.setCompletionRate(BigDecimal.ZERO);
            }

            progressList.add(progress);
        }

        return progressList;
    }
}
