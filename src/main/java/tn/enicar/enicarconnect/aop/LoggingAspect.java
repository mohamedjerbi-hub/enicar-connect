package tn.enicar.enicarconnect.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

/**
 * Journalisation transversale (Spring AOP) pour toutes les méthodes des contrôleurs REST.
 */
@Slf4j
@Aspect
@Component
public class LoggingAspect {

    @Around("within(@org.springframework.web.bind.annotation.RestController *)")
    public Object logAroundRestController(ProceedingJoinPoint joinPoint) throws Throwable {
        long startNanos = System.nanoTime();
        String signature = joinPoint.getSignature().toShortString();
        try {
            Object result = joinPoint.proceed();
            long elapsedMs = (System.nanoTime() - startNanos) / 1_000_000;
            log.info("REST {} terminé en {} ms", signature, elapsedMs);
            return result;
        } catch (Throwable ex) {
            long elapsedMs = (System.nanoTime() - startNanos) / 1_000_000;
            log.error("REST {} après {} ms — erreur: {}", signature, elapsedMs, ex.getMessage(), ex);
            throw ex;
        }
    }
}
