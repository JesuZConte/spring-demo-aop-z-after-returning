package com.luv2code.aopdemo.aspect;

import com.luv2code.aopdemo.Account;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.logging.Logger;

@Aspect
@Component
@Order(2)
public class MyDemoLoggingAspect {

    private Logger myLogger = Logger.getLogger(getClass().getName());

    @Around("execution(* com.luv2code.aopdemo.service.TrafficFortuneService.getFortune(..))")
    public Object aroundGetFortune(ProceedingJoinPoint theProceedingJoinPoint) throws Throwable {

        // print out method we are advising on
        String method = theProceedingJoinPoint.getSignature().toShortString();
        myLogger.info("\n=====> Executing @Around on method: " + method);

        // get begin timestamp
        long begin = System.currentTimeMillis();

        // now, let's execute the method
        Object result = null;
        try {
            result = theProceedingJoinPoint.proceed();
        } catch (Exception e) {
            // log the exception (best practice)
            myLogger.warning(e.getMessage());

            // give user a custom message
            result =  "Major accident! But no worries, your private AOP helicopter is on the way!";
        }

        // get end timestamp
        long end = System.currentTimeMillis();

        // compute duration and display it
        long duration = end - begin;
        myLogger.info("\n====> Duration: " + duration / 1000.0 + " seconds");

        return result;
    }

    @After("execution(* com.luv2code.aopdemo.dao.AccountDAO.findAccounts(..))")
    public void afterFinallyFindAccountsAdvice(JoinPoint theJoinPoint) {
        // print out which method we are advising on
        String method = theJoinPoint.getSignature().toShortString();
        myLogger.info("\n=====> Executing @After (finally) on method: " + method);
    }

    @AfterThrowing(pointcut="execution(* com.luv2code.aopdemo.dao.AccountDAO.findAccounts(..))",
    throwing="theExc")
    public void afterThrowingFindAccountsAdvice(JoinPoint theJoinPoint, Throwable theExc) {

        // print out which method we are advising on
        String method = theJoinPoint.getSignature().toShortString();
        myLogger.info("\n=====> Executing @AfterThrowing on method: " + method);

        // log the exception
        myLogger.info("\n=====> The exception is: " + theExc);
    }

    // add a new advice for @AfterReturning on the findAccounts method

    @AfterReturning(pointcut="execution(* com.luv2code.aopdemo.dao.AccountDAO.findAccounts(..))",
    returning="result")
    public void afterReturningFindAccountAdvice(JoinPoint theJoinPoint, List<Account> result) {

        // print out which method we are advising on
        String method = theJoinPoint.getSignature().toShortString();
        myLogger.info("\n=====> Executing @AfterReturning on method: " + method);

        // print out the results of the method call
        myLogger.info("\n=====> Result is: " + result);

        // let's post-process the data... let's modify it :-)

        // convert the account names to uppercase
        convertAccountNamesToUpperCase(result);

        // print out the results of the method call
        myLogger.info("\n=====> Result is: " + result);
    }

    private void convertAccountNamesToUpperCase(List<Account> result) {
        // loop through accounts

        for (Account tempAccount : result) {
            // get uppercase version of name
            String theUpperName = tempAccount.getName().toUpperCase();

            // update the name on the account
            tempAccount.setName(theUpperName);
        }



    }

    @Before("com.luv2code.aopdemo.aspect.LuvAopExpressions.forDaoPackageNoGetterSetter()")
    public void beforeAddAccountAdvice(JoinPoint theJoinPoint) {
        myLogger.info("\n=========>>> Executing @Before advice");

        // display the method signature
        MethodSignature methodSig = (MethodSignature) theJoinPoint.getSignature();
        myLogger.info("Method: " + methodSig);

        // display the method arguments

        // get arguments
        Object[] args = theJoinPoint.getArgs();

        // loop through arguments
        for (Object tempArgs : args) {
            myLogger.info("Argument: " + tempArgs);

            if (tempArgs instanceof Account) {
                // downcast and print Account specific stuff
                Account theAccount = (Account) tempArgs;
                myLogger.info("Account name: " + theAccount.getName());
                myLogger.info("Account level: " + theAccount.getLevel());
            }
        }

    }

}
