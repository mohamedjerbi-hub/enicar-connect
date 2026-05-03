package tn.enicar.enicarconnect.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class LoggingAspect {

    // Pointcut covering controllers and services, avoiding filters to prevent CGLIB proxy issues
    @Pointcut("within(@org.springframework.web.bind.annotation.RestController *) " +
            "|| within(@org.springframework.stereotype.Service *)")
    public void applicationPackagePointcut() {}

    @Around("applicationPackagePointcut()")
    public Object logAround(ProceedingJoinPoint pjp) throws Throwable {
        String signature = pjp.getSignature().toShortString();
        Object[] args = pjp.getArgs();
        try {
            if (log.isDebugEnabled()) {
                log.debug("Enter: {} args={}", signature, args);
            }
            long start = System.currentTimeMillis();
            Object result = pjp.proceed();
            long elapsed = System.currentTimeMillis() - start;
            if (log.isDebugEnabled()) {
                String resultSummary = result != null ? result.getClass().getSimpleName() : "void";
                log.debug("Exit: {} resultType={} time={}ms", signature, resultSummary, elapsed);
            }
            return result;
        } catch (Throwable ex) {
            log.error("Exception in {}: {}", signature, ex.getMessage(), ex);
            throw ex;
        }
    }
}