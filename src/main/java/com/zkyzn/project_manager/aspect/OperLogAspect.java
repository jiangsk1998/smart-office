package com.zkyzn.project_manager.aspect;

import com.zkyzn.project_manager.annotation.OperLog;
import com.zkyzn.project_manager.models.ProjectInfo;
import com.zkyzn.project_manager.models.ProjectPhase;
import com.zkyzn.project_manager.models.ProjectPlan;
import com.zkyzn.project_manager.services.OperationLogService;
import com.zkyzn.project_manager.services.ProjectInfoService;
import com.zkyzn.project_manager.services.ProjectPhaseService;
import com.zkyzn.project_manager.services.ProjectPlanService;
import jakarta.annotation.Resource;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

@Aspect
@Component
public class OperLogAspect {

    @Resource
    private OperationLogService operationLogService;
    @Resource
    private ProjectPhaseService projectPhaseService;
    @Resource
    private ProjectPlanService projectPlanService;
    @Resource
    private ProjectInfoService projectInfoService;

    private final ThreadLocal<Map<String, Object>> threadLocal = new ThreadLocal<>();

    @Pointcut("@annotation(com.zkyzn.project_manager.annotation.OperLog)")
    public void operLogPointcut() {
    }

    @Before("operLogPointcut()")
    public void doBefore(JoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        OperLog operLog = method.getAnnotation(OperLog.class);

        if (operLog.recordOriginal()) {
            Object[] args = joinPoint.getArgs();
            Long targetId = getArgument(args, operLog.idPosition(), Long.class);
            if (targetId != null) {
                Object originalData = getOriginalData(operLog.targetType(), targetId);
                Map<String, Object> context = new HashMap<>();
                context.put("originalData", originalData);
                threadLocal.set(context);
            }
        }
    }

    @AfterReturning(pointcut = "operLogPointcut()", returning = "result")
    public void doAfterReturning(JoinPoint joinPoint, Object result) {
        handleLog(joinPoint);
    }

    protected void handleLog(final JoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        OperLog operLog = method.getAnnotation(OperLog.class);
        Object[] args = joinPoint.getArgs();

        Long targetId = getArgument(args, operLog.idPosition(), Long.class);

        // 从 @Before 阶段存储的数据中获取原始对象
        Object originalData = null;
        Map<String, Object> context = threadLocal.get();
        if (context != null) {
            originalData = context.get("originalData");
            threadLocal.remove();
        }

        Long projectId = getProjectIdFromObject(originalData);

        Object updatedData = null;
        Object entity = (args.length > 0) ? args[0] : null;

        // 根据操作类型，处理 targetId 和 updatedData
        if ("CREATE".equals(operLog.type())) {
            updatedData = entity;
            if (entity instanceof ProjectPhase) {
                targetId = ((ProjectPhase) entity).getPhaseId();
                // 对于创建操作，projectId 从新实体中获取
                projectId = ((ProjectPhase) entity).getProjectId();
            } else if (entity instanceof ProjectPlan) {
                targetId = ((ProjectPlan) entity).getProjectPlanId();
                projectId = ((ProjectPlan) entity).getProjectId();
            }
        } else if (targetId != null && !"DELETE".equals(operLog.type())) {
            // 对于更新和删除，获取操作后的数据用于记录
            updatedData = getOriginalData(operLog.targetType(), targetId);
        }
        
        // 如果 projectId 仍然为空 (例如在创建时实体没有 projectId)，尝试从更新后的数据中再次获取
        if (projectId == null) {
            projectId = getProjectIdFromObject(updatedData);
        }

        operationLogService.logOperation(
                operLog.type(),
                operLog.targetType().getSimpleName(),
                targetId,
                operLog.desc(),
                projectId,
                originalData,
                updatedData
        );
    }

    private <T> T getArgument(Object[] args, int index, Class<T> type) {
        if (index >= 0 && index < args.length && args[index] != null && type.isAssignableFrom(args[index].getClass())) {
            return type.cast(args[index]);
        }
        return null;
    }

    private Long getProjectIdFromObject(Object dataObject) {
        if (dataObject instanceof ProjectPhase) {
            return ((ProjectPhase) dataObject).getProjectId();
        }
        if (dataObject instanceof ProjectPlan) {
            return ((ProjectPlan) dataObject).getProjectId();
        }
        if (dataObject instanceof ProjectInfo) {
            return ((ProjectInfo) dataObject).getProjectId();
        }
        return null;
    }

    private Object getOriginalData(Class<?> targetType, Long id) {
        if (targetType.equals(ProjectPhase.class)) {
            return projectPhaseService.getById(id);
        } else if (targetType.equals(ProjectPlan.class)) {
            return projectPlanService.getById(id);
        } else if (targetType.equals(ProjectInfo.class)) {
            return projectInfoService.getById(id);
        }
        return null;
    }
}