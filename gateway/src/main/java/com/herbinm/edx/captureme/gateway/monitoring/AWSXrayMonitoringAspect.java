package com.herbinm.edx.captureme.gateway.monitoring;

import com.amazonaws.xray.AWSXRay;
import com.amazonaws.xray.entities.Subsegment;
import com.amazonaws.xray.spring.aop.AbstractXRayInterceptor;
import com.amazonaws.xray.spring.aop.XRayInterceptorUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.util.Map;

@Aspect
@Component
public class AWSXrayMonitoringAspect extends AbstractXRayInterceptor {
    @Override
    protected Map<String, Map<String, Object>> generateMetadata(ProceedingJoinPoint proceedingJoinPoint, Subsegment subsegment) {
        return super.generateMetadata(proceedingJoinPoint, subsegment);
    }

    @Override
    @Pointcut("@within(com.amazonaws.xray.spring.aop.XRayEnabled)")
    public void xrayEnabledClasses() {
    }

    @Override
    protected Object processXRayTrace(ProceedingJoinPoint pjp) throws Throwable {
        try {
            String className = pjp.getSignature().getDeclaringTypeName();
            String methodName = pjp.getSignature().getName();
            Subsegment subsegment = AWSXRay.beginSubsegment(className + "." + methodName);
            subsegment.setMetadata(generateMetadata(pjp, subsegment));
            return XRayInterceptorUtils.conditionalProceed(pjp);
        } catch (Exception e) {
            AWSXRay.getCurrentSegment().addException(e);
            throw e;
        } finally {
            AWSXRay.endSubsegment();
        }
    }
}
